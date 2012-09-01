package kgu.kscp.csp.sugarsync;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import kgu.kscp.csp.CSPInterface;

import com.sugarsync.sample.auth.AccessToken;
import com.sugarsync.sample.auth.RefreshToken;
import com.sugarsync.sample.file.FileCreation;
import com.sugarsync.sample.file.FileDownloadAPI;
import com.sugarsync.sample.file.FileUploadAPI;
import com.sugarsync.sample.userinfo.UserInfo;
import com.sugarsync.sample.util.HttpResponse;
import com.sugarsync.sample.util.SugarSyncHTTPGetUtil;
import com.sugarsync.sample.util.XmlUtil;


public final class SugarSyncProvider implements CSPInterface{
	
	private String mAccessToken;
	public SugarSyncProvider(String id,String pw){
		try{
			mAccessToken = requestAccessToken(id,pw);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public String requestAccessToken(String loginId, String passsword)
			throws Exception {
        HttpResponse httpResponse = AccessToken.getAccessTokenResponse(SugarSyncConstant.ACCESSKEY, SugarSyncConstant.PRIVATEKEY, getRefreshToken(loginId,passsword));

        if (httpResponse.getHttpStatusCode() > 299) {
            System.out.println("Error while getting access token!");
            throw new Exception();
        }
        
        return httpResponse.getHeader("Location").getValue();
        
        
	}
	
	private String getRefreshToken(String loginId, String passsword) throws Exception{
		HttpResponse httpResponse = null;
		httpResponse = RefreshToken.getAuthorizationResponse(loginId, passsword, SugarSyncConstant.APPLICATION, SugarSyncConstant.ACCESSKEY, SugarSyncConstant.PRIVATEKEY);
		if(httpResponse.getHttpStatusCode() > 299) {
			System.out.println("Error while getting refresh token!");
			throw new Exception();
		}
		return httpResponse.getHeader("Location").getValue();
	}
	
	
	@Override
	public boolean downloadFile(String localDownloadPath, String fileName) throws Exception {
        HttpResponse magicBriefcaseContents = getMagicBriefcaseFolderContents(mAccessToken);
        List<String> fileDataLink = XmlUtil.getNodeValues(magicBriefcaseContents.getResponseBody(),
                "collectionContents/file[displayName=\"" + fileName + "\"]/fileData/text()");
        if (fileDataLink.size() == 0) {
            System.out.println("\nFile " + fileName + " not found in Magic Briefcase folder");
            System.exit(0);
        }
        HttpResponse fileDownloadResponse = FileDownloadAPI.downloadFileData(fileDataLink.get(0), localDownloadPath+fileName, mAccessToken);
        validateHttpResponse(fileDownloadResponse);

        System.out.println("\nDownload completed successfully. The " + fileName
                + " from \"Magic Briefcase\" was downloaded to the local directory.");
		return true;
	}
	
	@Override
	public boolean uploadFile(String localUploadPath, String fileName) throws Exception {
        if (!(new File(localUploadPath+fileName).exists())) {
            System.out.println("\nFile " + localUploadPath+fileName + "  doesn not exists in the current directory");
            System.exit(0);
        }
        HttpResponse userInfoResponse = getUserInfo(mAccessToken);

        String magicBriefcaseFolderLink = XmlUtil.getNodeValues(userInfoResponse.getResponseBody(),"/user/magicBriefcase/text()").get(0);

        HttpResponse resp = FileCreation.createFile(magicBriefcaseFolderLink, fileName, "", mAccessToken);
        String fileDataUrl = resp.getHeader("Location").getValue() + "/data";
        resp = FileUploadAPI.uploadFile(fileDataUrl, localUploadPath+fileName, mAccessToken);

        System.out.println("\nUpload completed successfully. Check \"Magic Briefcase\" remote folder");
		return true;
	}
	
	@Override
	public void viewRootList() throws Exception {
		HttpResponse folderContentsResponse = getMagicBriefcaseFolderContents(mAccessToken);
		String response = folderContentsResponse.getResponseBody();
		printFolderContents(response);
	}
	
    private void printFolderContents(String responseBody) {
        try {
            List<String> folderNames = XmlUtil.getNodeValues(responseBody,
                    "/collectionContents/collection[@type=\"folder\"]/displayName/text()");
            List<String> fileNames = XmlUtil.getNodeValues(responseBody,
                    "/collectionContents/file/displayName/text()");
            
            System.out.println("\n-Magic Briefcase");
            System.out.println("\t-Folders:");
            for (String folder : folderNames) {
                System.out.println("\t\t" + folder);
            }
            System.out.println("\t-Files:");
            for (String file : fileNames) {
                System.out.println("\t\t" + file);
            }
        } catch (XPathExpressionException e1) {
            System.out.println("Error while printing the folder contents:");
            System.out.println(responseBody);
        }
    }

    private static HttpResponse getMagicBriefcaseFolderContents(String accessToken) throws IOException, XPathExpressionException {
        HttpResponse folderRepresentationResponse = getMagicBriefcaseFolderRepresentation(accessToken);
        validateHttpResponse(folderRepresentationResponse);
        String magicBriefcaseFolderContentsLink = XmlUtil.getNodeValues(
                folderRepresentationResponse.getResponseBody(), "/folder/contents/text()").get(0);
        HttpResponse folderContentsResponse = SugarSyncHTTPGetUtil.getRequest(magicBriefcaseFolderContentsLink, accessToken);
        validateHttpResponse(folderContentsResponse);
        return folderContentsResponse;
    }
    
    private static HttpResponse getMagicBriefcaseFolderRepresentation(String accessToken)
            throws IOException, XPathExpressionException {
        HttpResponse userInfoResponse = getUserInfo(accessToken);

        // get the magicBriefcase folder representation link
        String magicBriefcaseFolderLink = XmlUtil.getNodeValues(userInfoResponse.getResponseBody(), "/user/magicBriefcase/text()").get(0);
        
        // make a HTTP GET to the link extracted from user info
        HttpResponse folderRepresentationResponse = SugarSyncHTTPGetUtil.getRequest(magicBriefcaseFolderLink, accessToken);
        validateHttpResponse(folderRepresentationResponse);

        return folderRepresentationResponse;
    }
    
    private static HttpResponse getUserInfo(String accessToken) throws IOException {
        HttpResponse httpResponse = UserInfo.getUserInfo(accessToken);
        validateHttpResponse(httpResponse);
        return httpResponse;
    }
    
    private static void validateHttpResponse(HttpResponse httpResponse) {
        if (httpResponse.getHttpStatusCode() > 299) {
            System.out.println("HTTP ERROR!");
            printResponse(httpResponse);
            System.exit(0);
        }
    }
    
    private static void printResponse(HttpResponse response) {
        System.out.println("STATUS CODE: " + response.getHttpStatusCode());
        // if the response is in xml format try to pretty format it, otherwise
        // leave it as it is
        String responseBodyString = null;
        try {
            responseBodyString = XmlUtil.formatXml(response.getResponseBody());
        } catch (Exception e) {
            responseBodyString = response.getResponseBody();
        }
        System.out.println("RESPONSE BODY:\n" + responseBodyString);
    }
}
