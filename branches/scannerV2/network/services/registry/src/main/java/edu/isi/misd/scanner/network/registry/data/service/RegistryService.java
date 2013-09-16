package edu.isi.misd.scanner.network.registry.data.service;

import edu.isi.misd.scanner.network.registry.data.domain.DataSetDefinition;
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
