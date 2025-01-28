'use client';

import React from 'react';
import { Box, SimpleGrid, Icon, Text, Stack, Flex } from '@chakra-ui/react';
import { SiFastapi } from "react-icons/si";
import { BsLightning } from "react-icons/bs";
import { GoClock } from "react-icons/go";

const Feature = ({ title, text, icon }) => {
  return (
    <Stack align="center" textAlign="center">
      <Flex
        w={16}
        h={16}
        align={'center'}
        justify={'center'}
        color={'white'}
        rounded={'full'}
        bg={'#1a202d'}
        mb={1}>
        {icon}
      </Flex>
      <Text fontWeight={600}>{title}</Text>
      <Text color={'gray.600'}>{text}</Text>
    </Stack>
  );
};

const Features = () => {
  return (
    
    <Box p={4}>
      <SimpleGrid columns={{ base: 1, md: 3 }} spacing={10}>
        <Feature
          icon={<Icon as={SiFastapi} w={10} h={10} />}
          title={'FastAPI'}
          text={
            "Powered by FastAPI, we harness Python's efficiency to build high-performance APIs effortlessly"
          }
        />
        <Feature
          icon={<Icon as={BsLightning} w={10} h={10} />}
          title={'Lightning Fast'}
          text={
            'Experience the speed of our lightning-fast API, delivering rapid responses and seamless performance'
          }
        />
        <Feature
          icon={<Icon as={GoClock} w={10} h={10} />}
          title={'24/7 Uptime'}
          text={
            'Enjoy uninterrupted service with our API, ensuring reliable performance around the clock, 24/7'
          }
        />
      </SimpleGrid>
    </Box>
    
    
  );
};

export default Features;
