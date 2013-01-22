package edu.isi.misd.scanner.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import edu.isi.misd.scanner.client.JakartaClient;
import edu.isi.misd.scanner.client.JakartaClient.ClientURLResponse;
import edu.isi.misd.scanner.utils.Utils;

/**
 * Servlet implementation class Tagfiler
 */
@WebServlet("/tagfiler")
public class Tagfiler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Tagfiler() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = request.getParameter("action");
		HttpSession session = request.getSession(false);
		JakartaClient httpClient = (JakartaClient) session.getAttribute("httpClient");
		String cookie = (String) session.getAttribute("tagfilerCookie");
		httpClient.setCookieValue(cookie);
		if (action.equals("getDatasets")) {
			String func = request.getParameter("func");
			String url = "https://serbancentos.isi.edu/tagfiler/query/datasetType=function;datasetName=" + Utils.urlEncode(func) + "(datasetNodes)";
			ClientURLResponse rsp = httpClient.get(url, cookie);
			String res = rsp.getEntityString();
			System.out.println("url: "+url);
			System.out.println("cookie: "+cookie);
			System.out.println("Get datasets:\n"+res);
			PrintWriter out = response.getWriter();
			out.print(res);
		} else if (action.equals("getLibraries")) {
			//String dataset = request.getParameter("dataset");
			String url = "https://serbancentos.isi.edu/tagfiler/query/datasetType=library(datasetName)";
			ClientURLResponse rsp = httpClient.get(url, cookie);
			String res = rsp.getEntityString();
			System.out.println("Get libraries:\n"+res);
			PrintWriter out = response.getWriter();
			out.print(res);
		} else if (action.equals("getFunctions")) {
			try {
				String lib = request.getParameter("lib");
				String url = "https://serbancentos.isi.edu/tagfiler/query/datasetType=library;datasetName=" + Utils.urlEncode(lib) + "(datasetFunctions)";
				System.out.println("Functions URL: "+url);
				ClientURLResponse rsp = httpClient.get(url, cookie);
				String res = rsp.getEntityString();
				System.out.println("Get functions: "+res);
				JSONArray arr = new JSONArray(res);
				JSONObject obj = arr.getJSONObject(0);
				JSONArray functions = obj.getJSONArray("datasetFunctions");
				JSONObject ret = new JSONObject();
				for (int i=0; i < functions.length(); i++) {
					String functionName = functions.getString(i);
					url = "https://serbancentos.isi.edu/tagfiler/query/datasetType=function;datasetName=" + Utils.urlEncode(functionName) + "(datasetDisplayName)";
					System.out.println("Functions Display URL: "+url);
					rsp = httpClient.get(url, cookie);
					res = rsp.getEntityString();
					System.out.println("Get functions display result: "+res);
					arr = new JSONArray(res);
					obj = arr.getJSONObject(0);
					String displayName = obj.getString("datasetDisplayName");
					ret.put(functionName, displayName);
				}
				System.out.println("Returned value: "+ret.toString());
				PrintWriter out = response.getWriter();
				out.print(ret.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("getDataSources")) {
			String func = request.getParameter("func");
			String url = "https://serbancentos.isi.edu/tagfiler/query/datasetType=function;datasetName=" + Utils.urlEncode(func) + "(datasetSources)";
			System.out.println("Get getDataSources url: "+url);
			ClientURLResponse rsp = httpClient.get(url, cookie);
			String res = rsp.getEntityString();
			System.out.println("Get getDataSources:\n"+res);
			PrintWriter out = response.getWriter();
			out.print(res);
		} else if (action.equals("getParameters")) {
			try {
				String func = request.getParameter("func");
				String url = "https://serbancentos.isi.edu/tagfiler/query/datasetType=function;datasetName=" + Utils.urlEncode(func) + "(datasetParameters)";
				ClientURLResponse rsp = httpClient.get(url, cookie);
				String res = rsp.getEntityString();
				System.out.println("Get parameters name:\n"+res);
				JSONArray arr = new JSONArray(res);
				JSONArray params = arr.getJSONObject(0).getJSONArray("datasetParameters");
				JSONArray ret = new JSONArray();
				for (int i=0; i < params.length(); i++) {
					String param = params.getString(i);
					url = "https://serbancentos.isi.edu/tagfiler/query/datasetType=parameter;datasetName=" + Utils.urlEncode(param) + "(datasetDisplayName;minOccurs;maxOccurs;parametersValues)";
					rsp = httpClient.get(url, cookie);
					res = rsp.getEntityString();
					JSONArray temp = new JSONArray(res);
					JSONObject obj = new JSONObject();
					//obj.put(param, temp.getJSONObject(0).getJSONArray("datasetContains"));
					obj.put(param, temp.getJSONObject(0));
					ret.put(obj);
					System.out.println("Get parameters:\n"+res);
				}
				System.out.println("Return results:\n"+ret.toString());
				PrintWriter out = response.getWriter();
				out.print(ret.toString());
				//JSONObject test = new JSONObject(Utils.oceansResult);
				//System.out.println("test:\n"+test.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("getResults")) {
			JSONObject test;
			try {
				test = new JSONObject(Utils.oceansResult);
				PrintWriter out = response.getWriter();
				out.print(test.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("In Tagfiler servlet");
		JSONObject obj = new JSONObject();
		try {
			String action = request.getParameter("action");
			HttpSession session = request.getSession(false);
			JakartaClient httpClient = (JakartaClient) session.getAttribute("httpClient");
			if (action.equals("login")) {
				obj.put("status", "login error");
				ClientURLResponse rsp = httpClient.login("https://serbancentos.isi.edu/tagfiler/session", "guest", "just4demo");
				if (rsp != null) {
					//rsp.debug();
					session.setAttribute("tagfilerCookie", httpClient.getCookieValue());
					//System.out.println("tagfilerCookie: "+httpClient.getCookieValue());
				} else {
					System.out.println("Response is null");
				}
				obj.put("status", "success");
			} else if (action.equals("file")) {
				obj.put("status", "download error");
				String cookie = (String) session.getAttribute("tagfilerCookie");
				httpClient.setCookieValue(cookie);
				ClientURLResponse rsp = httpClient.downloadFile("https://serbancentos.isi.edu/tagfiler/file/name=OceanTypes", cookie);
				if (rsp != null) {
					if (httpClient.getCookieValue() != null) {
						session.setAttribute("tagfilerCookie", httpClient.getCookieValue());
					}
					//System.out.println("tagfilerCookie: "+httpClient.getCookieValue());
					String res = rsp.getEntityString();
					System.out.println("The file content is:\n"+res);
					obj.put("status", "success");
				}
			} else if (action.equals("logout")) {
				obj.put("status", "logout error");
				String cookie = (String) session.getAttribute("tagfilerCookie");
				httpClient.setCookieValue(cookie);
				ClientURLResponse rsp = httpClient.delete("https://serbancentos.isi.edu/tagfiler/session", cookie);
				if (rsp != null) {
					session.setAttribute("tagfilerCookie", null);
					//System.out.println("tagfilerCookie: "+httpClient.getCookieValue());
					System.out.println("Logout successfully");
					obj.put("status", "success");
				}
			} else if (action.equals("getResults")) {
				try {
					String params = request.getParameter("params");
					System.out.println("Params:\n"+params);
					String dataset = request.getParameter("dataset");
					String lib = request.getParameter("lib");
					String func = request.getParameter("func");
					System.out.println("dataset: "+dataset+", lib: "+lib+", func: "+func);
					String cookie = (String) session.getAttribute("tagfilerCookie");
					httpClient.setCookieValue(cookie);
					ClientURLResponse rsp = httpClient.get("https://serbancentos.isi.edu/tagfiler/query/datasetType=node;datasetName=" + Utils.urlEncode(dataset)  + "(datasetWorkers;datasetURL)", cookie);
					String res = rsp.getEntityString();
					System.out.println("Get dataset attributes:\n"+res);
					JSONObject temp = (new JSONArray(res)).getJSONObject(0);
					JSONArray targets = temp.getJSONArray("datasetWorkers");
					String datasetURL = temp.getString("datasetURL");
					
					rsp = httpClient.get("https://serbancentos.isi.edu/tagfiler/query/datasetType=library;datasetName=" + Utils.urlEncode(lib)  + "(datasetPath)", cookie);
					res = rsp.getEntityString();
					temp = (new JSONArray(res)).getJSONObject(0);
					String libPath = temp.getString("datasetPath");

					rsp = httpClient.get("https://serbancentos.isi.edu/tagfiler/query/datasetType=function;datasetName=" + Utils.urlEncode(func)  + "(datasetPath)", cookie);
					res = rsp.getEntityString();
					temp = (new JSONArray(res)).getJSONObject(0);
					String funcPath = temp.getString("datasetPath");
					
					StringBuffer buff = new StringBuffer();
					
					for (int i=0; i < targets.length(); i++) {
						if (i > 0) {
							buff.append(",");
						}
						rsp = httpClient.get("https://serbancentos.isi.edu/tagfiler/query/datasetType=worker;datasetName=" + Utils.urlEncode(targets.getString(i))  + "(datasetSource;datasetURL)", cookie);
						//rsp = httpClient.get("https://serbancentos.isi.edu/tagfiler/query/datasetType=function;datasetName=" + Utils.urlEncode(func)  + "(datasetPath)", cookie);
						res = rsp.getEntityString();
						temp = (new JSONArray(res)).getJSONObject(0);
						String dataSource = temp.getString("datasetSource");
						buff.append(temp.getString("datasetURL")).append("/dataset/").append(libPath).append("/").append(funcPath).append("?dataSource=").append(Utils.urlEncode(dataSource));
					}
					
					String targetsURLs = buff.toString();
					buff = new StringBuffer();
					buff.append(datasetURL).append("/query/").append(libPath).append("/").append(funcPath);
					String url = buff.toString();
					
					System.out.println("URL: " + url + "\nTargets: "+targetsURLs+"\nParams: "+params);

					// just a hardcode test
					//targetsURLs = "http://scanner.misd.isi.edu:8888/scanner/dataset/glore/lr?dataSource=ca_part1,http://scanner.misd.isi.edu:8887/scanner/dataset/glore/lr?dataSource=ca_part2,http://scanner.misd.isi.edu:8886/scanner/dataset/glore/lr?dataSource=ca_part3";
					
					
					
					rsp = httpClient.postScannerQuery(url, targetsURLs, params);
					res = rsp.getEntityString();
					System.out.println("Real Result: \n"+res);

					PrintWriter out = response.getWriter();
					out.print(res);
					return;
					//obj = new JSONObject(Utils.oceansResult);
					//PrintWriter out = response.getWriter();
					//out.print(obj.toString());
					//return;
					//System.out.println("Result: \n"+obj.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter out = response.getWriter();
		String text = obj.toString();
		out.print(text);
	}

}
