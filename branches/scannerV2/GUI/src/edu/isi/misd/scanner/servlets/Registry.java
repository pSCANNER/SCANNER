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
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.isi.misd.scanner.client.ERDClient;
import edu.isi.misd.scanner.client.JakartaClient;
import edu.isi.misd.scanner.client.RegistryClient;
import edu.isi.misd.scanner.client.RegistryClientResponse;
import edu.isi.misd.scanner.utils.Utils;

/**
 * Servlet for implementing the web service registry API.
 * 
 * @author Serban Voinea
 */
@WebServlet(description = "Manage Registry", urlPatterns = { "/registry" })
public class Registry extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletConfig servletConfig;
	private String erdURL;
       
    /**
     * Default constructor. 
     * @see HttpServlet#HttpServlet()
     */
    public Registry() {
        super();
    }

	/**
	 * Initializes the servlet with the configuration values.
	 * @param config
     *            the servlet configuration.
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		servletConfig = config;
		erdURL = servletConfig.getServletContext().getInitParameter("registryURL");
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
		if (action == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No action specified.");
			return;
		}
		HttpSession session = request.getSession(false);
		if (session == null) {
			PrintWriter out = response.getWriter();
			out.print("Not logged in.\n");
			return;
		}
		RegistryClient registryClient = (RegistryClient) session.getAttribute("registryClient");
		String res = "";
		String name = request.getParameter("cname");
		String study = request.getParameter("study");
		String studyId = request.getParameter("studyId");
		String lib = request.getParameter("library");
		String dataset = request.getParameter("dataset");
		String func = request.getParameter("method");
		String site = request.getParameter("site");
		RegistryClientResponse clientResponse = null;
		if (action.equals("getMyStudies")) {
			clientResponse = registryClient.getMyStudies();
			if (clientResponse != null) {
				res = clientResponse.getEntityResponse().toString();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get my studies.");
				return;
			}
		} else if (action.equals("getStudyData")) {
			clientResponse = registryClient.getStudyRequestedSites(Integer.parseInt(studyId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get study requested sites for study ." + studyId);
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				JSONArray responseArray = clientResponse.getEntityResponse();
				responseObject.put("sites", responseArray);
				clientResponse.release();
				clientResponse = registryClient.getUserRoles(Integer.parseInt(studyId));
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get users roles." + studyId);
					return;
				}
				responseArray = clientResponse.getEntityResponse();
				responseObject.put("userRoles", responseArray);
				clientResponse.release();
				clientResponse = registryClient.getStudyRoles(Integer.parseInt(studyId));
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get study roles." + studyId);
					return;
				}
				responseArray = clientResponse.getEntityResponse();
				responseObject.put("studyRoles", responseArray);
				clientResponse.release();
				clientResponse = registryClient.getStudyPolicies();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get study policies." + studyId);
					return;
				}
				responseArray = clientResponse.getEntityResponse();
				for (int i=responseArray.length() - 1; i >=0; i--) {
					JSONObject studyPolicy = responseArray.getJSONObject(i);
					JSONObject obj = studyPolicy.getJSONObject("study");
					if (obj.getInt("studyId") != Integer.parseInt(studyId)) {
						responseArray.remove(i);
					}
				}
				responseObject.put("studyPolicies", responseArray);
				clientResponse.release();
				clientResponse = registryClient.getAnalysisPolicies();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get analyze policies." + studyId);
					return;
				}
				responseArray = clientResponse.getEntityResponse();
				for (int i=responseArray.length() - 1; i >=0; i--) {
					JSONObject analysisPolicy = responseArray.getJSONObject(i);
					JSONObject obj = analysisPolicy.getJSONObject("studyRole");
					if (obj.getInt("study") != Integer.parseInt(studyId)) {
						responseArray.remove(i);
					}
				}
				responseObject.put("analysisPolicies", responseArray);
				clientResponse.release();
				res = responseObject.toString();
				System.out.println("getStudyData responseBody: " + res);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("getAllStudies")) {
			clientResponse = registryClient.getAllStudies();
			if (clientResponse != null) {
				res = clientResponse.getEntityResponse().toString();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get the studies.");
				return;
			}
		} else if (action.equals("getAllLibraries")) {
			clientResponse = registryClient.getAllLibraries();
			if (clientResponse != null) {
				res = clientResponse.getEntityResponse().toString();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get libraries.");
				return;
			}
		} else if (action.equals("getAllSites")) {
			clientResponse = registryClient.getAllSites();
			if (clientResponse != null) {
				res = clientResponse.getEntityResponse().toString();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get sites.");
				return;
			}
		} else if (action.equals("getAllNodes")) {
			clientResponse = registryClient.getAllNodes();
			if (clientResponse != null) {
				res = clientResponse.getEntityResponse().toString();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get nodes.");
				return;
			}
		} else if (action.equals("getAllStandardRoles")) {
			clientResponse = registryClient.getAllStandardRoles();
			if (clientResponse != null) {
				res = clientResponse.getEntityResponse().toString();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get standard roles.");
				return;
			}
		} else if (action.equals("getAllStudyManagementPolicies")) {
			clientResponse = registryClient.getAllStudyManagementPolicies();
			if (clientResponse != null) {
				res = clientResponse.getEntityResponse().toString();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get study management policies.");
				return;
			}
		}  else if (action.equals("getAllSitesPolicies")) {
			clientResponse = registryClient.getAllSitesPolicies();
			if (clientResponse != null) {
				res = clientResponse.getEntityResponse().toString();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get study management policies.");
				return;
			}
		} else if (action.equals("getAllStudyRequestedSites")) {
			clientResponse = registryClient.getAllStudyRequestedSites();
			if (clientResponse != null) {
				res = clientResponse.getEntityResponse().toString();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get study management policies.");
				return;
			}
		} else if (action.equals("getStudyRequestedSites")) {
			clientResponse = registryClient.getStudyRequestedSites(Integer.parseInt(studyId));
			if (clientResponse != null) {
				res = clientResponse.getEntityResponse().toString();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get study management policies for study ." + studyId);
				return;
			}
		} else if (action.equals("getSite")) {
			clientResponse = registryClient.getSite(name);
			if (clientResponse != null) {
				res = clientResponse.toSite();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get site.");
				return;
			}
		} else if (action.equals("getPI")) {
			clientResponse = registryClient.getPI();
			if (clientResponse != null) {
				res = clientResponse.getEntityString();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get site.");
				return;
			}
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unknown action: \"" + action + "\"");
			return;
		}
		
		if (clientResponse != null) {
			clientResponse.release();
		}
		PrintWriter out = response.getWriter();
		out.print(res);
	}

	/**
	 * Creates, updates or deletes the entries in the registry.
	 * <br/>Called by the server (via the service method) to allow a servlet to handle a POST request.
	 * @param request
	 * 		an HttpServletRequest object that contains the request the client has made of the servlet.
	 * @param response
	 * 		an HttpServletResponse object that contains the response the servlet sends to the client.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No action specified.");
			return;
		}
		if (action.equals("login")) {
			HttpSession session = request.getSession(true);
			if (session.isNew() == false) {
				session.invalidate();
				session = request.getSession(true);
			}  
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			boolean valid = username != null && password != null;
			if (valid) {
				session.setAttribute("user", username);
				JakartaClient client = new JakartaClient(4, 8192, 120000);
				session.setAttribute("httpClient", client);
				RegistryClient registryClient = new ERDClient(erdURL, (String) session.getAttribute("user"));
				session.setAttribute("registryClient", registryClient);
				PrintWriter out = response.getWriter();
				out.print(client.getCookieValue() + "\n");
			} else {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password.");
			}
			return;
		}
		HttpSession session = request.getSession(false);
		if (session == null) {
			PrintWriter out = response.getWriter();
			out.print("Not logged in.\n");
			return;
		}
		RegistryClient registryClient = (RegistryClient) session.getAttribute("registryClient");
		String studyName = request.getParameter("studyName");
		String irbId = request.getParameter("irbId");
		String studyOwner = request.getParameter("studyOwner");
		String studyStatusType = request.getParameter("studyStatusType");
		String studyId = request.getParameter("studyId");
		String studyRequestedSiteId = request.getParameter("studyRequestedSiteId");
		String siteId = request.getParameter("siteId");
		String clinicalTrialsId = request.getParameter("clinicalTrialsId");
		String description = request.getParameter("description"); 
		String protocol = request.getParameter("protocol"); 
		String startDate = request.getParameter("startDate"); 
		String endDate = request.getParameter("endDate"); 
		String analysisPlan = request.getParameter("analysisPlan"); 
		String userId = request.getParameter("userId");
		String roleId = request.getParameter("roleId");
		String userRoleId = request.getParameter("userRoleId");
		String dataSetDefinitionId = request.getParameter("dataSetDefinitionId");
		String toolId = request.getParameter("toolId");
		String accessModeId = request.getParameter("accessModeId");
		String studyPolicyStatementId = request.getParameter("studyPolicyStatementId");
		String dataSetInstanceId = request.getParameter("dataSetInstanceId");
		String analysisPolicyStatementId = request.getParameter("analysisPolicyStatementId");
		String dataSetInstanceName = request.getParameter("dataSetInstanceName");
		String dataSource = request.getParameter("dataSource");
		String nodeId = request.getParameter("nodeId");
		
		
		String study = request.getParameter("study");
		String id = request.getParameter("id");
		String name = request.getParameter("cname");
		String rpath = request.getParameter("rpath");
		String lib = request.getParameter("library");
		String rURL = request.getParameter("rURL");
		String datasource = request.getParameter("datasource");
		String dataset = request.getParameter("dataset");
		String func = request.getParameter("method");
		String site = request.getParameter("site");
		String resourceValues = request.getParameter("values"); 
		String title = request.getParameter("title"); 
		String email = request.getParameter("email"); 
		String phone = request.getParameter("phone"); 
		String website = request.getParameter("website"); 
		String address = request.getParameter("address"); 
		String contact = request.getParameter("contact"); 
		String approvals = request.getParameter("approvals"); 
		String variables = request.getParameter("variables"); 
		String path = request.getParameter("path"); 
		String users = request.getParameter("users"); 
		String agreement = request.getParameter("agreement"); 
		Integer minOccurs = null; 
		Integer maxOccurs = null; 
		if (request.getParameter("minOccurs") != null) {
			minOccurs = Integer.parseInt(request.getParameter("minOccurs"));
		}
		if (request.getParameter("maxOccurs") != null) {
			maxOccurs = Integer.parseInt(request.getParameter("maxOccurs"));
		}
		RegistryClientResponse clientResponse = null;
		String responseBody = null;
		if (action.equals("createStudy")) {
			clientResponse = registryClient.createStudy(studyName, irbId, studyOwner, studyStatusType);
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create study.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				responseObject.put("study", clientResponse.getEntity());
				clientResponse.release();
				clientResponse = registryClient.getAllStudies();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get the studies.");
					return;
				}
				responseObject.put("allStudies", clientResponse.getEntityResponse());
				clientResponse.release();
				clientResponse = registryClient.getUserRoles();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get user roles.");
					return;
				}
				responseObject.put("userRoles", clientResponse.getEntityResponse());
				clientResponse.release();
				clientResponse = registryClient.getStudyRoles();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get studies roles.");
					return;
				}
				responseObject.put("studiesRoles", clientResponse.getEntityResponse());
				clientResponse.release();
				clientResponse = registryClient.getAllStudyManagementPolicies();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get studies management policies.");
					return;
				}
				responseObject.put("studyManagementPolicies", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("responseBody: " + responseBody);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("createStudyRequestedSites")) {
			clientResponse = registryClient.createStudyRequestedSites(Integer.parseInt(studyId), Integer.parseInt(siteId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create study requested sites.");
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				responseObject.put("site", clientResponse.getEntity());
				clientResponse.release();
				clientResponse = registryClient.getAllStudyRequestedSites();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get all studies requested sites.");
					return;
				}
				responseObject.put("allSites", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("createStudyRequestedSites responseBody: " + responseBody);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("createUserRole")) {
			clientResponse = registryClient.createUserRole(Integer.parseInt(userId), Integer.parseInt(roleId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create user role.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				responseObject.put("userRole", clientResponse.getEntity());
				clientResponse.release();
				clientResponse = registryClient.getUserRoles();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get all users roles.");
					return;
				}
				responseObject.put("userRoles", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("createStudyRequestedSites responseBody: " + responseBody);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("createStudyPolicy")) {
			clientResponse = registryClient.createStudyPolicy(Integer.parseInt(roleId), Integer.parseInt(studyId), 
					Integer.parseInt(dataSetDefinitionId), Integer.parseInt(toolId), Integer.parseInt(accessModeId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create study protocol.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("createStudyPolicy:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				responseObject.put("studyPolicy", clientResponse.getEntity());
				clientResponse.release();
				clientResponse = registryClient.getStudyPolicies();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get all studies requested sites.");
					return;
				}
				responseObject.put("studyPolicies", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("createStudyPolicy responseBody: " + responseBody);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("createSitePolicy")) {
			clientResponse = registryClient.createSitePolicy(Integer.parseInt(roleId), Integer.parseInt(dataSetInstanceId), 
					Integer.parseInt(studyPolicyStatementId), Integer.parseInt(toolId), Integer.parseInt(accessModeId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create site protocol.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("createSitePolicy:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				responseObject.put("analysisPolicy", clientResponse.getEntity());
				clientResponse.release();
				clientResponse = registryClient.getAnalysisPolicies();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get analysis policies.");
					return;
				}
				responseObject.put("analysisPolicies", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("createSitePolicy responseBody: " + responseBody);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("createDatasetInstance")) {
			clientResponse = registryClient.createDatasetInstance(dataSetInstanceName, description, 
					dataSource, Integer.parseInt(dataSetDefinitionId), Integer.parseInt(nodeId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create dataset instance.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("createDatasetInstance:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				responseObject.put("instance", clientResponse.getEntity());
				clientResponse.release();
				clientResponse = registryClient.getDatasetInstances();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get analysis policies.");
					return;
				}
				responseObject.put("instances", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("createDatasetInstance responseBody: " + responseBody);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("deleteStudyRequestedSites")) {
			clientResponse = registryClient.deleteStudyRequestedSites(Integer.parseInt(studyRequestedSiteId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete study requested sites.");
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				clientResponse.release();
				clientResponse = registryClient.getAllStudyRequestedSites();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get all studies requested sites.");
					return;
				}
				responseObject.put("allSites", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("deleteStudyRequestedSites responseBody: " + responseBody);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("deleteUserRole")) {
			clientResponse = registryClient.deleteUserRole(Integer.parseInt(userRoleId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete user role.");
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				clientResponse.release();
				clientResponse = registryClient.getUserRoles();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get all users roles.");
					return;
				}
				responseObject.put("userRoles", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("deleteUserRole responseBody: " + responseBody);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("deleteStudyPolicy")) {
			clientResponse = registryClient.deleteStudyPolicy(Integer.parseInt(studyPolicyStatementId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete study policy.");
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				clientResponse.release();
				clientResponse = registryClient.getStudyPolicies();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get study policies.");
					return;
				}
				responseObject.put("studyPolicies", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("deleteStudyPolicy responseBody: " + responseBody);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("deleteAnalyzePolicy")) {
			clientResponse = registryClient.deleteAnalyzePolicy(Integer.parseInt(analysisPolicyStatementId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete site policy.");
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				clientResponse.release();
				clientResponse = registryClient.getAnalysisPolicies();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get site policies.");
					return;
				}
				responseObject.put("analysisPolicies", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("deleteSitePolicy responseBody: " + responseBody);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("createDataset")) {
			try {
				ArrayList<String> values = null;
				if (variables != null) {
					JSONArray arr = new JSONArray(variables);
					if (arr.length() > 0) {
						values = new ArrayList<String>();
						for (int i=0; i < arr.length(); i++) {
							values.add(arr.getString(i));
						}
					}
				}
				clientResponse = registryClient.createDataset(name, study, description, values);
				if (clientResponse != null) {
					responseBody = Utils.extractId(clientResponse.getEntityString());
				} else {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create dataset.");
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createLibrary")) {
			clientResponse = registryClient.createLibrary(name, rpath, description);
			if (clientResponse != null) {
				responseBody = Utils.extractId(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create library.");
				return;
			}
		} else if (action.equals("createMethod")) {
			try {
				ArrayList<String> values = null;
				if (lib != null) {
					JSONArray arr = new JSONArray(lib);
					if (arr.length() > 0) {
						values = new ArrayList<String>();
						for (int i=0; i < arr.length(); i++) {
							values.add(arr.getString(i));
						}
					}
				}
				clientResponse = registryClient.createMethod(name, values, rpath, description);
				if (clientResponse != null) {
					responseBody = Utils.extractId(clientResponse.getEntityString());
				} else {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create method.");
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createMaster")) {
			clientResponse = registryClient.createMaster(rURL, title,
					email, phone, website, address, contact);
			if (clientResponse != null) {
				responseBody = Utils.extractId(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create master.");
				return;
			}
		} else if (action.equals("createWorker")) {
				try {
					ArrayList<String> values = null;
					if (users != null) {
						JSONArray arr;
						arr = new JSONArray(users);
						if (arr.length() > 0) {
							values = new ArrayList<String>();
							for (int i=0; i < arr.length(); i++) {
								values.add(arr.getString(i));
							}
						}
					}
					clientResponse = registryClient.createWorker(study, dataset, lib, func, site, datasource, values);
					if (clientResponse != null) {
						responseBody = Utils.extractId(clientResponse.getEntityString());
					} else {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create worker.");
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		} else if (action.equals("createSite")) {
			clientResponse = registryClient.createSite(name, rURL, title,
					email, phone, website, address, agreement, contact);
			if (clientResponse != null) {
				responseBody = Utils.extractId(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create site.");
				return;
			}
		} else if (action.equals("createParameter")) {
			try {
				ArrayList<String> values = null;
				if (resourceValues != null) {
					JSONArray arr = new JSONArray(resourceValues);
					if (arr.length() > 0) {
						values = new ArrayList<String>();
						for (int i=0; i < arr.length(); i++) {
							values.add(arr.getString(i));
						}
					}
				}
				ArrayList<String> libs = null;
				if (resourceValues != null) {
					JSONArray arr = new JSONArray(lib);
					if (arr.length() > 0) {
						libs = new ArrayList<String>();
						for (int i=0; i < arr.length(); i++) {
							libs.add(arr.getString(i));
						}
					}
				}
				clientResponse = registryClient.createParameter(name, func, libs, 
						minOccurs, maxOccurs, values, path, description);
				if (clientResponse != null) {
					responseBody = Utils.extractId(clientResponse.getEntityString());
				} else {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create parameter.");
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteMethod")) {
			clientResponse = registryClient.deleteMethod(name, lib);
			if (clientResponse != null) {
				responseBody = Utils.getEntity(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete method.");
				return;
			}
		} else if (action.equals("deleteLibrary")) {
			clientResponse = registryClient.deleteLibrary(name);
			if (clientResponse != null) {
				responseBody = Utils.getEntity(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete library.");
				return;
			}
		} else if (action.equals("deleteDataset")) {
			clientResponse = registryClient.deleteDataset(name, study);
			if (clientResponse != null) {
				responseBody = Utils.getEntity(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete dataset.");
				return;
			}
		} else if (action.equals("deleteStudy")) {
			clientResponse = registryClient.deleteStudy(name);
			if (clientResponse != null) {
				responseBody = Utils.getEntity(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete study.");
				return;
			}
		} else if (action.equals("deleteMaster")) {
			clientResponse = registryClient.deleteMaster();
			if (clientResponse != null) {
				responseBody = Utils.getEntity(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete master.");
				return;
			}
		}  else if (action.equals("deleteSite")) {
			clientResponse = registryClient.deleteSite(name);
			if (clientResponse != null) {
				responseBody = Utils.getEntity(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete site.");
				return;
			}
		} else if (action.equals("deleteParameter")) {
			clientResponse = registryClient.deleteParameter(name, func, lib);
			if (clientResponse != null) {
				responseBody = Utils.getEntity(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete parameter.");
				return;
			}
		} else if (action.equals("deleteWorker")) {
			clientResponse = registryClient.deleteWorker(study, dataset, lib, func, site);
			if (clientResponse != null) {
				responseBody = Utils.getEntity(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete worker.");
				return;
			}
		} else if (action.equals("updateStudy")) {
			clientResponse = registryClient.updateStudy(studyId, studyName, irbId, studyOwner, studyStatusType,
					description, protocol, startDate, endDate, clinicalTrialsId, analysisPlan);
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not update study.");
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				responseObject.put("study", clientResponse.getEntity());
				clientResponse.release();
				clientResponse = registryClient.getAllStudies();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get the studies.");
					return;
				}
				responseObject.put("allStudies", clientResponse.getEntityResponse());
				clientResponse.release();
				clientResponse = registryClient.getUserRoles();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get user roles.");
					return;
				}
				responseObject.put("userRoles", clientResponse.getEntityResponse());
				clientResponse.release();
				clientResponse = registryClient.getStudyRoles();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get studies roles.");
					return;
				}
				responseObject.put("studiesRoles", clientResponse.getEntityResponse());
				clientResponse.release();
				clientResponse = registryClient.getAllStudyManagementPolicies();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get studies management policies.");
					return;
				}
				responseObject.put("studyManagementPolicies", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("responseBody: " + responseBody);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals("updateDataset")) {
			try {
				ArrayList<String> values = null;
				if (variables != null) {
					JSONArray arr = new JSONArray(variables);
					if (arr.length() > 0) {
						values = new ArrayList<String>();
						for (int i=0; i < arr.length(); i++) {
							values.add(arr.getString(i));
						}
					}
				}
				clientResponse = registryClient.updateDataset(id, name, study, description, values);
				if (clientResponse != null) {
					responseBody = Utils.getEntity(clientResponse.getEntityString());
				} else {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not update dataset.");
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("updateLibrary")) {
			clientResponse = registryClient.updateLibrary(id, name, rpath, description);
			if (clientResponse != null) {
				responseBody = Utils.getEntity(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not update library.");
				return;
			}
		} else if (action.equals("updateMethod")) {
			try {
				ArrayList<String> values = null;
				if (lib != null) {
					JSONArray arr = new JSONArray(lib);
					if (arr.length() > 0) {
						values = new ArrayList<String>();
						for (int i=0; i < arr.length(); i++) {
							values.add(arr.getString(i));
						}
					}
				}
				clientResponse = registryClient.updateMethod(id, name, values, rpath, description);
				if (clientResponse != null) {
					responseBody = Utils.getEntity(clientResponse.getEntityString());
				} else {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not update method.");
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("updateMaster")) {
			clientResponse = registryClient.updateMaster(rURL, title,
					email, phone, website, address, contact);
			if (clientResponse != null) {
				responseBody = Utils.getEntity(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not update master.");
				return;
			}
		}  else if (action.equals("updateSite")) {
			clientResponse = registryClient.updateSite(id, name, rURL, title,
					email, phone, website, address, agreement, contact);
			if (clientResponse != null) {
				responseBody = Utils.getEntity(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not update site.");
				return;
			}
		} else if (action.equals("updateWorker")) {
			try {
				ArrayList<String> values = null;
				if (users != null) {
					JSONArray arr;
					arr = new JSONArray(users);
					if (arr.length() > 0) {
						values = new ArrayList<String>();
						for (int i=0; i < arr.length(); i++) {
							values.add(arr.getString(i));
						}
					}
				}
				clientResponse = registryClient.updateWorker(id, study, dataset, lib, func, site, 
						datasource, values);
				if (clientResponse != null) {
					responseBody = Utils.getEntity(clientResponse.getEntityString());
				} else {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not update worker.");
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("updateParameter")) {
			try {
				ArrayList<String> values = null;
				ArrayList<String> libs = null;
				if (resourceValues != null) {
					JSONArray arr = new JSONArray(resourceValues);
					if (arr.length() > 0) {
						values = new ArrayList<String>();
						for (int i=0; i < arr.length(); i++) {
							values.add(arr.getString(i));
						}
					}
				}
				if (lib != null) {
					JSONArray arr = new JSONArray(lib);
					if (arr.length() > 0) {
						libs = new ArrayList<String>();
						for (int i=0; i < arr.length(); i++) {
							libs.add(arr.getString(i));
						}
					}
				}
				clientResponse = registryClient.updateParameter(id, name, func, libs, 
					 minOccurs, maxOccurs, values, path, description);
				if (clientResponse != null) {
					responseBody = Utils.getEntity(clientResponse.getEntityString());
				} else {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not update parameter.");
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unknown action: \"" + action + "\"");
			return;
		}
		if (clientResponse != null) {
			clientResponse.release();
		}
		PrintWriter out = response.getWriter();
		String text = (responseBody != null) ? responseBody : "";
		out.print(text);
	}

}
