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
     * 		- the values are the studies description
     *   
     * Example: {"Study1": "Study1 - Designed to investigate the etiology and natural history of atherosclerosis"}
     * 
     */
    public String toStudies();

    /**
     * Returns a string representing a JSONObject for the datasets
     * 		- the keys are the datasets names
     * 		- the values are the datasets description
     *   
     * Example: {"Dataset1": "Dataset1 - Sitting blood pressure measurement among participants"}
     * 
     */
    public String toDatasets();

    /**
     * Returns a string representing a JSONObject for the libraries
     * 		- the keys are the libraries names
     * 		- the values are the libraries description
     *   
     * Example: {"Oceans": "Oceans - Observational Cohort Event Analysis and Notification System",
     * 			 "GLORE": "GLORE - Grid Binary LOgistic REgression"}
     * 
     */
    public String toLibraries();

    /**
     * Returns a string representing a JSONObject for the methods
     * 		- the keys are the methods names
     * 		- the values are the methods description
     *   
     * Example: {"Logistic Regression": "Logistic Regression - Predicting the outcome of a categorical dependent variable"}
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
     * 					"dataset":"Dataset1","datasource":"ca_part3",
     * 					"id":185
     * 				},
     * 				{"method":"Logistic Regression","study":"Study1","site":"UCSD","library":"GLORE",
     * 					"dataset":"Dataset1","datasource":"ca_part1",
     * 					"id":187
     * 				}
     * 			]
     * 
     */
    public String toWorkers();

    /**
     * Returns a string representing a JSONArray for the contacts
     * 		- each array element is a string representing a JSONObject of a contact 
     *   
     * Example: [	{"website":"http://www.study1.org","title":"PI","approvals":"http://www.study1.org/approvals",
     * 					"rtype":"study","agreement":null,"phone":"555-123-4567","contact":"John Smith",
     * 					"address":"Study1<br/>1-st Avenue<br/>New York, NY 10026","email":"john@study1.org"},
     *				{"website":"http://www.scanner-net.org","title":"Project PI","approvals":null,"rtype":"master",
     *					"agreement":null,"phone":"858-822-4931","contact":"Prof. Lucila Ohno-Machado",
     *					"address":"UCSD<br/>Division of Biomedical Informatics<br/>9500 Gilman Dr. MC 0505<br/>La Jolla, CA 92093-0505",
     *					"email":"scanner@ussd.edu"},
     *				{"website":"http://www.usc.edu","title":"Administrator","approvals":null,"rtype":"site",
     *					"agreement":"http://www.usc.edu/agreement","phone":"555-123-4570","contact":"Jane Doe",
     *					"address":"USC<br/>900 West 34th Street<br/>Los Angeles, CA 90033","email":"jane@usc.edu"},
     *				{"website":"http://www.ucsd.edu","title":"Administrator","approvals":null,"rtype":"site",
     *					"agreement":"http://www.ucsd.edu/agreement","phone":"555-123-4581","contact":"Jim Martinez",
     *					"address":"USC<br/>9500 GILMAN DR. # 0533<br/>LA JOLLA, CA  92093-0533","email":"jim@ucsd.edu"},
     *				{"website":"http://www.usc.edu","title":"Administrator","approvals":null,"rtype":"site",
     *					"agreement":"http://www.rand.org/agreement","phone":"555-123-4550","contact":"Dan Wu",
     *					"address":"RAND<br/>1776 Main St<br/>Santa Monica, CA 90401","email":"dan@rand.edu"}
     *			]
     * 
     */
    public String toContacts();

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
     * Example: {"id":187,"site":"UCSD",
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
     * Returns a string representing a JSONArray for the sites
     * 		- each array element is a string representing a JSONObject of the site having as keys the cname, rURL and id
     *   
     * Example: [	{"rURL":"http://scanner.misd.isi.edu:8885/scanner","cname":"SDSC","id":152},
     * 				{"rURL":"http://scanner.misd.isi.edu:8886/scanner","cname":"ISI","id":153},
     * 				{"rURL":"http://scanner.misd.isi.edu:8887/scanner","cname":"USC","id":154},
     * 				{"rURL":"http://scanner.misd.isi.edu:8888/scanner","cname":"UCSD","id":155},
     * 			]
     * 
     */
    public String toSite();

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
