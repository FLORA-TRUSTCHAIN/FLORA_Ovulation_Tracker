import React, { useState, useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import { validateToken } from '../api/auth/validate_token';

const PrivateRoute = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(null);

  useEffect(() => {
    const checkAuth = async () => {
      const token = sessionStorage.getItem('jwtToken');
      if (!token) {
        setIsAuthenticated(false);
        return;
      }

      try {
        await validateToken(token);
        setIsAuthenticated(true);
      } catch (error) {
        setIsAuthenticated(false);
      }
    };

    checkAuth();
  }, []);

  if (isAuthenticated === null) {
    return <div>Loading...</div>; // Or some kind of loading indicator
  }

  return isAuthenticated ? children : <Navigate to="/login" />;
};

export default PrivateRoute;
