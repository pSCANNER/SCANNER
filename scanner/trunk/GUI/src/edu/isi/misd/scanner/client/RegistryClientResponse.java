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
     * Returns a string representing a JSONObject for the studies
     * 		- the keys are the studies names
     * 		- the values are the studies id
     *   
     * Example: {"Study1":196}
     * 
     */
    public String toStudies();

    /**
     * Returns a string representing a JSONObject for the datasets
     * 		- the keys are the datasets names
     * 		- the values are the datasets id
     *   
     * Example: {"Dataset1":195}
     * 
     */
    public String toDatasets();

    /**
     * Returns a string representing a JSONObject for the libraries
     * 		- the keys are the libraries names
     * 		- the values are the libraries id
     *   
     * Example: {"Oceans":193,"GLORE":194}
     * 
     */
    public String toLibraries();

    /**
     * Returns a string representing a JSONObject for the methods
     * 		- the keys are the methods names
     * 		- the values are the methods id
     *   
     * Example: {"Logistic Regression":191}
     * 
     */
    public String toMethods();


    /**
     * Returns a string representing a JSONObject for the sites
     * 		- the keys are the sites names
     * 		- the values are the ids of their workers
     *   
     * Example: {"ISI":182,"UCSD":184,"SDSC":181,"USC":183}
     * 
     */
    public String toSites();
    
    /**
     * Returns a string representing a JSONArray for the parameters
     * 		- each array element is a string representing a JSONObject of the parameter
     * 			- the keys are the parameters ids 
     *   
     * Example: [	{"188":	{"values":["Age","CAD","Creatinine","Diabetes","LOS","Race_Cat"],"maxOccurs":-1,
     * 							"cname":"independentVariableNames","minOccurs":0
     * 						}
     * 				},
     * 				{"189":	{"values":["Outcome"],"maxOccurs":1,"cname":"dependentVariableName","minOccurs":1
     * 						}
     * 				}
     * 			]
     * 
     */
    public String toParameters();

    /**
     * Returns a string representing a JSONArray for the workers
     * 		- each array element is a string representing a JSONObject of the worker 
     *   
     * Example: [	{"method":"Logistic Regression","study":"Study1","site":"ISI","library":"GLORE",
     * 					"dataset":"Dataset1","datasource":"ca_part3","rURL":"http://scanner.misd.isi.edu:8886/scanner",
     * 					"id":185
     * 				},
     * 				{"method":"Logistic Regression","study":"Study1","site":"UCSD","library":"GLORE",
     * 					"dataset":"Dataset1","datasource":"ca_part1","rURL":"http://scanner.misd.isi.edu:8888/scanner",
     * 					"id":187
     * 				}
     * 			]
     * 
     */
    public String toWorkers();

    /**
     * Returns a string representing a JSONObject for the study
     *   
     * Example: {"id":196,"cname":"Study1"}
     * 
     */
    public String toStudy();

    /**
     * Returns a string representing a JSONObject for the dataset
     *   
     * Example: {"id":195,"study":"Study1","cname":"Dataset1"}
     * 
     */
    public String toDataset();

    /**
     * Returns a string representing a JSONObject for the library
     *   
     * Example: {"id":193,"rpath":"oceans","cname":"Oceans"}
     * 
     */
    public String toLibrary();

    /**
     * Returns a string representing a JSONObject for the method
     *   
     * Example: {"id":191,"rpath":"lr","library":"Oceans","cname":"Logistic Regression"}
     * 
     */
    public String toMethod();

    /**
     * Returns a string representing a JSONObject for the master
     *   
     * Example: {"id":180,"rURL":"http://scanner.misd.isi.edu:9999/scanner"}
     * 
     */
    public String toMaster();

    /**
     * Returns a string representing a JSONObject for the worker
     *   
     * Example: {"id":187,"site":"UCSD","rURL":"http://scanner.misd.isi.edu:8888/scanner",
     * 			"dataset":"Dataset1","library":"GLORE","study":"Study1","datasource":"ca_part1",
     * 			"method":"Logistic Regression"}
     * 
     */
    public String toWorker();

    /**
     * Returns a string representing a JSONObject for the parameter
     *   
     * Example: {"id":188,"values":["Age","CAD","Creatinine","Diabetes","LOS","Race_Cat"],
     * 			"maxOccurs":-1,"library":"Oceans","cname":"independentVariableNames","minOccurs":0,
     * 			"method":"Logistic Regression"}
     * 
     */
    public String toParameter();

    /**
     * Return the unique id of the resource in the registry
     * The method will be called for a RegistryClientResponse from one of the following requests:
     * 		- getStudy(String name)
     * 		- getDataset(String name, String study)
     * 		- getLibrary(String name)
     * 		- getMethod(String name, String lib)
     * 		- getMaster()
     * 		- getParameter(String name, String func, String lib)
     * 		- getWorker(String study, String dataset, String lib, String func, String site)
     */
    public String getResourceId();

}
