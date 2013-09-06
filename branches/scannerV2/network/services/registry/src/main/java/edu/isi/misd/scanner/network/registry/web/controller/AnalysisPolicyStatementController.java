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
    
    public static final String REQUEST_PARAM_USER_ID = "userId";   
    public static final String REQUEST_PARAM_INSTANCE_ID = "dataSetInstanceId"; 
    public static final String REQUEST_PARAM_ANALYSIS_TOOL_ID = "analysisToolId";     
    
    @Autowired
    private AnalysisPolicyStatementRepository analysisPolicyStatementRepository;   
    
	@RequestMapping(value = "/analysisPolicies", method = RequestMethod.GET)
	public @ResponseBody List<AnalysisPolicyStatement> 
        getAnalysisPolicyStatements(@RequestParam Map<String, String> paramMap) 
    {
        if (!paramMap.isEmpty()) 
        {
            ArrayList<String> missingParams = new ArrayList<String>();            
            String userId = paramMap.remove(REQUEST_PARAM_USER_ID);  
            if (userId == null) {
                missingParams.add(REQUEST_PARAM_USER_ID);
            }              
            String instanceId = paramMap.remove(REQUEST_PARAM_INSTANCE_ID);
            if (instanceId == null) {
                missingParams.add(REQUEST_PARAM_INSTANCE_ID);
            }
            String toolId = paramMap.remove(REQUEST_PARAM_ANALYSIS_TOOL_ID);
            if (toolId == null) {
                missingParams.add(REQUEST_PARAM_ANALYSIS_TOOL_ID);
            }             
            if (!paramMap.isEmpty()) {
                throw new BadRequestException(paramMap.keySet());
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
    
    @RequestMapping(value = "/analysisPolicies", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody AnalysisPolicyStatement createAnalysisPolicyStatement(
           @RequestBody AnalysisPolicyStatement analysisPolicyStatement) 
    {
        try {
            analysisPolicyStatementRepository.save(analysisPolicyStatement);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return analysisPolicyStatementRepository.findOne(
            analysisPolicyStatement.getAnalysisPolicyStatementId());
    }  
    
    @RequestMapping(value = "/analysisPolicies/{id}", method = RequestMethod.GET)
    public @ResponseBody AnalysisPolicyStatement getAnalysisPolicyStatement(
           @PathVariable("id") Integer id) 
    {
        AnalysisPolicyStatement foundAnalysisPolicyStatement = 
            analysisPolicyStatementRepository.findOne(id);

        if (foundAnalysisPolicyStatement == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundAnalysisPolicyStatement;
    }  
    
    @RequestMapping(value = "/analysisPolicies/{id}", method = RequestMethod.PUT)
    public @ResponseBody AnalysisPolicyStatement updateAnalysisPolicyStatement(
           @PathVariable("id") Integer id,
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
        } else if (!analysisPolicyStatement.getAnalysisPolicyStatementId().equals(
                    foundAnalysisPolicyStatement.getAnalysisPolicyStatementId())) 
        {
            throw new ConflictException(
                "Update failed: specified object ID (" + 
                analysisPolicyStatement.getAnalysisPolicyStatementId() + 
                ") does not match referenced ID (" + 
                foundAnalysisPolicyStatement.getAnalysisPolicyStatementId() + ")"); 
        }
        // ok, good to go
        try {
            analysisPolicyStatementRepository.save(analysisPolicyStatement);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return analysisPolicyStatementRepository.findOne(
            analysisPolicyStatement.getAnalysisPolicyStatementId());
    }     
    
    @RequestMapping(value = "/analysisPolicies/{id}", method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeAnalysisPolicyStatement(@PathVariable("id") Integer id) 
    {
        if (!analysisPolicyStatementRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        analysisPolicyStatementRepository.delete(id);
    } 
  
}
