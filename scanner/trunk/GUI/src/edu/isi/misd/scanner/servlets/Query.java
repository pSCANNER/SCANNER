package edu.isi.misd.scanner.servlets;

/* 
 * Copyright 2012 University of Southern California
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
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
 * Servlet implementation class Query
 * 
 * @author Serban Voinea
 */

@WebServlet(description = "Query Tagfiler and SCANNER", urlPatterns = { "/query" })
public class Query extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletConfig servletConfig;
	private String tagfilerURL;
	private String tagfilerUser;
	private String tagfilerPassword;
       
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Query() {
        super();
        // TODO Auto-generated constructor stub
    }

	public void init(ServletConfig config)  throws ServletException {
		super.init(config);
		servletConfig = config;
		tagfilerURL = servletConfig.getInitParameter("tagfilerURL");
		tagfilerUser = servletConfig.getInitParameter("tagfilerUser");
		tagfilerPassword = servletConfig.getInitParameter("tagfilerPassword");
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
		if (action.equals("getStudies")) {
			String url = tagfilerURL + "/query/resourceType=study(resourceName)";
			ClientURLResponse rsp = httpClient.get(url, cookie);
			String res = rsp.getEntityString();
			System.out.println("Get Studies:\n"+res);
			PrintWriter out = response.getWriter();
			out.print(res);
		} else if (action.equals("getDatasets")) {
			String study = request.getParameter("study");
			String url = tagfilerURL + "/query/resourceType=study;resourceName=" + Utils.urlEncode(study) + "(resourceDatasets)";
			ClientURLResponse rsp = httpClient.get(url, cookie);
			String res = rsp.getEntityString();
			System.out.println("Get Datasets:\n"+res);
			PrintWriter out = response.getWriter();
			out.print(res);
		} else if (action.equals("getLibraries")) {
			try {
				String dataset = request.getParameter("dataset");
				String url = tagfilerURL + "/query/resourceType=dataset;resourceName=" + Utils.urlEncode(dataset) + "(resourceLibraries)";
				ClientURLResponse rsp = httpClient.get(url, cookie);
				String res = rsp.getEntityString();
				System.out.println("Get Libraries:\n"+res);
				JSONArray arr;
				arr = new JSONArray(res);
				JSONObject obj = arr.getJSONObject(0);
				JSONArray libraries = obj.getJSONArray("resourceLibraries");
				JSONObject ret = new JSONObject();
				for (int i=0; i < libraries.length(); i++) {
					String libraryName = libraries.getString(i);
					url = tagfilerURL + "/query/resourceType=library;resourceName=" + Utils.urlEncode(libraryName) + "(resourceDisplayName)";
					rsp = httpClient.get(url, cookie);
					res = rsp.getEntityString();
					arr = new JSONArray(res);
					obj = arr.getJSONObject(0);
					String displayName = obj.getString("resourceDisplayName");
					ret.put(libraryName, displayName);
				}
				System.out.println("Get Libraries: "+ret.toString());
				PrintWriter out = response.getWriter();
				out.print(ret.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("getFunctions")) {
			try {
				String lib = request.getParameter("lib");
				String url = tagfilerURL + "/query/resourceType=library;resourceName=" + Utils.urlEncode(lib) + "(resourceFunctions)";
				ClientURLResponse rsp = httpClient.get(url, cookie);
				String res = rsp.getEntityString();
				JSONArray arr = new JSONArray(res);
				JSONObject obj = arr.getJSONObject(0);
				JSONArray functions = obj.getJSONArray("resourceFunctions");
				JSONObject ret = new JSONObject();
				for (int i=0; i < functions.length(); i++) {
					String functionName = functions.getString(i);
					url = tagfilerURL + "/query/resourceType=function;resourceName=" + Utils.urlEncode(functionName) + "(resourceDisplayName)";
					rsp = httpClient.get(url, cookie);
					res = rsp.getEntityString();
					arr = new JSONArray(res);
					obj = arr.getJSONObject(0);
					String displayName = obj.getString("resourceDisplayName");
					ret.put(functionName, displayName);
				}
				System.out.println("Get Functions: "+ret.toString());
				PrintWriter out = response.getWriter();
				out.print(ret.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("getParameters")) {
			try {
				String func = request.getParameter("func");
				String url = tagfilerURL + "/query/resourceType=function;resourceName=" + Utils.urlEncode(func) + "(resourceParameters)";
				System.out.println("Get parameters url: "+url);
				ClientURLResponse rsp = httpClient.get(url, cookie);
				String res = rsp.getEntityString();
				JSONArray arr = new JSONArray(res);
				JSONArray params = arr.getJSONObject(0).getJSONArray("resourceParameters");
				JSONArray ret = new JSONArray();
				for (int i=0; i < params.length(); i++) {
					String param = params.getString(i);
					url = tagfilerURL + "/query/resourceType=parameter;resourceName=" + Utils.urlEncode(param) + "(resourceDisplayName;resourceMinOccurs;resourceMaxOccurs;resourceValues)";
					rsp = httpClient.get(url, cookie);
					res = rsp.getEntityString();
					JSONArray temp = new JSONArray(res);
					JSONObject obj = new JSONObject();
					obj.put(param, temp.getJSONObject(0));
					ret.put(obj);
				}
				System.out.println("Get Parameters:\n"+ret.toString());
				PrintWriter out = response.getWriter();
				out.print(ret.toString());
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
		JSONObject obj = new JSONObject();
		try {
			String action = request.getParameter("action");
			HttpSession session = request.getSession(false);
			JakartaClient httpClient = (JakartaClient) session.getAttribute("httpClient");
			if (action.equals("loginRegistry")) {
				obj.put("status", "login error");
				ClientURLResponse rsp = httpClient.login(tagfilerURL + "/session", tagfilerUser, tagfilerPassword);
				if (rsp != null) {
					//rsp.debug();
					session.setAttribute("tagfilerCookie", httpClient.getCookieValue());
				} else {
					System.out.println("Response is null");
				}
				obj.put("status", "success");
			} else if (action.equals("file")) {
				obj.put("status", "download error");
				String cookie = (String) session.getAttribute("tagfilerCookie");
				httpClient.setCookieValue(cookie);
				ClientURLResponse rsp = httpClient.downloadFile(tagfilerURL + "/file/name=OceanTypes", cookie);
				if (rsp != null) {
					if (httpClient.getCookieValue() != null) {
						session.setAttribute("tagfilerCookie", httpClient.getCookieValue());
					}
					String res = rsp.getEntityString();
					System.out.println("The file content is:\n"+res);
					obj.put("status", "success");
				}
			} else if (action.equals("logout")) {
				obj.put("status", "logout error");
				String cookie = (String) session.getAttribute("tagfilerCookie");
				httpClient.setCookieValue(cookie);
				ClientURLResponse rsp = httpClient.delete(tagfilerURL + "/session", cookie);
				if (rsp != null) {
					session.setAttribute("tagfilerCookie", null);
					System.out.println("Logout successfully");
					obj.put("status", "success");
				}
			} else if (action.equals("getResults")) {
				try {
					String params = request.getParameter("params");
					String lib = request.getParameter("lib");
					String func = request.getParameter("func");
					String cookie = (String) session.getAttribute("tagfilerCookie");
					httpClient.setCookieValue(cookie);
					ClientURLResponse rsp = httpClient.get(tagfilerURL + "/query/resourceType=function;resourceName=" + Utils.urlEncode(func)  + "(resourceMaster)", cookie);
					String res = rsp.getEntityString();
					JSONObject temp = (new JSONArray(res)).getJSONObject(0);
					String master = temp.getString("resourceMaster");
					System.out.println("Master: " + master);
					rsp = httpClient.get(tagfilerURL + "/query/resourceType=master;resourceName=" + Utils.urlEncode(master)  + "(resourceWorkers;resourceURL)", cookie);
					res = rsp.getEntityString();
					System.out.println("Master attributes:\n"+res);
					temp = (new JSONArray(res)).getJSONObject(0);
					JSONArray targets = temp.getJSONArray("resourceWorkers");
					String masterURL = temp.getString("resourceURL");
					
					rsp = httpClient.get(tagfilerURL + "/query/resourceType=library;resourceName=" + Utils.urlEncode(lib)  + "(resourcePath)", cookie);
					res = rsp.getEntityString();
					temp = (new JSONArray(res)).getJSONObject(0);
					String libPath = temp.getString("resourcePath");

					rsp = httpClient.get(tagfilerURL + "/query/resourceType=function;resourceName=" + Utils.urlEncode(func)  + "(resourcePath)", cookie);
					res = rsp.getEntityString();
					temp = (new JSONArray(res)).getJSONObject(0);
					String funcPath = temp.getString("resourcePath");
					
					StringBuffer buff = new StringBuffer();
					
					for (int i=0; i < targets.length(); i++) {
						if (i > 0) {
							buff.append(",");
						}
						rsp = httpClient.get(tagfilerURL + "/query/resourceType=worker;resourceName=" + Utils.urlEncode(targets.getString(i))  + "(resourceData;resourceURL)", cookie);
						res = rsp.getEntityString();
						temp = (new JSONArray(res)).getJSONObject(0);
						String dataSource = temp.getString("resourceData");
						buff.append(temp.getString("resourceURL")).append("/dataset/").append(libPath).append("/").append(funcPath).append("?dataSource=").append(Utils.urlEncode(dataSource));
					}
					
					String targetsURLs = buff.toString();
					buff = new StringBuffer();
					buff.append(masterURL).append("/query/").append(libPath).append("/").append(funcPath);
					String url = buff.toString();
					
					System.out.println("URL: " + url + "\nTargets: "+targetsURLs+"\nParams: "+params);

					rsp = httpClient.postScannerQuery(url, targetsURLs, params);
					res = rsp.getEntityString();
					System.out.println("Response Body: \n"+res);

					PrintWriter out = response.getWriter();
					out.print(res);
					return;
					// simulated response
					//obj = new JSONObject(Utils.oceansResult);
					//PrintWriter out = response.getWriter();
					//out.print(obj.toString());
					//return;
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
