import * as ort from 'onnxruntime-web/training';
import { showStatusMessage, showErrorMessage } from './logging';
import { getFileFromIndexedDB } from '../../../api/indexedDB/IndexedDBLocalONNX';
import { getLocalCheckpoint } from '../../../api/indexedDB/IndexedDBLocalCheckpoint';

// Function to load an inference session
export async function getInferenceSession(
    url: string,
    setStatusMessage: React.Dispatch<React.SetStateAction<string>>,
    setErrorMessage: React.Dispatch<React.SetStateAction<string>>
): Promise<ort.InferenceSession> {
    // Show a status message indicating the start of the model loading process
    showStatusMessage(`Loading ONNX model at "${url}"...`, setStatusMessage);

	try {
        // create an inference session, using WASM backend.
		const result = await ort.InferenceSession.create(url, { executionProviders: ['wasm'] })
		// Show a status message indicating the model was successfully loaded
		showStatusMessage(`Loaded the model at "${url}"`, setStatusMessage);
		return result;
	} catch (err) {
        // Show an error message if the model loading fails
        showErrorMessage(`Error loading the model: ${err}`, setErrorMessage);
		throw err
	}
}

// Function to load a training session
export async function getTrainingSession(
    setStatusMessage: React.Dispatch<React.SetStateAction<string>>,
    setErrorMessage: React.Dispatch<React.SetStateAction<string>>
): Promise<ort.TrainingSession> {
    // Show a status message indicating the start of the training session loading process
    showStatusMessage(`[Training] Attempting to load training session...`, setStatusMessage);

    // Define the paths for the checkpoint, training, optimizer, and evaluation models
    const checkpointPath = await getLocalCheckpoint('train_mlp_checkpoint');
    const trainingPath = await getFileFromIndexedDB('train_mlp_training_model.onnx');
    const optimizerPath = await getFileFromIndexedDB('train_mlp_optimizer_model.onnx');
    const evalPath = await getFileFromIndexedDB('train_mlp_eval_model.onnx');
    
    const createOptions: ort.TrainingSessionCreateOptions = {
        checkpointState: checkpointPath,
        trainModel: trainingPath,
        evalModel: evalPath,
        optimizerModel: optimizerPath
    };

    try {
        // Create the training session using the WASM backend
        const session = await ort.TrainingSession.create(createOptions, { executionProviders: ['wasm'] });
        // Show a status message indicating the training session was successfully loaded
        showStatusMessage(`TrainingSession loaded!`, setStatusMessage);
        return session;
    } catch (err) {
        // Show an error message if the training session loading fails
        showErrorMessage(`Error Loading the training session: ` + err, setErrorMessage);
        console.error(`Error loading the training session: `, err);
        throw err;
    }
}