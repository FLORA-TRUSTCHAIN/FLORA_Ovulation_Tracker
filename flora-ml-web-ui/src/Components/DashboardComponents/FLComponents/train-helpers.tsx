import * as ort from 'onnxruntime-web/training';

import { MyData } from './data';

//import { showStatusMessage, showErrorMessage } from './logging';
import { calculateMSE, calculateMAE } from './helpers';
//import { getPredictions } from './inference-helpers';

export async function trainOneEpoch(
    session: ort.TrainingSession, dataset: MyData, epoch: number, lossNodeName: string
) {
    let batchNum = 0;
    const numSamples = dataset.getNumTrainSamples();
    let totalNumBatches = dataset.getNumBatches(numSamples);
    const epochStartTime = Date.now();
    let iterationsPerSecond = 0;
    for await (const batch of dataset.trainingBatches(true)) {
        ++batchNum;

        const batchedInput = batch.data;
        const batchedTarget = batch.target;

        // create the input
        const feeds = {
            input: batchedInput,
            target: batchedTarget
        }


        // call train step
        const results = await session.runTrainStep(feeds);
        //const loss = results[lossNodeName];
        //console.log("[res]: ", results[lossNodeName].data);
        iterationsPerSecond = batchNum / ((Date.now() - epochStartTime) / 1000);

         // update weights and reset gradients
         await session.runOptimizerStep();
         await session.lazyResetGrad();
    }
    return iterationsPerSecond;
}

// Function to evaluate the model
export async function evaluateModel(
    trainingSession: ort.TrainingSession,
    dataset: MyData,
    lossNodeName: string,
    mode: string = 'eval'
): Promise<{ averageLoss: number, mse: number, mae: number }> {
    let accumulatedLoss: number = 0.0;
    let batchNum: number = 0;
    let allPredictions: number[] = [];
    let allTargets: number[] = [];

    // Determine which batches to use based on the mode
    const batchGenerator = mode === 'eval' ? dataset.testBatches() : dataset.trainingBatches();

    for await (const batch of batchGenerator) {
        // Create input
        ++batchNum;
        const feeds = {
            input: batch.data,
            target: batch.target
        };

        // Run evaluation step
        const results = await trainingSession.runEvalStep(feeds);

        // Extract loss
        const loss = results[lossNodeName].data[0] as number;
        accumulatedLoss += loss;

        // Get predictions directly as Float32Array
        const batchPredictions = Array.from(results['output'].data as Float32Array);
        allPredictions = allPredictions.concat(batchPredictions);

        // Collect all targets for MSE and MAE calculation
        const batchTargets = Array.from(batch.target.data as Float32Array);
        allTargets = allTargets.concat(batchTargets);
    }

    // Calculate MSE and MAE
    const mse = calculateMSE(allPredictions, allTargets);
    const mae = calculateMAE(allPredictions, allTargets);

    return {
        averageLoss: accumulatedLoss / batchNum,
        mse,
        mae
    };
}
