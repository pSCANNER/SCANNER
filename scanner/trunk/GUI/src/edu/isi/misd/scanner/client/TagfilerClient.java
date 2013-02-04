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

	public TagfilerClient(JakartaClient cli, String url) {
		client = cli;
		tagfilerURL = url;
	}
	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createStudy(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createStudy(String study, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createDataset(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createDataset(String dataset, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createLibrary(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createLibrary(String name,
			String displayName, String urlPath, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createFunction(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createFunction(String name,
			String displayName, String urlPath, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createMaster(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createMaster(String name, String url,
			String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createParameter(java.lang.String, java.lang.String, int, int, java.util.List, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createParameter(String name,
			String displayName, int minOccurs, int maxOccurs,
			List<String> values, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#createWorker(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse createWorker(String name, String sourceData,
			String url, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addDataset(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addDataset(String dataset, String study,
			String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addDataset(java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addDataset(List<String> dataset,
			String study, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addLibrary(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addLibrary(String lib, String dataset,
			String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addLibrary(java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addLibrary(List<String> lib, String dataset,
			String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addFunction(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addFunction(String func, String lib,
			String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addFunction(java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addFunction(List<String> func, String lib,
			String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addMaster(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addMaster(String master, String func,
			String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addParameter(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addParameter(String param, String func,
			String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addParameter(java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addParameter(List<String> param, String func,
			String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addWorker(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addWorker(String worker, String master,
			String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#addWorker(java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse addWorker(List<String> worker, String master,
			String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteStudy(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteStudy(String study, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteDataset(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteDataset(String dataset, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteLibrary(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteLibrary(String name, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteFunction(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteFunction(String name, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteMaster(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteMaster(String name, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteParameter(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteParameter(String name, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteWorker(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteWorker(String name, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteParameter(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteParameter(String name, String func,
			String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#deleteWorker(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse deleteWorker(String name, String master,
			String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#modifyLibrary(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse modifyLibrary(String name,
			String displayName, String urlPath, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#modifyFunction(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse modifyFunction(String name,
			String displayName, String urlPath, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#modifyParameter(java.lang.String, java.lang.String, int, int, java.util.List, java.lang.String)
	 */
	@Override
	public RegistryClientResponse modifyParameter(String name,
			String displayName, int minOccurs, int maxOccurs,
			List<String> values, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#modifyMaster(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse modifyMaster(String name, String url,
			String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#modifyWorker(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse modifyWorker(String name, String sourceData,
			String url, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getStudies(java.lang.String)
	 */
	@Override
	public RegistryClientResponse getStudies(String cookie) {
		// TODO Auto-generated method stub
		client.setCookieValue(cookie);
		String url = tagfilerURL + "/query/resourceType=study(rname;resourceDisplayName)";
		ClientURLResponse rsp = client.get(url, cookie);
		return new TagfilerClientResponse(rsp);
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getDatasets(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getDatasets(String study, String cookie) {
		// TODO Auto-generated method stub
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=study;rname=" + Utils.urlEncode(study) + "(rcontains)/(rname;resourceDisplayName)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getLibraries(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getLibraries(String dataset, String cookie) {
		// TODO Auto-generated method stub
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=dataset;rname=" + Utils.urlEncode(dataset) + "(rcontains)/(rname;resourceDisplayName)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getFunctions(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getFunctions(String lib, String cookie) {
		// TODO Auto-generated method stub
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=library;rname=" + Utils.urlEncode(lib) + "(rcontains)/(rname;resourceDisplayName)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getParameters(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getParameters(String func, String cookie) {
		// TODO Auto-generated method stub
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=function;rname=" + Utils.urlEncode(func) + "(rcontains)/(rname;resourceDisplayName;resourceMinOccurs;resourceMaxOccurs;resourceValues)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getMasters(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getMasters(String func, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getWorkers(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getWorkers(String master, String cookie) {
		// TODO Auto-generated method stub
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=master;rname=" + Utils.urlEncode(master)  + "(rcontains)/(resourceURL;resourceData)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getStudy(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getStudy(String study, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getDataset(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getDataset(String dataset, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getLibrary(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getLibrary(String name, String cookie) {
		// TODO Auto-generated method stub
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=library;rname=" + Utils.urlEncode(name)  + "(resourcePath)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getFunction(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getFunction(String name, String cookie) {
		// TODO Auto-generated method stub
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=function;rname=" + Utils.urlEncode(name)  + "(resourceMaster;resourcePath)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getMaster(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getMaster(String name, String cookie) {
		// TODO Auto-generated method stub
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/resourceType=master;rname=" + Utils.urlEncode(name)  + "(rname;rcontains;resourceURL)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clientResponse;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getParameter(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getParameter(String name, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.isi.misd.scanner.client.RegistryClient#getWorker(java.lang.String, java.lang.String)
	 */
	@Override
	public RegistryClientResponse getWorker(String name, String cookie) {
		// TODO Auto-generated method stub
		return null;
	}

}
