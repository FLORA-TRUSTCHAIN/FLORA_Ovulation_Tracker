import React from 'react';
import axios from 'axios';
import { MyData } from './data';


// Base URL from environment variables
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

async function sendFloats(floatArray, curr_round) {
    

    const convertedarray = Array.from(floatArray);
    const dataToSend = {
        params: convertedarray,
        numSamples : MyData.NUM_TRAIN_SAMPLES,
        current_round: String(curr_round)
    };

    
    const jsonString = JSON.stringify(dataToSend);

    
    const blob = new Blob([jsonString], { type: 'application/json' });

    
    const formData = new FormData();
    formData.append('file', blob, 'float-array.json');

    try {
        // Send POST request with FormData
        await axios.post(`${API_BASE_URL}/upload-json-floats`, formData, {
            headers: {
                Authorization: `Bearer ${sessionStorage.getItem('jwtToken')}`,
                'Content-Type': 'multipart/form-data'
            },
        });

        console.log('File sent successfully');
    } catch (error) {
        console.error('Error sending file:', error);
    }
}

export default sendFloats;