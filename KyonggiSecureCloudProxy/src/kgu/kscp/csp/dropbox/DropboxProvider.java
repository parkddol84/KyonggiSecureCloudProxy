package kgu.kscp.csp.dropbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import kgu.kscp.csp.CSPInterface;

import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

public class DropboxProvider implements CSPInterface {

	private DropBoxUtils utils = null;
	private static final String TOKEN_FILE_NAME = "dropbox_token";
	private AccessType accessType = AccessType.APP_FOLDER;
	private DropboxAuthenticator authn = DropboxAuthenticator.getInstance();

	public DropboxProvider(String id, String pw) throws Exception {

		File file = new File(TOKEN_FILE_NAME);

		AccessTokenPair accessToken = null;

		if (!file.exists() || (file.length() == 0)) {

			authn.init(DropboxConstant.AUTH_KEY, DropboxConstant.AUTH_SECRET,
					id, pw, accessType);
			accessToken = authn.getAccessToken();
			FileWriter fw = new FileWriter(file);
			fw.write(accessToken.key + " " + accessToken.secret);
			fw.close();

		} else {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			String temp = in.readLine();
			String[] keys = temp.split(" ");
			accessToken = new AccessTokenPair(keys[0], keys[1]);
			in.close();
		}
		utils = new DropBoxUtils(new AppKeyPair(DropboxConstant.AUTH_KEY,
				DropboxConstant.AUTH_SECRET), accessToken,
				AccessType.APP_FOLDER);

	}

	@Override
	public String requestAccessToken(String loginId, String passsword)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean downloadFile(String localDownloadPath, String fileName)
			throws Exception {
		// TODO Auto-generated method stub
		DropboxFileInfo info = utils.downloadFile(localDownloadPath + fileName);
		Entry entry = info.getMetadata();
		System.out.println(entry.path);

		System.out.println(info.getFileSize());

		return true;
	}

	@Override
	public boolean uploadFile(String localUploadPath, String fileName)
			throws Exception {
		// TODO Auto-generated method stub
		Entry entry = utils.uploadFile(localUploadPath + fileName);
		System.out.println(entry.mimeType);

		return true;
	}

	@Override
	public void viewRootList() throws Exception {
		// TODO Auto-generated method stub

	}

}
