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

import edu.isi.misd.scanner.client.RegistryClient;
import edu.isi.misd.scanner.client.RegistryClientResponse;

/**
 * Servlet implementation class Registry
 * 
 * @author Serban Voinea
 */
@WebServlet(description = "Manage Registry", urlPatterns = { "/registry" })
public class Registry extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletConfig servletConfig;
	private String tagfilerURL;
	private String tagfilerUser;
	private String tagfilerPassword;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Registry() {
        super();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		servletConfig = config;
		tagfilerURL = servletConfig.getServletContext().getInitParameter("tagfilerURL");
		tagfilerUser = servletConfig.getServletContext().getInitParameter("tagfilerUser");
		tagfilerPassword = servletConfig.getServletContext().getInitParameter("tagfilerPassword");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		HttpSession session = request.getSession(false);
		RegistryClient registryClient = (RegistryClient) session.getAttribute("registryClient");
		JSONObject obj = new JSONObject();
		String name = request.getParameter("name");
		String displayName = request.getParameter("displayName");
		String parent = request.getParameter("parent");
		RegistryClientResponse clientResponse = null;
		if (action.equals("createStudy")) {
			clientResponse = registryClient.createStudy(name, displayName);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createDataset")) {
			clientResponse = registryClient.createDataset(name, displayName);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createLibrary")) {
			String resourcePath = request.getParameter("resourcePath");
			clientResponse = registryClient.createLibrary(name, displayName, resourcePath);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createFunction")) {
			String resourcePath = request.getParameter("resourcePath");
			clientResponse = registryClient.createFunction(name, displayName, resourcePath);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createMaster")) {
			String resourceURL = request.getParameter("resourceURL");
			String resourceStudy = request.getParameter("resourceStudy");
			String resourceDataset = request.getParameter("resourceDataset");
			String resourceLibrary = request.getParameter("resourceLibrary");
			String resourceFunction = request.getParameter("resourceFunction");
			clientResponse = registryClient.createMaster(name, displayName, resourceURL, resourceStudy, resourceDataset, resourceLibrary, resourceFunction);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createWorker")) {
			String resourceURL = request.getParameter("resourceURL");
			String resourceData = request.getParameter("resourceData");
			clientResponse = registryClient.createWorker(name, resourceData, resourceURL);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createParameter")) {
			try {
				int minOccurs = Integer.parseInt(request.getParameter("resourceMinOccurs")); 
				int maxOccurs = Integer.parseInt(request.getParameter("resourceMaxOccurs")); 
				String resourceValues = request.getParameter("resourceValues"); 
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
				clientResponse = registryClient.createParameter(name, displayName, minOccurs, maxOccurs, values);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("addDataset")) {
			clientResponse = registryClient.addDataset(name, parent);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("addDatasets")) {
			try {
				JSONArray datasets = new JSONArray(name);
				ArrayList<String> names = new ArrayList<String>();
				for (int i=0; i < datasets.length(); i++) {
					names.add(datasets.getString(i));
				}
				clientResponse = registryClient.addDataset(names, parent);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("addLibrary")) {
			clientResponse = registryClient.addLibrary(name, parent);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("addLibraries")) {
			try {
				JSONArray libs = new JSONArray(name);
				ArrayList<String> names = new ArrayList<String>();
				for (int i=0; i < libs.length(); i++) {
					names.add(libs.getString(i));
				}
				clientResponse = registryClient.addLibrary(names, parent);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("addFunction")) {
			clientResponse = registryClient.addFunction(name, parent);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("addFunctions")) {
			try {
				JSONArray funcs = new JSONArray(name);
				ArrayList<String> names = new ArrayList<String>();
				for (int i=0; i < funcs.length(); i++) {
					names.add(funcs.getString(i));
				}
				clientResponse = registryClient.addFunction(names, parent);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("addParameter")) {
			clientResponse = registryClient.addParameter(name, parent);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("addParameters")) {
			try {
				JSONArray params = new JSONArray(name);
				ArrayList<String> names = new ArrayList<String>();
				for (int i=0; i < params.length(); i++) {
					names.add(params.getString(i));
				}
				clientResponse = registryClient.addParameter(names, parent);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("addWorker")) {
			clientResponse = registryClient.addWorker(name, parent);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("addWorkers")) {
			try {
				JSONArray workers = new JSONArray(name);
				ArrayList<String> names = new ArrayList<String>();
				for (int i=0; i < workers.length(); i++) {
					names.add(workers.getString(i));
				}
				clientResponse = registryClient.addWorker(names, parent);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteFunctionParameter")) {
			clientResponse = registryClient.deleteParameter(name, parent);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteFunctionParameters")) {
			try {
				JSONArray params = new JSONArray(name);
				ArrayList<String> names = new ArrayList<String>();
				for (int i=0; i < params.length(); i++) {
					names.add(params.getString(i));
				}
				clientResponse = registryClient.deleteParameter(names, parent);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteMasterWorker")) {
			clientResponse = registryClient.deleteWorker(name, parent);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteMasterWorkers")) {
			try {
				JSONArray workers = new JSONArray(name);
				ArrayList<String> names = new ArrayList<String>();
				for (int i=0; i < workers.length(); i++) {
					names.add(workers.getString(i));
				}
				clientResponse = registryClient.deleteWorker(names, parent);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteLibraryFunction")) {
			clientResponse = registryClient.deleteFunction(name, parent);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteLibraryFunctions")) {
			try {
				JSONArray funcs = new JSONArray(name);
				ArrayList<String> names = new ArrayList<String>();
				for (int i=0; i < funcs.length(); i++) {
					names.add(funcs.getString(i));
				}
				clientResponse = registryClient.deleteFunction(names, parent);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteDatasetLibrary")) {
			clientResponse = registryClient.deleteLibrary(name, parent);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteDatasetLibraries")) {
			try {
				JSONArray libs = new JSONArray(name);
				ArrayList<String> names = new ArrayList<String>();
				for (int i=0; i < libs.length(); i++) {
					names.add(libs.getString(i));
				}
				clientResponse = registryClient.deleteLibrary(names, parent);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteStudyDataset")) {
			clientResponse = registryClient.deleteDataset(name, parent);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteStudyDatasets")) {
			try {
				JSONArray datasets = new JSONArray(name);
				ArrayList<String> names = new ArrayList<String>();
				for (int i=0; i < datasets.length(); i++) {
					names.add(datasets.getString(i));
				}
				clientResponse = registryClient.deleteDataset(names, parent);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteStudy")) {
			try {
				clientResponse = registryClient.deleteStudy(name);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteDataset")) {
			try {
				clientResponse = registryClient.deleteDataset(name);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteLibrary")) {
			try {
				clientResponse = registryClient.deleteLibrary(name);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteFunction")) {
			try {
				clientResponse = registryClient.deleteFunction(name);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteMaster")) {
			try {
				clientResponse = registryClient.deleteMaster(name);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteParameter")) {
			try {
				clientResponse = registryClient.deleteParameter(name);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteWorker")) {
			try {
				clientResponse = registryClient.deleteWorker(name);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("modifyLibrary")) {
			try {
				String resourcePath = request.getParameter("resourcePath");
				clientResponse = registryClient.modifyLibrary(name, displayName, resourcePath);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("modifyFunction")) {
			try {
				String resourcePath = request.getParameter("resourcePath");
				clientResponse = registryClient.modifyFunction(name, displayName, resourcePath);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("modifyMaster")) {
			try {
				String resourceURL = request.getParameter("resourceURL");
				String resourceStudy = request.getParameter("resourceStudy");
				String resourceDataset = request.getParameter("resourceDataset");
				String resourceLibrary = request.getParameter("resourceLibrary");
				String resourceFunction = request.getParameter("resourceFunction");
				clientResponse = registryClient.modifyMaster(name, resourceURL, resourceStudy, resourceDataset, resourceLibrary, resourceFunction);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("modifyWorker")) {
			try {
				String resourceURL = request.getParameter("resourceURL");
				String resourceData = request.getParameter("resourceData");
				clientResponse = registryClient.modifyWorker(name, resourceData, resourceURL);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("modifyParameter")) {
			try {
				int minOccurs = Integer.parseInt(request.getParameter("resourceMinOccurs")); 
				int maxOccurs = Integer.parseInt(request.getParameter("resourceMaxOccurs")); 
				String resourceValues = request.getParameter("resourceValues"); 
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
				clientResponse = registryClient.modifyParameter(name, displayName, minOccurs, maxOccurs, values);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("getMasters")) {
			try {
				String resourceStudy = request.getParameter("resourceStudy");
				String resourceDataset = request.getParameter("resourceDataset");
				String resourceLibrary = request.getParameter("resourceLibrary");
				String resourceFunction = request.getParameter("resourceFunction");
				clientResponse = registryClient.getMasters(resourceStudy, resourceDataset, resourceLibrary, resourceFunction);
				//System.out.println("Masters: " + clientResponse.toMasters());
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("getStudy")) {
			try {
				clientResponse = registryClient.getStudy(name);
				//System.out.println("Study: " + clientResponse.toStudy());
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("getDataset")) {
			try {
				clientResponse = registryClient.getDataset(name);
				//System.out.println("Dataset: " + clientResponse.toDataset());
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("getLibrary")) {
			try {
				clientResponse = registryClient.getLibrary(name);
				//System.out.println("Library: " + clientResponse.toLibrary());
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("getFunction")) {
			try {
				clientResponse = registryClient.getFunction(name);
				//System.out.println("Function: " + clientResponse.toFunction());
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("getMaster")) {
			try {
				clientResponse = registryClient.getMaster(name);
				//System.out.println("Master: " + clientResponse.toMaster());
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("getParameter")) {
			try {
				clientResponse = registryClient.getParameter(name);
				//System.out.println("Parameter: " + clientResponse.toParameter());
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("getWorker")) {
			try {
				clientResponse = registryClient.getWorker(name);
				//System.out.println("Worker: " + clientResponse.toWorker());
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (clientResponse != null) {
			clientResponse.release();
		}
		PrintWriter out = response.getWriter();
		String text = obj.toString();
		out.print(text);
	}

}
