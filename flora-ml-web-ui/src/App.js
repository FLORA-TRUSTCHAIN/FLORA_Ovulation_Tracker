import * as React from 'react';
import { ChakraProvider } from '@chakra-ui/react'

import MainApplication from './MainApplication';

function App() {
  return (
    <ChakraProvider>
    <MainApplication />
  </ChakraProvider>
  );
}

export default App;
