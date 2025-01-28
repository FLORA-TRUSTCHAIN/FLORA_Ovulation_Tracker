import * as ort from 'onnxruntime-web/training';
import { jwtDecode } from 'jwt-decode';

export class MyData {
    static readonly BATCH_SIZE = 4;
    static NUM_TRAIN_SAMPLES = 0;
    static NUM_TEST_SAMPLES = 0;
    static NUM_FEATURES = 3;
    static TOKEN_USERNAME = '';
    static trainData: ort.Tensor[] = [];
    static testData: ort.Tensor[] = [];
    static trainTarget: ort.Tensor[] = [];
    static testTarget: ort.Tensor[] = [];

    constructor(
        public batchSize = MyData.BATCH_SIZE,

    ) {
        if (batchSize <= 0) {
            throw new Error("batchSize must be > 0")
        }

        // Initialize the number of samples and features
        //this.initializeSamplesAndFeatures();
    }

    public getNumTrainSamples(): number {
        return MyData.NUM_TRAIN_SAMPLES;
    }

    public getNumTestSamples(): number {
        return MyData.NUM_TEST_SAMPLES;
    }

    public getNumBatches(totalSamples: number): number {
        return Math.ceil(totalSamples / this.batchSize);
    }



    private *batches(data: ort.Tensor[], labels: ort.Tensor[]) {
        for (let batchIndex = 0; batchIndex < data.length; ++batchIndex) {
            yield {
                data: data[batchIndex],
                target: labels[batchIndex],
            };
        }
    }


    public async *trainingBatches(shuffle = false) {
        const trainData = MyData.trainData;
        const trainLabels = MyData.trainTarget;

        /*if (shuffle) {
            this.shuffleArray(trainData, trainLabels);
        }*/
        console.log("trainData", trainData);
        yield* this.batches(trainData, trainLabels);
    }

    /*public shuffleArray(data: ort.Tensor[], target: ort.Tensor[]) {
       // TODO
    }*/

    public async *testBatches() {
        const testData = MyData.testData;
        const testLabels = MyData.testTarget;
        // console.log("testData", testData);
        yield* this.batches(testData, testLabels);
    }

    private async toTensor(data: Float32Array, target: Float32Array): Promise<{ trainData: ort.Tensor[], trainLabels: ort.Tensor[], testData: ort.Tensor[], testLabels: ort.Tensor[] }> {
        const numSamples = data.length / MyData.NUM_FEATURES;
        const trainSamples = MyData.NUM_TRAIN_SAMPLES;
        const testSamples = MyData.NUM_TEST_SAMPLES;
        const batchSize = this.batchSize;
        const numFeatures = MyData.NUM_FEATURES;

        let trainBatchData: number[] = [];
        let trainBatchLabels: number[] = [];
        let testBatchData: number[] = [];
        let testBatchLabels: number[] = [];

        const trainData: ort.Tensor[] = [];
        const trainLabels: ort.Tensor[] = [];
        const testData: ort.Tensor[] = [];
        const testLabels: ort.Tensor[] = [];

        //console.log("Data", data);

        for (let i = 0; i < numSamples; i++) {
            if (i < trainSamples) { // First part of the data goes to the training set
                trainBatchData.push(...data.slice(i * numFeatures, (i + 1) * numFeatures));
                trainBatchLabels.push(target[i]);

                // Process batch if it's full or if it's the last sample in the training set
                if (trainBatchData.length === batchSize * numFeatures || i === trainSamples - 1) {
                    trainData.push(new ort.Tensor('float32', new Float32Array(trainBatchData), [trainBatchData.length / numFeatures, numFeatures]));
                    trainLabels.push(new ort.Tensor('float32', new Float32Array(trainBatchLabels), [trainBatchLabels.length, 1]));

                    // Clear the batch arrays
                    trainBatchData = [];
                    trainBatchLabels = [];
                }
            } else { // Remaining data goes to the test set
                testBatchData.push(...data.slice(i * numFeatures, (i + 1) * numFeatures));
                testBatchLabels.push(target[i]);

                // Process batch if it's full or if it's the last sample in the test set
                if (testBatchData.length === batchSize * numFeatures || i === numSamples - 1) {
                    testData.push(new ort.Tensor('float32', new Float32Array(testBatchData), [testBatchData.length / numFeatures, numFeatures]));
                    testLabels.push(new ort.Tensor('float32', new Float32Array(testBatchLabels), [testBatchLabels.length, 1]));

                    // Clear the batch arrays
                    testBatchData = [];
                    testBatchLabels = [];
                }
            }
        }

        return { trainData, trainLabels, testData, testLabels };
    }

    // Function to remove duplicates
    private removeDuplicates(data: Float32Array, target: Float32Array): { data: Float32Array, target: Float32Array } {
        const dataMap = new Map<string, number>();
        const uniqueDataArray: number[] = [];
        const uniqueTargetArray: number[] = [];
        let duplicateCount = 0;

        for (let i = 0; i < data.length; i += MyData.NUM_FEATURES) {
            const slice = data.slice(i, i + MyData.NUM_FEATURES);
            const sliceKey = JSON.stringify(Array.from(slice));

            if (!dataMap.has(sliceKey)) {
                dataMap.set(sliceKey, uniqueDataArray.length);
                uniqueDataArray.push(...slice);
                uniqueTargetArray.push(target[i / MyData.NUM_FEATURES]);
            }else {
                duplicateCount++;
            }
        }

        console.log(`Number of duplicate entries removed: ${duplicateCount}`);

        return {
            data: new Float32Array(uniqueDataArray),
            target: new Float32Array(uniqueTargetArray),
        };
    }

    public async initializeSamplesAndFeatures() {

        const allData = this.transformDataFromObject(await this.fetchIndexedDBDataAndExtractFeaturesByIndex());
        const allTarget = this.extractLastArrayAndTransform(await this.fetchIndexedDBDataAndExtractFeaturesByIndex());

        // Remove duplicate entries from data and target
        const { data, target } = this.removeDuplicates(allData, allTarget);


        // Determine the number of samples
        const numSamples = data.length / MyData.NUM_FEATURES;
        MyData.NUM_TRAIN_SAMPLES = Math.floor(0.8 * numSamples);
        MyData.NUM_TEST_SAMPLES = numSamples - MyData.NUM_TRAIN_SAMPLES;

        // Get the tensors for training and testing data
        const { trainData, trainLabels, testData, testLabels } = await this.toTensor(data, target);
        // Assign to MyData
        MyData.trainData = trainData;
        MyData.testData = testData;
        MyData.trainTarget = trainLabels;
        MyData.testTarget = testLabels;

    }

    public async fetchIndexedDBDataAndExtractFeaturesByIndex(): Promise<{ [key: string]: Float32Array }> {
        // Function to initialize IndexedDB
        const initDB = (): Promise<IDBDatabase> => {
            return new Promise((resolve, reject) => {
                const token = sessionStorage.getItem('jwtToken');
                if (!token) return reject('No JWT token found');
                if (token) {
                    const decodedToken = jwtDecode(token);
                    //console.log("decodedToken", decodedToken);

                    const username = decodedToken.sub;

                    const request = indexedDB.open(`IndexedDB_${username}`, 1);

                    request.onupgradeneeded = (event) => {
                        const db = (event.target as IDBOpenDBRequest).result;
                        if (!db.objectStoreNames.contains('user_data_observations')) {
                            db.createObjectStore('user_data_observations', {keyPath: 'id', autoIncrement: true});
                        }
                    };

                    request.onsuccess = (event) => {
                        resolve((event.target as IDBOpenDBRequest).result);
                    };

                    request.onerror = (event) => {
                        reject('Database error: ' + (event.target as IDBOpenDBRequest).error);
                    };
                }
            });
        };

        // Function to fetch data from IndexedDB
        const fetchData = (db: IDBDatabase): Promise<any[]> => {
            return new Promise((resolve, reject) => {
                const transaction = db.transaction(['user_data_observations'], 'readonly');
                const objectStore = transaction.objectStore('user_data_observations');
                const request = objectStore.getAll();

                request.onsuccess = (event) => {
                    resolve((event.target as IDBRequest).result);
                };

                request.onerror = (event) => {
                    reject('Fetch error: ' + (event.target as IDBRequest).error);
                };
            });
        };

        const extractFeatures = (data: any[]): { [key: string]: Float32Array } => {
            const features: { [key: string]: Float32Array } = {};

            // Iterate over each entry in the data array
            for (const entry of data) {
                // Iterate over each key in the entry's data
                for (const key in entry.data) {
                    // Skip the "date" key
                    if (key !== "date") {
                        // Convert the data to a Float32Array
                        const array = new Float32Array(entry.data[key]);

                        // If the key already exists, concatenate the existing and new Float32Array
                        if (features[key]) {
                            const existingArray = features[key];
                            const concatenatedArray = new Float32Array(existingArray.length + array.length);
                            concatenatedArray.set(existingArray);
                            concatenatedArray.set(array, existingArray.length);
                            features[key] = concatenatedArray;
                        } else {
                            // If the key does not exist, just assign the new array
                            features[key] = array;
                        }
                    }
                }
            }

            return features;
        };


        try {
            const db = await initDB() as IDBDatabase;
            const allData: any[] = await fetchData(db);
            console.log(allData);

            if (allData.length > 0) {
                return extractFeatures(allData);
            } else {
                throw new Error('No data found in IndexedDB');
            }
        } catch (error) {
            console.error('Error fetching data from IndexedDB:', error);
            throw error;
        }
        // here
    }

    public transformDataFromObject = (dataObject: any): Float32Array => {
        // Extract arrays from the object
        const arrays = Object.values(dataObject);

        // Exclude the last array
        const arraysToUse: any[] = arrays.slice(0, -1);

        const numArrays = arraysToUse.length;
        const arrayLength = arraysToUse[0].length;

        // Initialize the 2D array
        const result = Array.from({ length: arrayLength }, () => Array(numArrays).fill(0));

        // Populate the 2D array
        for (let i = 0; i < arrayLength; i++) {
            for (let j = 0; j < numArrays; j++) {
                result[i][j] = arraysToUse[j][i];
            }
        }

        // Flatten the 2D array into a 1D array
        const flatArray = result.flat();

        // Convert the 1D array to a Float32Array
        return new Float32Array(flatArray);
    };


    public extractLastArrayAndTransform = (dataObject: any): Float32Array => {
        // Extract arrays from the object
        const arrays: any = Object.values(dataObject);

        // Get the last array
        const lastArray: Float32Array = arrays[arrays.length - 1];

        // Convert the last array to a 1D Float32Array directly
        return new Float32Array(lastArray);
    };



}