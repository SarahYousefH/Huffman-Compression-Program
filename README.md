
# Huffman Coding File Compressor 🗜️

## Last Modified 📆
- **Date:** 23-October-2024

## About This Project 📘
This Huffman Coding application is a powerful tool designed for lossless data compression. It efficiently compresses any type of file by utilizing variable-length codes based on character frequencies — the most frequent characters receive the shortest codes. This project not only demonstrates technical proficiency but also showcases custom implementations of heaps and trees, built from scratch, to manage the priority queue and the Huffman coding tree, ensuring optimal compression.

## Features 🌟
- **Dynamic Compression:** Compresses a wide range of file types, adjusting codes dynamically based on content.
- **Custom Data Structures:** Implements priority queues and binary trees from scratch to manage character frequencies and encoding processes.
- **Lossless Decompression:** Ensures the original file is perfectly reconstructed from the compressed version.
- **Efficient Encoding and Decoding:** Displays a table of encodings for quick reference and efficient processing.

## Encoding the Huffman Tree 🌳
In the `encodeTree` method, a **pre-order traversal** (root, left, right) is used to encode the Huffman tree structure:

1. **Leaf Node Detection**: If the node is a leaf (it contains a character), the method writes a `1` followed by the **byte value** of the character.
2. **Non-Leaf Node**: If the node is not a leaf, the method writes a `0` and continues the traversal by recursively encoding the left and right children of the node.

This ensures the tree is encoded in a way that can later be fully reconstructed for decompression.

## Reconstructing the Huffman Tree 🌱
The Huffman tree is reconstructed using the frequencies of characters in the file:

1. **Min-Heap Construction**: A heap is built where each node represents a character and its frequency. Nodes with lower frequencies are prioritized.
2. **Tree Building**: The two nodes with the smallest frequencies are merged into a new node, which becomes their parent. This continues until the root of the tree is formed, which allows the Huffman codes to be generated.

This process is designed to maximize the compression efficiency by giving the shortest codes to the most frequent characters.

## What's Next? 🚀
- **GUI Implementation:** Plan to develop a user-friendly graphical interface to make it accessible to non-technical users.
- **Performance Optimization:** Continuous optimization of the compression algorithm to increase efficiency and speed.
- **Support for Multiple File Formats:** Expand the application to explicitly support more file formats, enhancing its versatility.



Feel free to explore, suggest improvements, or contribute to the project! For any issues or discussions, please open an issue or submit a pull request.
