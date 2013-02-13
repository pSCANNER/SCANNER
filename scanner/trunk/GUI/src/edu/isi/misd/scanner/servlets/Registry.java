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
		String id = request.getParameter("id");
		String name = request.getParameter("cname");
		String study = request.getParameter("study");
		String rpath = request.getParameter("rpath");
		String lib = request.getParameter("library");
		String rURL = request.getParameter("rURL");
		String datasource = request.getParameter("datasource");
		String dataset = request.getParameter("dataset");
		String func = request.getParameter("function");
		String site = request.getParameter("site");
		String resourceValues = request.getParameter("values"); 
		String sourceData = request.getParameter("sourcedata");
		RegistryClientResponse clientResponse = null;
		if (action.equals("createStudy")) {
			clientResponse = registryClient.createStudy(name);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createDataset")) {
			clientResponse = registryClient.createDataset(name, study);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createLibrary")) {
			clientResponse = registryClient.createLibrary(name, rpath);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createFunction")) {
			clientResponse = registryClient.createFunction(name, lib, rpath);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createMaster")) {
			clientResponse = registryClient.createMaster(rURL);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createWorker")) {
			clientResponse = registryClient.createWorker(study, dataset, lib, func, site, datasource, rURL);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createParameter")) {
			try {
				int minOccurs = Integer.parseInt(request.getParameter("minOccurs")); 
				int maxOccurs = Integer.parseInt(request.getParameter("maxOccurs")); 
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
				clientResponse = registryClient.createParameter(name, func, lib, minOccurs, maxOccurs, values);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteFunction")) {
			clientResponse = registryClient.deleteFunction(name, lib);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteLibrary")) {
			clientResponse = registryClient.deleteLibrary(name);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteDataset")) {
			clientResponse = registryClient.deleteDataset(name, study);
			try {
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
		} else if (action.equals("deleteMaster")) {
			try {
				clientResponse = registryClient.deleteMaster();
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteParameter")) {
			try {
				clientResponse = registryClient.deleteParameter(name, func, lib);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteWorker")) {
			try {
				clientResponse = registryClient.deleteWorker(study, dataset, lib, func, site);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("updateLibrary")) {
			try {
				clientResponse = registryClient.updateLibrary(id, name, rpath);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("updateFunction")) {
			try {
				clientResponse = registryClient.updateFunction(id, name, lib, rpath);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("updateMaster")) {
			try {
				clientResponse = registryClient.updateMaster(rURL);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("updateWorker")) {
			try {
				clientResponse = registryClient.updateWorker(id, study, dataset, lib, func, site, sourceData, rURL);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("updateParameter")) {
			try {
				int minOccurs = Integer.parseInt(request.getParameter("minOccurs")); 
				int maxOccurs = Integer.parseInt(request.getParameter("maxOccurs")); 
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
				clientResponse = registryClient.updateParameter(id, name, func, lib, minOccurs, maxOccurs, values);
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
				clientResponse = registryClient.getDataset(name, study);
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
				clientResponse = registryClient.getFunction(name, lib);
				//System.out.println("Function: " + clientResponse.toFunction());
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("getMaster")) {
			try {
				clientResponse = registryClient.getMaster();
				//System.out.println("Master: " + clientResponse.toMaster());
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("getParameter")) {
			try {
				clientResponse = registryClient.getParameter(name, func, lib);
				//System.out.println("Parameter: " + clientResponse.toParameter());
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("getWorker")) {
			try {
				clientResponse = registryClient.getWorker(study, dataset, lib, func, site);
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
