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
 * Public interface for accessing the registry.
 * 
 * @author Serban Voinea
 *
 */
public interface RegistryClient {
	
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
	public RegistryClientResponse createStudy(String studyName, String irbId, String studyOwner, String studyStatusType);
	
    /**
     * Creates an entry of "dataset" type in the registry.
     * 
     * @param name
     *            the name of the dataset.
     * @param study
     *            the study the dataset belongs to.
     * @param description
     *            the description of the dataset.
     * @param variables
     *            the list of dependent/independent variables names.
     * @return The client response.
     */
	public RegistryClientResponse createDataset(String name, String study, String description, List<String>  variables);
	
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
	public RegistryClientResponse createLibrary(String name, String urlPath, String description);
	
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
	public RegistryClientResponse createMethod(String name, List<String> libs, String urlPath, String description);
	
    /**
     * Creates an entry of "master" type in the registry.
     * 
     * @param url
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
	public RegistryClientResponse createMaster(String url, String title,
			String email, String phone, String website, String address, String contact);
	
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
	public RegistryClientResponse createSite(String name, String rURL, String title,
			String email, String phone, String website, String address, String agreement, String contact);
	
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
	public RegistryClientResponse createParameter(String name, String func, List<String> libs, 
			Integer minOccurs, Integer maxOccurs, List<String> values, String path, String description);
	
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
	public RegistryClientResponse createWorker(String study, String dataset, String lib, String func, String site, 
			String sourceData, List<String> users);
	
    /**
     * Deletes a study from the registry.
     * 
     * @param name
     *            the name of the study.
     * @return The client response.
     */
	public RegistryClientResponse deleteStudy(String name);
	
    /**
     * Deletes a dataset from the registry.
     * 
     * @param name
     *            the name of the dataset.
     * @param study
     *            the study the dataset belongs to.
     * @return The client response.
     */
	public RegistryClientResponse deleteDataset(String name, String study);
	
    /**
     * Deletes a library from the registry.
     * 
     * @param name
     *            the name of the library.
     * @return The client response.
     */
	public RegistryClientResponse deleteLibrary(String name);
	
    /**
     * Deletes a method from the registry.
     * 
     * @param name
     *            the name of the method.
     * @param lib
     *            the library the method belongs to.
     * @return The client response.
     */
	public RegistryClientResponse deleteMethod(String name, String lib);
	
    /**
     * Deletes the master from the registry.
     * 
     * @return The client response.
     */
	public RegistryClientResponse deleteMaster();
	
    /**
     * Deletes a site from the registry.
     * 
     * @param name
     *            the name of the site.
     * @return The client response.
     */
	public RegistryClientResponse deleteSite(String name);
	
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
	public RegistryClientResponse deleteParameter(String name, String func, String lib);
	
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
	public RegistryClientResponse deleteWorker(String study, String dataset, String lib, String func, String site);
	
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
	public RegistryClientResponse updateStudy(String studyId, String studyName, String irbId, String studyOwner, 
			String studyStatusType, String description, String protocol, String startDate, String endDate, 
			String clinicalTrialsId, String analysisPlan);
	
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
	public RegistryClientResponse updateDataset(String id, String name, String study, String description, List<String>  variables);
	
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
	public RegistryClientResponse updateLibrary(String id, String name, String urlPath, String description);
	
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
	public RegistryClientResponse updateMethod(String id, String name, List<String> libs, String urlPath, String description);
	
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
	public RegistryClientResponse updateParameter(String id, String name, String func, List<String> libs, 
			Integer minOccurs, Integer maxOccurs, List<String> values, String path, String description);
	
    /**
     * Updates the master. 
     * 
     * @param url
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
	public RegistryClientResponse updateMaster(String url, String title,
			String email, String phone, String website, String address, String contact);
	
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
	public RegistryClientResponse updateSite(String id, String name, String rURL, String title,
			String email, String phone, String website, String address, String agreement, String contact);
	
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
     * @param sourceData
     *            the new data source.
     * @param users
     *            the new users who can access the data source.
     * @return The client response.
     */
	public RegistryClientResponse updateWorker(String id, String study, String dataset, String lib, String func, String site, 
			String sourceData, List<String> users);
	
    /**
     * Gets the studies. 
     * 
     * @return The client response.
     */
	public RegistryClientResponse getStudies();
	
    /**
     * Gets the datasets of a study. 
     * 
     * @param study
     *            the name of the study.
     * @return The client response.
     */
	public RegistryClientResponse getDatasets(String study);
	
    /**
     * Gets the libraries of a dataset. 
     * 
     * @param study
     *            the name of the study the library belongs to.
     * @param dataset
     *            the name of the dataset the library belongs to.
     * @return The client response.
     */
	public RegistryClientResponse getLibraries(String study, String dataset, String sites);
	
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
	public RegistryClientResponse getMethods(String study, String dataset, String lib);
	
    /**
     * Gets the parameters of a method. 
     * 
     * @param func
     *            the name of the method.
     * @param lib
     *            the name of the library.
     * @return The client response.
     */
	public RegistryClientResponse getParameters(String dataset, String jsonFile);
	
    /**
     * Gets the variables of a dataset. 
     * 
     * @param dataset
     *            the name of the dataset.
     * @return The client response.
     */
	public RegistryClientResponse getVariables(String dataset);
	
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
	public RegistryClientResponse getSites(String study, String dataset);
	
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
	public RegistryClientResponse getWorkers(String study, String dataset, String lib, String func, List<String> sites);
	
    /**
     * Gets a study from the registry.
     * 
     * @param name
     *            the name of the study.
     * @return The client response.
     */
	public RegistryClientResponse getStudy(String name);
	
    /**
     * Gets a dataset from the registry.
     * 
     * @param name
     *            the name of the dataset.
     * @param study
     *            the study the dataset belongs to.
     * @return The client response.
     */
	public RegistryClientResponse getDataset(String name, String study);
	
    /**
     * Gets a library from the registry.
     * 
     * @param name
     *            the name of the library.
     * @return The client response.
     */
	public RegistryClientResponse getLibrary(String name);
	
    /**
     * Gets a library from the registry.
     * 
     * @param name
     *            the name of the library.
     * @return The client response.
     */
	public RegistryClientResponse getLibraryObject(String name);
	
    /**
     * Gets a method from the registry.
     * 
     * @param name
     *            the name of the method.
     * @param lib
     *            the library the method belongs to.
     * @return The client response.
     */
	public RegistryClientResponse getMethod(String name, String lib);
	
    /**
     * Gets a method from the registry.
     * 
     * @param name
     *            the name of the method.
     * @param lib
     *            the library the method belongs to.
     * @return The client response.
     */
	public RegistryClientResponse getMethodObject(String name, String lib);
	
    /**
     * Gets the master from the registry.
     * 
     * @return The client response.
     */
	public RegistryClientResponse getMaster();
	
    /**
     * Gets the master from the registry.
     * 
     * @return The client response.
     */
	public RegistryClientResponse getMasterObject();
	
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
	public RegistryClientResponse getParameter(String name, String func, String lib);
	
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
	public RegistryClientResponse getWorker(String study, String dataset, String lib, String func, String site);
	
    /**
     * Gets the site.
     * 
     * @param site
     *            the name of the site.
     * @return The client response.
     */
	public RegistryClientResponse getSite(String site);
		
    /**
     * Gets the site(s).
     * 
     * @param sites
     *            the name of the sites; if null or empty then get all the sites.
     * @return The client response.
     */
	public RegistryClientResponse getSiteObject(List<String> sites, String study);
	
    /**
     * Gets the contacts of the SCANNER, studies and sites.
     * 
     * @return The client response.
     */
	public RegistryClientResponse getContacts();
	
    /**
     * Checks if the user has any role.
     * @return True if the user has at least one role.
     */
	public boolean hasRoles();
	
    /**
     * Gets the user roles.
     * @return The user roles.
     */
	public List<String> getRoles();
	
    /**
     * Gets all the sites.
     * 
     * @return The client response.
     */
	public RegistryClientResponse getSitesMap();

    /**
     * Gets the studies. 
     * 
     * @return The client response.
     */
	public RegistryClientResponse getMyStudies();
	

    /**
     * Gets the studies. 
     * 
     * @return The client response.
     */
	public RegistryClientResponse getAllStudies();
	
    /**
     * Gets the studies. 
     * 
     * @return The client response.
     */
	public RegistryClientResponse getAllLibraries();
	
    /**
     * Gets the studies. 
     * 
     * @return The client response.
     */
	public RegistryClientResponse getAllSites();
	
    /**
     * Gets the studies. 
     * 
     * @return The client response.
     */
	public RegistryClientResponse getAllNodes();
	
    /**
     * Gets the studies. 
     * 
     * @return The client response.
     */
	public RegistryClientResponse getAllStandardRoles();
	
    /**
     * Gets the studies. 
     * 
     * @return The client response.
     */
	public RegistryClientResponse getAllStudyManagementPolicies();
	

	public RegistryClientResponse getPI();
	public RegistryClientResponse getUsers();
	public RegistryClientResponse getNodes();
	public RegistryClientResponse getTools();
	public RegistryClientResponse getDatasetInstances();
	public RegistryClientResponse getDatasetDefinitions();
	public RegistryClientResponse getUser(String user);
	public RegistryClientResponse getAnalysisPolicies(int userId, int toolId, int datasetInstanceId);
	public RegistryClientResponse getAnalysisPolicies();
	public RegistryClientResponse getUserRoles();
	public RegistryClientResponse getStudyPolicies();
	public RegistryClientResponse getStudyRoles();
}
