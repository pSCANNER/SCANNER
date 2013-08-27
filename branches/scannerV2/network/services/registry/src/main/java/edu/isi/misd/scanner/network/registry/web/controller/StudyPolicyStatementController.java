package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.StudyPolicyStatement;
import edu.isi.misd.scanner.network.registry.data.repository.StudyPolicyStatementRepository;
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
public class StudyPolicyStatementController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(StudyPolicyStatementController.class.getName());
    
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";   
    public static final String REQUEST_PARAM_DATASET_ID = "dataSetId"; 
    public static final String REQUEST_PARAM_ANALYSIS_TOOL_ID = "analysisToolId";     
    
    @Autowired
    private StudyPolicyStatementRepository studyPolicyStatementRepository;   
    
	@RequestMapping(value = "/studyPolicies", method = RequestMethod.GET)
	public @ResponseBody List<StudyPolicyStatement> 
        getStudyPolicyStatements(@RequestParam Map<String, String> paramMap) 
    {
        if (!paramMap.isEmpty()) 
        {
            ArrayList missingParams = new ArrayList();            
            String studyId = paramMap.remove(REQUEST_PARAM_STUDY_ID);  
            if (studyId == null) {
                missingParams.add(REQUEST_PARAM_STUDY_ID);
            }              
            String dataSetId = paramMap.remove(REQUEST_PARAM_DATASET_ID);
            if (dataSetId == null) {
                missingParams.add(REQUEST_PARAM_DATASET_ID);
            }
            String toolId = paramMap.remove(REQUEST_PARAM_ANALYSIS_TOOL_ID);
            if (toolId == null) {
                missingParams.add(REQUEST_PARAM_ANALYSIS_TOOL_ID);
            }               
            if (!paramMap.isEmpty()) {
                throw new BadRequestException(paramMap.keySet());
            }
            if ((studyId != null) && 
                (dataSetId != null) &&                 
                (toolId != null)) 
            {
                Integer sId;
                try {
                    sId = Integer.parseInt(studyId);
                } catch (NumberFormatException nfe) {
                    throw new BadRequestException(
                        String.format("Invalid parameter format for %s - %s",
                            REQUEST_PARAM_STUDY_ID, nfe.toString()));
                } 
                Integer dId;
                try {
                    dId = Integer.parseInt(dataSetId);
                } catch (NumberFormatException nfe) {
                    throw new BadRequestException(
                        String.format("Invalid parameter format for %s - %s",
                            REQUEST_PARAM_DATASET_ID, nfe.toString()));
                }                 
                Integer tId;
                try {
                    tId = Integer.parseInt(toolId);
                } catch (NumberFormatException nfe) {
                    throw new BadRequestException(
                        String.format("Invalid parameter format for %s - %s",
                            REQUEST_PARAM_ANALYSIS_TOOL_ID, nfe.toString()));
                }                 
                return
                    studyPolicyStatementRepository.
                        findStudyPolicyStatementByStudyIdAndDataSetIdAndToolId(
                            sId, dId, tId);
            }
            if ((studyId == null) || 
                (dataSetId == null) ||                
                (toolId == null)) 
            {
                throw new BadRequestException(
                    "Required parameter(s) missing: " + missingParams);                
            }
        }
        
        List<StudyPolicyStatement> studyPolicyStatements = 
            new ArrayList<StudyPolicyStatement>();
        Iterator iter = studyPolicyStatementRepository.findAll().iterator();
        CollectionUtils.addAll(studyPolicyStatements, iter);
        
        return studyPolicyStatements;         
	}
    
    @RequestMapping(value = "/studyPolicies", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody StudyPolicyStatement createStudyPolicyStatement(
           @RequestBody StudyPolicyStatement studyPolicyStatement) 
    {
        try {
            studyPolicyStatementRepository.save(studyPolicyStatement);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return studyPolicyStatementRepository.findOne(
            studyPolicyStatement.getStudyPolicyStatementId());
    }  
    
    @RequestMapping(value = "/studyPolicies/{id}", method = RequestMethod.GET)
    public @ResponseBody StudyPolicyStatement getStudyPolicyStatement(
           @PathVariable("id") Integer id) 
    {
        StudyPolicyStatement foundStudyPolicyStatement = 
            studyPolicyStatementRepository.findOne(id);

        if (foundStudyPolicyStatement == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundStudyPolicyStatement;
    }  
    
    @RequestMapping(value = "/studyPolicies/{id}", method = RequestMethod.PUT)
    public @ResponseBody StudyPolicyStatement updateStudyPolicyStatement(
           @PathVariable("id") Integer id,
           @RequestBody StudyPolicyStatement studyPolicyStatement) 
    {
        // find the requested resource
        StudyPolicyStatement foundStudyPolicyStatement = 
            studyPolicyStatementRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundStudyPolicyStatement == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = 
            studyPolicyStatement.getStudyPolicyStatementId();
        if (updateID == null) {
            studyPolicyStatement.setStudyPolicyStatementId(id);
        } else if (!studyPolicyStatement.getStudyPolicyStatementId().equals(
                    foundStudyPolicyStatement.getStudyPolicyStatementId())) 
        {
            throw new ConflictException(
                "Update failed: specified object ID (" + 
                studyPolicyStatement.getStudyPolicyStatementId() + 
                ") does not match referenced ID (" + 
                foundStudyPolicyStatement.getStudyPolicyStatementId() + ")"); 
        }
        // ok, good to go
        try {
            studyPolicyStatementRepository.save(studyPolicyStatement);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return studyPolicyStatementRepository.findOne(
            studyPolicyStatement.getStudyPolicyStatementId());
    }     
    
    @RequestMapping(value = "/studyPolicies/{id}", method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeStudyPolicyStatement(@PathVariable("id") Integer id) 
    {
        if (!studyPolicyStatementRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        studyPolicyStatementRepository.delete(id);
    } 
  
}
