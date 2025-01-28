import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

export const login_api = async ({ username, password }) => {
  const response = await axios.post(`${API_BASE_URL}/login`, {
    username,
    password
  },{ headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }
);

  const { access_token } = response.data;
  
    
  sessionStorage.setItem('jwtToken', access_token);
  
  console.log("JWT Token",access_token)
  
  return response.data;
};
