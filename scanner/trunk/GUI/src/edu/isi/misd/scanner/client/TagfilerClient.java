package edu.isi.misd.scanner.client;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.isi.misd.scanner.client.JakartaClient.ClientURLResponse;
import edu.isi.misd.scanner.utils.Utils;

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
 * Implementation for Accessing the Tagfiler Registry
 * 
 * @author Serban Voinea
 *
 */
public class TagfilerClient implements RegistryClient {
	JakartaClient client;
	String tagfilerURL;
	String tagfilerCreateURL;
	String cookie;
	List<String> roles;

	public TagfilerClient(JakartaClient client, String tagfilerURL, String cookie, HttpServletRequest request) {
		this(client, tagfilerURL, cookie);
		String url = tagfilerURL + "/query/rtype=worker(users)";
		ClientURLResponse rsp = client.get(url, cookie);
		RegistryClientResponse clientResponse = new TagfilerClientResponse(rsp);
		List<String> res = new ArrayList<String>();
		try {
			JSONArray arr = new JSONArray(clientResponse.getEntityString());
			for (int i=0; i < arr.length(); i++) {
				JSONArray values = arr.getJSONObject(i).getJSONArray("users");
				for (int j=0; j < values.length(); j++) {
					String role = values.getString(j);
					if (!res.contains(role)) {
						res.add(role);
					}
				}
			}
			System.out.println("Workers roles: ");
			for (int i=0; i < res.size(); i++) {
				System.out.println(res.get(i));
			}
			roles = new ArrayList<String>();
			for (int i=0; i < res.size(); i++) {
				String role = res.get(i);
				if (request.isUserInRole(role)) {
					roles.add(role);
				}
			}
			System.out.println("User roles:");
			for (int i=0; i < roles.size(); i++) {
				System.out.println(roles.get(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public TagfilerClient(JakartaClient client, String tagfilerURL, String cookie) {
		this.client = client;
		this.tagfilerURL = tagfilerURL;
		this.cookie = cookie;
		tagfilerCreateURL = tagfilerURL + "/subject/?read+users=*&write+users=*&rtype=";
	}
	
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createStudy(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createStudy(String name) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerCreateURL + "study&cname=" + Utils.urlEncode(name);
			ClientURLResponse rsp = client.postResource(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createDataset(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createDataset(String dataset, String study) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerCreateURL + "dataset&cname=" + Utils.urlEncode(dataset) + "&study=" + Utils.urlEncode(study);
			ClientURLResponse rsp = client.postResource(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createLibrary(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createLibrary(String name,
			String urlPath) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerCreateURL + "library&cname=" + Utils.urlEncode(name) + 
					"&rpath=" + Utils.urlEncode(urlPath);
			ClientURLResponse rsp = client.postResource(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createFunction(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createFunction(String name,
			String lib, String urlPath) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerCreateURL + "function&cname=" + Utils.urlEncode(name) + 
					"&library=" + Utils.urlEncode(lib) +
					"&rpath=" + Utils.urlEncode(urlPath);
			ClientURLResponse rsp = client.postResource(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	@Override
	public RegistryClientResponse createMaster(String url) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String req_url = tagfilerCreateURL + "master" + 
					"&rURL=" + Utils.urlEncode(url);
			ClientURLResponse rsp = client.postResource(req_url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createParameter(java.lang.String, java.lang.String, int, int, java.util.List)
	 */
	@Override
	public RegistryClientResponse createParameter(String name, String func, String lib, int minOccurs, int maxOccurs, List<String> values) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerCreateURL + "parameter&cname=" + Utils.urlEncode(name) + 
					"&library=" + Utils.urlEncode(lib) +
					"&function=" + Utils.urlEncode(func) +
					"&maxOccurs=" + maxOccurs +
					"&minOccurs=" + minOccurs;
			if (values != null) {
				url += "&values=";
				for (int i=0; i < values.size(); i++) {
					if (i != 0) {
						url += ",";
					}
					url += Utils.urlEncode(values.get(i));
				}
			}
			ClientURLResponse rsp = client.postResource(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createWorker(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createWorker(String study, String dataset, String lib, String func, String site, String sourceData, String url) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String req_url = tagfilerCreateURL + "worker&study=" + Utils.urlEncode(study) + 
					"&dataset=" + Utils.urlEncode(dataset) +
					"&library=" + Utils.urlEncode(lib) +
					"&function=" + Utils.urlEncode(func) +
					"&site=" + Utils.urlEncode(site) +
					"&rURL=" + Utils.urlEncode(url) +
					"&datasource=" + Utils.urlEncode(sourceData);
			ClientURLResponse rsp = client.postResource(req_url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteStudy(java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteStudy(String name) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/subject/rtype=study;cname=" + Utils.urlEncode(name);
			ClientURLResponse rsp = client.delete(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteLibrary(java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteLibrary(String name) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/subject/rtype=library;cname=" + Utils.urlEncode(name);
			ClientURLResponse rsp = client.delete(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteMaster(java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteMaster() {
		client.setCookieValue(cookie);
		String url = tagfilerURL + "/subject/rtype=master";
		ClientURLResponse rsp = client.delete(url, cookie);
		RegistryClientResponse clientResponse = new TagfilerClientResponse(rsp);
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteParameter(java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteParameter(String name, String func, String lib) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url;
			url = tagfilerURL + "/subject/rtype=parameter" + 
			";cname=" + Utils.urlEncode(name) +
			";function=" + Utils.urlEncode(func) +
			";library=" + Utils.urlEncode(lib);
			ClientURLResponse rsp = client.delete(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteWorker(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteWorker(String study, String dataset, String lib, String func, String site) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url;
			url = tagfilerURL + "/subject/rtype=worker" + 
			";study=" + Utils.urlEncode(study) +
			";dataset=" + Utils.urlEncode(dataset) +
			";library=" + Utils.urlEncode(lib) +
			";function=" + Utils.urlEncode(func) +
			";site=" + Utils.urlEncode(site);
			ClientURLResponse rsp = client.delete(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	@Override
	public RegistryClientResponse deleteFunction(String name, String lib) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url;
			url = tagfilerURL + "/subject/rtype=function" + 
			";cname=" + Utils.urlEncode(name) +
			";library=" + Utils.urlEncode(lib);
			ClientURLResponse rsp = client.delete(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}
	@Override
	public RegistryClientResponse deleteDataset(String name, String study) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url;
			url = tagfilerURL + "/subject/rtype=dataset" + 
			";cname=" + Utils.urlEncode(name) +
			";study=" + Utils.urlEncode(study);
			ClientURLResponse rsp = client.delete(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#modifyLibrary(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse updateLibrary(String id, String name, String urlPath) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(cname)";
			ClientURLResponse rsp = client.get(url, cookie);
			JSONObject obj = (new JSONArray(rsp.getEntityString())).getJSONObject(0);
			String libName = obj.getString("cname");
			if (!libName.equals(name)) {
				url = tagfilerURL + "/tags/rtype=function,worker,parameter;library=" + Utils.urlEncode(libName) + "(library=" + Utils.urlEncode(name) + ")";
				client.putResource(url, cookie);
			}
			url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(" +
					"cname=" + Utils.urlEncode(name) +
					";rpath=" + Utils.urlEncode(urlPath) +
					")";
			rsp = client.putResource(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#modifyFunction(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse updateFunction(String id, String name, String lib, String urlPath) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(cname;library)";
			ClientURLResponse rsp = client.get(url, cookie);
			JSONObject obj = (new JSONArray(rsp.getEntityString())).getJSONObject(0);
			String funcName = obj.getString("cname");
			String libName = obj.getString("library");
			if (!libName.equals(lib) || !funcName.equals(name)) {
				url = tagfilerURL + "/tags/rtype=worker,parameter;library=" + Utils.urlEncode(libName) + ";function=" + Utils.urlEncode(funcName) + "(library=" + Utils.urlEncode(lib) + ";function=" + Utils.urlEncode(name) + ")";
				client.putResource(url, cookie);
			}
			url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(" +
					"cname=" + Utils.urlEncode(name) +
					";library=" + Utils.urlEncode(lib) +
					";rpath=" + Utils.urlEncode(urlPath) +
					")";
			rsp = client.putResource(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#modifyParameter(java.lang.String, java.lang.String, int, int, java.util.List)
	 */
	@Override
	public RegistryClientResponse updateParameter(String id, String name, String func, String lib, int minOccurs, int maxOccurs, List<String> values) {
		// to delete first the old values
		StringBuffer buff = new StringBuffer();
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(values)";
			ClientURLResponse rsp = client.delete(url, cookie);
			for (int i=0; i < values.size(); i++) {
				if (i != 0) {
					buff.append(",");
				}
				buff.append(Utils.urlEncode(values.get(i)));
			}
			url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(" +
					"cname=" + Utils.urlEncode(name) +
					";function=" + Utils.urlEncode(func) +
					";library=" + Utils.urlEncode(lib) +
					";minOccurs=" + minOccurs +
					";maxOccurs=" + maxOccurs +
					";values=" + buff.toString() +
					")";
			rsp = client.putResource(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	@Override
	public RegistryClientResponse updateMaster(String url) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String req_url = tagfilerURL + "/tags/rtype=master(" +
			"rURL=" + Utils.urlEncode(url) +
			")";
			ClientURLResponse rsp = client.putResource(req_url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#modifyWorker(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse updateWorker(String id, String study, String dataset, String lib, String func, String site, String sourceData, String url) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String req_url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(" +
					"rURL=" + Utils.urlEncode(url) +
					";study=" + Utils.urlEncode(study) +
					";library=" + Utils.urlEncode(lib) +
					";function=" + Utils.urlEncode(func) +
					";dataset=" + Utils.urlEncode(dataset) +
					";site=" + Utils.urlEncode(site) +
					";datasource=" + Utils.urlEncode(sourceData) +
					")";
			ClientURLResponse rsp = client.putResource(req_url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getStudies(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getStudies() {
		RegistryClientResponse ret = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=worker(study)";
			ClientURLResponse rsp = client.get(url, cookie);
			JSONArray arr = new JSONArray(rsp.getEntityString());
			ArrayList<String> studies = new ArrayList<String>();
			for (int i=0; i < arr.length(); i++) {
				String study = arr.getJSONObject(i).getString("study");
				if (!studies.contains(study)) {
					studies.add(study);
				}
			}
			String cname = "cname=";
			for (int i=0; i < studies.size(); i++) {
				if (i != 0) {;
					cname += ",";
				}
				cname += Utils.urlEncode(studies.get(i));
			}
			url = tagfilerURL + "/query/rtype=study;" + cname + "(id;cname)";
			rsp = client.get(url, cookie);
			ret = new TagfilerClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getDatasets(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getDatasets(String study) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=dataset;study=" + Utils.urlEncode(study) + "(id;cname)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getLibraries(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getLibraries() {
		RegistryClientResponse clientResponse = null;
		client.setCookieValue(cookie);
		String url = tagfilerURL + "/query/rtype=library(id;cname)";
		ClientURLResponse rsp = client.get(url, cookie);
		clientResponse = new TagfilerClientResponse(rsp);
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getFunctions(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getFunctions(String lib) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=function;library=" + Utils.urlEncode(lib) + "(id;cname)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getParameters(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getParameters(String func, String lib) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=parameter;function=" + Utils.urlEncode(func) + ";library=" + Utils.urlEncode(lib) + "(id;cname;minOccurs;maxOccurs;values)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	@Override
	public RegistryClientResponse getSites(String study, String dataset,
			String lib, String func) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=worker" +
				";study=" + Utils.urlEncode(study)  +
				";dataset=" + Utils.urlEncode(dataset)  +
				";library=" + Utils.urlEncode(lib)  +
				";function=" + Utils.urlEncode(func)  +
				"(id;site)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getWorkers(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getWorkers(String study, String dataset, String lib, String func, List<String> sites) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=worker" +
							";study=" + Utils.urlEncode(study)  +
							";dataset=" + Utils.urlEncode(dataset) +
							";library=" + Utils.urlEncode(lib) +
							";function=" + Utils.urlEncode(func);
			if (sites != null) {
				url += ";site=";
				for (int i=0; i < sites.size(); i++) {
					if (i != 0) {
						url += ",";
					}
					url += Utils.urlEncode(sites.get(i));
				}
			}
			url += "(id;study;dataset;library;function;site;rURL;datasource)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getStudy(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getStudy(String study) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=study;cname=" + Utils.urlEncode(study)  + "(id;cname)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getDataset(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getDataset(String dataset, String study) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=dataset;cname=" + Utils.urlEncode(dataset) + ";study=" + study + "(id;cname;study)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getLibrary(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getLibrary(String name) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=library;cname=" + Utils.urlEncode(name)  + "(id;cname;rpath)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getFunction(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getFunction(String name, String lib) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=function;cname=" + Utils.urlEncode(name) + ";library=" + Utils.urlEncode(lib) + "(id;cname;library;rpath)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getMaster(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getMaster() {
		RegistryClientResponse clientResponse = null;
		client.setCookieValue(cookie);
		String url = tagfilerURL + "/query/rtype=master(id;rURL)";
		ClientURLResponse rsp = client.get(url, cookie);
		clientResponse = new TagfilerClientResponse(rsp);
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getParameter(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getParameter(String name, String func, String lib) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=parameter;cname=" + Utils.urlEncode(name) + 
				";function=" + Utils.urlEncode(func) +
				";library=" + Utils.urlEncode(lib) +
				"(id;library;function;cname;values;minOccurs;maxOccurs)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getWorker(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getWorker(String study, String dataset, String lib, String func, String site) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=worker;study=" + Utils.urlEncode(study) +
				";dataset=" + Utils.urlEncode(dataset) +
				";library=" + Utils.urlEncode(lib) +
				";function=" + Utils.urlEncode(func) +
				";site=" + Utils.urlEncode(site) +
				"(id;study;dataset;library;function;site;datasource;rURL)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

}
