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
package edu.isi.misd.scanner.network.registry.web.controller; 

import edu.isi.misd.scanner.network.registry.data.domain.StudyRole;
import edu.isi.misd.scanner.network.registry.data.domain.UserRole;
import edu.isi.misd.scanner.network.registry.data.repository.StudyRoleRepository;
import edu.isi.misd.scanner.network.registry.data.repository.UserRoleRepository;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
import edu.isi.misd.scanner.network.registry.data.service.RegistryServiceConstants;
import edu.isi.misd.scanner.network.registry.web.errors.BadRequestException;
import edu.isi.misd.scanner.network.registry.web.errors.ConflictException;
import edu.isi.misd.scanner.network.registry.web.errors.ForbiddenException;
import edu.isi.misd.scanner.network.registry.web.errors.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *  @author Mike D'Arcy 
 */
@Controller
public class UserRoleController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(UserRoleController.class.getName());
    
    public static final String BASE_PATH = "/userRoles";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH; 
    public static final String REQUEST_PARAM_USER_NAME = "userName";      
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";  
    public static final String REQUEST_PARAM_SITE_ID = "siteId";  
    public static final String REQUEST_PARAM_NODE_ID = "nodeId";  
    public static final String REQUEST_PARAM_DATASET_INSTANCE_ID = 
        "dataSetInstanceId";      
    
    @Autowired
    private UserRoleRepository userRoleRepository;   
    
    @Autowired
    private StudyRoleRepository studyRoleRepository;   
    
    @Autowired
    private RegistryService registryService;  
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<UserRole> getUserRoles(
           @RequestParam Map<String, String> paramMap) 
    {
        Map<String,String> params = 
            validateParameterMap(
                paramMap,
                REQUEST_PARAM_USER_NAME,
                REQUEST_PARAM_STUDY_ID,
                REQUEST_PARAM_SITE_ID,
                REQUEST_PARAM_NODE_ID,
                REQUEST_PARAM_DATASET_INSTANCE_ID); 
        
        String userName = params.get(REQUEST_PARAM_USER_NAME);        
        String studyId = params.get(REQUEST_PARAM_STUDY_ID);        
        String siteId = params.get(REQUEST_PARAM_SITE_ID);   
        String nodeId = params.get(REQUEST_PARAM_NODE_ID);   
        String dataSetInstanceId = params.get(REQUEST_PARAM_DATASET_INSTANCE_ID);  
        
        if (userName != null)
        {
            if (studyId != null) {
                return 
                    userRoleRepository.
                        findByUserUserNameAndStudyRoleStudyStudyId(
                            userName, 
                            validateIntegerParameter(
                                REQUEST_PARAM_STUDY_ID, studyId));
            } else if  (siteId != null) {
                return 
                    userRoleRepository.
                        findByUserUserNameAndStudyRoleSitePoliciesSiteSiteId(
                            userName, 
                            validateIntegerParameter(
                                REQUEST_PARAM_SITE_ID, siteId));                    
            } else if  (nodeId != null) {
                return 
                    userRoleRepository.
                        findByUserUserNameAndStudyRoleSitePoliciesSiteNodesNodeId(
                            userName, 
                            validateIntegerParameter(
                                REQUEST_PARAM_NODE_ID, nodeId));                  
            } else if  (dataSetInstanceId != null) {
                return 
                    userRoleRepository.
                        findByUserUserNameAndStudyRoleSitePoliciesSiteNodesDataSetInstancesDataSetInstanceId(
                            userName, 
                            validateIntegerParameter(
                                REQUEST_PARAM_DATASET_INSTANCE_ID,
                                dataSetInstanceId));                    
            } else {
                return 
                    userRoleRepository.findByUserUserName(userName);                
            }
        } else if (studyId != null) {
            if (siteId != null) {
                return
                    userRoleRepository.
                        findByStudyRoleStudyStudyIdAndStudyRoleSitePoliciesSiteSiteId(
                            validateIntegerParameter(
                                REQUEST_PARAM_STUDY_ID, studyId),
                            validateIntegerParameter(
                                REQUEST_PARAM_SITE_ID, siteId));                
            } else {
                return
                    userRoleRepository.findByStudyRoleStudyStudyId(
                        validateIntegerParameter(
                            REQUEST_PARAM_STUDY_ID, studyId));
            }
        } else if (siteId != null || 
                   nodeId != null || 
                   dataSetInstanceId != null) {
            throw new BadRequestException(
                "Required parameter missing: " + REQUEST_PARAM_USER_NAME);                    
        } else {
            List<UserRole> roles = new ArrayList<UserRole>();             
            Iterator iter = userRoleRepository.findAll().iterator();
            CollectionUtils.addAll(roles, iter);    
            return roles;
        }
    }
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody UserRole getUserRole(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        UserRole foundUserRole = userRoleRepository.findOne(id);

        if (foundUserRole == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundUserRole;
    }
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody UserRole createUserRole(
           @RequestHeader(HEADER_LOGIN_NAME) String loginName,        
           @RequestBody UserRole userRole) 
    {
        // first, check that the requested StudyRole association is valid
        Assert.notNull(userRole.getStudyRole(), 
            nullVariableMsg(StudyRole.class.getSimpleName()));     
        Integer roleId = userRole.getStudyRole().getRoleId();
        StudyRole studyRole = studyRoleRepository.findOne(roleId);
        if (studyRole == null) {
            throw new BadRequestException(
                StudyRole.class.getSimpleName(),roleId);
        }
        // check that the user can perform the create
        if (!registryService.userCanManageStudy(
            loginName, studyRole.getStudy().getStudyId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);         
        }         
        try {
            userRoleRepository.save(userRole);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return userRoleRepository.findOne(userRole.getUserRoleId());
    }   
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody UserRole updateUserRole(
           @RequestHeader(HEADER_LOGIN_NAME) String loginName,        
           @PathVariable(ID_URL_PATH_VAR) Integer id,
           @RequestBody UserRole userRole) 
    {
        // find the requested resource
        UserRole foundUserRole = userRoleRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundUserRole == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = userRole.getUserRoleId();
        if (updateID == null) {
            userRole.setUserRoleId(id);
        } else if (!userRole.getUserRoleId().equals(
                   foundUserRole.getUserRoleId())) {
            throw new ConflictException(
                userRole.getUserRoleId(), foundUserRole.getUserRoleId()); 
        }

        // check that the user can perform the update
        if (!registryService.userCanManageStudy(
            loginName, userRole.getStudyRole().getStudy().getStudyId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);         
        } 
        try {
            userRoleRepository.save(userRole);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return userRoleRepository.findOne(userRole.getUserRoleId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeUserRole(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,        
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        // find the requested resource
        UserRole userRole = userRoleRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (userRole == null) {
            throw new ResourceNotFoundException(id);            
        }
        // check that the user can perform the delete
        if (!registryService.userCanManageStudy(
            loginName, userRole.getStudyRole().getStudy().getStudyId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);         
        }          
        userRoleRepository.delete(id);
    } 
  
}
