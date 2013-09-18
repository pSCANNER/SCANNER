package edu.isi.misd.scanner.network.registry.data.service;

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisTool;
import edu.isi.misd.scanner.network.registry.data.domain.DataSetDefinition;
import edu.isi.misd.scanner.network.registry.data.domain.DataSetInstance;
import edu.isi.misd.scanner.network.registry.data.domain.ScannerUser;
import edu.isi.misd.scanner.network.registry.data.domain.StandardRole;
import edu.isi.misd.scanner.network.registry.data.domain.Study;
import edu.isi.misd.scanner.network.registry.data.domain.StudyManagementPolicy;
import edu.isi.misd.scanner.network.registry.data.domain.StudyRole;
import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import edu.isi.misd.scanner.network.registry.data.domain.UserRole;
import edu.isi.misd.scanner.network.registry.data.repository.*;
import edu.isi.misd.scanner.network.registry.web.errors.ForbiddenException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Service("registryService")
public class RegistryServiceImpl implements RegistryService 
{

    @Autowired
    private DataSetDefinitionRepository dataSetDefinitionRepository;
    @Autowired
    private ToolLibraryRepository toolLibraryRepository;   
    @Autowired
    private StudyRepository studyRepository;   
    @Autowired
    private StudyRoleRepository studyRoleRepository;   
    @Autowired
    private StudyManagementPolicyRepository studyManagementPolicyRepository;   
    @Autowired
    private UserRoleRepository userRoleRepository;   
    @Autowired
    private StandardRoleRepository standardRoleRoleRepository;   
    @Autowired
    private ScannerUserRepository scannerUserRepository;       
    /**
     * Creates or updates a single ToolLibrary, with optional creation of 
     * AnalysisTool child relations.
     * @param library
     */    
    @Override
    @Transactional
    public ToolLibrary saveToolLibrary(ToolLibrary library) 
    {
        List<AnalysisTool> toolList = library.getAnalysisTools();
        if ((toolList != null) && (!toolList.isEmpty())) {
            library.setAnalysisTools(null);
            toolLibraryRepository.save(library);              
            for (AnalysisTool tool : toolList) {
                tool.setToolParentLibrary(library);
            }
            library.setAnalysisTools(toolList);            
        }
        return toolLibraryRepository.save(library);
    } 
    
    /**
     * Creates or updates a single DataSetDefinition, with optional creation of
     * DataSetInstance child relations.
     * @param dataSet
     */    
    @Override
    @Transactional
    public DataSetDefinition saveDataSetDefinition(DataSetDefinition dataSet) 
    {
        Set<DataSetInstance> dataSetInstances = dataSet.getDataSetInstances();
        if ((dataSetInstances != null) && (!dataSetInstances.isEmpty())) {
            dataSet.setDataSetInstances(dataSetInstances);
            dataSetDefinitionRepository.save(dataSet);              
            for (DataSetInstance instance : dataSetInstances) {
                instance.setDataSetDefinition(dataSet);
            }
            dataSet.setDataSetInstances(dataSetInstances);            
        }
        return dataSetDefinitionRepository.save(dataSet);
    }     

    /**
     * Creates a single Study and creates default StudyRoles for the Study based
     * on the existing StandardRoles.  Also assigns the Study creator to 
     * the created StudyRoles, and adds flagged StandardRoles to the 
     * StudyManagementPolicy table.
     * @param study
     */    
    @Override
    @Transactional    
    public Study createStudy(Study study) 
    {
        // default role assignment lists based on StandardRole configuration
        ArrayList<StudyRole> defaultStudyManagementRoles = 
            new ArrayList<StudyRole>();
        ArrayList<StudyRole> defaultStudyRolesToUserRoles = 
            new ArrayList<StudyRole>();  
        
        /**
         *   1. Create the Study
         */
        Study createdStudy = studyRepository.save(study);
        
        /**
         *   2. Create the default StudyRoles based on the predefined 
         *   StandardRoles.  Also store (in a separate list) any StandardRoles 
         *   that have AddToStudyPolicyByDefault set, as they will then be 
         *   saved later to the StudyManagementPolicy table.  Same applies to
         *   default UserRole creation based on getAddToUserRoleByDefault.
         */
        ArrayList<StudyRole> studyRoles = new ArrayList<StudyRole>();        
        for (StandardRole standardRole : standardRoleRoleRepository.findAll())
        {
            StudyRole studyRole = new StudyRole();
            studyRole.setRoleWithinStudy(standardRole.getStandardRoleName());
            studyRole.setStudy(createdStudy);
            studyRoles.add(studyRole);
            if (standardRole.getAddToStudyPolicyByDefault()) {
                defaultStudyManagementRoles.add(studyRole);
            }
            if (standardRole.getAddToUserRoleByDefault()) {
                defaultStudyRolesToUserRoles.add(studyRole);
            }
        }
        studyRoleRepository.save(studyRoles);        
        
        /**
         *   3. Add any of the flagged StudyManagement default roles to the 
         *   StudyManagementPolicy table
         */
        ArrayList<StudyManagementPolicy> studyManagementPolicies = 
            new ArrayList<StudyManagementPolicy>();
        for (StudyRole studyManagementRole : defaultStudyManagementRoles) 
        {
            StudyManagementPolicy studyManagementPolicy = 
                new StudyManagementPolicy();
            studyManagementPolicy.setStudyRoleId(studyManagementRole);
            studyManagementPolicy.setStudy(createdStudy);
            studyManagementPolicies.add(studyManagementPolicy);
        }
        studyManagementPolicyRepository.save(studyManagementPolicies);
        
        /**
         *   4. Add UserRole-to-StudyRole mappings for the Study creator for the
         *   newly created StudyRoles
         */
        ScannerUser owner = createdStudy.getStudyOwner();
        ArrayList<UserRole> userRoles = new ArrayList<UserRole>();
        for (StudyRole studyRole : defaultStudyRolesToUserRoles) 
        {
            UserRole userRole = new UserRole();
            userRole.setUser(owner);
            userRole.setStudyRole(studyRole);
            userRoles.add(userRole);
        }
        userRoleRepository.save(userRoles);
        
        return createdStudy;
    }

    /**
     * Updates a single Study, altering UserRole references (where required).
     * @param study
     */    
    @Override
    @Transactional        
    public void updateStudy(Study study) 
    {
        ArrayList<StudyRole> defaultStudyRolesToUserRoles = 
            new ArrayList<StudyRole>();          
        List<StudyRole> studyRoles = 
            studyRoleRepository.findByStudyStudyId(study.getStudyId());
        /*
         * Find the set of default study roles that need to have corresponding 
         * user roles automatically created.
         */
        for (StandardRole standardRole : standardRoleRoleRepository.findAll())
        {
            if (standardRole.getAddToUserRoleByDefault()) {
                for (StudyRole studyRole : studyRoles) {
                    if (studyRole.getRoleWithinStudy().equalsIgnoreCase(
                        standardRole.getStandardRoleName()))
                        defaultStudyRolesToUserRoles.add(studyRole);
                }
            }
        }    
        /*
         * Create default user roles for the (potentially) new owner of the 
         * study, if those roles do not exist already.  If the owner has not 
         * changed, then the roles will exist already and no UserRole update 
         * will take place.
         */
        ArrayList<UserRole> newUserRoles = new ArrayList<UserRole>();            
        List<StudyRole> studyRolesForUser = 
            studyRoleRepository.findByStudyStudyIdAndScannerUsersUserName(
                study.getStudyId(), study.getStudyOwner().getUserName());
        for (StudyRole studyRole : defaultStudyRolesToUserRoles) 
        {                
            if (studyRolesForUser.contains(studyRole)) {
                continue;
            }
            UserRole userRole = new UserRole();
            userRole.setUser(study.getStudyOwner());
            userRole.setStudyRole(studyRole);
            newUserRoles.add(userRole);
        }
        if (!newUserRoles.isEmpty()) {
            userRoleRepository.save(newUserRoles);
        }
        studyRepository.save(study);
    }
    
    /**
     * Deletes a single Study by Id, deleting references first (where 
     * explicity required).
     * @param studyId
     */    
    @Override
    @Transactional        
    public void deleteStudy(Integer studyId) 
    {
        List<UserRole> userRoles = 
            userRoleRepository.findByStudyRoleStudyStudyId(studyId);
        userRoleRepository.delete(userRoles);
        
        studyRepository.delete(studyId);
    }

    /**
     * Checks if the userName specified has the superuser flag set.
     * @param userName
     * @return true if the user is a superuser, false otherwise
     * @exception ForbiddenException if the userName is unknown (does not exist)
     */      
    @Override
    public boolean userIsSuperuser(String userName) 
        throws ForbiddenException
    {
        ScannerUser user = 
            scannerUserRepository.findByUserName(userName);
        if (user == null) {
            throw new ForbiddenException(
                userName,RegistryServiceConstants.MSG_UNKNOWN_USER_NAME);
        }
        return user.getIsSuperuser();
    }
    
    /**
     * Checks if the userName specified can view the studyId specified.
     * @param studyId
     * @param userName
     * @return true if the user can view the study, false otherwise
     * @exception ForbiddenException if the userName is unknown (does not exist)
     */      
    @Override
    public boolean userCanViewStudy(Integer studyId, String userName) 
    {
        // maybe this is too permissive and should be checked independently
        if (userIsSuperuser(userName)) {
            return true;
        }
        
        // check that the current user has the proper role for this operation
        // if so, one or more StudyRoles will be returned.
        List<StudyRole> studyRoles = 
            studyRoleRepository.findByStudyStudyIdAndScannerUsersUserName(
                studyId, userName);
        
        if (studyRoles.isEmpty()) {
            return false;
        }        
        return true;
    }
    
    /**
     * Checks if the userName specified can manage the studyId specified.
     * @param studyId
     * @param userName
     * @return true if the user can manage the study, false otherwise
     * @exception ForbiddenException if the userName is unknown (does not exist)
     */      
    @Override    
    public boolean userCanManageStudy(Integer studyId, String userName)
        throws ForbiddenException
    {
        // maybe this is too permissive and should be checked independently
        if (userIsSuperuser(userName)) {
            return true;
        }
        
        // check that the current user has the proper role for this operation
        // if so, one or more StudyManagementPolicies will be returned.
        List<StudyManagementPolicy> studyManagementPolicies = 
            studyManagementPolicyRepository.
                findByStudyStudyIdAndStudyRoleScannerUsersUserName(
                    studyId, userName);
        
        if (studyManagementPolicies.isEmpty()) {
            return false;
        }        
        return true;
    }      
}
