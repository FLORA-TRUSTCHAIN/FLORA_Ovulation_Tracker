// Function to represent the data
export function getFeatureMatrix(data: Float32Array, numSamples: number, numFeatures: number): number[][] {
    const result: number[][] = [];
    // Iterate over each sample
    for (let sample = 0; sample < numSamples; ++sample) {
        const featureRow: number[] = [];
        // Iterate over each feature of the current sample
        for (let feature = 0; feature < numFeatures; ++feature) {
            // Calculate the index in the data array and push the feature value to the feature row
            featureRow.push(data[sample * numFeatures + feature]);
        }
        // Add the feature row to the result matrix
        result.push(featureRow);
    }
    // Return the resulting feature matrix
    return result;
}

// Finds the index of the maximum value in a Float32Array
export function indexOfMax(arr: Float32Array): number {
    // Throw an error if the array is empty
    if (arr.length === 0) {
        throw new Error('index of max (used in test accuracy function) expects a non-empty array. Something went wrong.');
    }

    // Initialize the index of the maximum value to the first element
    let maxIndex = 0;
    // Iterate over the array starting from the second element
    for (let i = 1; i < arr.length; i++) {
        // Update maxIndex if the current element is greater than the current max
        if (arr[i] > arr[maxIndex]) {
            maxIndex = i;
        }
    }
    // Return the index of the maximum value
    return maxIndex;
}

// Function to calculate Mean Squared Error
export function calculateMSE(predictions: number[], labels: number[]): number {
    const squareDiffs = predictions.map((pred, index) => {
        const diff = pred - labels[index];
        return diff * diff;
    });
    const meanSquareError = squareDiffs.reduce((a, b) => a + b, 0) / squareDiffs.length;
    return meanSquareError;
}

// Function to calculate Mean Absolute Error
export function calculateMAE(predictions: number[], labels: number[]): number {
    const absoluteDiffs = predictions.map((pred, index) => Math.abs(pred - labels[index]));
    const meanAbsoluteError = absoluteDiffs.reduce((a, b) => a + b, 0) / absoluteDiffs.length;
    return meanAbsoluteError;
}
