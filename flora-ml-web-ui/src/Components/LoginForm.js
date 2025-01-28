'use client';

import {
  Flex,
  Box,
  FormControl,
  FormLabel,
  Input,
  Stack,
  Button,
  Heading,
  Text,
  useColorModeValue,
  Link as ChakraLink,
} from '@chakra-ui/react';

import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { login_api } from '../api/auth/login_api';

export default function LoginForm() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
        await login_api({ username, password });
           
      // Handle successful login here (e.g., save token, redirect)
      console.log('Login successful:');
      navigate('/dashboard');
    } catch (err) {
      // Handle error (e.g., show error message)
      setError('Login failed. Please check your credentials.');
      console.error('Login error:', err);
    }
  };

  return (
    <Flex
      minH={'100vh'}
      align={'center'}
      justify={'center'}
      bg={useColorModeValue('gray.50', 'gray.800')}
    >
      <Stack spacing={8} mx={'auto'} maxW={'lg'} py={12} px={6}>
        <Stack align={'center'}>
          <Heading fontSize={'4xl'}>Sign in to your account</Heading>
          <Box fontSize={'lg'} color={'gray.600'}>
            to enjoy all of our cool <span style={{ color: 'blue.400' }}>features</span> ✌️
          </Box>
        </Stack>
        <Box
          rounded={'lg'}
          bg={useColorModeValue('white', 'gray.700')}
          boxShadow={'lg'}
          p={8}
        >
          <Stack spacing={4} as="form" onSubmit={handleSubmit}>
            <FormControl id="username">
              <FormLabel>Username</FormLabel>
              <Input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
            </FormControl>
            <FormControl id="password">
              <FormLabel>Password</FormLabel>
              <Input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </FormControl>
            <Stack spacing={10}>
              <Stack
                direction={{ base: 'column', sm: 'row' }}
                align={'start'}
                justify={'space-between'}
              >
                <Text color={'blue.400'}>Forgot password?</Text>
              </Stack>
              <Button
                type="submit"
                bg={'blue.400'}
                color={'white'}
                _hover={{
                  bg: 'blue.500',
                }}
              >
                Sign in
              </Button>
            </Stack>
            {error && (
              <Text color="red.500" mt={4}>
                {error}
              </Text>
            )}
          </Stack>
        </Box>
        <Stack pt={6}>
          <Text align={'center'}>
            Don't have an account? <ChakraLink as={RouterLink} to="/register" color={'blue.400'}>Register</ChakraLink>
          </Text>
        </Stack>
      </Stack>
    </Flex>
  );
}
