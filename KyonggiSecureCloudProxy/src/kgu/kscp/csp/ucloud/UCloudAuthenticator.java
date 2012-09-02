package kgu.kscp.csp.ucloud;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import kgu.kscp.util.Base64;

import oauth.signpost.OAuth;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class UCloudAuthenticator {
	private static UCloudAuthenticator authenticator;
	private static final String MAC_NAME = "HmacSHA1";
	public static String REQUEST_TOKEN_URL = "https://openapi.ucloud.com/ucloud/oauth/1.0a/request_token";
	public static String ACCESS_TOKEN_URL = "https://openapi.ucloud.com/ucloud/oauth/1.0a/access_token";
	public static String AUTHORIZE_URL = "https://openapi.ucloud.com/ucloud/oauth/1.0a/authorize";

	private String loginid;
	private String password;
	private String callback;

	private String consumerKey;
	private String consumerSecret;

	private String OAUTH_TOKEN;
	private String OAUTH_TOKEN_SECRET;
	private String OAUTH_VERIFIER;
	private String OAUTH_ACCESS_TOKEN;
	private String OAUTH_ACCESS_TOKEN_SECRET;

	private HttpClient httpClient = new DefaultHttpClient();

	public static UCloudAuthenticator getInstance() {
		if (authenticator == null)
			authenticator = new UCloudAuthenticator();
		return authenticator;

	}

	public String getTokenSecret() {
		if (OAUTH_TOKEN_SECRET == null)
			return "";

		return OAUTH_TOKEN_SECRET;

	}

	public String getToken() {
		if (OAUTH_TOKEN == null) {
			System.out.println("Oauth token is null");
			return null;
		}
		return OAUTH_TOKEN;
	}

	private UCloudAuthenticator() {
	}

	private String getConsumerKey() {
		return consumerKey;

	}

	private void initAccessToken() throws Exception {
		HttpPost post = new HttpPost(ACCESS_TOKEN_URL);

		String nonce = KTCredentialUtils.getNonce();
		String ts = KTCredentialUtils.getTimestamp();

		String plain = OAuth.OAUTH_CONSUMER_KEY + "="
				+ OAuth.percentEncode(getConsumerKey()) + "&"
				+ OAuth.OAUTH_NONCE + "=" + OAuth.percentEncode(nonce) + "&"
				+ OAuth.OAUTH_SIGNATURE_METHOD + "="
				+ OAuth.percentEncode("HMAC-SHA1") + "&"
				+ OAuth.OAUTH_TIMESTAMP + "=" + ts + "&" + OAuth.OAUTH_TOKEN
				+ "=" + OAUTH_TOKEN + "&" + OAuth.OAUTH_VERIFIER + "="
				+ OAUTH_VERIFIER + "&" + OAuth.OAUTH_VERSION + "=" + "1.0";

		String baseString = "POST&" + OAuth.percentEncode(ACCESS_TOKEN_URL)
				+ "&" + OAuth.percentEncode(plain);

		String header = "OAuth " + OAuth.OAUTH_CONSUMER_KEY + "="
				+ getConsumerKey() + "," + OAuth.OAUTH_NONCE + "=" + nonce
				+ "," + OAuth.OAUTH_SIGNATURE_METHOD + "=" + "HMAC-SHA1" + ","
				+ OAuth.OAUTH_TIMESTAMP + "=" + ts + "," + OAuth.OAUTH_TOKEN
				+ "=" + OAUTH_TOKEN + "," + OAuth.OAUTH_VERIFIER + "="
				+ OAUTH_VERIFIER + "," + OAuth.OAUTH_VERSION + "=" + "1.0"
				+ "," + OAuth.OAUTH_SIGNATURE + "=" + getSignature(baseString);

		post.addHeader("Authorization", header);

		HttpResponse response = httpClient.execute(post);

		String body = EntityUtils.toString(response.getEntity());

		String[] accessToken = body.split("&");
		this.OAUTH_ACCESS_TOKEN = accessToken[0].substring(OAuth.OAUTH_TOKEN
				.length() + 1);
		this.OAUTH_ACCESS_TOKEN_SECRET = accessToken[1]
				.substring(OAuth.OAUTH_TOKEN_SECRET.length() + 1);

		this.OAUTH_TOKEN = OAUTH_ACCESS_TOKEN;
		this.OAUTH_TOKEN_SECRET = OAUTH_ACCESS_TOKEN_SECRET;

		System.out.println("oauth_token =" + OAUTH_TOKEN);
		System.out.println("oauth_secret=" + OAUTH_TOKEN_SECRET);

	}

	private void initRequestToken() throws Exception {
		HttpPost post = new HttpPost(REQUEST_TOKEN_URL);

		String nonce = KTCredentialUtils.getNonce();
		String ts = KTCredentialUtils.getTimestamp();

		String plain = OAuth.OAUTH_CALLBACK + "="
				+ OAuth.percentEncode(callback) + "&"
				+ OAuth.OAUTH_CONSUMER_KEY + "="
				+ OAuth.percentEncode(getConsumerKey()) + "&"
				+ OAuth.OAUTH_NONCE + "=" + OAuth.percentEncode(nonce) + "&"
				+ OAuth.OAUTH_SIGNATURE_METHOD + "="
				+ OAuth.percentEncode("HMAC-SHA1") + "&"
				+ OAuth.OAUTH_TIMESTAMP + "=" + ts + "&" + OAuth.OAUTH_VERSION
				+ "=" + "1.0";

		String baseString = "POST&" + OAuth.percentEncode(REQUEST_TOKEN_URL)
				+ "&" + OAuth.percentEncode(plain);
		System.out.println(baseString);

		String header = "OAuth " + OAuth.OAUTH_CALLBACK + "=" + "" + callback
				+ "" + "," + OAuth.OAUTH_CONSUMER_KEY + "=" + getConsumerKey()
				+ "," + OAuth.OAUTH_NONCE + "=" + nonce + ","
				+ OAuth.OAUTH_SIGNATURE_METHOD + "=" + "HMAC-SHA1" + ","
				+ OAuth.OAUTH_TIMESTAMP + "=" + ts + "," + OAuth.OAUTH_VERSION
				+ "=" + "1.0" + "," + OAuth.OAUTH_SIGNATURE + "="
				+ getSignature(baseString);

		System.out.println(header);

		post.addHeader("Authorization", header);

		HttpResponse response = null;

		response = httpClient.execute(post);

		System.out.println(response.getStatusLine());

		String body = EntityUtils.toString(response.getEntity());
		// System.out.println(body);

		String[] tokens = body.split("&");
		this.OAUTH_TOKEN = tokens[0].substring(OAuth.OAUTH_TOKEN.length() + 1);

		this.OAUTH_TOKEN_SECRET = tokens[1].substring(OAuth.OAUTH_TOKEN_SECRET
				.length() + 1);
		// System.out.println(OAUTH_TOKEN+" "+OAUTH_TOKEN_SECRET);

	}

	private String getSignature(String baseString) {
		return Base64.encodeBytes(computeSignature(baseString));

	}

	private String getConsumerSecret() {
		return consumerSecret;

	}

	private byte[] computeSignature(String baseString) {

		SecretKey key = null;

		String keyString = OAuth.percentEncode(getConsumerSecret()) + '&'
				+ OAuth.percentEncode(getTokenSecret());
		byte[] keyBytes = keyString.getBytes();
		key = new SecretKeySpec(keyBytes, MAC_NAME);
		Mac mac = null;
		try {
			mac = Mac.getInstance(MAC_NAME);
			mac.init(key);
		} catch (Exception e) {
			e.printStackTrace();

		}
		return mac.doFinal(baseString.getBytes());

	}

	public void init(String consumerKey, String consumerSecret,
			String callback, String loginid, String password) throws Exception {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.callback = callback;
		this.loginid = loginid;
		this.password = password;

		initRequestToken();
		authorize();
		initAccessToken();

	}

	private void authorize() throws Exception {

		HttpPost post = new HttpPost(
				"https://openapi.ucloud.com/ucloud/oauth/1.0a/authorize");

		List<NameValuePair> formparams = new ArrayList<>();

		formparams.add(new BasicNameValuePair("oauth_token", OAUTH_TOKEN));

		formparams.add(new BasicNameValuePair("loginid", loginid));
		formparams.add(new BasicNameValuePair("password", password));

		UrlEncodedFormEntity body = new UrlEncodedFormEntity(formparams);
		post.setEntity(body);

		HttpResponse response = httpClient.execute(post);

		String entity = EntityUtils.toString(response.getEntity());

		int index = entity.indexOf("oauth_verifier");
		OAUTH_VERIFIER = entity.substring(index + "oauth_verifier=".length(),
				index + "oauth_verifier=".length() + 20);
		// System.out.println(OAUTH_TOKEN);
		// System.out.println(OAUTH_TOKEN_SECRET);
		//
		// System.out.println(OAUTH_VERIFIER);

	}

}
