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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.isi.misd.scanner.client.ERDClient;
import edu.isi.misd.scanner.client.JakartaClient;
import edu.isi.misd.scanner.client.RegistryClient;
import edu.isi.misd.scanner.client.RegistryClientResponse;

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
		String studyId = request.getParameter("studyId");
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
		String userName = request.getParameter("userName");
		String email = request.getParameter("email");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String phone = request.getParameter("phone");
		String isSuperuser = request.getParameter("isSuperuser");
		String siteName = request.getParameter("siteName");
		String nodeName = request.getParameter("nodeName");
		String basePath = request.getParameter("basePath");
		String hostUrl = request.getParameter("hostUrl");
		String hostPort = request.getParameter("hostPort");
		String isMaster = request.getParameter("isMaster");
		
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
				clientResponse = registryClient.getAllStudyManagementPolicies();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get studies management policies.");
					return;
				}
				responseObject.put("studyManagementPolicies", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("responseBody: " + responseBody);
			} catch (JSONException e) {
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
				clientResponse.release();
				clientResponse = registryClient.getAllStudyManagementPolicies();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get all study management policies.");
					return;
				}
				responseObject.put("studyManagementPolicies", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("createStudyRequestedSites responseBody: " + responseBody);
			} catch (JSONException e) {
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
				responseBody = responseObject.toString();
				System.out.println("createSitePolicy responseBody: " + responseBody);
			} catch (JSONException e) {
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
				e.printStackTrace();
			}
		} else if (action.equals("createUser")) {
			clientResponse = registryClient.createUser(userName, email, 
					firstName, lastName, phone, Boolean.parseBoolean(isSuperuser));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create user.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("createUser:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				responseObject.put("user", clientResponse.getEntity());
				clientResponse.release();
				clientResponse = registryClient.getUsers();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get users.");
					return;
				}
				responseObject.put("users", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("createUser responseBody: " + responseBody);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createSite")) {
			clientResponse = registryClient.createSite(siteName, description);
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create site.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("createSite:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				responseObject.put("site", clientResponse.getEntity());
				clientResponse.release();
				clientResponse = registryClient.getAllSites();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get sites.");
					return;
				}
				responseObject.put("sites", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("createSite responseBody: " + responseBody);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createNode")) {
			clientResponse = registryClient.createNode(nodeName, hostUrl, Integer.parseInt(hostPort), basePath, description, Boolean.parseBoolean(isMaster), Integer.parseInt(siteId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create node.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("createNode:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				responseObject.put("node", clientResponse.getEntity());
				clientResponse.release();
				clientResponse = registryClient.getAllNodes();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get nodes.");
					return;
				}
				responseObject.put("nodes", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("createNode responseBody: " + responseBody);
			} catch (JSONException e) {
				e.printStackTrace();
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
				clientResponse = registryClient.getAllStudyManagementPolicies();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get studies management policies.");
					return;
				}
				responseObject.put("studyManagementPolicies", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("responseBody: " + responseBody);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("updateUser")) {
			clientResponse = registryClient.updateUser(Integer.parseInt(userId), userName, email, 
					firstName, lastName, phone, Boolean.parseBoolean(isSuperuser));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not update user.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("updateUser:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				responseObject.put("user", clientResponse.getEntity());
				clientResponse.release();
				clientResponse = registryClient.getUsers();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get users.");
					return;
				}
				responseObject.put("users", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("updateUser responseBody: " + responseBody);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("updateSite")) {
			clientResponse = registryClient.updateSite(Integer.parseInt(siteId), siteName, description);
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not update site.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("updateSite:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				responseObject.put("site", clientResponse.getEntity());
				clientResponse.release();
				clientResponse = registryClient.getAllSites();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get sites.");
					return;
				}
				responseObject.put("sites", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("updateSite responseBody: " + responseBody);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("updateNode")) {
			clientResponse = registryClient.updateNode(Integer.parseInt(nodeId), nodeName, hostUrl, Integer.parseInt(hostPort), basePath, description, Boolean.parseBoolean(isMaster), Integer.parseInt(siteId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not update node.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("updateNode:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				responseObject.put("node", clientResponse.getEntity());
				clientResponse.release();
				clientResponse = registryClient.getAllNodes();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get sites.");
					return;
				}
				responseObject.put("nodes", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("updateNode responseBody: " + responseBody);
			} catch (JSONException e) {
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
				e.printStackTrace();
			}
		} else if (action.equals("deleteUser")) {
			clientResponse = registryClient.deleteUser(Integer.parseInt(userId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete user.");
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				clientResponse.release();
				clientResponse = registryClient.getUsers();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get users.");
					return;
				}
				responseObject.put("users", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("deleteUser responseBody: " + responseBody);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteAnalyzePolicy")) {
			clientResponse = registryClient.deleteAnalyzePolicy(Integer.parseInt(analysisPolicyStatementId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete site policy.");
				return;
			}
			JSONObject responseObject = new JSONObject();
			clientResponse.release();
			responseBody = responseObject.toString();
			System.out.println("deleteSitePolicy responseBody: " + responseBody);
		} else if (action.equals("deleteSite")) {
			clientResponse = registryClient.deleteSite(Integer.parseInt(siteId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete site.");
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				clientResponse.release();
				clientResponse = registryClient.getAllSites();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get sites.");
					return;
				}
				responseObject.put("sites", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("deleteSite responseBody: " + responseBody);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteNode")) {
			clientResponse = registryClient.deleteNode(Integer.parseInt(nodeId));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not delete node.");
				return;
			}
			try {
				JSONObject responseObject = new JSONObject();
				clientResponse.release();
				clientResponse = registryClient.getAllNodes();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get nodes.");
					return;
				}
				responseObject.put("nodes", clientResponse.getEntityResponse());
				responseBody = responseObject.toString();
				System.out.println("deleteNode responseBody: " + responseBody);
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
