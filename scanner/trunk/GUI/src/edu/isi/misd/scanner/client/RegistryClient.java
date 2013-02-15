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
     * @return the Client Response
     */
	public RegistryClientResponse createStudy(String name);
	
    /**
     * Creates an entry of "dataset" type in the registry
     * 
     * @param name
     *            the name of the dataset
     * @param study
     *            the study the dataset belongs to
     * @return the Client Response
     */
	public RegistryClientResponse createDataset(String name, String study);
	
    /**
     * Creates an entry of "library" type in the registry
     * 
     * @param name
     *            the name of the library
     * @param urlPath
     *            the path to be used in the URL
     * @return the Client Response
     */
	public RegistryClientResponse createLibrary(String name, String urlPath);
	
    /**
     * Creates an entry of "function" type in the registry
     * 
     * @param name
     *            the name of the function
     * @param lib
     *            the library the function belongs to
     * @param urlPath
     *            the path to be used in the URL
     * @return the Client Response
     */
	public RegistryClientResponse createFunction(String name, String lib, String urlPath);
	
    /**
     * Creates an entry of "master" type in the registry
     * 
     * @param url
     *            the url of the master
     * @return the Client Response
     */
	public RegistryClientResponse createMaster(String url);
	
    /**
     * Creates an entry of "parameter" type in the registry
     * 
     * @param name
     *            the name of the parameter
     * @param func
     *            the function the parameter belongs to
     * @param lib
     *            the library the parameter belongs to
     * @param minOccurs
     *            the minimum occurrences of the parameter
     * @param maxOccurs
     *            the maximum occurrences of the parameter (-1 if unbounded)
     * @param values
     *            the list of the parameter values
     * @return the Client Response
     */
	public RegistryClientResponse createParameter(String name, String func, String lib, int minOccurs, int maxOccurs, List<String> values);
	
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
     *            the function the worker belongs to
     * @param site
     *            the site the worker belongs to
     * @param sourceData
     *            the data source
     * @param url
     *            the url of the worker
     * @return the Client Response
     */
	public RegistryClientResponse createWorker(String study, String dataset, String lib, String func, String site, String sourceData, String url);
	
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
     * Delete a function from the registry
     * 
     * @param name
     *            the name of the function
     * @param lib
     *            the library the function belongs to
     * @return the Client Response
     */
	public RegistryClientResponse deleteFunction(String name, String lib);
	
    /**
     * Delete the master from the registry
     * 
     * @return the Client Response
     */
	public RegistryClientResponse deleteMaster();
	
    /**
     * Delete a parameter from the registry
     * 
     * @param name
     *            the name of the parameter
     * @param func
     *            the function the parameter belongs to
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
     *            the function the worker belongs to
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
     * Update a function
     * 
     * @param id
     *            the id of the function in the registry
     * @param name
     *            the new name of the function
     * @param lib
     *            the new library the function belongs to
     * @param urlPath
     *            the new path to be used in the URL
     * @return the Client Response
     */
	public RegistryClientResponse updateFunction(String id, String name, String lib, String urlPath);
	
    /**
     * Update a parameter 
     * 
     * @param id
     *            the id of the parameter in the registry
     * @param name
     *            the new name of the parameter
     * @param func
     *            the new function the parameter belongs to
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
	public RegistryClientResponse updateParameter(String id, String name, String func, String lib, int minOccurs, int maxOccurs, List<String> values);
	
    /**
     * Update the master 
     * 
     * @param url
     *            the new url of the master
     * @return the Client Response
     */
	public RegistryClientResponse updateMaster(String url);
	
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
     *            the new function the worker belongs to
     * @param site
     *            the new site the worker belongs to
     * @param sourceData
     *            the new data source
     * @param url
     *            the new url of the worker
     * @return the Client Response
     */
	public RegistryClientResponse updateWorker(String id, String study, String dataset, String lib, String func, String site, String sourceData, String url);
	
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
	public RegistryClientResponse getLibraries();
	
    /**
     * Get the functions of a library 
     * 
     * @param lib
     *            the name of the library
     * @return the Client Response
     */
	public RegistryClientResponse getFunctions(String lib);
	
    /**
     * Get the parameters of a function 
     * 
     * @param func
     *            the name of the function
     * @return the Client Response
     */
	public RegistryClientResponse getParameters(String func, String lib);
	
    /**
     * Get the sites for a given study, dataset, library and function 
     * 
     * @param study
     *            the study name
     * @param dataset
     *            the dataset name
     * @param lib
     *            the library name
     * @param func
     *            the function name
     * @return the Client Response
     */
	public RegistryClientResponse getSites(String study, String dataset, String lib, String func);
	
    /**
     * Get the workers  for a given study, dataset, library, function and sites
     * 
     * @param master
     *            the name of the master
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
     * Get a function from the registry
     * 
     * @param name
     *            the name of the function
     * @param lib
     *            the library the function belongs to
     * @return the Client Response
     */
	public RegistryClientResponse getFunction(String name, String lib);
	
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
     *            the function the parameter belongs to
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
     *            the function the worker belongs to
     * @param site
     *            the site the worker belongs to
     * @return the Client Response
     */
	public RegistryClientResponse getWorker(String study, String dataset, String lib, String func, String site);
	

}
