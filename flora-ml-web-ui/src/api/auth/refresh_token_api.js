import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

export const refreshJWTtoken = async () => {
  try {
    const response = await axios.post(
      `${API_BASE_URL}/refreshToken`,
      {},
      {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`
        }
      }
    );
    console.log("Old token");
    console.log(sessionStorage.getItem('jwtToken'));
    const { access_token } = response.data;
    sessionStorage.setItem('jwtToken', access_token);

    console.log("New token");
    console.log(sessionStorage.getItem('jwtToken'));
    return true;
  } catch (error) {
    console.error('Token validation failed:', error);
    throw error; // Rethrow the error so that the caller can handle it
  }
};
