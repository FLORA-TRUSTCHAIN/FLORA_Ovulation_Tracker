import React, { useState } from 'react';
import { downloadPthFile } from '../../api/file_download_upload/download_pth_api';
import { uploadPthFile } from '../../api/file_download_upload/upload_pth_api';
import { getFileFromCache, getFilenameFromCache } from '../../api/file_download_upload/idb-utils'; // Adjust the import path as necessary
import { timestamp, getCurrentUser } from '../../api/file_download_upload/utils';

const AutoDownload = () => {
  const [downloadStatus, setDownloadStatus] = useState(null);
  const [uploadStatus, setUploadStatus] = useState(null);
  const [cachedFilename, setCachedFilename] = useState('');

  const handleDownloadClick = async () => {
    try {
      console.log('Starting download...');
      const fileBlob = await downloadPthFile();
      if (fileBlob) {
        setDownloadStatus('success');
        const currentTimestamp = timestamp();
        const username = getCurrentUser();
        const filename = `${currentTimestamp}_${username}_model_mlp.pth`;
        setCachedFilename(filename);
      } else {
        setDownloadStatus('failure');
      }
    } catch (error) {
      console.error('Error downloading the file:', error);
      setDownloadStatus('failure');
    }
  };

  const handleUploadClick = async () => {
    try {
      const fileBlob = await getFileFromCache(cachedFilename);
      const filename = await getFilenameFromCache(cachedFilename);
      if (fileBlob && filename) {
        await uploadPthFile(fileBlob, filename);
        setUploadStatus('success');
      } else {
        setUploadStatus('failure');
      }
    } catch (error) {
      console.error('Error uploading the file:', error);
      setUploadStatus('failure');
    }
  };

  return (
    <div>
      <h1>Download and Upload File</h1>
      <button onClick={handleDownloadClick}>Download</button>
      {downloadStatus === 'success' && <p>File downloaded and cached successfully.</p>}
      {downloadStatus === 'failure' && <p>Failed to download file.</p>}
      <button onClick={handleUploadClick} disabled={!cachedFilename}>Upload Cached File</button>
      {uploadStatus === 'success' && <p>File uploaded successfully.</p>}
      {uploadStatus === 'failure' && <p>Failed to upload file.</p>}
    </div>
  );
};

export default AutoDownload;
