import * as ort from 'onnxruntime-web/training';
import { showStatusMessage, showErrorMessage } from './logging'
import { calculateMSE, calculateMAE } from './helpers';

// Converts the output tensor from the neural network into an array of predicted values
export function getPredictions(output: ort.Tensor): number[] {
    const result = [];
    const [batchSize] = output.dims; // The output shape is [batch_size, 1]
    for (let i = 0; i < batchSize; ++i) {
        // Extract the single value for each batch entry
        const value = output.data[i] as number;
        result.push(value);
    }
    return result;
}

// Function to update inference predictions
export async function updateInferencePredictions(
    session: ort.InferenceSession,
    data: number[][],
    labels: number[],
    setPredictions: React.Dispatch<React.SetStateAction<number[]>>,
    setMSE: React.Dispatch<React.SetStateAction<number>>,
    setMAE: React.Dispatch<React.SetStateAction<number>>,
    setStatusMessage: React.Dispatch<React.SetStateAction<string>>,
    setErrorMessage: React.Dispatch<React.SetStateAction<string>>
) {
    const numSamples = data.length;
    const numFeatures = data[0].length;
    showStatusMessage(`[Inference] Num Samples: ${numSamples}`, setStatusMessage);
    showStatusMessage(`[Inference] Num Features: ${numFeatures}`, setStatusMessage);

    // Flatten the input data into a single Float32Array
    const input = new Float32Array(numSamples * numFeatures);
    for (let i = 0; i < numSamples; ++i) {
        for (let j = 0; j < numFeatures; ++j) {
            input[i * numFeatures + j] = data[i][j];
        }
    }
    console.log('[Inference] Input: ', input);

    // Set the correct shape for the input tensor
    const batchShape = [numSamples, numFeatures];
    showStatusMessage(`[Inference] BatchShape: ${batchShape}`, setStatusMessage);

    // Create the batched input tensor
    const batchedInput = new ort.Tensor('float32', input, batchShape);
    console.log('[Inference] Batched Input: ', batchedInput);
    showStatusMessage(`Input Tensor Shape: ${batchedInput.dims}`, setStatusMessage);
    showStatusMessage(`Input Tensor Data Type: ${batchedInput.type}`, setStatusMessage);

    // Prepare the feeds object with only the input tensor
    const feeds = {
        input: batchedInput,  // Ensure the input name matches the model export input name
    };
    console.log('[Inference] feeds: ', feeds);
    showStatusMessage(`Input tensor shape: ${feeds.input.dims}`, setStatusMessage);

    // Run the model with the prepared inputs
    try {
        const runModelResults = await session.run(feeds);
        console.log('[Inference] results ', runModelResults);

        // Extract predictions from the model's output
        const predictions = getPredictions(runModelResults['output']);
        setPredictions(predictions.slice(0, numSamples));
        const newMSE = calculateMSE(predictions, labels);
        const newMAE = calculateMAE(predictions, labels);
        setMSE(newMSE);
        setMAE(newMAE);
    } catch (error) {
        showErrorMessage(`Error during inference: ${error}`, setErrorMessage);
    }
}