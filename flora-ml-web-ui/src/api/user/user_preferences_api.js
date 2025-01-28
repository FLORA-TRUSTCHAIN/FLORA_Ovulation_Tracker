import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

export const update_user_preferences = async (preferenceType, booleanPreference, token) => {
    try {
        const response = await axios.post(
            `${API_BASE_URL}/update_user_preferences/${preferenceType}/${booleanPreference}`,
            null,
            {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );
        return response.data;
    } catch (error) {
        // Handle error responses here if needed
        throw error;
    }
};


export const update_user = async (payload) => {
  const token = sessionStorage.getItem('jwtToken');
  try {
    const response = await axios.post(
      `${API_BASE_URL}/update_user_info`,
      { ...payload, token },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error('Error updating user:', error);
    // Assuming the error response contains a message in error.response.data
    return { error: error.response?.data?.detail || 'An error occurred while updating the user.' };
  }
};
