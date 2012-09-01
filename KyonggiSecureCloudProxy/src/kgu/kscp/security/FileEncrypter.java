package kgu.kscp.security;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FileEncrypter {

	private static final String algorithm = "AES";
	private static final String transformation = algorithm
			+ "/CBC/PKCS5Padding";

	private Key key;
	private IvParameterSpec iv;

	public FileEncrypter(Key key, IvParameterSpec ivspec) {

		this.key = key;
		this.iv = ivspec;
	}

	public void encrypt(File source, File dest) throws Exception {
		crypt(Cipher.ENCRYPT_MODE, source, dest);

	}

	public void decrypt(File source, File dest) throws Exception {
		crypt(Cipher.DECRYPT_MODE, source, dest);

	}

	private void crypt(int mode, File source, File dest) throws Exception {
		Cipher cipher = Cipher.getInstance(transformation);

		cipher.init(mode, key, iv);

		InputStream input = null;
		OutputStream output = null;

		input = new BufferedInputStream(new FileInputStream(source));
		output = new BufferedOutputStream(new FileOutputStream(dest));
		byte[] buffer = new byte[1024];
		int read = -1;
		while ((read = input.read(buffer)) != -1) {
			output.write(cipher.update(buffer, 0, read));
		}
		output.write(cipher.doFinal());

		output.close();
		input.close();

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
