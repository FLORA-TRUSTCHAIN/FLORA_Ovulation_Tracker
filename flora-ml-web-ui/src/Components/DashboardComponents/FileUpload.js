import React, { useCallback, useState, useEffect } from 'react';
import { useDropzone } from 'react-dropzone';
import {
  Box,
  Text,
  VStack,
  HStack,
  Icon,
  Button,
  IconButton,
  useColorMode,
  useToast,
} from '@chakra-ui/react';
import { MdDelete } from 'react-icons/md';
import axios from 'axios';
import {jwtDecode} from 'jwt-decode';
import InitDB from '../../api/indexedDB/InitDB';
import Papa from 'papaparse';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

const FileUpload = ({ onUpload }) => {
  const { colorMode } = useColorMode();
  const isDark = colorMode === 'dark';
  const [acceptedFiles, setAcceptedFiles] = useState([]);
  const [isUploading, setIsUploading] = useState(false);
  const toast = useToast();

  useEffect(() => {
    const initializeDB = async () => {
      const token = sessionStorage.getItem("jwtToken");
      const decodedToken = jwtDecode(token);
      const username = decodedToken.sub;
      const objectStoreItem = 'user_data_observations';
      try {
        const db = await InitDB(username, objectStoreItem);
        console.log(`Database initialized for ${username} with object store ${objectStoreItem}`);
      } catch (error) {
        console.error('Failed to initialize database', error);
      }
    };
    initializeDB();
  }, []);

  const onDrop = useCallback(
    (droppedFiles) => {
      const filteredFiles = droppedFiles.filter(file =>
        file.type === 'application/zip' || file.type === 'text/csv' ||
        file.name.endsWith('.zip') || file.name.endsWith('.csv')
      );

      const rejectedFiles = droppedFiles.filter(file => !filteredFiles.includes(file));
      if (rejectedFiles.length > 0) {
        toast({
          title: 'Invalid File Type',
          description: `Only .csv and .zip files are allowed. Ignoring ${rejectedFiles.length} file(s).`,
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }

      const newFiles = [...acceptedFiles, ...filteredFiles.slice(0, 5 - acceptedFiles.length)];
      setAcceptedFiles(newFiles);
    },
    [acceptedFiles, toast]
  );

  const removeFile = (fileToRemove) => {
    const updatedFiles = acceptedFiles.filter(file => file !== fileToRemove);
    setAcceptedFiles(updatedFiles);
  };

  const uploadToServer = async () => {
    const formData = new FormData();
    acceptedFiles.forEach((file) => {
      formData.append('files', file);
    });

    await axios.post(`${API_BASE_URL}/upload`, formData, {
      headers: {
        Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`,
        'Content-Type': 'multipart/form-data'
      }
    });
  };

  const saveLocally = async () => {
    const token = sessionStorage.getItem("jwtToken");
    const decodedToken = jwtDecode(token);
    const username = decodedToken.sub;
  
    for (const file of acceptedFiles) {
      const text = await file.text();
      const parsedData = Papa.parse(text, {
        header: true,
        skipEmptyLines: true,
        trimHeaders: true,
        dynamicTyping: true,
      });
  
      const dataDict = parsedData.data.reduce((acc, row) => {
        Object.keys(row).forEach((key) => {
          if (!acc[key]) {
            acc[key] = [];
          }
          acc[key].push(row[key]);
        });
        return acc;
      }, {});
  
      const db = await InitDB(username, 'user_data_observations');
      const transaction = db.transaction('user_data_observations', 'readwrite');
      const store = transaction.objectStore('user_data_observations');
      store.add({ data: dataDict });
  
      await transaction.complete;
    }
  };
  

  const handleUpload = async (toServer = true, toLocal = true) => {
    setIsUploading(true);
    try {
      if (toServer) {
        await uploadToServer();
      }

      if (toLocal) {
        await saveLocally();
      }

      toast({
        title: 'Upload Successful',
        description: 'All files have been uploaded and processed successfully.',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
      setAcceptedFiles([]);
    } catch (error) {
      toast({
        title: 'Upload Failed',
        description: 'An error occurred while uploading files. Please try again.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setIsUploading(false);
    }
  };

  const {
    getRootProps,
    getInputProps,
    isDragActive,
    isDragReject,
  } = useDropzone({
    onDrop,
    accept: { 'text/csv': ['.csv'], "application/zip": ['.zip'] },
    multiple: true,
    maxSize: 200 * 1024 * 1024,
    maxFiles: 5 - acceptedFiles.length,
  });

  const files = acceptedFiles.map((file) => (
    <HStack key={file.name} justifyContent="space-between" w="100%">
      <Text>{file.name}</Text>
      <IconButton
        icon={<Icon as={MdDelete} />}
        aria-label="Delete"
        onClick={() => removeFile(file)}
      />
    </HStack>
  ));

  return (
    <VStack spacing={4} align="flex-start">
      <Box
        {...getRootProps({
          bg: isDragActive ? (isDark ? 'gray.700' : 'gray.100') : (isDark ? 'gray.800' : 'gray.50'),
          color: isDark ? 'white' : 'gray.800',
          border: '2px dashed',
          borderColor: isDragActive ? 'green.500' : 'gray.200',
          borderRadius: 'md',
          p: 4,
          w: '100%',
          cursor: 'pointer',
        })}
        _hover={{
          bg: isDark ? 'gray.700' : 'gray.100',
        }}
        _focus={{
          outline: 'none',
          boxShadow: 'outline',
        }}
      >
        <input {...getInputProps()} />
        {isDragActive ? (
          <Text>Drop the files here ...</Text>
        ) : (
          <Text>
            Drag 'n' drop .csv or .zip files here, or click to select files (Limit: 5 files per upload)
          </Text>
        )}
        {isDragReject && (
          <Text color="red.500">Only .csv and .zip files are allowed</Text>
        )}
      </Box>
      <VStack spacing={2} align="flex-start" w="100%">
        {files}
      </VStack>
      {acceptedFiles.length > 0 && (
        <HStack spacing={2}>
          <Button colorScheme="blue" onClick={() => handleUpload(true, false)} isLoading={isUploading} loadingText="Uploading">
            Upload Data to the Server
          </Button>
          <Button colorScheme="green" onClick={() => handleUpload(false, true)} isLoading={isUploading} loadingText="Saving">
            Save Data Locally
          </Button>
          <Button colorScheme="purple" onClick={() => handleUpload(true, true)} isLoading={isUploading} loadingText="Uploading">
            Upload to Server & Save locally
          </Button>
        </HStack>
      )}
    </VStack>
  );
};

export default FileUpload;
