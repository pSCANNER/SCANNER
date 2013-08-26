package edu.isi.misd.scanner.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
 * Tagfiler implementation for accessing the registry.
 * 
 * @author Serban Voinea
 *
 */
public class TagfilerClient implements RegistryClient {
	/**
	 * The HTTP client.
	 * 
	 */
	protected JakartaClient client;
	/**
	 * The Tagfiler URL.
	 * 
	 */
	protected String tagfilerURL;
	/**
	 * The Tagfiler URL for creating entries.
	 * 
	 */
	protected String tagfilerCreateURL;
	/**
	 * The Tagfiler cookie.
	 * 
	 */
	protected String cookie;
	/**
	 * The user's roles.
	 * 
	 */
	protected List<String> roles;

    /**
     * Constructs a client to handle the registry requests. 
     * 
	 * @param client
	 * 		the HTTP client to be used in accessing the registry.
	 * @param tagfilerURL
	 * 		the URL to be used in accessing the registry.
	 * @param cookie
	 * 		the cookie to be used in accessing the registry.
	 * @param request
	 * 		an HttpServletRequest object that contains the request the client has made of the servlet.
     */
	@SuppressWarnings("unchecked")
	public TagfilerClient(JakartaClient client, String tagfilerURL, String cookie, HttpServletRequest request) {
		this(client, tagfilerURL, cookie);
		String url = tagfilerURL + "/query/rtype=worker(users)";
		ClientURLResponse rsp = client.get(url, cookie);
		RegistryClientResponse clientResponse = new TagfilerClientResponse(rsp);
		List<String> res = new ArrayList<String>();
		try {
			JSONArray arr = new JSONArray(clientResponse.getEntityString());
			for (int i=0; i < arr.length(); i++) {
				if (!arr.getJSONObject(i).isNull("users")) {
					JSONArray values = arr.getJSONObject(i).getJSONArray("users");
					for (int j=0; j < values.length(); j++) {
						String role = values.getString(j);
						if (!res.contains(role)) {
							res.add(role);
						}
					}
				}
			}
			System.out.println("Workers roles: ");
			for (int i=0; i < res.size(); i++) {
				System.out.println(res.get(i));
			}
			roles = new ArrayList<String>();
			ArrayList<String> userRoles = (ArrayList<String>) request.getSession(false).getAttribute("roles");
			for (int i=0; i < res.size(); i++) {
				String role = res.get(i);
				if (userRoles.contains(role)) {
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
	
    /**
     * Constructs a client to handle the registry requests. 
     * 
	 * @param client
	 * 		the HTTP client to be used in accessing the registry.
	 * @param tagfilerURL
	 * 		the URL to be used in accessing the registry.
	 * @param cookie
	 * 		the cookie to be used in accessing the registry.
     */
	public TagfilerClient(JakartaClient client, String tagfilerURL, String cookie) {
		this.client = client;
		this.tagfilerURL = tagfilerURL;
		this.cookie = cookie;
		tagfilerCreateURL = tagfilerURL + "/subject/?read+users=*&write+users=*&rtype=";
	}
	
    /**
     * Creates an entry of "study" type in the registry.
     * 
     * @param name
     *            the name of the study.
     * @param description
     *            the description of the study.
     * @param title
     *            the title of the person for contact.
     * @param email
     *            the email for contact.
     * @param phone
     *            the phone for contact.
     * @param website
     *            the website of the study.
     * @param address
     *            the address for contact.
     * @param contact
     *            the name of the person for contact.
     * @param approvals
     *            the website for the study approvals.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse createStudy(String name, String description, String title,
			String email, String phone, String website, String address, String contact, String approvals) {
		RegistryClientResponse clientResponse = null;
		if (name != null) {
			try {
				client.setCookieValue(cookie);
				String url = tagfilerCreateURL + "study&cname=" + Utils.urlEncode(name);
				if (description != null) {
					url += "&description=" + Utils.urlEncode(description);
				}
				if (title != null) {
					url += "&title=" + Utils.urlEncode(title);
				}
				if (email != null) {
					url += "&email=" + Utils.urlEncode(email);
				}
				if (phone != null) {
					url += "&phone=" + Utils.urlEncode(phone);
				}
				if (website != null) {
					url += "&website=" + Utils.urlEncode(website);
				}
				if (address != null) {
					url += "&address=" + Utils.urlEncode(address);
				}
				if (contact != null) {
					url += "&contact=" + Utils.urlEncode(contact);
				}
				if (approvals != null) {
					url += "&approvals=" + Utils.urlEncode(approvals);
				}
				ClientURLResponse rsp = client.postResource(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Creates an entry of "dataset" type in the registry.
     * 
     * @param dataset
     *            the name of the dataset.
     * @param study
     *            the study the dataset belongs to.
     * @param description
     *            the description of the dataset.
     * @param variables
     *            the list of dependent/independent variables names.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse createDataset(String dataset, String study, String description, List<String> variables) {
		RegistryClientResponse clientResponse = null;
		if (dataset != null && study != null) {
			try {
				client.setCookieValue(cookie);
				String url = tagfilerCreateURL + "dataset&cname=" + Utils.urlEncode(dataset) + "&study=" + Utils.urlEncode(study);
				if (description != null) {
					url += "&description=" + Utils.urlEncode(description);
				}
				if (variables != null && variables.size() > 0) {
					url += "&variables=";
					for (int i=0; i < variables.size(); i++) {
						if (i != 0) {
							url += ",";
						}
						url += Utils.urlEncode(variables.get(i));
					}
				}
				ClientURLResponse rsp = client.postResource(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Creates an entry of "library" type in the registry.
     * 
     * @param name
     *            the name of the library.
     * @param urlPath
     *            the path to be used in the URL.
     * @param description
     *            the description of the library.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse createLibrary(String name,
			String urlPath, String description) {
		RegistryClientResponse clientResponse = null;
		if (name != null && urlPath != null) {
			try {
				client.setCookieValue(cookie);
				String url = tagfilerCreateURL + "library&cname=" + Utils.urlEncode(name) + 
						"&rpath=" + Utils.urlEncode(urlPath);
				if (description != null) {
					url += "&description=" + Utils.urlEncode(description);
				}
				ClientURLResponse rsp = client.postResource(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Creates an entry of "method" type in the registry.
     * 
     * @param name
     *            the name of the method.
     * @param libs
     *            the libraries the method belongs to.
     * @param urlPath
     *            the path to be used in the URL.
     * @param description
     *            the description of the method.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse createMethod(String name,
			List<String> libs, String urlPath, String description) {
		RegistryClientResponse clientResponse = null;
		if (name != null && libs != null && libs.size() > 0 && urlPath != null) {
			try {
				client.setCookieValue(cookie);
				String url = tagfilerCreateURL + "method&cname=" + Utils.urlEncode(name) + 
						"&library=";
				for (int i=0; i < libs.size(); i++) {
					if (i != 0) {
						url += ",";
					}
					url += Utils.urlEncode(libs.get(i));
				}
				url += "&rpath=" + Utils.urlEncode(urlPath);
				if (description != null) {
					url += "&description=" + Utils.urlEncode(description);
				}
				ClientURLResponse rsp = client.postResource(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Creates an entry of "master" type in the registry.
     * 
     * @param rURL
     *            the url of the master.
     * @param title
     *            the title of the person for contact.
     * @param email
     *            the email for contact.
     * @param phone
     *            the phone for contact.
     * @param website
     *            the website of the project.
     * @param address
     *            the address for contact.
     * @param contact
     *            the name of the person for contact.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse createMaster(String rURL, String title,
			String email, String phone, String website, String address, String contact) {
		RegistryClientResponse clientResponse = null;
		if (rURL != null) {
			try {
				client.setCookieValue(cookie);
				String url = tagfilerCreateURL + "master" + 
						"&rURL=" + Utils.urlEncode(rURL);
				if (title != null) {
					url += "&title=" + Utils.urlEncode(title);
				}
				if (email != null) {
					url += "&email=" + Utils.urlEncode(email);
				}
				if (phone != null) {
					url += "&phone=" + Utils.urlEncode(phone);
				}
				if (website != null) {
					url += "&website=" + Utils.urlEncode(website);
				}
				if (address != null) {
					url += "&address=" + Utils.urlEncode(address);
				}
				if (contact != null) {
					url += "&contact=" + Utils.urlEncode(contact);
				}
				ClientURLResponse rsp = client.postResource(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}
	
    /**
     * Creates an entry of "parameter" type in the registry.
     * 
     * @param name
     *            the name of the parameter.
     * @param func
     *            the method the parameter belongs to.
     * @param libs
     *            the libraries the parameter belongs to.
     * @param minOccurs
     *            the minimum occurrences of the parameter.
     * @param maxOccurs
     *            the maximum occurrences of the parameter (-1 if unbounded).
     * @param values
     *            the list of the parameter values.
     * @param path
     *            the path of the parameter in the input structure.
     * @param description
     *            the description of the parameter.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse createParameter(String name, String func, List<String> libs, 
			Integer minOccurs, Integer maxOccurs, List<String> values, String path, String description) {
		RegistryClientResponse clientResponse = null;
		if (name != null && func != null && libs != null && libs.size() > 0 && path != null) {
			try {
				client.setCookieValue(cookie);
				String url = tagfilerCreateURL + "parameter&cname=" + Utils.urlEncode(name) + "&library=";
				for (int i=0; i < libs.size(); i++) {
					if (i != 0) {
						url += ",";
					}
					url += Utils.urlEncode(libs.get(i));
				}
				url += "&method=" + Utils.urlEncode(func);
				if (maxOccurs != null) {
					url += "&maxOccurs=" + maxOccurs;
				}
				if (minOccurs != null) {
					url += "&minOccurs=" + minOccurs;
				}
				if (description != null) {
					url += "&description=" + Utils.urlEncode(description);
				}
				if (path != null) {
					url += "&path=" + Utils.urlEncode(path);
				}
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
		}
		return clientResponse;
	}

    /**
     * Creates an entry of "worker" type in the registry.
     * 
     * @param study
     *            the study the worker belongs to.
     * @param dataset
     *            the dataset the worker belongs to.
     * @param lib
     *            the library the worker belongs to.
     * @param func
     *            the method the worker belongs to.
     * @param site
     *            the site the worker belongs to.
     * @param sourceData
     *            the data source.
     * @param users
     *            the users who can access the data source.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse createWorker(String study, String dataset, String lib, String func, String site, 
			String sourceData, List<String> users) {
		RegistryClientResponse clientResponse = null;
		if (study != null && dataset != null && lib != null && func != null && site != null && sourceData != null) {
			try {
				client.setCookieValue(cookie);
				String url = tagfilerCreateURL + "worker&study=" + Utils.urlEncode(study) + 
						"&dataset=" + Utils.urlEncode(dataset) +
						"&library=" + Utils.urlEncode(lib) +
						"&method=" + Utils.urlEncode(func) +
						"&site=" + Utils.urlEncode(site) +
						"&datasource=" + Utils.urlEncode(sourceData);
				if (users != null) {
					url += "&users=";
					for (int i=0; i < users.size(); i++) {
						if (i != 0) {
							url += ",";
						}
						url += Utils.urlEncode(users.get(i));
					}
				}
				ClientURLResponse rsp = client.postResource(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Creates an entry of "site" type in the registry.
     * 
     * @param name
     *            the name of the site.
     * @param rURL
     *            the url of the site.
     * @param title
     *            the title of the person for contact.
     * @param email
     *            the email for contact.
     * @param phone
     *            the phone for contact.
     * @param website
     *            the website of the site.
     * @param address
     *            the address for contact.
     * @param agreement
     *            the URL for agreement.
     * @param contact
     *            the name of the person for contact.
      * @return The client response.
     */
	@Override
	public RegistryClientResponse createSite(String name, String rURL, String title,
			String email, String phone, String website, String address, String agreement, String contact) {
		RegistryClientResponse clientResponse = null;
		if (name != null && rURL != null) {
			try {
				client.setCookieValue(cookie);
				String url = tagfilerCreateURL + "site&cname=" + Utils.urlEncode(name) + "&rURL=" + Utils.urlEncode(rURL);
				if (title != null) {
					url += "&title=" + Utils.urlEncode(title);
				}
				if (email != null) {
					url += "&email=" + Utils.urlEncode(email);
				}
				if (phone != null) {
					url += "&phone=" + Utils.urlEncode(phone);
				}
				if (website != null) {
					url += "&website=" + Utils.urlEncode(website);
				}
				if (address != null) {
					url += "&address=" + Utils.urlEncode(address);
				}
				if (agreement != null) {
					url += "&agreement=" + Utils.urlEncode(agreement);
				}
				if (contact != null) {
					url += "&contact=" + Utils.urlEncode(contact);
				}
				ClientURLResponse rsp = client.postResource(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Deletes a study from the registry.
     * 
     * @param name
     *            the name of the study.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse deleteStudy(String name) {
		RegistryClientResponse clientResponse = null;
		if (name != null) {
			try {
				client.setCookieValue(cookie);
				String url = tagfilerURL + "/subject/rtype;study=" + Utils.urlEncode(name);
				ClientURLResponse rsp = client.delete(url, cookie);
				url = tagfilerURL + "/subject/rtype=study;cname=" + Utils.urlEncode(name);
				rsp = client.delete(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Deletes a library from the registry.
     * 
     * @param name
     *            the name of the library.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse deleteLibrary(String name) {
		RegistryClientResponse clientResponse = null;
		if (name != null) {
			try {
				client.setCookieValue(cookie);
				String url = tagfilerURL + "/subject/rtype=worker;library=" + Utils.urlEncode(name);
				ClientURLResponse rsp = client.delete(url, cookie);
				url = tagfilerURL + "/tags/rtype=method,parameter;library=" + Utils.urlEncode(name) + "(library=" + Utils.urlEncode(name) + ")";
				rsp = client.delete(url, cookie);
				url = tagfilerURL + "/subject/rtype=library;cname=" + Utils.urlEncode(name);
				rsp = client.delete(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Deletes the master from the registry.
     * 
     * @return The client response.
     */
	@Override
	public RegistryClientResponse deleteMaster() {
		client.setCookieValue(cookie);
		String url = tagfilerURL + "/subject/rtype=master";
		ClientURLResponse rsp = client.delete(url, cookie);
		RegistryClientResponse clientResponse = new TagfilerClientResponse(rsp);
		return clientResponse;
	}

    /**
     * Deletes a parameter from the registry.
     * 
     * @param name
     *            the name of the parameter.
     * @param func
     *            the method the parameter belongs to.
     * @param lib
     *            the library the parameter belongs to.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse deleteParameter(String name, String func, String lib) {
		RegistryClientResponse clientResponse = null;
		if (name != null) {
			try {
				client.setCookieValue(cookie);
				ClientURLResponse rsp = null;
				String url;
				if (lib != null) {
					url = tagfilerURL + "/tags/rtype=parameter;cname=" + Utils.urlEncode(name) + ";library=" + Utils.urlEncode(lib);
					if (func != null) {
						url += ";method=" + Utils.urlEncode(func);
					}
					url += "(library=" + Utils.urlEncode(lib) + ")";
					rsp = client.delete(url, cookie);
				} else {
					url = tagfilerURL + "/subject/rtype=parameter" + 
					";cname=" + Utils.urlEncode(name);
					if (func != null) {
						url += ";method=" + Utils.urlEncode(func);
					}
					rsp = client.delete(url, cookie);
				}
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Deletes a worker from the registry.
     * 
     * @param study
     *            the study the worker belongs to.
     * @param dataset
     *            the dataset the worker belongs to.
     * @param lib
     *            the library the worker belongs to.
     * @param func
     *            the method the worker belongs to.
     * @param site
     *            the site the worker belongs to.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse deleteWorker(String study, String dataset, String lib, String func, String site) {
		RegistryClientResponse clientResponse = null;
		if (study != null && dataset != null && lib != null && func != null && site != null) {
			try {
				client.setCookieValue(cookie);
				String url;
				url = tagfilerURL + "/subject/rtype=worker" + 
				";study=" + Utils.urlEncode(study) +
				";dataset=" + Utils.urlEncode(dataset) +
				";library=" + Utils.urlEncode(lib) +
				";method=" + Utils.urlEncode(func) +
				";site=" + Utils.urlEncode(site);
				ClientURLResponse rsp = client.delete(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Deletes a method from the registry.
     * 
     * @param name
     *            the name of the method.
     * @param lib
     *            the library the method belongs to.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse deleteMethod(String name, String lib) {
		RegistryClientResponse clientResponse = null;
		if (name != null) {
			try {
				client.setCookieValue(cookie);
				String url = tagfilerURL + "/subject/rtype;method=" + Utils.urlEncode(name);
				if (lib != null) {
					url += ";library=" + Utils.urlEncode(lib);
				}
				ClientURLResponse rsp = client.delete(url, cookie);
				url = tagfilerURL;
				if (lib != null) {
					url += "/tags/";
				} else {
					url += "/subject/";
				}
				url += "rtype=method" + 
				";cname=" + Utils.urlEncode(name);
				if (lib != null) {
					url += ";library=" + Utils.urlEncode(lib) + "(library=" + Utils.urlEncode(lib) + ")";
				}
				rsp = client.delete(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}
	
    /**
     * Deletes a dataset from the registry.
     * 
     * @param name
     *            the name of the dataset.
     * @param study
     *            the study the dataset belongs to.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse deleteDataset(String name, String study) {
		RegistryClientResponse clientResponse = null;
		if (name != null) {
			try {
				client.setCookieValue(cookie);
				String url = tagfilerURL + "/subject/rtype;dataset=" + Utils.urlEncode(name);
				if (study != null) {
					url += ";study=" + Utils.urlEncode(study);
				}
				ClientURLResponse rsp = client.delete(url, cookie);
				url = tagfilerURL + "/subject/rtype=dataset" + 
				";cname=" + Utils.urlEncode(name);
				if (study != null) {
					url += ";study=" + Utils.urlEncode(study);
				}
				rsp = client.delete(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}
	
    /**
     * Deletes a site from the registry.
     * 
     * @param name
     *            the name of the site.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse deleteSite(String name) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/subject/rtype;site=" + Utils.urlEncode(name);
			ClientURLResponse rsp = client.delete(url, cookie);
			url = tagfilerURL + "/subject/rtype=site;cname=" + Utils.urlEncode(name);
			rsp = client.delete(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Updates an entry of "study" type in the registry.
     * 
     * @param id
     *            the study id in the registry.
     * @param name
     *            the new name of the study.
     * @param description
     *            the new description of the study.
     * @param title
     *            the new title of the person for contact.
     * @param email
     *            the new email for contact.
     * @param phone
     *            the new phone for contact.
     * @param website
     *            the new website of the study.
     * @param address
     *            the new address for contact.
     * @param contact
     *            the new name of the person for contact.
     * @param approvals
     *            the new website for the study approvals.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse updateStudy(String id, String name,
			String description, String title, String email, String phone,
			String website, String address, String contact, String approvals) {
		RegistryClientResponse clientResponse = null;
		if (id != null && name != null || description != null || title != null || email != null || phone != null || website != null ||
				address != null || approvals != null || contact != null) {
			ClientURLResponse rsp = null;
			try {
				client.setCookieValue(cookie);
				String url = null;
				if (name != null) {
					// update the study references
					url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(cname)";
					rsp = client.get(url, cookie);
					JSONObject obj = (new JSONArray(rsp.getEntityString())).getJSONObject(0);
					String studyName = obj.getString("cname");
					if (!studyName.equals(name)) {
						url = tagfilerURL + "/tags/study=" + Utils.urlEncode(studyName) + "(study=" + Utils.urlEncode(name) + ")";
						client.putResource(url, cookie);
					}
				}
				url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(";
				String sep = "";
				if (name != null) {
					url += sep + "cname=" + Utils.urlEncode(name);
					sep = ";";
				}
				if (description != null) {
					url += sep + "description=" + Utils.urlEncode(description);
					sep = ";";
				}
				if (title != null) {
					url += sep + "title=" + Utils.urlEncode(title);
					sep = ";";
				}
				if (email != null) {
					url += sep + "email=" + Utils.urlEncode(email);
					sep = ";";
				}
				if (phone != null) {
					url += sep + "phone=" + Utils.urlEncode(phone);
					sep = ";";
				}
				if (website != null) {
					url += sep + "website=" + Utils.urlEncode(website);
					sep = ";";
				}
				if (address != null) {
					url += sep + "address=" + Utils.urlEncode(address);
					sep = ";";
				}
				if (approvals != null) {
					url += sep + "approvals=" + Utils.urlEncode(approvals);
					sep = ";";
				}
				if (contact != null) {
					url += sep + "contact=" + Utils.urlEncode(contact);
					sep = ";";
				}
				url += ")";
				rsp = client.putResource(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Updates a dataset. 
     * 
     * @param id
     *            the dataset id in the registry.
     * @param name
     *            the new name of the dataset.
     * @param study
     *            the new study the dataset belongs to.
     * @param description
     *            the new description of the dataset.
     * @param variables
     *            the new list of dependent/independent variables names.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse updateDataset(String id, String name,
			String study, String description, List<String> variables) {
		RegistryClientResponse clientResponse = null;
		if (id != null && (name != null || study != null || description != null || variables != null)) {
				try {
					String url = null;
					ClientURLResponse rsp = null;
					client.setCookieValue(cookie);
					String sep = "";
					if (name != null || study != null) {
						// update dataset references
						// get the old name
						url = tagfilerURL + "/query/id=" + id + "(cname;study)";
						rsp = client.get(url, cookie);
						JSONObject obj = (new JSONArray(rsp.getEntityString())).getJSONObject(0);
						String datasetName = obj.getString("cname");
						String studyName = obj.getString("study");
						if (name != null && !name.equals(datasetName) || study != null && !study.equals(studyName)) {
							url = tagfilerURL + "/tags/dataset=" + Utils.urlEncode(datasetName) + ";study=" + Utils.urlEncode(studyName) + "(";
							sep = "";
							if (name != null && !name.equals(datasetName)) {
								url += sep + "dataset=" + Utils.urlEncode(name);
								sep = ";";
							}
							if (study != null && !study.equals(studyName)) {
								url += sep + "study=" + Utils.urlEncode(study);
								sep = ";";
							}
							url += ")";
							rsp = client.putResource(url, cookie);
						}
					}
					if (variables != null) {
						// delete the old values
						url = tagfilerURL + "/tags/id=" + id + "(variables)";
						rsp = client.delete(url, cookie);
					}
					// update now the dataset
					url = tagfilerURL + "/tags/id=" + id + "(";
					sep = "";
					if (name != null) {
						url += sep + "cname=" + Utils.urlEncode(name);
						sep = ";";
					}
					if (study != null) {
						url += sep + "study=" + Utils.urlEncode(study);
						sep = ";";
					}
					if (description != null) {
						url += sep + "description=" + Utils.urlEncode(description);
						sep = ";";
					}
					if (variables != null && variables.size() > 0) {
						url += sep + "variables=";
						for (int i=0; i < variables.size(); i++) {
							if (i != 0) {
								url += ",";
							}
							url += Utils.urlEncode(variables.get(i));
						}
					}
					url += ")";
					rsp = client.putResource(url, cookie);
					clientResponse = new TagfilerClientResponse(rsp);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
		}
		return clientResponse;
	}

    /**
     * Updates a library. 
     * 
     * @param id
     *            the library id in the registry.
     * @param name
     *            the new name of the library.
     * @param urlPath
     *            the new path to be used in the URL.
     * @param description
     *            the new description of the library.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse updateLibrary(String id, String name, String urlPath, String description) {
		RegistryClientResponse clientResponse = null;
		if (id != null && name != null || urlPath != null || description != null) {
			try {
				client.setCookieValue(cookie);
				String url = null;
				ClientURLResponse rsp = null;
				client.setCookieValue(cookie);
				String sep = "";
				if (name != null) {
					url = tagfilerURL + "/query/id=" + id + "(cname)";
					rsp = client.get(url, cookie);
					JSONObject obj = (new JSONArray(rsp.getEntityString())).getJSONObject(0);
					String libName = obj.getString("cname");
					if (!libName.equals(name)) {
						// put the new name
						url = tagfilerURL + "/tags/library=" + Utils.urlEncode(libName) + "(library=" + Utils.urlEncode(name) + ")";
						client.putResource(url, cookie);
						// delete the old references
						url = tagfilerURL + "/tags/library=" + Utils.urlEncode(libName) + "(library=" + Utils.urlEncode(libName) + ")";
						client.delete(url, cookie);
					}
				}
				// set the new values
				url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(";
				sep = "";
				if (name != null) {
					url += sep + "cname=" + Utils.urlEncode(name);
					sep = ";";
				}
				if (urlPath != null) {
					url += sep + "rpath=" + Utils.urlEncode(urlPath);
				}
				if (description != null) {
					url += sep + "description=" + Utils.urlEncode(description);
				}
				url += ")";
				rsp = client.putResource(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Updates a method.
     * 
     * @param id
     *            the id of the method in the registry.
     * @param name
     *            the new name of the method.
     * @param libs
     *            the new libraries the method belongs to.
     * @param urlPath
     *            the new path to be used in the URL.
     * @param description
     *            the new description of the method.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse updateMethod(String id, String name, List<String> libs, String urlPath, String description) {
		RegistryClientResponse clientResponse = null;
		if (id != null && name != null || urlPath != null || libs != null || description != null) {
			try {
				client.setCookieValue(cookie);
				String url = null;
				String sep = "";
				ClientURLResponse rsp = null;
				if (name != null || libs != null) {
					// update method references
					// get the old name
					url = tagfilerURL + "/query/id=" + id + "(cname;library)";
					rsp = client.get(url, cookie);
					JSONObject obj = (new JSONArray(rsp.getEntityString())).getJSONObject(0);
					String methodName = obj.getString("cname");
					JSONArray oldLibs = obj.getJSONArray("library");
					if (libs != null) {
						// delete old libraries references
						ArrayList<String> values = new ArrayList<String>();
						for (int i=0; i < oldLibs.length(); i++) {
							values.add(oldLibs.getString(i));
						}
						ArrayList<String> removeValues = new ArrayList<String>();
						for (int i=0; i < values.size(); i++) {
							if (!libs.contains(values.get(i))) {
								removeValues.add(values.get(i));
							}
						}
						if (removeValues.size() > 0) {
							url = tagfilerURL + "/tags/method=" + Utils.urlEncode(methodName) + "(library=";
							sep = "";
							for (int i=0; i < removeValues.size(); i++) {
								url += sep + removeValues.get(i);
								sep = ",";
							}
							url += ")";
							rsp = client.delete(url, cookie);
						}
						// delete old libraries
						url = tagfilerURL + "/tags/id=" + id + "(library)";
						rsp = client.delete(url, cookie);
					}
					// update method references
					if (name != null && !name.equals(methodName)) {
						url = tagfilerURL + "/tags/method=" + Utils.urlEncode(methodName) + "(method=" + Utils.urlEncode(name) + ")";
						rsp = client.putResource(url, cookie);
					}
				}
				url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(";
				sep = "";
				if (name != null) {
					url += sep + "cname=" + Utils.urlEncode(name);
					sep = ";";
				}
				if (urlPath != null) {
					url += sep + "rpath=" + Utils.urlEncode(urlPath);
					sep = ";";
				}
				if (description != null) {
					url += sep + "description=" + Utils.urlEncode(description);
					sep = ";";
				}
				if (libs != null) {
					url += sep + "library=";
					sep = ";";
					for (int i=0; i < libs.size(); i++) {
						if (i != 0) {
							url += ",";
						}
						url += Utils.urlEncode(libs.get(i));
					}
				}
				url += ")";
				rsp = client.putResource(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Updates a parameter. 
     * 
     * @param id
     *            the id of the parameter in the registry.
     * @param name
     *            the new name of the parameter.
     * @param func
     *            the new method the parameter belongs to.
     * @param libs
     *            the new libraries the parameter belongs to.
     * @param minOccurs
     *            the new minimum occurrences of the parameter.
     * @param maxOccurs
     *            the new maximum occurrences of the parameter (-1 if unbounded).
     * @param values
     *            the new list of the parameter values.
     * @param path
     *            the new path of the parameter in the input structure.
     * @param description
     *            the new description of the parameter.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse updateParameter(String id, String name, String func, List<String> libs, 
			Integer minOccurs, Integer maxOccurs, List<String> values, String path, String description) {
		RegistryClientResponse clientResponse = null;
		if (id != null && name != null || func != null || libs != null || minOccurs != null || maxOccurs != null || values != null || path != null || description != null) {
			try {
				client.setCookieValue(cookie);
				StringBuffer buff = null;
				String url = null;
				ClientURLResponse rsp = null;
				String sep = "";
				if (libs != null || values != null) {
					url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(";
					if (libs != null) {
						url += sep + "library";
						sep = ";";
					}
					if (values != null) {
						url += sep + "values";
						sep = ";";
					}
					url += ")";
					rsp = client.delete(url, cookie);
				}
				url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(";
				sep = "";
				if (name != null) {
					url += sep + "cname=" + Utils.urlEncode(name);
					sep = ";";
				}
				if (func != null) {
					url += sep + "method=" + Utils.urlEncode(func);
					sep = ";";
				}
				if (libs != null && libs.size() > 0) {
					buff = new StringBuffer();
					for (int i=0; i < libs.size(); i++) {
						if (i != 0) {
							buff.append(",");
						}
						buff.append(Utils.urlEncode(libs.get(i)));
					}
					url += sep + "library=" + buff.toString();
					sep = ";";
				}
				if (minOccurs != null) {
					url += sep + "minOccurs=" + minOccurs;
					sep = ";";
				}
				if (maxOccurs != null) {
					url += sep + "maxOccurs=" + maxOccurs;
					sep = ";";
				}
				if (values != null && values.size() > 0) {
					buff = new StringBuffer();
					for (int i=0; i < values.size(); i++) {
						if (i != 0) {
							buff.append(",");
						}
						buff.append(Utils.urlEncode(values.get(i)));
					}
					url += sep + "values=" + buff.toString();
					sep = ";";
				}
				if (path != null) {
					url += sep + "path=" + Utils.urlEncode(path);
					sep = ";";
				}
				if (description != null) {
					url += sep + "description=" + Utils.urlEncode(description);
					sep = ";";
				}
				url += ")";
				rsp = client.putResource(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Updates the master. 
     * 
     * @param rURL
     *            the new url of the master.
     * @param title
     *            the new title of the person for contact.
     * @param email
     *            the new email for contact.
     * @param phone
     *            the new phone for contact.
     * @param website
     *            the new website of the project.
     * @param address
     *            the new address for contact.
     * @param contact
     *            the new name of the person for contact.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse updateMaster(String rURL, String title,
			String email, String phone, String website, String address, String contact) {
		RegistryClientResponse clientResponse = null;
		if (rURL != null || title != null || email != null || phone != null || website != null || address != null || contact != null) {
			try {
				client.setCookieValue(cookie);
				String sep = "";
				String url = tagfilerURL + "/tags/rtype=master(";
				if (rURL != null) {
					url += sep + "rURL=" + Utils.urlEncode(rURL);
					sep = ";";
				}
				if (title != null) {
					url += sep + "title=" + Utils.urlEncode(title);
					sep = ";";
				}
				if (email != null) {
					url += sep + "email=" + Utils.urlEncode(email);
					sep = ";";
				}
				if (phone != null) {
					url += sep + "phone=" + Utils.urlEncode(phone);
					sep = ";";
				}
				if (website != null) {
					url += sep + "website=" + Utils.urlEncode(website);
					sep = ";";
				}
				if (address != null) {
					url += sep + "address=" + Utils.urlEncode(address);
					sep = ";";
				}
				if (contact != null) {
					url += sep + "contact=" + Utils.urlEncode(contact);
					sep = ";";
				}
				url += ")";
				ClientURLResponse rsp = client.putResource(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}
	
    /**
     * Updates the site. 
     * 
     * @param id
     *            the id of the site.
     * @param name
     *            the new name of the site.
     * @param rURL
     *            the new url of the site.
     * @param title
     *            the new title of the person for contact.
     * @param email
     *            the new email for contact.
     * @param phone
     *            the new phone for contact.
     * @param website
     *            the new website of the site.
     * @param address
     *            the new address for contact.
     * @param agreement
     *            the new URL for agreement.
     * @param contact
     *            the new name of the person for contact.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse updateSite(String id, String name, String rURL, String title,
			String email, String phone, String website, String address, String agreement, String contact) {
		RegistryClientResponse clientResponse = null;
		if (id != null && name != null || rURL != null || title != null || email != null || phone != null || website != null ||
				address != null || agreement != null || contact != null) {
			ClientURLResponse rsp = null;
			try {
				client.setCookieValue(cookie);
				String url = null;
				if (name != null) {
					// update the site references
					url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(cname)";
					rsp = client.get(url, cookie);
					JSONObject obj = (new JSONArray(rsp.getEntityString())).getJSONObject(0);
					String siteName = obj.getString("cname");
					if (!siteName.equals(name)) {
						url = tagfilerURL + "/tags/site=" + Utils.urlEncode(siteName) + "(site=" + Utils.urlEncode(name) + ")";
						client.putResource(url, cookie);
					}
				}
				url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(";
				String sep = "";
				if (name != null) {
					url += sep + "cname=" + Utils.urlEncode(name);
					sep = ";";
				}
				if (rURL != null) {
					url += sep + "rURL=" + Utils.urlEncode(rURL);
					sep = ";";
				}
				if (title != null) {
					url += sep + "title=" + Utils.urlEncode(title);
					sep = ";";
				}
				if (email != null) {
					url += sep + "email=" + Utils.urlEncode(email);
					sep = ";";
				}
				if (phone != null) {
					url += sep + "phone=" + Utils.urlEncode(phone);
					sep = ";";
				}
				if (website != null) {
					url += sep + "website=" + Utils.urlEncode(website);
					sep = ";";
				}
				if (address != null) {
					url += sep + "address=" + Utils.urlEncode(address);
					sep = ";";
				}
				if (agreement != null) {
					url += sep + "agreement=" + Utils.urlEncode(agreement);
					sep = ";";
				}
				if (contact != null) {
					url += sep + "contact=" + Utils.urlEncode(contact);
					sep = ";";
				}
				url += ")";
				rsp = client.putResource(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Updates a worker. 
     * 
     * @param id
     *            the id of the worker in the registry.
     * @param study
     *            the new study the worker belongs to.
     * @param dataset
     *            the new dataset the worker belongs to.
     * @param lib
     *            the new library the worker belongs to.
     * @param func
     *            the new method the worker belongs to.
     * @param site
     *            the new site the worker belongs to.
     * @param datasource
     *            the new data source.
     * @param users
     *            the new users who can access the data source.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse updateWorker(String id, String study, String dataset, String lib, String func, String site, 
			String datasource, List<String> users) {
		RegistryClientResponse clientResponse = null;
		ClientURLResponse rsp = null;
		if (id != null && study != null || dataset != null || lib != null || func != null || site != null || datasource != null || users != null) {
			try {
				client.setCookieValue(cookie);
				String url = null;
				String sep = "";
				if (lib != null || users != null) {
					url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(";
					if (lib != null) {
						url += sep + "library";
						sep = ";";
					}
					if (users != null) {
						url += sep + "users";
						sep = ";";
					}
					url += ")";
					rsp = client.delete(url, cookie);
				}
				url = tagfilerURL + "/tags/id=" + Utils.urlEncode(id) +  "(";
				sep = "";
				if (study != null) {
					url += sep + "study=" + Utils.urlEncode(study);
					sep = ";";
				}
				if (dataset != null) {
					url += sep + "dataset=" + Utils.urlEncode(dataset);
					sep = ";";
				}
				if (lib != null) {
					url += sep + "library=" + Utils.urlEncode(lib);
				}
				if (func != null) {
					url += sep + "method=" + Utils.urlEncode(func);
					sep = ";";
				}
				if (site != null) {
					url += sep + "site=" + Utils.urlEncode(site);
					sep = ";";
				}
				if (datasource != null) {
					url += sep + "datasource=" + Utils.urlEncode(datasource);
					sep = ";";
				}
				if (users != null) {
					url += sep + "users=";
					for (int i=0; i < users.size(); i++) {
						if (i != 0) {
							url += ",";
						}
						url += Utils.urlEncode(users.get(i));
					}
				}
				url += ")";
				rsp = client.putResource(url, cookie);
				clientResponse = new TagfilerClientResponse(rsp);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return clientResponse;
	}

    /**
     * Gets the studies. 
     * 
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getStudies() {
		RegistryClientResponse ret = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=worker" + getUserPredicate() + "(study)";
			System.out.println("url: " + url);
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
				if (i != 0) {
					cname += ",";
				}
				cname += Utils.urlEncode(studies.get(i));
			}
			url = tagfilerURL + "/query/rtype=study;" + cname + "(description;cname)";
			rsp = client.get(url, cookie);
			ret = new TagfilerClientResponse(rsp);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

    /**
     * Gets the datasets of a study. 
     * 
     * @param study
     *            the name of the study.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getDatasets(String study) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=worker;study=" + Utils.urlEncode(study) + getUserPredicate() + "(dataset)";
			System.out.println("url: " + url);
			ClientURLResponse rsp = client.get(url, cookie);
			JSONArray arr = new JSONArray(rsp.getEntityString());
			ArrayList<String> datasets = new ArrayList<String>();
			for (int i=0; i < arr.length(); i++) {
				String dataset = arr.getJSONObject(i).getString("dataset");
				if (!datasets.contains(dataset)) {
					datasets.add(dataset);
				}
			}
			String cname = ";cname=";
			for (int i=0; i < datasets.size(); i++) {
				if (i != 0) {
					cname += ",";
				}
				cname += Utils.urlEncode(datasets.get(i));
			}
			url = tagfilerURL + "/query/rtype=dataset;study=" + Utils.urlEncode(study) + cname + "(description;cname)";
			rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Gets the libraries of a dataset. 
     * 
     * @param study
     *            the name of the study the library belongs to.
     * @param dataset
     *            the name of the dataset the library belongs to.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getLibraries(String study, String dataset, String sites) {
		RegistryClientResponse clientResponse = null;
		String method = "";
		ArrayList<String> sitesList = new ArrayList<String>();
		client.setCookieValue(cookie);
		try {
			JSONArray sitesArray = new JSONArray(sites);
			String sitesURL = ";site=";
			for (int i=0; i < sitesArray.length(); i++) {
				if (i != 0) {
					sitesURL += ",";
				}
				sitesURL += Utils.urlEncode(sitesArray.getString(i));
				sitesList.add(sitesArray.getString(i));
			}
			String url = tagfilerURL + "/query/rtype=worker;study=" + Utils.urlEncode(study) + 
			";dataset=" + Utils.urlEncode(dataset) + ";method=" + Utils.urlEncode(method) + sitesURL + getUserPredicate() + "(id;site;library)";
			System.out.println("url: " + url);
			ClientURLResponse rsp = client.get(url, cookie);
			JSONArray arr = new JSONArray(rsp.getEntityString());
			HashMap <String, HashSet<String>> sitesMap = new HashMap <String, HashSet<String>>();
			for (int i=0; i < sitesList.size(); i++) {
				sitesMap.put(sitesList.get(i), new HashSet<String>());
			}
			for (int i=0; i < arr.length(); i++) {
				JSONArray libraryValues = arr.getJSONObject(i).getJSONArray("library");
				String site = arr.getJSONObject(i).getString("site");
				for (int j=0; j < libraryValues.length(); j++) {
					String library = libraryValues.getString(j);
					HashSet<String> libraries = sitesMap.get(site);
					libraries.add(library);
				}
			}
			HashSet<String> librariesSet = sitesMap.get(sitesList.get(0));
			for (int i=1; i < sitesList.size(); i++) {
				librariesSet.retainAll(sitesMap.get(sitesList.get(i)));
			}
			ArrayList<String> libraries = new ArrayList<String>();
			Iterator <String> iter = librariesSet.iterator();
			while (iter.hasNext()) {
				libraries.add(iter.next());
			}
			for (int i=0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				JSONArray values = obj.getJSONArray("library");
				for (int j=0; j < values.length(); j++) {
					String library = values.getString(j);
					if (!libraries.contains(library)) {
						libraries.add(library);
					}
				}
			}
			String cname = "cname=";
			for (int i=0; i < libraries.size(); i++) {
				if (i != 0) {
					cname += ",";
				}
				cname += Utils.urlEncode(libraries.get(i));
			}
			url = tagfilerURL + "/query/rtype=library;" + cname + "(description;cname)";
			rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Gets the methods of a library. 
     * 
     * @param study
     *            the name of the study the method belongs to.
     * @param dataset
     *            the name of the dataset the method belongs to.
     * @param lib
     *            the name of the library the method belongs to.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getMethods(String study, String dataset, String sites) {
		RegistryClientResponse clientResponse = null;
		ArrayList<String> sitesList = new ArrayList<String>();
		try {
			JSONArray sitesArray = new JSONArray(sites);
			String sitesURL = ";site=";
			for (int i=0; i < sitesArray.length(); i++) {
				if (i != 0) {
					sitesURL += ",";
				}
				sitesURL += Utils.urlEncode(sitesArray.getString(i));
				sitesList.add(sitesArray.getString(i));
			}
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=worker;study=" + Utils.urlEncode(study) + 
			";dataset=" + Utils.urlEncode(dataset) + sitesURL + getUserPredicate() + 
			"(method;site;library)";
			ClientURLResponse rsp = client.get(url, cookie);
			JSONArray arr = new JSONArray(rsp.getEntityString());
			HashMap <String, HashSet<String>> sitesMap = new HashMap <String, HashSet<String>>();
			for (int i=0; i < sitesList.size(); i++) {
				sitesMap.put(sitesList.get(i), new HashSet<String>());
			}
			for (int i=0; i < arr.length(); i++) {
				JSONArray methodValues = arr.getJSONObject(i).getJSONArray("method");
				String site = arr.getJSONObject(i).getString("site");
				for (int j=0; j < methodValues.length(); j++) {
					String method = methodValues.getString(j);
					HashSet<String> methods = sitesMap.get(site);
					methods.add(method);
				}
			}
			HashSet<String> methodsSet = sitesMap.get(sitesList.get(0));
			for (int i=1; i < sitesList.size(); i++) {
				methodsSet.retainAll(sitesMap.get(sitesList.get(i)));
			}
			ArrayList<String> methods = new ArrayList<String>();
			Iterator <String> iter = methodsSet.iterator();
			while (iter.hasNext()) {
				methods.add(iter.next());
			}
			String cname = ";cname=";
			for (int i=0; i < methods.size(); i++) {
				if (i != 0) {
					cname += ",";
				}
				cname += Utils.urlEncode(methods.get(i));
			}
			url = tagfilerURL + "/query/rtype=method" + cname + "(description;cname)";
			rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Gets the parameters of a method. 
     * 
     * @param func
     *            the name of the method.
     * @param lib
     *            the name of the library.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getParameters(String dataset, String jsonFile) {
		RegistryClientResponse clientResponse = null;
		client.setCookieValue(cookie);
		//String url = tagfilerURL + "/query/rtype=parameter;method=" + Utils.urlEncode(func) + ";library=" + Utils.urlEncode(lib) + "(id;parentParameter;cname;minOccurs;maxOccurs;position;parameterType;description;selected;linkedParameter;quickHelp;arrayParameter)?limit=none";
		String url = "";
		//ClientURLResponse rsp = client.get(url, cookie);
		//clientResponse = new TagfilerClientResponse(rsp);
		//JSONArray jsonRsp = readParameterFile(jsonFile, lib);
		JSONArray jsonRsp = readParameterFile(jsonFile, dataset);
		clientResponse = new TagfilerClientResponse(jsonRsp);
		return clientResponse;
	}

	public static JSONArray readParameterFile(String file, String lib) {
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
			for (int i=ret.length()-1; i >= 0; i--) {
				JSONObject param = ret.getJSONObject(i);
				JSONArray libs = param.getJSONArray("library");
				boolean hasLib = false;
				for (int j=0; j < libs.length(); j++) {
					if (libs.getString(j).equals(lib)) {
						hasLib = true;
						break;
					}
				}
				if (!hasLib) {
					ret.remove(i);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
    /**
     * Gets the variables of a dataset. 
     * 
     * @param dataset
     *            the name of the dataset.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getVariables(String dataset) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=dataset;cname=" + Utils.urlEncode(dataset) + "(variables)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Gets the sites for a given study, dataset, library and method. 
     * 
     * @param study
     *            the study name.
     * @param dataset
     *            the dataset name.
     * @param lib
     *            the library name.
     * @param func
     *            the method name.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getSites(String study, String dataset
			) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=worker" +
				";study=" + Utils.urlEncode(study)  +
				";dataset=" + Utils.urlEncode(dataset)  +
				getUserPredicate() +
				"(id;site)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
			List<String> sites = new ArrayList<String>();
			String res = clientResponse.getEntityString();
			JSONArray arr = new JSONArray(res);
			for (int i=0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				if (!sites.contains(obj.getString("site"))) {
					sites.add(obj.getString("site"));
				}
			}
			clientResponse = this.getSiteObject(sites, study);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}
	
    /**
     * Gets the workers  for a given study, dataset, library, method and sites.
     * 
     * @param study
     *            the study name.
     * @param dataset
     *            the dataset name.
     * @param lib
     *            the library name.
     * @param func
     *            the method name.
     * @param sites
     *            the sites names.
     * @return The client response.
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
							";method=" + Utils.urlEncode(func) +
							getUserPredicate();
			if (sites != null) {
				url += ";site=";
				for (int i=0; i < sites.size(); i++) {
					if (i != 0) {
						url += ",";
					}
					url += Utils.urlEncode(sites.get(i));
				}
			}
			url += "(id;study;dataset;library;method;site;datasource)";
			System.out.println("url: " + url);
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Gets a study from the registry.
     * 
     * @param study
     *            the name of the study.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getStudy(String study) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=study";
			if (study != null) {
				url += ";cname=" + Utils.urlEncode(study);
			}
			url  += "(id;cname;description;title;email;website;address;contact;approvals)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Gets a dataset from the registry.
     * 
     * @param dataset
     *            the name of the dataset.
     * @param study
     *            the study the dataset belongs to.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getDataset(String dataset, String study) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=dataset";
			if (dataset != null) {
				url += ";cname=" + Utils.urlEncode(dataset);
			}
			if (study != null) {
				url += ";study=" + study;
			}
			url += "(id;cname;study;description;variables)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Gets a library from the registry.
     * 
     * @param name
     *            the name of the library.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getLibrary(String name) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=library";
			if (name != null) {
				url += ";cname=" + Utils.urlEncode(name);
			}
			 url += "(id;cname;rpath;description)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Gets a library from the registry.
     * 
     * @param name
     *            the name of the library.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getLibraryObject(String name) {
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

    /**
     * Gets a method from the registry.
     * 
     * @param name
     *            the name of the method.
     * @param lib
     *            the library the method belongs to.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getMethod(String name, String lib) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=method";
			if (name != null) {
				url += ";cname=" + Utils.urlEncode(name);
			}
			if (lib != null) {
				url += ";library=" + Utils.urlEncode(lib);
			}
			url += "(id;cname;library;rpath;description)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Gets a method from the registry.
     * 
     * @param name
     *            the name of the method.
     * @param lib
     *            the library the method belongs to.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getMethodObject(String name, String lib) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=method;cname=" + Utils.urlEncode(name) + ";library=" + Utils.urlEncode(lib) + "(id;cname;library;rpath)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Gets the master from the registry.
     * 
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getMaster() {
		RegistryClientResponse clientResponse = null;
		client.setCookieValue(cookie);
		String url = tagfilerURL + "/query/rtype=master(id;rURL;title;email;phone;website;address;contact)";
		ClientURLResponse rsp = client.get(url, cookie);
		clientResponse = new TagfilerClientResponse(rsp);
		return clientResponse;
	}

    /**
     * Gets the master from the registry.
     * 
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getMasterObject() {
		RegistryClientResponse clientResponse = null;
		client.setCookieValue(cookie);
		String url = tagfilerURL + "/query/rtype=master(id;rURL)";
		ClientURLResponse rsp = client.get(url, cookie);
		clientResponse = new TagfilerClientResponse(rsp);
		return clientResponse;
	}

    /**
     * Gets a parameter from the registry.
     * 
     * @param name
     *            the name of the parameter.
     * @param func
     *            the method the parameter belongs to.
     * @param lib
     *            the library the parameter belongs to.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getParameter(String name, String func, String lib) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=parameter";
			if (name != null) {
				url += ";cname=" + Utils.urlEncode(name);
			}
			if (func != null) {
				url += ";method=" + Utils.urlEncode(func);
			}
			if (lib != null) {
				url += ";library=" + Utils.urlEncode(lib);
			}
			url += "(id;library;method;cname;path;description;values;minOccurs;maxOccurs)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Gets a worker from the registry.
     * 
     * @param study
     *            the study the worker belongs to.
     * @param dataset
     *            the dataset the worker belongs to.
     * @param lib
     *            the library the worker belongs to.
     * @param func
     *            the method the worker belongs to.
     * @param site
     *            the site the worker belongs to.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getWorker(String study, String dataset, String lib, String func, String site) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=worker";
			if (study != null) {
				url += ";study=" + Utils.urlEncode(study);
			}
			if (dataset != null) {
				url += ";dataset=" + Utils.urlEncode(dataset);
			}
			if (lib != null) {
				url += ";library=" + Utils.urlEncode(lib);
			}
			if (func != null) {
				url += ";method=" + Utils.urlEncode(func);
			}
			if (site != null) {
				url += ";site=" + Utils.urlEncode(site);
			}
			url += "(id;study;dataset;library;method;site;datasource;users)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Gets the site.
     * 
     * @param site
     *            the name of the site.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getSite(String site) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=site";
			if (site != null) {
				url += ";cname=" + Utils.urlEncode(site);
			}
			url += "(id;cname;rURL;title;email;phone;website;address;agreement;contact)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Gets the site(s).
     * 
     * @param sites
     *            the name of the sites; if null or empty then get all the sites.
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getSiteObject(List<String> sites, String study) {
		RegistryClientResponse clientResponse = null;
		try {
			client.setCookieValue(cookie);
			String url = tagfilerURL + "/query/rtype=site";
			if (sites != null && sites.size() > 0) {
				url += ";cname=";
				for (int i=0; i < sites.size(); i++) {
					if (i != 0) {
						url += ",";
					}
					url += Utils.urlEncode(sites.get(i));
				}
			}
			url += "(id;cname;rURL)";
			ClientURLResponse rsp = client.get(url, cookie);
			clientResponse = new TagfilerClientResponse(rsp);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return clientResponse;
	}

    /**
     * Gets the contacts of the SCANNER, studies and sites.
     * 
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getContacts() {
		RegistryClientResponse clientResponse = null;
		client.setCookieValue(cookie);
		String url = tagfilerURL + "/query/contact(cname;rtype;title;contact;email;phone;website;address;agreement;approvals)";
		ClientURLResponse rsp = client.get(url, cookie);
		clientResponse = new TagfilerClientResponse(rsp);
		return clientResponse;
	}
	
    /**
     * Gets the predicate with the users roles.
     * 
     * @return The string representing the predicate of the user roles.
     */
	protected String getUserPredicate() {
		String ret = "";
		if (roles != null && roles.size() > 0) {
			try {
				StringBuffer buff = new StringBuffer(";users=");
				for (int i = 0; i < roles.size(); i++) {
					if (i > 0) {
						buff.append(",");
					}
					buff.append(Utils.urlEncode(roles.get(i)));
				}
				ret = buff.toString();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

    /**
     * Checks if the user has any role.
     * @return True if the user has at least one role.
     */
	@Override
	public boolean hasRoles() {
		return roles != null && roles.size() > 0;
	}

    /**
     * Gets the user roles.
     * @return The user roles.
     */
	@Override
	public List<String> getRoles() {
		return roles;
	}

    /**
     * Gets all the sites.
     * 
     * @return The client response.
     */
	@Override
	public RegistryClientResponse getSitesMap() {
		RegistryClientResponse clientResponse = null;
		client.setCookieValue(cookie);
		String url = tagfilerURL + "/query/rtype=site" +
			"(rURL;cname)";
		//System.out.println("url: " + url);
		ClientURLResponse rsp = client.get(url, cookie);
		clientResponse = new TagfilerClientResponse(rsp);
		return clientResponse;
	}

	@Override
	public RegistryClientResponse getPI() {
		RegistryClientResponse clientResponse = null;
		client.setCookieValue(cookie);
		String url = tagfilerURL + "/query/rtype=user;roles=PI" +
			"(id;firstName;lastName)lastName:asc:";
		//System.out.println("url: " + url);
		ClientURLResponse rsp = client.get(url, cookie);
		clientResponse = new TagfilerClientResponse(rsp);
		return clientResponse;
	}

}
