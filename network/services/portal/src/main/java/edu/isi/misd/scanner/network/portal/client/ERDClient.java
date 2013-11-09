/**
 * 
 */
package edu.isi.misd.scanner.network.portal.client;

/* 
 * Copyright 2013 University of Southern California
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.isi.misd.scanner.network.portal.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author serban
 *
 */
public class ERDClient extends JakartaClient implements RegistryClient {
	private String erdURL = null;
	private String loginUser = null;
	private static final transient Logger log = 
	        LoggerFactory.getLogger(ERDClient.class);    

	public ERDClient(String url, String user) {
		super(4, 8192, 120000);
		erdURL = url;
		this.loginUser = user;
		log.info("Registry Client was initialized.");
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.network.portal.client.RegistryClient#createStudy(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createStudy(String studyName, String irbId, String studyOwner, String studyStatusType) {
		RegistryClientResponse ret = null;
		String url = erdURL + "studies";
		try {
			JSONObject body = new JSONObject();
			body.put("studyName", studyName);
			body.put("irbId", irbId != null ? irbId : "");
			JSONObject investigator = new JSONObject();
			investigator.put("userId", Integer.parseInt(studyOwner));
			body.put("studyOwner", investigator);
			if (log.isDebugEnabled()) log.debug("POST: " + url);
			if (log.isDebugEnabled()) log.debug("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.network.portal.client.RegistryClient#updateStudy(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
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
			body.put("irbId", irbId != null ? irbId : "");
			JSONObject investigator = new JSONObject();
			investigator.put("userId", Integer.parseInt(studyOwner));
			body.put("studyOwner", investigator);
			body.put("description", description.length() == 0 ? JSONObject.NULL : description);
			body.put("protocol", protocol.length() == 0 ? JSONObject.NULL : protocol);
			body.put("startDate", startDate.length() == 0 ? JSONObject.NULL : startDate);
			body.put("endDate", endDate.length() == 0 ? JSONObject.NULL : endDate);
			body.put("clinicalTrialsId", clinicalTrialsId.length() == 0 ? JSONObject.NULL : clinicalTrialsId);
			body.put("analysisPlan", analysisPlan.length() == 0 ? JSONObject.NULL : analysisPlan);
			if (log.isDebugEnabled()) log.debug("PUT: " + url);
			if (log.isDebugEnabled()) log.debug("PUT Body:\n" + body.toString(2));
			ClientURLResponse rsp = putRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.network.portal.client.RegistryClient#getStudies()
	 */
	@Override
	public RegistryClientResponse getStudies() {
		RegistryClientResponse ret = null;
		String url = erdURL + "studies" + getUserPredicate("?");
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.network.portal.client.RegistryClient#getDatasets(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getDatasets(int studyId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "datasets?studyId=" + studyId + getUserPredicate("&");
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.network.portal.client.RegistryClient#getLibraries(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getLibraries(int studyId, int dataSetId, String sites) {
		RegistryClientResponse ret = null;
		String url = erdURL + "libraries" + "?studyId=" + studyId + "&dataSetId=" + dataSetId + getUserPredicate("&");
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.network.portal.client.RegistryClient#getMethods(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getMethods(int studyId, int dataSetId, int libraryId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "tools" + "?studyId=" + studyId + "&dataSetId=" + dataSetId + 
			"&libraryId=" + libraryId + getUserPredicate("&");
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.network.portal.client.RegistryClient#getParameters(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getParameters(int dataSetId, String jsonFile) {
		RegistryClientResponse clientResponse = null;
		try {
			JSONArray jsonRsp = readParameterFile(jsonFile);
			String url = erdURL + "variables?dataSetId=" + dataSetId;
			if (log.isDebugEnabled()) log.debug("GET: " + url);
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
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.network.portal.client.RegistryClient#getSites(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getSites(int studyId, int dataSetId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "instances?dataSetId=" + dataSetId + "&studyId=" + studyId + getUserPredicate("&");
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.network.portal.client.RegistryClient#getMethodObject(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getMethodObject(String name, String lib) {
		RegistryClientResponse ret = null;
		String url = erdURL + "libraries";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.network.portal.client.RegistryClient#getMasterObject()
	 */
	@Override
	public RegistryClientResponse getMasterObject() {
		RegistryClientResponse ret = null;
		String url = erdURL + "nodes?nodeType=master";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.network.portal.client.RegistryClient#getSitesMap()
	 */
	@Override
	public RegistryClientResponse getSitesMap() {
		RegistryClientResponse ret = null;
		String url = erdURL + "nodes";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
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
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getNodes() {
		RegistryClientResponse ret = null;
		String url = erdURL + "nodes";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getTools() {
		RegistryClientResponse ret = null;
		String url = erdURL + "tools";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getDatasetInstances() {
		RegistryClientResponse ret = null;
		String url = erdURL + "instances";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getDatasetDefinitions() {
		RegistryClientResponse ret = null;
		String url = erdURL + "datasets";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getUser(String user) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "users?userName=" + Utils.urlEncode(user);
			if (log.isDebugEnabled()) log.debug("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse getAnalysisPolicies(String userName,
			int toolId, int datasetInstanceId) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "analysisPolicies?userName=" + Utils.urlEncode(userName) + "&analysisToolId=" + toolId + "&dataSetInstanceId=" + datasetInstanceId;
			if (log.isDebugEnabled()) log.debug("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse getUserRoles() {
		RegistryClientResponse ret = null;
		String url = erdURL + "userRoles";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getStudyPolicies() {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyPolicies";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getStudyRoles() {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyRoles";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getMyStudies() {
		RegistryClientResponse ret = null;
		String url = erdURL + "studies" + getUserPredicate("?");
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAnalysisPolicies() {
		RegistryClientResponse ret = null;
		String url = erdURL + "analysisPolicies";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllStudies() {
		RegistryClientResponse ret = null;
		String url = erdURL + "studies";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllLibraries() {
		RegistryClientResponse ret = null;
		String url = erdURL + "libraries";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllSites() {
		RegistryClientResponse ret = null;
		String url = erdURL + "sites";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllNodes() {
		RegistryClientResponse ret = null;
		String url = erdURL + "nodes";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllStandardRoles() {
		RegistryClientResponse ret = null;
		String url = erdURL + "standardRoles";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllStudyManagementPolicies() {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyManagementPolicies";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllSitesPolicies() {
		RegistryClientResponse ret = null;
		String url = erdURL + "sitePolicies";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllStudyRequestedSites() {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyRequestedSites";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
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
			if (log.isDebugEnabled()) log.debug("POST: " + url);
			if (log.isDebugEnabled()) log.debug("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse deleteStudyRequestedSites(
			Integer studyRequestedSiteId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyRequestedSites/" + studyRequestedSiteId;
		if (log.isDebugEnabled()) log.debug("DELETE: " + url);
		ClientURLResponse rsp = delete(url, null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getStudyRequestedSites(Integer studyId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyRequestedSites?studyId=" + studyId;
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getUserRoles(int studyId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "userRoles?studyId=" + studyId;
		if (log.isDebugEnabled()) log.debug("GET: " + url);
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
			if (log.isDebugEnabled()) log.debug("POST: " + url);
			if (log.isDebugEnabled()) log.debug("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse deleteUserRole(Integer userRoleId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "userRoles/" + userRoleId;
		if (log.isDebugEnabled()) log.debug("DELETE: " + url);
		ClientURLResponse rsp = delete(url, null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getStudyRoles(int studyId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyRoles?studyId=" + studyId;
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse createStudyPolicy(int roleId, int studyId,
			int dataSetDefinitionId, int toolId, int accessModeId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyPolicies";
		try {
			JSONObject body = new JSONObject();
			JSONObject studyRole = new JSONObject();
			studyRole.put("roleId", roleId);
			body.put("studyRole", studyRole);
			JSONObject study = new JSONObject();
			study.put("studyId", studyId);
			body.put("study", study);
			JSONObject policyStatus = new JSONObject();
			policyStatus.put("policyStatusTypeId", 0);
			body.put("policyStatus", policyStatus);
			JSONObject dataSetDefinition = new JSONObject();
			dataSetDefinition.put("dataSetDefinitionId", dataSetDefinitionId);
			body.put("dataSetDefinition", dataSetDefinition);
			JSONObject analysisTool = new JSONObject();
			analysisTool.put("toolId", toolId);
			body.put("analysisTool", analysisTool);
			JSONObject accessMode = new JSONObject();
			accessMode.put("accessModeId", accessModeId);
			body.put("accessMode", accessMode);
			if (log.isDebugEnabled()) log.debug("POST: " + url);
			if (log.isDebugEnabled()) log.debug("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse deleteStudyPolicy(int studyPolicyStatementId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyPolicies/" + studyPolicyStatementId;
		if (log.isDebugEnabled()) log.debug("DELETE: " + url);
		ClientURLResponse rsp = delete(url, null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse createSitePolicy(int roleId,
			int dataSetInstanceId, int studyPolicyStatementId, int toolId,
			int accessModeId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "analysisPolicies";
		try {
			JSONObject body = new JSONObject();
			JSONObject studyRole = new JSONObject();
			studyRole.put("roleId", roleId);
			body.put("studyRole", studyRole);
			JSONObject dataSetInstance = new JSONObject();
			dataSetInstance.put("dataSetInstanceId", dataSetInstanceId);
			body.put("dataSetInstance", dataSetInstance);
			JSONObject policyStatus = new JSONObject();
			policyStatus.put("policyStatusTypeId", 0);
			body.put("policyStatus", policyStatus);
			JSONObject analysisTool = new JSONObject();
			analysisTool.put("toolId", toolId);
			body.put("analysisTool", analysisTool);
			JSONObject accessMode = new JSONObject();
			accessMode.put("accessModeId", accessModeId);
			body.put("accessMode", accessMode);
			JSONObject parentStudyPolicyStatement = new JSONObject();
			parentStudyPolicyStatement.put("studyPolicyStatementId", studyPolicyStatementId);
			body.put("parentStudyPolicyStatement", parentStudyPolicyStatement);
			if (log.isDebugEnabled()) log.debug("POST: " + url);
			if (log.isDebugEnabled()) log.debug("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse deleteAnalyzePolicy(
			int analysisPolicyStatementId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "analysisPolicies/" + analysisPolicyStatementId;
		if (log.isDebugEnabled()) log.debug("DELETE: " + url);
		ClientURLResponse rsp = delete(url, null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse createDatasetInstance(
			String dataSetInstanceName, String description, String dataSource,
			int dataSetDefinitionId, int nodeId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "instances";
		try {
			JSONObject body = new JSONObject();
			body.put("dataSetInstanceName", dataSetInstanceName);
			if (description.length() > 0) {
				body.put("description", description);
			}
			body.put("dataSource", dataSource);
			JSONObject dataSetDefinition = new JSONObject();
			dataSetDefinition.put("dataSetDefinitionId", dataSetDefinitionId);
			body.put("dataSetDefinition", dataSetDefinition);
			JSONObject node = new JSONObject();
			node.put("nodeId", nodeId);
			body.put("node", node);
			if (log.isDebugEnabled()) log.debug("POST: " + url);
			if (log.isDebugEnabled()) log.debug("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse createUser(String userName, String email,
			String firstName, String lastName, String phone, boolean isSuperuser) {
		RegistryClientResponse ret = null;
		String url = erdURL + "users";
		try {
			JSONObject body = new JSONObject();
			body.put("userName", userName);
			body.put("email", email);
			body.put("firstName", firstName);
			body.put("lastName", lastName);
			body.put("phone", phone);
			body.put("isSuperuser", isSuperuser);
			if (log.isDebugEnabled()) log.debug("POST: " + url);
			if (log.isDebugEnabled()) log.debug("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse updateUser(int userId, String userName, String email,
			String firstName, String lastName, String phone, boolean isSuperuser) {
		RegistryClientResponse ret = null;
		String url = erdURL + "users/" + userId;
		try {
			JSONObject body = new JSONObject();
			body.put("userName", userName);
			body.put("email", email);
			body.put("firstName", firstName);
			body.put("lastName", lastName);
			body.put("phone", phone);
			body.put("isSuperuser", isSuperuser);
			if (log.isDebugEnabled()) log.debug("PUT: " + url);
			if (log.isDebugEnabled()) log.debug("PUT Body: " + body.toString());
			ClientURLResponse rsp = putRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse deleteUser(int userId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "users/" + userId;
		if (log.isDebugEnabled()) log.debug("DELETE: " + url);
		ClientURLResponse rsp = delete(url, null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse createSite(String siteName, String description, String userName) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "sites?userName=" + Utils.urlEncode(userName);
			JSONObject body = new JSONObject();
			body.put("siteName", siteName);
			body.put("description", description);
			if (log.isDebugEnabled()) log.debug("POST: " + url);
			if (log.isDebugEnabled()) log.debug("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse updateSite(int siteId, String siteName,
			String description, String userName) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "sites/" + siteId;
			//+ "?userName=" + Utils.urlEncode(userName);
			JSONObject body = new JSONObject();
			body.put("siteName", siteName);
			body.put("description", description);
			if (log.isDebugEnabled()) log.debug("PUT: " + url);
			if (log.isDebugEnabled()) log.debug("PUT Body: " + body.toString());
			ClientURLResponse rsp = putRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		} //catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		//}
		return ret;
	}
	@Override
	public RegistryClientResponse deleteSite(int siteId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "sites/" + siteId;
		if (log.isDebugEnabled()) log.debug("DELETE: " + url);
		ClientURLResponse rsp = delete(url, null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse createNode(String nodeName, String hostUrl,
			int hostPort, String basePath, String description,
			boolean isMaster, int siteId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "nodes";
		try {
			JSONObject body = new JSONObject();
			body.put("nodeName", nodeName);
			body.put("hostUrl", hostUrl);
			body.put("hostPort", hostPort);
			body.put("basePath", basePath);
			body.put("description", description);
			body.put("isMaster", isMaster);
			JSONObject site = new JSONObject();
			site.put("siteId", siteId);
			body.put("site", site);
			if (log.isDebugEnabled()) log.debug("POST: " + url);
			if (log.isDebugEnabled()) log.debug("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse updateNode(int nodeId, String nodeName,
			String hostUrl, int hostPort, String basePath, String description,
			boolean isMaster, int siteId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "nodes/" + nodeId;
		try {
			JSONObject body = new JSONObject();
			body.put("nodeName", nodeName);
			body.put("hostUrl", hostUrl);
			body.put("hostPort", hostPort);
			body.put("basePath", basePath);
			body.put("description", description);
			body.put("isMaster", isMaster);
			JSONObject site = new JSONObject();
			site.put("siteId", siteId);
			body.put("site", site);
			if (log.isDebugEnabled()) log.debug("PUT: " + url);
			if (log.isDebugEnabled()) log.debug("PUT Body: " + body.toString());
			ClientURLResponse rsp = putRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse deleteNode(int nodeId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "nodes/" + nodeId;
		if (log.isDebugEnabled()) log.debug("DELETE: " + url);
		ClientURLResponse rsp = delete(url, null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse updateDatasetInstance(int dataSetInstanceId,
			String dataSetInstanceName, String description, String dataSource,
			int dataSetDefinitionId, int nodeId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "instances/" + dataSetInstanceId;
		try {
			JSONObject body = new JSONObject();
			body.put("dataSetInstanceName", dataSetInstanceName);
			if (description.length() > 0) {
				body.put("description", description);
			}
			body.put("dataSource", dataSource);
			JSONObject dataSetDefinition = new JSONObject();
			dataSetDefinition.put("dataSetDefinitionId", dataSetDefinitionId);
			body.put("dataSetDefinition", dataSetDefinition);
			JSONObject node = new JSONObject();
			node.put("nodeId", nodeId);
			body.put("node", node);
			if (log.isDebugEnabled()) log.debug("PUT: " + url);
			if (log.isDebugEnabled()) log.debug("PUT Body: " + body.toString());
			ClientURLResponse rsp = putRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse deleteInstance(int dataSetInstanceId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "instances/" + dataSetInstanceId;
		if (log.isDebugEnabled()) log.debug("DELETE: " + url);
		ClientURLResponse rsp = delete(url, null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getUserRoles(String userName) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "userRoles?userName=" + Utils.urlEncode(userName);
			if (log.isDebugEnabled()) log.debug("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse getStudyManagementPolicies(int studyId,
			String userName) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "studyManagementPolicies?userName=" + Utils.urlEncode(userName) + "&studyId=" + studyId;
			if (log.isDebugEnabled()) log.debug("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse getDatasetInstances(String userName) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "instances?userName=" + Utils.urlEncode(userName);
			if (log.isDebugEnabled()) log.debug("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse getNodes(String userName) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "nodes?userName=" + Utils.urlEncode(userName);
			if (log.isDebugEnabled()) log.debug("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse getSitesPolicies(String userName) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "sitePolicies?userName=" + Utils.urlEncode(userName);
			if (log.isDebugEnabled()) log.debug("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse getUserRoles(int siteId, int studyId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "userRoles?studyId=" + studyId + "&siteId=" + siteId;
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	
	@Override
	public RegistryClientResponse getSiteStudyRoles(int siteId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "studyRoles?siteId=" + siteId;
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse createSiteAdministrationRole(int siteId,
			int roleId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "sitePolicies";
		try {
			JSONObject body = new JSONObject();
			JSONObject site = new JSONObject();
			site.put("siteId", siteId);
			body.put("site", site);
			JSONObject studyRole = new JSONObject();
			studyRole.put("roleId", roleId);
			body.put("studyRole", studyRole);
			if (log.isDebugEnabled()) log.debug("POST: " + url);
			if (log.isDebugEnabled()) log.debug("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse deleteSiteAdministrationRole(int sitePolicyId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "sitePolicies/" + sitePolicyId;
		if (log.isDebugEnabled()) log.debug("DELETE: " + url);
		ClientURLResponse rsp = delete(url, null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse findConcept(String conceptType,
			String searchString) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + conceptType;
			if (searchString != null) {
				url += "?search=" + Utils.urlEncode(searchString);
			}
			if (log.isDebugEnabled()) log.debug("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse getMethods() {
		RegistryClientResponse ret = null;
		String url = erdURL + "tools";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getDatasetInstances(int studyId,
			String userName) {
		RegistryClientResponse ret = null;
		try {
			String url = erdURL + "instances?studyId=" + studyId + "&userName=" + Utils.urlEncode(userName);
			if (log.isDebugEnabled()) log.debug("GET: " + url);
			ClientURLResponse rsp = get(url, (String) null, loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse getTools(int toolId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "tools/" + toolId;
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllHistory() {
		RegistryClientResponse ret = null;
		String url = erdURL + "analysisHistory";
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse updateHistory(int analysisId, String status) {
		RegistryClientResponse ret = null;
		try {
			ret = getHistory(analysisId);
			JSONObject history = ret.getEntity();
			ret.release();
			JSONArray analysisResults = history.getJSONArray("analysisResults");
			for (int i=0; i < analysisResults.length(); i++) {
				JSONObject analysisResult = analysisResults.getJSONObject(i);
				analysisResult.put("status", status);
			}
			String url = erdURL + "analysisHistory/" + analysisId;
			JSONObject body = new JSONObject();
			body.put("status", status);
			body.put("analysisResults", analysisResults);
			if (log.isDebugEnabled()) log.debug("PUT: " + url);
			if (log.isDebugEnabled()) log.debug("PUT Body: " + body.toString());
			ClientURLResponse rsp = putRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse deleteHistory(int analysisId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "analysisHistory/" + analysisId;
		if (log.isDebugEnabled()) log.debug("DELETE: " + url);
		ClientURLResponse rsp = delete(url, null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse getAllHistory(int studyId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "analysisHistory?studyId=" + studyId + getUserPredicate("&");
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse createHistory(String transactionId,
			String status, int studyId, int userId, int nodeId, int toolId,
			String analysisResults) {
		RegistryClientResponse ret = null;
		String url = erdURL + "analysisHistory";
		try {
			JSONObject body = new JSONObject();
			body.put("transactionId", transactionId);
			body.put("status", status);
			JSONObject study = new JSONObject();
			study.put("studyId", studyId);
			body.put("study", study);
			JSONObject user = new JSONObject();
			user.put("userId", userId);
			body.put("user", user);
			JSONObject node = new JSONObject();
			node.put("nodeId", nodeId);
			body.put("node", node);
			JSONObject analysisTool = new JSONObject();
			analysisTool.put("toolId", toolId);
			body.put("analysisTool", analysisTool);
			body.put("analysisResults", new JSONArray(analysisResults));
			if (log.isDebugEnabled()) log.debug("POST: " + url);
			if (log.isDebugEnabled()) log.debug("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse getHistory(int analysisId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "analysisHistory/" + analysisId;
		if (log.isDebugEnabled()) log.debug("GET: " + url);
		ClientURLResponse rsp = get(url, (String) null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse createDatasetDefinition(String dataSetName,
			String description, int studyId, int userId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "datasets";
		try {
			JSONObject body = new JSONObject();
			body.put("dataSetName", dataSetName);
			body.put("description", description);
			JSONObject originatingStudy = new JSONObject();
			originatingStudy.put("studyId", studyId);
			body.put("originatingStudy", originatingStudy);
			JSONObject author = new JSONObject();
			author.put("userId", userId);
			body.put("author", author);
			JSONObject dataSetConfidentialityLevel = new JSONObject();
			dataSetConfidentialityLevel.put("levelId", 100);
			dataSetConfidentialityLevel.put("description", "SAFE HARBOR");
			body.put("dataSetConfidentialityLevel", dataSetConfidentialityLevel);
			if (log.isDebugEnabled()) log.debug("POST: " + url);
			if (log.isDebugEnabled()) log.debug("POST Body: " + body.toString());
			ClientURLResponse rsp = postRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse updateDatasetDefinition(
			int dataSetDefinitionId, String dataSetName, String description,
			int studyId, int userId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "datasets/" + dataSetDefinitionId;
		try {
			JSONObject body = new JSONObject();
			body.put("dataSetName", dataSetName);
			body.put("description", description);
			JSONObject originatingStudy = new JSONObject();
			originatingStudy.put("studyId", studyId);
			body.put("originatingStudy", originatingStudy);
			JSONObject author = new JSONObject();
			author.put("userId", userId);
			body.put("author", author);
			JSONObject dataSetConfidentialityLevel = new JSONObject();
			dataSetConfidentialityLevel.put("levelId", 100);
			dataSetConfidentialityLevel.put("description", "SAFE HARBOR");
			body.put("dataSetConfidentialityLevel", dataSetConfidentialityLevel);
			if (log.isDebugEnabled()) log.debug("PUT: " + url);
			if (log.isDebugEnabled()) log.debug("PUT Body:\n" + body.toString(2));
			ClientURLResponse rsp = putRegistry(url, body.toString(), loginUser);
			ret = new ERDClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public RegistryClientResponse deleteDatasetDefinition(
			int dataSetDefinitionId) {
		RegistryClientResponse ret = null;
		String url = erdURL + "datasets/" + dataSetDefinitionId;
		if (log.isDebugEnabled()) log.debug("DELETE: " + url);
		ClientURLResponse rsp = delete(url, null, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
	@Override
	public RegistryClientResponse postDatasetSpec(String body) {
		RegistryClientResponse ret = null;
		String url = erdURL + "datasetSpecProcessor";
		if (log.isDebugEnabled()) log.debug("POST: " + url);
		if (log.isDebugEnabled()) log.debug("POST Body: " + body);
		ClientURLResponse rsp = postRegistry(url, body, loginUser);
		ret = new ERDClientResponse(rsp);
		return ret;
	}
}
