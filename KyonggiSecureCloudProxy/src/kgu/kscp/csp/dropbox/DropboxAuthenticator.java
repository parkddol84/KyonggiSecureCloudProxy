package kgu.kscp.csp.dropbox;

import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;

public class DropboxAuthenticator {
	private static DropboxAuthenticator authenticator;

	private String APP_KEY;
	private String APP_SECRET;
	private AccessType ACCESS_TYPE;

	private String loginid;
	private String password;

	private AccessTokenPair accessToken;
	private RequestTokenPair requestToken;

	public static DropboxAuthenticator getInstance() {
		if (authenticator == null) {
			authenticator = new DropboxAuthenticator();

		}
		return authenticator;

	}

	private void storeAccessToken(AccessTokenPair pair) {

	}

	public AccessTokenPair getAccessToken() {
		return accessToken;

	}

	public void init(String appKey, String appSecret, String loginid,
			String password, Session.AccessType type) {
		this.APP_KEY = appKey;
		this.APP_SECRET = appSecret;
		this.ACCESS_TYPE = type;
		this.loginid = loginid;
		this.password = password;

		AppKeyPair appKeyPair = new AppKeyPair(appKey, appSecret);

		WebAuthSession was = new WebAuthSession(appKeyPair, ACCESS_TYPE);

		try {
			WebAuthSession.WebAuthInfo info = was.getAuthInfo();
			System.out.println("1. Go to: " + info.url);
			System.out.println("2. Allow access to this app.");
			System.out.println("3. Press ENTER.");

			while (System.in.read() != '\n') {
			}

			String userId = was.retrieveWebAccessToken(info.requestTokenPair);

			System.out.println("User ID: " + userId);
			System.out.println("Access Key: " + was.getAccessTokenPair().key);
			System.out.println("Access Secret "
					+ was.getAccessTokenPair().secret);
			this.accessToken = was.getAccessTokenPair();

		}

		catch (Exception e) {
			e.printStackTrace();

		}

	}

	private DropboxAuthenticator() {

	}

}
