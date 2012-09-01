package kgu.kscp.erasure;

public class ErasureTest {
	public static void main(String args[]) {
		System.out.println("started...");

		//Encoder.encoding("files/RACS.pptx", 3, 1);
		Decoder.decoding("Coding/RACS.pptx");

		System.out.println("ended...");
	}
}
