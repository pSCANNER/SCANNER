/*  
 * Copyright 2013 University of Southern California 
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
package edu.isi.misd.scanner.network.registry.data.service; 

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisInstance;
import edu.isi.misd.scanner.network.registry.data.domain.DataSetDefinition;
import edu.isi.misd.scanner.network.registry.data.domain.ScannerUser;
import edu.isi.misd.scanner.network.registry.data.domain.Site;
import edu.isi.misd.scanner.network.registry.data.domain.Study;
import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;

/**
 * Registry Service interface.
 * 
 * @author Mike D'Arcy 
 */
public interface RegistryService 
{
    /**
     * Checks if the user is a superuser
     * @param userName
     */    
    public boolean userIsSuperuser(String userName);
    
     /**
     * Checks that the user is authorized to manage a Study
     * @param userName
     * @param studyId
     */    
    public boolean userCanManageStudy(String userName, Integer studyId);
    
    /**
     * Checks that the user is authorized to manage a Site
     * @param userName
     * @param siteId
     */    
    public boolean userCanManageSite(String userName, Integer siteId);

    /**
     * Checks that the user is authorized to manage a Node
     * @param userName
     * @param nodeId
     */    
    public boolean userCanManageNode(String userName, Integer nodeId);
    
    /**
     * Checks that the user is authorized to manage a DataSetInstance
     * @param userName
     * @param dataSetInstanceId
     */    
    public boolean userCanManageDataSetInstance(
        String userName, Integer dataSetInstanceId);
    
    /**
     * Creates a single Study.
     * @param Study
     */
    public Study createStudy(Study study); 
    
    /**
     * Updates a single Study.
     * @param studyId
     */    
    public void updateStudy(Study study); 
        
    /**
     * Deletes a single Study by studyId.
     * @param studyId
     */
    public void deleteStudy(Integer studyId);
    
    /**
     * Creates a single Site.
     * @param site
     */
    public Site createSite(Site site);
    
    /**
     * Creates a single Site, assigning a user as the default site administrator.
     * @param site
     * @param siteAdmin the default Site Administrator
     */
    public Site createSite(Site site, ScannerUser siteAdmin);   
    
    /**
     * Deletes a single Site.
     * @param site
     */
    public void deleteSite(Site site);
    
    /**
     * Creates or updates a single ToolLibrary.
     * @param library
     */
    public ToolLibrary saveToolLibrary(ToolLibrary library);
    
    /**
     * Creates or updates a single DataSetDefinition.
     * @param dataSet
     */
    public DataSetDefinition saveDataSetDefinition(DataSetDefinition dataSet);    
    
    /**
     * Creates or updates a single AnalysisInstance.
     * @param instance
     */
    public AnalysisInstance saveAnalysisInstance(AnalysisInstance instance);  
    
    /**
     * Converts a Data Processing Specification into an executable script (e.g., SQL).
     */
    public String convertDataProcessingSpecification(String workingDir,
                                                     String execName,
                                                     String args,
                                                     String input,
                                                     Integer dataSetId)        
        throws Exception;       
}
