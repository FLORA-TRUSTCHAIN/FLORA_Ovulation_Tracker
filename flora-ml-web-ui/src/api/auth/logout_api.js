import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

export const logout_api = async () => {
  const response = await axios.post(`${API_BASE_URL}/logout`, null, {
        headers: { Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}` }
  });
  return response.data;
};
