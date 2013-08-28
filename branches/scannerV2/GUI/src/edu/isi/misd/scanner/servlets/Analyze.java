package edu.isi.misd.scanner.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
import edu.isi.misd.scanner.client.ERDClientResponse;
import edu.isi.misd.scanner.client.RegistryClient;
import edu.isi.misd.scanner.client.RegistryClientResponse;
import edu.isi.misd.scanner.client.ScannerClient;
import edu.isi.misd.scanner.client.JakartaClient.ClientURLResponse;
import edu.isi.misd.scanner.utils.Utils;

/**
 * Servlet implementation class Analyze
 */
@WebServlet(description = "Analyze", urlPatterns = { "/query" })
public class Analyze extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletConfig servletConfig;
	private String trustStoreType;
	private String trustStorePassword;
	private String trustStoreResource;
	private String keyStoreType;
	private String keyStorePassword;
	private String keyStoreResource;
	private String keyManagerPassword;
       
	private String erdURL;
	private String webContentPath;
	private JSONArray retrievedStudies;
	private JSONArray retrievedDatasets;
	private JSONArray retrievedLibraries;
	private JSONArray retrievedTools;
	private JSONArray retrievedDatasetInstances;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Analyze() {
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
		erdURL = "http://aspc.isi.edu:8088/scannerV2/registry/";
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		HttpSession session = request.getSession(false);
		RegistryClient registryClient = (RegistryClient) session.getAttribute("registryClient");
		if (!registryClient.hasRoles()) {
			PrintWriter out = response.getWriter();
			out.print("{}");
			return;
		}
		if (action.equals("getStudies")) {
			RegistryClientResponse clientResponse = registryClient.getStudies();
			retrievedStudies = clientResponse.getEntityResponse();
			String ret = clientResponse.toStudies();
			clientResponse.release();
			System.out.println("Analyze Get Studies:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getDatasets")) {
			String study = request.getParameter("study");
			RegistryClientResponse clientResponse = registryClient.getDatasets(study);
			retrievedDatasets = clientResponse.getEntityResponse();
			String ret = clientResponse.toDatasets();
			clientResponse.release();
			System.out.println("Analyze Get Datasets: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getLibraries")) {
			String study = request.getParameter("study");
			String dataset = request.getParameter("dataset");
			String sites = request.getParameter("site");
			RegistryClientResponse clientResponse = registryClient.getLibraries(study, dataset, sites);
			retrievedLibraries = clientResponse.getEntityResponse();
			String ret = clientResponse.toLibraries();
			clientResponse.release();
			System.out.println("Analyze Get Libraries: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getMethods")) {
			String study = request.getParameter("study");
			String dataset = request.getParameter("dataset");
			String lib = request.getParameter("library");
			RegistryClientResponse clientResponse = registryClient.getMethods(study, dataset, "" + getLibraryId(lib));
			retrievedTools = clientResponse.getEntityResponse();
			String ret = clientResponse.toMethods();
			clientResponse.release();
			System.out.println("Analyze Get Methods: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getParameters")) {
			String dataset = request.getParameter("dataset");
			RegistryClientResponse clientResponse = registryClient.getParameters(dataset, webContentPath + "etc/parameterTypes/LogisticRegressionBase.json");
			String ret = clientResponse.toParameters();
			clientResponse.release();
			System.out.println("Analyze Get Parameters:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getSites")) {
			String study = request.getParameter("study");
			String dataset = request.getParameter("dataset");
			RegistryClientResponse clientResponse = registryClient.getSites(study, dataset);
			retrievedDatasetInstances = clientResponse.getEntityResponse();
			String ret = clientResponse.toSites(dataset);
			clientResponse.release();
			System.out.println("Analyze Get Sites:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getUsers")) {
			RegistryClientResponse clientResponse = registryClient.getUsers();
			JSONArray ret = clientResponse.toUsers();
			clientResponse.release();
			System.out.println("Analyze Get Users:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret.toString());
		} else if (action.equals("getNodes")) {
			RegistryClientResponse clientResponse = registryClient.getNodes();
			JSONArray ret = clientResponse.toNodes();
			clientResponse.release();
			System.out.println("Analyze Get Nodes:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret.toString());
		} else if (action.equals("getTools")) {
			RegistryClientResponse clientResponse = registryClient.getTools();
			String ret = clientResponse.toTools().toString();
			clientResponse.release();
			System.out.println("Analyze Get Tools: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getDatasetInstances")) {
			RegistryClientResponse clientResponse = registryClient.getDatasetInstances();
			String ret = clientResponse.toDatasetInstances().toString();
			clientResponse.release();
			System.out.println("Analyze Get Tools: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getDatasetDefinitions")) {
			RegistryClientResponse clientResponse = registryClient.getDatasetDefinitions();
			String ret = clientResponse.toDatasetDefinitions().toString();
			clientResponse.release();
			System.out.println("Analyze Get Tools: "+ret);
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
			ScannerClient scannerClient = (ScannerClient) session.getAttribute("scannerClient");
			if (action.equals("loginRegistry")) {
				obj.put("status", "login error");
				RegistryClient registryClient = new ERDClient(erdURL, (String) session.getAttribute("user"));
				session.setAttribute("registryClient", registryClient);
				/*
				RegistryClientResponse clientResponse = registryClient.getContacts();
				String ret = clientResponse.toContacts();
				clientResponse.release();
				JSONArray arr = new JSONArray(ret);
				obj.put("contacts", arr);
				System.out.println("Get Contacts:\n"+ret);
				*/
				scannerClient = new ScannerClient(4, 8192, 300000,
						trustStoreType, trustStorePassword, trustStoreResource,
						keyStoreType, keyStorePassword, keyStoreResource, keyManagerPassword);
				session.setAttribute("scannerClient", scannerClient);
				obj.put("status", "success");
			} else if (action.equals("logout")) {
				System.out.println("Logout successfully");
				obj.put("status", "success");
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
					String dataset = request.getParameter("dataset");
					String study = request.getParameter("study");
					RegistryClientResponse clientResponse = registryClient.getMasterObject();
					String res = clientResponse.toMasterString();
					clientResponse.release();
					System.out.println("master string: " + res);
					JSONObject temp = new JSONObject(res);
					String masterURL = temp.getString("hostUrl") + ":" + temp.getString("hostPort") + temp.getString("basePath");
					clientResponse = new ERDClientResponse(retrievedTools);
					res = clientResponse.toMethodString(func, "" + getLibraryId(lib));
					clientResponse.release();
					System.out.println("method string: " + res);
					temp = new JSONObject(res);
					String funcPath = temp.getString("toolPath").substring(1);
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
					clientResponse = new ERDClientResponse(retrievedDatasetInstances);
					res = clientResponse.toSiteString(values, dataset);
					clientResponse.release();
					System.out.println("site string: " + res);
					JSONArray targets = new JSONArray(res);
					StringBuffer buff = new StringBuffer();
					for (int i=0; i < targets.length(); i++) {
						if (i > 0) {
							buff.append(",");
						}
						temp = targets.getJSONObject(i);
						String dataSource = temp.getString("dataSource");
						JSONObject node = temp.getJSONObject("node");
						buff.append(node.getString("hostUrl")).append(":").append(node.getString("hostPort")).append(node.getString("basePath")).append(funcPath).append("?dataSource=").append(Utils.urlEncode(dataSource));
						if ((node.getString("hostUrl") + ":" + node.getString("hostPort")).equals("https://scanner-node1.misd.isi.edu:8888") && lib.equals("OCEANS")) {
							buff.append("&resultsReleaseAuthReq=true");
						}
					}
					String targetsURLs = buff.toString();
					buff = new StringBuffer();
					buff.append(masterURL).append(funcPath);
					String trxId = request.getParameter("trxId");
					String url = null;
					ClientURLResponse rsp = null;
					String rspId = null;
					obj = new JSONObject();
					System.out.println("Study Id: " + getStudyId(study));
					System.out.println("Dataset Id: " + getDatasetId(dataset));
					System.out.println("Library Id: " + getLibraryId(lib));
					System.out.println("Method Id: " + getMethodId(lib, func));
					System.out.println("Dataset Instances Ids: " + getDatasetInstancesIds(values));
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
							temp = targets.getJSONObject(i);
							JSONObject node = temp.getJSONObject("node");
							buff.append(node.getString("hostUrl")).append(":").append(node.getString("hostPort")).append(node.getString("basePath")).append(funcPath);
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
	
	int getStudyId(String studyName) {
		int ret = -1;
		try {
			for (int i=0; i < retrievedStudies.length(); i++) {
				JSONObject obj = retrievedStudies.getJSONObject(i);
				if (obj.getString("studyName").equals(studyName)) {
					ret = obj.getInt("studyId");
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	int getDatasetId(String dataSetName) {
		int ret = -1;
		try {
			for (int i=0; i < retrievedDatasets.length(); i++) {
				JSONObject obj = retrievedDatasets.getJSONObject(i);
				if (obj.getString("dataSetName").equals(dataSetName)) {
					ret = obj.getInt("dataSetDefinitionId");
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	int getLibraryId(String libraryName) {
		int ret = -1;
		try {
			for (int i=0; i < retrievedLibraries.length(); i++) {
				JSONObject obj = retrievedLibraries.getJSONObject(i);
				if (obj.getString("libraryName").equals(libraryName)) {
					ret = obj.getInt("libraryId");
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	int getMethodId(String libraryName, String toolName) {
		int ret = -1;
		try {
			JSONObject lib = null;
			for (int i=0; i < retrievedLibraries.length(); i++) {
				JSONObject obj = retrievedLibraries.getJSONObject(i);
				if (obj.getString("libraryName").equals(libraryName)) {
					lib = obj;
					break;
				}
			}
			JSONArray analysisTools = lib.getJSONArray("analysisTools");
			for (int i=0; i < analysisTools.length(); i++) {
				JSONObject obj = analysisTools.getJSONObject(i);
				if (obj.getString("toolName").equals(toolName)) {
					ret = obj.getInt("toolId");
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	JSONArray getDatasetInstancesIds(ArrayList<String> values) {
		JSONArray ret = new JSONArray();
		try {
			for (int i=0; i < retrievedDatasetInstances.length(); i++) {
				JSONObject instance = retrievedDatasetInstances.getJSONObject(i);
				JSONObject node = instance.getJSONObject("node");
				String site = node.getString("site") + ":" + node.getInt("nodeId");
				if (values.contains(site)) {
					ret.put(instance.getInt("dataSetInstanceId"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

}
