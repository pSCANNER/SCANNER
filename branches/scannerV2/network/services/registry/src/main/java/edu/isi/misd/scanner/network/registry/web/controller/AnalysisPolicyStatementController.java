package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisPolicyStatement;
import edu.isi.misd.scanner.network.registry.data.repository.AnalysisPolicyStatementRepository;
import edu.isi.misd.scanner.network.registry.web.errors.BadRequestException;
import edu.isi.misd.scanner.network.registry.web.errors.ConflictException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 */
@Controller
public class AnalysisPolicyStatementController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(AnalysisPolicyStatementController.class.getName());
    
    public static final String BASE_PATH = "/analysisPolicies";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_USER_ID = "userId";   
    public static final String REQUEST_PARAM_INSTANCE_ID = "dataSetInstanceId"; 
    public static final String REQUEST_PARAM_ANALYSIS_TOOL_ID = "analysisToolId";     
    
    @Autowired
    private AnalysisPolicyStatementRepository analysisPolicyStatementRepository;   
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<AnalysisPolicyStatement> 
        getAnalysisPolicyStatements(@RequestParam Map<String, String> paramMap) 
    {
        Map<String,String> params = 
            validateParameterMap(
                paramMap,
                REQUEST_PARAM_USER_ID,
                REQUEST_PARAM_INSTANCE_ID,
                REQUEST_PARAM_ANALYSIS_TOOL_ID);
        
        if (!params.isEmpty()) 
        {
            ArrayList<String> missingParams = new ArrayList<String>();            
            String userId = params.get(REQUEST_PARAM_USER_ID);  
            if (userId == null) {
                missingParams.add(REQUEST_PARAM_USER_ID);
            }              
            String instanceId = params.get(REQUEST_PARAM_INSTANCE_ID);
            if (instanceId == null) {
                missingParams.add(REQUEST_PARAM_INSTANCE_ID);
            }
            String toolId = params.get(REQUEST_PARAM_ANALYSIS_TOOL_ID);
            if (toolId == null) {
                missingParams.add(REQUEST_PARAM_ANALYSIS_TOOL_ID);
            }                     
            if ((userId == null) || 
                (instanceId == null) ||                
                (toolId == null)) 
            {
                throw new BadRequestException(
                    "Required parameter(s) missing: " + missingParams);                
            }             
            return
                analysisPolicyStatementRepository.
                    findAnalysisPolicyStatementByUserIdAndInstanceIdAndToolId(
                        validateIntegerParameter(
                            REQUEST_PARAM_USER_ID, userId),
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
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody AnalysisPolicyStatement createAnalysisPolicyStatement(
           @RequestBody AnalysisPolicyStatement analysisPolicyStatement) 
    {
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
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody AnalysisPolicyStatement updateAnalysisPolicyStatement(
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
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        if (!analysisPolicyStatementRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        analysisPolicyStatementRepository.delete(id);
    } 
  
}
