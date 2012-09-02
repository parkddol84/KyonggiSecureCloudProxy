package kgu.kscp.csp.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;

public class DropBoxUtils {
	private AccessTokenPair accessToken;
	private WebAuthSession was;
	private DropboxAPI<WebAuthSession> api;

	private String downloadPath = "";
	private String uploadPath = "";

	public DropBoxUtils(AppKeyPair appKey, AccessTokenPair accessToken,
			AccessType accessType) {
		this.accessToken = accessToken;
		was = new WebAuthSession(appKey, accessType);
		was.setAccessTokenPair(accessToken);
		api = new DropboxAPI<WebAuthSession>(was);

	}

	public Entry getMetadata(String path) throws Exception {
		Entry existingEntry = api.metadata(path, 1, null, false, null);
		System.out.println(existingEntry.rev);
		return existingEntry;
	}

	public Entry uploadFile(String path) throws Exception {
		FileInputStream inputStream = null;

		File file = new File(path);
		inputStream = new FileInputStream(file);
		Entry newEntry = api.putFile(path, inputStream,
				file.length(), null, null);

		inputStream.close();
		
		return newEntry;

	}

	public DropboxFileInfo downloadFile(String path) throws Exception {
		FileOutputStream outputStream = null;

		File file = new File(downloadPath+ path);
		outputStream = new FileOutputStream(file);
		DropboxFileInfo info = api.getFile(path, null, outputStream, null);

		outputStream.close();
		return info;

	}

}
