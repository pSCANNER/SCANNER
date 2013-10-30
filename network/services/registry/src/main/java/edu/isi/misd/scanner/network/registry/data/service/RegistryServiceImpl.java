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
import edu.isi.misd.scanner.network.registry.data.domain.AnalysisResult;
import edu.isi.misd.scanner.network.registry.data.domain.AnalysisTool;
import edu.isi.misd.scanner.network.registry.data.domain.DataSetDefinition;
import edu.isi.misd.scanner.network.registry.data.domain.DataSetInstance;
import edu.isi.misd.scanner.network.registry.data.domain.ScannerUser;
import edu.isi.misd.scanner.network.registry.data.domain.Site;
import edu.isi.misd.scanner.network.registry.data.domain.SitePolicy;
import edu.isi.misd.scanner.network.registry.data.domain.StandardRole;
import edu.isi.misd.scanner.network.registry.data.domain.Study;
import edu.isi.misd.scanner.network.registry.data.domain.StudyManagementPolicy;
import edu.isi.misd.scanner.network.registry.data.domain.StudyRole;
import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import edu.isi.misd.scanner.network.registry.data.domain.UserRole;
import edu.isi.misd.scanner.network.registry.data.repository.*;
import edu.isi.misd.scanner.network.registry.web.errors.ForbiddenException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

/**
 *  Registry Service implementation.
 * 
 *  @author Mike D'Arcy 
 */
@Service("registryService")
public class RegistryServiceImpl implements RegistryService 
{
    public static final String ROOT_STUDY_NAME = "SCANNER";
    
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
    private StandardRoleRepository standardRoleRepository;   
    @Autowired
    private ScannerUserRepository scannerUserRepository; 
    @Autowired
    private SiteRepository siteRepository;   
    @Autowired
    private SitePolicyRepository sitePolicyRepository;   
    @Autowired
    private AnalysisInstanceRepository analysisInstanceRepository;        
    
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
     * Checks if the userName specified can manage the studyId specified.
     * @param userName
     * @param studyId
     * @return true if the user can manage the study, false otherwise
     * @exception ForbiddenException if the userName is unknown (does not exist)
     */      
    @Override    
    public boolean userCanManageStudy(String userName, Integer studyId)
        throws ForbiddenException
    {
        if (userIsSuperuser(userName)) {
            return true;
        }
        Integer count = 
            userRoleRepository.countByUserUserNameAndStudyRoleStudyStudyId(
                userName,studyId);
        return (count > 0) ? true : false;       
    }  
    
    /**
     * Checks if the userName specified can manage the siteId specified.
     * @param userName
     * @param siteId
     * @return true if the user can manage the site, false otherwise
     * @exception ForbiddenException if the userName is unknown (does not exist)
     */      
    @Override    
    public boolean userCanManageSite(String userName, Integer siteId)
        throws ForbiddenException
    {
        if (userIsSuperuser(userName)) {
            return true;
        }        
        Integer count = 
            userRoleRepository.
                countByUserUserNameAndStudyRoleSitePoliciesSiteSiteId(
                    userName,siteId);
        return (count > 0) ? true : false;  
    }
    
    /**
     * Checks if the userName specified can manage the nodeId specified.
     * @param userName
     * @param nodeId
     * @return true if the user can manage the node, false otherwise
     * @exception ForbiddenException if the userName is unknown (does not exist)
     */         
    @Override
    public boolean userCanManageNode(String userName, Integer nodeId)
        throws ForbiddenException 
    {
        if (userIsSuperuser(userName)) {
            return true;
        }        
        Integer count = 
            userRoleRepository.
                countByUserUserNameAndStudyRoleSitePoliciesSiteNodesNodeId(
                    userName,nodeId);
        return (count > 0) ? true : false;          
    }
    
    /**
     * Checks if the userName specified can manage the dataSetInstanceId specified.
     * @param userName
     * @param dataSetInstanceId
     * @return true if the user can manage the dataSetInstance, false otherwise
     * @exception ForbiddenException if the userName is unknown (does not exist)
     */     
    @Override
    public boolean userCanManageDataSetInstance(String userName,
                                                Integer dataSetInstanceId) 
        throws ForbiddenException 
    {
        if (userIsSuperuser(userName)) {
            return true;
        }        
        Integer count = 
            userRoleRepository.
                countByUserUserNameAndStudyRoleSitePoliciesSiteNodesDataSetInstancesDataSetInstanceId(
                    userName,dataSetInstanceId);
        return (count > 0) ? true : false;          
    }
    
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
        for (StandardRole standardRole : standardRoleRepository.findAll())
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
        for (StandardRole standardRole : standardRoleRepository.findAll())
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
     * Creates a single Site with associated roles and role permissions.
     * 
     * 1.  Creates the site table entry for the new site. 
     * 2.  Creates a role for the administrator at that site. 
     *     The way we've set things up, roles are associated with studies; 
     *     we use a study called "SCANNER" for this role,
     *     and we call the role something like "SiteX Site Administrator".
     * 3.  Creates an entry in the site_policy table giving the 
     *     "SiteX Site Administrator" role permission to administer SiteX policy.
     * 4.  Optionally assigns a user (if specified) the new site admin role.
     * 
     * @param site the site to create
     * @param defaultSiteAdmin the user to assign as the default site admin
     * @return the created site
     */
    @Override
    @Transactional
    public Site createSite(Site site, ScannerUser defaultSiteAdmin) 
    {
        /* 1. Create the site table entry for the new site. */
        Site createdSite = siteRepository.save(site);
        
        /* 2. Create a role for the administrator at the new site. */
        // First we have to find the correct root Study to associate the site
        // with.  In this version, it is basically hard-coded to "SCANNER"
        // which is of course hacky, but will have to suffice.
        Study scannerStudy = 
            studyRepository.findByStudyName(ROOT_STUDY_NAME);
        if (scannerStudy == null) {
            throw new RuntimeException(
                String.format(
                "Unable to locate the root administrative study.  " + 
                "New sites cannot be created unless a study named %s " + 
                "already exsists.", ROOT_STUDY_NAME));
        }
        // Now initialize the StudyRole for the site admin and associate it 
        // with the root study, then try to create it.
        StudyRole siteAdminRole = new StudyRole();
        siteAdminRole.setStudy(scannerStudy);
        siteAdminRole.setRoleWithinStudy(
            String.format("%s Site Administrator", site.getSiteName()));
        StudyRole createdSiteAdminRole = 
            studyRoleRepository.save(siteAdminRole);
        
        /* 3. Create an entry in the SitePolicy table giving the 
         * newly created role permission to administer the new site's policy. */
        SitePolicy sitePolicy = new SitePolicy();
        sitePolicy.setSite(createdSite);
        sitePolicy.setStudyRole(createdSiteAdminRole);
        sitePolicyRepository.save(sitePolicy);
        
        /* 4. If a default user was specifed, create a new UserRole for that 
         *    user and map it to the new SiteAdminRole. */
        if (defaultSiteAdmin != null) {
            UserRole siteAdminUserRole = new UserRole();
            siteAdminUserRole.setUser(defaultSiteAdmin);
            siteAdminUserRole.setStudyRole(createdSiteAdminRole);
            userRoleRepository.save(siteAdminUserRole);
        }
        return createdSite;
    }
    
    /**
     * See {@link #createSite(Site,ScannerUser) createSite}.
     * @param site the site to create
     * @return the created Site object
     */
    @Override
    @Transactional
    public Site createSite(Site site) {
        return createSite(site,null);
    }
    
    /**
     * Deletes a single Site, removing any roles and privileges
     * associated with the Site.
     * @param site
     */    
    @Override
    @Transactional
    public void deleteSite(Site site) 
    {
        for (SitePolicy sitePolicy : 
             sitePolicyRepository.findBySiteSiteId(site.getSiteId())) 
        {
            StudyRole studyRole = sitePolicy.getStudyRole();
            List<UserRole> userRoles = 
                userRoleRepository.findByStudyRoleRoleId(studyRole.getRoleId());
            userRoleRepository.delete(userRoles);
            // this is hacky but it is the only way to remove the built-in Site
            // Administrator role without deleting other delegated study roles
            // which could still be valid in the context of the related study
            if (studyRole.getRoleWithinStudy().endsWith("Site Administrator")) {
                studyRoleRepository.delete(studyRole);
            }
        }
        siteRepository.delete(site);
    }        
    
    /**
     * Creates or updates a single AnalysisInstance, with optional creation of 
     * AnalysisResult child relations.
     * @param instance
     */    
    @Override
    @Transactional
    public AnalysisInstance saveAnalysisInstance(AnalysisInstance instance) 
    {
        List<AnalysisResult> resultList = 
            instance.getAnalysisResults();
        if ((resultList != null) && (!resultList.isEmpty())) 
        {
            instance.setAnalysisResults(null);
            Date now = Calendar.getInstance().getTime();
            if (instance.getCreated() == null) {
                instance.setCreated(now);
            }
            instance.setUpdated(now);
            analysisInstanceRepository.save(instance);              
            for (AnalysisResult result : resultList) {
                result.setAnalysisInstance(instance);
            }
            instance.setAnalysisResults(resultList);            
        }
        return analysisInstanceRepository.save(instance);
    }     

    @Override
    public String convertDataProcessingSpecification(
        String workingDir, String execName, String args, String input)
            throws Exception
    {
        Executor exec = new DefaultExecutor();    
        exec.setWorkingDirectory(new File(workingDir));
        CommandLine cl = new CommandLine(execName);
        cl.addArgument(args);
        
        ByteArrayInputStream in =
            new ByteArrayInputStream(input.getBytes("UTF-8"));        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();  
        PumpStreamHandler streamHandler = new PumpStreamHandler(out,err,in);
        exec.setStreamHandler(streamHandler);
        
        try {
            exec.execute(cl);  
        } catch (Exception e) {
            throw new Exception(e.toString() + "\n\n" + err.toString("UTF-8"));
        } 
        return out.toString("UTF-8");
    }
}
