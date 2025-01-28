import axios from 'axios';

export const uploadPthFile = async (fileBlob, filename) => {
  const formData = new FormData();
  formData.append('file', fileBlob, filename);
console.log(formData);
  try {
    const response = await axios.post(
      `${process.env.REACT_APP_API_BASE_URL}/upload-pth-file/`,
      formData,
      {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`,
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    console.log('File uploaded successfully.');
    return response.data;
  } catch (error) {
    console.error('Error uploading the file:', error);
    throw error;
  }
};
