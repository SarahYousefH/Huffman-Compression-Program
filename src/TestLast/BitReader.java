package TestLast;

import java.io.*;

public class BitReader extends BufferedInputStream {

	/**
	 * Field Variable
	 */

	// buffer of bytes
	byte[] buffer;
	// size of buffer
	int bufferSize;
	// represent currentByte we are reading from
	byte currentByte;
	// count number of bytes read from buffer so far (ranges from 1 to bufferSize)
	int bytesCount;
	// count of how many bits unread form current byte yet
	int bitsCount;
	// keep track of read bytes successfully from input (and not reached end of
	// file)
	public int bytesRead; // remove public later

	/**
	 * The constructor takes the file to read from and buffer size
	 * 
	 * @param file : the file that we want to write bits to
	 * @param size : the size of the created buffer
	 * @throws IOException
	 */

	public BitReader(File file, int size) throws IOException {
		super(new FileInputStream(file));

		bufferSize = size;

		// create the buffer array
		buffer = new byte[size];

		// Initialize the first read, read first and fill buffer
		bytesRead = this.read(buffer);
		// System.out.println(">>" + bytesRead);
		// Initialize current byte to first byte in buffer
		currentByte = buffer[bytesCount++];
		// Initialize bitsCount to 8 because current byte is has 8 unread bits now
		bitsCount = 8;

	}

	private int binaries[] = { 0b00000000, 0b00000001, 0b00000011, 0b00000111, 0b00001111, 0b00011111, 0b00111111,
			0b01111111, 0b0000000011111111 };

	/**
	 * A method to read number of specified bits, and return the byte value. This
	 * specific function is designed to return a byte value meaning that maximum
	 * length that can be sent here is 8 (as one byte). We read here from left to
	 * right.For our huffman we don't really need to read more than 8 bits at a time
	 * 
	 * @param length : the number of bits to read
	 * @return the value of bits read
	 * @throws IOException
	 */
	public int readBits(int length) throws IOException {

	

		int originalLength = length;

		// check if is valid
		if (length > 8 || length < 0)
			throw new IllegalArgumentException("The length is out of range for a byte value");

		// this is the return value of the function
		int byteValue = 0;

		// Test:
		// System.out.println("*"+byteValue);

		// check if current byte has enough unread bits(bitsCount) for the specified
		// length
		if (length >= bitsCount) {

			// Test:
			// System.out.println("yes");

			// if entered here this mean that the currentByte does not have enough unread
			// bits for the specific length. we first read what is left of currentByte, then
			// read nextByte

			// read the first part of the wanted value in the currentByte.
			// (length-bitsCount) represent the number of bits to read from next byte
			byteValue = (currentByte << (length - bitsCount));
			// System.out.println("---" + byteValue);
			// System.out.println("**"+byteValue);

			// update length that is left to read from next byte
			length = length - bitsCount;

			// check if finished reading all buffer and read new
			if (bytesCount >= bufferSize) {

				// clear current buffer first
				this.clearBuffer();

				bytesRead = this.read(buffer);
				// System.out.println(">>" + bytesRead);

				// reset number of bytes read in current buffer to zero
				bytesCount = 0;

				// Test:
				// System.out.println("new buffer");
				// System.out.println("**"+bytesRead);
				// System.out.println("---" + byteValue);
				// System.out.println("------------");
			}

			// System.out.println(">>" + bytesCount);

			// System.out.println(">reached");
			// the currentByte is finished with reading we get next byte
			currentByte = buffer[bytesCount++];
			// update number of unread bits in the new byte to 8
			bitsCount = 8;
			// System.out.println("***"+byteValue);

		}

		// something left to read (code be 8)
		if (length > 0) {
			// It would come here if there is enough unread bits in the currentByte for the
			// specific length. Here length <= bitsCount(unread bits)

			// System.out.println("new length"+ (bitsCount - length));
			int m = (currentByte >> (bitsCount - length)) & binaries[length];

			byteValue = (byteValue | m);

			// make sure correct value
			byteValue = (byte) (byteValue & binaries[originalLength]);
			
			//clear what we read
			currentByte &= binaries[bitsCount - length];

			// System.out.println(">>>>"+byteValue);
			// update unread bits in currentByte
			bitsCount -= length;
		}

		return byteValue;
	}

	/**
	 * A method that can be used directly to read one byte from sequence.
	 * 
	 * @return
	 * @throws IOException
	 */
	public int readAByte() throws IOException {
		int y = readBits(8);
		// System.out.println("y return"+y);
		return y;
	}

	/**
	 * A method to read a short value, as 2 bytes
	 * 
	 * @return the short value read
	 * @throws IOException
	 */
	public short readAShort() throws IOException {
		short shortValue = (short) (readAByte() << 8); // shifted 8 to left to fill LSB

		shortValue = (short) (shortValue | readAByte()); // reading LSB

		return shortValue;

	}

	/**
	 * @throws IOException
	 * 
	 */
	public void readNewByte() throws IOException {

		if (bytesCount >= bufferSize) {

			// clear current buffer first
			this.clearBuffer();

			bytesRead = this.read(buffer);

			bytesCount = 0;

		}

		currentByte = buffer[bytesCount++];
		bitsCount = 8;

	}

	/**
	 * clearing current buffer
	 */
	public void clearBuffer() {

		// clear the buffer
		for (int i = 0; i < bufferSize; i++) {
			buffer[i] = 0;
		}
	}

	/**
	 * not sure if this is necessary?
	 * 
	 * @throws IOException
	 */
	public void closeBuffer() throws IOException {
		clearBuffer();
		this.close();
	}

}
