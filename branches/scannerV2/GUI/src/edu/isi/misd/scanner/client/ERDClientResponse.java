/**
 * 
 */
package edu.isi.misd.scanner.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	protected JSONArray jsonResponse;

	public ERDClientResponse(ClientURLResponse rsp) {
		response = rsp;
	}
	
	public ERDClientResponse(JSONArray rsp) {
		jsonResponse = rsp;
	}
	
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#getStatus()
	 */
	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return null;
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
			String res = response.getEntityString();
			JSONArray arr = new JSONArray(res);
			JSONObject ret = new JSONObject();
			for (int i=0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				ret.put(obj.getString("studyName"), obj.getString("analysisPlan"));
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
			String res = response.getEntityString();
			JSONArray arr = new JSONArray(res);
			JSONObject ret = new JSONObject();
			for (int i=0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				ret.put(obj.getString("dataSetName"), obj.getString("dataSetName"));
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
			String res = response.getEntityString();
			JSONArray arr = new JSONArray(res);
			JSONObject ret = new JSONObject();
			for (int i=0; i < arr.length(); i++) {
				JSONObject library = arr.getJSONObject(i);
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
			String res = response.getEntityString();
			JSONArray arr = new JSONArray(res);
			JSONObject ret = new JSONObject();
			for (int i=0; i < arr.length(); i++) {
				JSONObject library = arr.getJSONObject(i);
				JSONArray analysisTools = library.getJSONArray("analysisTools");
				for (int j=0; j < analysisTools.length(); j++) {
					JSONObject tool = analysisTools.getJSONObject(j);
					ret.put(tool.getString("toolName"), tool.getString("toolDescription"));
				}
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
			String res = response.getEntityString();
			JSONArray arr = new JSONArray(res);
			JSONObject ret = new JSONObject();
			for (int i=0; i < arr.length(); i++) {
				JSONObject datasetObj = arr.getJSONObject(i);
				JSONObject node = datasetObj.getJSONObject("node");
				ret.put(node.getString("site") + ":" + node.getInt("nodeId"), node.getString("nodeId"));
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
		JSONArray params = buildParameters(jsonResponse, null);
		String result = params.toString();
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toWorkers()
	 */
	@Override
	public String toWorkers() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toContacts()
	 */
	@Override
	public String toContacts() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toStudy()
	 */
	@Override
	public String toStudy() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toDataset()
	 */
	@Override
	public String toDataset() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toLibrary()
	 */
	@Override
	public String toLibrary() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toMethod()
	 */
	@Override
	public String toMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toMaster()
	 */
	@Override
	public String toMaster() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toWorker()
	 */
	@Override
	public String toWorker() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toParameter()
	 */
	@Override
	public String toParameter() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toSite()
	 */
	@Override
	public String toSite() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toMasterString()
	 */
	@Override
	public String toMasterString() {
		String ret = null;
		try {
			JSONObject obj = (new JSONArray(response.getEntityString())).getJSONObject(0);
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
			String res = response.getEntityString();
			JSONArray arr = new JSONArray(res);
			JSONObject ret = new JSONObject();
			for (int i=0; i < arr.length(); i++) {
				JSONObject library = arr.getJSONObject(i);
				if (library.get("libraryName").equals(lib)) {
					JSONArray analysisTools = library.getJSONArray("analysisTools");
					for (int j=0; j < analysisTools.length(); j++) {
						JSONObject tool = analysisTools.getJSONObject(j);
						if (tool.getString("toolName").equals(func)) {
							ret = tool;
							break;
						}
					}
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
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toLibraryString()
	 */
	@Override
	public String toLibraryString() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toSiteString()
	 */
	@Override
	public String toSiteString(List<String> sites, String dataset) {
		String result = null;
		try {
			String res = response.getEntityString();
			JSONArray nodes = new JSONArray();
			JSONArray datasets = new JSONArray(res);
			for (int i=0; i < datasets.length(); i++) {
				JSONObject instance = datasets.getJSONObject(i);
				JSONObject node = instance.getJSONObject("node");
				String site = node.getString("site") + ":" + node.getInt("nodeId");
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
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#getResourceId()
	 */
	@Override
	public String getResourceId() {
		// TODO Auto-generated method stub
		return null;
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
			String res = response.getEntityString();
			JSONArray arr = new JSONArray(res);
			for (int i=0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				if (!obj.getBoolean("isMaster")) {
					String key = obj.getString("hostUrl") + ":" + obj.getInt("hostPort");
					String rURL = key + obj.getString("basePath");
					String cname = obj.getString("site") + ":" + obj.getInt("nodeId");
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

}
