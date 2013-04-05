package edu.isi.misd.scanner.client;

import java.util.List;

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
 * Public Interface for Accessing the Registry
 * 
 * @author Serban Voinea
 *
 */
public interface RegistryClient {
	
    /**
     * Creates an entry of "study" type in the registry
     * 
     * @param name
     *            the name of the study
     * @param description
     *            the description of the study
     * @param title
     *            the title of the person for contact
     * @param email
     *            the email for contact
     * @param phone
     *            the phone for contact
     * @param website
     *            the website of the study
     * @param address
     *            the address for contact
     * @param contact
     *            the name of the person for contact
     * @param approvals
     *            the website for the study approvals
     * @return the Client Response
     */
	public RegistryClientResponse createStudy(String name, String description, String title,
			String email, String phone, String website, String address, String contact, String approvals);
	
    /**
     * Creates an entry of "dataset" type in the registry
     * 
     * @param name
     *            the name of the dataset
     * @param study
     *            the study the dataset belongs to
     * @param description
     *            the description of the study
     * @param variables
     *            the list of dependent/independent variables names
     * @return the Client Response
     */
	public RegistryClientResponse createDataset(String name, String study, String description, List<String>  variables);
	
    /**
     * Creates an entry of "library" type in the registry
     * 
     * @param name
     *            the name of the library
     * @param urlPath
     *            the path to be used in the URL
     * @param description
     *            the description of the study
     * @return the Client Response
     */
	public RegistryClientResponse createLibrary(String name, String urlPath, String description);
	
    /**
     * Creates an entry of "method" type in the registry
     * 
     * @param name
     *            the name of the method
     * @param libs
     *            the libraries the method belongs to
     * @param urlPath
     *            the path to be used in the URL
     * @param description
     *            the description of the study
     * @return the Client Response
     */
	public RegistryClientResponse createMethod(String name, List<String> libs, String urlPath, String description);
	
    /**
     * Creates an entry of "master" type in the registry
     * 
     * @param url
     *            the url of the master
     * @param title
     *            the title of the person for contact
     * @param email
     *            the email for contact
     * @param phone
     *            the phone for contact
     * @param website
     *            the website of the project
     * @param address
     *            the address for contact
     * @param contact
     *            the name of the person for contact
     * @return the Client Response
     */
	public RegistryClientResponse createMaster(String url, String title,
			String email, String phone, String website, String address, String contact);
	
    /**
     * Creates an entry of "site" type in the registry
     * 
     * @param name
     *            the name of the site
     * @param url
     *            the url of the site
     * @param title
     *            the title of the person for contact
     * @param email
     *            the email for contact
     * @param phone
     *            the phone for contact
     * @param website
     *            the website of the project
     * @param address
     *            the address for contact
     * @param agreement
     *            the URL for agreement
     * @param contact
     *            the name of the person for contact
      * @return the Client Response
     */
	public RegistryClientResponse createSite(String name, String rURL, String title,
			String email, String phone, String website, String address, String agreement, String contact);
	
    /**
     * Creates an entry of "parameter" type in the registry
     * 
     * @param name
     *            the name of the parameter
     * @param func
     *            the method the parameter belongs to
     * @param libs
     *            the libraries the parameter belongs to
     * @param minOccurs
     *            the minimum occurrences of the parameter
     * @param maxOccurs
     *            the maximum occurrences of the parameter (-1 if unbounded)
     * @param values
     *            the list of the parameter values
     * @param description
     *            the description of the study
     * @return the Client Response
     */
	public RegistryClientResponse createParameter(String name, String func, List<String> libs, 
			Integer minOccurs, Integer maxOccurs, List<String> values, String path, String description);
	
    /**
     * Creates an entry of "worker" type in the registry
     * 
     * @param study
     *            the study the worker belongs to
     * @param dataset
     *            the dataset the worker belongs to
     * @param lib
     *            the library the worker belongs to
     * @param func
     *            the method the worker belongs to
     * @param site
     *            the site the worker belongs to
     * @param sourceData
     *            the data source
     * @param users
     *            the users who can access the data source
     * @return the Client Response
     */
	public RegistryClientResponse createWorker(String study, String dataset, String lib, String func, String site, 
			String sourceData, List<String> users);
	
    /**
     * Deletes a study from the registry
     * 
     * @param name
     *            the name of the study
     * @return the Client Response
     */
	public RegistryClientResponse deleteStudy(String name);
	
    /**
     * Delete a dataset from the registry
     * 
     * @param name
     *            the name of the dataset
     * @param study
     *            the study the dataset belongs to
     * @return the Client Response
     */
	public RegistryClientResponse deleteDataset(String name, String study);
	
    /**
     * Delete a library from the registry
     * 
     * @param name
     *            the name of the library
     * @return the Client Response
     */
	public RegistryClientResponse deleteLibrary(String name);
	
    /**
     * Delete a method from the registry
     * 
     * @param name
     *            the name of the method
     * @param lib
     *            the library the method belongs to
     * @return the Client Response
     */
	public RegistryClientResponse deleteMethod(String name, String lib);
	
    /**
     * Delete the master from the registry
     * 
     * @return the Client Response
     */
	public RegistryClientResponse deleteMaster();
	
    /**
     * Delete a site from the registry
     * 
     * @param name
     *            the name of the site
     * @return the Client Response
     */
	public RegistryClientResponse deleteSite(String name);
	
    /**
     * Delete a parameter from the registry
     * 
     * @param name
     *            the name of the parameter
     * @param func
     *            the method the parameter belongs to
     * @param lib
     *            the library the parameter belongs to
     * @return the Client Response
     */
	public RegistryClientResponse deleteParameter(String name, String func, String lib);
	
    /**
     * Delete a worker from the registry
     * 
     * @param study
     *            the study the worker belongs to
     * @param dataset
     *            the dataset the worker belongs to
     * @param lib
     *            the library the worker belongs to
     * @param func
     *            the method the worker belongs to
     * @param site
     *            the site the worker belongs to
     * @return the Client Response
     */
	public RegistryClientResponse deleteWorker(String study, String dataset, String lib, String func, String site);
	
    /**
     * Update a library 
     * 
     * @param id
     *            the library id in the registry
     * @param name
     *            the new name of the library
     * @param urlPath
     *            the new path to be used in the URL
     * @return the Client Response
     */
	public RegistryClientResponse updateLibrary(String id, String name, String urlPath);
	
    /**
     * Update a method
     * 
     * @param id
     *            the id of the method in the registry
     * @param name
     *            the new name of the method
     * @param lib
     *            the new library the method belongs to
     * @param urlPath
     *            the new path to be used in the URL
     * @return the Client Response
     */
	public RegistryClientResponse updateMethod(String id, String name, String lib, String urlPath);
	
    /**
     * Update a parameter 
     * 
     * @param id
     *            the id of the parameter in the registry
     * @param name
     *            the new name of the parameter
     * @param func
     *            the new method the parameter belongs to
     * @param lib
     *            the new library the parameter belongs to
     * @param minOccurs
     *            the new minimum occurrences of the parameter
     * @param maxOccurs
     *            the new maximum occurrences of the parameter (-1 if unbounded)
     * @param values
     *            the new list of the parameter values
     * @return the Client Response
     */
	public RegistryClientResponse updateParameter(String id, String name, String func, String lib, Integer minOccurs, Integer maxOccurs, List<String> values);
	
    /**
     * Update the master 
     * 
     * @param url
     *            the new url of the master
     * @return the Client Response
     */
	public RegistryClientResponse updateMaster(String url);
	
    /**
     * Update the site 
     * 
     * @param id
     *            the id of the site
     * @param url
     *            the new url of the site
     * @param name
     *            the new name of the site
     * @return the Client Response
     */
	public RegistryClientResponse updateSite(String id, String name, String url);
	
    /**
     * Modify a worker 
     * 
     * @param id
     *            the id of the worker in the registry
     * @param study
     *            the new study the worker belongs to
     * @param dataset
     *            the new dataset the worker belongs to
     * @param lib
     *            the new library the worker belongs to
     * @param func
     *            the new method the worker belongs to
     * @param site
     *            the new site the worker belongs to
     * @param sourceData
     *            the new data source
     * @param url
     *            the new url of the worker
     * @return the Client Response
     */
	public RegistryClientResponse updateWorker(String id, String study, String dataset, String lib, String func, String site, String sourceData);
	
    /**
     * Get the studies 
     * 
     * @return the Client Response
     */
	public RegistryClientResponse getStudies();
	
    /**
     * Get the datasets of a study 
     * 
     * @param study
     *            the name of the study
     * @return the Client Response
     */
	public RegistryClientResponse getDatasets(String study);
	
    /**
     * Get the libraries of a dataset 
     * 
     * @return the Client Response
     */
	public RegistryClientResponse getLibraries(String study, String dataset);
	
    /**
     * Get the methods of a library 
     * 
     * @param lib
     *            the name of the library
     * @return the Client Response
     */
	public RegistryClientResponse getMethods(String study, String dataset, String lib);
	
    /**
     * Get the parameters of a method 
     * 
     * @param func
     *            the name of the method
     * @param lib
     *            the name of the library
     * @return the Client Response
     */
	public RegistryClientResponse getParameters(String func, String lib);
	
    /**
     * Get the parameters of a method 
     * 
     * @param func
     *            the name of the method
     * @param lib
     *            the name of the library
     * @return the Client Response
     */
	public RegistryClientResponse getVariables(String dataset);
	
    /**
     * Get the sites for a given study, dataset, library and method 
     * 
     * @param study
     *            the study name
     * @param dataset
     *            the dataset name
     * @param lib
     *            the library name
     * @param func
     *            the method name
     * @return the Client Response
     */
	public RegistryClientResponse getSites(String study, String dataset, String lib, String func);
	
    /**
     * Get the workers  for a given study, dataset, library, method and sites
     * 
     * @param study
     *            the study name
     * @param dataset
     *            the dataset name
     * @param lib
     *            the library name
     * @param func
     *            the method name
     * @param sites
     *            the sites names
     * @return the Client Response
     */
	public RegistryClientResponse getWorkers(String study, String dataset, String lib, String func, List<String> sites);
	
    /**
     * Get a study from the registry
     * 
     * @param name
     *            the name of the study
     * @return the Client Response
     */
	public RegistryClientResponse getStudy(String name);
	
    /**
     * Get a dataset from the registry
     * 
     * @param name
     *            the name of the dataset
     * @param study
     *            the study the dataset belongs to
     * @return the Client Response
     */
	public RegistryClientResponse getDataset(String name, String study);
	
    /**
     * Get a library from the registry
     * 
     * @param name
     *            the name of the library
     * @return the Client Response
     */
	public RegistryClientResponse getLibrary(String name);
	
    /**
     * Get a method from the registry
     * 
     * @param name
     *            the name of the method
     * @param lib
     *            the library the method belongs to
     * @return the Client Response
     */
	public RegistryClientResponse getMethod(String name, String lib);
	
    /**
     * Get the master from the registry
     * 
     * @param name
     *            the name of the master
     * @return the Client Response
     */
	public RegistryClientResponse getMaster();
	
    /**
     * Get a parameter from the registry
     * 
     * @param name
     *            the name of the parameter
     * @param func
     *            the method the parameter belongs to
     * @param lib
     *            the library the parameter belongs to
     * @return the Client Response
     */
	public RegistryClientResponse getParameter(String name, String func, String lib);
	
    /**
     * Get a worker from the registry
     * 
     * @param study
     *            the study the worker belongs to
     * @param dataset
     *            the dataset the worker belongs to
     * @param lib
     *            the library the worker belongs to
     * @param func
     *            the method the worker belongs to
     * @param site
     *            the site the worker belongs to
     * @return the Client Response
     */
	public RegistryClientResponse getWorker(String study, String dataset, String lib, String func, String site);
	
    /**
     * Get the site(s)
     * 
     * @param sites
     *            the name of the sites; if null or empty; get all the sites
     * @return the Client Response
     */
	public RegistryClientResponse getSite(List<String> sites);
	
    /**
     * Get the libraries of a site
     * 
     * @param site
     *            the site name
     * @return the Client Response
     */
	public RegistryClientResponse getNodeLibraries(String site);
	
    /**
     * Get the extracts of a node
     * 
     * @param site
     *            the site the worker belongs to
     * @return the Client Response
     */
	public RegistryClientResponse getNodeExtracts(String site);
	
    /**
     * Get the sites of a dataset
     * 
     * @param study
     *            the study the worker belongs to
     * @param dataset
     *            the dataset the worker belongs to
     * @return the Client Response
     */
	public RegistryClientResponse getDatasetSites(String study, String dataset);
	
    /**
     * Get the contacts of the SCANNER, studies and sites
     * 
     * @param study
     *            the study the worker belongs to
     * @param dataset
     *            the dataset the worker belongs to
     * @return the Client Response
     */
	public RegistryClientResponse getContacts();
	
    /**
     * Return true if the user has at least one role
     */
	public boolean hasRoles();
	
    /**
     * Return the roles of the user
     */
	public List<String> getRoles();

}
