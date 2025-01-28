'use client';

import React from 'react';
import { Stack, Container, Box, Flex, Text, Heading, SimpleGrid } from '@chakra-ui/react';

const Stats = () => {
  return (
    <Box bg={'gray.800'} position={'relative'}>
      <Flex
        flex={1}
        zIndex={0}
        display={{ base: 'none', lg: 'flex' }}
        backgroundImage="url('/templates/stats-grid-with-image.png')"
        backgroundSize={'cover'}
        backgroundPosition="center"
        backgroundRepeat="no-repeat"
        position={'absolute'}
        width={'50%'}
        insetY={0}
        right={0}>
        <Flex
          bgGradient={'linear(to-r, gray.800 10%, transparent)'}
          w={'full'}
          h={'full'}
        />
      </Flex>
      <Container maxW={'7xl'} zIndex={10} position={'relative'}>
        <Stack direction={{ base: 'column', lg: 'row' }}>
          <Stack
            flex={1}
            color={'gray.400'}
            justify={{ lg: 'center' }}
            py={{ base: 4, md: 20, xl: 60 }}>
            <Box mb={{ base: 8, md: 20 }}>
              <Text
                fontFamily={'heading'}
                fontWeight={700}
                textTransform={'uppercase'}
                mb={3}
                fontSize={'xl'}
                color={'gray.500'}>
                Unlock the Power of Predictions with Our ML API
              </Text>
              <Heading color={'white'} mb={5} fontSize={{ base: '3xl', md: '5xl' }}>
              Enhance Decision-Making
              </Heading>
              <Text fontSize={'xl'} color={'gray.400'}>
              Empower your applications with cutting-edge predictive analytics using our ML Prediction API. Here's why our API stands out:
              </Text>
            </Box>

            <SimpleGrid columns={{ base: 1, md: 2 }} spacing={10}>
              {stats.map((stat) => (
                <Box key={stat.title}>
                  <Text fontFamily={'heading'} fontSize={'3xl'} color={'white'} mb={3}>
                    {stat.title}
                  </Text>
                  <Text fontSize={'xl'} color={'gray.400'}>
                    {stat.content}
                  </Text>
                </Box>
              ))}
            </SimpleGrid>
          </Stack>
          <Flex flex={1} />
        </Stack>
      </Container>
    </Box>
  );
};

const StatsText = ({ children }) => (
  <Text as={'span'} fontWeight={700} color={'white'}>
    {children}
  </Text>
);

const stats = [
  {
    title: 'High Accuracy',
    content: (
      <>
        <StatsText>Leverage </StatsText>  advanced machine learning models that deliver precise predictions tailored to your needs
      </>
    ),
  },
  {
    title: 'Scalability',
    content: (
      <>
        <StatsText>Handle large volumes</StatsText> of data seamlessly with our scalable infrastructure, ensuring consistent performance under any workload
      </>
    ),
  },
  {
    title: 'Ease of Integration',
    content: (
      <>
        <StatsText>Integrate</StatsText> effortlessly into your existing systems with clear documentation and robust support
      </>
    ),
  },
  {
    title: 'Real-Time Insights',
    content: (
      <>
         Gain actionable insights <StatsText>instantly</StatsText> , enabling proactive decision-making and enhancing user engagement.
      </>
    ),
  },
];

export default Stats;
