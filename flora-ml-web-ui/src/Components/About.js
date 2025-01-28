import React from 'react';
import { Box, Heading, Text, List, ListItem, ListIcon, Link } from '@chakra-ui/react';
import { CheckCircleIcon } from '@chakra-ui/icons';

const About = () => {
  return (
    <Box p={6} maxW="800px" mx="auto">
      <Heading as="h1" mb={4}>About Our FastAPI Service</Heading>

      <Text fontSize="lg" mb={4}>
        Welcome to FLx API! We are a dedicated team of developers focused on delivering high-performance, scalable, and secure API solutions using FastAPI. Our mission is to empower businesses and developers with reliable and efficient backend services that drive innovation and growth.
      </Text>

      <Heading as="h2" size="md" mt={6} mb={2}>What is FastAPI?</Heading>
      <Text fontSize="lg" mb={4}>
        FastAPI is a modern, fast (high-performance), web framework for building APIs with Python 3.7+ based on standard Python type hints. It is designed to be easy to use and to provide the best possible developer experience, ensuring you can build robust and performant applications quickly and efficiently.
      </Text>

      <Heading as="h2" size="md" mt={6} mb={2}>Our Service</Heading>
      <Text fontSize="lg" mb={4}>
        At FLx API, we leverage the power of FastAPI to create RESTful APIs that are:
      </Text>
      <List spacing={3} mb={4}>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          <Text as="span" fontWeight="bold">Fast:</Text> Built on Starlette for the web parts and Pydantic for the data parts, our APIs are optimized for speed and performance.
        </ListItem>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          <Text as="span" fontWeight="bold">Reliable:</Text> We follow best practices and rigorous testing to ensure our APIs are reliable and robust.
        </ListItem>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          <Text as="span" fontWeight="bold">Secure:</Text> Security is a top priority. Our APIs include features such as OAuth2.0, JWT token authentication, and more to protect your data.
        </ListItem>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          <Text as="span" fontWeight="bold">Scalable:</Text> Designed to grow with your business, our APIs can handle increased load and scale efficiently.
        </ListItem>
      </List>

      <Heading as="h2" size="md" mt={6} mb={2}>Key Features</Heading>
      <List spacing={3} mb={4}>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          Auto-generated Documentation: FastAPI automatically generates interactive API documentation (Swagger UI and ReDoc) to make it easier for developers to explore and integrate with our APIs.
        </ListItem>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          Type Safety and Validation: With Pydantic, our APIs ensure type safety and data validation, reducing errors and improving data integrity.
        </ListItem>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          Asynchronous Support: Our APIs support asynchronous programming, allowing for better performance and scalability.
        </ListItem>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          Extensive Ecosystem: FastAPI integrates seamlessly with other Python libraries and frameworks, providing a versatile and comprehensive ecosystem for development.
        </ListItem>
      </List>

      <Heading as="h2" size="md" mt={6} mb={2}>Use Cases</Heading>
      <Text fontSize="lg" mb={4}>
        Our FastAPI service is perfect for:
      </Text>
      <List spacing={3} mb={4}>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          Web Applications: Providing backend support for modern web applications.
        </ListItem>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          Mobile Applications: Serving as a robust backend for mobile apps.
        </ListItem>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          IoT Devices: Handling data and commands for Internet of Things devices.
        </ListItem>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          Microservices: Forming part of a microservices architecture for larger systems.
        </ListItem>
      </List>

      <Heading as="h2" size="md" mt={6} mb={2}>Why Choose Us?</Heading>
      <List spacing={3} mb={4}>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          Expertise: Our team has extensive experience in API development and a deep understanding of FastAPI.
        </ListItem>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          Support: We offer comprehensive support and maintenance services to ensure your APIs run smoothly.
        </ListItem>
        <ListItem>
          <ListIcon as={CheckCircleIcon} color="green.500" />
          Customization: We provide tailored solutions to meet the unique needs of your business.
        </ListItem>
      </List>

      <Heading as="h2" size="md" mt={6} mb={2}>Get in Touch</Heading>
      <Text fontSize="lg" mb={4}>
        Ready to take your API development to the next level? Contact us today to learn more about how our FastAPI services can benefit your business.
      </Text>
      <Link color="blue.500" href="mailto:kapoio@email.com">kapoio@email.com</Link>
    </Box>
  );
};

export default About;
