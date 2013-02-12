package edu.isi.misd.scanner.client;

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
			System.out.println("Entity: " + res);
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
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toFunctions()
	 */
	@Override
	public String toFunctions() {
		return toStudies();
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toFunctions()
	 */
	@Override
	public String toSites() {
		String result = null;
		try {
			String res = response.getEntityString();
			System.out.println("Entity: " + res);
			JSONArray arr = new JSONArray(res);
			JSONObject ret = new JSONObject();
			for (int i=0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				ret.put(obj.getString("site"), obj.getString("id"));
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
	public String toParameters() {
		String result = null;
		try {
			String res = response.getEntityString();
			JSONArray arr = new JSONArray(res);
			JSONArray ret = new JSONArray();
			for (int i=0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				String name = obj.getString("id");
				String cname = obj.getString("cname");
				JSONArray resourceValues = obj.getJSONArray("values");
				int resourceMaxOccurs = obj.getInt("maxOccurs");
				int resourceMinOccurs = obj.getInt("minOccurs");
				JSONObject value = new JSONObject();
				value.put("cname", cname);
				value.put("values", resourceValues);
				value.put("maxOccurs", resourceMaxOccurs);
				value.put("minOccurs", resourceMinOccurs);
				JSONObject temp = new JSONObject();
				temp.put(name, value);
				ret.put(temp);
			}
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
		return toResource();
	}
	@Override
	public String toDataset() {
		return toResource();
	}
	@Override
	public String toLibrary() {
		return toResource();
	}
	@Override
	public String toFunction() {
		return toResource();
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toMaster()
	 */
	@Override
	public String toMaster() {
		return toResource();
	}

	@Override
	public String toWorker() {
		return toResource();
	}
	@Override
	public String toParameter() {
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

}
