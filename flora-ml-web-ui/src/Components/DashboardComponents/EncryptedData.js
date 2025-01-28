import React, { useState, useEffect } from 'react';
import axios from 'axios';
import CryptoJS from 'crypto-js';
import {
  Box,
  Button,
  Container,
  Heading,
  Input,
  Table,
  Tbody,
  Td,
  Th,
  Thead,
  Tr,
  VStack,
  useToast,
  HStack,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalFooter,
  ModalBody,
  ModalCloseButton,
  RadioGroup,
  Radio,
  Stack
} from '@chakra-ui/react';

import ChartTemplate from './ChartTemplate';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

const EncryptedData = () => {
  const [password, setPassword] = useState('');
  const [salt, setSalt] = useState('');
  const [isSaltRetrieved, setIsSaltRetrieved] = useState(false);
  const [decryptedItems, setDecryptedItems] = useState([]);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;
  const [filteredItemsAllPages, setFilteredItemsAllPages] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [chartType, setChartType] = useState('line');
  const [isChartVisible, setIsChartVisible] = useState(false);

  const toast = useToast();
  const token = sessionStorage.getItem('jwtToken');

  useEffect(() => {
    setFilteredItemsAllPages(filterItems());

  }, [decryptedItems, startDate, endDate, currentPage]);

  useEffect(() => {
    // Check if 'salt' exists in sessionStorage when component mounts
    const storedSalt = sessionStorage.getItem('salt');
    if (storedSalt) {
      setSalt(storedSalt);
      setIsSaltRetrieved(true);
    }
  }, []);

  useEffect(() => {
    return () => {
      sessionStorage.removeItem('salt');
      console.log('Salt deleted from sessionStorage');
    };
  }, []);

  const deriveKey = (password, salt) => {
    return CryptoJS.PBKDF2(password, CryptoJS.enc.Hex.parse(salt), {
      keySize: 256 / 32,
      iterations: 1000,
    });
  };

  const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (file && file.type === 'text/csv') {
      const reader = new FileReader();
      reader.onload = async (e) => {
        const csvData = e.target.result;
        encryptAndUpload(csvData);
      };
      reader.readAsText(file);
    } else {
      toast({
        title: 'Error',
        description: 'Please select a .csv file',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const encryptAndUpload = async (dataToEncrypt) => {
    try {
      const key = deriveKey(password, salt);
      const iv = CryptoJS.lib.WordArray.random(128 / 8);
      const encrypted = CryptoJS.AES.encrypt(dataToEncrypt, key, { iv: iv });

      await axios.post(
        `${API_BASE_URL}/upload-encrypted-data`,
        {
          encryptedData: encrypted.toString(),
          iv: iv.toString(),
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      toast({
        title: 'Success',
        description: 'Encrypted Data uploaded successfully',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
    } catch (error) {
      toast({
        title: 'Error uploading encrypted data',
        description: error.message,
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const getSalt = async () => {
    try {
      const response = await axios.post(
        `${API_BASE_URL}/verify-password-retrieve-salt`,
        { password },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        }
      );

      setSalt(response.data.salt);
      sessionStorage.setItem('salt', response.data.salt);
      setIsSaltRetrieved(true);
      toast({
        title: 'Salt retrieved successfully',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
    } catch (error) {
      toast({
        title: 'Error retrieving salt',
        description: error.message,
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const decryptData = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/download-encrypted-data`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      const key = deriveKey(password, salt);
      let decryptedData = response.data.map((item) => {
        const decryptedText = CryptoJS.AES.decrypt(item.encryptedData, key, { iv: CryptoJS.enc.Hex.parse(item.iv) }).toString(CryptoJS.enc.Utf8);
        
        const decryptedLines = decryptedText.split('\n').filter(line => line.trim() !== '');
        
        const headers = decryptedLines[0].split(',');
        const items = decryptedLines.slice(1).map(entry => entry.split(','));

        return items.map(entry => ({
          feature1: parseFloat(entry[0]),  // Convert to float
          feature2: parseFloat(entry[1]),  // Convert to float
          feature3: parseFloat(entry[2]),  // Convert to float
          feature4: parseFloat(entry[3]),  // Convert to float
          date: entry[4], 
        }));
      }).flat();

      // Filter out NaN values or handle them appropriately if needed
      decryptedData = decryptedData.filter(item => !isNaN(item.feature1) && !isNaN(item.feature2) && !isNaN(item.feature3) && !isNaN(item.feature4));

      decryptedData.sort((a, b) => new Date(a.date) - new Date(b.date));

      if (decryptedData.length === 0) {
        toast({
          title: 'No data found',
          status: 'warning',
          duration: 5000,
          isClosable: true,
        });
        return;
      }

      setDecryptedItems(decryptedData);
      setFilteredItemsAllPages(filterItems()); // Update filtered items

      toast({
        title: 'Success',
        description: 'Encrypted Data decrypted successfully',
        status: 'success',
        duration: 5000,
        isClosable: true,
      });
    } catch (error) {
      toast({
        title: 'Error decrypting data',
        description: error.message,
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const filterItems = () => {
    let filtered = decryptedItems.filter(item => {
      const itemDate = new Date(item.date);
      if (startDate && endDate) {
        return itemDate >= new Date(startDate) && itemDate <= new Date(endDate);
      } else if (startDate) {
        return itemDate >= new Date(startDate);
      } else if (endDate) {
        return itemDate <= new Date(endDate);
      } else {
        return true;
      }
    });

    return filtered;
  };

  const pageCount = Math.ceil(filteredItemsAllPages.length / itemsPerPage);

  const handlePageChange = (action) => {
    if (action === 'prev' && currentPage > 1) {
      setCurrentPage(currentPage - 1);
    } else if (action === 'next' && currentPage < pageCount) {
      setCurrentPage(currentPage + 1);
    }
  };

  const handleLogFilteredItems = () => {
    console.log(filteredItemsAllPages);
  };

  const openModal = () => {
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  const handleGenerateChart = () => {
    console.log(filteredItemsAllPages);
    setIsChartVisible(true);
    closeModal();
  };

  const handleClearChart = () => {
    setIsChartVisible(false);
  };

  return (
    <Container maxW="100%" p={4}>
      <VStack spacing={4} align="stretch">
        <Heading as="h2" size="xl">
          Encrypt and Upload Data
        </Heading>
        {/* Input for password */}
        <Input
          type="password"
          placeholder="Enter Password"
          value={password}
          onChange={(e) => {
            setPassword(e.target.value);
            setIsSaltRetrieved(false);
          }}
        />
        {/* Button to retrieve salt */}
        <Button
          colorScheme="teal"
          onClick={getSalt}
          isDisabled={!password}
        >
          Retrieve Salt
        </Button>
        {/* Input for file upload */}
        <input
          type="file"
          accept=".csv"
          onChange={handleFileUpload}
          style={{ display: 'none' }}
          id="file-upload"
          disabled={!isSaltRetrieved}
        />
        <label htmlFor="file-upload">
          <Button as="span" colorScheme="blue" disabled={!isSaltRetrieved}>
            Upload .csv File
          </Button>
        </label>

        {/* Heading for Download and Decrypt section */}
        <Heading as="h2" size="xl">
          Download and Decrypt Data
        </Heading>
        {/* Button to decrypt data */}
        <Button
          colorScheme="green"
          onClick={decryptData}
          isDisabled={!isSaltRetrieved}
        >
          Download and Decrypt Encrypted Data
        </Button>

        {/* Render only if decrypted items exist */}
        {decryptedItems.length > 0 && (
          <Box overflowX="auto">
            {/* Date range inputs */}
            <HStack mt={4} spacing={4}>
              <Input
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
              />
              <Input
                type="date"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
              />
            </HStack>

            {/* Table for displaying data */}
            <Table mt={4} variant="simple">
              <Thead>
                <Tr>
                  <Th>Feature 1</Th>
                  <Th>Feature 2</Th>
                  <Th>Feature 3</Th>
                  <Th>Feature 4</Th>
                  <Th>Date</Th>
                </Tr>
              </Thead>
              <Tbody>
                {filteredItemsAllPages
                  .slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage)
                  .map((item, index) => (
                    <Tr key={index}>
                      <Td>{item.feature1}</Td>
                      <Td>{item.feature2}</Td>
                      <Td>{item.feature3}</Td>
                      <Td>{item.feature4}</Td>
                      <Td>{item.date}</Td>
                    </Tr>
                  ))}
              </Tbody>
            </Table>

            {/* Pagination and logging buttons */}
            <HStack mt={4} spacing={4}>
              <Button
                onClick={() => handlePageChange('prev')}
                isDisabled={currentPage === 1}
              >
                Previous
              </Button>
              <Button
                onClick={() => handlePageChange('next')}
                isDisabled={currentPage === pageCount}
              >
                Next
              </Button>
            </HStack>

            {/* Button to generate chart */}
            <Button colorScheme="blue" onClick={openModal}>
              Generate Chart
            </Button>
            {/* Button to clear chart, shown conditionally */}
            {isChartVisible && (
              <Button colorScheme="red" onClick={handleClearChart}>
                Clear Chart
              </Button>
            )}
          </Box>
        )}

        {/* Modal for selecting chart type */}
        <Modal isOpen={isModalOpen} onClose={closeModal}>
          <ModalOverlay />
          <ModalContent>
            <ModalHeader>Select Chart Type</ModalHeader>
            <ModalCloseButton />
            <ModalBody>
              <RadioGroup onChange={setChartType} value={chartType}>
                <Stack direction="column">
                  <Radio value="line">Line Chart</Radio>
                  <Radio value="bar">Bar Chart</Radio>
                  <Radio value="scatter">Scatter Chart</Radio>
                </Stack>
              </RadioGroup>
            </ModalBody>
            <ModalFooter>
              <Button colorScheme="blue" mr={3} onClick={handleGenerateChart}>
                Generate Chart
              </Button>
              <Button variant="ghost" onClick={closeModal}>
                Cancel
              </Button>
            </ModalFooter>
          </ModalContent>
        </Modal>

        {/* Render ChartTemplate if chart is visible */}
        {isChartVisible && (
          <ChartTemplate data={filteredItemsAllPages} chartType={chartType} />
        )}
      </VStack>
    </Container>
  );
};

export default EncryptedData;
