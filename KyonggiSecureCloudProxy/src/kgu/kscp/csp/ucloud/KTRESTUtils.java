package kgu.kscp.csp.ucloud;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class KTRESTUtils {
	public static JSONObject getJsonObjectFromJsonText(String jsonText) throws ParseException{
		  return (JSONObject)new JSONParser().parse(jsonText);
	 }
	
	 public static ArrayList<FileInfo> getFileListFromJsonText(String jsonText) throws Exception{
		  JSONObject jsonObj = getJsonObjectFromJsonText(jsonText);
		  JSONArray jsonArray = (JSONArray)jsonObj.get("Files");
		  ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
		  for(int i=0;i<jsonArray.size();++i){
			  JSONObject entrie = (JSONObject)jsonArray.get(i);
			  String id = (String)entrie.get("file_id");
			  String name = (String)entrie.get("file_name");
			  fileList.add(new FileInfo(id, name));
		  }
		  return fileList;
	 }

}
