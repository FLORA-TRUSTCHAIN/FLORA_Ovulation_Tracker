import * as ort from 'onnxruntime-web';

export function flatten(arr: any[]): any[] {
	while (arr.length > 0 && Array.isArray(arr[0])) {
		arr = [].concat(...arr)
	}
	return arr
}

export function size(shape: readonly number[]): number {
	return shape.reduce((a, b) => a * b)
}

export function randomArray(shape: number[], minimum: number, maximum: number): Float32Array {
	const result = new Float32Array(size(shape))
	for (let i = 0; i < result.length; ++i) {
		result[i] = Math.random() * (maximum - minimum) + minimum
	}
	return result
}

export function randomTensor(shape: number[], minimum: number, maximum: number): ort.Tensor {
	return new ort.Tensor('float32', randomArray(shape, minimum, maximum), shape)
}

