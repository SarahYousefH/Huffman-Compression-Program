package application;

public class TableCell {

	int byteValue;
	String ascii;
	int huffman;
	int length;
	int freq;

	public TableCell(int byteValue, int huffman, int length, int freq) {
		this.byteValue = byteValue;
		this.huffman = huffman;
		this.length = length;
		this.freq = freq;
	}

	public int getByteValue() {
		return byteValue;
	}

	public String getAscii() {
		if (byteValue >= 0 && byteValue <= 127) {
			return (char) byteValue + "";
		} else {
			return "No ASCII";
		}

	}

	public String getHuffman() {
		String res=Integer.toBinaryString(huffman);
		
		while(res.length()<length) // concatenate zero to left
			res="0"+res;
		
		return res;
	}

	public int getLength() {
		return length;
	}

	public int getFreq() {
		return freq;
	}

}
