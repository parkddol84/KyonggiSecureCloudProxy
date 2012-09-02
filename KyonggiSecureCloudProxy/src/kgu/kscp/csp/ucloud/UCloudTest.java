package kgu.kscp.csp.ucloud;

public class UCloudTest {

	public static void main(String[] args) throws Exception {
		System.out.println("Started...");
		String id = "";
		String pw = "";
		UCloudProvider up = new UCloudProvider(id, pw);
		up.getUserInfo();
		//up.uploadFile("", "RACS.pptx");
		up.downloadFile("", "sj.jpg");
		
		System.out.println("Ended...");

	}

}
