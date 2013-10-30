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

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisInstance;
import edu.isi.misd.scanner.network.registry.data.repository.AnalysisInstanceRepository;
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
public class AnalysisInstanceController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(AnalysisInstanceController.class.getName());

    public static final String BASE_PATH = "/analysisHistory";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";
    public static final String REQUEST_PARAM_USER_NAME = "userName";
     
    @Autowired
    private AnalysisInstanceRepository analysisInstanceRepository;   
    
    @Autowired    
    private RegistryService registryService;   
    
	@RequestMapping(value = BASE_PATH, 
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<AnalysisInstance> getAnalysisInstances(
           @RequestParam Map<String, String> paramMap)        
    {
        Map<String,String> params = 
            validateParameterMap(
                paramMap, REQUEST_PARAM_STUDY_ID, REQUEST_PARAM_USER_NAME); 
        
        if (!params.isEmpty()) 
        {
            String studyId = params.get(REQUEST_PARAM_STUDY_ID);
            String userName = params.get(REQUEST_PARAM_USER_NAME);
            if ((studyId != null) && (userName != null)) {
                return
                    analysisInstanceRepository.
                        findByStudyIdAndUserNameFilteredByStudyRole(
                            validateIntegerParameter(
                                REQUEST_PARAM_STUDY_ID,studyId),
                            userName);
            }
            if (studyId != null) {
                return 
                    analysisInstanceRepository.findByStudyStudyId(
                        validateIntegerParameter(
                            REQUEST_PARAM_STUDY_ID,studyId));
            }
            if (studyId == null) {
                throw new BadRequestException(
                    "Required parameter missing: " + REQUEST_PARAM_STUDY_ID);                
            }
        }

        List<AnalysisInstance> analysisInstances = 
            new ArrayList<AnalysisInstance>();            
        Iterator iter = analysisInstanceRepository.findAll().iterator();
        CollectionUtils.addAll(analysisInstances, iter);            
        return analysisInstances;                     
	}
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody AnalysisInstance getAnalysisInstance(       
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        AnalysisInstance foundInstance = 
            analysisInstanceRepository.findOne(id);

        if (foundInstance == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundInstance;
    } 
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody AnalysisInstance createAnalysisInstance(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,         
        @RequestBody AnalysisInstance instance) 
    {
        try {           
            registryService.saveAnalysisInstance(instance);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return analysisInstanceRepository.findOne(
            instance.getAnalysisId());
    }   
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody AnalysisInstance updateAnalysisInstance(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,           
        @PathVariable(ID_URL_PATH_VAR) Integer id, 
        @RequestBody AnalysisInstance instance) 
    {
        // find the requested resource
        AnalysisInstance foundInstance = 
            analysisInstanceRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundInstance == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = instance.getAnalysisId();
        if (updateID == null) {
            instance.setAnalysisId(id);
        } else if (!instance.getAnalysisId().equals(
                    foundInstance.getAnalysisId())) {
            throw new ConflictException(
                instance.getAnalysisId(),
                foundInstance.getAnalysisId()); 
        }
        // check that required fields are populated from the existing record
        if (instance.getTransactionId()== null) {
            instance.setTransactionId(foundInstance.getTransactionId());
        }         
        if (instance.getUser() == null) {
            instance.setUser(foundInstance.getUser());
        }
        if (instance.getStudy() == null) {
            instance.setStudy(foundInstance.getStudy());
        } 
        if (instance.getNode() == null) {
            instance.setNode(foundInstance.getNode());
        }
        if (instance.getAnalysisTool()== null) {
            instance.setAnalysisTool(foundInstance.getAnalysisTool());
        }
        if (instance.getCreated()== null) {
            instance.setCreated(foundInstance.getCreated());
        }
        if (instance.getAnalysisResults() == null) {
            instance.setAnalysisResults(foundInstance.getAnalysisResults());
        }         
        // check that the user can perform the update
        if (!loginName.equalsIgnoreCase(instance.getUser().getUserName())) {
            throw new ForbiddenException(
                loginName, 
                "The current user is not the owner of the analysis instance");            
        }        
        try {
            registryService.saveAnalysisInstance(instance);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return analysisInstanceRepository.findOne(
            instance.getAnalysisId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeAnalysisInstance(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,           
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        // check that the user can perform the delete
        if (!registryService.userIsSuperuser(loginName)) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SUPERUSER_ROLE_REQUIRED);
        }            
        if (!analysisInstanceRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        analysisInstanceRepository.delete(id);
    } 
  
}
