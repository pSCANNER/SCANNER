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

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisPolicyStatement;
import edu.isi.misd.scanner.network.registry.data.repository.AnalysisPolicyStatementRepository;
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
public class AnalysisPolicyStatementController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(AnalysisPolicyStatementController.class.getName());
    
    public static final String BASE_PATH = "/analysisPolicies";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_USER_NAME = "userName";   
    public static final String REQUEST_PARAM_INSTANCE_ID = "dataSetInstanceId"; 
    public static final String REQUEST_PARAM_ANALYSIS_TOOL_ID = "analysisToolId";     
    
    @Autowired
    private AnalysisPolicyStatementRepository analysisPolicyStatementRepository;      
    
    @Autowired
    private RegistryService registryService;   
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<AnalysisPolicyStatement> 
        getAnalysisPolicyStatements(@RequestParam Map<String, String> paramMap) 
    {
        Map<String,String> params = 
            validateParameterMap(
                paramMap,
                REQUEST_PARAM_USER_NAME,
                REQUEST_PARAM_INSTANCE_ID,
                REQUEST_PARAM_ANALYSIS_TOOL_ID);
        
        if (!params.isEmpty()) 
        {
            ArrayList<String> missingParams = new ArrayList<String>();            
            String userName = params.get(REQUEST_PARAM_USER_NAME);  
            if (userName == null) {
                missingParams.add(REQUEST_PARAM_USER_NAME);
            }              
            String instanceId = params.get(REQUEST_PARAM_INSTANCE_ID);
            if (instanceId == null) {
                missingParams.add(REQUEST_PARAM_INSTANCE_ID);
            }
            String toolId = params.get(REQUEST_PARAM_ANALYSIS_TOOL_ID);
            if (toolId == null) {
                missingParams.add(REQUEST_PARAM_ANALYSIS_TOOL_ID);
            }                     
            if (!missingParams.isEmpty()) {
                throw new BadRequestException(
                    "Required parameter(s) missing: " + missingParams);                
            }             
            return
                analysisPolicyStatementRepository.
                    findAnalysisPolicyStatementByUserNameAndInstanceIdAndToolId(
                        userName,
                        validateIntegerParameter(
                            REQUEST_PARAM_INSTANCE_ID, instanceId),
                        validateIntegerParameter(
                            REQUEST_PARAM_ANALYSIS_TOOL_ID, toolId)
                );
        }
        
        List<AnalysisPolicyStatement> analysisPolicyStatements = 
            new ArrayList<AnalysisPolicyStatement>();
        Iterator iter = analysisPolicyStatementRepository.findAll().iterator();
        CollectionUtils.addAll(analysisPolicyStatements, iter);
        
        return analysisPolicyStatements;         
	}
    
    @RequestMapping(value = ENTITY_PATH, 
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody AnalysisPolicyStatement getAnalysisPolicyStatement(
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        AnalysisPolicyStatement foundAnalysisPolicyStatement = 
            analysisPolicyStatementRepository.findOne(id);

        if (foundAnalysisPolicyStatement == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundAnalysisPolicyStatement;
    } 
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody AnalysisPolicyStatement createAnalysisPolicyStatement(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,           
        @RequestBody AnalysisPolicyStatement analysisPolicyStatement) 
    {
        // first, check that the requested DataSetInstance association is valid
        Assert.notNull(analysisPolicyStatement.getDataSetInstance(), 
            nullVariableMsg(AnalysisPolicyStatement.class.getSimpleName()));
        
        // check that the user can perform the create
        Integer dataSetInstanceId = 
            analysisPolicyStatement.getDataSetInstance().getDataSetInstanceId();
        if (!registryService.userCanManageDataSetInstance(
            loginName, dataSetInstanceId)) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SITE_MANAGEMENT_ROLE_REQUIRED);            
        }        
        try {
            analysisPolicyStatementRepository.save(analysisPolicyStatement);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return analysisPolicyStatementRepository.findOne(
            analysisPolicyStatement.getAnalysisPolicyStatementId());
    }   
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody AnalysisPolicyStatement updateAnalysisPolicyStatement(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,           
        @PathVariable(ID_URL_PATH_VAR) Integer id,
        @RequestBody AnalysisPolicyStatement analysisPolicyStatement) 
    {
        // find the requested resource
        AnalysisPolicyStatement foundAnalysisPolicyStatement = 
            analysisPolicyStatementRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundAnalysisPolicyStatement == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = 
            analysisPolicyStatement.getAnalysisPolicyStatementId();
        if (updateID == null) {
            analysisPolicyStatement.setAnalysisPolicyStatementId(id);
        } else if (
            !analysisPolicyStatement.getAnalysisPolicyStatementId().equals(
            foundAnalysisPolicyStatement.getAnalysisPolicyStatementId())) 
        {
            throw new ConflictException(
                analysisPolicyStatement.getAnalysisPolicyStatementId(),
                foundAnalysisPolicyStatement.getAnalysisPolicyStatementId()); 
        }
        // check that the user can perform the update
        Integer dataSetInstanceId = 
            analysisPolicyStatement.getDataSetInstance().getDataSetInstanceId();
        if (!registryService.userCanManageDataSetInstance(
            loginName, dataSetInstanceId)) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SITE_MANAGEMENT_ROLE_REQUIRED);            
        }
        try {
            analysisPolicyStatementRepository.save(analysisPolicyStatement);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return analysisPolicyStatementRepository.findOne(
            analysisPolicyStatement.getAnalysisPolicyStatementId());
    }     
    
    @RequestMapping(value = ENTITY_PATH, 
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeAnalysisPolicyStatement(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,           
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        // find the requested resource
        AnalysisPolicyStatement analysisPolicyStatement = 
            analysisPolicyStatementRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (analysisPolicyStatement == null) {
            throw new ResourceNotFoundException(id);            
        }
        // check that the user can perform the delete
        Integer dataSetInstanceId = 
            analysisPolicyStatement.getDataSetInstance().getDataSetInstanceId();
        if (!registryService.userCanManageDataSetInstance(
            loginName, dataSetInstanceId)) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SITE_MANAGEMENT_ROLE_REQUIRED);            
        }     
        analysisPolicyStatementRepository.delete(id);
    } 
  
}
