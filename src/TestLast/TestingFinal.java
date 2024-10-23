package TestLast;

import java.io.File;
import java.io.IOException;


public class TestingFinal {

	public static void main(String[] args) throws IOException {
		// testing
		
		//String[] testcases = {};
		//File sourceFile = new File("photo2.jpg");
		File sourceFile = new File("r2.png");

		//File sourceFile = new File("Test4.txt");

		// int[] arr= new int[256];

		// int x= huff.getFileFrequencies(sourceFile, arr);

		// System.out.print(x);

		try {
			HuffmanFinal.huffmanCompress(sourceFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("\n\n\n---finished of compression---- \n\n\n");

		/*
		 * for (int i = 0; i < arr.length; i++) { if(arr[i]!=0)
		 * //System.out.println(i+">>>>>"+ arr[i] + " "); System.out.println( arr[i] );
		 * 
		 * }
		 */

		// huff.huffmanCompress(sourceFile);

		File compressedFile = new File("r2.veryImportant");
		//File compressedFile = new File("Test4.veryImportant");

		try {
			//huffmanUnCompress(compressedFile);
			HuffmanFinal.huffmanUnCompress(compressedFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
