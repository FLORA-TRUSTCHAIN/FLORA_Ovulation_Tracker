import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

export const delete_my_files_api = async () => {
  try {
    const response = await axios.post(
      `${API_BASE_URL}/delete_user_files`,
      {}, // Empty object since no additional data is needed for deletion
      {
        headers: {
          
          'Content-Type': 'application/json',
          Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`,
          
        },
      }
    );

    const result = response.data;
    // Assuming backend returns a meaningful response indicating success
    if (result && result.message === "User files deleted successfully") {
      return true; // Return true if deletion was successful
    } else {
      return false; // Return false or handle differently if deletion was not successful
    }
  } catch (error) {
    console.error('Error deleting user files:', error);
    throw error; // Re-throw the error to handle it elsewhere in your application
  }
};
