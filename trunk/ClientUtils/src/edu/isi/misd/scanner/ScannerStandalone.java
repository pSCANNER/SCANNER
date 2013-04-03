/**
 * 
 */
package edu.isi.misd.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author serban
 *
 */
public class ScannerStandalone {

	static String textSimple[] = {"path", "study", "dataset", "method", "rtype", "rURL", "datasource", "rpath", "cname", 
		"site", "description", "title", "email", "phone", "website", "address", "agreement", "contact", "approvals"};
	static String textMultiple[] = {"library", "values", "users", "variables"};
	static String intSimple[] = {"minOccurs", "maxOccurs"};
	static String curlPrefix = "curl  -b cookiefile -c cookiefile -s -S -k -H \"Accept: text/uri-list\" -d \"action=post\" \"";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// TODO Auto-generated method stub
			System.out.println("#!/bin/sh\n");
			System.out.println("./login_VM.sh\n");
			String host = args[0];
			String file = args[1];
			String url = "https://" + host + "/tagfiler";
			for (int i=0; i < textSimple.length; i++) {
				genTagdef(url, textSimple[i], "text", "false");
			}
			for (int i=0; i < textMultiple.length; i++) {
				genTagdef(url, textMultiple[i], "text", "true");
			}
			for (int i=0; i < intSimple.length; i++) {
				genTagdef(url, intSimple[i], "int8", "false");
			}
			System.out.println("");
			StringBuffer buff = new StringBuffer();
			buff.append("curl  -b cookiefile -c cookiefile -s -S -k -H \"Accept: text/uri-list\" -d \"action=post\" \"").append(url).append("/subject/?");
			String read_users = URLEncoder.encode("read users", "UTF-8");
			String write_users = URLEncoder.encode("write users", "UTF-8");
			buff.append(read_users).append("=*&").append(write_users).append("=*");
			curlPrefix = buff.toString();
			readFile(file);
			System.out.println("");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static void genTagdef(String url, String name, String type, String multivalue) {
		StringBuffer buff = new StringBuffer();
		buff.append("curl  -b cookiefile -c cookiefile -s -S -k ");
		buff.append("-d \"action=add\" ");
		buff.append("-d \"tag-1=" + name + "\" ");
		buff.append("-d \"type-1=" + type + "\" ");
		buff.append("-d \"multivalue-1=" + multivalue + "\" ");
		buff.append("-d \"readpolicy-1=anonymous\" ");
		buff.append("-d \"writepolicy-1=anonymous\" ");
		buff.append("-d \"unique-1=false\" ");
		buff.append("\"").append(url).append("/tagdef\"");
		System.out.println(buff.toString());
	}

	public static void readFile(String file) {
		try {
			File jsonFile = new File(file);
			StringBuffer buff = new StringBuffer();
			BufferedReader input =  new BufferedReader(new FileReader(jsonFile));
			String line = null;
			while (( line = input.readLine()) != null) {
				buff.append(line).append(System.getProperty("line.separator"));
			}
			input.close();
			JSONArray arr = new JSONArray(buff.toString());
			for (int i=0; i < arr.length(); i++) {
				buff = new StringBuffer(curlPrefix);
				JSONObject obj = arr.getJSONObject(i);
				JSONArray names = obj.names();
				for (int j=0; j < names.length(); j++) {
					String key = names.getString(j);
					buff.append("&").append(URLEncoder.encode(key, "UTF-8")).append("=");
					if (key.equals("values") || key.equals("users") || key.equals("library") || key.equals("variables")) {
						genArrayValues(obj.getJSONArray(key), buff);
					} else {
						buff.append(URLEncoder.encode(obj.getString(key), "UTF-8"));
					}
				}
				buff.append("\"");
				System.out.println(buff.toString());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void genArrayValues(JSONArray arr, StringBuffer buff) {
		try {
			for (int i=0; i < arr.length(); i++) {
				if (i > 0) {
					buff.append(",");
				}
				String value = URLEncoder.encode(arr.getString(i), "UTF-8");
				buff.append(value);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
