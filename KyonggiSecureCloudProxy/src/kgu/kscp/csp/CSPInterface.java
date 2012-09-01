package kgu.kscp.csp;


public interface CSPInterface {
	public String requestAccessToken(String loginId,String passsword)		throws Exception;
	public boolean downloadFile(String localDownloadPath, String fileName)	throws Exception;
	public boolean uploadFile(String localUploadFile,String fileName) 		throws Exception;
	public void viewRootList()												throws Exception;
}
