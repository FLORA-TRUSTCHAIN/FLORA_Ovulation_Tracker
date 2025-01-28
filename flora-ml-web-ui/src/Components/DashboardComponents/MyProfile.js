import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Heading,
  Text,
  Button,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalFooter,
  ModalBody,
  ModalCloseButton,
  FormControl,
  FormLabel,
  Input,
  Switch,
  useDisclosure,
  Radio,
  RadioGroup,
  Stack,
  HStack,
  useToast,
} from '@chakra-ui/react';
import { fetchUserData } from '../../api/user/user_data_api';
import { update_user_preferences, update_user } from '../../api/user/user_preferences_api';
import { logout_api } from '../../api/auth/logout_api';
import { delete_my_files_api } from '../../api/user/delete_my_files_api';

const MyProfile = () => {
  const navigate = useNavigate();
  const token = sessionStorage.getItem('jwtToken');
  const [userData, setUserData] = useState(null);
  const { isOpen: isModalOpen, onOpen: onModalOpen, onClose: onModalClose } = useDisclosure();
  const { isOpen: isSuccessModalOpen, onOpen: onSuccessModalOpen, onClose: onSuccessModalClose } = useDisclosure();
  const { isOpen: isDeleteModalOpen, onOpen: onDeleteModalOpen, onClose: onDeleteModalClose } = useDisclosure();
  const [password, setPassword] = useState('');
  const [newEmail, setNewEmail] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [federatedLearningEnabled, setFederatedLearningEnabled] = useState(false);
  const [initialFederatedLearning, setInitialFederatedLearning] = useState(false);
  const [sharing4goodEnabled, setSharing4goodEnabled] = useState(false);
  const [initialSharing4good, setInitialSharing4good] = useState(false);
  const [updateType, setUpdateType] = useState('email');
  const [errorMessage, setErrorMessage] = useState('');
  const toast = useToast();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const fetchedUserData = await fetchUserData(token);
        setUserData(fetchedUserData);
        setNewEmail(fetchedUserData.email);
        setFederatedLearningEnabled(fetchedUserData.federated_learning);
        setSharing4goodEnabled(fetchedUserData.sharing4good);
        setInitialFederatedLearning(fetchedUserData.federated_learning);
        setInitialSharing4good(fetchedUserData.sharing4good);
      } catch (error) {
        console.error('Error fetching user data:', error);
      }
    };

    fetchData();
  }, [token]);

  const handleFederatedLearningUpdate = async () => {
    try {
      const response = await update_user_preferences('federated_learning', federatedLearningEnabled, token);
      console.log(response); // Handle success or use response as needed
      setInitialFederatedLearning(federatedLearningEnabled);
    } catch (error) {
      console.error('Error updating Federated Learning:', error);
    }
  };

  const handleSharing4goodUpdate = async () => {
    try {
      const response = await update_user_preferences('sharing4good', sharing4goodEnabled, token);
      console.log(response); // Handle success or use response as needed
      setInitialSharing4good(sharing4goodEnabled);
    } catch (error) {
      console.error('Error updating Sharing for Good:', error);
    }
  };

  const handleUpdate = () => {
    onModalOpen();
  };

  const handleConfirmUpdate = async () => {
    try {
      const payload = {
        current_password: password,
        new_email: updateType === 'email' ? newEmail : null,
        new_password: updateType === 'password' ? newPassword : null,
      };

      const response = await update_user(payload);

      if (response.error) {
        setErrorMessage(response.error);
        return;
      }

      setUserData((prev) => ({
        ...prev,
        email: updateType === 'email' ? newEmail : prev.email,
      }));

      onModalClose();
      onSuccessModalOpen();
    } catch (error) {
      console.error('Error updating user:', error);
      setErrorMessage('An error occurred while updating the user.');
    }
  };

  const handleCancelUpdate = () => {
    setPassword('');
    setNewEmail(userData?.email || '');
    setNewPassword('');
    setErrorMessage('');
    onModalClose();
  };

  const handleLogout = async () => {
    try {
      await logout_api();
      sessionStorage.removeItem('jwtToken');
      console.log('Logout successful');
      navigate('/login');
    } catch (err) {
      console.error('Logout error:', err);
    }
  };

  const handleSuccessModalClose = () => {
    onSuccessModalClose();
    handleLogout();
  };

  const handleDeleteFiles = async () => {
    try {
      if (initialSharing4good) {
        return; // Exit if initialSharing4good is false
      }

      const response = await delete_my_files_api();

      if (response) {
        toast({
          title: 'Files Deleted',
          description: 'Your files have been successfully deleted.',
          status: 'success',
          duration: 5000,
          isClosable: true,
        });
      } else {
        throw new Error('Failed to delete files');
      }
    } catch (error) {
      toast({
        title: 'Error',
        description: 'There was an error deleting your files.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
      console.error('Error deleting files:', error);
    }
  };

  const isFederatedLearningModified = federatedLearningEnabled !== initialFederatedLearning;
  const isSharing4goodModified = sharing4goodEnabled !== initialSharing4good;

  return (
    <Box maxW="xl" borderWidth="1px" borderRadius="lg" overflow="hidden" p="4">
      <Heading as="h2" size="lg" mb="2">
        User Profile
      </Heading>
      {userData ? (
        <Box>
          <Text fontSize="lg">Username: {userData.username}</Text>
          <Text fontSize="lg">Email: {userData.email}</Text>
          <Text fontSize="lg" mt="2">
            Access Type: {userData.access_type}
          </Text>

          <FormControl display="flex" alignItems="center" mt="4">
            <FormLabel htmlFor="federatedLearning" mb="0">
              Federated Learning
            </FormLabel>
            <Switch
              id="federatedLearning"
              isChecked={federatedLearningEnabled}
              onChange={(e) => setFederatedLearningEnabled(e.target.checked)}
              colorScheme="blue"
            />
            {isFederatedLearningModified && (
              <Button ml="2" onClick={handleFederatedLearningUpdate}>
                Save Changes
              </Button>
            )}
          </FormControl>

          <FormControl display="flex" alignItems="center" mt="4">
            <FormLabel htmlFor="sharing4good" mb="0">
              Sharing for Good
            </FormLabel>
            <Switch
              id="sharing4good"
              isChecked={sharing4goodEnabled}
              onChange={(e) => setSharing4goodEnabled(e.target.checked)}
              colorScheme="blue"
            />
            {isSharing4goodModified && (
              <Button ml="2" onClick={handleSharing4goodUpdate}>
                Save Changes
              </Button>
            )}
          </FormControl>

          <HStack mt="4">
            <Button onClick={handleUpdate}>
              Update Password/Email
            </Button>
            <Button onClick={onDeleteModalOpen} isDisabled={initialSharing4good}>
              Delete My Files
            </Button>
          </HStack>
        </Box>
      ) : (
        <Text>Loading user data...</Text>
      )}

      <Modal isOpen={isModalOpen} onClose={handleCancelUpdate}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Update Account</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <FormControl mb="4">
              <FormLabel>Current Password</FormLabel>
              <Input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter your current password"
              />
            </FormControl>
            <RadioGroup onChange={setUpdateType} value={updateType}>
              <Stack direction="row">
                <Radio value="email">Update Email</Radio>
                <Radio value="password">Update Password</Radio>
              </Stack>
            </RadioGroup>
            {updateType === 'email' ? (
              <FormControl mt="4">
                <FormLabel>New Email</FormLabel>
                <Input
                  type="email"
                  value={newEmail}
                  onChange={(e) => setNewEmail(e.target.value)}
                  placeholder="Enter your new email"
                />
              </FormControl>
            ) : (
              <FormControl mt="4">
                <FormLabel>New Password</FormLabel>
                <Input
                  type="password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  placeholder="Enter your new password"
                />
              </FormControl>
            )}
            {errorMessage && <Text color="red.500">{errorMessage}</Text>}
          </ModalBody>

          <ModalFooter>
            <Button colorScheme="blue" mr={3} onClick={handleConfirmUpdate}>
              Confirm
            </Button>
            <Button variant="ghost" onClick={handleCancelUpdate}>
              Cancel
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      <Modal isOpen={isSuccessModalOpen} onClose={handleSuccessModalClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Success</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <Text>Your account has been successfully updated! Please login again.</Text>
          </ModalBody>

          <ModalFooter>
            <Button colorScheme="blue" onClick={handleSuccessModalClose}>
              Close
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

      <Modal isOpen={isDeleteModalOpen} onClose={onDeleteModalClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Confirm Delete</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <Text>Are you sure you want to delete your files? All your data will be deleted. This action cannot be undone.</Text>
          </ModalBody>
          <ModalFooter>
            <Button colorScheme="red" mr={3} onClick={handleDeleteFiles}>
              Delete
            </Button>
            <Button variant="ghost" onClick={onDeleteModalClose}>
              Cancel
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </Box>
  );
};

export default MyProfile;
