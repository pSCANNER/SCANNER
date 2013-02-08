package edu.isi.misd.scanner.client;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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

	public TagfilerClient(JakartaClient client, String tagfilerURL, String cookie) {
		this.client = client;
		this.tagfilerURL = tagfilerURL;
		this.cookie = cookie;
		tagfilerCreateURL = tagfilerURL + "/subject/?read+users=*&write+users=*&resourceType=";
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createStudy(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createStudy(String study, String displayName) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerCreateURL + "study&rname=" + Utils.urlEncode(study) + "&resourceDisplayName=" + Utils.urlEncode(displayName);
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
	public RegistryClientResponse createDataset(String dataset, String displayName) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerCreateURL + "dataset&rname=" + Utils.urlEncode(dataset) + "&resourceDisplayName=" + Utils.urlEncode(displayName);
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
			String displayName, String urlPath) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerCreateURL + "library&rname=" + Utils.urlEncode(name) + 
					"&resourceDisplayName=" + Utils.urlEncode(displayName) +
					"&resourcePath=" + Utils.urlEncode(urlPath);
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
			String displayName, String urlPath) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerCreateURL + "function&rname=" + Utils.urlEncode(name) + 
					"&resourceDisplayName=" + Utils.urlEncode(displayName) +
					"&resourcePath=" + Utils.urlEncode(urlPath);
			ClientURLResponse rsp = client.postResource(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	@Override
	public RegistryClientResponse createMaster(String name, String displayName,
			String url, String study, String dataset, String lib, String func) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String req_url = tagfilerCreateURL + "master&rname=" + Utils.urlEncode(name) + 
					"&resourceDisplayName=" + Utils.urlEncode(displayName) +
					"&resourceURL=" + Utils.urlEncode(url) +
					"&resourceStudy=" + Utils.urlEncode(study) +
					"&resourceDataset=" + Utils.urlEncode(dataset) +
					"&resourceLibrary=" + Utils.urlEncode(lib) +
					"&resourceFunction=" + Utils.urlEncode(func);
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
	public RegistryClientResponse createParameter(String name,
			String displayName, int minOccurs, int maxOccurs,
			List<String> values) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerCreateURL + "parameter&rname=" + Utils.urlEncode(name) + 
					"&resourceDisplayName=" + Utils.urlEncode(displayName) +
					"&resourceMaxOccurs=" + maxOccurs +
					"&resourceMinOccurs=" + minOccurs;
			if (values != null) {
				url += "&resourceValues=";
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
	public RegistryClientResponse createWorker(String name, String sourceData,
			String url) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String req_url = tagfilerCreateURL + "worker&rname=" + Utils.urlEncode(name) + 
					"&resourceDisplayName=" + Utils.urlEncode(name) +
					"&resourceURL=" + Utils.urlEncode(url) +
					"&resourceData=" + Utils.urlEncode(sourceData);
			ClientURLResponse rsp = client.postResource(req_url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addDataset(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addDataset(String dataset, String study) {
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(dataset);
		return addDataset(arr, study);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addDataset(java.util.List, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addDataset(List<String> dataset,
			String study) {
		return addResource(dataset, study);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addLibrary(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addLibrary(String lib, String dataset) {
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(lib);
		return addLibrary(arr, dataset);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addLibrary(java.util.List, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addLibrary(List<String> lib, String dataset) {
		return addResource(lib, dataset);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addFunction(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addFunction(String func, String lib) {
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(func);
		return addFunction(arr, lib);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addFunction(java.util.List, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addFunction(List<String> func, String lib) {
		return addResource(func, lib);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addParameter(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addParameter(String param, String func) {
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(param);
		return addFunction(arr, func);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addParameter(java.util.List, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addParameter(List<String> param, String func) {
		return addResource(param, func);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addWorker(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addWorker(String worker, String master) {
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(worker);
		return addWorker(arr, master);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addWorker(java.util.List, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addWorker(List<String> worker, String master) {
		return addResource(worker, master);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteStudy(java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteStudy(String study) {
		return deleteResource(study);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteDataset(java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteDataset(String dataset) {
		return deleteResource(dataset);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteLibrary(java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteLibrary(String name) {
		return deleteResource(name);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteFunction(java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteFunction(String name) {
		return deleteResource(name);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteMaster(java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteMaster(String name) {
		return deleteResource(name);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteParameter(java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteParameter(String name) {
		return deleteResource(name);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteWorker(java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteWorker(String name) {
		return deleteResource(name);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteParameter(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteParameter(String name, String func) {
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(name);
		return deleteParameter(arr, func);
	}

	@Override
	public RegistryClientResponse deleteParameter(List<String> name,
			String func) {
		return deleteResourceValues(name, func);
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteWorker(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteWorker(String name, String master) {
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(name);
		return deleteWorker(arr, master);
	}

	@Override
	public RegistryClientResponse deleteWorker(List<String> name,
			String master) {
		return deleteResourceValues(name, master);
	}
	@Override
	public RegistryClientResponse deleteFunction(String name, String lib) {
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(name);
		return deleteFunction(arr, lib);
	}
	@Override
	public RegistryClientResponse deleteFunction(List<String> name, String lib) {
		return deleteResourceValues(name, lib);
	}
	@Override
	public RegistryClientResponse deleteLibrary(String name, String dataset) {
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(name);
		return deleteLibrary(arr, dataset);
	}
	@Override
	public RegistryClientResponse deleteLibrary(List<String> name,
			String dataset) {
		return deleteResourceValues(name, dataset);
	}
	@Override
	public RegistryClientResponse deleteDataset(String name, String study) {
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(name);
		return deleteDataset(arr, study);
	}
	@Override
	public RegistryClientResponse deleteDataset(List<String> name,
			String study) {
		return deleteResourceValues(name, study);
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#modifyLibrary(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse modifyLibrary(String name,
			String displayName, String urlPath) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/tags/rname=" + Utils.urlEncode(name) +  "(" +
					"resourceDisplayName=" + Utils.urlEncode(displayName) +
					";resourcePath=" + Utils.urlEncode(urlPath) +
					")";
			ClientURLResponse rsp = client.putResource(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#modifyFunction(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse modifyFunction(String name,
			String displayName, String urlPath) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/tags/rname=" + Utils.urlEncode(name) +  "(" +
					"resourceDisplayName=" + Utils.urlEncode(displayName) +
					";resourcePath=" + Utils.urlEncode(urlPath) +
					")";
			ClientURLResponse rsp = client.putResource(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#modifyParameter(java.lang.String, java.lang.String, int, int, java.util.List)
	 */
	@Override
	public RegistryClientResponse modifyParameter(String name,
			String displayName, int minOccurs, int maxOccurs,
			List<String> values) {
		// to delete first the old values
		StringBuffer buff = new StringBuffer();
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			for (int i=0; i < values.size(); i++) {
				if (i != 0) {
					buff.append(",");
				}
				buff.append(Utils.urlEncode(values.get(i)));
			}
			String url = tagfilerURL + "/tags/rname=" + Utils.urlEncode(name) +  "(" +
					"resourceDisplayName=" + Utils.urlEncode(displayName) +
					";resourceMinOccurs=" + minOccurs +
					";resourceMaxOccurs=" + maxOccurs +
					";resourceValues=" + buff.toString() +
					")";
			ClientURLResponse rsp = client.putResource(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	@Override
	public RegistryClientResponse modifyMaster(String name, String url,
			String study, String dataset, String lib, String func) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String req_url = tagfilerURL + "/tags/rname=" + Utils.urlEncode(name) +  "(" +
			"resourceURL=" + Utils.urlEncode(url) +
			";resourceStudy=" + Utils.urlEncode(study) +
			";resourceDataset=" + Utils.urlEncode(dataset) +
			";resourceLibrary=" + Utils.urlEncode(lib) +
			";resourceFunction=" + Utils.urlEncode(func) +
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
	public RegistryClientResponse modifyWorker(String name, String sourceData,
			String url) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String req_url = tagfilerURL + "/tags/rname=" + Utils.urlEncode(name) +  "(" +
					"resourceURL=" + Utils.urlEncode(url) +
					";resourceData=" + Utils.urlEncode(sourceData) +
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
		client.setCookieValue(cookie);
		String url = tagfilerURL + "/query/resourceType=study(rname;resourceDisplayName)";
		ClientURLResponse rsp = client.get(url, cookie);
		return new TagfilerClientResponse(rsp);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getDatasets(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getDatasets(String study) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=study;rname=" + Utils.urlEncode(study) + "(rcontains)/(rname;resourceDisplayName)";
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
	public RegistryClientResponse getLibraries(String dataset) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=dataset;rname=" + Utils.urlEncode(dataset) + "(rcontains)/(rname;resourceDisplayName)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
			String url = tagfilerURL + "/query/resourceType=library;rname=" + Utils.urlEncode(lib) + "(rcontains)/(rname;resourceDisplayName)";
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
	public RegistryClientResponse getParameters(String func) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=function;rname=" + Utils.urlEncode(func) + "(rcontains)/(rname;resourceDisplayName;resourceMinOccurs;resourceMaxOccurs;resourceValues)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	@Override
	public RegistryClientResponse getMasters(String study, String dataset,
			String lib, String func) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=master" +
				";resourceStudy=" + Utils.urlEncode(study)  +
				";resourceDataset=" + Utils.urlEncode(dataset)  +
				";resourceLibrary=" + Utils.urlEncode(lib)  +
				";resourceFunction=" + Utils.urlEncode(func)  +
				"(rname;resourceDisplayName)";
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
	public RegistryClientResponse getWorkers(String master) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=master;rname=" + Utils.urlEncode(master)  + "(rcontains)/(resourceURL;resourceData)";
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
			String url = tagfilerURL + "/query/resourceType=study;rname=" + Utils.urlEncode(study)  + "(rname;resourceDisplayName;rcontains)";
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
	public RegistryClientResponse getDataset(String dataset) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=dataset;rname=" + Utils.urlEncode(dataset)  + "(rname;resourceDisplayName;rcontains)";
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
			String url = tagfilerURL + "/query/resourceType=library;rname=" + Utils.urlEncode(name)  + "(rname;resourceDisplayName;resourcePath;rcontains)";
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
	public RegistryClientResponse getFunction(String name) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=function;rname=" + Utils.urlEncode(name)  + "(rname;resourceDisplayName;resourcePath;rcontains)";
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
	public RegistryClientResponse getMaster(String name) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=master;rname=" + Utils.urlEncode(name)  + "(rname;resourceDisplayName;rcontains;resourceURL)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getParameter(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getParameter(String name) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=parameter;rname=" + Utils.urlEncode(name)  + "(rname;resourceDisplayName;resourceValues;resourceMinOccurs;resourceMaxOccurs)";
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
	public RegistryClientResponse getWorker(String name) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=worker;rname=" + Utils.urlEncode(name)  + "(rname;resourceDisplayName;resourceData;resourceURL)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	public RegistryClientResponse addResource(List<String> resource,
			String subject) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/tags/rname=" + Utils.urlEncode(subject) + "(rcontains=";
			if (resource != null) {
				for (int i=0; i < resource.size(); i++) {
					if (i != 0) {
						url += ",";
					}
					url += Utils.urlEncode(resource.get(i));
				}
			}
			url += ")";
			ClientURLResponse rsp = client.putResource(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	public RegistryClientResponse deleteResourceValues(List<String> resource,
			String subject) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/tags/rname=" + Utils.urlEncode(subject) + "(rcontains=";
			if (resource != null) {
				for (int i=0; i < resource.size(); i++) {
					if (i != 0) {
						url += ",";
					}
					url += Utils.urlEncode(resource.get(i));
				}
			}
			url += ")";
			ClientURLResponse rsp = client.delete(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

	public RegistryClientResponse deleteResource(String subject) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/subject/rname=" + Utils.urlEncode(subject);
			ClientURLResponse rsp = client.delete(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

}
