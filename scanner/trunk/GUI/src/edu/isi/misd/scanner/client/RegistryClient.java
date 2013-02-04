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
     * @param study
     *            the name of the study
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse createStudy(String study, String cookie);
	
    /**
     * Creates an entry of "dataset" type in the registry
     * 
     * @param dataset
     *            the name of the dataset
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse createDataset(String dataset, String cookie);
	
    /**
     * Creates an entry of "library" type in the registry
     * 
     * @param name
     *            the name of the library
     * @param displayName
     *            the name of the library to be displayed
     * @param urlPath
     *            the path to be used in the URL
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse createLibrary(String name, String displayName, String urlPath, String cookie);
	
    /**
     * Creates an entry of "function" type in the registry
     * 
     * @param name
     *            the name of the function
     * @param displayName
     *            the name of the function to be displayed
     * @param urlPath
     *            the path to be used in the URL
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse createFunction(String name, String displayName, String urlPath, String cookie);
	
    /**
     * Creates an entry of "master" type in the registry
     * 
     * @param name
     *            the name of the master
     * @param url
     *            the url of the worker
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse createMaster(String name, String url, String cookie);
	
    /**
     * Creates an entry of "parameter" type in the registry
     * 
     * @param name
     *            the name of the parameter
     * @param displayName
     *            the name of the parameter to be displayed
     * @param minOccurs
     *            the minimum occurrences of the parameter
     * @param maxOccurs
     *            the maximum occurrences of the parameter (-1 if unbounded)
     * @param values
     *            the list of the parameter values
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse createParameter(String name, String displayName, int minOccurs, int maxOccurs, List<String> values, String cookie);
	
    /**
     * Creates an entry of "worker" type in the registry
     * 
     * @param name
     *            the name of the worker
     * @param sourceData
     *            the data source
     * @param url
     *            the url of the worker
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse createWorker(String name, String sourceData, String url, String cookie);
	
    /**
     * Add a dataset to a study
     * 
     * @param dataset
     *            the name of the dataset
     * @param study
     *            the name of the study
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse addDataset(String dataset, String study, String cookie);
	
    /**
     * Add a list of datasets to a study
     * 
     * @param dataset
     *            the list of the datasets
     * @param study
     *            the name of the study
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse addDataset(List<String> dataset, String study, String cookie);
	
    /**
     * Add a library to a dataset
     * 
     * @param lib
     *            the name of the library
     * @param dataset
     *            the name of the dataset
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse addLibrary(String lib, String dataset, String cookie);
	
    /**
     * Add a list of libraries to a dataset
     * 
     * @param lib
     *            the list of the libraries
     * @param dataset
     *            the name of the dataset
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse addLibrary(List<String> lib, String dataset, String cookie);
	
    /**
     * Add a function to a library
     * 
     * @param func
     *            the name of the function
     * @param lib
     *            the name of the library
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse addFunction(String func, String lib, String cookie);
	
    /**
     * Add a list of functions to a library
     * 
     * @param func
     *            the list of the functions
     * @param lib
     *            the name of the library
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse addFunction(List<String> func, String lib, String cookie);
	
    /**
     * Add a function to a library
     * 
     * @param func
     *            the name of the function
     * @param master
     *            the name of the master
     * @param func
     *            the name of the function
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse addMaster(String master, String func, String cookie);
	
    /**
     * Add a parameter to a function
     * 
     * @param param
     *            the name of the parameter
     * @param func
     *            the name of the function
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse addParameter(String param, String func, String cookie);
	
    /**
     * Add a list of parameters to a function
     * 
     * @param param
     *            the list of the parameters
     * @param func
     *            the name of the function
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse addParameter(List<String> param, String func, String cookie);
	
    /**
     * Add a worker to a master
     * 
     * @param worker
     *            the name of the worker
     * @param master
     *            the name of the master
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse addWorker(String worker, String master, String cookie);
	
    /**
     * Add a list of workers to a master
     * 
     * @param worker
     *            the list of the workers
     * @param master
     *            the name of the master
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse addWorker(List<String> worker, String master, String cookie);
	
    /**
     * Deletes a study from the registry
     * 
     * @param study
     *            the name of the study
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse deleteStudy(String study, String cookie);
	
    /**
     * Deletes a dataset from the registry
     * 
     * @param dataset
     *            the name of the dataset
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse deleteDataset(String dataset, String cookie);
	
    /**
     * Delete a library from the registry
     * 
     * @param name
     *            the name of the library
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse deleteLibrary(String name, String cookie);
	
    /**
     * Delete a function from the registry
     * 
     * @param name
     *            the name of the function
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse deleteFunction(String name, String cookie);
	
    /**
     * Delete a master from the registry
     * 
     * @param name
     *            the name of the master
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse deleteMaster(String name, String cookie);
	
    /**
     * Delete a parameter from the registry
     * 
     * @param name
     *            the name of the parameter
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse deleteParameter(String name, String cookie);
	
    /**
     * Delete a worker from the registry
     * 
     * @param name
     *            the name of the worker
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse deleteWorker(String name, String cookie);
	
    /**
     * Delete a parameter from a function
     * 
     * @param name
     *            the name of the parameter
     * @param func
     *            the name of the function
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse deleteParameter(String name, String func, String cookie);
	
    /**
     * Delete a worker from a master
     * 
     * @param name
     *            the name of the worker
     * @param master
     *            the name of the master
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse deleteWorker(String name, String master, String cookie);
	
    /**
     * Modify a library 
     * 
     * @param name
     *            the name of the library
     * @param displayName
     *            the new name of the library to be displayed
     * @param urlPath
     *            the new path to be used in the URL
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse modifyLibrary(String name, String displayName, String urlPath, String cookie);
	
    /**
     * Modify a function 
     * 
     * @param name
     *            the name of the function
     * @param displayName
     *            the new name of the function to be displayed
     * @param urlPath
     *            the new path to be used in the URL
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse modifyFunction(String name, String displayName, String urlPath, String cookie);
	
    /**
     * Modify a function 
     * 
     * @param name
     *            the name of the parameter
     * @param displayName
     *            the new name of the parameter to be displayed
     * @param minOccurs
     *            the new minimum occurrences of the parameter
     * @param maxOccurs
     *            the new maximum occurrences of the parameter (-1 if unbounded)
     * @param values
     *            the new list of the parameter values
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse modifyParameter(String name, String displayName, int minOccurs, int maxOccurs, List<String> values, String cookie);
	
    /**
     * Modify a master 
     * 
     * @param name
     *            the name of the master
     * @param url
     *            the new url of the master
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse modifyMaster(String name, String url, String cookie);
	
    /**
     * Modify a worker 
     * 
     * @param name
     *            the name of the worker
     * @param sourceData
     *            the new data source
     * @param url
     *            the new url of the worker
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse modifyWorker(String name, String sourceData, String url, String cookie);
	
    /**
     * Get the studies 
     * 
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse getStudies(String cookie);
	
    /**
     * Get the datasets of a study 
     * 
     * @param study
     *            the name of the study
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse getDatasets(String study, String cookie);
	
    /**
     * Get the libraries of a dataset 
     * 
     * @param dataset
     *            the name of the dataset
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse getLibraries(String dataset, String cookie);
	
    /**
     * Get the functions of a library 
     * 
     * @param lib
     *            the name of the library
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse getFunctions(String lib, String cookie);
	
    /**
     * Get the parameters of a function 
     * 
     * @param func
     *            the name of the function
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse getParameters(String func, String cookie);
	
    /**
     * Get the master of a function 
     * 
     * @param func
     *            the name of the function
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse getMasters(String func, String cookie);
	
    /**
     * Get the workers of a master 
     * 
     * @param master
     *            the name of the master
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse getWorkers(String master, String cookie);
	
    /**
     * Get a study from the registry
     * 
     * @param study
     *            the name of the study
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse getStudy(String study, String cookie);
	
    /**
     * Get a dataset from the registry
     * 
     * @param dataset
     *            the name of the dataset
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse getDataset(String dataset, String cookie);
	
    /**
     * Get a library from the registry
     * 
     * @param name
     *            the name of the library
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse getLibrary(String name, String cookie);
	
    /**
     * Get a function from the registry
     * 
     * @param name
     *            the name of the function
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse getFunction(String name, String cookie);
	
    /**
     * Get a master from the registry
     * 
     * @param name
     *            the name of the master
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse getMaster(String name, String cookie);
	
    /**
     * Get a parameter from the registry
     * 
     * @param name
     *            the name of the parameter
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse getParameter(String name, String cookie);
	
    /**
     * Get a worker from the registry
     * 
     * @param name
     *            the name of the worker
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
	public RegistryClientResponse getWorker(String name, String cookie);
	
}
