package edu.isi.misd.scanner.client;

import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.isi.misd.scanner.client.JakartaClient.ClientURLResponse;

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

/**
 * Implementation for the Tagfiler RegistryClient responses
 * 
 * @author Serban Voinea
 *
 */
public class TagfilerClientResponse implements RegistryClientResponse {
	
	ClientURLResponse response;

	public TagfilerClientResponse(ClientURLResponse rsp) {
		response = rsp;
		
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#getStatus()
	 */
	@Override
	public int getStatus() {
		return response.getStatus();
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
		return response.getErrorMessage();
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#release()
	 */
	@Override
	public void release() {
		response.release();

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
				ret.put(obj.getString("cname"), obj.getString("description"));
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
		return toStudies();
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toLibraries()
	 */
	@Override
	public String toLibraries() {
		return toStudies();
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toMethods()
	 */
	@Override
	public String toMethods() {
		return toStudies();
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toMethods()
	 */
	@Override
	public String toSites() {
		String result = null;
		try {
			String res = response.getEntityString();
			JSONArray arr = new JSONArray(res);
			JSONObject ret = new JSONObject();
			for (int i=0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				ret.put(obj.getString("cname"), obj.getString("id"));
			}
			result = ret.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toWorkers()
	 */
	@Override
	public String toWorkers() {
		return response.getEntityString();
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toParameters()
	 */
	@Override
	public String toParameters(String variables) {
		String result = null;
		try {
			String res = response.getEntityString();
			System.out.println("Params:\n"+res);
			JSONArray arr = new JSONArray(res);
			JSONObject params = new JSONObject();
			JSONObject paramsDescription = new JSONObject();
			for (int i=0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				String cname = obj.getString("cname");
				if (!obj.isNull("description")) {
					paramsDescription.put(cname, obj.getString("description"));
				}
				String path = obj.getString("path");
				StringTokenizer tokenizer = new StringTokenizer(path, "|");
				JSONObject crtParam = params;
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					if (!crtParam.has(token)) {
						crtParam.put(token, new JSONObject());
					}
					crtParam = crtParam.getJSONObject(token);
				}
				if (obj.isNull("values")) {
					JSONArray resourceValues = new JSONArray(variables);
					if (resourceValues.length() > 0) {
						JSONObject resourceObj = resourceValues.getJSONObject(0);
						resourceValues = resourceObj.getJSONArray("variables");
					}
					crtParam.put(cname, resourceValues);
				} else {
					JSONArray resourceValues = obj.getJSONArray("values");
					if (resourceValues.length() == 1 && !resourceValues.getString(0).equals("string")) {
						crtParam.put(cname, resourceValues.getString(0));
					} else {
						crtParam.put(cname, resourceValues);
					}
				}
			}
			JSONObject ret = new JSONObject();
			ret.put("description", paramsDescription);
			ret.put("params", params);
			result = ret.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	String toResource() {
		String ret = null;
		try {
			JSONObject obj = (new JSONArray(response.getEntityString())).getJSONObject(0);
			ret = obj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public String toStudy() {
		return getEntityString();
	}
	@Override
	public String toDataset() {
		return getEntityString();
	}
	@Override
	public String toLibrary() {
		return getEntityString();
	}
	@Override
	public String toMethod() {
		return getEntityString();
	}
	@Override
	public String toMaster() {
		return getEntityString();
	}
	@Override
	public String toParameter() {
		return getEntityString();
	}
	@Override
	public String toWorker() {
		return getEntityString();
	}
	@Override
	public String toSite() {
		return response.getEntityString();
	}
	@Override
	public String toLibraryString() {
		return toResource();
	}
	@Override
	public String toMethodString() {
		return toResource();
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toMaster()
	 */
	@Override
	public String toMasterString() {
		return toResource();
	}

	@Override
	public String getResourceId() {
		String ret = null;
		try {
			JSONObject obj = (new JSONArray(response.getEntityString())).getJSONObject(0);
			ret = obj.getString("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public String toSiteString() {
		return response.getEntityString();
	}
	@Override
	public String toContacts() {
		return response.getEntityString();
	}
	@Override
	public JSONObject toSitesMap() {
		JSONObject ret = new JSONObject();
		try {
			String res = response.getEntityString();
			JSONArray arr = new JSONArray(res);
			for (int i=0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				ret.put(obj.getString("rURL"), obj.getString("cname"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

}
