import {jwtDecode} from 'jwt-decode';

const OBJECT_STORE_NAME_CHECKPOINT = 'downloaded_checkpoint';

const initDB = (storeName) => {
  return new Promise((resolve, reject) => {
    const token = sessionStorage.getItem('jwtToken');
    const decodedToken = jwtDecode(token);
    const username = decodedToken.sub;
    const request = indexedDB.open(`IndexedDB_${username}`, 1);

    request.onupgradeneeded = (event) => {
      const db = event.target.result;
      if (!db.objectStoreNames.contains(storeName)) {
        db.createObjectStore(storeName, { keyPath: 'filename' });
      }
    };

    request.onsuccess = (event) => resolve(event.target.result);
    request.onerror = (event) => reject(event.target.error);
  });
};

export const getLocalCheckpoint = async (filename) => {
  try {
    const db = await initDB(OBJECT_STORE_NAME_CHECKPOINT);
    const transaction = db.transaction([OBJECT_STORE_NAME_CHECKPOINT], 'readonly');
    const store = transaction.objectStore(OBJECT_STORE_NAME_CHECKPOINT);

    return new Promise((resolve, reject) => {
      const request = store.get(filename);
      request.onsuccess = (event) => {
        const file = event.target.result;
        if (file) {
          const url = URL.createObjectURL(file.blob);
          resolve(url);
        } else {
          resolve(null);
        }
      };
      request.onerror = (event) => reject(event.target.error);
    });
  } catch (error) {
    console.error('Error fetching file from IndexedDB:', error);
    return null;
  }
};
