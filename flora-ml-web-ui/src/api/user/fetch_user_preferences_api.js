import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

export const fetchUserPreference = async (preference) => {
  try {
    const response = await axios.post(
      `${API_BASE_URL}/get_user_preference/${preference}`,
      {}, // Empty body
      {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`,
        },
      }
    );

    if (response.data[preference]) {
      return true; 
    }
    return false;

  } catch (error) {
    console.error('Error fetching user data:', error);
    throw error; // Propagate the error back to the caller
  }
};
