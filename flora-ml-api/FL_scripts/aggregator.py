import os
import shutil



from FL_scripts.aggregator_helpers import load_json_files, mean_aggregate, set_torch_weights, export_to_onnx
from FL_scripts.torch_helpers.models.mlp import MLP

FL_FOLDER = "./FL/"

def safe_key(folder):
    parts = folder.split('_')
    if len(parts) > 1 and parts[1].isdigit():
        return int(parts[1])
    return -1





def create_global_checkpoint():
    # List everything in the main folder
    all_items = os.listdir(FL_FOLDER)

    # Filter out subfolders only
    subfolders = [item for item in all_items if os.path.isdir(os.path.join(FL_FOLDER, item))]

    # Sort subfolders by the numerical value in their name
    sorted_subfolders = sorted(subfolders, key=safe_key)

    # Get the latest subfolder and latest - 1 subfolder
    latest_subfolder = sorted_subfolders[-1]
    latest_minus_one_subfolder = sorted_subfolders[-2] if len(sorted_subfolders) > 1 else None

    # Construct the path to the latest subfolder
    latest_subfolder_path = os.path.join(FL_FOLDER, latest_subfolder)

    # reserved for possible future use
    #latest_minus_one_subfolder_path = os.path.join(FL_FOLDER,
    #                                               latest_minus_one_subfolder) if latest_minus_one_subfolder else None

    print(f"The latest subfolder is: {latest_subfolder}")
    print(f"The second latest subfolder is: {latest_minus_one_subfolder}")

    data = load_json_files(latest_subfolder_path)

    mean_params = mean_aggregate(data)

    model = MLP(input_size=3, hidden_size=64, output_size=1)

    set_torch_weights(model, mean_params)

    onnx_store_dir = "./FL/tmp/"
    export_to_onnx(model, onnx_store_dir=onnx_store_dir)

    # Copy the file that ends with '_checkpoint' to 'round_X'
    for file in os.listdir(onnx_store_dir):
        if file.endswith('_checkpoint'):
            shutil.copy(os.path.join(onnx_store_dir, file), latest_subfolder_path)

    # Delete all files in 'tmp' except 'README.md'
    for file in os.listdir(onnx_store_dir):
        if file != 'README.md':
            os.remove(os.path.join(onnx_store_dir, file))

    latest_checkpoint_filename = "train_mlp_checkpoint"
    latest_checkpoint_path = os.path.join(latest_subfolder_path, latest_checkpoint_filename)

    parent_directory = os.path.abspath(os.path.join(latest_subfolder_path, os.pardir))
    main_checkpoint_path = os.path.join(parent_directory, latest_checkpoint_filename)

    try:
        # Ensure the source file exists
        if os.path.isfile(latest_checkpoint_path):
            # Replace the main checkpoint file with the latest one
            shutil.copy(latest_checkpoint_path, main_checkpoint_path)
            print(f"Replaced {main_checkpoint_path} with {latest_checkpoint_path}")
        else:
            print(f"The file {latest_checkpoint_path} does not exist.")
    except Exception as e:
        print(f"An error occurred: {e}")

