import React from 'react';
import {
  Box,
  Container,
  Stack,
  SimpleGrid,
  Text,
  useColorModeValue,
  Link,
} from '@chakra-ui/react';

import PlayStoreBadge from '../Assets/img/playstore-badge.png';
import AppStoreBadge from '../Assets/img/appstore-badge.png';

const ListHeader = ({ children }) => {
  return (
    <Text fontWeight={'500'} fontSize={'lg'} mb={2}>
      {children}
    </Text>
  );
};

export default function Footer() {
  const badgeStyle = {
    width: '150px', // Adjust width as needed
    height: 'auto', // Maintain aspect ratio
  };

  return (
    <Box
      bg={useColorModeValue('gray.50', 'gray.900')}
      color={useColorModeValue('gray.700', 'gray.200')}>
      <Container as={Stack} maxW={'6xl'} py={10}>
        <SimpleGrid columns={{ base: 1, sm: 2, md: 4 }} spacing={8}>
          <Stack align={'flex-start'}>
            <ListHeader>Company</ListHeader>
            <Link href={'/about'}>
              About Us
            </Link>
            <Link href={'/news'}>
              News
            </Link>
            <Link href={'/faq'}>
              FAQ
            </Link>
            <Link href={'/contact'}>
              Contact Us
            </Link>
          </Stack>

          <Stack align={'flex-start'}>
            <ListHeader>Legal</ListHeader>
            <Link href={'#'}>
              Cookies Policy
            </Link>
            <Link href={'#'}>
              Privacy Policy
            </Link>
            <Link href={'#'}>
              Terms of Service
            </Link>
            <Link href={'#'}>
              Law Enforcement
            </Link>
          </Stack>

          <Stack align={'flex-start'}>
            <ListHeader>Install App</ListHeader>
            <img src={PlayStoreBadge} alt="Play Store Badge" style={badgeStyle} />
            <img src={AppStoreBadge} alt="App Store Badge" style={badgeStyle} />
          </Stack>
        </SimpleGrid>
      </Container>
    </Box>
  );
}
