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

import edu.isi.misd.scanner.network.registry.data.domain.DataSetInstance;
import edu.isi.misd.scanner.network.registry.data.repository.DataSetInstanceRepository;
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
public class DataSetInstanceController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(DataSetInstanceController.class.getName());

    public static final String BASE_PATH = "/instances";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_DATASET_ID = "dataSetId";    
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";       
    public static final String REQUEST_PARAM_USER_NAME = "userName";
     
    @Autowired
    private DataSetInstanceRepository dataSetInstanceRepository;      
    
    @Autowired
    private RegistryService registryService;
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<DataSetInstance> getDataSetInstances(
           @RequestParam Map<String, String> paramMap)        
    {
        Map<String,String> params = 
            validateParameterMap(
                paramMap,
                REQUEST_PARAM_DATASET_ID,
                REQUEST_PARAM_STUDY_ID,                
                REQUEST_PARAM_USER_NAME);
        
        if (!params.isEmpty()) 
        {
            ArrayList<String> missingParams = new ArrayList<String>();            
            String dataSetId = params.get(REQUEST_PARAM_DATASET_ID);  
            if (dataSetId == null) {
                missingParams.add(REQUEST_PARAM_DATASET_ID);
            }              
            String studyId = params.get(REQUEST_PARAM_STUDY_ID);
            if (studyId == null) {
                missingParams.add(REQUEST_PARAM_STUDY_ID);
            }
            String userName = params.get(REQUEST_PARAM_USER_NAME);                              
            if (userName != null) 
            {
                
                if ((dataSetId != null) && (studyId != null)) {                 
                    return
                        dataSetInstanceRepository.
                            findByDataSetIdAndStudyIdAndUserNameFilteredByAnalysisPolicy(
                                validateIntegerParameter(
                                    REQUEST_PARAM_DATASET_ID, dataSetId),
                                validateIntegerParameter(
                                    REQUEST_PARAM_STUDY_ID, studyId),                                
                                userName);
                } else if (studyId != null) {
                    return
                        dataSetInstanceRepository.
                            findByStudyIdAndUserNameFilteredByAnalysisPolicy(
                                validateIntegerParameter(
                                    REQUEST_PARAM_STUDY_ID, studyId),                                
                                userName);                    
                } else if ((dataSetId == null) && (studyId == null)) {   
                    return 
                        dataSetInstanceRepository.
                            findByNodeSiteSitePoliciesStudyRoleUserRolesUserUserName(
                                userName);
                } else {
                    throw new BadRequestException(
                        "Required parameter(s) missing: " + missingParams);                            
                }
            } else {
                throw new BadRequestException(
                    "Required parameter missing: " + REQUEST_PARAM_USER_NAME);                       
            }           
        }

        List<DataSetInstance> dataSetInstances = 
            new ArrayList<DataSetInstance>();            
        Iterator iter = dataSetInstanceRepository.findAll().iterator();
        CollectionUtils.addAll(dataSetInstances, iter);            
        return dataSetInstances;                     
	}
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody DataSetInstance getDataSetInstance(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        DataSetInstance foundDataSet = 
            dataSetInstanceRepository.findOne(id);

        if (foundDataSet == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundDataSet;
    } 
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody DataSetInstance createDataSetInstance(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,            
        @RequestBody DataSetInstance dataSetInstance) 
    {       
        // check that the user can perform the create
        if (!registryService.userCanManageDataSetInstance(
            loginName, dataSetInstance.getDataSetInstanceId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SITE_MANAGEMENT_ROLE_REQUIRED);            
        }         
        try {
            dataSetInstanceRepository.save(dataSetInstance);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return dataSetInstanceRepository.findOne(
            dataSetInstance.getDataSetInstanceId());
    }   
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody DataSetInstance updateDataSetInstance(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,         
        @PathVariable(ID_URL_PATH_VAR) Integer id, 
        @RequestBody DataSetInstance dataSetInstance) 
    {
        // find the requested resource
        DataSetInstance foundDataSetInstance = 
            dataSetInstanceRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundDataSetInstance == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = dataSetInstance.getDataSetInstanceId();
        if (updateID == null) {
            dataSetInstance.setDataSetInstanceId(id);
        } else if (!dataSetInstance.getDataSetInstanceId().equals(
                    foundDataSetInstance.getDataSetInstanceId())) {
            throw new ConflictException( 
                dataSetInstance.getDataSetInstanceId(),
                foundDataSetInstance.getDataSetInstanceId()); 
        }
        // check that the user can perform the update
        if (!registryService.userCanManageDataSetInstance(
            loginName, dataSetInstance.getDataSetInstanceId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SITE_MANAGEMENT_ROLE_REQUIRED);            
        }   
        try {
            dataSetInstanceRepository.save(dataSetInstance);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return dataSetInstanceRepository.findOne(
            dataSetInstance.getDataSetInstanceId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeDataSetInstance(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,         
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        // find the requested resource
        DataSetInstance dataSetInstance = 
            dataSetInstanceRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (dataSetInstance == null) {
            throw new ResourceNotFoundException(id);            
        }
        // check that the user can perform the delete
        if (!registryService.userCanManageDataSetInstance(
            loginName, dataSetInstance.getDataSetInstanceId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SITE_MANAGEMENT_ROLE_REQUIRED);            
        }          
        dataSetInstanceRepository.delete(id);
    } 
  
}
