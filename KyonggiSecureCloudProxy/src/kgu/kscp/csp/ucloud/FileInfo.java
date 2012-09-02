package kgu.kscp.csp.ucloud;

public class FileInfo {
	private String filename;
	private String fileID;
	 
	public FileInfo(String fileID,String filename){
		this.fileID = fileID;
		this.filename = filename;
	}
	public String getFileName(){
		return filename;
	}
	public String getFileID(){
		return fileID;
	}
}
