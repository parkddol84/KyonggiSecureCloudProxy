package kgu.kscp.erasure;

import java.io.File;

public class Encoder implements Runnable {
	static {
		System.loadLibrary("Encoder");
	}

	private native String jerasureEncoding(String[] args);

	private String mParams[];

	private Encoder() {
	}

	private Encoder(String args[]) {
		mParams = args;
	}

	@Override
	public void run() {
		jerasureEncoding(mParams);
	}

	public static void encoding(String fileName, int k, int m) {
		String args[] = new String[8];
		args[0] = "encoder";
		args[1] = fileName;
		args[2] = String.valueOf(k);
		args[3] = String.valueOf(m);
		args[4] = "liberation";
		args[5] = "7";
		args[6] = "1024";
		File file = new File(fileName);
		long fileSize = file.length() / 1024;
		args[7] = String.valueOf(fileSize);
		Thread encoder = new Thread(new Encoder(args));
		encoder.start();
	}
}
