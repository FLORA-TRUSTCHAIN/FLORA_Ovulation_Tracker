import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

export const fetchUserData = async (token) => {
  try {
    const response = await axios.post(
      `${API_BASE_URL}/get_user_data`,
      null,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error('Error fetching user data:', error);
    throw error; // Propagate the error back to the caller
  }
};
