package edu.isi.misd.scanner.network.registry.data.service;

import edu.isi.misd.scanner.network.registry.data.domain.DataSetDefinition;
import edu.isi.misd.scanner.network.registry.data.domain.ScannerUser;
import edu.isi.misd.scanner.network.registry.data.domain.Site;
import edu.isi.misd.scanner.network.registry.data.domain.Study;
import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;

/**
 *
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
}
