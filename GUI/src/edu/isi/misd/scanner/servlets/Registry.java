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
		String lib = request.getParameter("library");
		String dataset = request.getParameter("dataset");
		String func = request.getParameter("method");
		String site = request.getParameter("site");
		RegistryClientResponse clientResponse = null;
		if  (action.equals("getStudy")) {
			clientResponse = registryClient.getStudy(name);
			if (clientResponse != null) {
				res = clientResponse.toStudy();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get study.");
				return;
			}
		} else if (action.equals("getDataset")) {
			clientResponse = registryClient.getDataset(name, study);
			if (clientResponse != null) {
				res = clientResponse.toDataset();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get dataset.");
				return;
			}
		} else if (action.equals("getLibrary")) {
			clientResponse = registryClient.getLibrary(name);
			if (clientResponse != null) {
				res = clientResponse.toLibrary();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get library.");
				return;
			}
		} else if (action.equals("getMethod")) {
			clientResponse = registryClient.getMethod(name, lib);
			if (clientResponse != null) {
				res = clientResponse.toMethod();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get method.");
				return;
			}
		} else if (action.equals("getMaster")) {
			clientResponse = registryClient.getMaster();
			if (clientResponse != null) {
				res = clientResponse.toMaster();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get master.");
				return;
			}
		} else if (action.equals("getParameter")) {
			clientResponse = registryClient.getParameter(name, func, lib);
			if (clientResponse != null) {
				res = clientResponse.toParameter();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get parameter.");
				return;
			}
		} else if (action.equals("getWorker")) {
			clientResponse = registryClient.getWorker(study, dataset, lib, func, site);
			if (clientResponse != null) {
				res = clientResponse.toWorker();
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get worker.");
				return;
			}
		}  else if (action.equals("getSite")) {
			clientResponse = registryClient.getSite(name);
			if (clientResponse != null) {
				res = clientResponse.toSite();
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
			boolean valid = username != null && password != null && username.equals(tagfilerUser) && password.equals(tagfilerPassword);
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
		String description = request.getParameter("description"); 
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
			clientResponse = registryClient.createStudy(name, description, title,
					email, phone, website, address, contact, approvals);
			if (clientResponse != null) {
				responseBody = Utils.extractId(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create study.");
				return;
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
			clientResponse = registryClient.updateStudy(id, name,
					description, title, email, phone,
					website, address, contact, approvals);
			if (clientResponse != null) {
				responseBody = Utils.getEntity(clientResponse.getEntityString());
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not update study.");
				return;
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
