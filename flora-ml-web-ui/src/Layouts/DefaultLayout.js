import React from 'react'
import Navbar from '../Components/Navbar'
import Footer from '../Components/Footer'
import { Box, Flex } from '@chakra-ui/react';

const DefaultLayout = ({ children }) => {
  return (
    <>
        <Flex direction="column" minH="100vh">
    <Navbar />
    <Box flex="1">
    {children}
    </Box>
    <Footer />
    </Flex>
    </>
  )
}

export default DefaultLayout
