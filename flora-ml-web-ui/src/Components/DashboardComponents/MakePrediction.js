import React, { useState } from 'react';
import {
    Box,
    Button,
    FormControl,
    FormLabel,
    Input,
    Select,
    useToast,
    Modal,
    ModalOverlay,
    ModalContent,
    ModalHeader,
    ModalFooter,
    ModalBody,
    ModalCloseButton,
    Text,
    Link
} from '@chakra-ui/react';
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

const MakePrediction = () => {
    const [model, setModel] = useState('LR');
    const [data, setData] = useState('');
    const [fileData, setFileData] = useState(null); // State to hold file data
    const [predictionResult, setPredictionResult] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isDragOver, setIsDragOver] = useState(false); // State to track drag over

    const toast = useToast();

    const handleModelChange = (e) => setModel(e.target.value);

    const handleDataChange = (e) => {
        setData(e.target.value);
        setFileData(null); // Clear file data if input changes
    };

    const handleFileUpload = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (event) => {
                setFileData(event.target.result);
                setData(''); // Clear text data if file is uploaded
            };
            reader.readAsText(file);
        }
    };

    const isValidArray = (input) => {
        try {
            const parsed = JSON.parse(input);
            if (!Array.isArray(parsed)) return false; // Must be an array

            // Check if it's a 2D array
            if (parsed.length > 0 && Array.isArray(parsed[0])) {
                // Check each item in the array of arrays
                for (let i = 0; i < parsed.length; i++) {
                    const row = parsed[i];
                    if (!Array.isArray(row)) return false; // Each item must be an array
                    for (let j = 0; j < row.length; j++) {
                        if (typeof row[j] !== 'number') return false; // Each element must be a number
                    }
                }
                return true; // Valid 2D array of numbers
            } else {
                // Check if it's a 1D array
                for (let i = 0; i < parsed.length; i++) {
                    if (typeof parsed[i] !== 'number') return false; // Each element must be a number
                }
                return true; // Valid 1D array of numbers
            }
        } catch {
            return false; // Error parsing JSON
        }
    };

    const handleSubmit = async () => {
        let inputData = data.trim(); // Use text data by default

        if (fileData) {
            // Use file data if available
            inputData = fileData.trim();
        }

        if (!isValidArray(inputData)) {
            toast({
                title: 'Invalid input.',
                description: 'Please enter a valid array of floats or upload a valid text file.',
                status: 'error',
                duration: 5000,
                isClosable: true,
            });
            return;
        }

        const token = sessionStorage.getItem('jwtToken');
        if (!token) {
            toast({
                title: 'Authentication required.',
                description: 'Please log in to get a token.',
                status: 'error',
                duration: 5000,
                isClosable: true,
            });
            return;
        }

        try {
            const response = await axios.post(
                `${API_BASE_URL}/make_prediction/${model}`,
                { features: JSON.parse(inputData) },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );

            setPredictionResult(response.data); // Set the prediction result
            setIsModalOpen(true); // Open the modal with the result
        } catch (error) {
            toast({
                title: 'Prediction failed.',
                description: error.message,
                status: 'error',
                duration: 5000,
                isClosable: true,
            });
        }
    };

    const handleModalClose = () => {
        setIsModalOpen(false);
        setPredictionResult(''); // Clear the prediction result when closing modal
    };

    const handleDownloadCSV = () => {
        if (!predictionResult || !Array.isArray(predictionResult) || predictionResult.length === 0) {
            toast({
                title: 'No data to download.',
                description: 'There is no prediction result to download.',
                status: 'warning',
                duration: 5000,
                isClosable: true,
            });
            return;
        }

        const csvData = convertToCSV(predictionResult);
        const blob = new Blob([csvData], { type: 'text/csv;charset=utf-8' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'prediction_result.csv');
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    const convertToCSV = (data) => {
        if (!data || !Array.isArray(data) || data.length === 0) return '';

        const header = Object.keys(data[0]).join(',');
        const rows = data.map(obj => Object.values(obj).join(',')).join('\n');
        return `${header}\n${rows}`;
    };

    const handleDragOver = (e) => {
        e.preventDefault();
        setIsDragOver(true);
    };

    const handleDragEnter = (e) => {
        e.preventDefault();
        setIsDragOver(true);
    };

    const handleDragLeave = (e) => {
        e.preventDefault();
        setIsDragOver(false);
    };

    const handleDrop = (e) => {
        e.preventDefault();
        setIsDragOver(false);

        const file = e.dataTransfer.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (event) => {
                setFileData(event.target.result);
                setData(''); // Clear text data if file is uploaded
            };
            reader.readAsText(file);
        }
    };

    return (
        <Box p={5} maxW="500px" mx="auto">
            <FormControl id="model" mb={4}>
                <FormLabel>Model</FormLabel>
                <Select value={model} onChange={handleModelChange}>
                    <option value="LR">LR</option>
                    <option value="MLP">MLP</option>
                    {/* Add more options here */}
                </Select>
            </FormControl>

            <FormControl id="data" mb={4}>
                <FormLabel>Data (array of floats)</FormLabel>
                <Input
                    value={data}
                    onChange={handleDataChange}
                    placeholder='e.g. [-2.765, -2.71, -1.092, 0.252] or [[-2.765, -2.71, -1.092, 0.252], [-0.438, 1.957, 0.352, 3.347]]'
                />
            </FormControl>

            <FormControl id="file-upload" mb={4}>
                <FormLabel>Upload File (.txt)</FormLabel>
                <Input type="file" onChange={handleFileUpload} accept=".txt" />
            </FormControl>
            <div
                id="file-drop-area"
                onDragOver={handleDragOver}
                onDragEnter={handleDragEnter}
                onDragLeave={handleDragLeave}
                onDrop={handleDrop}
                style={{ border: isDragOver ? '2px dashed #007bff' : '2px dashed #cccccc', padding: '20px', marginTop: '10px' }}
            >
                <FormLabel>or Drag and Drop a File (.txt)</FormLabel>
            </div>

            <Button onClick={handleSubmit} colorScheme="teal" isDisabled={!data && !fileData}>
                Submit
            </Button>

            {/* Modal for displaying prediction result */}
            <Modal isOpen={isModalOpen} onClose={handleModalClose}>
                <ModalOverlay />
                <ModalContent>
                    <ModalHeader>Prediction Result</ModalHeader>
                    <ModalCloseButton />
                    <ModalBody>
                        {predictionResult && (
                            <>
                                <Text mb={4}>Result: {JSON.stringify(predictionResult)}</Text>
                                <Button colorScheme="teal" onClick={handleDownloadCSV} mb={4}>
                                    Download as CSV
                                </Button>
                                <Text fontSize="sm">
                                    Note: CSV download feature is for demonstration purposes. Implement actual CSV conversion based on your backend response.
                                </Text>
                            </>
                        )}
                    </ModalBody>
                    <ModalFooter>
                        <Button colorScheme="blue" onClick={handleModalClose}>
                            Close
                        </Button>
                    </ModalFooter>
                </ModalContent>
            </Modal>
        </Box>
    );
};

export default MakePrediction;
