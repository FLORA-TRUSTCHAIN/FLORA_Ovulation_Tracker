import { jwtDecode } from 'jwt-decode';

export const timestamp = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  const seconds = String(now.getSeconds()).padStart(2, '0');

  return `${year}-${month}-${day}-${hours}-${minutes}-${seconds}`;
};

export const getCurrentUser = () => {
  const token = sessionStorage.getItem('jwtToken');
  if (!token) {
    return 'guest';
  }

  try {
    const decodedToken = jwtDecode(token);
    return decodedToken.sub || 'guest';  
  } catch (error) {
    console.error('Error decoding the JWT token:', error);
    return 'guest';
  }
};
