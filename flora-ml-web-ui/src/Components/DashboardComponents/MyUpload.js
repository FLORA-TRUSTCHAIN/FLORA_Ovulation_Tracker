import React, { useState, useEffect } from 'react';
import { fetchUserPreference } from '../../api/user/fetch_user_preferences_api';
import FileUpload from './FileUpload'; // Adjust path based on your project structure

const MyUpload = () => {
  const [isAllowed, setIsAllowed] = useState(null);

  useEffect(() => {
    const checkUserPreference = async () => {
      try {
        const result = await fetchUserPreference("sharing4good");
        setIsAllowed(result);
      } catch (error) {
        console.error('Failed to fetch user preference:', error);
        setIsAllowed(false);
      }
    };

    checkUserPreference();
  }, []);

  if (isAllowed === null) {
    // Optionally, render a loading indicator while waiting for the API call to complete
    return <div>Loading...</div>;
  }

  if (isAllowed === false) {
    // If the user is not allowed, render a message
    return (
      <div>
        Access Denied. Uploading files is only available if you enable Sharing4Good.
      </div>
    );
  }

  // Render the main content if the user is allowed
  return (
    <div>
      <FileUpload />
    </div>
  );
};

export default MyUpload;
