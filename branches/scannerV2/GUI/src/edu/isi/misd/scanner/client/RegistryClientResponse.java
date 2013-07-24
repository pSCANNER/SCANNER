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
 * Public interface for the registry client responses.
 * 
 * @author Serban Voinea
 *
 */
public interface RegistryClientResponse {

    /**
     * Gets the HTTP status code.
     * @return An integer with the HTTP status code.
     * 
     */
    public int getStatus();

    /**
     * Returns the response body as a string.
     * @return A string with the response body.
     * 
     */
    public String getEntityString();

    /**
     * Returns the error message of an HTTP Response.
     * @return A string with the error message of an HTTP Response.
     * 
     */
    public String getErrorMessage();
    
    /**
     * Releases the response resourses.
     * 
     */
    public void release();

    /**
     * Returns a string representing a JSONObject for the studies.
     * <ul>
     * 		<li> the keys are the studies names. </li>
     * 		<li> the values are the studies description. </li>
     *  </ul> 
     * Example: 
     * <br/>{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"MTM": "MTM - Medication Therapy Management"
     * <br/>}
     */
    public String toStudies();

    /**
     * Returns a string representing a JSONObject for the datasets.
     * <ul>
     * 		<li> the keys are the datasets names. </li>
     * 		<li> the values are the datasets description. </li>
     *  </ul> 
     * Example: 
     * <br/>{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"MTM_SIMULATED": "MTM_SIMULATED - Medication Therapy Management Simulated"
     * <br/>}
     * 
     */
    public String toDatasets();

    /**
     * Returns a string representing a JSONObject for the libraries.
     * <ul>
     * 		<li> the keys are the libraries names. </li>
     * 		<li> the values are the libraries description. </li>
     *  </ul> 
     * Example: 
     * <br/>{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"OCEANS": "OCEANS - Observational Cohort Event Analysis and Notification System",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"GLORE": "GLORE - Grid Binary LOgistic REgression"
     * <br/>}
     * 
     */
    public String toLibraries();

    /**
     * Returns a string representing a JSONObject for the methods.
     * <ul>
     * 		<li> the keys are the methods names. </li>
     * 		<li> the values are the methods description. </li>
     *  </ul> 
     * Example: 
     * <br/>{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"Logistic Regression": "Logistic Regression - Predicting the outcome of a categorical dependent variable"
     * <br/>}
     * 
     */
    public String toMethods();


    /**
     * Returns a string representing a JSONObject for the sites.
     * <ul>
     * 		<li> the keys are the sites names. </li>
     * 		<li> the values are the ids of their workers. </li>
     *  </ul> 
     * Example: 
     * <br/>
     * {	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"UCSD": "168",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"USC": "167",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"RAND": "169"
     * <br/>}
     * 
     */
    public String toSites();
    
    /**
     * Returns a string representing a JSONObject for the parameters.
     * <ul>
     * 		<li> the keys represent the parameter names and the values represent their description. </li>
     * 		<li> the "params" describes the structure of the input parameters . </li>
     * 		<ul>
     * 			<li> the dependentVariableName/independentVariableName contains a list with their values: </li>
     * 				<ul>
     * 				<li> the value is separated by the HTML line separator <br/> from its description. </li>
     *  			</ul> 
     *  	</ul> 
     *  </ul> 
     * Example: 
     * <br/>{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"description":
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": "id - Query identifier",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dependentVariableName": "dependentVariableName - Dependent variables",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"description": "description - Query description", 
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name": "name - Query name",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"independentVariableName": "independentVariableName - Independent variables"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * 
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"params": 
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"logisticRegressionInput":
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"inputParameters":
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dependentVariableName":
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"a1c baseline&lt;br/&gt;HbA1c at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"a1c final&lt;br/&gt;HbA1c at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"age&lt;br/&gt;Age",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"bmi baseline&lt;br/&gt;BMI at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"bmi final&lt;br/&gt;BMI at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"categorical_insurance&lt;br/&gt;Insurance",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"days between index date and final measure date&lt;br/&gt;Days between index date and final HbA1c Measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dibp baseline&lt;br/&gt;Diasolic Blood Pressure At Baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dibp final&lt;br/&gt;Diasolic Blood Pressure at final measurel",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"eth&lt;br/&gt;Ethnicity",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"group&lt;br/&gt;Received MTM",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"hdl baseline&lt;br/&gt;HDL at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"hdl final&lt;br/&gt;HDL at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"ldl baseline&lt;br/&gt;LDL at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"ldl final&lt;br/&gt;LDL at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"male&lt;br/&gt;Male Gender",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"number of visits&lt;br/&gt;Number of visits during study",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"outcome_a1c_final_lt_9&lt;br/&gt;Final HbA1c is less than 9",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"smoke&lt;br/&gt;Currently Smokes",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"sybp baseline&lt;br/&gt;Systolic Blood Pressure At Baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"sybp final&lt;br/&gt;Systolic Blood Pressure at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"totchol baseline&lt;br/&gt;Cholesterol at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"totchol final&lt;br/&gt;Total Cholesterol at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"trig baseline&lt;br/&gt;Triglicerides at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"trig final&lt;br/&gt;Triglicerides at final measaure"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;],
     * 
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"independentVariableName":
     * 	<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"a1c baseline&lt;br/&gt;HbA1c at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"a1c final&lt;br/&gt;HbA1c at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"age&lt;br/&gt;Age",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"bmi baseline&lt;br/&gt;BMI at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"bmi final&lt;br/&gt;BMI at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"categorical_insurance&lt;br/&gt;Insurance",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"days between index date and final measure date&lt;br/&gt;Days between index date and final HbA1c Measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dibp baseline&lt;br/&gt;Diasolic Blood Pressure At Baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dibp final&lt;br/&gt;Diasolic Blood Pressure at final measurel",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"eth&lt;br/&gt;Ethnicity",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"group&lt;br/&gt;Received MTM",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"hdl baseline&lt;br/&gt;HDL at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"hdl final&lt;br/&gt;HDL at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"ldl baseline&lt;br/&gt;LDL at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"ldl final&lt;br/&gt;LDL at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"male&lt;br/&gt;Male Gender",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"number of visits&lt;br/&gt;Number of visits during study",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"outcome_a1c_final_lt_9&lt;br/&gt;Final HbA1c is less than 9",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"smoke&lt;br/&gt;Currently Smokes",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"sybp baseline&lt;br/&gt;Systolic Blood Pressure At Baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"sybp final&lt;br/&gt;Systolic Blood Pressure at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"totchol baseline&lt;br/&gt;Cholesterol at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"totchol final&lt;br/&gt;Total Cholesterol at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"trig baseline&lt;br/&gt;Triglicerides at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"trig final&lt;br/&gt;Triglicerides at final measaure"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;]
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},
     * 
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"inputDescription":
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": ["string"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"description": ["string"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name": ["string"]
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;}
     * <br/>}
     */
    public String toParameters();

    /**
     * Returns a string representing a JSONArray for the workers.
     * <ul>
     * 		<li> each array element is a string representing a JSONObject of the worker. </li>
     *  </ul> 
     * Example:	
     * <br/>[
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"study":"MTM",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"site":"USC",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library":["OCEANS"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id":563,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"datasource":"MTM_SIMULATED_ALTAMED.csv",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dataset":"MTM_SIMULATED",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method":"Logistic Regression"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"study":"MTM",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"site":"UCSD",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library":["OCEANS"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id":565,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"datasource":"MTM_SIMULATED_UCSD.csv",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dataset":"MTM_SIMULATED",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method":"Logistic Regression"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"study":"MTM",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"site":"RAND",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library":["OCEANS"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id":568,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"datasource":"MTM_SIMULATED_RAND.csv",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dataset":"MTM_SIMULATED",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method":"Logistic Regression"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;}
     * <br/>]
     */
    public String toWorkers();

    /**
     * Returns a string representing a JSONArray for the contacts.
     * <ul>
     * 		<li> each array element is a string representing a JSONObject of a contact. </li>
     *  </ul> 
     * Example: 
     * <br/>[	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"website":"http://www.study1.org",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"title":"PI",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"approvals":"http://www.study1.org/approvals",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rtype":"study",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"agreement":null,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"phone":"555-123-4567",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"contact":"John Smith",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address":"Study1&lt;br/&gt;1-st Avenue&lt;br/&gt;New York, NY 10026",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"email":"john@study1.org"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"website":"http://www.scanner-net.org",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"title":"Project PI",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"approvals":null,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rtype":"master",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"agreement":null,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"phone":"858-822-4931",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"contact":"Prof. Lucila Ohno-Machado",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address":"UCSD&lt;br/&gt;Division of Biomedical Informatics&lt;br/&gt;9500 Gilman Dr. MC 0505&lt;br/&gt;La Jolla, CA 92093-0505",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"email":"scanner@ussd.edu"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"website":"http://www.usc.edu",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"title":"Administrator",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"approvals":null,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rtype":"site",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"agreement":"http://www.usc.edu/agreement",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"phone":"555-123-4570",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"contact":"Jane Doe",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address":"USC&lt;br/&gt;900 West 34th Street&lt;br/&gt;Los Angeles, CA 90033",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"email":"jane@usc.edu"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"website":"http://www.ucsd.edu",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"title":"Administrator",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"approvals":null,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rtype":"site",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"agreement":"http://www.ucsd.edu/agreement",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"phone":"555-123-4581",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"contact":"Jim Martinez",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address":"USC&lt;br/&gt;9500 GILMAN DR. # 0533&lt;br/&gt;LA JOLLA, CA  92093-0533",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"email":"jim@ucsd.edu"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"website":"http://www.usc.edu",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"title":"Administrator",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"approvals":null,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rtype":"site",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"agreement":"http://www.rand.org/agreement",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"phone":"555-123-4550",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"contact":"Dan Wu",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address":"RAND&lt;br/&gt;1776 Main St&lt;br/&gt;Santa Monica, CA 90401",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"email":"dan@rand.edu"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;}
     * <br/>]
     * 
     */
    public String toContacts();

    /**
     * Returns a string representing a JSONArray with the studies.
     *   
     * <br/>Example: 
     * <br/>[
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"website":"http://www.MTM.org",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"description":"MTM - Medication Therapy Management",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"title":"PI",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"approvals":"http://www.MTM.org/approvals",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"email":"john@MTM.org",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname":"MTM",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"contact":"John Smith",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address":"MTM&lt;br/&gt;1-st Avenue&lt;br/&gt;New York, NY 10026",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id":547
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;}
     * <br/>]
    */
    public String toStudy();

    /**
     * Returns a string representing a JSONArray with the datasets.
     *   
     * <br/>Example: 
     * <br/>[
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"variables":
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"totchol final&lt;br/&gt;Total Cholesterol at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"eth&lt;br/&gt;Ethnicity",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"trig baseline&lt;br/&gt;Triglicerides at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"sybp baseline&lt;br/&gt;Systolic Blood Pressure At Baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"a1c final&lt;br/&gt;HbA1c at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"smoke&lt;br/&gt;Currently Smokes",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"number of visits&lt;br/&gt;Number of visits during study",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"group&lt;br/&gt;Received MTM",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dibp baseline&lt;br/&gt;Diasolic Blood Pressure At Baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"ldl final&lt;br/&gt;LDL at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"a1c baseline&lt;br/&gt;HbA1c at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"age&lt;br/&gt;Age",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"sybp final&lt;br/&gt;Systolic Blood Pressure at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"categorical_insurance&lt;br/&gt;Insurance",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"male&lt;br/&gt;Male Gender",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"ldl baseline&lt;br/&gt;LDL at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dibp final&lt;br/&gt;Diasolic Blood Pressure at final measurel",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"bmi baseline&lt;br/&gt;BMI at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"trig final&lt;br/&gt;Triglicerides at final measaure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"hdl final&lt;br/&gt;HDL at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"days between index date and final measure date&lt;br/&gt;Days between index date and final HbA1c Measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"bmi final&lt;br/&gt;BMI at final measure",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"hdl baseline&lt;br/&gt;HDL at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"totchol baseline&lt;br/&gt;Cholesterol at baseline",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"outcome_a1c_final_lt_9&lt;br/&gt;Final HbA1c is less than 9"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"study":"MTM",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname":"MTM_SIMULATED",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id":548,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"description":"MTM_SIMULATED - Medication Therapy Management Simulated"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;}
     * <br/>] 
     */
    public String toDataset();

    /**
     * Returns a string representing a JSONArray with the libraries.
     *   
     * <br/>Example: 
     * <br/>[	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rpath":"oceans",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname":"OCEANS",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id":549,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"description":"OCEANS - Observational Cohort Event Analysis and Notification System"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rpath":"glore",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname":"GLORE",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id":550,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"description":"GLORE - Grid Binary LOgistic REgression"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;}
     * <br/>]
     */
    public String toLibrary();

    /**
     * Returns a string representing a JSONArray with the methods.
     *   
     * <br/>Example: 
     * <br/>[
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rpath":"lr",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname":"Logistic Regression",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id":551,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library":["GLORE","OCEANS"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"description":"Logistic Regression - Predicting the outcome of a categorical dependent variable"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;}
     * <br/>]
     */
    public String toMethod();

    /**
     * Returns a string representing a JSONArray with the master.
     *   
     * <br/>Example: 
     * <br/>[
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"website": "http://scanner.ucsd.edu/",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"title": "Project PI",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 559,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"phone": "858-822-4931",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"contact": "Prof. Lucila Ohno-Machado",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address": "UCSD&lt;br/&gt;Division of Biomedical Informatics&lt;br/&gt;9500 Gilman Dr. MC 0505&lt;br/&gt;La Jolla, CA 92093-0505",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rURL": "https://master.scanner-net.org:9999/scanner",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"email": "sdsc-scanner@ucsd.edu"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;}
     * <br/>] 
     */
    public String toMaster();

    /**
     * Returns a string representing a JSONArray with the workers.
     *   
     * <br/>Example: 
     * <br/>[
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"users": ["test-role@@misd.isi.edu"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"study": "MTM",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"site": "USC",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library": ["OCEANS"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 563,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"datasource": "MTM_SIMULATED_ALTAMED.csv",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dataset": "MTM_SIMULATED",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method": "Logistic Regression"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"users": ["test-role@@misd.isi.edu"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"study": "MTM",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"site": "USC",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library": ["GLORE"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 564,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"datasource": "MTM_SIMULATED_ALTAMED.csv",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dataset": "MTM_SIMULATED",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method": "Logistic Regression"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"users": ["test-role@@misd.isi.edu"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"study": "MTM",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"site": "UCSD",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library": ["OCEANS"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 565,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"datasource": "MTM_SIMULATED_UCSD.csv",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dataset": "MTM_SIMULATED",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method": Logistic Regression"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"users": ["test-role@@misd.isi.edu"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"study": "MTM",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"site": "UCSD",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library": ["GLORE"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 566,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"datasource": "MTM_SIMULATED_UCSD.csv",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dataset": "MTM_SIMULATED",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method":"Logistic Regression"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"users": ["test-role@@misd.isi.edu", "RAND-role@@misd.isi.edu"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"study": "MTM",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"site": "RAND",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library": ["GLORE"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 567,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"datasource": "MTM_SIMULATED_RAND.csv",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dataset": "MTM_SIMULATED",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method": "Logistic Regression"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"users": ["test-role@@misd.isi.edu", "RAND-role@@misd.isi.edu"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"study": "MTM",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"site": "RAND",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library": ["OCEANS"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 568,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"datasource": "MTM_SIMULATED_RAND.csv",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dataset": "MTM_SIMULATED",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method":"Logistic Regression"
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;}
     * <br/>] 
     */
    public String toWorker();

    /**
     * Returns a string representing a JSONArray with the parameters.
     *   
     * <br/>Example: 
     * <br/>[	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"description": "id - Query identifier",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"minOccurs": 1,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library": ["GLORE","OCEANS"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method": "Logistic Regression",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname": "id",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"values": ["string"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"maxOccurs": 1,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"path": "logisticRegressionInput|inputDescription",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 552
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"description": "name - Query name",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"minOccurs": 1,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library": ["GLORE","OCEANS"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method": "Logistic Regression",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname": "name",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"values": ["string"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"maxOccurs": 1,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"path": "logisticRegressionInput|inputDescription",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 553
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"description": "description - Query description",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"minOccurs": 0,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library": ["GLORE","OCEANS"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method": "Logistic Regression",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname": "description",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"values": ["string"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"maxOccurs": 1,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"path": "logisticRegressionInput|inputDescription",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 554
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"description": "dependentVariableName - Dependent variables",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"minOccurs": 1,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library": ["OCEANS"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method": "Logistic Regression",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname": "dependentVariableName",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"values": null,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"maxOccurs": 1,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"path": "logisticRegressionInput|inputParameters",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 555
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"description": "dependentVariableName - Dependent variables",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"minOccurs": 1,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library": ["GLORE"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method": "Logistic Regression",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname": "dependentVariableName",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"values": null,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"maxOccurs": 1,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"path": "logisticRegressionInput|inputParameters",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 556
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"description": "independentVariableName - Independent variables",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"minOccurs": 0,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library": ["OCEANS"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method": "Logistic Regression",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname": "independentVariableName",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"values": null,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"maxOccurs": -1,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"path": "logisticRegressionInput|inputParameters",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 557
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"description": "independentVariableName - Independent variables",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"minOccurs": 0,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"library": ["GLORE"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"method": "Logistic Regression",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname": "independentVariableName",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"values": null,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"maxOccurs": -1,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"path": "logisticRegressionInput|inputParameters",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 558
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;}
     * <br/>] 
     * 
     */
    public String toParameter();

    /**
     * Returns a string representing a JSONArray with the sites.
     *   
     * <br/>Example: 
     * <br/>[	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"website": "http://www.usc.edu",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"title": "Administrator",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"agreement": "http://www.usc.edu/agreement",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"email": "jane@usc.edu",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"phone": "555-123-4570",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname": "USC",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"contact": "Jane Doe",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address": "USC&lt;br/&gt;900 West 34th Street&lt;br/&gt;Los Angeles, CA 90033",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rURL": "https://scanner-am.misd.isi.edu:8888/scanner",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 560
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"website": "http://www.ucsd.edu",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"title": "Administrator",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"agreement": "http://www.ucsd.edu/agreement",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"email": "jim@ucsd.edu",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"phone": "555-123-4581",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname": "UCSD",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"contact": "Jim Martinez",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address": "UCSD&lt;br/&gt;9500 Gilman Dr. # 0533&lt;br/&gt;La Jolla, CA  92093-0533",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rURL": "https://idash-scanner1-dev.ucsd.edu/scanner",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 561
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"website": "http://www.rand.edu",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"title": "Administrator",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"agreement": "http://www.rand.edu/agreement",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"email": "jim@rand.edu",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"phone": "555-123-4581",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname": "RAND",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"contact": "Jim Martinez",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address": "RAND&lt;br/&gt;9500 Gilman Dr. # 0533&lt;br/&gt;Santa Monica, CA 92093-0533",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rURL": "https://scanner-rand.misd.isi.edu:8888/scanner",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id": 562
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;}
     * <br/>]
     * 
     */
    public String toSite();

    /**
     * Returns a string representing a JSONObject for the master.
     *   
     * <br/>Example: 
     * <br/>{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"id":559,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"rURL":"https://master.scanner-net.org:9999/scanner"
     * <br/>}
     * 
     */
    public String toMasterString();
    
    /**
     * Returns a string representing a JSONObject for the method.
     *   
     * <br/>Example: 
     * <br/>{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"id":551,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"rpath":"lr","library":["GLORE","OCEANS"],
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"cname":"Logistic Regression"
     * <br/>}
     */
    public String toMethodString();
    
    /**
     * Returns a string representing a JSONObject for the library.
     *   
     * <br/>Example: 
     * <br/>{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"id":549,
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"rpath":"oceans",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"cname":"OCEANS"
     * <br/>}
     * 
     */
    public String toLibraryString();
    
    /**
     * Returns a string representing a JSONArray for the sites.
     * <ul>
     * 		<li> each array element is a string representing a JSONObject of the site having as keys the cname, rURL and id.</li>
     * </ul>
     * <br/>Example: 
     * <br/>[	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rURL":"https://scanner-am.misd.isi.edu:8888/scanner",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname":"USC",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id":560
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rURL":"https://idash-scanner1-dev.ucsd.edu/scanner",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname":"UCSD",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id":561
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;},
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;{
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"rURL":"https://scanner-rand.misd.isi.edu:8888/scanner",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"cname":"RAND",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id":562
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;}
     * <br/>]
     */
    public String toSiteString();
    

    /**
     * Returns the unique id of the resource in the registry.
     * The method will be called for a RegistryClientResponse from one of the following requests:
     * <ul>
     * 		<li> getStudy(String name)</li>
     * 		<li> getDataset(String name, String study)</li>
     * 		<li> getLibrary(String name)</li>
     * 		<li> getMethod(String name, String lib)</li>
     * 		<li> getMaster()</li>
     * 		<li> getParameter(String name, String func, String lib)</li>
     * 		<li> getWorker(String study, String dataset, String lib, String func, String site)</li>
     * </ul>
     */
    public String getResourceId();
    
    /**
     * Returns a JSONObject representing the sites map having as keys the URL and as values the name.
     *   
     * <br/>Example: 
     * <br/>{	
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"https://scanner-am.misd.isi.edu:8888/scanner":"USC",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"https://idash-scanner1-dev.ucsd.edu/scanner":"UCSD",
     * <br/>&nbsp;&nbsp;&nbsp;&nbsp;"https://scanner-rand.misd.isi.edu:8888/scanner":"RAND"
     * <br/>}
     */
    public JSONObject toSitesMap();

}
