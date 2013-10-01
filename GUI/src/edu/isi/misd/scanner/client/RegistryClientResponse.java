package edu.isi.misd.scanner.client;

import java.util.List;

import org.json.JSONArray;
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
    public String toSites(String dataset);
    
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
    public String toMethodString(String func, String lib);
    
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
    public String toSiteString(List<String> sites, String dataset);
    
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
    
    public JSONObject getEntity();
    public JSONArray getEntityResponse();

    public JSONArray toUsers();
    public JSONArray toNodes();
    public JSONArray toTools();
    public JSONArray toDatasetInstances();
    public JSONArray toDatasetDefinitions();
    public JSONArray toUserRoles();
    public JSONArray toStudyPolicies();
    public JSONArray toStudyRoles();
}
