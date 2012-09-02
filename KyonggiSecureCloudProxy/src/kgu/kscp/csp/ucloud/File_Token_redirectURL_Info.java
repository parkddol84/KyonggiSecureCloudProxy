package kgu.kscp.csp.ucloud;

public class File_Token_redirectURL_Info {

	private String redirectURL;

	private String fileToken;

	public String getFile_Token() {
		return fileToken;
	}

	public void setFile_Token(String fileToken) {
		this.fileToken = fileToken;
	}

	public String getRedirect_URL() {
		return redirectURL;
	}

	public void setRedirect_URL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	public File_Token_redirectURL_Info(String fileToken, String redirectURL) {
		this.fileToken = fileToken;
		this.redirectURL = redirectURL;
	}
}
