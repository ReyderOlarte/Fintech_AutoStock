package com.autoStock.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * @author Kevin Kowalewski
 *
 */
public class BranchSTDIO {
	
	public static final String defaultFileName = "C:/Users/Kevin/Desktop/autoStock.txt";
	
	public void branchSTDOUTToFile(String fileName) throws Exception{
		System.setOut(new CustomPrintStream(new File(fileName)));
	}
	
	public static class CustomPrintStream extends PrintStream {
		public CustomPrintStream(File file) throws FileNotFoundException {
			super(file);
		}

		@Override
		public void write(byte[] buf, int off, int len) {
			super.write(buf, off, len);
		}

		@Override
		public void write(int b) {
			super.write(b);
		}
	}
}
