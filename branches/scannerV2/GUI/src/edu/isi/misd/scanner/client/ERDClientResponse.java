/**
 * 
 */
package edu.isi.misd.scanner.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.isi.misd.scanner.client.JakartaClient.ClientURLResponse;

/**
 * @author serban
 *
 */
public class ERDClientResponse implements RegistryClientResponse {

	protected ClientURLResponse response;
	protected JSONArray entityResponse;
	protected JSONObject entityObject;
	protected JSONObject errorObject;

	public ERDClientResponse(ClientURLResponse rsp) {
		response = rsp;
		String res = response.getEntityString();
		if (res != null) {
			try {
				entityResponse = new JSONArray(res);
			} catch (JSONException e) {
				try {
					entityObject = new JSONObject(res);
					if (entityObject.has("errorStatusCode") && entityObject.has("errorMessage")) {
						errorObject = entityObject;
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public ERDClientResponse(JSONArray rsp) {
		entityResponse = rsp;
	}
	
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#getStatus()
	 */
	@Override
	public int getStatus() {
		int status = HttpServletResponse.SC_OK;
		try {
			if (errorObject != null) {
				status = errorObject.getInt("errorStatusCode");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			status = 0;
		}
		return status;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#getEntityString()
	 */
	@Override
	public String getEntityString() {
		return response.getEntityString();
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		String ret = "";
		try {
			if (errorObject != null) {
				ret = errorObject.getString("errorMessage");
				if (errorObject.has("errorDetail")) {
					ret += "\n" + errorObject.getString("errorDetail");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#release()
	 */
	@Override
	public void release() {
		if (response != null) {
			response.release();
		}
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toStudies()
	 */
	@Override
	public String toStudies() {
		String result = null;
		try {
			JSONObject ret = new JSONObject();
			for (int i=0; i < entityResponse.length(); i++) {
				JSONObject obj = entityResponse.getJSONObject(i);
				String description = obj.optString("description", "");
				ret.put(obj.getString("studyName"), description);
			}
			result = ret.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toDatasets()
	 */
	@Override
	public String toDatasets() {
		String result = null;
		try {
			JSONObject ret = new JSONObject();
			for (int i=0; i < entityResponse.length(); i++) {
				JSONObject obj = entityResponse.getJSONObject(i);
				ret.put(obj.getString("dataSetName"), obj.getString("description"));
			}
			result = ret.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toLibraries()
	 */
	@Override
	public String toLibraries() {
		String result = null;
		try {
			JSONObject ret = new JSONObject();
			for (int i=0; i < entityResponse.length(); i++) {
				JSONObject library = entityResponse.getJSONObject(i);
				ret.put(library.getString("libraryName"), library.getString("description"));
			}
			result = ret.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toMethods()
	 */
	@Override
	public String toMethods() {
		String result = null;
		try {
			JSONObject ret = new JSONObject();
			for (int i=0; i < entityResponse.length(); i++) {
				JSONObject tool = entityResponse.getJSONObject(i);
				ret.put(tool.getString("toolName"), tool.getString("toolDescription"));
			}
			result = ret.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toSites()
	 */
	@Override
	public String toSites(String dataset) {
		String result = null;
		try {
			JSONObject ret = new JSONObject();
			for (int i=0; i < entityResponse.length(); i++) {
				JSONObject datasetObj = entityResponse.getJSONObject(i);
				JSONObject node = datasetObj.getJSONObject("node");
				JSONObject siteObj = node.getJSONObject("site");
				ret.put(siteObj.getString("siteName") + " - " + node.getString("nodeName"), node.getInt("nodeId"));
			}
			result = ret.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toParameters()
	 */
	@Override
	public String toParameters() {
		JSONArray params = buildParameters(entityResponse, null);
		String result = params.toString();
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toMasterString()
	 */
	@Override
	public String toMasterString() {
		String ret = null;
		try {
			JSONObject obj = entityResponse.getJSONObject(0);
			ret = obj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toMethodString()
	 */
	@Override
	public String toMethodString(String func, String lib) {
		String result = null;
		try {
			JSONObject ret = new JSONObject();
			for (int i=0; i < entityResponse.length(); i++) {
				JSONObject tool = entityResponse.getJSONObject(i);
				if (tool.getString("toolName").equals(func) && tool.getString("toolParentLibrary").equals(lib)) {
					ret = tool;
					break;
				}
			}
			result = ret.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toSiteString()
	 */
	@Override
	public String toSiteString(List<String> sites, String dataset) {
		String result = null;
		try {
			JSONArray nodes = new JSONArray();
			for (int i=0; i < entityResponse.length(); i++) {
				JSONObject instance = entityResponse.getJSONObject(i);
				JSONObject node = instance.getJSONObject("node");
				JSONObject siteObj = node.getJSONObject("site");
				String site = siteObj.getString("siteName") + " - " + node.getString("nodeName");
				if (sites.contains(site)) {
					nodes.put(instance);
				}
			}
			result = nodes.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toSitesMap()
	 */
	@Override
	public JSONObject toSitesMap() {
		JSONObject ret = new JSONObject();
		JSONObject map = new JSONObject();
		JSONObject targets = new JSONObject();
		try {
			ret.put("map", map);
			ret.put("targets", targets);
			for (int i=0; i < entityResponse.length(); i++) {
				JSONObject obj = entityResponse.getJSONObject(i);
				if (!obj.getBoolean("isMaster")) {
					String key = obj.getString("hostUrl") + ":" + obj.getInt("hostPort");
					String rURL = key + obj.getString("basePath");
					JSONObject site = obj.getJSONObject("site");
					String cname = site.getString("siteName") + " - " + obj.getString("nodeName");
					map.put(key, cname);
					targets.put(rURL, cname);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	static JSONArray buildParameters(JSONArray arr, String parent) {
		JSONArray ret = new JSONArray();
		try {
			JSONArray params = getParameter(arr, parent);
			for (int i=0; i < params.length(); i++) {
				JSONObject param = params.getJSONObject(i);
				String cname = param.getString("cname");
				JSONObject rootObj = new JSONObject();
				JSONObject paramNode = new JSONObject();
				rootObj.put(cname, paramNode);
				paramNode.put("metadata", param);
				JSONArray data = buildParameters(arr, cname);
				if (data.length() > 0) {
					paramNode.put("data", data);
				}
				ret.put(rootObj);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	static JSONArray getParameter( JSONArray arr, String parent) {
		JSONArray ret = new JSONArray();
		JSONArray enumArray = new JSONArray();
		try {
			for (int i=0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				JSONArray parents = obj.optJSONArray("parentParameter");
				if (parents != null && parent != null) {
					for (int j=0; j < parents.length(); j++) {
						if (parents.getString(j).equals(parent)) {
							Integer position = obj.optInt("position");
							if (position != null && position > 0) {
								ret.put(--position, obj);
							} else if (obj.optString("parameterType") != null && obj.optString("parameterType").equals("enum")){
								enumArray.put(obj);
							} else {
								ret.put(obj);
							}
						}
					}
				} else if (parent == null && parents == null) {
					ret.put(obj);
				}
			}
			if (enumArray.length() > 0) {
				enumArray = sortParameters(enumArray);
				for (int i=0; i < enumArray.length(); i++) {
					ret.put(enumArray.getJSONObject(i));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	static JSONArray sortParameters(JSONArray arr) {
		JSONArray ret = new JSONArray();
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		try {
			for (int i=0; i < arr.length(); i++) {
				jsonValues.add(arr.getJSONObject(i));
			}
			Collections.sort(jsonValues, new JSONComparator());
			for (int i=0; i < jsonValues.size(); i++) {
				ret.put(jsonValues.get(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public JSONArray getEntityResponse() {
		return entityResponse;
	}

	@Override
	public JSONArray toUsers() {
		return getEntityResponse();
	}

	@Override
	public JSONArray toNodes() {
		JSONArray ret = new JSONArray();
		try {
			for (int i=0; i < entityResponse.length(); i++) {
				JSONObject node = entityResponse.getJSONObject(i);
				if (!node.getBoolean("isMaster")) {
					ret.put(node);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public JSONArray toTools() {
		return getEntityResponse();
	}

	@Override
	public JSONArray toDatasetInstances() {
		return getEntityResponse();
	}

	@Override
	public JSONArray toDatasetDefinitions() {
		return getEntityResponse();
	}

	@Override
	public JSONObject getEntity() {
		return entityObject;
	}

	@Override
	public JSONArray toUserRoles() {
		return getEntityResponse();
	}

	@Override
	public JSONArray toStudyPolicies() {
		return getEntityResponse();
	}

	@Override
	public JSONArray toStudyRoles() {
		return getEntityResponse();
	}

}

class JSONComparator implements Comparator<JSONObject>
{

    public int compare(JSONObject a, JSONObject b)
    {
		try {
			String valA = a.getString("text");
		    String valB = b.getString("text");
	        return valA.compareToIgnoreCase(valB);    
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
 
    }
}

