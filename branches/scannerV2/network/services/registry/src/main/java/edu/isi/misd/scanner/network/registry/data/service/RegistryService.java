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
     * Checks that the user is authorized to view a Study
     * @param studyId
     * @param userName
     */    
    public boolean userCanViewStudy(Integer studyId, String userName);
    
    /**
     * Checks that the user is authorized to manage a Study
     * @param studyId
     * @param userName
     */    
    public boolean userCanManageStudy(Integer studyId, String userName);
    
    /**
     * Checks if the user is a superuser
     * @param userName
     */    
    public boolean userIsSuperuser(String userName);
    
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
     * Checks that the user is authorized to manage a Site
     * @param siteId
     * @param userName
     */    
    public boolean userCanManageSite(Integer siteId, String userName);
    
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
