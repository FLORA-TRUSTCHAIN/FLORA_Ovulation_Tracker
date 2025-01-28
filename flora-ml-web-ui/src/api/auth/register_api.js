import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000, // Adjust as needed
});

export const registerUser = async (userData) => {
  try {
    const response = await axiosInstance.post('/register/', userData);
    return response.data;
  } catch (error) {
    throw error;
  }
};

// Add more API functions as needed
