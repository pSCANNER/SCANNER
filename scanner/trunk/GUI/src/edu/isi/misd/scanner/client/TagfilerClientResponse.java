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
		// TODO Auto-generated method stub
		return response.getStatus();
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#getEntityString()
	 */
	@Override
	public String getEntityString() {
		// TODO Auto-generated method stub
		return response.getEntityString();
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return response.getErrorMessage();
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#release()
	 */
	@Override
	public void release() {
		// TODO Auto-generated method stub
		response.release();

	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toStudies()
	 */
	@Override
	public String toStudies() {
		// TODO Auto-generated method stub
		String result = null;
		try {
			String res = response.getEntityString();
			JSONArray arr = new JSONArray(res);
			JSONObject ret = new JSONObject();
			for (int i=0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				ret.put(obj.getString("resourceDisplayName"), obj.getString("rname"));
			}
			result = ret.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toDatasets()
	 */
	@Override
	public String toDatasets() {
		// TODO Auto-generated method stub
		return toStudies();
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toLibraries()
	 */
	@Override
	public String toLibraries() {
		// TODO Auto-generated method stub
		return toStudies();
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toFunctions()
	 */
	@Override
	public String toFunctions() {
		// TODO Auto-generated method stub
		return toStudies();
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toParameters()
	 */
	@Override
	public String toParameters() {
		// TODO Auto-generated method stub
		String result = null;
		try {
			String res = response.getEntityString();
			JSONArray arr = new JSONArray(res);
			JSONArray ret = new JSONArray();
			for (int i=0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				String name = obj.getString("rname");
				String resourceDisplayName = obj.getString("resourceDisplayName");
				JSONArray resourceValues = obj.getJSONArray("resourceValues");
				int resourceMaxOccurs = obj.getInt("resourceMaxOccurs");
				int resourceMinOccurs = obj.getInt("resourceMinOccurs");
				JSONObject value = new JSONObject();
				value.put("resourceDisplayName", resourceDisplayName);
				value.put("resourceValues", resourceValues);
				value.put("resourceMaxOccurs", resourceMaxOccurs);
				value.put("resourceMinOccurs", resourceMinOccurs);
				JSONObject temp = new JSONObject();
				temp.put(name, value);
				ret.put(temp);
			}
			result = ret.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String toFunction() {
		// TODO Auto-generated method stub
		return response.getEntityString();
	}
	@Override
	public String toLibrary() {
		// TODO Auto-generated method stub
		return response.getEntityString();
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toMaster()
	 */
	@Override
	public String toMaster() {
		// TODO Auto-generated method stub
		return response.getEntityString();
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClientResponse#toWorkers()
	 */
	@Override
	public String toWorkers() {
		// TODO Auto-generated method stub
		return response.getEntityString();
	}

}
