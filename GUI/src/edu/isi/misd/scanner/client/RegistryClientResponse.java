package edu.isi.misd.scanner.client;

import org.json.JSONObject;

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
     * Example: {"MTM": "MTM - Medication Therapy Management"}
     * 
     */
    public String toStudies();

    /**
     * Returns a string representing a JSONObject for the datasets
     * 		- the keys are the datasets names
     * 		- the values are the datasets description
     *   
     * Example: {"MTM_SIMULATED": "MTM_SIMULATED - Medication Therapy Management Simulated"}
     * 
     */
    public String toDatasets();

    /**
     * Returns a string representing a JSONObject for the libraries
     * 		- the keys are the libraries names
     * 		- the values are the libraries description
     *   
     * Example: {	"OCEANS": "OCEANS - Observational Cohort Event Analysis and Notification System",
     * 				"GLORE": "GLORE - Grid Binary LOgistic REgression"
     * 			}
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
     * Example: {	"UCSD": "168",
     * 				"USC": "167",
     * 				"RAND": "169"
     * 			}
     * 
     */
    public String toSites();
    
    /**
     * Returns a string representing a JSONObject for the parameters
     * 		- the keys represent the parameter names and the values represent their description
     * 		- the "params" describes the structure of the input parameters 
     * 			- the dependentVariableName/independentVariableName contains a list with their values:
     * 				- the value if separated by the HTML line separator <br/> from its description
     * Example: {"description":{	"id": "id - Query identifier",
     * 								"dependentVariableName": "dependentVariableName - Dependent variables",
     * 								"description": "description - Query description", 
     * 								"name": "name - Query name",
     * 								"independentVariableName": "independentVariableName - Independent variables"},
     * 								"params": {	"logisticRegressionInput":
     * 											{"inputParameters":
     * 												{"dependentVariableName":
     * 													[	"a1c baseline<br/>HbA1c at baseline",
     * 														"a1c final<br/>HbA1c at final measure",
     * 														"age<br/>Age",
     * 														"bmi baseline<br/>BMI at baseline",
     * 														"bmi final<br/>BMI at final measure",
     * 														"categorical_insurance<br/>Insurance",
     * 														"days between index date and final measure date<br/>Days between index date and final HbA1c Measure",
     * 														"dibp baseline<br/>Diasolic Blood Pressure At Baseline",
     * 														"dibp final<br/>Diasolic Blood Pressure at final measurel",
     * 														"eth<br/>Ethnicity","group<br/>Received MTM",
     * 														"hdl baseline<br/>HDL at baseline",
     * 														"hdl final<br/>HDL at final measure",
     * 														"ldl baseline<br/>LDL at baseline",
     * 														"ldl final<br/>LDL at final measure",
     * 														"male<br/>Male Gender",
     * 														"number of visits<br/>Number of visits during study",
     * 														"outcome_a1c_final_lt_9<br/>Final HbA1c is less than 9",
     * 														"smoke<br/>Currently Smokes",
     * 														"sybp baseline<br/>Systolic Blood Pressure At Baseline",
     * 														"sybp final<br/>Systolic Blood Pressure at final measure",
     * 														"totchol baseline<br/>Cholesterol at baseline",
     * 														"totchol final<br/>Total Cholesterol at final measure",
     * 														"trig baseline<br/>Triglicerides at baseline",
     * 														"trig final<br/>Triglicerides at final measaure"
     * 													],
     * 												"independentVariableName":
     * 													[	"a1c baseline<br/>HbA1c at baseline",
     * 														"a1c final<br/>HbA1c at final measure",
     * 														"age<br/>Age",
     * 														"bmi baseline<br/>BMI at baseline",
     * 														"bmi final<br/>BMI at final measure","categorical_insurance<br/>Insurance",
     * 														"days between index date and final measure date<br/>Days between index date and final HbA1c Measure",
     * 														"dibp baseline<br/>Diasolic Blood Pressure At Baseline",
     * 														"dibp final<br/>Diasolic Blood Pressure at final measurel",
     * 														"eth<br/>Ethnicity",
     * 														"group<br/>Received MTM",
     * 														"hdl baseline<br/>HDL at baseline",
     * 														"hdl final<br/>HDL at final measure",
     * 														"ldl baseline<br/>LDL at baseline",
     * 														"ldl final<br/>LDL at final measure",
     * 														"male<br/>Male Gender",
     * 														"number of visits<br/>Number of visits during study",
     * 														"outcome_a1c_final_lt_9<br/>Final HbA1c is less than 9",
     * 														"smoke<br/>Currently Smokes",
     * 														"sybp baseline<br/>Systolic Blood Pressure At Baseline",
     * 														"sybp final<br/>Systolic Blood Pressure at final measure",
     * 														"totchol baseline<br/>Cholesterol at baseline",
     * 														"totchol final<br/>Total Cholesterol at final measure",
     * 														"trig baseline<br/>Triglicerides at baseline",
     * 														"trig final<br/>Triglicerides at final measaure"
     * 													]
     * 												},
     * 											"inputDescription":{	"id": ["string"],
     * 																	"description": ["string"],
     * 																	"name": ["string"]
     * 																}
     * 										}
     * 							}
     * 				}
     * 
     */
    public String toParameters(String variables);

    /**
     * Returns a string representing a JSONArray for the workers
     * 		- each array element is a string representing a JSONObject of the worker 
     * Example:	[	{"study":"MTM","site":"USC","library":["OCEANS"],"id":563,"datasource":"MTM_SIMULATED_ALTAMED.csv","dataset":"MTM_SIMULATED","method":"Logistic Regression"},
     * 				{"study":"MTM","site":"UCSD","library":["OCEANS"],"id":565,"datasource":"MTM_SIMULATED_UCSD.csv","dataset":"MTM_SIMULATED","method":"Logistic Regression"},
     * 				{"study":"MTM","site":"RAND","library":["OCEANS"],"id":568,"datasource":"MTM_SIMULATED_RAND.csv","dataset":"MTM_SIMULATED","method":"Logistic Regression"}
     * 			]
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
     * Returns a string representing a JSONArray with the studies
     *   
     * Example: [{	"website":"http://www.MTM.org",
     * 				"description":"MTM - Medication Therapy Management",
     * 				"title":"PI",
     * 				"approvals":"http://www.MTM.org/approvals",
     * 				"email":"john@MTM.org",
     * 				"cname":"MTM",
     * 				"contact":"John Smith",
     * 				"address":"MTM<br/>1-st Avenue<br/>New York, NY 10026",
     * 				"id":547
     * 			}]
     * 
     */
    public String toStudy();

    /**
     * Returns a string representing a JSONArray with the datasets
     *   
     * Example: [{	"variables":[
     * 								"totchol final<br/>Total Cholesterol at final measure",
     * 								"eth<br/>Ethnicity",
     * 								"trig baseline<br/>Triglicerides at baseline",
     * 								"sybp baseline<br/>Systolic Blood Pressure At Baseline",
     * 								"a1c final<br/>HbA1c at final measure",
     * 								"smoke<br/>Currently Smokes",
     * 								"number of visits<br/>Number of visits during study",
     * 								"group<br/>Received MTM",
     * 								"dibp baseline<br/>Diasolic Blood Pressure At Baseline",
     * 								"ldl final<br/>LDL at final measure",
     * 								"a1c baseline<br/>HbA1c at baseline",
     * 								"age<br/>Age",
     * 								"sybp final<br/>Systolic Blood Pressure at final measure",
     * 								"categorical_insurance<br/>Insurance",
     * 								"male<br/>Male Gender",
     * 								"ldl baseline<br/>LDL at baseline",
     * 								"dibp final<br/>Diasolic Blood Pressure at final measurel",
     * 								"bmi baseline<br/>BMI at baseline",
     * 								"trig final<br/>Triglicerides at final measaure",
     * 								"hdl final<br/>HDL at final measure",
     * 								"days between index date and final measure date<br/>Days between index date and final HbA1c Measure",
     * 								"bmi final<br/>BMI at final measure",
     * 								"hdl baseline<br/>HDL at baseline",
     * 								"totchol baseline<br/>Cholesterol at baseline",
     * 								"outcome_a1c_final_lt_9<br/>Final HbA1c is less than 9"
     * 							],
     * 					"study":"MTM","cname":"MTM_SIMULATED",
     * 					"id":548,
     * 					"description":"MTM_SIMULATED - Medication Therapy Management Simulated"
     * 			}] 
     * 
     */
    public String toDataset();

    /**
     * Returns a string representing a JSONArray with the libraries
     *   
     * Example: [	{	"rpath":"oceans",
     * 					"cname":"OCEANS",
     * 					"id":549,
     * 					"description":"OCEANS - Observational Cohort Event Analysis and Notification System"
     * 				},
     * 				{	"rpath":"glore",
     * 					"cname":"GLORE",
     * 					"id":550,
     * 					"description":"GLORE - Grid Binary LOgistic REgression"
     * 				}
     * 			]
     * 
     */
    public String toLibrary();

    /**
     * Returns a string representing a JSONArray with the methods
     *   
     * Example: [{	"rpath":"lr",
     * 				"cname":"Logistic Regression",
     * 				"id":551,
     * 				"library":["GLORE","OCEANS"],
     * 				"description":"Logistic Regression - Predicting the outcome of a categorical dependent variable"
     * 			}]
     * 
     */
    public String toMethod();

    /**
     * Returns a string representing a JSONArray with the master
     *   
     * Example: [{	"website": "http://scanner.ucsd.edu/",
     * 				"title": "Project PI",
     * 				"id": 559,
     * 				"phone": "858-822-4931",
     * 				"contact": "Prof. Lucila Ohno-Machado",
     * 				"address": "UCSD<br/>Division of Biomedical Informatics<br/>9500 Gilman Dr. MC 0505<br/>La Jolla, CA 92093-0505",
     * 				"rURL": "https://master.scanner-net.org:9999/scanner",
     * 				"email": "sdsc-scanner@ucsd.edu"
     * 			}] 
     * 
     */
    public String toMaster();

    /**
     * Returns a string representing a JSONArray with the workers
     *   
     * Example: [{	"users": ["test-role@@misd.isi.edu"],
     * 				"study": "MTM",
     * 				"site": "USC",
     * 				"library": ["OCEANS"],
     * 				"id": 563,
     * 				"datasource": "MTM_SIMULATED_ALTAMED.csv",
     * 				"dataset": "MTM_SIMULATED",
     * 				"method": "Logistic Regression"
     * 			},
     * 			{	"users": ["test-role@@misd.isi.edu"],
     * 				"study": "MTM",
     * 				"site": "USC",
     * 				"library": ["GLORE"],
     * 				"id": 564,
     * 				"datasource": "MTM_SIMULATED_ALTAMED.csv",
     * 				"dataset": "MTM_SIMULATED",
     * 				"method": "Logistic Regression"
     * 			},
     * 			{	"users": ["test-role@@misd.isi.edu"],
     * 				"study": "MTM",
     * 				"site": "UCSD",
     * 				"library": ["OCEANS"],
     * 				"id": 565,
     * 				"datasource": "MTM_SIMULATED_UCSD.csv",
     * 				"dataset": "MTM_SIMULATED",
     * 				"method": Logistic Regression"
     * 			},
     * 			{	"users": ["test-role@@misd.isi.edu"],
     * 				"study": "MTM",
     * 				"site": "UCSD",
     * 				"library": ["GLORE"],
     * 				"id": 566,
     * 				"datasource": "MTM_SIMULATED_UCSD.csv",
     * 				"dataset": "MTM_SIMULATED",
     * 				"method":"Logistic Regression"
     * 			},
     * 			{	"users": ["test-role@@misd.isi.edu", "RAND-role@@misd.isi.edu"],
     * 				"study": "MTM",
     * 				"site": "RAND",
     * 				"library": ["GLORE"],
     * 				"id": 567,
     * 				"datasource": "MTM_SIMULATED_RAND.csv",
     * 				"dataset": "MTM_SIMULATED",
     * 				"method": "Logistic Regression"
     * 			},
     * 			{	"users": ["test-role@@misd.isi.edu", "RAND-role@@misd.isi.edu"],
     * 				"study": "MTM",
     * 				"site": "RAND",
     * 				"library": ["OCEANS"],
     * 				"id": 568,
     * 				"datasource": "MTM_SIMULATED_RAND.csv",
     * 				"dataset": "MTM_SIMULATED",
     * 				"method":"Logistic Regression"
     * 			}] 
     * 
     */
    public String toWorker();

    /**
     * Returns a string representing a JSONArray with the parameters
     *   
     * Example: [	{	"description": "id - Query identifier",
     * 					"minOccurs": 1,
     * 					"library": ["GLORE","OCEANS"],
     * 					"method": "Logistic Regression",
     * 					"cname": "id",
     * 					"values": ["string"],
     * 					"maxOccurs": 1,
     * 					"path": "logisticRegressionInput|inputDescription",
     * 					"id": 552
     * 				},
     * 				{	"description": "name - Query name",
     * 					"minOccurs": 1,
     * 					"library": ["GLORE","OCEANS"],
     * 					"method": "Logistic Regression",
     * 					"cname": "name",
     * 					"values": ["string"],
     * 					"maxOccurs": 1,
     * 					"path": "logisticRegressionInput|inputDescription",
     * 					"id": 553
     * 				},
     * 				{	"description": "description - Query description",
     * 					"minOccurs": 0,
     * 					"library": ["GLORE","OCEANS"],
     * 					"method": "Logistic Regression",
     * 					"cname": "description",
     * 					"values": ["string"],
     * 					"maxOccurs": 1,
     * 					"path": "logisticRegressionInput|inputDescription",
     * 					"id": 554
     * 				},
     * 				{	"description": "dependentVariableName - Dependent variables",
     * 					"minOccurs": 1,
     * 					"library": ["OCEANS"],
     * 					"method": "Logistic Regression",
     * 					"cname": "dependentVariableName",
     * 					"values": null,
     * 					"maxOccurs": 1,
     * 					"path": "logisticRegressionInput|inputParameters",
     * 					"id": 555
     * 				},
     * 				{	"description": "dependentVariableName - Dependent variables",
     * 					"minOccurs": 1,
     * 					"library": ["GLORE"],
     * 					"method": "Logistic Regression",
     * 					"cname": "dependentVariableName",
     * 					"values": null,
     * 					"maxOccurs": 1,
     * 					"path": "logisticRegressionInput|inputParameters",
     * 					"id": 556
     * 				},
     * 				{	"description": "independentVariableName - Independent variables",
     * 					"minOccurs": 0,
     * 					"library": ["OCEANS"],
     * 					"method": "Logistic Regression",
     * 					"cname": "independentVariableName",
     * 					"values": null,
     * 					"maxOccurs": -1,
     * 					"path": "logisticRegressionInput|inputParameters",
     * 					"id": 557
     * 				},
     * 				{	"description": "independentVariableName - Independent variables",
     * 					"minOccurs": 0,
     * 					"library": ["GLORE"],
     * 					"method": "Logistic Regression",
     * 					"cname": "independentVariableName",
     * 					"values": null,
     * 					"maxOccurs": -1,
     * 					"path": "logisticRegressionInput|inputParameters",
     * 					"id": 558
     * 				}
     * 			] 
     * 
     */
    public String toParameter();

    /**
     * Returns a string representing a JSONArray with the sites
     *   
     * Example: [	{	"website": "http://www.usc.edu",
     * 					"title": "Administrator",
     * 					"agreement": "http://www.usc.edu/agreement",
     * 					"email": "jane@usc.edu",
     * 					"phone": "555-123-4570",
     * 					"cname": "USC",
     * 					"contact": "Jane Doe",
     * 					"address": "USC<br/>900 West 34th Street<br/>Los Angeles, CA 90033",
     * 					"rURL": "https://scanner-am.misd.isi.edu:8888/scanner",
     * 					"id": 560
     * 				},
     * 				{	"website": "http://www.ucsd.edu",
     * 					"title": "Administrator",
     * 					"agreement": "http://www.ucsd.edu/agreement",
     * 					"email": "jim@ucsd.edu",
     * 					"phone": "555-123-4581",
     * 					"cname": "UCSD",
     * 					"contact": "Jim Martinez",
     * 					"address": "UCSD<br/>9500 Gilman Dr. # 0533<br/>La Jolla, CA  92093-0533",
     * 					"rURL": "https://idash-scanner1-dev.ucsd.edu/scanner",
     * 					"id": 561
     * 				},
     * 				{	"website": "http://www.rand.edu",
     * 					"title": "Administrator",
     * 					"agreement": "http://www.rand.edu/agreement",
     * 					"email": "jim@rand.edu",
     * 					"phone": "555-123-4581",
     * 					"cname": "RAND",
     * 					"contact": "Jim Martinez",
     * 					"address": "RAND<br/>9500 Gilman Dr. # 0533<br/>Santa Monica, CA 92093-0533",
     * 					"rURL": "https://scanner-rand.misd.isi.edu:8888/scanner",
     * 					"id": 562
     * 				}
     * 			]
     * 
     */
    public String toSite();

    /**
     * Returns a string representing a JSONObject for the master
     *   
     * Example: {"id":559,"rURL":"https://master.scanner-net.org:9999/scanner"}
     * 
     */
    public String toMasterString();
    
    /**
     * Returns a string representing a JSONObject for the method
     *   
     * Example: {"id":551,"rpath":"lr","library":["GLORE","OCEANS"],"cname":"Logistic Regression"}
     * 
     */
    public String toMethodString();
    
    /**
     * Returns a string representing a JSONObject for the library
     *   
     * Example: {"id":549,"rpath":"oceans","cname":"OCEANS"}
     * 
     */
    public String toLibraryString();
    
    /**
     * Returns a string representing a JSONArray for the sites
     * 		- each array element is a string representing a JSONObject of the site having as keys the cname, rURL and id
     *   
     * Example: [	{"rURL":"https://scanner-am.misd.isi.edu:8888/scanner","cname":"USC","id":560},
     * 				{"rURL":"https://idash-scanner1-dev.ucsd.edu/scanner","cname":"UCSD","id":561},
     * 				{"rURL":"https://scanner-rand.misd.isi.edu:8888/scanner","cname":"RAND","id":562}
				]
     * 
     */
    public String toSiteString();
    

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
    
    /**
     * Returns a JSONObject representing the sites map having as keys the URL and as values the name
     *   
     * Example: {	"https://scanner-am.misd.isi.edu:8888/scanner":"USC",
     * 				"https://idash-scanner1-dev.ucsd.edu/scanner":"UCSD",
     * 				"https://scanner-rand.misd.isi.edu:8888/scanner":"RAND"
				}
     * 
     */
    public JSONObject toSitesMap();

}
