package kgu.kscp.csp.dropbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
public class DropboxTest {
	public static void main(String[] args) throws Exception {
		
		System.out.println("started...");
		String id ="";
		String pw = "";
		
		DropboxProvider dp = new DropboxProvider(id, pw);
		//dp.uploadFile("Coding/", "encryptedRACS_m1.pptx");
		dp.downloadFile("Coding/", "encryptedRACS_m1.pptx");
		
		
		
		
	
		System.out.println("ended...");
		
	}

}
