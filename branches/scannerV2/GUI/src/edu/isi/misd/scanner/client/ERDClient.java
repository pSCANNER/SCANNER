/**
 * 
 */
package edu.isi.misd.scanner.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.isi.misd.scanner.utils.Utils;

/**
 * @author serban
 *
 */
public class ERDClient extends JakartaClient implements RegistryClient {
	private String erdURL = null;
	private String loginUser = null;

	public ERDClient(String url, String user) {
		super(4, 8192, 120000);
		erdURL = url;
		this.loginUser = user;
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createStudy(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createStudy(String studyName, String irbId, String studyOwner, String studyStatusType) {
		RegistryClientResponse ret = null;
		String url = erdURL + "studies";
		try {
			JSONObject body = new JSONObject();
			body.put("studyName", studyName);
			body.put("irbId", irbId != null ? Integer.parseInt(irbId) : 0);
			JSONObject investigator = new JSONObject();
			investigator.put("userId", Integer.parseInt(studyOwner));
			body.put("studyOwner", investigator);
			System.out.println("POST: " + url);
			System.out.println("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createDataset(java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public RegistryClientResponse createDataset(String name, String study,
			String description, List<String> variables) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createLibrary(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createLibrary(String name, String urlPath,
			String description) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createMethod(java.lang.String, java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createMethod(String name, List<String> libs,
			String urlPath, String description) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createMaster(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createMaster(String url, String title,
			String email, String phone, String website, String address,
			String contact) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createSite(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createSite(String name, String rURL,
			String title, String email, String phone, String website,
			String address, String agreement, String contact) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createParameter(java.lang.String, java.lang.String, java.util.List, java.lang.Integer, java.lang.Integer, java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createParameter(String name, String func,
			List<String> libs, Integer minOccurs, Integer maxOccurs,
			List<String> values, String path, String description) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createWorker(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public RegistryClientResponse createWorker(String study, String dataset,
			String lib, String func, String site, String sourceData,
			List<String> users) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteStudy(java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteStudy(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteDataset(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteDataset(String name, String study) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteLibrary(java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteLibrary(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteMethod(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteMethod(String name, String lib) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteMaster()
	 */
	@Override
	public RegistryClientResponse deleteMaster() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteSite(java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteSite(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteParameter(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteParameter(String name, String func,
			String lib) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteWorker(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteWorker(String study, String dataset,
			String lib, String func, String site) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#updateStudy(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse updateStudy(String studyId, String studyName, String irbId, String studyOwner, 
			String studyStatusType, String description, String protocol, String startDate, String endDate, 
			String clinicalTrialsId, String analysisPlan) {
		RegistryClientResponse ret = null;
		String url = erdURL + "studies/" + studyId;
		try {
			JSONObject body = new JSONObject();
			body.put("studyName", studyName);
			body.put("irbId", irbId != null ? Integer.parseInt(irbId) : 0);
			JSONObject investigator = new JSONObject();
			investigator.put("userId", Integer.parseInt(studyOwner));
			body.put("studyOwner", investigator);
			body.put("description", description.length() == 0 ? JSONObject.NULL : description);
			body.put("protocol", protocol.length() == 0 ? JSONObject.NULL : protocol);
			body.put("startDate", startDate.length() == 0 ? JSONObject.NULL : startDate);
			body.put("endDate", endDate.length() == 0 ? JSONObject.NULL : endDate);
			body.put("clinicalTrialsId", clinicalTrialsId.length() == 0 ? JSONObject.NULL : Integer.parseInt(clinicalTrialsId));
			body.put("analysisPlan", analysisPlan.length() == 0 ? JSONObject.NULL : analysisPlan);
			System.out.println("PUT: " + url);
			System.out.println("PUT Body:\n" + body.toString(2));
			ClientURLResponse rsp = putRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#updateDataset(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public RegistryClientResponse updateDataset(String id, String name,
			String study, String description, List<String> variables) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#updateLibrary(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse updateLibrary(String id, String name,
			String urlPath, String description) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#updateMethod(java.lang.String, java.lang.String, java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse updateMethod(String id, String name,
			List<String> libs, String urlPath, String description) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#updateParameter(java.lang.String, java.lang.String, java.lang.String, java.util.List, java.lang.Integer, java.lang.Integer, java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse updateParameter(String id, String name,
			String func, List<String> libs, Integer minOccurs,
			Integer maxOccurs, List<String> values, String path,
			String description) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#updateMaster(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse updateMaster(String url, String title,
			String email, String phone, String website, String address,
			String contact) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#updateSite(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse updateSite(String id, String name,
			String rURL, String title, String email, String phone,
			String website, String address, String agreement, String contact) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#updateWorker(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public RegistryClientResponse updateWorker(String id, String study,
			String dataset, String lib, String func, String site,
			String sourceData, List<String> users) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getStudies()
	 */
	@Override
	public RegistryClientResponse getStudies() {
		RegistryClientResponse ret = null;
		String url = erdURL + "studies" + getUserPredicate("?");
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getDatasets(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getDatasets(String study) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "datasets?studyName=" + Utils.urlEncode(study) + getUserPredicate("&");
			System.out.println("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getLibraries(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getLibraries(String study, String dataset, String sites) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "libraries" + "?studyName=" + Utils.urlEncode(study) + "&dataSetName=" + Utils.urlEncode(dataset) + getUserPredicate("&");
			System.out.println("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getMethods(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getMethods(String study, String dataset,
			String lib) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "tools" + "?studyName=" + Utils.urlEncode(study) + "&dataSetName=" + Utils.urlEncode(dataset) + 
				"&libraryId=" + Utils.urlEncode(lib) + getUserPredicate("&");
			System.out.println("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getParameters(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getParameters(String dataset, String jsonFile) {
		RegistryClientResponse clientResponse = null;
		try {
			JSONArray jsonRsp = readParameterFile(jsonFile);
			String url = erdURL + "variables?dataSetName=" + Utils.urlEncode(dataset);
			System.out.println("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			RegistryClientResponse ret = new ERDClientResponse(rsp);
			JSONArray parents = new JSONArray();
			parents.put("dependentVariableName");
			parents.put("independentVariableName");
			JSONArray arr = ret.getEntityResponse();
			for (int i=0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				JSONObject variable = new JSONObject();
				variable.put("cname", obj.get("variableName"));
				variable.put("text", obj.get("variableName"));
				variable.put("minOccurs", 0);
				variable.put("maxOccurs", 1);
				variable.put("parameterType", "enum");
				variable.put("parentParameter", parents);
				variable.put("description", obj.get("variableDescription"));
				variable.put("variableType", obj.get("variableType"));
				jsonRsp.put(variable);
			}
			ret.release();
			clientResponse = new ERDClientResponse(jsonRsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getVariables(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getVariables(String dataset) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getSites(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getSites(String study, String dataset) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "instances?dataSetName=" + Utils.urlEncode(dataset) + getUserPredicate("&");
			System.out.println("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getWorkers(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public RegistryClientResponse getWorkers(String study, String dataset,
			String lib, String func, List<String> sites) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getStudy(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getStudy(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getDataset(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getDataset(String name, String study) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getLibrary(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getLibrary(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getLibraryObject(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getLibraryObject(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getMethod(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getMethod(String name, String lib) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getMethodObject(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getMethodObject(String name, String lib) {
		RegistryClientResponse ret = null;
		String url = erdURL + "libraries";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getMaster()
	 */
	@Override
	public RegistryClientResponse getMaster() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getMasterObject()
	 */
	@Override
	public RegistryClientResponse getMasterObject() {
		RegistryClientResponse ret = null;
		String url = erdURL + "nodes?nodeType=master";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getParameter(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getParameter(String name, String func,
			String lib) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getWorker(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getWorker(String study, String dataset,
			String lib, String func, String site) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getSite(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getSite(String site) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getSiteObject(java.util.List)
	 */
	@Override
	public RegistryClientResponse getSiteObject(List<String> sites, String study) {
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getContacts()
	 */
	@Override
	public RegistryClientResponse getContacts() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#hasRoles()
	 */
	@Override
	public boolean hasRoles() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getRoles()
	 */
	@Override
	public List<String> getRoles() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getSitesMap()
	 */
	@Override
	public RegistryClientResponse getSitesMap() {
		RegistryClientResponse ret = null;
		String url = erdURL + "nodes";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getPI()
	 */
	@Override
	public RegistryClientResponse getPI() {
		// TODO Auto-generated method stub
		return null;
	}

	protected String getUserPredicate(String prefix) {
		String ret = prefix + "userName=" + loginUser;
		return ret;
	}
	
	public static JSONArray readParameterFile(String file) {
		JSONArray ret = null;
		try {
			File jsonFile = new File(file);
			StringBuffer buff = new StringBuffer();
			BufferedReader input =  new BufferedReader(new FileReader(jsonFile));
			String line = null;
			while (( line = input.readLine()) != null) {
				buff.append(line).append(System.getProperty("line.separator"));
			}
			input.close();
			ret = new JSONArray(buff.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse getUsers() {
		RegistryClientResponse ret = null;
		String url = erdURL + "users";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getNodes() {
		RegistryClientResponse ret = null;
		String url = erdURL + "nodes";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getTools() {
		RegistryClientResponse ret = null;
		String url = erdURL + "tools";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getDatasetInstances() {
		RegistryClientResponse ret = null;
		String url = erdURL + "instances";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getDatasetDefinitions() {
		RegistryClientResponse ret = null;
		String url = erdURL + "datasets";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getUser(String user) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "users?userName=" + Utils.urlEncode(user);
			System.out.println("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse getAnalysisPolicies(int userId,
			int toolId, int datasetInstanceId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "analysisPolicies?userId=" + userId + "&analysisToolId=" + toolId + "&dataSetInstanceId=" + datasetInstanceId;
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getUserRoles() {
		RegistryClientResponse ret = null;
		String url = erdURL + "userRoles";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getStudyPolicies() {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyPolicies";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getStudyRoles() {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyRoles";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getMyStudies() {
		RegistryClientResponse ret = null;
		String url = erdURL + "studies" + getUserPredicate("?");
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAnalysisPolicies() {
		RegistryClientResponse ret = null;
		String url = erdURL + "analysisPolicies";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllStudies() {
		RegistryClientResponse ret = null;
		String url = erdURL + "studies";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllLibraries() {
		RegistryClientResponse ret = null;
		String url = erdURL + "libraries";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllSites() {
		RegistryClientResponse ret = null;
		String url = erdURL + "sites";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllNodes() {
		RegistryClientResponse ret = null;
		String url = erdURL + "nodes";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllStandardRoles() {
		RegistryClientResponse ret = null;
		String url = erdURL + "standardRoles";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllStudyManagementPolicies() {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyManagementPolicies";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllSitesPolicies() {
		RegistryClientResponse ret = null;
		String url = erdURL + "sitePolicies";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllStudyRequestedSites() {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyRequestedSites";
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse createStudyRequestedSites(Integer studyId,
			Integer siteId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyRequestedSites";
		try {
			JSONObject body = new JSONObject();
			JSONObject study = new JSONObject();
			study.put("studyId", studyId);
			body.put("study", study);
			JSONObject site = new JSONObject();
			site.put("siteId", siteId);
			body.put("site", site);
			System.out.println("POST: " + url);
			System.out.println("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse deleteStudyRequestedSites(
			Integer studyRequestedSiteId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyRequestedSites/" + studyRequestedSiteId;
		System.out.println("DELETE: " + url);
		ClientURLResponse rsp = delete(url, null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getStudyRequestedSites(Integer studyId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyRequestedSites?studyId=" + studyId;
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getUserRoles(int studyId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "userRoles?studyId=" + studyId;
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse createUserRole(Integer userId, Integer roleId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "userRoles";
		try {
			JSONObject body = new JSONObject();
			JSONObject user = new JSONObject();
			user.put("userId", userId);
			body.put("user", user);
			JSONObject studyRole = new JSONObject();
			studyRole.put("roleId", roleId);
			body.put("studyRole", studyRole);
			System.out.println("POST: " + url);
			System.out.println("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse deleteUserRole(Integer userRoleId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "userRoles/" + userRoleId;
		System.out.println("DELETE: " + url);
		ClientURLResponse rsp = delete(url, null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getStudyRoles(int studyId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyRoles?studyId=" + studyId;
		System.out.println("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	
}
