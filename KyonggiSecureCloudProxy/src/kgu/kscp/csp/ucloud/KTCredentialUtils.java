package kgu.kscp.csp.ucloud;

import java.security.SecureRandom;

import kgu.kscp.util.Base64;

public class KTCredentialUtils {
	private static SecureRandom random = new SecureRandom();
	
	
	public static String getNonce(){
		byte[] nonce = new byte[32];
		random.nextBytes(nonce);
		return Base64.encodeBytes(nonce).substring(0, 31);
		
	}
	public static String getTimestamp(){
		return System.currentTimeMillis() / 1000 + "";
	}

}
