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
import edu.isi.misd.scanner.client.RegistryClient;
import edu.isi.misd.scanner.client.RegistryClientResponse;
import edu.isi.misd.scanner.client.TagfilerClient;
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
		RegistryClient registryClient = (RegistryClient) session.getAttribute("registryClient");
		if (action.equals("getStudies")) {
			RegistryClientResponse clientResponse = registryClient.getStudies(cookie);
			String ret = clientResponse.toStudies();
			System.out.println("Get Studies:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getDatasets")) {
			String study = request.getParameter("study");
			RegistryClientResponse clientResponse = registryClient.getDatasets(study, cookie);
			String ret = clientResponse.toDatasets();
			System.out.println("Get Datasets: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getLibraries")) {
			String dataset = request.getParameter("dataset");
			RegistryClientResponse clientResponse = registryClient.getLibraries(dataset, cookie);
			String ret = clientResponse.toLibraries();
			System.out.println("Get Libraries: "+ret.toString());
			PrintWriter out = response.getWriter();
			out.print(ret.toString());
		} else if (action.equals("getFunctions")) {
			String lib = request.getParameter("lib");
			RegistryClientResponse clientResponse = registryClient.getFunctions(lib, cookie);
			String ret = clientResponse.toFunctions();
			System.out.println("Get Functions: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getParameters")) {
			String func = request.getParameter("func");
			RegistryClientResponse clientResponse = registryClient.getParameters(func, cookie);
			String ret = clientResponse.toParameters();
			System.out.println("Get Parameters:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
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
					RegistryClient registryClient = new TagfilerClient(httpClient, tagfilerURL);
					session.setAttribute("registryClient", registryClient);
				} else {
					// to handle this case
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
					RegistryClient registryClient = (RegistryClient) session.getAttribute("registryClient");
					String params = request.getParameter("params");
					String lib = request.getParameter("lib");
					String func = request.getParameter("func");
					String cookie = (String) session.getAttribute("tagfilerCookie");
					RegistryClientResponse clientResponse = registryClient.getFunction(func, cookie);
					String res = clientResponse.toFunction();
					JSONObject temp = (new JSONArray(res)).getJSONObject(0);
					String master = temp.getString("resourceMaster");
					String funcPath = temp.getString("resourcePath");
					clientResponse = registryClient.getLibrary(lib, cookie);
					res = clientResponse.toLibrary();
					temp = (new JSONArray(res)).getJSONObject(0);
					String libPath = temp.getString("resourcePath");
					clientResponse = registryClient.getMaster(master, cookie);
					res = clientResponse.toMaster();
					temp = (new JSONArray(res)).getJSONObject(0);
					String masterURL = temp.getString("resourceURL");
					clientResponse = registryClient.getWorkers(master, cookie);
					res = clientResponse.toWorkers();
					StringBuffer buff = new StringBuffer();
					JSONArray targets= new JSONArray(res);
					for (int i=0; i < targets.length(); i++) {
						if (i > 0) {
							buff.append(",");
						}
						temp = targets.getJSONObject(i);
						String dataSource = temp.getString("resourceData");
						buff.append(temp.getString("resourceURL")).append("/dataset/").append(libPath).append("/").append(funcPath).append("?dataSource=").append(Utils.urlEncode(dataSource));
					}
					String targetsURLs = buff.toString();
					buff = new StringBuffer();
					buff.append(masterURL).append("/query/").append(libPath).append("/").append(funcPath);
					String url = buff.toString();
					System.out.println("URL: " + url + "\nTargets: "+targetsURLs+"\nParams: "+params);
					ClientURLResponse rsp = httpClient.postScannerQuery(url, targetsURLs, params);
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
