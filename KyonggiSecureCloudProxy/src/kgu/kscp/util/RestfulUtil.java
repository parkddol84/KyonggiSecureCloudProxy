package kgu.kscp.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import kgu.kscp.csp.box.Constant;
import kgu.kscp.csp.box.FileInfo;

import org.apache.http.HttpEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class RestfulUtil {
	private static final int BUFSIZE = 4096;

	public static boolean toFileFromEntity(String fileName, HttpEntity entity,
			String charset) throws Exception {
		long length = entity.getContentLength();
		InputStream inputStream = entity.getContent();

		File getFile = new File(fileName);
		FileOutputStream fileOutputStream = new FileOutputStream(getFile);

		byte[] buf = new byte[BUFSIZE];
		int len = 0;
		while ((len = inputStream.read(buf)) != -1) {
			fileOutputStream.write(buf, 0, len);
		}
		inputStream.close();
		fileOutputStream.close();

		if (getFile.exists() && getFile.length() == length)
			return true;
		else
			return false;
	}

	public static String toStringFromInputStream(InputStream inputStream,
			String charset) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buf = new byte[BUFSIZE];
		int len = 0;
		while ((len = inputStream.read(buf)) != -1) {
			bout.write(buf, 0, len);
		}
		inputStream.close();
		bout.close();
		return new String(bout.toByteArray(), Charset.forName(charset));
	}

	public static JSONObject getJsonObjectFromJsonText(String jsonText)
			throws ParseException {
		// JSON Lib : http://code.google.com/p/json-simple/
		return (JSONObject) new JSONParser().parse(jsonText);

	}

	public static ArrayList<FileInfo> getFileListFromJsonText(String jsonText)
			throws Exception {

		JSONObject jsonObj = getJsonObjectFromJsonText(jsonText);
		jsonObj = (JSONObject) jsonObj
				.get(Constant.FORDLER_ATTRIBUTES_ITEM_COLLECTION);
		JSONArray jsonArray = (JSONArray) jsonObj
				.get(Constant.FORDLER_ATTRIBUTES_ENTRIES);
		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
		for (int i = 0; i < jsonArray.size(); ++i) {
			JSONObject entrie = (JSONObject) jsonArray.get(i);
			String type = (String) entrie.get(Constant.FILE_ATTRIBUTES_TYPE);
			String id = (String) entrie.get(Constant.FILE_ATTRIBUTES_ID);
			String name = (String) entrie.get(Constant.FILE_ATTRIBUTES_NAME);
			fileList.add(new FileInfo(type, id, name));
			System.out.println("[type:" + type + "][id:" + id + "][name:"
					+ name + "]");
		}
		return fileList;
	}

	public static Object getJsonFromJSONObject(JSONObject jsonObject, String key) {
		return jsonObject.get(key);
	}

	public static List<String> getNodeValuesFromXML(String xml,
			String xpathExpression) throws XPathExpressionException {
		Document document = parseXml(xml);
		XPath xPath = XPathFactory.newInstance().newXPath();
		XPathExpression xPathExpression = xPath.compile(xpathExpression);
		Object result = xPathExpression.evaluate(document,
				XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		List<String> nodeValues = new ArrayList<String>();
		for (int i = 0; i < nodes.getLength(); i++) {
			nodeValues.add(nodes.item(i).getNodeValue());
		}
		return nodeValues;
	}

	private static Document parseXml(String xmlString) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			InputSource inputSource = new InputSource(new StringReader(
					xmlString));
			return documentBuilder.parse(inputSource);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
