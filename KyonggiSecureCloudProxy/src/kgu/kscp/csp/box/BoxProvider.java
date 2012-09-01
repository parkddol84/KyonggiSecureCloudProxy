package kgu.kscp.csp.box;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import kgu.kscp.csp.CSPInterface;
import kgu.kscp.util.RestfulUtil;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
public class BoxProvider implements CSPInterface {	
	private static final String CHARSET = "UTF-8";
//	private static final String HOSTNAME = "box.com";
	private static final int HTTPSPORTNUMBER = 443;
	private static final String URL_TICKET = "https://www.box.com/api/1.0/rest?action=get_ticket&api_key="+Constant.APIKEY;
	private static final String URL_AUTH_USERPAGE = "/api/1.0/auth/"; // "https://www.box.com/api/1.0/auth/";
	private static final String URL_AUTH_ACCESS_TOKEN_API = "https://www.box.com/api/1.0/rest?action=get_auth_token&api_key="+Constant.APIKEY+"&ticket=";
	private static final String URL_DOWNLOAD_FILE = "https://api.box.com/2.0/files/";
	private static final String URL_SEE_ROOTFOLDER = "https://api.box.com/2.0/folders/0";
	
	private HttpClient mClient;
	private String mAccessToken;
	public BoxProvider(String id,String pw){
		try{
			mClient = new DefaultHttpClient();
			mAccessToken = requestAccessToken(id,pw);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	@Override
	public boolean uploadFile(String localUploadPath,String fileName) throws Exception{
		String url = URL_DOWNLOAD_FILE+"data";
		PostMethod pMethod = new PostMethod(url);
		
		pMethod.addRequestHeader(Constant.HEADER_AUTHORIZATION_NAME, String.format(Constant.HEADER_AUTHORIZATION_VALUE_TEMPLATE,mAccessToken));
		Part[] parts = new Part[2];
		parts[0] = new FilePart("filename1", new File(localUploadPath+fileName));
		parts[1] = new StringPart("folder_id", "0"); 
 
		MultipartRequestEntity requestEntity = new MultipartRequestEntity(parts, pMethod.getParams());
		pMethod.setRequestEntity(requestEntity);
		org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();  
		client.executeMethod(pMethod);
		byte[] responseBody = pMethod.getResponseBody();
		String response = new String(responseBody);
		try{
			JSONObject responseJSONObject = (JSONObject)RestfulUtil.getJsonObjectFromJsonText(response);
			Long count = (Long)responseJSONObject.get(Constant.FORDLER_ATTRIBUTES_TOTAL_COUNT);
			if(count > 0)
				return true;
			else
				return false;			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean downloadFile(String localDownloadPath,String fileName) throws Exception{
		ArrayList<FileInfo> fileInfo = RestfulUtil.getFileListFromJsonText(getRootFolderInfo());
		String fileId = "";
		for(int i=0;i<fileInfo.size();++i){
			if(fileInfo.get(i).getName().equals(fileName)){
				fileId = fileInfo.get(i).getId();
				break;
			}
		}
		HttpGet getDownloadFile = new HttpGet(URL_DOWNLOAD_FILE+fileId+"/data");
		getDownloadFile.setHeader(Constant.HEADER_AUTHORIZATION_NAME, String.format(Constant.HEADER_AUTHORIZATION_VALUE_TEMPLATE,mAccessToken));
		HttpResponse response = mClient.execute(getDownloadFile);
		
		return RestfulUtil.toFileFromEntity(localDownloadPath+fileName,response.getEntity(),CHARSET);
	}
	
	@Override
	public void viewRootList() throws Exception{
		RestfulUtil.getFileListFromJsonText(getRootFolderInfo());
	}
	
	private String getRootFolderInfo() throws Exception{
		HttpGet getRootFolder = new HttpGet(URL_SEE_ROOTFOLDER);
		getRootFolder.setHeader(Constant.HEADER_AUTHORIZATION_NAME, String.format(Constant.HEADER_AUTHORIZATION_VALUE_TEMPLATE,mAccessToken));
		HttpResponse response = mClient.execute(getRootFolder);
		return RestfulUtil.toStringFromInputStream(response.getEntity().getContent(),CHARSET);
	}
	
	@Override
	public String requestAccessToken(String loginId, String passsword) throws Exception {
		String ticket = getTicket();
		
//		SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(InetAddress.getByName(HOSTNAME),HTTPSPORTNUMBER);
		SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket("74.112.184.201",HTTPSPORTNUMBER);
		
		// Send
		StringBuilder requestParams =  new StringBuilder();
		requestParams.append("login="+loginId);
		requestParams.append("&password="+passsword);
		requestParams.append("&dologin=1");
		
		BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),CHARSET));
		bufferWriter.write("POST "+ URL_AUTH_USERPAGE+ticket +" HTTP/1.0\r\n");
		bufferWriter.write("Content-Length: "+requestParams.length()+"\r\n");
		bufferWriter.write("Content-Type: application/x-www-form-urlencoded\r\n\r\n");
		bufferWriter.write(requestParams.toString());
		bufferWriter.flush();
		
		// Receive
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bufferReader.readLine();
		bufferReader.close();
		bufferWriter.close();
		
		// get AccessToken
		HttpGet getAccessToken = new HttpGet(URL_AUTH_ACCESS_TOKEN_API+ticket);
		HttpResponse response = mClient.execute(getAccessToken);
		
		String xml = RestfulUtil.toStringFromInputStream(response.getEntity().getContent(),CHARSET);
		String accessToken = RestfulUtil.getNodeValuesFromXML(xml,"response/auth_token/text()").get(0);
		System.out.println(accessToken);
		return accessToken;
	}
	
	private String getTicket() throws Exception {
		HttpGet getTicket = new HttpGet(URL_TICKET);
		HttpResponse response = mClient.execute(getTicket);
		String xml = RestfulUtil.toStringFromInputStream(response.getEntity().getContent(),CHARSET);
		String ticket = RestfulUtil.getNodeValuesFromXML(xml,"response/ticket/text()").get(0);
		return ticket;
	}
}
