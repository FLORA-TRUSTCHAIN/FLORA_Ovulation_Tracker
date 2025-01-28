import axios from "axios";
import { getFileFromCache, saveFileToCache } from './idb-utils';
import { timestamp, getCurrentUser } from './utils'



export const downloadPthFile = async () => {
  const currentTimestamp = timestamp();
  const username = getCurrentUser();
  const filename = `${currentTimestamp}_${username}_model_mlp.pth`;

  const cachedFile = await getFileFromCache(filename);
  if (cachedFile) {
    console.log('File served from cache.');
    return cachedFile;
  }

  try {
    const response = await axios.post(
      `${process.env.REACT_APP_API_BASE_URL}/download-pth-file/`,
      {},
      {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`,
        },
        responseType: 'blob',
      }
    );

    const fileBlob = response.data;
    await saveFileToCache(filename, fileBlob, filename);

    console.log('File downloaded and saved to cache.');
    return fileBlob;
  } catch (error) {
    console.error('Error downloading the file:', error);
    return null;

}};
