// logging.tsx
import React from 'react';

// Function to toggle the collapse state of the "More Info" section
export function toggleMoreInfoIsCollapsed(
    setMoreInfoIsCollapsed: React.Dispatch<React.SetStateAction<boolean>>,
    moreInfoIsCollapsed: boolean
) {
    setMoreInfoIsCollapsed(!moreInfoIsCollapsed);
}

// Function to show a status message
export function showStatusMessage(
    message: string,
    setStatusMessage: React.Dispatch<React.SetStateAction<string>>
) {
    console.log(message);
    setStatusMessage(message);
}

// Function to show an error message
export function showErrorMessage(
    message: string,
    setErrorMessage: React.Dispatch<React.SetStateAction<string>>
) {
    console.log(message);
    setErrorMessage(message);
}

// Function to add an array of messages to the existing messages
export function addMessages(
    messagesToAdd: string[],
    setMessages: React.Dispatch<React.SetStateAction<string[]>>
) {
    setMessages(messages => [...messages, ...messagesToAdd]);
}

// Function to add a single message to the message queue
export function addMessageToQueue(message: string, messagesQueue: string[]) {
    messagesQueue.push(message);
}

// Function to clear all output states
export function clearOutputs(
    setTrainingLosses: React.Dispatch<React.SetStateAction<number[]>>,
    setTestMSE: React.Dispatch<React.SetStateAction<number[]>>,
    setTestMAE: React.Dispatch<React.SetStateAction<number[]>>,
    setMessages: React.Dispatch<React.SetStateAction<string[]>>,
    setStatusMessage: React.Dispatch<React.SetStateAction<string>>,
    setErrorMessage: React.Dispatch<React.SetStateAction<string>>,
    messagesQueue: string[]
) {
    setTrainingLosses([]);
    setTestMSE([]);
    setTestMAE([]);
    setMessages([]);
    setStatusMessage("");
    setErrorMessage("");
    messagesQueue.length = 0;
}

// Function to log a message with a throttling mechanism
export async function logMessage(
    message: string,
    messagesQueue: string[],
    lastLogTime: number,
    logIntervalMs: number,
    waitAfterLoggingMs: number,
    setStatusMessage: React.Dispatch<React.SetStateAction<string>>,
    enableLiveLogging: boolean,
    setMessages: React.Dispatch<React.SetStateAction<string[]>>
) {
    addMessageToQueue(message, messagesQueue);
    if (Date.now() - lastLogTime > logIntervalMs) {
        showStatusMessage(message, setStatusMessage);
        if (enableLiveLogging) {
            addMessages(messagesQueue, setMessages);
            messagesQueue.length = 0;
        }
        // Wait for UI to update before updating lastLogTime, otherwise will have lags in updates
        await new Promise(r => setTimeout(r, waitAfterLoggingMs));
        lastLogTime = Date.now();
    }
}
