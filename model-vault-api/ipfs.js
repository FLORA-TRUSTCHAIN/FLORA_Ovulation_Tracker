import { create } from 'kubo-rpc-client';

const ipfs = create();

/**
 * Uploads data to IPFS
 * @param {string} data - The data to be uploaded
 * @returns {Promise} - The CID of the uploaded file
 */
export async function upload(data) {
    try {
        const result = await ipfs.add(data);
        const cid = result.cid.toString();

        return cid;
    } catch (error) {
        throw new Error('Error uploading model to IPFS: ' + error.message);
    }
}

/**
 * Downloads data from IPFS
 * @param {string} cid - The CID of the data to download
 * @returns {Buffer} - The downloaded data as a buffer
 */
export async function download(cid) {
    try {
        const stream = ipfs.cat(cid);
        const chunks = [];

        for await (const chunk of stream) {
            chunks.push(chunk);
        }

        return Buffer.concat(chunks);
    } catch (error) {
        throw new Error('Error downloading model from IPFS: ' + error.message);
    }
}
