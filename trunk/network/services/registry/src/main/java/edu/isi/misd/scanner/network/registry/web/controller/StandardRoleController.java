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

import edu.isi.misd.scanner.network.registry.data.domain.StandardRole;
import edu.isi.misd.scanner.network.registry.data.repository.StandardRoleRepository;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
import edu.isi.misd.scanner.network.registry.data.service.RegistryServiceConstants;
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
public class StandardRoleController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(StandardRoleController.class.getName());
    
    public static final String BASE_PATH = "/standardRoles";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_ROLE_NAME = "roleName";
    
    @Autowired
    private StandardRoleRepository standardRoleRepository;   
    
    @Autowired 
    private RegistryService registryService;
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<StandardRole> getStandardRoles(
           @RequestParam Map<String, String> paramMap) 
    {
        Map<String,String> params = 
            validateParameterMap(paramMap, REQUEST_PARAM_ROLE_NAME); 
        
        String standardRoleName = params.get(REQUEST_PARAM_ROLE_NAME);        
        List<StandardRole> standardRoles = new ArrayList<StandardRole>();        
        if (standardRoleName != null) {
            StandardRole standardRole = 
                standardRoleRepository.findByStandardRoleName(standardRoleName);
            if (standardRole == null) {
                throw new ResourceNotFoundException(standardRoleName);
            }
            standardRoles.add(standardRole);
        } else {
            Iterator iter = standardRoleRepository.findAll().iterator();
            CollectionUtils.addAll(standardRoles, iter);      
        }
        return standardRoles;           
	}
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody StandardRole getStandardRole(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        StandardRole foundStandardRole = standardRoleRepository.findOne(id);

        if (foundStandardRole == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundStandardRole;
    }  
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody StandardRole createStandardRole(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,            
        @RequestBody StandardRole standardRole) 
    {
        // check that the user can perform the create
        if (!registryService.userIsSuperuser(loginName)) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SUPERUSER_ROLE_REQUIRED);
        }            
        try {
            standardRoleRepository.save(standardRole);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return standardRoleRepository.findOne(standardRole.getStandardRoleId());
    }  
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody StandardRole updateStandardRole(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,            
        @PathVariable(ID_URL_PATH_VAR) Integer id, 
        @RequestBody StandardRole standardRole) 
    {
        // find the requested resource
        StandardRole foundStandardRole = standardRoleRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundStandardRole == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = standardRole.getStandardRoleId();
        if (updateID == null) {
            standardRole.setStandardRoleId(id);
        } else if (!standardRole.getStandardRoleId().equals(
                    foundStandardRole.getStandardRoleId())) {
            throw new ConflictException(
                standardRole.getStandardRoleId(),
                foundStandardRole.getStandardRoleId()); 
        }
        // check that the user can perform the update
        if (!registryService.userIsSuperuser(loginName)) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SUPERUSER_ROLE_REQUIRED);
        }    
        try {
            standardRoleRepository.save(standardRole);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return standardRoleRepository.findOne(standardRole.getStandardRoleId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeStandardRole(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,            
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        // check that the user can perform the delete
        if (!registryService.userIsSuperuser(loginName)) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SUPERUSER_ROLE_REQUIRED);
        }            
        if (!standardRoleRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        standardRoleRepository.delete(id);
    } 
  
}
