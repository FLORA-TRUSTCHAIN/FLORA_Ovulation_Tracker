import React, { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Flex,
  Stack,
  Text,
  useColorMode,
  useColorModeValue,
  IconButton,
  Drawer,
  DrawerBody,
  DrawerHeader,
  DrawerOverlay,
  DrawerContent,
  DrawerCloseButton,
  useDisclosure,
} from '@chakra-ui/react';
import { HamburgerIcon } from '@chakra-ui/icons';
import { logout_api } from '../api/auth/logout_api';
import { refreshJWTtoken } from '../api/auth/refresh_token_api';
import { useNavigate } from 'react-router-dom';
import WebSocketComponent from '../Components/DashboardComponents/WebSocketComponents';
import Charts from '../Components/DashboardComponents/Charts';
import MyProfile from '../Components/DashboardComponents/MyProfile';
import MyUpload from '../Components/DashboardComponents/MyUpload';
import MyCharts from '../Components/DashboardComponents/MyCharts';
import MakePrediction from '../Components/DashboardComponents/MakePrediction';
import EncryptedData from '../Components/DashboardComponents/EncryptedData';
import { jwtDecode } from 'jwt-decode';

const DashboardPage = () => {
  const [content, setContent] = useState(<WebSocketComponent />);
  const [timer, setTimer] = useState(30 * 60);
  const navigate = useNavigate();
  const { colorMode, toggleColorMode } = useColorMode();
  const { isOpen, onOpen, onClose } = useDisclosure();

  useEffect(() => {
    // IndexedDB setup
    const token = sessionStorage.getItem("jwtToken");
      const decodedToken = jwtDecode(token);
      const current_username = decodedToken.sub;
    const dbName = `IndexedDB_${current_username}`;
    const request = indexedDB.open(dbName, 1);
    
    request.onupgradeneeded = (event) => {
      const db = event.target.result;
      db.createObjectStore('downloaded_checkpoint', { keyPath: 'filename' });
      db.createObjectStore('local_onnx_files', { keyPath: 'filename'});
      db.createObjectStore('local_fl_rounds_logs', { keyPath: 'id', autoIncrement: true, });
      db.createObjectStore('user_data_observations', { keyPath: 'id', autoIncrement: true, });

      // Define indexes if necessary
      // onnxFilesStore.createIndex('name', 'name', { unique: false });
      // userDataObservationsStore.createIndex('userId', 'userId', { unique: false });
    };

    request.onsuccess = (event) => {
      console.log('Database opened successfully:', event.target.result);
    };

    request.onerror = (event) => {
      console.error('Database error:', event.target.error);
    };

    // Timer logic and other effects
    const interval = setInterval(() => {
      setTimer((prevTimer) => {
        if (prevTimer > 0) {
          return prevTimer - 1;
        } else {
          clearInterval(interval);
          sessionStorage.removeItem('jwtToken');
          navigate('/login');
          return 0;
        }
      });
    }, 1000);

    const handleTabClose = async () => {
      await logout_api();
      sessionStorage.removeItem('jwtToken');
      console.log('Logout successful');
    };

    // window.addEventListener('beforeunload', handleTabClose);

    // return () => {
    //   clearInterval(interval);
    //   window.removeEventListener('beforeunload', handleTabClose);
    // };
  }, [navigate]);

  const handleLogout = async () => {
    try {
      await logout_api();
      sessionStorage.removeItem('jwtToken');
      console.log('Logout successful');
      navigate('/');
    } catch (err) {
      console.error('Logout error:', err);
    }
  };

  const handleContentChange = (newContent) => {
    setContent(newContent);
    onClose();
  };

  const handleRefreshTimer = async () => {
    try {
      await refreshJWTtoken();
      console.log('Token refresh ok');
      setTimer(30 * 60);
    } catch (err) {
      console.error('Error Refreshing timer:', err);
    }
  };

  const formatTime = (time) => {
    const minutes = Math.floor(time / 60);
    const seconds = time % 60;
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  };

  return (
    <Box minH="100vh">
      <Flex
        as="header"
        bg={useColorModeValue('white', 'gray.900')}
        color={useColorModeValue('gray.800', 'white')}
        minH="60px"
        py={{ base: 2 }}
        px={{ base: 4 }}
        borderBottom={1}
        borderStyle="solid"
        borderColor={useColorModeValue('gray.200', 'gray.700')}
        align="center"
        justify="space-between"
      >
        <IconButton
          icon={<HamburgerIcon />}
          aria-label="Open Menu"
          variant="ghost"
          onClick={onOpen}
        />
        <Text fontSize={{ base: 'lg', md: 'xl' }} fontWeight="bold">
          Dashboard
        </Text>
        <Flex align="center">
          <Text mr={{ base: 2, md: 4 }}>{formatTime(timer)}</Text>
          <Button onClick={handleRefreshTimer} size={{ base: 'sm', md: 'md' }}>
            Refresh Timer
          </Button>
        </Flex>
      </Flex>

      <Drawer isOpen={isOpen} placement="left" onClose={onClose}>
        <DrawerOverlay />
        <DrawerContent>
          <DrawerCloseButton />
          <DrawerHeader>Menu</DrawerHeader>
          <DrawerBody>
            <Stack spacing={4}>
              <Button variant="ghost" onClick={() => handleContentChange(<MyUpload />)}>
                myUpload
              </Button>
              <Button variant="ghost" onClick={() => handleContentChange(<WebSocketComponent />)}>
                Websocket Test (Under Construction)
              </Button>
              <Button variant="ghost" onClick={() => handleContentChange(<Charts />)}>
                Public Charts
              </Button>
              <Button variant="ghost" onClick={() => handleContentChange(<MyCharts />)}>
                myCharts
              </Button>
              <Button variant="ghost" onClick={() => handleContentChange(<MyProfile />)}>
                myProfile
              </Button>
              <Button variant="ghost" onClick={() => handleContentChange(<MakePrediction />)}>
                Make Prediction
              </Button>
              <Button variant="ghost" onClick={() => handleContentChange(<EncryptedData />)}>
                Encrypted Data
              </Button>
              <Button onClick={toggleColorMode} size={{ base: 'sm', md: 'md' }}>
                {colorMode === 'light' ? 'Dark' : 'Light'} Mode
              </Button>
              <Button
                onClick={handleLogout}
                bg="red.400"
                color="white"
                size={{ base: 'sm', md: 'md' }}
                _hover={{
                  bg: 'red.500',
                }}
              >
                Logout
              </Button>
            </Stack>
          </DrawerBody>
        </DrawerContent>
      </Drawer>

      <Flex
        flex={1}
        bg={useColorModeValue('gray.50', 'gray.800')}
        p={{ base: 4, md: 6 }}
        minH="calc(100vh - 60px)"
      >
        <Box w="full" maxW="5xl">
          {content}
        </Box>
      </Flex>
    </Box>
  );
};

export default DashboardPage;
