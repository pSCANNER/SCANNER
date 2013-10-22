package edu.isi.misd.scanner.network.portal.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

import edu.isi.misd.scanner.network.portal.client.ERDClient;
import edu.isi.misd.scanner.network.portal.client.ERDClientResponse;
import edu.isi.misd.scanner.network.portal.client.RegistryClient;
import edu.isi.misd.scanner.network.portal.client.RegistryClientResponse;
import edu.isi.misd.scanner.network.portal.client.ScannerClient;
import edu.isi.misd.scanner.network.portal.client.JakartaClient.ClientURLResponse;
import edu.isi.misd.scanner.network.portal.utils.Utils;

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
	private JSONArray ptrDatasetInstances;
	private JSONObject ptrTool;
       
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
		erdURL = servletConfig.getServletContext().getInitParameter("registryURL");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		RegistryClient registryClient = (RegistryClient) session.getAttribute("registryClient");
		if (action.equals("getStudies")) {
			RegistryClientResponse clientResponse = registryClient.getStudies();
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get studies.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			retrievedStudies = clientResponse.getEntityResponse();
			String ret = clientResponse.toStudies();
			clientResponse.release();
			System.out.println("Analyze Get Studies:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getDatasets")) {
			String study = request.getParameter("study");
			RegistryClientResponse clientResponse = null;
			if (getStudyId(study) == -1) {
				clientResponse = registryClient.getStudies();
				if (clientResponse == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get studies.");
					return;
				} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
					System.out.println("Result:\n" + clientResponse.getEntity());
					response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
					return;
				}
				retrievedStudies = clientResponse.getEntityResponse();
				clientResponse.release();
			}
			clientResponse = registryClient.getDatasets(getStudyId(study));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get datasets for study " + study);
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
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
			RegistryClientResponse clientResponse = registryClient.getLibraries(getStudyId(study), getDatasetId(dataset), sites);
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get libraries for study " + study + " and dataset " + dataset);
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
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
			RegistryClientResponse clientResponse = registryClient.getMethods(getStudyId(study), getDatasetId(dataset), getLibraryId(lib));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get methods for study " + study + " and dataset " + dataset + " and library " + lib);
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			retrievedTools = clientResponse.getEntityResponse();
			String ret = clientResponse.toMethods();
			clientResponse.release();
			System.out.println("Analyze Get Methods: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getParameters")) {
			String dataset = request.getParameter("dataset");
			RegistryClientResponse clientResponse = registryClient.getParameters(getDatasetId(dataset), webContentPath + "etc/parameterTypes/LogisticRegressionBase.json");
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get parameters.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			String ret = clientResponse.toParameters();
			clientResponse.release();
			System.out.println("Analyze Get Parameters:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getSites")) {
			String study = request.getParameter("study");
			String dataset = request.getParameter("dataset");
			RegistryClientResponse clientResponse = registryClient.getSites(getStudyId(study), getDatasetId(dataset));
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get sites for study " + study + " and dataset " + dataset);
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			retrievedDatasetInstances = clientResponse.getEntityResponse();
			String ret = clientResponse.toSites(dataset);
			clientResponse.release();
			System.out.println("Analyze Get Sites:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getUsers")) {
			RegistryClientResponse clientResponse = registryClient.getUsers();
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get users.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			JSONArray ret = clientResponse.toUsers();
			clientResponse.release();
			System.out.println("Analyze Get Users:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret.toString());
		} else if (action.equals("getNodes")) {
			RegistryClientResponse clientResponse = registryClient.getNodes();
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get nodes.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			JSONArray ret = clientResponse.toNodes();
			clientResponse.release();
			System.out.println("Analyze Get Nodes:\n"+ret);
			PrintWriter out = response.getWriter();
			out.print(ret.toString());
		} else if (action.equals("getTools")) {
			RegistryClientResponse clientResponse = registryClient.getTools();
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get methods.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			String ret = clientResponse.toTools().toString();
			clientResponse.release();
			System.out.println("Analyze Get Tools: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getDatasetInstances")) {
			RegistryClientResponse clientResponse = registryClient.getDatasetInstances();
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get dataset instances.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			String ret = clientResponse.toDatasetInstances().toString();
			clientResponse.release();
			System.out.println("Analyze Get Dataset Instances: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getDatasetDefinitions")) {
			RegistryClientResponse clientResponse = registryClient.getDatasetDefinitions();
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get dataset definitions.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			String ret = clientResponse.toDatasetDefinitions().toString();
			clientResponse.release();
			System.out.println("Analyze Get Datasets: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getUserRoles")) {
			RegistryClientResponse clientResponse = registryClient.getUserRoles();
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get user roles.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			String ret = clientResponse.toUserRoles().toString();
			clientResponse.release();
			System.out.println("Analyze Get user roles: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getStudyPolicies")) {
			RegistryClientResponse clientResponse = registryClient.getStudyPolicies();
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get study policies.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			String ret = clientResponse.toStudyPolicies().toString();
			clientResponse.release();
			System.out.println("Analyze Get studies policies: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getStudyRoles")) {
			RegistryClientResponse clientResponse = registryClient.getStudyRoles();
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get study roles.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			String ret = clientResponse.toStudyRoles().toString();
			clientResponse.release();
			System.out.println("Analyze Get studies roles: "+ret);
			PrintWriter out = response.getWriter();
			out.print(ret);
		} else if (action.equals("getAnalysisPolicies")) {
			RegistryClientResponse clientResponse = registryClient.getAnalysisPolicies();
			if (clientResponse == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get analysis policies.");
				return;
			} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
				System.out.println("Result:\n" + clientResponse.getEntity());
				response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
				return;
			}
			String ret = clientResponse.getEntityResponse().toString();
			clientResponse.release();
			System.out.println("Analyze Get Analysis Policies: "+ret);
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
			if (session == null) {
				return;
			}
			ScannerClient scannerClient = (ScannerClient) session.getAttribute("scannerClient");
			if (action.equals("loginRegistry")) {
				obj.put("status", "login error");
				RegistryClient registryClient = new ERDClient(erdURL, (String) session.getAttribute("user"));
				session.setAttribute("registryClient", registryClient);
				RegistryClientResponse rsp = registryClient.getUser((String) session.getAttribute("user"));
				if (rsp == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get user " + session.getAttribute("user"));
					return;
				} else if (rsp.getStatus() != HttpServletResponse.SC_OK) {
					System.out.println("Result:\n" + rsp.getEntity());
					response.sendError(rsp.getStatus(), rsp.getErrorMessage());
					return;
				}
				JSONArray user = rsp.getEntityResponse();
				if (user != null) {
					session.setAttribute("userId", user.getJSONObject(0).getInt("userId"));
				} else {
					// to handle error case "user not found"
				}
				rsp.release();
				rsp = registryClient.getUserRoles((String) session.getAttribute("user"));
				if (rsp == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get roles for user " + session.getAttribute("user"));
					return;
				} else if (rsp.getStatus() != HttpServletResponse.SC_OK) {
					System.out.println("Result:\n" + rsp.getEntity());
					response.sendError(rsp.getStatus(), rsp.getErrorMessage());
					return;
				}
				JSONArray userRoles = rsp.getEntityResponse();
				if (user != null) {
					session.setAttribute("userId", user.getJSONObject(0).getInt("userId"));
				} else {
					// to handle error case "user not found"
				}
				rsp.release();
				scannerClient = new ScannerClient(4, 8192, 300000,
						trustStoreType, trustStorePassword, trustStoreResource,
						keyStoreType, keyStorePassword, keyStoreResource, keyManagerPassword);
				session.setAttribute("scannerClient", scannerClient);
				if (user != null) {
					obj.put("status", "success");
					obj.put("user", user);
					obj.put("userRoles", userRoles);
				}
			} else if (action.equals("logout")) {
				System.out.println("Logout successfully");
				obj.put("status", "success");
			} else if (action.equals("getResultsAsync")) {
				try {
					RegistryClient registryClient = (RegistryClient) session.getAttribute("registryClient");
					String userName = (String) session.getAttribute("user");
					String params = request.getParameter("parameters");
					String sites = request.getParameter("sites");
					String lib = request.getParameter("library");
					String func = request.getParameter("method");
					String dataset = request.getParameter("dataset");
					String study = request.getParameter("study");
					String analysisId = request.getParameter("analysisId");
					RegistryClientResponse clientResponse = registryClient.getMasterObject();
					if (clientResponse == null) {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get master node.");
						return;
					} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
						System.out.println("Result:\n" + clientResponse.getEntity());
						response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
						return;
					}
					String res = clientResponse.toMasterString();
					clientResponse.release();
					System.out.println("master string: " + res);
					JSONObject temp = new JSONObject(res);
					String masterURL = temp.getString("hostUrl") + ":" + temp.getString("hostPort") + temp.getString("basePath");
					int masterId = temp.getInt("nodeId");
					StringBuffer buff = new StringBuffer();
					boolean isAuthorized = true;
					String funcPath = null;
					String url = null;
					ClientURLResponse rsp = null;
					String responseBody = null;
					if (analysisId != null) {
						// check that the user authorization for this transactionId
						isAuthorized = true;
						clientResponse = registryClient.getHistory(Integer.parseInt(analysisId));
						if (clientResponse == null) {
							response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get history for analysis " + analysisId);
							return;
						} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
							System.out.println("Result:\n" + clientResponse.getEntity());
							response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
							return;
						}
						JSONObject history = clientResponse.getEntity();
						clientResponse.release();
						JSONArray instances = null;
						// check authorization
						String user = (String) session.getAttribute("user");
						int analysisToolId = history.getInt("analysisTool");
						clientResponse = registryClient.getTools(analysisToolId);
						if (clientResponse == null) {
							response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get method for analysisToolId " + analysisToolId);
							return;
						} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
							System.out.println("Result:\n" + clientResponse.getEntity());
							response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
							return;
						}
						JSONObject tool = clientResponse.getEntity();
						clientResponse.release();
						funcPath = tool.getString("toolPath").substring(1);
						instances = history.getJSONArray("analysisResults");
						for (int i=0; i < instances.length(); i++) {
							JSONObject instance = instances.getJSONObject(i);
							RegistryClientResponse resp = registryClient.getAnalysisPolicies(user, analysisToolId, instance.getInt("dataSetInstanceId"));
							if (resp == null) {
								response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get analysis policies for user " + user + " and analysisToolId " + analysisToolId + " and instance " + instance.getInt("dataSetInstanceId"));
								return;
							} else if (resp.getStatus() != HttpServletResponse.SC_OK) {
								System.out.println("Result:\n" + resp.getEntity());
								response.sendError(resp.getStatus(), resp.getErrorMessage());
								return;
							}
							JSONArray policy = resp.getEntityResponse();
							if (policy.length() == 0) {
								isAuthorized = false;
								break;
							}
						}
						if (!isAuthorized) {
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
							return;
						}
						buff = new StringBuffer();
						buff.append(masterURL).append(funcPath);
						String transactionId = Utils.urlEncode(history.getString("transactionId"));
						buff.append("/id/" + transactionId);
						url = buff.toString();
						System.out.println("URL: " + url);
						buff = new StringBuffer();
						for (int i=0; i < instances.length(); i++) {
							if (i > 0) {
								buff.append(",");
							}
							String targetURL = instances.getJSONObject(i).getString("url");
							buff.append(targetURL);
						}
						System.out.println("History URL: " + url + "\nHistory Targets: " + buff.toString());
						rsp = scannerClient.get(url, buff);
						responseBody = rsp.getEntityString();
					} else {
						clientResponse = registryClient.getMethods(getStudyId(study), getDatasetId(dataset), getLibraryId(lib));
						if (clientResponse == null) {
							response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get methods for study " + study + " and dataset " + dataset + " and library " + lib);
							return;
						} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
							System.out.println("Result:\n" + clientResponse.getEntity());
							response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
							return;
						}
						retrievedTools = clientResponse.getEntityResponse();
						clientResponse = new ERDClientResponse(retrievedTools);
						res = clientResponse.toMethodString(func, "" + getLibraryId(lib));
						clientResponse.release();
						System.out.println("method string: " + res);
						temp = new JSONObject(res);
						funcPath = temp.getString("toolPath").substring(1);
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
						buff = new StringBuffer();
						int toolId = getMethodId(lib, func);
						for (int i=0; i < targets.length(); i++) {
							// check authorization
							temp = targets.getJSONObject(i);
							RegistryClientResponse resp = registryClient.getAnalysisPolicies(userName, toolId, temp.getInt("dataSetInstanceId"));
							if (resp == null) {
								response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get analysis policies for user " + userName + " and toolId " + toolId + " and dataSetInstanceId " + temp.getInt("dataSetInstanceId"));
								return;
							} else if (resp.getStatus() != HttpServletResponse.SC_OK) {
								System.out.println("Result:\n" + clientResponse.getEntity());
								response.sendError(resp.getStatus(), resp.getErrorMessage());
								return;
							}
							JSONArray policy = resp.getEntityResponse();
							if (policy.length() == 0) {
								isAuthorized = false;
								break;
							}
							if (i > 0) {
								buff.append(",");
							}
							String dataSource = temp.getString("dataSource");
							JSONObject node = temp.getJSONObject("node");
							buff.append(node.getString("hostUrl")).append(":").append(node.getString("hostPort")).append(node.getString("basePath")).append(funcPath).append("?dataSource=").append(Utils.urlEncode(dataSource));
							if (policy.getJSONObject(0).getJSONObject("accessMode").getString("description").equals("async")) {
								buff.append("&resultsReleaseAuthReq=true");
							}
						}
						if (!isAuthorized) {
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
							return;
						}
						String targetsURLs = buff.toString();
						buff = new StringBuffer();
						buff.append(masterURL).append(funcPath);
						rsp = null;
						String rspId = null;
						obj = new JSONObject();
						System.out.println("Study Id: " + getStudyId(study));
						System.out.println("Dataset Id: " + getDatasetId(dataset));
						System.out.println("Library Id: " + getLibraryId(lib));
						System.out.println("Method Id: " + getMethodId(lib, func));
						System.out.println("Sites: " + sites);
						System.out.println("Dataset Instances Ids: " + getDatasetInstancesIds(values));
						url = buff.toString();
						System.out.println("URL: " + url + "\nTargets: "+targetsURLs+"\nParams: "+params);
						rsp = scannerClient.postScannerQuery(url, targetsURLs, params);
						if (!rsp.isException()) {
							rspId = rsp.getIdHeader();
							System.out.println("Response Id: "+rspId);
							if (rspId != null) {
								responseBody = rsp.getEntityString();
								JSONArray analysisResults = getAnalysisResults(responseBody, targets, funcPath);
								String transactionId = rspId;
								String status = getAnalysisStatus(analysisResults);
								int studyId = getStudyId(study);
								int userId = (Integer) session.getAttribute("userId");
								clientResponse = registryClient.createHistory(transactionId, status, studyId, userId, masterId, toolId, analysisResults.toString());
								if (clientResponse == null) {
									response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not create history.");
									return;
								} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
									System.out.println("Result:\n" + clientResponse.getEntity());
									response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
									return;
								}
								JSONObject history = clientResponse.getEntity();
								clientResponse.release();
				                SimpleDateFormat from_sdf = new SimpleDateFormat("MMM dd, yyyy h:mm:ss a z");
				                SimpleDateFormat to_sdf = new SimpleDateFormat("yyyy-MM-dd h:mm a");
								Date created = from_sdf.parse(history.getString("created"));
								obj.put("created", to_sdf.format(created));
								obj.put("analysisId", history.getString("analysisId"));
							}
						}
					}
					if (rsp.isException()) {
						throw(new ServletException(rsp.getException()));
					} else if (rsp.isError()) {
						response.sendError(rsp.getStatus(), rsp.getEntityString());
						return;
					}
					res = responseBody;
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
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (action.equals("displaySitesStatus")) {
				obj = (JSONObject) servletConfig.getServletContext().getAttribute("echo");
				if (obj == null) {
					obj = new JSONObject();
				}
				//System.out.println("Return: "+obj.toString());
			} else if (action.equals("analyzePTR")) {
				RegistryClient registryClient = (RegistryClient) session.getAttribute("registryClient");
				RegistryClientResponse clientResponse = null;
				if (ptrTool == null) {
					clientResponse = registryClient.getMethods();
					if (clientResponse == null) {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get methods.");
						return;
					} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
						System.out.println("Result:\n" + clientResponse.getEntity());
						response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
						return;
					}
					JSONArray methods = clientResponse.getEntityResponse();
					for (int i=0; i < methods.length(); i++) {
						JSONObject method = methods.getJSONObject(i);
						if (method.getString("toolName").equals("Prep to Research")) {
							ptrTool = method;
						}
						clientResponse = registryClient.getDatasetInstances();
						if (clientResponse == null) {
							response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get dataset instances.");
							return;
						} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
							System.out.println("Result:\n" + clientResponse.getEntity());
							response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
							return;
						}
						ptrDatasetInstances = clientResponse.getEntityResponse();
						for (int j=ptrDatasetInstances.length()-1; j >= 0; j--) {
							JSONObject instance = ptrDatasetInstances.getJSONObject(j);
							if (!instance.getString("dataSetDefinition").equals("PTR")) {
								ptrDatasetInstances.remove(j);
							}
						}
						
					}
					
				}
				if (ptrTool != null) {
					StringBuffer buff = new StringBuffer();
					for (int i=0; i < ptrDatasetInstances.length(); i++) {
						JSONObject temp = ptrDatasetInstances.getJSONObject(i);
						if (i > 0) {
							buff.append(",");
						}
						String dataSource = temp.getString("dataSource");
						JSONObject node = temp.getJSONObject("node");
						buff.append(node.getString("hostUrl")).append(":").append(node.getString("hostPort")).append(node.getString("basePath")).append(ptrTool.getString("toolPath").substring(1)).append("?dataSource=").append(Utils.urlEncode(dataSource));
					}
					String targetsURLs = buff.toString();
					JSONObject body = new JSONObject();
					String omopConceptID = request.getParameter("omopConceptID");
					body.put("omopConceptID", Integer.parseInt(omopConceptID));
					clientResponse = registryClient.getMasterObject();
					if (clientResponse == null) {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can not get master node.");
						return;
					} else if (clientResponse.getStatus() != HttpServletResponse.SC_OK) {
						System.out.println("Result:\n" + clientResponse.getEntity());
						response.sendError(clientResponse.getStatus(), clientResponse.getErrorMessage());
						return;
					}
					String res = clientResponse.toMasterString();
					clientResponse.release();
					System.out.println("master string: " + res);
					JSONObject temp = new JSONObject(res);
					String masterURL = temp.getString("hostUrl") + ":" + temp.getString("hostPort") + temp.getString("basePath");
					String url = masterURL + ptrTool.getString("toolPath").substring(1);
					System.out.println("URL: " + url + "\nTargets: "+targetsURLs+"\nParams: "+body);
					ClientURLResponse rsp = scannerClient.postScannerQuery(url, targetsURLs, body.toString());
					if (rsp.isException()) {
						throw(new ServletException(rsp.getException()));
					} else if (rsp.isError()) {
						response.sendError(rsp.getStatus(), rsp.getEntityString());
						return;
					}
					res = rsp.getEntityString();
					System.out.println("Response Body: \n"+res);
					PrintWriter out = response.getWriter();
					String text = res;
					out.print(text);
					return;
				} else {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No PTR library.");
					return;
				}
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
			for (int i=0; i < retrievedTools.length(); i++) {
				JSONObject obj = retrievedTools.getJSONObject(i);
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
				String site = node.getJSONObject("site").getString("siteName") + " - " + node.getString("nodeName");
				if (values.contains(site)) {
					ret.put(instance.getInt("dataSetInstanceId"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	JSONArray getAnalysisResults(String response, JSONArray targets, String funcPath) {
		JSONObject instancesDict = new JSONObject();
		JSONArray ret = new JSONArray();
		try {
			for (int i=0; i < targets.length(); i++) {
				JSONObject target = targets.getJSONObject(i);
				JSONObject node = target.getJSONObject("node");
				instancesDict.put(node.getString("hostUrl") + ":" + node.getString("hostPort") + node.getString("basePath") + funcPath, target);
			}
			JSONObject serviceResponses = (new JSONObject(response)).getJSONObject("ServiceResponses");
			JSONArray serviceResponse = serviceResponses.optJSONArray("ServiceResponse");
			if (serviceResponse == null) {
				serviceResponse = new JSONArray();
				serviceResponse.put(serviceResponses.getJSONObject("ServiceResponse"));
			}
			boolean isMaster = false;
			String status = null;
			String statusDetail = null;
			for (int i=0; i < serviceResponse.length(); i++) {
				JSONObject serviceResponseMetadata = serviceResponse.getJSONObject(i).getJSONObject("ServiceResponseMetadata");
				JSONObject analysisResult = new JSONObject();
				status = serviceResponseMetadata.getString("RequestState");
				statusDetail = serviceResponseMetadata.getString("RequestStateDetail");
				String url = serviceResponseMetadata.getString("RequestURL");
				if (instancesDict.optJSONObject(url) == null) {
					isMaster = true;
					break;
				}
				analysisResult.put("status", status);
				analysisResult.put("statusDetail", statusDetail);
				analysisResult.put("url", url);
				analysisResult.put("dataSetInstanceId", ((JSONObject) instancesDict.get(serviceResponseMetadata.getString("RequestURL"))).getInt("dataSetInstanceId"));
				ret.put(analysisResult);
			}
			if (isMaster) {
				ret = new JSONArray();
				for (int i=0; i < targets.length(); i++) {
					JSONObject target = targets.getJSONObject(i);
					JSONObject analysisResult = new JSONObject();
					analysisResult.put("status", status);
					analysisResult.put("statusDetail", statusDetail);
					JSONObject node = target.getJSONObject("node");
					String url = node.getString("hostUrl") + ":" + node.getString("hostPort") + node.getString("basePath") + funcPath;
					analysisResult.put("url", url);
					analysisResult.put("dataSetInstanceId", ((JSONObject) instancesDict.get(url)).getInt("dataSetInstanceId"));
					ret.put(analysisResult);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	String getAnalysisStatus(JSONArray serviceResponse) {
		String ret = "Complete";
		try {
			for (int i=0; i < serviceResponse.length(); i++) {
				String status = serviceResponse.getJSONObject(i).getString("status");
				if (!ret.equals(status)) {
					ret = status;
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

}
