import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
  Box,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  Input,
  VStack,
  useToast,
  Spinner,
  Button,
  HStack,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalCloseButton,
  ModalBody,
  ModalFooter,
  RadioGroup,
  Radio,
  Stack
} from '@chakra-ui/react';

import ChartTemplate from './ChartTemplate';
import InitDB from '../../api/indexedDB/InitDB';
import {jwtDecode} from 'jwt-decode';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;
const ITEMS_PER_PAGE = 10;

const MyCharts = () => {
  const [data, setData] = useState([]);
  const [filteredData, setFilteredData] = useState([]);
  const [filterStartDate, setFilterStartDate] = useState('');
  const [filterEndDate, setFilterEndDate] = useState('');
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedChart, setSelectedChart] = useState('line');
  const [isChartVisible, setIsChartVisible] = useState(false);

  const toast = useToast();

  const chartOptions = [
    { value: 'line', label: 'Line Chart' },
    { value: 'bar', label: 'Bar Chart' },
    { value: 'scatter', label: 'Scatter Chart' },
  ];

  const fetchDataFromServer = async () => {
    setLoading(true);
    try {
      const response = await axios.post(
        `${API_BASE_URL}/retrieve-data-per-user`,
        {},
        {
          headers: {
            Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`,
          },
        }
      );
      return response.data;
    } catch (error) {
      toast({
        title: 'Error fetching data',
        description: 'An error occurred while fetching data from the server. Please try again.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const fetchDataFromIndexedDB = async () => {
    const token = sessionStorage.getItem("jwtToken");
    const decodedToken = jwtDecode(token);
    const username = decodedToken.sub;
    const db = await InitDB(username, 'user_data_observations');
    const transaction = db.transaction('user_data_observations', 'readonly');
    const store = transaction.objectStore('user_data_observations');
    const allData = await store.getAll();
    
    const flatData = allData.flatMap(item => {
      const { data } = item;
      return data.date.map((date, index) => ({
        feature1: data.feature1[index],
        feature2: data.feature2[index],
        feature3: data.feature3[index],
        feature4: data.feature4[index],
        date: new Date(date).toISOString() // Normalize date format
      }));
    });

    return flatData;
  };

  const removeDuplicatesAndSort = (localData, serverData) => {
    // Normalize server data dates
    const normalizedServerData = serverData.map(item => ({
      ...item,
      date: new Date(item.date).toISOString() // Normalize date format
    }));

    const serverIdentifiers = new Set(
      normalizedServerData.map(item => `${item.feature1}-${item.feature2}-${item.feature3}-${item.feature4}-${item.date}`)
    );

    const uniqueLocalData = localData.filter(item => 
      !serverIdentifiers.has(`${item.feature1}-${item.feature2}-${item.feature3}-${item.feature4}-${item.date}`)
    );
    
    const combinedData = [...uniqueLocalData, ...normalizedServerData];

    const uniqueData = Array.from(
      new Map(combinedData.map(item => [`${item.feature1}-${item.feature2}-${item.feature3}-${item.feature4}-${item.date}`, item])).values()
    );

    uniqueData.sort((a, b) => new Date(a.date) - new Date(b.date));
    return uniqueData;
  };

  const handleFetchLocalData = async () => {
    setLoading(true);
    const localData = await fetchDataFromIndexedDB();
    const processedData = removeDuplicatesAndSort(localData, []);
    setData(processedData);
    setFilteredData(processedData);
    setLoading(false);
  };

  const handleFetchDataFromServer = async () => {
    const serverData = await fetchDataFromServer();
    const processedData = removeDuplicatesAndSort([], serverData);
    setData(processedData);
    setFilteredData(processedData);
  };

  const handleFetchBoth = async () => {
    setLoading(true);
    const [localData, serverData] = await Promise.all([
      fetchDataFromIndexedDB(),
      fetchDataFromServer()
    ]);
    
    const processedData = removeDuplicatesAndSort(localData, serverData);
    setData(processedData);
    setFilteredData(processedData);
    setLoading(false);
  };

  useEffect(() => {
    filterDataByDateRange();
  }, [filterStartDate, filterEndDate, data]);

  const filterDataByDateRange = () => {
    let filtered = data;
    if (filterStartDate && filterEndDate) {
      filtered = data.filter(item => {
        const itemDate = new Date(item.date);
        return itemDate >= new Date(filterStartDate) && itemDate <= new Date(filterEndDate);
      });
    }
    setFilteredData(filtered);
    setCurrentPage(1);
  };

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const indexOfLastItem = currentPage * ITEMS_PER_PAGE;
  const indexOfFirstItem = indexOfLastItem - ITEMS_PER_PAGE;
  const currentItems = filteredData.slice(indexOfFirstItem, indexOfLastItem);

  const handleGenerate = () => {
    setIsModalOpen(true);
  };

  const handleModalClose = () => {
    setIsModalOpen(false);
  };

  const handleChartSelect = () => {
    setIsChartVisible(true);
    handleModalClose();
  };

  const clearChart = () => {
    setIsChartVisible(false);
  };

  if (loading) {
    return <Spinner size="xl" />;
  }

  return (
    <Box p={4}>
      <VStack spacing={4} align="flex-start">
        <HStack spacing={4}>
          <Button onClick={handleFetchLocalData}>Fetch Local Data</Button>
          <Button onClick={handleFetchDataFromServer}>Fetch Data From Server</Button>
          <Button onClick={handleFetchBoth}>Fetch Both</Button>
        </HStack>
        <HStack spacing={4}>
          <Input
            type="date"
            placeholder="Start Date"
            value={filterStartDate}
            onChange={(e) => setFilterStartDate(e.target.value)}
          />
          <Input
            type="date"
            placeholder="End Date"
            value={filterEndDate}
            onChange={(e) => setFilterEndDate(e.target.value)}
          />
          <Button onClick={() => { setFilterStartDate(''); setFilterEndDate(''); }}>Clear</Button>
        </HStack>
        <Table variant="simple">
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
            {currentItems.map((item, index) => (
              <Tr key={index}>
                <Td>{item.feature1}</Td>
                <Td>{item.feature2}</Td>
                <Td>{item.feature3}</Td>
                <Td>{item.feature4}</Td>
                <Td>{new Date(item.date).toISOString().replace('T', ' ').replace(/\.\d{3}Z$/, '')}</Td>
              </Tr>
            ))}
          </Tbody>
        </Table>
        <HStack spacing={2} mt={4}>
          <Button
            onClick={() => handlePageChange(currentPage - 1)}
            isDisabled={currentPage === 1}
          >
            Previous
          </Button>
          <Button
            onClick={() => handlePageChange(currentPage + 1)}
            isDisabled={indexOfLastItem >= filteredData.length}
          >
            Next
          </Button>
          <Button onClick={handleGenerate}>Generate</Button>
          {isChartVisible && (
            <Button onClick={clearChart}>Clear</Button>
          )}
        </HStack>
        {isChartVisible && (
          <ChartTemplate data={filteredData} chartType={selectedChart} />
        )}
      </VStack>

      <Modal isOpen={isModalOpen} onClose={handleModalClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Select a Chart</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <RadioGroup onChange={setSelectedChart} value={selectedChart}>
              <Stack direction="column">
                {chartOptions.map((chart) => (
                  <Radio key={chart.value} value={chart.value}>
                    {chart.label}
                  </Radio>
                ))}
              </Stack>
            </RadioGroup>
          </ModalBody>
          <ModalFooter>
            <Button colorScheme="blue" mr={3} onClick={handleChartSelect}>
              Select
            </Button>
            <Button variant="ghost" onClick={handleModalClose}>Cancel</Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </Box>
  );
};

export default MyCharts;
