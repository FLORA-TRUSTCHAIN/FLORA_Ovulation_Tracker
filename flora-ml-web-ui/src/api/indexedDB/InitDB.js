import { openDB } from 'idb';

const InitDB = async (username, objectStoreItem) => {
  const dbName = `IndexedDB_${username}`;
  const db = await openDB(dbName, 1, {
    upgrade(db) {
      if (!db.objectStoreNames.contains(objectStoreItem)) {
        db.createObjectStore(objectStoreItem, {
          keyPath: 'id',
          autoIncrement: true, // Enable auto-incrementing keys
        });
      }
    },
  });
  return db;
};

export default InitDB;
