package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisTool;
import edu.isi.misd.scanner.network.registry.data.repository.AnalysisToolRepository;
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
public class AnalysisToolController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(AnalysisToolController.class.getName());
    
    public static final String BASE_PATH = "/tools";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_USER_NAME = "userName";  
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";      
    public static final String REQUEST_PARAM_DATASET_ID = "dataSetId"; 
    public static final String REQUEST_PARAM_LIBRARY_ID = "libraryId";     
    
    @Autowired
    private AnalysisToolRepository analysisToolRepository;   
    
	@RequestMapping(value = BASE_PATH, 
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<AnalysisTool> getAnalysisTools(
           @RequestParam Map<String, String> paramMap) 
    {
        Map<String,String> params = 
            validateParameterMap(
                paramMap,
                REQUEST_PARAM_USER_NAME,
                REQUEST_PARAM_STUDY_ID,
                REQUEST_PARAM_DATASET_ID,
                REQUEST_PARAM_LIBRARY_ID);  
        
        if (!params.isEmpty()) 
        {
            ArrayList<String> missingParams = new ArrayList<String>();            
            String userName = params.get(REQUEST_PARAM_USER_NAME);  
            if (userName == null) {
                missingParams.add(REQUEST_PARAM_USER_NAME);
            }   
            String studyId = params.get(REQUEST_PARAM_STUDY_ID);
            if (studyId == null) {
                missingParams.add(REQUEST_PARAM_STUDY_ID);
            }                  
            String dataSetId = params.get(REQUEST_PARAM_DATASET_ID);
            if (dataSetId == null) {
                missingParams.add(REQUEST_PARAM_DATASET_ID);
            }
            String libraryId = params.get(REQUEST_PARAM_LIBRARY_ID);
            if (libraryId == null) {
                missingParams.add(REQUEST_PARAM_LIBRARY_ID);
            }               
            if ((userName == null) || 
                (studyId == null) ||                
                (dataSetId == null) ||
                (libraryId == null)) 
            {
                throw new BadRequestException(
                    "Required parameter(s) missing: " + missingParams);                
            }                          
            return
                analysisToolRepository.
                    findAnalysisToolByStudyPolicyStatement(
                        userName, 
                        validateIntegerParameter(
                            REQUEST_PARAM_STUDY_ID, studyId),
                        validateIntegerParameter(
                            REQUEST_PARAM_DATASET_ID, dataSetId),                
                        validateIntegerParameter(
                            REQUEST_PARAM_LIBRARY_ID, libraryId)
                );
        }
        
        List<AnalysisTool> toolLibraries = new ArrayList<AnalysisTool>();
        Iterator iter = analysisToolRepository.findAll().iterator();
        CollectionUtils.addAll(toolLibraries, iter);
        
        return toolLibraries;         
	}
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody AnalysisTool createAnalysisTool(
           @RequestBody AnalysisTool tool) 
    {
        try {
            analysisToolRepository.save(tool);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return analysisToolRepository.findOne(tool.getToolId());
    }  
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody AnalysisTool getAnalysisTool(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        AnalysisTool toolLib = analysisToolRepository.findOne(id);

        if (toolLib == null) {
            throw new ResourceNotFoundException(id);
        }
        return toolLib;
    }  
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody AnalysisTool updateAnalysisTool(
           @PathVariable(ID_URL_PATH_VAR) Integer id, @RequestBody AnalysisTool tool) 
    {
        // find the requested resource
        AnalysisTool toolLib = analysisToolRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (toolLib == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = tool.getToolId();
        if (updateID == null) {
            tool.setToolId(id);
        } else if (!tool.getToolId().equals(toolLib.getToolId())) {
            throw new ConflictException(tool.getToolId(),toolLib.getToolId()); 
        }

        try {
            analysisToolRepository.save(tool);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return analysisToolRepository.findOne(tool.getToolId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeAnalysisTool(@PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        if (!analysisToolRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        analysisToolRepository.delete(id);
    } 
  
}
