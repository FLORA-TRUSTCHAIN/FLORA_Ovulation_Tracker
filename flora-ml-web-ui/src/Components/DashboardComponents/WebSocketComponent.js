import React, { useEffect, useState } from 'react';
import { w3cwebsocket as W3CWebSocket } from 'websocket';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import FLComponent from './FLComponents/FLComponent';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;
const WS_BASE_URL = process.env.REACT_APP_WS_BASE_URL;
const OBJECT_STORE_NAME_LOCAL = 'local_onnx_files';
const OBJECT_STORE_NAME_CHECKPOINT = 'downloaded_checkpoint';

const WebSocketComponent = () => {
  const [isPageLoaded, setPageLoaded] = useState(false);
  const [isMounted, setIsMounted] = useState(false);

  const initDB = (storeName) => {
    return new Promise((resolve, reject) => {
      const token = sessionStorage.getItem('jwtToken');
      const decodedToken = jwtDecode(token);
      const username = decodedToken.sub;
      const request = indexedDB.open(`IndexedDB_${username}`, 1);

      request.onupgradeneeded = (event) => {
        const db = event.target.result;
        if (!db.objectStoreNames.contains(storeName)) {
          db.createObjectStore(storeName, { keyPath: 'filename' });
        }
      };

      request.onsuccess = (event) => resolve(event.target.result);
      request.onerror = (event) => reject(event.target.error);
    });
  };

  const fileExistsInIndexedDB = async (filename, storeName) => {
    try {
      const db = await initDB(storeName);
      const transaction = db.transaction([storeName], 'readonly');
      const store = transaction.objectStore(storeName);

      return new Promise((resolve, reject) => {
        const request = store.get(filename);
        request.onsuccess = (event) => {
          resolve(!!event.target.result);
        };
        request.onerror = (event) => reject(event.target.error);
      });
    } catch (error) {
      console.error('Error checking IndexedDB for file existence:', error);
      return false;
    }
  };

  const saveToIndexedDB = (blob, storeName, filename) => {
    return new Promise(async (resolve, reject) => {
      try {
        const db = await initDB(storeName);
        const transaction = db.transaction([storeName], 'readwrite');
        const store = transaction.objectStore(storeName);
        const request = store.put({ filename, blob });

        request.onsuccess = () => resolve();
        request.onerror = (event) => reject(event.target.error);
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

    const response = await axios.post(`${API_BASE_URL}/download-FL-checkpoint`, {}, {
      responseType: 'blob',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const blob = new Blob([response.data], { type: 'application/octet-stream' });
    await saveToIndexedDB(blob, OBJECT_STORE_NAME_CHECKPOINT, 'train_mlp_checkpoint');
    console.log('train_mlp_checkpoint saved to IndexedDB successfully');

  };

  useEffect(() => {
    setPageLoaded(true);

    if (isPageLoaded) {
      const token = sessionStorage.getItem('jwtToken');
      const socket = new W3CWebSocket(`ws://${WS_BASE_URL}/ws?token=${token}`);

      socket.onopen = () => {
        console.log('WebSocket Client Connected');
        // Download and save the 3 .onnx files upon connection
        downloadAndSaveFiles();
        console.log('Downloading 3 ONNX files and saved them locally');
      };

      socket.onmessage = async (event) => {
        const message = event.data;
        if (message === "trigger_learning") {
          console.log("Triggered");
          await downloadCheckpoint();
          setIsMounted(true);
        }
      };

      socket.onclose = () => {
        setIsMounted(false);
        console.log('WebSocket Client Disconnected');
      };

      return () => {
        setIsMounted(false);
        socket.close();
      };
    }
  }, [isPageLoaded]);

  return (
    <div style={{ padding: '20px' }}>
      {isMounted && <FLComponent />}
    </div>
  );
};

export default WebSocketComponent;
