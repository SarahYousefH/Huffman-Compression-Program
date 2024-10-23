package TestLast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import dataStructures.BinaryTree;
import dataStructures.Heap;
import dataStructures.TreeNode;

/**
 * The main class to represent the Huffman compression algorithm, we have two
 * main static method: compress and uncompress
 * 
 * @author SarahHassouneh
 */
public class HuffmanFinal {

	/**
	 * define constants as to be shared between files and methods
	 */
	// I used 16 because my computer architecture has 128-bit data line
	public final static int BUFFER_SIZE = 16;
	public final static int BYTES = 265;

	public final static String EXTENSION = ".huf";

	// define varibles to be used and in fx
	public static int[] lengths;
	public static int[] freq;
	public static BinaryTree huffmanTree;
	public static int[] codesBinaries;

	/**
	 * This is the main method that compresses a file
	 * 
	 * @param file : the file to be compressed
	 * @return the compressed file
	 * @throws IOException
	 */
	public static File huffmanCompress(File file) throws IOException {
		/**
		 * we can think of compression as steps as follows:
		 */

		/**
		 * Step 1 : scan the file and read it, process bytes and construct table of
		 * frequencies
		 */

		freq = new int[BYTES];

		// This method fills the array of frequencies and returns the number of
		// (non-zero) frequencies of BYTES orders
		int number = getFileFrequencies(file, freq);

		/**
		 * Step 2 : construct the Huffman tree of codes
		 */

		huffmanTree = buildHuffmanTree(freq);

		// Test:
		huffmanTree.printLevelOrder();

		/**
		 * Step 3 : store the codes of each sequence in a table
		 */

		lengths = new int[BYTES];
		codesBinaries = new int[BYTES];

		// fill array with Huffman codes (after traversing it in-order), using this
		// recursive function
		fillArray(huffmanTree.getRoot(), 0, 0, codesBinaries, lengths);

		// a method to print sample table
		printTable(freq, codesBinaries, lengths);

		/**
		 * Step 4 :store the appropriate header
		 */

		// header size initialized to 7 : 2 bytes for header size, one for bits left, 4
		// for extension and the tree size will be added
		int headerSize = 7;

		// get file extension and get the file name
		String fileName = file.getName();

		// Extract the extension from the file name
		int index = fileName.lastIndexOf('.');
		String extension = fileName;
		String nameOnly = fileName;

		if (index > 0) {
			extension = fileName.substring(index + 1);
			nameOnly = fileName.substring(0, index);

			// Test:
			// System.out.println(nameOnly+","+extension);
		}

		/**
		 * calculate how many bits in last reading
		 */

		// this represent number of bits on the encoded data
		int numOfBits = 0;
		for (int i = 0; i < BYTES; i++) {
			if (freq[i] != 0)
				numOfBits += (freq[i] * lengths[i]);
		}

		// Test:
		System.out.println("num of bits" + numOfBits);

		// number of bits left after reading 16 byte at a time (number of bits in last
		// reading)
		

		/**
		 * tree size can be calculated as 9 bits for each leaf and 1 bit for each non
		 * leaf the number of non leaves is always the number of (leaves-1). Here we
		 * calculated in bytes ad took ceil because the last byte can be partially
		 * filled
		 */
		int treeSize = (int) Math.ceil((9 * number + number - 1) / 8.0); // in bytes

		headerSize += treeSize;

		int bitsLeft = (headerSize * 8 + numOfBits) % (BUFFER_SIZE * 8);
		
		// Test:
		System.out.println("tree size" + treeSize);
		System.out.println("header size" + headerSize);

		// Test:
		System.out.println("number of bits left" + bitsLeft);

		/**
		 * This is where writing of header starts
		 */
		// save file in same directory
		Path directoryPath = file.toPath().getParent();

		// Create the file
		Path filePath = directoryPath.resolve(nameOnly + EXTENSION);

		// File outputFile = new File(nameOnly + EXTENSION);
		File outputFile = filePath.toFile();

		// File outputFile = new File(nameOnly + EXTENSION);

		// This is a custom class , where I designed to help in writing bits values and
		// manage different writing operations to outputFile with regards to huffman

		BitsWriter output = new BitsWriter(outputFile, BUFFER_SIZE);

		// First write two bytes for size
		output.writeAShort((short) headerSize);

		// Then write one byte for bits left in last reading
		output.writeAByte((byte) bitsLeft);

		/*
		 * Then write the extension; there are 4 bytes for extension if it was 3
		 * characters it would be written for example like this: (0 t x t)
		 */
		for (int i = 4; i > 0; i--) {
			if (extension.length() - i < 0) {
				output.writeAByte((byte) 0);
				// output.write(0);
			} else {
				// output.write(extension.charAt(extension.length() - i));
				output.writeAByte((byte) extension.charAt(extension.length() - i));
			}
		}

		System.out.println("--------------");

		// Then write the tree representation
		encodeTree(huffmanTree, output);

		// Test:
		// System.out.println("\n>>" + BinaryTree.isFullTree(huffmanTree.getRoot()));

		// we finished writing header so we will start at a new byte
		output.startNewByte();

		/**
		 * Step 5: read the file again and encode each byte and write to output
		 */

		try {
			// Create a buffered stream to read binary file with size 16
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);

			byte[] bufferIn = new byte[BUFFER_SIZE];

			// we need this to get the unsigned value of each byte to save in array (byte in
			// java has numerical value from -128 to 127)
			int x;

			// keep count of read bytes, in order to be exact
			int readBytes = 0;

			// we read 16 bytes at a time in the buffer then we process them
			while ((readBytes = input.read(bufferIn)) != -1) { // the method returns number of bytes read

				for (int i = 0; i < readBytes; i++) {

					x = bufferIn[i] & 0xFF; // gets unsigned value

					output.writeCode(lengths[x], codesBinaries[x]);// --> important
					// System.out.print(codesBinaries[x] + ","+lengths[x]);

				}

			}

			input.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// close the buffer and return the compressed File
		output.closeBuffer();

		return outputFile;

	}

	/**
	 * A private method that builds huffman tree using the frequency array
	 * 
	 * @param freq : array that hold the frequencies of each byte value in current
	 *             file
	 * @return the Huffman tree that was built
	 */
	private static BinaryTree buildHuffmanTree(int[] freq) {

		// this represent number of non-zero frequencies
		int number = 0;

		// build the heap, we are using min heap
		Heap heap = new Heap();

		for (int i = 0; i < BYTES; i++) {
			if (freq[i] != 0) {
				heap.insert(new TreeNode(i, freq[i]));
				number++;
			}

		}

		// Test:
		// heap.printHeap();
		// System.out.print(heap.getSize());

		// build the tree of codes
		TreeNode min1, min2, join;
		for (int i = 0; i < number - 1; i++) { // we put number -1 to keep track of the root

			min1 = (TreeNode) heap.removeMin();
			min2 = (TreeNode) heap.removeMin();

			join = new TreeNode(min1, min2);

			heap.insert(join);

		}

		// Test:
		// heap.printHeap();

		// Extract the root of Huffman tree
		return new BinaryTree((TreeNode) heap.removeMin());

	}

	/**
	 * read the input file and get the frequencies
	 * 
	 * @param freq the array to fill of frequencies
	 * @param file the file to be read
	 * 
	 * @return the number of elements with (non-zero) frequencies, if count=-1 the
	 *         file reading had errors
	 */
	private static int getFileFrequencies(File file, int[] freq) {

		// counter to represent number of (bytes sequences) with non-zero frequencies
		int count = -1;

		// Continuously read a byte from file (-1 means reached end of file)
		try {
			// Create a buffered stream to read binary file with size of buffer (16)
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
			// reset count to zero means the buffered input stream is successfully opened
			count = 0;

			// buffer to read multiple bytes at a time
			byte[] bufferIn = new byte[BUFFER_SIZE];

			// we need this to get the unsigned value of each byte to save in array (byte in
			// java has numerical value from -128 to 127) but we wan to deal with it as
			// unsigned (0-255) so we need to cast it to int value to make it correct
			int x;

			// keep track of bytes read especially in last reading if less that buffer size.
			// bytesRead
			int bytesRead;

			while ((bytesRead = input.read(bufferIn)) != -1) {

				for (int i = 0; i < bytesRead; i++) {

					x = (bufferIn[i] & 0b11111111); // get unsigned value

					// Test:
					// System.out.println(buffer[i]);
					// System.out.println("**"+x);

					// Before incrementing this frequency checks if current frequency is zero , this
					// means it is the first time we passed it and will become non-zero, so we
					// increment count of non-zero numbers
					if (freq[x] == 0)
						count++;

					freq[x] += 1;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return count;

	}

	/**
	 * A recursive method to fill array of codes for each byte sequence.
	 * 
	 * @param T             : the tree node current
	 * @param sequence      : the sequence of 0's and 1's to be built upon
	 * @param length        : the current length of sequence
	 * @param codesBinaries : the array to fill when encountering leaf
	 * @param lengths:      the lengths of codes filled
	 */
	private static void fillArray(TreeNode T, int sequence, int length, int[] codesBinaries, int[] lengths) {

		// Test:
		// System.out.print( ">>" + sequence2);

		// we used in-order traversal to get binary codes
		if (T != null) {

			// go to the left, concatenate zero
			fillArray(T.getLeft(), ((sequence << 1) | 0), length + 1, codesBinaries, lengths);

			if (T.isLeaf()) {
				short x = (short) (T.getValue() & 0xFF);

				codesBinaries[x] = sequence;
				lengths[x] = length;

				// Test:
				// System.out.print(T.toString() + ">>" + sequence);
			}

			// go to the left, concatenate one
			fillArray(T.getRight(), ((sequence << 1) | 1), length + 1, codesBinaries, lengths);

		}
	}

	/**
	 * A simple helper method to print a simple table with values, frequencies and
	 * their lengths
	 * 
	 * @param freq          : The array where frequencies of each pattern is saved
	 * @param codesBinaries
	 * @param lengths
	 */
	private static void printTable(int[] freq, int[] codesBinaries, int[] lengths) {

		System.out.println("\nASCII \t Huffman \tLength \tFreq");

		for (int i = 0; i < BYTES; i++) {
			if (freq[i] != 0) {
				System.out.println("\n>>" + i + "\t\t" + (char) i + "\t\t" + Integer.toBinaryString(codesBinaries[i])
						+ "\t\t" + lengths[i] + "\t\t" + freq[i]);

			}

		}

	}

	/**
	 * A wrapper method for the recusive method to write the tree representation
	 * 
	 * @param tree   : tree to be written
	 * @param output : where to write
	 * @throws IOException
	 */
	private static void encodeTree(BinaryTree tree, BitsWriter output) throws IOException {
		encodeTree(tree.getRoot(), output);
	}

	/**
	 * A recursive method to write the tree representation. It writes the tree in
	 * pre-order(root,left,right), it writes 0 if non leaf and 1 if leaf followed by
	 * byte value(of 8 bits)
	 * 
	 * @param node   : current node written
	 * @param output : where to write
	 * @throws IOException
	 */

	private static void encodeTree(TreeNode node, BitsWriter output) throws IOException {

		if (node.isLeaf()) {

			// Test:
			System.out.print(1);
			System.out.print("*" + node.getValue() + "*");

			// write 1 followed by byte value
			output.writeABit((byte) 1);
			output.writeAByte((byte) node.getValue());

		} else {

			// Test:
			System.out.print(0);

			// write 0 then continue to write left then right
			output.writeABit((byte) 0);
			encodeTree(node.getLeft(), output);
			encodeTree(node.getRight(), output);

		}
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Now for uncompress Section:
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	/**
	 * This is the main method that uncompresses a file
	 * 
	 * @param file: : the file to be uncompressed
	 * @return the original file
	 * @throws IOException
	 */
	public static File huffmanUnCompress(File file) throws IOException {

		/**
		 * We can think of compression as steps as follows:
		 */

		/**
		 * Step 1 : Read appropriate header info
		 */

		// define new bit reader
		BitReader input = new BitReader(file, BUFFER_SIZE);

		// the first two bytes in file are header size
		int headerSize = input.readAShort();

		// read bits left, that is number of bits in last reading, when buffer is not
		// full.
		int lastReadingBits = input.readAByte();

		// if file is too small
		if (file.length() < BUFFER_SIZE)
			lastReadingBits -= headerSize * 8;

		// read the original file extension, max is 4 bytes
		String originalExtension = ".";
		for (int i = 0; i < 4; i++) {
			int x = input.readAByte(); // changed here to int
			if (x != 0)
				originalExtension += (char) x;

		}

		// remove from here --> temporary for testing
		// originalExtension += 'y';

		// Test:
		System.out.println("header size =" + headerSize);
		System.out.println("last reading bits" + lastReadingBits);
		System.out.println(originalExtension);

		/**
		 * Step 2 : Reconstruct the tree
		 */

		// update what is left of header size not read, we already read 7 bytes, the
		// rest of header size is for the tree representation
		headerSize = headerSize - 7;

		// decode tree
		// BinaryTree huffmanTree= decodeTree(input,headerSize*8);

		// This is correct
		// for(int i=0; i<headerSize;i++)
		// System.out.println(input.readAByte());

		System.out.println();

		BinaryTree huffmanTree = new BinaryTree(decodeTree(input));
		// BinaryTree huffmanTree = new BinaryTree(decodeTree2(input, headerSize * 8));

		huffmanTree.printLevelOrder();

		System.out.println();

		System.out.println(headerSize * 8 + "**" + ysize + "**");
		// The pointer to help in reading basically.
		TreeNode pointer = huffmanTree.getRoot();

		System.out.println("\n*******************************************\n");

		/*
		 * while(ysize >8) { int x = input.readBits(1); System.out.print(x); ysize--;
		 * if(x==1) { int y = input.readAByte();
		 * 
		 * System.out.print(y); ysize-=8; } }
		 */

		System.out.println("\n*****************" + "ysize" + ysize + "**************************\n");
		/**
		 * Step 3: Read encoded Data and write original data
		 */

		// start reading encoded data from new byte
		input.readNewByte();

		//
		// int bitsInLastByte = lastReadingBits % 8;

		// get name of file and add extension
		String fileName = file.getName();

		// Extract the extension from the file name
		int index = fileName.lastIndexOf('.');
		String nameOnly = fileName.substring(0, index);

		// System.out.println(nameOnly);

		Path directoryPath = file.toPath().getParent();

		// Create the file
		Path filePath = directoryPath.resolve(nameOnly + "Uncompressed" + originalExtension);

		// File outputFile = new File(nameOnly + EXTENSION);
		File outputFile = filePath.toFile();

		// create output file
		// File outputFile = new File( nameOnly +"Uncompressed" + originalExtension);
		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile));
		byte[] bufferOut = new byte[BUFFER_SIZE];
		int bytesWritten = 0;

		// read all filled buffers completely and encode
		while (input.bytesRead >= 16) {

			// System.out.println("entered");
			// read bit by bit
			byte bit = (byte) (input.readBits(1) & 0b00000001);

			// System.out.print("*"+bit+"*");

			if (pointer.isLeaf()) {
				// System.out.println("leaf");
				// write the value
				bufferOut[bytesWritten++] = (byte) pointer.getValue();
				// System.out.print((char) pointer.getValue());
				// reset pointer
				pointer = huffmanTree.getRoot();

			}

			if (bit == 0)
				pointer = pointer.getLeft();
			else
				pointer = pointer.getRight();

			if (bytesWritten >= 16) {
				output.write(bufferOut);
				bytesWritten = 0;
				// System.out.print("--------------");
			}

		}

		// System.out.println("e\t\t"+input.bytesRead);

		// handle last read buffer
		int x = 0;

		while (x < lastReadingBits) {

			// read bit by bit
			byte bit = (byte) (input.readBits(1) & 0b00000001);

			// System.out.print("*"+bit+"*");
			if (pointer.isLeaf()) {
				// System.out.println("leaf");
				// write the value
				bufferOut[bytesWritten++] = (byte) pointer.getValue();
				// System.out.print(((char) pointer.getValue()));
				//x+=8;
				// reset pointer
				// System.out.print("entered>>"+(byte) pointer.getValue());
				pointer = huffmanTree.getRoot();

			}

			if (bit == 0)
				pointer = pointer.getLeft();
			else
				pointer = pointer.getRight();

			x++;

			if (bytesWritten >= 16) {
				output.write(bufferOut);
				bytesWritten = 0;
				// System.out.print("--------------");
			}

		}

		System.out.print("\nout>>" + x);

		// Write any remaining bytes to the output
		if (bytesWritten > 0) {
			output.write(bufferOut, 0, bytesWritten);
		}

		//// ****************************************************8
		output.flush();
		output.close();
		return outputFile;

	}

	private static BinaryTree getTree(BitReader input, int treeSize) {
		return null;

	}

	/**
	 * A method to reconstruct Tree, works but the problem is that it does not
	 * account for tree length, see that later
	 * 
	 * @param input
	 * @param headerSize
	 * @return
	 * @throws IOException
	 */
	// static int t2 = 0;
	// static int t1 = 0;
	static int ysize = 0;

	private static TreeNode decodeTree(BitReader input) throws IOException {

		// char bit = input.charAt(index++);

		// if(treeSize==0) {
		// read one bit
		byte bit = (byte) (input.readBits(1) & 0b00000001);
		// byte bit = (byte) (input.readBits(1));

		System.out.print(bit);
		ysize++;

		if (bit == 1) {
			// if it is a leaf node
			// System.out.print("entered\t");
			// treeSize = -8;
			ysize += 8;
			int x = (input.readAByte());
			System.out.print("*" + x + "*");

			return new TreeNode((byte) x);

		} else {
			// not a tree node continue decoding

			// System.out.println("t1->"+ (++t1));
			TreeNode leftChild = decodeTree(input);

			// System.out.println("t2->"+ (++t2));
			TreeNode rightChild = decodeTree(input);

			return new TreeNode(leftChild, rightChild);
		}

		// }
		/*
		 * TreeNode leftChild = decodeTree(input, treeSize-1); TreeNode rightChild =
		 * decodeTree(input, treeSize-1);
		 * 
		 * return new TreeNode(leftChild, rightChild);
		 */

	}
}
