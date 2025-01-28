import io
import json
import os
from typing import Dict, List

import numpy as np
import onnx
import torch.nn
from onnxruntime.training import artifacts


def load_json_files(folder_path: str) -> Dict[str, List]:
    """
    Load JSON files from a specified folder and extract the 'params' key from each file.

    Parameters:
        folder_path (str): Path to the folder containing JSON files.

    Returns:
        dict: A dictionary where the keys are the JSON filenames and the values are the 'params' data.
    """
    # List all JSON files in the specified folder
    json_files = [f for f in os.listdir(folder_path) if f.endswith('.json')]
    print(f"Found {len(json_files)} files in {folder_path}")

    # Initialize an empty dictionary to store the 'params' data
    data = {}

    for file_name in json_files:
        file_path = os.path.join(folder_path, file_name)
        try:
            with open(file_path, 'r') as file:
                json_data = json.load(file)
                if 'params' in json_data:
                    key_name = file_name.replace('.json', '')
                    data[key_name] = json_data['params']
        except json.JSONDecodeError as e:
            print(f"Error decoding JSON from file {file_name}: {e}")

    return data


def mean_aggregate(data: Dict[str, List]) -> np.ndarray:
    """
    Compute the mean of the 'params' data.

    Parameters:
        data (dict): A dictionary where the keys are the JSON filenames and the values are the 'params' data.

    Returns:
        np.ndarray: An array representing the mean of the 'params' data.
    """
    # List to store all parameter arrays
    param_arrays = []

    # Iterate over the 'params' data from each file
    for key in data:
        param_arrays.append(np.array(data[key]))

    # Compute the mean of all parameter arrays
    mean_params = np.mean(param_arrays, axis=0)

    return mean_params


def set_torch_weights(model: torch.nn.Module, params: np.ndarray) -> None:
    """
    Set the weights of a PyTorch model using the provided parameters.

    Parameters:
        model (torch.nn.Module): The PyTorch model to update.
        params (np.ndarray): The parameters to set.

    Returns:
        None
    """
    # Convert numpy array to torch tensor
    params = torch.from_numpy(params).float()

    # Pointer to keep track of the current position in the mean parameters array
    pointer = 0

    # Iterate over the model parameters
    for name, param in model.named_parameters():
        # Calculate the number of elements in the parameter
        num_elements = param.numel()

        # Extract the corresponding part of the params
        param_mean = params[pointer:pointer + num_elements]

        # Reshape the extracted part to match the parameter shape
        param_mean = param_mean.view(param.size())

        # Update the parameter data
        param.data.copy_(param_mean)

        # Move the pointer to the next position
        pointer += num_elements

    #Make sure we 've used exactly all the parameters
    print(pointer)
    print(len(params))
    assert pointer == len(params), "Mismatch between model parameters and mean parameters length"


def export_to_onnx(model: torch.nn.Module,
                   input_shape: int = 3,
                   model_name: str = 'mlp',
                   onnx_store_dir = "./checkpoints/fl_files/tmp/"):
    input_names = ['input']  # input names
    output_names = ['output']  # output names
    do_constant_folding = False  # whether to execute constant folding for optimization
    dynamic_axes = {  # variable length axes. The names must match the input and output names
        'input': {0: 'batch_size'},
        'output': {0: 'batch_size'}
    }

    # Generate some random inputs
    model_inputs = (torch.randn(3, input_shape, device='cpu'),)


    prefix = f"train_{model_name}_"
    mode = torch.onnx.TrainingMode.TRAINING

    f = io.BytesIO()
    onnx_optim = artifacts.OptimType.AdamW
    onnx_criterion = artifacts.LossType.L1Loss

    print(onnx_optim)
    print(onnx_criterion)

    torch.onnx.export(
        model,
        model_inputs,
        f,
        input_names=input_names,
        output_names=output_names,
        do_constant_folding=do_constant_folding,
        training=mode,
        dynamic_axes=dynamic_axes,
        export_params=True,  # store the trained parameter weights inside the model file
        keep_initializers_as_inputs=False,
        verbose=False
    )
    onnx_model = onnx.load_model_from_string(f.getvalue())

    requires_grad = [name for name, param in model.named_parameters() if param.requires_grad]
    frozen_params = [name for name, param in model.named_parameters() if not param.requires_grad]

    artifacts.generate_artifacts(
        onnx_model,  # The base model to be used for gradient graph generation.
        optimizer=onnx_optim,  # The optimizer enum or onnxblock to be used for training.
        loss=onnx_criterion,  # The loss function enum or onnxblock to be used for training
        requires_grad=requires_grad,  # List of names of model parameters that require gradient computation
        frozen_params=frozen_params,  # List of names of model parameters that should be frozen.
        additional_output_names=output_names,
        # List of additional output names to be added to the training/eval model in addition to the loss output.
        artifact_directory=onnx_store_dir,  # The directory to save the generated artifacts.
        prefix=prefix,  # The prefix to be used for the generated artifacts.
    )
    print(f"ONNX artifacts have been saved to {onnx_store_dir}")

