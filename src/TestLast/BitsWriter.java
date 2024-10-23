package TestLast;

import java.io.*;


public class BitsWriter extends BufferedOutputStream {

	/**
	 * Field Variable
	 */

	// buffer of bytes
	byte[] buffer;
	// size of buffer
	int bufferSize;
	// represent currentByte we are writing bits on
	byte currentByte;
	// count number of bytes written in buffer so far (ranges from 1 to bufferSize)
	int bytesInBuffer;
	// count of how many bits left in the current byte to write on
	int bitsLeft;

	/**
	 * The constructor takes the file to write to and buffer size
	 * 
	 * @param file : the file that we want to write bits to
	 * @param size : the size of the created buffer
	 * @throws FileNotFoundException : if the file could not be found
	 */

	public BitsWriter(File file, int size) throws FileNotFoundException {
		super(new FileOutputStream(file));

		bufferSize = size;

		// create the buffer array
		buffer = new byte[size];

		// Initialize bitsCount to max because there is space for 8 bits in current byte
		// to store
		bitsLeft = 8;
	}

	/**
	 * Another method to also write code, useful for sequences larger than 8 where
	 * we split them in chunks of 8 and write them.
	 * 
	 * @param sequence : the int value( byte sequence) to be written
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void writeCode(int length, int sequence) throws NumberFormatException, IOException {

		int i = length;
		byte currentVal = 0;

		while (i > 8) {
			// Test:
			// System.out.println(i+"integer method\t\t"+ (byte) (sequence>>(i-8) &
			// 0b11111111));

			// write the left most first - because of how the code
			currentVal = (byte) (sequence >> (i - 8) & 0b11111111);
			writeBits(8, currentVal);
			i -= 8;

		}

		// System.out.println(i+"integer method\t\t"+ (byte) (sequence & binaries[i]));

		// write the rest of code, that is less than or equal 8, write what is left
		writeBits(i, (byte) (sequence & binaries[i]));

	}

	private int binaries[] = { 0b00000000, 0b00000001, 0b00000011, 0b00000111, 0b00001111, 0b00011111, 0b00111111,
			0b01111111, 0b11111111 };

	/**
	 * write the bits defined , note that the writing is done from starting from
	 * right most bit and going upward. We first store in the right most bits, and
	 * then when we want to write again we shift the previous value to the left and
	 * write at the right most.
	 * 
	 * @param length : how many bits to write (from 1-8 inclusive)
	 * @param value  : the value to write (from 0-255)
	 * @throws IOException because of writing buffer
	 */
	public void writeBits(int length, int value) throws IOException {

		// check if valid
		if (length > 8 || length < 0)
			throw new IllegalArgumentException("The length is out of range for a byte value");

		// this is to ensure we only take the wanted right most bits
		value &= binaries[length];

		// check if current byte fits all the value
		if (length >= bitsLeft) {

			// if entered here this mean that the value does not fit as a whole in the
			// currentByte

			// here we write the first chunk of byteValue that would fit in currentByte,
			// we want to shift left by bitsCount, to write in the current byte as much as
			// it fits, at the same time we shif value to the right because we want to write
			// the leftmost bits of the value first
			currentByte = (byte) ((currentByte << bitsLeft) | (value >> (length - bitsLeft)));

			// the currentByte is full so add it to buffer
			// buffer[bytesInBuffer++] = currentByte;

			// the currentByte is full so add it to buffer
			buffer[bytesInBuffer++] = currentByte;

			// check if buffer is full and write it if full
			if (bytesInBuffer >= bufferSize) {
				this.write(buffer);
				// reset number of bytes in buffer after writing the buffer to zero
				bytesInBuffer = 0;

			}

			// after writing the left most, we update what is left to write of the value,
			// which are the rightmost bits
			value = value & binaries[length - bitsLeft];
			// update length because we already wrote part of the value
			length -= bitsLeft;

			// reset the current byte
			bitsLeft = 8;
			currentByte = 0;
		}

		// re-check if there is something left to write (because it could be equal in
		// the first if statement)
		// note: we come here when basically there is space to write the value in the
		// current byte
		if (length > 0) {
			// we shift to left by as much as we want to write and then write at the
			// rightmost of the byte
			currentByte = (byte) ((currentByte << length) | value);
			// update empty spaces
			bitsLeft -= length;
		}
	}

	/**
	 * A method used to write a whole byte in the buffer, especially to be used in
	 * header to write full bytes
	 * 
	 * @param byteValue : the byte value to be written. This would throw an error if
	 *                  we send a value larger than byte.
	 * @throws IOException
	 */
	public void writeAByte(byte byteValue) throws IOException {
		// call the writeBits with length 8
		writeBits(8, byteValue);
	}

	/**
	 * A method to write a short value , especially to be used in header to write
	 * full bytes. This would throw an error if we send a value larger than 2
	 * bytes(short).
	 * 
	 * @param shortValue : the short Value to be written
	 * @throws IOException
	 */
	public void writeAShort(short shortValue) throws IOException {

		// least significant byte
		byte LSB = (byte) (shortValue & 0b0000000011111111);

		// most significant byte
		byte MSB = (byte) ((shortValue >> 8) & 0b0000000011111111);

		// write the MSB first
		writeAByte(MSB);
		writeAByte(LSB);

	}

	/**
	 * A method to write one bit
	 * 
	 * @param bitValue : the value of bit to write, 0/1
	 * @throws IOException
	 */
	public void writeABit(byte bitValue) throws IOException {

		// Test:
		// System.out.println("\n*"+bitValue+"*");

		// check if value is valid
		if (bitValue != 0 && bitValue != 1)
			throw new IllegalArgumentException("The bit Value is not valid");

		// call the writeBits with length 1
		writeBits(1, bitValue);
	}

	/**
	 * A method to end the current byte writing, and start entering data in a new
	 * byte. This is helpful when ending header and starting the encoded data from a
	 * new byte.
	 * 
	 * @throws IOException
	 */
	public void startNewByte() throws IOException {
		writeBits(bitsLeft, 0);
	}

	/**
	 * a method to flush the buffer if we want to start from a clear buffer. This
	 * method must be called at the end, before closing the output stream.
	 * 
	 * @throws IOException
	 */
	public void flushBuffer() throws IOException {

		// check if we are at a start of new byte, then we don't need to start 
		if (bitsLeft != 8) {

			//save last byte in buffer
			buffer[bytesInBuffer++] = (byte) (currentByte<< bitsLeft); // write the byte, since we are writing from right most we need to shift it
			
			//write((currentByte << bitsLeft));// write the byte, since we are writing from right most we need to shift it

			
			currentByte = 0;
			bitsLeft = 8;
		}

		

		// write as much is available in the rest of last byte
		this.write(buffer, 0, bytesInBuffer); // this method writes the number of bytes as stated in (bytesInBuffer) in the
											// buffer starting from 0

		//reset byte count
		bytesInBuffer=0;
		
	
		
		// flush internally
		this.flush();

	}

	/**
	 * a method to be used to close the buffer and output stream.Very important to
	 * be called at the end.
	 * 
	 * @throws IOException
	 */
	public void closeBuffer() throws IOException {
		this.flushBuffer();
		// close the output stream
		this.close();
	}

}
