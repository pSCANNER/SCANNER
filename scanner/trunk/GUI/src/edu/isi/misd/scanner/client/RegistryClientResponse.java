package edu.isi.misd.scanner.client;

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
 * Public Interface for the RegistryClient responses
 * 
 * @author Serban Voinea
 *
 */
public interface RegistryClientResponse {

    /**
     * Return the HTTP status code
     * 
     */
    public int getStatus();

    /**
     * Return the body as a string
     * 
     */
    public String getEntityString();

    /**
     * Return the error message of an HTTP Response
     * 
     */
    public String getErrorMessage();
    
    /**
     * Release the response
     * 
     */
    public void release();

    /**
     * Return the list of studies representing a JSONArray for the AJAX client 
     * 
     */
    public String toStudies();

    /**
     * Return the list of datasets representing a JSONArray for the AJAX client 
     * 
     */
    public String toDatasets();

    /**
     * Return the list of libraries representing a JSONArray for the AJAX client 
     * 
     */
    public String toLibraries();

    /**
     * Return the list of functions representing a JSONArray for the AJAX client 
     * 
     */
    public String toFunctions();


    /**
     * Return the list of sites representing a JSONArray for the AJAX client 
     * 
     */
    public String toSites();
    
    /**
     * Return the list of parameters representing a JSONArray for the AJAX client 
     * 
     */
    public String toParameters();

    /**
     * Return a string representing a JSONArray for the AJAX client 
     * 
     */
    public String toWorkers();

    /**
     * Return the study representing a JSONObject for the AJAX client 
     * 
     */
    public String toStudy();

    /**
     * Return the dataset representing a JSONObject for the AJAX client 
     * 
     */
    public String toDataset();

    /**
     * Return the library representing a JSONObject for the AJAX client 
     * 
     */
    public String toLibrary();

    /**
     * Return the function representing a JSONObject for the AJAX client 
     * 
     */
    public String toFunction();

    /**
     * Return the master representing a JSONObject for the AJAX client 
     * 
     */
    public String toMaster();

    /**
     * Return the worker representing a JSONObject for the AJAX client 
     * 
     */
    public String toWorker();

    /**
     * Return the parameter representing a JSONObject for the AJAX client 
     * 
     */
    public String toParameter();

    /**
     * Return the unique id of the resource in the registry
     * The method will be called for a RegistryClientResponse from one of the following requests:
     * 		- getStudy(String name)
     * 		- getDataset(String name, String study)
     * 		- getLibrary(String name)
     * 		- getFunction(String name, String lib)
     * 		- getMaster()
     * 		- getParameter(String name, String func, String lib)
     * 		- getWorker(String study, String dataset, String lib, String func, String site)
     */
    public String getResourceId();

}
