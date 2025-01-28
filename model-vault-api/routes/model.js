import { Router } from 'express';
import { Web3 } from 'web3';
import { download, upload } from '../ipfs.js';
import { createRequire } from "module";

const require = createRequire(import.meta.url);
const builtContract = require("../contracts/ModelVault.json");

// Set up Web3 and connect to the local Ethereum network
const web3 = new Web3(new Web3.providers.HttpProvider("http://127.0.0.1:8545"));

// Set up the contract instance
const contractAddress = "0x5fbdb2315678afecb367f032d93f642f64180aa3";
const contract = new web3.eth.Contract(builtContract.abi, contractAddress);

// Set up your account with your private key
const account = web3.eth.accounts.privateKeyToAccount("0xdf57089febbacf7ba0bc227dafbffa9fc08a93fdc68e1e42411a14efcf23656e");
web3.eth.accounts.wallet.add(account);

const router = Router();

router.post('/save', async (req, res) => {
    const { modelVersion, modelHash, model } = req.body;

    try {
        const cid = await upload(model);

        // Estimate gas for the transaction
        const gas = await contract.methods
            .saveModel(modelVersion, cid.toString(), modelHash)
            .estimateGas({ from: account.address });

        // Create the transaction
        const tx = await contract.methods
            .saveModel(modelVersion, cid.toString(), modelHash)
            .send({ from: account.address, gas });

        res.status(200).send({ message: 'Model saved successfully', transactionHash: tx.transactionHash });
    } catch (error) {
        res.status(500).send({ message: 'Error saving model', error });
    }
});

router.get('/retrieve/:version', async (req, res) => {
    const { version } = req.params;

    try {
        const model = await contract.methods.getModel(version).call();

        res.status(200).send({ message: 'Model retrieved successfully',
            result: {
                version,
                cid: model.cid,
                modelHash: model.modelHash,
                timestamp: model.timestamp.toString(), // TODO: desirialize bigInt
                uploader: model.uploader,
                model: await download(model.cid)
            }
        });
    } catch (error) {
        console.log(error)
        res.status(500).send({ message: 'Error retrieving model', error });
    }
});


router.post('/verify/:version', async (req, res) => {
    const { version } = req.params;
    const { hash } = req.body;

    try {
        const isValid = await contract.methods
            .verifyModel(version, hash)
            .call();

        res.status(200).send({ message: 'Model verified successfully', isValid });
    } catch (error) {
        console.log(error)
        res.status(500).send({ message: 'Error verifying model', error });
    }
});

export default router;
