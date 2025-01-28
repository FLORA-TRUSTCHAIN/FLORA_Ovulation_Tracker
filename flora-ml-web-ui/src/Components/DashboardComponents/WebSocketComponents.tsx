import React, { useEffect, useState } from 'react';
import { w3cwebsocket as W3CWebSocket } from 'websocket';
import axios from 'axios';
import { jwtDecode }  from "jwt-decode";
//import FLComponent from './FLComponents/FLComponent';
import { MyData } from './FLComponents/data';
import {
    showStatusMessage,
    showErrorMessage,
    clearOutputs,
} from './FLComponents/logging';
//import { getFeatureMatrix } from './FLComponents/helpers';
import { getTrainingSession } from './FLComponents/onnx-helpers';
import { trainOneEpoch, evaluateModel } from './FLComponents/train-helpers';
import * as ort from 'onnxruntime-web/training';
import { useNavigate } from 'react-router-dom';
import sendFloats from './FLComponents/FinalStep';




const API_BASE_URL = process.env.REACT_APP_API_BASE_URL as string;
const WS_BASE_URL = process.env.REACT_APP_WS_BASE_URL as string;
const OBJECT_STORE_NAME_LOCAL = 'local_onnx_files';
const OBJECT_STORE_NAME_CHECKPOINT = 'downloaded_checkpoint';


type EvaluationResult = {
    mse: number;
    mae: number;
  };
  
  type EpochResults = {
    train: EvaluationResult;
    test: EvaluationResult;
  };




const WebSocketComponent: React.FC = () => {
    const navigate = useNavigate();
    const [isPageLoaded, setPageLoaded] = useState<boolean>(false);
    const [isMounted, setIsMounted] = useState<boolean>(false);
    const [access_token_username, set_access_token_username] = useState('');
    const [current_fl_round, set_current_fl_round]= React.useState<String>("");
    const resultsDictionary: { [key: string]: EpochResults } = {};

    /*for fl*/
    const lossNodeName = "onnx::reducemean_output::5";

    const [numEpochs, setNumEpochs] = React.useState<number>(5);
    const [statusMessage, setStatusMessage] = React.useState("")
    const [errorMessage, setErrorMessage] = React.useState("")
    


    const initDB = (storeName: string): Promise<IDBDatabase> => {
        return new Promise((resolve, reject) => {
            const token = sessionStorage.getItem('jwtToken');
            
            if (!token) return reject('No JWT token found');

            if (token) {
                
                const decodedToken = jwtDecode(token);
                //console.log("decodedToken", decodedToken);

                const username = decodedToken.sub;
                set_access_token_username(username || '');
                const request = indexedDB.open(`IndexedDB_${username}`, 1);

                request.onupgradeneeded = (event) => {
                    const db = (event.target as IDBOpenDBRequest).result;
                    if (!db.objectStoreNames.contains(storeName)) {
                        db.createObjectStore(storeName, {keyPath: 'filename'});
                    }
                };

                request.onsuccess = (event) => resolve((event.target as IDBOpenDBRequest).result);
                request.onerror = (event) => reject((event.target as IDBOpenDBRequest).error);
            }
        });
    };

    const fileExistsInIndexedDB = async (filename: string, storeName: string): Promise<boolean> => {
        try {
            const db = await initDB(storeName);
            const transaction = db.transaction([storeName], 'readonly');
            const store = transaction.objectStore(storeName);

            return new Promise((resolve, reject) => {
                const request = store.get(filename);
                request.onsuccess = (event) => {
                    resolve(!!(event.target as IDBRequest).result);
                };
                request.onerror = (event) => reject((event.target as IDBRequest).error);
            });
        } catch (error) {
            console.error('Error checking IndexedDB for file existence:', error);
            return false;
        }
    };

    const saveToIndexedDB = (blob: Blob, storeName: string, filename: string): Promise<void> => {
        return new Promise(async (resolve, reject) => {
            try {
                const db = await initDB(storeName);
                const transaction = db.transaction([storeName], 'readwrite');
                const store = transaction.objectStore(storeName);
                const request = store.put({ filename, blob });

                request.onsuccess = () => resolve();
                request.onerror = (event) => reject((event.target as IDBRequest).error);
            } catch (error) {
                reject(error);
            }
        });
    };

    const downloadAndSaveFiles = async () => {
        const token = sessionStorage.getItem('jwtToken');

        try {
            const filesToDownload = [
                { url: `${API_BASE_URL}/download-onnx-optimizer`, filename: 'train_mlp_optimizer_model.onnx' },
                { url: `${API_BASE_URL}/download-onnx-training-model`, filename: 'train_mlp_training_model.onnx' },
                { url: `${API_BASE_URL}/download-onnx-training-eval-model`, filename: 'train_mlp_eval_model.onnx' }
            ];

            for (const file of filesToDownload) {
                const { url, filename } = file;

                const fileExists = await fileExistsInIndexedDB(filename, OBJECT_STORE_NAME_LOCAL);
                if (fileExists) {
                    console.log(`${filename} already exists in IndexedDB. Skipping download.`);
                    continue;
                }

                const response = await axios.post(url, {}, {
                    responseType: 'blob',
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                const blob = new Blob([response.data], { type: 'application/octet-stream' });
                await saveToIndexedDB(blob, OBJECT_STORE_NAME_LOCAL, filename);
                console.log(`${filename} saved to IndexedDB successfully`);
            }

        } catch (error) {
            console.error('Error downloading files:', error);
        }
    };

    const downloadCheckpoint = async () => {
        const token = sessionStorage.getItem('jwtToken');

        try {
            const response = await axios.post(`${API_BASE_URL}/download-FL-checkpoint`, {}, {
                responseType: 'blob',
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            const blob = new Blob([response.data], { type: 'application/octet-stream' });
            await saveToIndexedDB(blob, OBJECT_STORE_NAME_CHECKPOINT, 'train_mlp_checkpoint');
            console.log('train_mlp_checkpoint saved to IndexedDB successfully');
        } catch (error) {
            console.error('Error downloading checkpoint:', error);
        }
    };

        

    async function train() {
        let session = await getTrainingSession(setStatusMessage, setErrorMessage);
        console.log("Training session", session);

        console.log("Test Results:");
        console.log((await session.getContiguousParameters(true)).data);

        const dataset = new MyData();
        await dataset.initializeSamplesAndFeatures();

        const numTrain = dataset.getNumTrainSamples();
        const numTest = dataset.getNumTestSamples();

        if (numTrain < 1 || numTest < 1) {
            showErrorMessage(`Number of training samples (${numTrain}) or test samples (${numTest}) cannot be <=1.`, setErrorMessage);
            return;
        }
        showStatusMessage(`Number of training samples: ${numTrain} and test samples: ${numTest}`, setStatusMessage);

        const trainingEvaluationResult = await evaluateModel(session, dataset, lossNodeName, 'train');
        const testEvaluationResult = await evaluateModel(session, dataset, lossNodeName, 'eval');
        showStatusMessage(`[Epoch 0], Train MSE: ${trainingEvaluationResult.mse}, Train MAE: ${trainingEvaluationResult.mae} Test MSE: ${testEvaluationResult.mse}, Test MAE: ${testEvaluationResult.mae}`, setStatusMessage);
        resultsDictionary[`Epoch 0`] = {
            train: {
              mse: trainingEvaluationResult.mse,
              mae: trainingEvaluationResult.mae,
            },
            test: {
              mse: testEvaluationResult.mse,
              mae: testEvaluationResult.mae,
            },
          };
        const startTrainingTime = Date.now();
        showStatusMessage('Training starting...', setStatusMessage);

        let itersPerSecCumulative = 0;
        let testMSE = 0;
        let testMAE = 0;

        for (let epoch = 0; epoch < numEpochs; epoch++) {
            //showStatusMessage(`[TRAINING] Epoch: ${String(epoch + 1).padStart(2)}/${numEpochs}`, setStatusMessage);

            itersPerSecCumulative += await trainOneEpoch(session, dataset, epoch, lossNodeName);
            const trainingEvaluationResult = await evaluateModel(session, dataset, lossNodeName, 'train');
            const testEvaluationResult = await evaluateModel(session, dataset, lossNodeName, 'eval');

            showStatusMessage(`[Epoch ${epoch+1}], Train MSE: ${trainingEvaluationResult.mse}, Train MAE: ${trainingEvaluationResult.mae} Test MSE: ${testEvaluationResult.mse}, Test MAE: ${testEvaluationResult.mae}`, setStatusMessage);
            resultsDictionary[`Epoch ${epoch + 1}`] = {
                train: {
                  mse: trainingEvaluationResult.mse,
                  mae: trainingEvaluationResult.mae,
                },
                test: {
                  mse: testEvaluationResult.mse,
                  mae: testEvaluationResult.mae,
                },
              };
          
        }
        

        const trainingTimeMs = Date.now() - startTrainingTime;
        showStatusMessage(`Training completed. Took ${trainingTimeMs/ 1000} seconds. Average iterations per second: ${(itersPerSecCumulative / numEpochs).toFixed(2)}`, setStatusMessage);
             

            
            let buffer = await session.getContiguousParameters(true);
            // let params: any = await buffer.getData();
            // let uint8Array = new Uint8Array(params.buffer);
            // await session.loadParametersBuffer(uint8Array, true)

            console.log("Logs Dict:");
            console.log(resultsDictionary);
            console.log("Trying to save to indexeddb:");
            try {
                const db = await initDB('local_fl_rounds_logs');
        
                const transaction = db.transaction('local_fl_rounds_logs', 'readwrite');
                const objectStore = transaction.objectStore('local_fl_rounds_logs');
        
                const dataToStore = {
                  filename: `Round ${current_fl_round}`,
                  results: resultsDictionary,
                };
        
                const addRequest = objectStore.add(dataToStore);
        
                addRequest.onsuccess = () => {
                  console.log('Data successfully stored in IndexedDB');
                };
        
                addRequest.onerror = (event) => {
                  console.error('Error storing data in IndexedDB:', (event.target as IDBRequest).error);
                };
        
                transaction.oncomplete = () => {
                  console.log('Transaction completed successfully');
                };
        
                transaction.onerror = (event) => {
                  console.error('Transaction error:', (event.target as IDBTransaction).error);
                };
              } catch (error) {
                console.error('Error initializing IndexedDB:', error);
              }
        

            // console.log("Result params", uint8Array);
             await sendFloats((await buffer).data, current_fl_round);
            console.log("JSON Sent to server ok");
            //  console.log("end of training");
            // navigate(0) reloads the page, uncomment the line below to make multiple triggers
            // when the page refreshes, we get some errors in the backend because the ws connection doesn't close properly
            // navigate(0);
            
            

            
            
       

    }
    
      
      


    useEffect(() => {
        if (isPageLoaded) {
            const token = sessionStorage.getItem('jwtToken');
            if (!token) return;

            const socket = new W3CWebSocket(`ws://${WS_BASE_URL}/ws?token=${token}`);

            socket.onopen = () => {
                console.log('WebSocket Client Connected');
                downloadAndSaveFiles().then(() => console.log('Downloading 3 ONNX files and saved them locally'));
            };

            socket.onmessage = async (event) => {
                const message = event.data;
                if (String(message).startsWith("trigger_learning")) {
                    console.log("Triggered");
                    let result = String(message).replace("trigger_learning_round_", "");
                    set_current_fl_round(result);
                }
            };

            socket.onclose = () => {
                setIsMounted(false);
                console.log('WebSocket Client Disconnected');
                socket.close();
            };

            return () => {
                setIsMounted(false);
                socket.close();
            };
        } else {
            setPageLoaded(true);
        }
    }, [isPageLoaded]);


     
     useEffect(() => {
        if (current_fl_round !== '') {
            (async () => {
                await downloadCheckpoint();
                setIsMounted(true);
                console.log("setismounted true");
                await train();
                console.log("setismounted false");
                setIsMounted(false);
            })();
        }
    }, [current_fl_round]);



    return (
        <div style={{ padding: '20px' }}>
            {isMounted && <div>Training in progress...</div>}
            {!isMounted && <div>Training complete or not started.</div>}
        </div>
    );
};

export default WebSocketComponent;