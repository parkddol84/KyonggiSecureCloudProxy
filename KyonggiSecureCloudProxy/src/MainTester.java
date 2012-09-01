import java.io.File;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import kgu.kscp.erasure.Encoder;
import kgu.kscp.security.FileEncrypter;

public class MainTester {
	public static void main(String[] args) throws Exception {
		System.out.println("File Upload Started...");

		SecretKeySpec key = new SecretKeySpec(toBytes(
				"696d697373796f7568616e6765656e61", 16), "AES");
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		IvParameterSpec ivspec = new IvParameterSpec(iv);

		FileEncrypter encrypter = new FileEncrypter(key, ivspec);

		encrypter.encrypt(new File("files/RACS.pptx"), new File("files/encryptedFiles/encryptedRACS.pptx"));
		
		
		Encoder.encoding("files/encryptedFiles/encryptedRACS.pptx", 3, 1);
		
		System.out.println("Ended...");
		

	}

	public static byte[] toBytes(String digits, int radix)
			throws IllegalArgumentException, NumberFormatException {
		if (digits == null) {
			return null;
		}
		if (radix != 16 && radix != 10 && radix != 8) {
			throw new IllegalArgumentException("For input radix: \"" + radix
					+ "\"");
		}
		int divLen = (radix == 16) ? 2 : 3;
		int length = digits.length();
		if (length % divLen == 1) {
			throw new IllegalArgumentException("For input string: \"" + digits
					+ "\"");
		}
		length = length / divLen;
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) {
			int index = i * divLen;
			bytes[i] = (byte) (Short.parseShort(
					digits.substring(index, index + divLen), radix));
		}
		return bytes;
	}
}
