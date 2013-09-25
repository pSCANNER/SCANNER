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
	public RegistryClientResponse getDatasets(int studyId);
	
    /**
     * Gets the libraries of a dataset. 
     * 
     * @param study
     *            the name of the study the library belongs to.
     * @param dataset
     *            the name of the dataset the library belongs to.
     * @return The client response.
     */
	public RegistryClientResponse getLibraries(int studyId, int dataSetId, String sites);
	
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
	public RegistryClientResponse getMethods(int studyId, int dataSetId, int libraryId);
	
    /**
     * Gets the parameters of a method. 
     * 
     * @param func
     *            the name of the method.
     * @param lib
     *            the name of the library.
     * @return The client response.
     */
	public RegistryClientResponse getParameters(int dataSetId, String jsonFile);
	
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
	public RegistryClientResponse getSites(int dataSetId);
	
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
	public RegistryClientResponse getMasterObject();
	
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
	
    /**
     * Gets the studies. 
     * 
     * @return The client response.
     */
	public RegistryClientResponse getAllSitesPolicies();
	
    /**
     * Gets the studies. 
     * 
     * @return The client response.
     */
	

	public RegistryClientResponse getAllStudyRequestedSites();
	public RegistryClientResponse getUsers();
	public RegistryClientResponse getNodes();
	public RegistryClientResponse getTools();
	public RegistryClientResponse getDatasetInstances();
	public RegistryClientResponse getDatasetDefinitions();
	public RegistryClientResponse getUser(String user);
	public RegistryClientResponse getAnalysisPolicies(String userName, int toolId, int datasetInstanceId);
	public RegistryClientResponse getAnalysisPolicies();
	public RegistryClientResponse getUserRoles();
	public RegistryClientResponse getUserRoles(int studyId);
	public RegistryClientResponse getUserRoles(String userName);
	public RegistryClientResponse getStudyPolicies();
	public RegistryClientResponse getStudyRoles();
	public RegistryClientResponse getStudyRoles(int studyId);
	
	public RegistryClientResponse createStudyRequestedSites(Integer studyId, Integer siteId);
	public RegistryClientResponse deleteStudyRequestedSites(Integer studyRequestedSiteId);
	public RegistryClientResponse getStudyRequestedSites(Integer studyId);
	
	public RegistryClientResponse createUserRole(Integer userId, Integer roleId);
	public RegistryClientResponse deleteUserRole(Integer userRoleId);
	
	public RegistryClientResponse createStudyPolicy(int roleId, int studyId, int dataSetDefinitionId, int toolId, int accessModeId);
	public RegistryClientResponse deleteStudyPolicy(int studyPolicyStatementId);
	
	public RegistryClientResponse createSitePolicy(int roleId, int dataSetInstanceId, int studyPolicyStatementId, int toolId, int accessModeId);
	public RegistryClientResponse deleteAnalyzePolicy(int analysisPolicyStatementId);
	
	public RegistryClientResponse createDatasetInstance(String dataSetInstanceName, String description, String dataSource, int dataSetDefinitionId, int nodeId);
	public RegistryClientResponse updateDatasetInstance(int dataSetInstanceId, String dataSetInstanceName, String description, String dataSource, int dataSetDefinitionId, int nodeId);
	public RegistryClientResponse deleteInstance(int dataSetInstanceId);

	public RegistryClientResponse createUser(String userName, String email, String firstName, String lastName, String phone, boolean isSuperuser);
	public RegistryClientResponse updateUser(int userId, String userName, String email, String firstName, String lastName, String phone, boolean isSuperuser);
	public RegistryClientResponse deleteUser(int userId);
	
	public RegistryClientResponse createSite(String siteName, String description);
	public RegistryClientResponse updateSite(int siteId, String siteName, String description);
	public RegistryClientResponse deleteSite(int siteId);
	
	public RegistryClientResponse createNode(String nodeName, String hostUrl, int hostPort, String basePath, String description, boolean isMaster, int siteId);
	public RegistryClientResponse updateNode(int nodeId, String nodeName, String hostUrl, int hostPort, String basePath, String description, boolean isMaster, int siteId);
	public RegistryClientResponse deleteNode(int nodeId);

	public RegistryClientResponse getStudyManagementPolicies(int studyId, String userName);
}
