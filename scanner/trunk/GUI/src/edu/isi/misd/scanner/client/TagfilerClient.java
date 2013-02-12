package edu.isi.misd.scanner.client;

import java.io.UnsupportedEncodingException;
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			String url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(" +
					"cname=" + Utils.urlEncode(name) +
					";rpath=" + Utils.urlEncode(urlPath) +
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
	public RegistryClientResponse updateFunction(String id, String name, String lib, String urlPath) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(" +
					"cname=" + Utils.urlEncode(name) +
					"library=" + Utils.urlEncode(lib) +
					";rpath=" + Utils.urlEncode(urlPath) +
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
	public RegistryClientResponse updateParameter(String id, String name, String func, String lib, int minOccurs, int maxOccurs, List<String> values) {
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
			String url = tagfilerURL + "/tags/id=" + Utils.urlEncode(name) +  "(" +
					"cname=" + Utils.urlEncode(name) +
					";function=" + Utils.urlEncode(func) +
					";library=" + Utils.urlEncode(lib) +
					";minOccurs=" + minOccurs +
					";maxOccurs=" + maxOccurs +
					";values=" + buff.toString() +
					")";
			ClientURLResponse rsp = client.putResource(url, cookie);
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
		client.setCookieValue(cookie);
		String url = tagfilerURL + "/query/rtype=study(id;cname)";
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
	public RegistryClientResponse getLibraries(String dataset) {
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
