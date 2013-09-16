package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import edu.isi.misd.scanner.network.registry.data.repository.ToolLibraryRepository;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
import static edu.isi.misd.scanner.network.registry.web.controller.BaseController.HEADER_JSON_MEDIA_TYPE;
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
public class ToolLibraryController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(ToolLibraryController.class.getName());
    
    public static final String BASE_PATH = "/libraries";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;     
    public static final String REQUEST_PARAM_USER_NAME = "userName";
    public static final String REQUEST_PARAM_STUDY_NAME = "studyName";       
    public static final String REQUEST_PARAM_DATASET_NAME = "dataSetName"; 
    
    @Autowired    
    private RegistryService registryService;
    @Autowired
    private ToolLibraryRepository toolLibraryRepository;   
    
	@RequestMapping(value = BASE_PATH,
                    method = RequestMethod.GET,
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<ToolLibrary> getToolLibraries(
           @RequestParam Map<String, String> paramMap)
    {
        Map<String,String> params = 
            validateParameterMap(
                paramMap,
                REQUEST_PARAM_USER_NAME,
                REQUEST_PARAM_STUDY_NAME,
                REQUEST_PARAM_DATASET_NAME);  
        
        if (!params.isEmpty()) 
        {
            ArrayList<String> missingParams = new ArrayList<String>();            
            String userName = params.get(REQUEST_PARAM_USER_NAME);  
            if (userName == null) {
                missingParams.add(REQUEST_PARAM_USER_NAME);
            }
            String studyName = params.get(REQUEST_PARAM_STUDY_NAME);
            if (studyName == null) {
                missingParams.add(REQUEST_PARAM_STUDY_NAME);
            }            
            String dataSetName = params.get(REQUEST_PARAM_DATASET_NAME);
            if (dataSetName == null) {
                missingParams.add(REQUEST_PARAM_DATASET_NAME);
            }            
            if ((studyName == null) || 
                (userName == null) || 
                (dataSetName == null)) 
            {
                throw new BadRequestException(
                    "Required parameter(s) missing: " + missingParams);                
            }            
            return
                toolLibraryRepository.
                    findToolLibraryByStudyPolicyStatement(
                        userName, studyName, dataSetName);
        }        
        List<ToolLibrary> toolLibraries = new ArrayList<ToolLibrary>();
        Iterator iter = toolLibraryRepository.findAll().iterator();
        CollectionUtils.addAll(toolLibraries, iter);
        
        return toolLibraries;         
	}
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody ToolLibrary createToolLibrary(
           @RequestBody ToolLibrary library) 
    {
        try {
            registryService.saveToolLibrary(library);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            // throwing nested MostSpecificCause should probably be replaced
            // eventually for privacy reasons, but for now it is good for debugging
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return toolLibraryRepository.findOne(library.getLibraryId());
    }  
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.GET,
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody ToolLibrary getToolLibrary(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        ToolLibrary toolLib = toolLibraryRepository.findOne(id);

        if (toolLib == null) {
            throw new ResourceNotFoundException(id);
        }
        return toolLib;
    }  
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody ToolLibrary updateToolLibrary(
           @PathVariable(ID_URL_PATH_VAR) Integer id, 
           @RequestBody ToolLibrary library) 
    {
        // find the requested resource
        ToolLibrary toolLib = toolLibraryRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (toolLib == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = library.getLibraryId();
        if (updateID == null) {
            library.setLibraryId(id);
        } else if (!library.getLibraryId().equals(toolLib.getLibraryId())) {
            throw new ConflictException(
                library.getLibraryId(),toolLib.getLibraryId()); 
        }

        try {
            registryService.saveToolLibrary(library);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return toolLibraryRepository.findOne(library.getLibraryId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeToolLibrary(@PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        if (!toolLibraryRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        toolLibraryRepository.delete(id);
    } 
  
}
