import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

export const validateToken = async (token) => {
  try {
    const response = await axios.post(
      `${API_BASE_URL}/verifyToken`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    );
    console.log("Validtation Ok");
    return response.data;
  } catch (error) {
    console.error('Token validation failed:', error);
    throw error; // Rethrow the error so that the caller can handle it
  }
};
