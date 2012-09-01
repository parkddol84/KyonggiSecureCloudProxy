package kgu.kscp.csp.box;


public class Constant {
	protected static final String APIKEY = "ul8lgel8236ikn1i7682y46doi854mco";
	
	public static final String FORDLER_ATTRIBUTES_ITEM_COLLECTION = "item_collection";
	public static final String FORDLER_ATTRIBUTES_ENTRIES = "entries";
	public static final String FORDLER_ATTRIBUTES_TOTAL_COUNT = "total_count";
	public static final String FILE_ATTRIBUTES_TYPE= "type";
	public static final String FILE_ATTRIBUTES_ID= "id";
	public static final String FILE_ATTRIBUTES_NAME= "name";
		
	public static final String HEADER_AUTHORIZATION_NAME = "Authorization";
	public static final String HEADER_AUTHORIZATION_VALUE_TEMPLATE = "BoxAuth api_key="+Constant.APIKEY+"&auth_token=%s";
}
