package kgu.kscp.csp.sugarsync;


public class SugarTest {
		
		public static void main(String[] args) throws Exception{
			String id = "";
			String pw = "";
			
			SugarSyncProvider sync = new SugarSyncProvider(id, pw);
			sync.viewRootList();
			System.out.println(sync.toString());
			boolean result=sync.uploadFile("Coding/", "encryptedRACS_k1.pptx");
			System.out.println("success: "+result);
			
			
			
			
			
			
		}

}
