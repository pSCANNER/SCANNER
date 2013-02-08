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
     * @param displayName
     *            the display name of the study
     * @return the Client Response
     */
	public RegistryClientResponse createStudy(String study, String displayName);
	
    /**
     * Creates an entry of "dataset" type in the registry
     * 
     * @param dataset
     *            the name of the dataset
     * @param displayName
     *            the display name of the dataset
     * @return the Client Response
     */
	public RegistryClientResponse createDataset(String dataset, String displayName);
	
    /**
     * Creates an entry of "library" type in the registry
     * 
     * @param name
     *            the name of the library
     * @param displayName
     *            the name of the library to be displayed
     * @param urlPath
     *            the path to be used in the URL
     * @return the Client Response
     */
	public RegistryClientResponse createLibrary(String name, String displayName, String urlPath);
	
    /**
     * Creates an entry of "function" type in the registry
     * 
     * @param name
     *            the name of the function
     * @param displayName
     *            the name of the function to be displayed
     * @param urlPath
     *            the path to be used in the URL
     * @return the Client Response
     */
	public RegistryClientResponse createFunction(String name, String displayName, String urlPath);
	
    /**
     * Creates an entry of "master" type in the registry
     * 
     * @param name
     *            the name of the master
     * @param displayName
     *            the name of the function to be displayed
     * @param url
     *            the url of the master
     * @param study
     *            the study of the master
     * @param dataset
     *            the dataset of the master
     * @param lib
     *            the library of the master
     * @param func
     *            the function of the master
     * @return the Client Response
     */
	public RegistryClientResponse createMaster(String name, String displayName, String url,
			String study, String dataset, String lib, String func);
	
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
     * @return the Client Response
     */
	public RegistryClientResponse createParameter(String name, String displayName, int minOccurs, int maxOccurs, List<String> values);
	
    /**
     * Creates an entry of "worker" type in the registry
     * 
     * @param name
     *            the name of the worker
     * @param sourceData
     *            the data source
     * @param url
     *            the url of the worker
     * @return the Client Response
     */
	public RegistryClientResponse createWorker(String name, String sourceData, String url);
	
    /**
     * Add a dataset to a study
     * 
     * @param dataset
     *            the name of the dataset
     * @param study
     *            the name of the study
     * @return the Client Response
     */
	public RegistryClientResponse addDataset(String dataset, String study);
	
    /**
     * Add a list of datasets to a study
     * 
     * @param dataset
     *            the list of the datasets
     * @param study
     *            the name of the study
     * @return the Client Response
     */
	public RegistryClientResponse addDataset(List<String> dataset, String study);
	
    /**
     * Add a library to a dataset
     * 
     * @param lib
     *            the name of the library
     * @param dataset
     *            the name of the dataset
     * @return the Client Response
     */
	public RegistryClientResponse addLibrary(String lib, String dataset);
	
    /**
     * Add a list of libraries to a dataset
     * 
     * @param lib
     *            the list of the libraries
     * @param dataset
     *            the name of the dataset
     * @return the Client Response
     */
	public RegistryClientResponse addLibrary(List<String> lib, String dataset);
	
    /**
     * Add a function to a library
     * 
     * @param func
     *            the name of the function
     * @param lib
     *            the name of the library
     * @return the Client Response
     */
	public RegistryClientResponse addFunction(String func, String lib);
	
    /**
     * Add a list of functions to a library
     * 
     * @param func
     *            the list of the functions
     * @param lib
     *            the name of the library
     * @return the Client Response
     */
	public RegistryClientResponse addFunction(List<String> func, String lib);
	
    /**
     * Add a parameter to a function
     * 
     * @param param
     *            the name of the parameter
     * @param func
     *            the name of the function
     * @return the Client Response
     */
	public RegistryClientResponse addParameter(String param, String func);
	
    /**
     * Add a list of parameters to a function
     * 
     * @param param
     *            the list of the parameters
     * @param func
     *            the name of the function
     * @return the Client Response
     */
	public RegistryClientResponse addParameter(List<String> param, String func);
	
    /**
     * Add a worker to a master
     * 
     * @param worker
     *            the name of the worker
     * @param master
     *            the name of the master
     * @return the Client Response
     */
	public RegistryClientResponse addWorker(String worker, String master);
	
    /**
     * Add a list of workers to a master
     * 
     * @param worker
     *            the list of the workers
     * @param master
     *            the name of the master
     * @return the Client Response
     */
	public RegistryClientResponse addWorker(List<String> worker, String master);
	
    /**
     * Deletes a study from the registry
     * 
     * @param study
     *            the name of the study
     * @return the Client Response
     */
	public RegistryClientResponse deleteStudy(String study);
	
    /**
     * Deletes a dataset from the registry
     * 
     * @param dataset
     *            the name of the dataset
     * @return the Client Response
     */
	public RegistryClientResponse deleteDataset(String dataset);
	
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
    * @return the Client Response
     */
	public RegistryClientResponse deleteFunction(String name);
	
    /**
     * Delete a master from the registry
     * 
     * @param name
     *            the name of the master
     * @return the Client Response
     */
	public RegistryClientResponse deleteMaster(String name);
	
    /**
     * Delete a parameter from the registry
     * 
     * @param name
     *            the name of the parameter
     * @return the Client Response
     */
	public RegistryClientResponse deleteParameter(String name);
	
    /**
     * Delete a worker from the registry
     * 
     * @param name
     *            the name of the worker
     * @return the Client Response
     */
	public RegistryClientResponse deleteWorker(String name);
	
    /**
     * Delete a parameter from a function
     * 
     * @param name
     *            the name of the parameter
     * @param func
     *            the name of the function
     * @return the Client Response
     */
	public RegistryClientResponse deleteParameter(String name, String func);
	
    /**
     * Delete a list of parameters from a function
     * 
     * @param name
     *            the list of the parameter
     * @param func
     *            the name of the function
     * @return the Client Response
     */
	public RegistryClientResponse deleteParameter(List<String> name, String func);
	
    /**
     * Delete a worker from a master
     * 
     * @param name
     *            the name of the worker
     * @param master
     *            the name of the master
     * @return the Client Response
     */
	public RegistryClientResponse deleteWorker(String name, String master);
	
    /**
     * Delete a list of workers from a master
     * 
     * @param name
     *            the list of the workers
     * @param master
     *            the name of the master
     * @return the Client Response
     */
	public RegistryClientResponse deleteWorker(List<String> name, String master);
	
    /**
     * Delete a function from a library
     * 
     * @param name
     *            the name of the function
     * @param lib
     *            the name of the library
     * @return the Client Response
     */
	public RegistryClientResponse deleteFunction(String name, String lib);
	
    /**
     * Delete a list of functions from a library
     * 
     * @param name
     *            the list of the function
     * @param lib
     *            the name of the library
     * @return the Client Response
     */
	public RegistryClientResponse deleteFunction(List<String> name, String lib);
	
    /**
     * Delete a library from a dataset
     * 
     * @param name
     *            the name of the library
     * @param dataset
     *            the name of the dataset
     * @return the Client Response
     */
	public RegistryClientResponse deleteLibrary(String name, String dataset);
	
    /**
     * Delete a list of libraries from a dataset
     * 
     * @param name
     *            the list of the libraries
     * @param dataset
     *            the name of the dataset
     * @return the Client Response
     */
	public RegistryClientResponse deleteLibrary(List<String> name, String dataset);
	
    /**
     * Delete a dataset from a study
     * 
     * @param name
     *            the name of the dataset
     * @param study
     *            the name of the study
     * @return the Client Response
     */
	public RegistryClientResponse deleteDataset(String name, String study);
	
    /**
     * Delete a list of datasets from a study
     * 
     * @param name
     *            the list of the datasets
     * @param study
     *            the name of the study
     * @return the Client Response
     */
	public RegistryClientResponse deleteDataset(List<String> name, String study);
	
    /**
     * Modify a library 
     * 
     * @param name
     *            the name of the library
     * @param displayName
     *            the new name of the library to be displayed
     * @param urlPath
     *            the new path to be used in the URL
     * @return the Client Response
     */
	public RegistryClientResponse modifyLibrary(String name, String displayName, String urlPath);
	
    /**
     * Modify a function 
     * 
     * @param name
     *            the name of the function
     * @param displayName
     *            the new name of the function to be displayed
     * @param urlPath
     *            the new path to be used in the URL
     * @return the Client Response
     */
	public RegistryClientResponse modifyFunction(String name, String displayName, String urlPath);
	
    /**
     * Modify a parameter 
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
     * @return the Client Response
     */
	public RegistryClientResponse modifyParameter(String name, String displayName, int minOccurs, int maxOccurs, List<String> values);
	
    /**
     * Modify a master 
     * 
     * @param name
     *            the name of the master
     * @param url
     *            the new url of the master
     * @param study
     *            the new study of the master
     * @param dataset
     *            the new dataset of the master
     * @param lib
     *            the new library of the master
     * @param func
     *            the new function of the master
     * @return the Client Response
     */
	public RegistryClientResponse modifyMaster(String name, String url, String study, String dataset, String lib, String func);
	
    /**
     * Modify a worker 
     * 
     * @param name
     *            the name of the worker
     * @param sourceData
     *            the new data source
     * @param url
     *            the new url of the worker
     * @return the Client Response
     */
	public RegistryClientResponse modifyWorker(String name, String sourceData, String url);
	
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
     * @param dataset
     *            the name of the dataset
     * @return the Client Response
     */
	public RegistryClientResponse getLibraries(String dataset);
	
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
	public RegistryClientResponse getParameters(String func);
	
    /**
     * Get the masters for a given study, dataset, library and function 
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
	public RegistryClientResponse getMasters(String study, String dataset, String lib, String func);
	
    /**
     * Get the workers of a master 
     * 
     * @param master
     *            the name of the master
     * @return the Client Response
     */
	public RegistryClientResponse getWorkers(String master);
	
    /**
     * Get a study from the registry
     * 
     * @param study
     *            the name of the study
     * @return the Client Response
     */
	public RegistryClientResponse getStudy(String study);
	
    /**
     * Get a dataset from the registry
     * 
     * @param dataset
     *            the name of the dataset
     * @return the Client Response
     */
	public RegistryClientResponse getDataset(String dataset);
	
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
     * @return the Client Response
     */
	public RegistryClientResponse getFunction(String name);
	
    /**
     * Get a master from the registry
     * 
     * @param name
     *            the name of the master
     * @return the Client Response
     */
	public RegistryClientResponse getMaster(String name);
	
    /**
     * Get a parameter from the registry
     * 
     * @param name
     *            the name of the parameter
     * @return the Client Response
     */
	public RegistryClientResponse getParameter(String name);
	
    /**
     * Get a worker from the registry
     * 
     * @param name
     *            the name of the worker
     * @return the Client Response
     */
	public RegistryClientResponse getWorker(String name);
	

}
