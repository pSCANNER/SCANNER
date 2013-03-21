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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

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
import edu.isi.misd.scanner.client.ScannerClient;
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
	
	private String trustStoreType;
	private String trustStorePassword;
	private String trustStoreResource;
	private String keyStoreType;
	private String keyStorePassword;
	private String keyStoreResource;
	private String keyManagerPassword;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Query() {
        super();
    }

	public void init(ServletConfig config)  throws ServletException {
		super.init(config);
		servletConfig = config;
		tagfilerURL = servletConfig.getServletContext().getInitParameter("tagfilerURL");
		tagfilerUser = servletConfig.getServletContext().getInitParameter("tagfilerUser");
		tagfilerPassword = servletConfig.getServletContext().getInitParameter("tagfilerPassword");
		trustStoreType = servletConfig.getServletContext().getInitParameter("trustStoreType");
		trustStorePassword = servletConfig.getServletContext().getInitParameter("trustStorePassword");
		trustStoreResource = servletConfig.getServletContext().getInitParameter("trustStoreResource");
		keyStoreType = servletConfig.getServletContext().getInitParameter("keyStoreType");
		keyStorePassword = servletConfig.getServletContext().getInitParameter("keyStorePassword");
		keyStoreResource = servletConfig.getServletContext().getInitParameter("keyStoreResource");
		keyManagerPassword = servletConfig.getServletContext().getInitParameter("keyManagerPassword");
		String path = servletConfig.getServletContext().getRealPath("/index.html");
		int index = path.lastIndexOf(File.separator) + 1;
		path = path.substring(0, index);
		trustStoreResource = path + trustStoreResource;
		keyStoreResource = path + keyStoreResource;
		System.out.println("trustStoreResource: " + trustStoreResource);
		System.out.println("keyStoreResource: " + keyStoreResource);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		HttpSession session = request.getSession(false);
		JakartaClient httpClient = (JakartaClient) session.getAttribute("httpClient");
		String cookie = (String) session.getAttribute("tagfilerCookie");
		httpClient.setCookieValue(cookie);
		RegistryClient registryClient = (RegistryClient) session.getAttribute("registryClient");
		if (action.equals("getStudies")) {
			RegistryClientResponse clientResponse = registryClient.getStudies();
			String ret = clientResponse.toStudies();
			System.out.println("Get Studies:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getDatasets")) {
			String study = request.getParameter("study");
			RegistryClientResponse clientResponse = registryClient.getDatasets(study);
			String ret = clientResponse.toDatasets();
			System.out.println("Get Datasets: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getLibraries")) {
			String study = request.getParameter("study");
			String dataset = request.getParameter("dataset");
			RegistryClientResponse clientResponse = registryClient.getLibraries(study, dataset);
			String ret = clientResponse.toLibraries();
			System.out.println("Get Libraries: "+ret.toString());
			PrintWriter out = response.getWriter();
			out.print(ret.toString());
		} else if (action.equals("getMethods")) {
			String study = request.getParameter("study");
			String dataset = request.getParameter("dataset");
			String lib = request.getParameter("library");
			RegistryClientResponse clientResponse = registryClient.getMethods(study, dataset, lib);
			String ret = clientResponse.toMethods();
			System.out.println("Get Methods: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getParameters")) {
			String func = request.getParameter("method");
			String lib = request.getParameter("library");
			RegistryClientResponse clientResponse = registryClient.getParameters(func, lib);
			String ret = clientResponse.toParameters();
			System.out.println("Get Parameters:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getSites")) {
			String study = request.getParameter("study");
			String dataset = request.getParameter("dataset");
			String lib = request.getParameter("library");
			String func = request.getParameter("method");
			RegistryClientResponse clientResponse = registryClient.getSites(study, dataset, lib, func);
			String ret = clientResponse.toSites();
			System.out.println("Get Sites:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject obj = new JSONObject();
		try {
			String action = request.getParameter("action");
			HttpSession session = request.getSession(false);
			JakartaClient httpClient = (JakartaClient) session.getAttribute("httpClient");
			ScannerClient scannerClient = (ScannerClient) session.getAttribute("scannerClient");
			if (action.equals("loginRegistry")) {
				obj.put("status", "login error");
				ClientURLResponse rsp = httpClient.login(tagfilerURL + "/session", tagfilerUser, tagfilerPassword);
				if (rsp != null) {
					//rsp.debug();
					session.setAttribute("tagfilerCookie", httpClient.getCookieValue());
					//RegistryClient registryClient = new TagfilerClient(httpClient, tagfilerURL, httpClient.getCookieValue(), request);
					RegistryClient registryClient = new TagfilerClient(httpClient, tagfilerURL, httpClient.getCookieValue());
					session.setAttribute("registryClient", registryClient);
					RegistryClientResponse clientResponse = registryClient.getContacts();
					String ret = clientResponse.toContacts();
					JSONArray arr = new JSONArray(ret);
					obj.put("contacts", arr);
					System.out.println("Get Contacts:\n"+ret);
				} else {
					// to handle this case
					System.out.println("Response is null");
				}
				scannerClient = new ScannerClient(4, 8192, 120000,
						trustStoreType, trustStorePassword, trustStoreResource,
						keyStoreType, keyStorePassword, keyStoreResource, keyManagerPassword);
				session.setAttribute("scannerClient", scannerClient);
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
					String params = request.getParameter("parameters");
					String sites = request.getParameter("sites");
					String lib = request.getParameter("library");
					String func = request.getParameter("method");
					String study = request.getParameter("study");
					String dataset = request.getParameter("dataset");
					RegistryClientResponse clientResponse = registryClient.getMaster();
					String res = clientResponse.toMaster();
					JSONObject temp = new JSONObject(res);
					String masterURL = temp.getString("rURL");
					clientResponse = registryClient.getMethod(func, lib);
					res = clientResponse.toMethod();
					temp = new JSONObject(res);
					String funcPath = temp.getString("rpath");
					clientResponse = registryClient.getLibrary(lib);
					res = clientResponse.toLibrary();
					temp = new JSONObject(res);
					String libPath = temp.getString("rpath");
					ArrayList<String> values = null;
					if (sites != null) {
						JSONArray arr = new JSONArray(sites);
						if (arr.length() > 0) {
							values = new ArrayList<String>();
							for (int i=0; i < arr.length(); i++) {
								values.add(arr.getString(i));
							}
						}
					}
					clientResponse = registryClient.getSite(values);
					res = clientResponse.toSite();
					HashMap<String, String> sitesMap = new HashMap<String, String>();
					JSONArray tempArray = new JSONArray(res);
					for (int i=0; i < tempArray.length(); i++) {
						temp = tempArray.getJSONObject(i);
						sitesMap.put(temp.getString("cname"), temp.getString("rURL"));
					}
					clientResponse = registryClient.getWorkers(study, dataset, lib, func, values);
					res = clientResponse.toWorkers();
					StringBuffer buff = new StringBuffer();
					JSONArray targets= new JSONArray(res);
					for (int i=0; i < targets.length(); i++) {
						if (i > 0) {
							buff.append(",");
						}
						temp = targets.getJSONObject(i);
						String dataSource = temp.getString("datasource");
						buff.append(sitesMap.get(temp.getString("site"))).append("/dataset/").append(libPath).append("/").append(funcPath).append("?dataSource=").append(Utils.urlEncode(dataSource));
					}
					String targetsURLs = buff.toString();
					buff = new StringBuffer();
					buff.append(masterURL).append("/query/").append(libPath).append("/").append(funcPath);
					String trxId = request.getParameter("trxId");
					String url = null;
					ClientURLResponse rsp = null;
					String rspId = null;
					obj = new JSONObject();
					if (trxId != null) {
						trxId = Utils.urlEncode(trxId);
						buff.append("/id/" + trxId);
						url = buff.toString();
						System.out.println("URL: " + url);
						rsp = scannerClient.get(url);
					} else {
						url = buff.toString();
						System.out.println("URL: " + url + "\nTargets: "+targetsURLs+"\nParams: "+params);
						rsp = scannerClient.postScannerQuery(url, targetsURLs, params);
						rspId = rsp.getIdHeader();
						System.out.println("Response Id: \n"+rspId);
						obj.put("trxId", rspId);
					}
					res = rsp.getEntityString();
					System.out.println("Response Body: \n"+res);
					JSONObject body = new JSONObject(res);
					obj.put("data", body);
					PrintWriter out = response.getWriter();
					out.print(obj.toString());
					//out.print(res);
					return;
					// simulated response
					//obj = new JSONObject(Utils.oceansResult);
					//PrintWriter out = response.getWriter();
					//out.print(obj.toString());
					//return;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		PrintWriter out = response.getWriter();
		String text = obj.toString();
		out.print(text);
	}

}