package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.DataSetDefinition;
import edu.isi.misd.scanner.network.registry.data.repository.DataSetDefinitionRepository;
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
 * 
 */
@Controller
public class DataSetDefinitionController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(DataSetDefinitionController.class.getName());

    public static final String BASE_PATH = "/datasets";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";
    public static final String REQUEST_PARAM_USER_NAME = "userName";
     
    @Autowired
    private DataSetDefinitionRepository dataSetDefinitionRepository;   
    
    @Autowired    
    private RegistryService registryService;   
    
	@RequestMapping(value = BASE_PATH, 
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<DataSetDefinition> getDataSetDefinitions(
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
                    dataSetDefinitionRepository.
                    findDataSetsForStudyIdAndUserNameFilteredByStudyPolicy(
                            validateIntegerParameter(
                                REQUEST_PARAM_STUDY_ID,studyId),
                            userName);
            }
            if (studyId != null) {
                return 
                    dataSetDefinitionRepository.
                        findDataSetsForStudyIdFilteredByStudyPolicy(
                            validateIntegerParameter(
                                REQUEST_PARAM_STUDY_ID,studyId));
            }
            if (studyId == null) {
                throw new BadRequestException(
                    "Required parameter missing: " + REQUEST_PARAM_STUDY_ID);                
            }
        }

        List<DataSetDefinition> dataSetDefinitions = 
            new ArrayList<DataSetDefinition>();            
        Iterator iter = dataSetDefinitionRepository.findAll().iterator();
        CollectionUtils.addAll(dataSetDefinitions, iter);            
        return dataSetDefinitions;                     
	}
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody DataSetDefinition getDataSetDefinition(       
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        DataSetDefinition foundDataSet = 
            dataSetDefinitionRepository.findOne(id);

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
    public @ResponseBody DataSetDefinition createDataSetDefinition(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,         
        @RequestBody DataSetDefinition dataSet) 
    {
        try {
            registryService.saveDataSetDefinition(dataSet);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return dataSetDefinitionRepository.findOne(
            dataSet.getDataSetDefinitionId());
    }   
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody DataSetDefinition updateDataSetDefinition(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,           
        @PathVariable(ID_URL_PATH_VAR) Integer id, 
        @RequestBody DataSetDefinition dataSet) 
    {
        // find the requested resource
        DataSetDefinition foundDataSet = 
            dataSetDefinitionRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundDataSet == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = dataSet.getDataSetDefinitionId();
        if (updateID == null) {
            dataSet.setDataSetDefinitionId(id);
        } else if (!dataSet.getDataSetDefinitionId().equals(
                    foundDataSet.getDataSetDefinitionId())) {
            throw new ConflictException(
                dataSet.getDataSetDefinitionId(),
                foundDataSet.getDataSetDefinitionId()); 
        }

        try {
            registryService.saveDataSetDefinition(dataSet);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return dataSetDefinitionRepository.findOne(
            dataSet.getDataSetDefinitionId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeDataSetDefinition(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,           
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        // check that the user can perform the create
        if (!registryService.userIsSuperuser(loginName)) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SUPERUSER_ROLE_REQUIRED);
        }            
        if (!dataSetDefinitionRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        dataSetDefinitionRepository.delete(id);
    } 
  
}
