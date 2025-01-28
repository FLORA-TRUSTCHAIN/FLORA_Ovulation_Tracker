import { openDB } from 'idb';

const dbPromise = openDB('file-cache-db', 1, {
  upgrade(db) {
    const store = db.createObjectStore('files');
    store.createIndex('filename', 'filename', { unique: false });
  },
});

export const getFileFromCache = async (key) => {
  const db = await dbPromise;
  const fileEntry = await db.get('files', key);
  return fileEntry ? fileEntry.file : null;
};

export const getFilenameFromCache = async (key) => {
  const db = await dbPromise;
  const fileEntry = await db.get('files', key);
  return fileEntry ? fileEntry.filename : null;
};

export const saveFileToCache = async (key, file, filename) => {
  const db = await dbPromise;
  const fileEntry = { file, filename };
  await db.put('files', fileEntry, key);
};
