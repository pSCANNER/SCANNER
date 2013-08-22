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
import java.util.Hashtable;
import java.util.List;

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
 * Servlet for submitting queries to the registry and the network.
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
       
	private String webContentPath;

	/**
     * Default constructor. 
     * @see HttpServlet#HttpServlet()
     */
    public Query() {
        super();
    }

	/**
	 * Initialize the servlet with the configuration values.
	 * @param config
     *            the servlet configuration.
	 */
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
		webContentPath = path;
		trustStoreResource = path + trustStoreResource;
		keyStoreResource = path + keyStoreResource;
		System.out.println("trustStoreResource: " + trustStoreResource);
		System.out.println("keyStoreResource: " + keyStoreResource);
	}

	/**
	 * Retrieves the registry entries.
	 * <br/>Default method called by the server (via the service method) to allow a servlet to handle a GET request.
	 * @param request
	 * 		an HttpServletRequest object that contains the request the client has made of the servlet.
	 * @param response
	 * 		an HttpServletResponse object that contains the response the servlet sends to the client.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		HttpSession session = request.getSession(false);
		JakartaClient httpClient = (JakartaClient) session.getAttribute("httpClient");
		String cookie = (String) session.getAttribute("tagfilerCookie");
		httpClient.setCookieValue(cookie);
		RegistryClient registryClient = (RegistryClient) session.getAttribute("registryClient");
		if (!registryClient.hasRoles()) {
			PrintWriter out = response.getWriter();
			out.print("{}");
			return;
		}
		if (action.equals("getStudies")) {
			RegistryClientResponse clientResponse = registryClient.getStudies();
			String ret = clientResponse.toStudies();
			clientResponse.release();
			System.out.println("Get Studies:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getDatasets")) {
			String study = request.getParameter("study");
			RegistryClientResponse clientResponse = registryClient.getDatasets(study);
			String ret = clientResponse.toDatasets();
			clientResponse.release();
			System.out.println("Get Datasets: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getLibraries")) {
			String study = request.getParameter("study");
			String dataset = request.getParameter("dataset");
			String sites = request.getParameter("site");
			String func = request.getParameter("method");
			RegistryClientResponse clientResponse = registryClient.getLibraries(study, dataset, func, sites);
			String ret = clientResponse.toLibraries();
			clientResponse.release();
			System.out.println("Get Libraries: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getMethods")) {
			String study = request.getParameter("study");
			String dataset = request.getParameter("dataset");
			String sites = request.getParameter("site");
			RegistryClientResponse clientResponse = registryClient.getMethods(study, dataset, sites);
			String ret = clientResponse.toMethods();
			clientResponse.release();
			System.out.println("Get Methods: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getParameters")) {
			String func = request.getParameter("method");
			String lib = request.getParameter("library");
			RegistryClientResponse clientResponse = registryClient.getParameters(func, lib, webContentPath + "etc/parameterTypes/LogisticRegression.json");
			String ret = clientResponse.toParameters();
			clientResponse.release();
			System.out.println("Get Parameters:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getSites")) {
			String study = request.getParameter("study");
			String dataset = request.getParameter("dataset");
			RegistryClientResponse clientResponse = registryClient.getSites(study, dataset);
			String ret = clientResponse.toSites(dataset);
			clientResponse.release();
			System.out.println("Get Sites:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		}
	}

	/**
	 * Submits the queries to the master node.
	 * <br/>Called by the server (via the service method) to allow a servlet to handle a POST request.
	 * @param request
	 * 		an HttpServletRequest object that contains the request the client has made of the servlet.
	 * @param response
	 * 		an HttpServletResponse object that contains the response the servlet sends to the client.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
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
					RegistryClient registryClient = new TagfilerClient(httpClient, tagfilerURL, httpClient.getCookieValue(), request);
					//RegistryClient registryClient = new TagfilerClient(httpClient, tagfilerURL, httpClient.getCookieValue());
					session.setAttribute("registryClient", registryClient);
					RegistryClientResponse clientResponse = registryClient.getContacts();
					String ret = clientResponse.toContacts();
					clientResponse.release();
					JSONArray arr = new JSONArray(ret);
					obj.put("contacts", arr);
					System.out.println("Get Contacts:\n"+ret);
				} else {
					// to handle this case
					System.out.println("Response is null");
				}
				scannerClient = new ScannerClient(4, 8192, 300000,
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
					if (!registryClient.hasRoles()) {
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
						return;
					}
					String params = request.getParameter("parameters");
					String sites = request.getParameter("sites");
					String lib = request.getParameter("library");
					String func = request.getParameter("method");
					String study = request.getParameter("study");
					String dataset = request.getParameter("dataset");
					RegistryClientResponse clientResponse = registryClient.getMasterObject();
					String res = clientResponse.toMasterString();
					clientResponse.release();
					System.out.println("master string: " + res);
					JSONObject temp = new JSONObject(res);
					String masterURL = temp.getString("rURL");
					clientResponse = registryClient.getMethodObject(func, lib);
					res = clientResponse.toMethodString(func, lib);
					clientResponse.release();
					System.out.println("method string: " + res);
					temp = new JSONObject(res);
					String funcPath = temp.getString("rpath");
					clientResponse = registryClient.getLibraryObject(lib);
					res = clientResponse.toLibraryString();
					clientResponse.release();
					System.out.println("library string: " + res);
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
					clientResponse = registryClient.getSiteObject(values, study);
					res = clientResponse.toSiteString(values, dataset);
					clientResponse.release();
					System.out.println("site string: " + res);
					HashMap<String, String> sitesMap = new HashMap<String, String>();
					JSONArray tempArray = new JSONArray(res);
					for (int i=0; i < tempArray.length(); i++) {
						temp = tempArray.getJSONObject(i);
						sitesMap.put(temp.getString("cname"), temp.getString("rURL"));
					}
					clientResponse = registryClient.getWorkers(study, dataset, lib, func, values);
					res = clientResponse.toWorkers();
					clientResponse.release();
					System.out.println("workers string: " + res);
					StringBuffer buff = new StringBuffer();
					JSONArray targets= new JSONArray(res);
					if (targets.length() == 0) {
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
						return;
					}
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
						// check that the user authorization for this trxId
						boolean isAuthorized = false;
						Hashtable<String, List<String>> trxTable = (Hashtable<String, List<String>>) session.getAttribute("trxIdTable");
						if (trxTable != null) {
							List<String> trxRoles = trxTable.get(trxId);
							if (trxRoles != null) {
								isAuthorized = true;
								List<String> userRoles = registryClient.getRoles();
								// check that the user has all the transaction roles
								for (int i=0; i < trxRoles.size(); i++) {
									if (!userRoles.contains(trxRoles.get(i))) {
										isAuthorized = false;
										break;
									}
								}
							}
						}
						if (!isAuthorized) {
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
							return;
						}
						trxId = Utils.urlEncode(trxId);
						buff.append("/id/" + trxId);
						url = buff.toString();
						System.out.println("URL: " + url);
						rsp = scannerClient.get(url);
					} else {
						url = buff.toString();
						System.out.println("URL: " + url + "\nTargets: "+targetsURLs+"\nParams: "+params);
						rsp = scannerClient.postScannerQuery(url, targetsURLs, params);
						if (!rsp.isException()) {
							rspId = rsp.getIdHeader();
							System.out.println("Response Id: "+rspId);
							if (rspId != null) {
								obj.put("trxId", rspId);
								Hashtable<String, List<String>> trxTable = (Hashtable<String, List<String>>) session.getAttribute("trxIdTable");
								if (trxTable == null) {
									trxTable = new Hashtable<String, List<String>>();
									session.setAttribute("trxIdTable", trxTable);
								}
								trxTable.put(rspId, registryClient.getRoles());
							}
						}
					}
					if (rsp.isException()) {
						throw(new ServletException(rsp.getException()));
					} else if (rsp.isError()) {
						response.sendError(rsp.getStatus(), rsp.getEntityString());
						return;
					}
					res = rsp.getEntityString();
					System.out.println("Response Body: \n"+res);
					try {
						JSONObject body = new JSONObject(res);
						obj.put("data", body);
						PrintWriter out = response.getWriter();
						out.print(obj.toString());
						return;
					} catch (JSONException e) {
						e.printStackTrace();
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					throw(new ServletException(e));
				}
			} else if (action.equals("getResultsAsync")) {
				try {
					RegistryClient registryClient = (RegistryClient) session.getAttribute("registryClient");
					if (!registryClient.hasRoles()) {
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
						return;
					}
					String params = request.getParameter("parameters");
					String sites = request.getParameter("sites");
					String lib = request.getParameter("library");
					String func = request.getParameter("method");
					String study = request.getParameter("study");
					String dataset = request.getParameter("dataset");
					RegistryClientResponse clientResponse = registryClient.getMasterObject();
					String res = clientResponse.toMasterString();
					clientResponse.release();
					System.out.println("master string: " + res);
					JSONObject temp = new JSONObject(res);
					String masterURL = temp.getString("rURL");
					clientResponse = registryClient.getMethodObject(func, lib);
					res = clientResponse.toMethodString(func, lib);
					clientResponse.release();
					System.out.println("method string: " + res);
					temp = new JSONObject(res);
					String funcPath = temp.getString("rpath");
					clientResponse = registryClient.getLibraryObject(lib);
					res = clientResponse.toLibraryString();
					clientResponse.release();
					System.out.println("library string: " + res);
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
					clientResponse = registryClient.getSiteObject(values, study);
					res = clientResponse.toSiteString(values, dataset);
					clientResponse.release();
					System.out.println("site string: " + res);
					HashMap<String, String> sitesMap = new HashMap<String, String>();
					JSONArray tempArray = new JSONArray(res);
					for (int i=0; i < tempArray.length(); i++) {
						temp = tempArray.getJSONObject(i);
						sitesMap.put(temp.getString("cname"), temp.getString("rURL"));
					}
					clientResponse = registryClient.getWorkers(study, dataset, lib, func, values);
					res = clientResponse.toWorkers();
					clientResponse.release();
					System.out.println("workers string: " + res);
					StringBuffer buff = new StringBuffer();
					JSONArray targets= new JSONArray(res);
					if (targets.length() == 0) {
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
						return;
					}
					for (int i=0; i < targets.length(); i++) {
						if (i > 0) {
							buff.append(",");
						}
						temp = targets.getJSONObject(i);
						String dataSource = temp.getString("datasource");
						buff.append(sitesMap.get(temp.getString("site"))).append("/dataset/").append(libPath).append("/").append(funcPath).append("?dataSource=").append(Utils.urlEncode(dataSource));
						if (sitesMap.get(temp.getString("site")).equals("https://scanner-node1.misd.isi.edu:8888/scanner")) {
							buff.append("&resultsReleaseAuthReq=true");
						}
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
						// check that the user authorization for this trxId
						boolean isAuthorized = false;
						Hashtable<String, List<String>> trxTable = (Hashtable<String, List<String>>) session.getAttribute("trxIdTable");
						if (trxTable != null) {
							List<String> trxRoles = trxTable.get(trxId);
							if (trxRoles != null) {
								isAuthorized = true;
								List<String> userRoles = registryClient.getRoles();
								// check that the user has all the transaction roles
								for (int i=0; i < trxRoles.size(); i++) {
									if (!userRoles.contains(trxRoles.get(i))) {
										isAuthorized = false;
										break;
									}
								}
							}
						}
						if (!isAuthorized) {
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
							return;
						}
						trxId = Utils.urlEncode(trxId);
						buff.append("/id/" + trxId);
						url = buff.toString();
						System.out.println("URL: " + url);
						buff = new StringBuffer();
						for (int i=0; i < targets.length(); i++) {
							if (i > 0) {
								buff.append(",");
							}
							temp = targets.getJSONObject(i);
							buff.append(sitesMap.get(temp.getString("site"))).append("/dataset/").append(libPath).append("/").append(funcPath);
						}
						System.out.println("GET Targets: "+buff.toString());
						rsp = scannerClient.get(url, buff);
					} else {
						url = buff.toString();
						System.out.println("URL: " + url + "\nTargets: "+targetsURLs+"\nParams: "+params);
						rsp = scannerClient.postScannerQuery(url, targetsURLs, params);
						if (!rsp.isException()) {
							rspId = rsp.getIdHeader();
							System.out.println("Response Id: "+rspId);
							if (rspId != null) {
								obj.put("trxId", rspId);
								Hashtable<String, List<String>> trxTable = (Hashtable<String, List<String>>) session.getAttribute("trxIdTable");
								if (trxTable == null) {
									trxTable = new Hashtable<String, List<String>>();
									session.setAttribute("trxIdTable", trxTable);
								}
								trxTable.put(rspId, registryClient.getRoles());
							}
						}
					}
					if (rsp.isException()) {
						throw(new ServletException(rsp.getException()));
					} else if (rsp.isError()) {
						response.sendError(rsp.getStatus(), rsp.getEntityString());
						return;
					}
					res = rsp.getEntityString();
					System.out.println("Response Body: \n"+res);
					try {
						JSONObject body = new JSONObject(res);
						obj.put("data", body);
					} catch (JSONException e) {
						obj.put("data", res);
					}
					try {
						obj.put("async", true);
						PrintWriter out = response.getWriter();
						out.print(obj.toString());
						return;
					} catch (JSONException e) {
						e.printStackTrace();
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					throw(new ServletException(e));
				}
			} else if (action.equals("displaySitesStatus")) {
				// keep Tagfiler's session alive
				RegistryClient registryClient = (RegistryClient) session.getAttribute("registryClient");
				RegistryClientResponse clientResponse = registryClient.getSitesMap();
				clientResponse.release();
				obj = (JSONObject) servletConfig.getServletContext().getAttribute("echo");
				if (obj == null) {
					obj = new JSONObject();
				}
				//System.out.println("Return: "+obj.toString());
			}

		} catch (JSONException e) {
			e.printStackTrace();
			throw(new ServletException(e));
		}
		PrintWriter out = response.getWriter();
		String text = obj.toString();
		out.print(text);
	}
	
}
