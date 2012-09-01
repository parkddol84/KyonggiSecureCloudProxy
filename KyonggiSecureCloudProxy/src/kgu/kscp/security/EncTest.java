package kgu.kscp.security;

import java.io.File;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncTest {
	private static final String algorithm = "AES";

	public static void main(String[] args) throws Exception {
		SecretKeySpec key = new SecretKeySpec(toBytes(
				"696d697373796f7568616e6765656e61", 16), algorithm);
		// KeyGenerator kg = KeyGenerator.getInstance("AES");

		// SecretKey key = kg.generateKey();
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		System.out.println(iv.length);

		IvParameterSpec ivspec = new IvParameterSpec(iv);

		FileEncrypter coder = new FileEncrypter(key, ivspec);

		coder.encrypt(new File("RACS.pptx"), new File("encryptedRACS.pptx"));

		coder.decrypt(new File("encryptedRACS.pptx"), new File(
				"decryptedRACS.pptx"));

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
