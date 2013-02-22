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

import edu.isi.misd.scanner.client.JakartaClient;
import edu.isi.misd.scanner.client.RegistryClient;
import edu.isi.misd.scanner.client.RegistryClientResponse;
import edu.isi.misd.scanner.client.TagfilerClient;
import edu.isi.misd.scanner.client.JakartaClient.ClientURLResponse;
import edu.isi.misd.scanner.utils.Utils;

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
		String action = request.getParameter("action");
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
		String lib = request.getParameter("library");
		String dataset = request.getParameter("dataset");
		String func = request.getParameter("method");
		String site = request.getParameter("site");
		String sites = request.getParameter("sites");
		RegistryClientResponse clientResponse = null;
		if  (action.equals("getStudy")) {
			clientResponse = registryClient.getStudy(name);
			res= clientResponse.toStudy();
		} else if (action.equals("getDataset")) {
			clientResponse = registryClient.getDataset(name, study);
			res= clientResponse.toDataset();
		} else if (action.equals("getLibrary")) {
			clientResponse = registryClient.getLibrary(name);
			res= clientResponse.toLibrary();
		} else if (action.equals("getMethod")) {
			clientResponse = registryClient.getMethod(name, lib);
			res= clientResponse.toMethod();
		} else if (action.equals("getMaster")) {
			clientResponse = registryClient.getMaster();
			res= clientResponse.toMaster();
		} else if (action.equals("getParameter")) {
			clientResponse = registryClient.getParameter(name, func, lib);
			res= clientResponse.toParameter();
		} else if (action.equals("getWorker")) {
			clientResponse = registryClient.getWorker(study, dataset, lib, func, site);
			res= clientResponse.toWorker();
		}  else if (action.equals("getSite")) {
			ArrayList<String> values = null;
			if (sites != null) {
				try {
					JSONArray arr = new JSONArray(sites);
					if (arr.length() > 0) {
						values = new ArrayList<String>();
						for (int i=0; i < arr.length(); i++) {
							values.add(arr.getString(i));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			clientResponse = registryClient.getSite(values);
			res= clientResponse.toSite();
		} else if (action.equals("getStudies")) {
			clientResponse = registryClient.getStudies();
			res= clientResponse.toStudies();
		} else if (action.equals("getDatasets")) {
			clientResponse = registryClient.getDatasets(study);
			res= clientResponse.toDatasets();
		} else if (action.equals("getLibraries")) {
			clientResponse = registryClient.getLibraries();
			res= clientResponse.toLibraries();
		} else if (action.equals("getMethods")) {
			clientResponse = registryClient.getMethods(lib);
			res= clientResponse.toMethods();
		} else if (action.equals("getParameters")) {
			clientResponse = registryClient.getParameters(func, lib);
			res= clientResponse.toParameters();
		} else if (action.equals("getSites")) {
			clientResponse = registryClient.getSites(study, dataset, lib, func);
			res= clientResponse.toSites();
		} else if (action.equals("getWorkers")) {
			ArrayList<String> values = null;
			if (sites != null) {
				try {
					JSONArray arr = new JSONArray(sites);
					if (arr.length() > 0) {
						values = new ArrayList<String>();
						for (int i=0; i < arr.length(); i++) {
							values.add(arr.getString(i));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			clientResponse = registryClient.getWorkers(study, dataset, lib, func, values);
			res= clientResponse.toWorkers();
		} else if (action.equals("getNodeLibraries")) {
			clientResponse = registryClient.getNodeLibraries(site);
			res= clientResponse.toLibraries();
		} else if (action.equals("getNodeExtracts")) {
			clientResponse = registryClient.getNodeExtracts(site);
			res= clientResponse.toDatasets();
		} else if (action.equals("getDatasetSites")) {
			clientResponse = registryClient.getDatasetSites(study, dataset);
			res= clientResponse.toSites();
		}
		
		if (clientResponse != null) {
			clientResponse.release();
		}
		PrintWriter out = response.getWriter();
		out.print(res);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action.equals("login")) {
			HttpSession session = request.getSession(true);
			if (session.isNew() == false) {
				session.invalidate();
				session = request.getSession(true);
			}  
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			boolean valid = Utils.login(request, response, username, password);
			if (valid) {
				session.setAttribute("user", username);
				JakartaClient client = new JakartaClient(4, 8192, 120000);
				session.setAttribute("httpClient", client);
				ClientURLResponse rsp = client.login(tagfilerURL + "/session", tagfilerUser, tagfilerPassword);
				if (rsp != null) {
					//rsp.debug();
					session.setAttribute("tagfilerCookie", client.getCookieValue());
					//RegistryClient registryClient = new TagfilerClient(httpClient, tagfilerURL, httpClient.getCookieValue(), request);
					RegistryClient registryClient = new TagfilerClient(client, tagfilerURL, client.getCookieValue());
					session.setAttribute("registryClient", registryClient);
					PrintWriter out = response.getWriter();
					out.print(client.getCookieValue() + "\n");
				} else {
					PrintWriter out = response.getWriter();
					out.print("Can not access registry service.\n");
				}
			} else {
				PrintWriter out = response.getWriter();
				out.print("Invalid username or password.\n");
			}
			return;
		}
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
		String func = request.getParameter("method");
		String site = request.getParameter("site");
		String resourceValues = request.getParameter("values"); 
		Integer minOccurs = null; 
		Integer maxOccurs = null; 
		if (request.getParameter("minOccurs") != null) {
			minOccurs = Integer.parseInt(request.getParameter("minOccurs"));
		}
		if (request.getParameter("maxOccurs") != null) {
			maxOccurs = Integer.parseInt(request.getParameter("maxOccurs"));
		}
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
		} else if (action.equals("createMethod")) {
			clientResponse = registryClient.createMethod(name, lib, rpath);
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
			clientResponse = registryClient.createWorker(study, dataset, lib, func, site, datasource);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("createSite")) {
			clientResponse = registryClient.createSite(name, rURL);
			try {
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
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
				clientResponse = registryClient.createParameter(name, func, lib, minOccurs, maxOccurs, values);
				obj.put("status", clientResponse.getStatus());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("deleteMethod")) {
			clientResponse = registryClient.deleteMethod(name, lib);
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
		}  else if (action.equals("deleteSite")) {
			clientResponse = registryClient.deleteSite(name);
			try {
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
				if (clientResponse != null) {
					obj.put("status", clientResponse.getStatus());
				} else {
					obj.put("status", "No library update");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("updateMethod")) {
			try {
				clientResponse = registryClient.updateMethod(id, name, lib, rpath);
				if (clientResponse != null) {
					obj.put("status", clientResponse.getStatus());
				} else {
					obj.put("status", "No method update");
				}
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
		}  else if (action.equals("updateSite")) {
			try {
				clientResponse = registryClient.updateSite(id, name, rURL);
				if (clientResponse != null) {
					obj.put("status", clientResponse.getStatus());
				} else {
					obj.put("status", "No site update");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("updateWorker")) {
			try {
				clientResponse = registryClient.updateWorker(id, study, dataset, lib, func, site, datasource);
				if (clientResponse != null) {
					obj.put("status", clientResponse.getStatus());
				} else {
					obj.put("status", "No worker update");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (action.equals("updateParameter")) {
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
				clientResponse = registryClient.updateParameter(id, name, func, lib, minOccurs, maxOccurs, values);
				if (clientResponse != null) {
					obj.put("status", clientResponse.getStatus());
				} else {
					obj.put("status", "No parameter update");
				}
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
