package kgu.kscp.csp.ucloud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import kgu.kscp.csp.CSPInterface;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.kt.openplatform.sdk.KTOpenApiHandler;


public class UCloudProvider implements CSPInterface {
	private String token;
	private String tokenSecret;

	private KTOpenApiHandler handler;

	private String mainfolderID = ":sc:545740:2";
	private HttpClient httpClient = new DefaultHttpClient();
	

	public UCloudProvider(String id, String pw) throws Exception {

		File file = new File("ucloud_token");

		if (!file.exists() || file.length() < 0) {
			UCloudAuthenticator authenticator = UCloudAuthenticator
					.getInstance();
			authenticator.init(UCloudConstant.AUTH_KEY,
					UCloudConstant.AUTH_SECRET, UCloudConstant.CALLBACK_URL,
					id, pw);

			token = authenticator.getToken();
			tokenSecret = authenticator.getTokenSecret();
			FileWriter fw = new FileWriter(file);
			fw.write(token + " " + tokenSecret);
			fw.close();

		} else {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String[] temp = in.readLine().split(" ");
			token = temp[0];
			tokenSecret = temp[1];
			in.close();

		}
		handler = KTOpenApiHandler.createHandler(UCloudConstant.AUTH_KEY,
				UCloudConstant.AUTH_SECRET);
		handler.setAccessToken(token, tokenSecret);

	}

	public void getUserInfo() {
		HashMap<String, String> params = new HashMap<>();
		HashMap result = handler.call(UCloudConstant.USERINFO_API, params,
				null, true);
		System.out.println("RESult =>" + result);
		System.out.println(result.size());

		Set set = result.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String obj = it.next();
			System.out.println(obj);

		}
	}

	@Override
	public String requestAccessToken(String loginId, String passsword)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	private String getMainfolderContents(String fileName) throws Exception {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("folder_id", mainfolderID);

		HashMap<?, ?> result = handler.call(
				UCloudConstant.MAINFOLDERCONTENTS_API, params, null, false);

		ArrayList<FileInfo> list = new ArrayList<FileInfo>();

		if (result != null) {
			
			String fileJsonText = result.get("Files").toString();
			System.out.println("file json text: "+fileJsonText);
			
			fileJsonText = "{\"Files\":" + fileJsonText + "}";
			System.out.println("file json text: "+fileJsonText);
			

			list= KTRESTUtils.getFileListFromJsonText(fileJsonText);

			System.out.println("=========보유 파일 정보==========");
			for (int i = 0; i < list.size(); i++) {
				System.out.println("[FILE NO." + (i + 1) + "] : "
						+ list.get(i).getFileName());
				if (list.get(i).getFileName().equals(fileName)) {
					System.out.println("[파일 " + fileName + " ]로 검색한 FILE_ID : "
							+ list.get(i).getFileID());
					System.out.println("=============================");
					return list.get(i).getFileID();
				}
			}
			System.out.println("=============================");
		}

		return null;
	}
	
	
	

	@Override
	public boolean downloadFile(String localDownloadPath, String fileName)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("============파일 다운로드==========");
		String file_Id = getMainfolderContents(fileName);
		File_Token_redirectURL_Info ftri = createFileToken(
				file_Id, "DN");
		
		
			download(ftri.getRedirect_URL(), ftri.getFile_Token(), fileName);
		
		return true;
	}

	@Override
	public boolean uploadFile(String localUploadPath, String fileName)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("============파일 업로드==========");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("folder_id", mainfolderID);
		params.put("file_name", fileName);
		String mediaType = "";

		params.put("mediaType", mediaType);
		HashMap<?, ?> result = handler.call(UCloudConstant.FILEUPLOAD_API,
				params, null, false);

		String file_id = result.get("file_id").toString();

		System.out.println("[파일 아이디] 생성 : " + file_id);

		File_Token_redirectURL_Info ftri = createFileToken(file_id, "UP");

		upload(ftri.getRedirect_URL(), ftri.getFile_Token(), fileName);
		System.out.println("=============================");

		return true;
	}
	
	private void download(String gw_url,String file_token,String fileName) throws ClientProtocolException, IOException{
		System.out.println("====[다운로드 수행]====");
		String full_url = gw_url + "?api_token=" + handler.makeApiToken() + "&file_token="+file_token;
		System.out.println(full_url);
		
		HttpGet getRequest = new HttpGet (full_url); 
		
		HttpResponse response = httpClient.execute(getRequest); 
		if ( 200 == response.getStatusLine().getStatusCode() ) 
		{ 
			//Create file
			String fileDownPath ="";
			
			System.out.println("[다운로드 할 URL] : "+fileDownPath+fileName);
		    OutputStream os = new FileOutputStream(fileDownPath+fileName);
		    BasicManagedEntity entity = (BasicManagedEntity) response.getEntity(); 
			InputStream is = entity.getContent(); 
		    byte[] buf = new byte[4096];
		    int read;
		    while ((read = is.read(buf)) != -1) {
		        os.write(buf, 0, read);
		    }
		    os.close();
		    System.out.println("[파일 다운로드 완료]");
		    System.out.println("==========================");
		} 
		else{
			System.out.println("[다운로드 실패 : "+response.getStatusLine());
		}
	}

	private void upload(String redirect_URL, String fileToken_ID,
			String fileName) throws Exception {
		System.out.println("[업로드 수행]");
		String full_url = redirect_URL + "?api_token=" + handler.makeApiToken()
				+ "&file_token=" + fileToken_ID;
		FileInputStream fis = null;
		String fileUploadPath = "";

		File file = new File(fileUploadPath + fileName);
		byte[] data = new byte[(int) file.length()];

		fis = new FileInputStream(fileUploadPath + fileName);

		fis.read(data);
		fis.close();
		
		HttpResponse response = null;
		HttpPut putRequest = new HttpPut(full_url);
		ByteArrayEntity bae = new ByteArrayEntity(data);
		putRequest.setEntity(bae);
		// 파일 업로드

		response = httpClient.execute(putRequest);

		System.out.println("[업로드 완료] : " + response.getStatusLine().toString());
	}

	private File_Token_redirectURL_Info createFileToken(String fileId,
			String mode) {

		System.out.println("[파일 토큰 생성][리다이렉트 URL 생성]");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("file_id", fileId);
		params.put("transfer_mode", mode);
		HashMap<?, ?> result = handler.call(UCloudConstant.CREATEFILETOKEN_API,
				params, null, false);
		File_Token_redirectURL_Info ftri = null;
		if (result != null) {
			String file_token = result.get("file_token").toString();
			String redirect_url = result.get("redirect_url").toString();
			ftri = new File_Token_redirectURL_Info(file_token, redirect_url);
		}
		System.out.println("[생성된 파일토큰] : " + ftri.getFile_Token());
		System.out.println("[생성된 리다이렉트 URL] : " + ftri.getRedirect_URL());
		return ftri;
	}

	@Override
	public void viewRootList() throws Exception {
		// TODO Auto-generated method stub

	}

}
