package kgu.kscp.erasure;

public class Decoder implements Runnable {
	static {
		System.loadLibrary("Decoder");
	}

	private native String jerasureDecoding(String[] args);

	private String mParams[];

	private Decoder() {
	}

	private Decoder(String args[]) {
		mParams = args;
	}

	@Override
	public void run() {
		jerasureDecoding(mParams);
	}

	public static void decoding(String fileName) {
		String args[] = new String[2];
		args[0] = "decoder";
		args[1] = fileName;
		Decoder d = new Decoder();
		d.jerasureDecoding(args);

		Thread encoder = new Thread(new Decoder(args));
		encoder.start();
	}

}
