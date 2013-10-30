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

import edu.isi.misd.scanner.network.registry.data.domain.ScannerUser;
import edu.isi.misd.scanner.network.registry.data.domain.Study;
import edu.isi.misd.scanner.network.registry.data.repository.ScannerUserRepository;
import edu.isi.misd.scanner.network.registry.data.repository.StudyRepository;
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
public class StudyController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(StudyController.class.getName());
    
    public static final String BASE_PATH = "/studies"; 
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;
    public static final String REQUEST_PARAM_STUDY_NAME = "studyName";
    public static final String REQUEST_PARAM_USER_NAME = "userName";  
    
    @Autowired
    private StudyRepository studyRepository;       
    
    @Autowired
    private ScannerUserRepository scannerUserRepository;  
    
    @Autowired
    private RegistryService registryService;   
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)                    
	public @ResponseBody List<Study> getStudies(        
        @RequestParam Map<String, String> paramMap) 
    {        
        Map<String,String> params = 
            validateParameterMap(
                paramMap,
                REQUEST_PARAM_STUDY_NAME,
                REQUEST_PARAM_USER_NAME);  
        
        String studyName = params.get(REQUEST_PARAM_STUDY_NAME);
        String userName = params.get(REQUEST_PARAM_USER_NAME);       
        List<Study> studies = new ArrayList<Study>();        
        if (studyName != null) {
            Study study = studyRepository.findByStudyName(studyName);
            if (study == null) {
                throw new ResourceNotFoundException(studyName);
            }
            studies.add(study);
        } else if (userName != null) {
            return 
                studyRepository.findStudiesForUserName(userName);
        } else {
            Iterator iter = studyRepository.findAll().iterator();
            CollectionUtils.addAll(studies, iter);              
        }  
        return studies;                 
    }

    @RequestMapping(value = ENTITY_PATH, 
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody Study getStudy(          
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        Study foundStudy = studyRepository.findOne(id);
        if (foundStudy == null) {
            throw new ResourceNotFoundException(id);
        }        
        return foundStudy;
    } 
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody Study createStudy(
           @RequestHeader(HEADER_LOGIN_NAME) String loginName,          
           @RequestBody Study study) 
    {
        // get the user record for the current user
        ScannerUser loggedInUser = 
            scannerUserRepository.findByUserName(loginName);
        if (loggedInUser == null) {
            throw new ForbiddenException(
                loginName,RegistryServiceConstants.MSG_UNKNOWN_USER_NAME);
        }        
        study.setStudyOwner(loggedInUser);
        try {
            registryService.createStudy(study);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return studyRepository.findOne(study.getStudyId());
    }   
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)                    
    public @ResponseBody Study updateStudy(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,    
        @PathVariable(ID_URL_PATH_VAR) Integer id,
        @RequestBody Study study) 
    {
        // find the requested resource
        Study foundStudy = studyRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundStudy == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = study.getStudyId();
        if (updateID == null) {
            study.setStudyId(id);
        } else if (!study.getStudyId().equals(foundStudy.getStudyId())) {
            throw new ConflictException(
                study.getStudyId(),foundStudy.getStudyId()); 
        }
        // check that the user can perform the update
        if (!registryService.userCanManageStudy(loginName,study.getStudyId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);            
        }        
        // ensure that the studyOwner is populated correctly. If it is absent
        // in the update request, use the value of the existing study. Otherwise
        // look up the the specified studyOwner to see if it is a valid user
        if (study.getStudyOwner() == null) {
            study.setStudyOwner(foundStudy.getStudyOwner());
        } else {
            ScannerUser owner = 
                scannerUserRepository.findOne(
                    study.getStudyOwner().getUserId());
            if (owner != null) {
                study.setStudyOwner(owner);
            } else {
                throw new BadRequestException(
                    String.format(
                        RegistryServiceConstants.MSG_INVALID_PARAMETER_VALUE,
                        study.getStudyOwner().getUserId()) + " " +
                    RegistryServiceConstants.MSG_UNKNOWN_USER_NAME);                
            }
        }
        // perform the update
        try {
            registryService.updateStudy(study);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return studyRepository.findOne(study.getStudyId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE)                
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeStudy(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,     
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        if (!studyRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        // check that the user can perform the delete
        if (!registryService.userCanManageStudy(loginName,id)) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);             
        }        
        registryService.deleteStudy(id);
    } 
  
}
