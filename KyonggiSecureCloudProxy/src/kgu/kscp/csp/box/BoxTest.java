package kgu.kscp.csp.box;

public class BoxTest {
	public static void main(String[] args) throws Exception{
		String id ="";
		String pw = "";
		
		BoxProvider box = new BoxProvider(id, pw);
		boolean result = box.uploadFile("Coding/", "encryptedRACS_k2.pptx");
		System.out.println("success: "+result);
		
		
		
	}
}
