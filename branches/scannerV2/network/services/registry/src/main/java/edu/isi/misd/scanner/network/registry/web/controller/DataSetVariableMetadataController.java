package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.DataSetVariableMetadata;
import edu.isi.misd.scanner.network.registry.data.repository.DataSetVariableMetadataRepository;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
import edu.isi.misd.scanner.network.registry.data.service.RegistryServiceConstants;
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
public class DataSetVariableMetadataController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(DataSetVariableMetadataController.class.getName());

    public static final String BASE_PATH = "/variables";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_DATASET_ID = "dataSetId";
    public static final String REQUEST_PARAM_DATASET_NAME = "dataSetName"; 
     
    @Autowired
    private DataSetVariableMetadataRepository dataSetVariableMetadataRepository;   
    
    @Autowired
    private RegistryService registryService;
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<DataSetVariableMetadata> 
        getDataSetVariableMetadata(@RequestParam Map<String, String> paramMap)        
    {
        Map<String,String> params = 
            validateParameterMap(paramMap, REQUEST_PARAM_DATASET_ID);
        
        if (!params.isEmpty()) 
        {
            String dataSetId = params.get(REQUEST_PARAM_DATASET_ID);            
            return
                dataSetVariableMetadataRepository.
                    findByDataSetDefinitionDataSetDefinitionId(
                        validateIntegerParameter(
                            REQUEST_PARAM_DATASET_ID, dataSetId));              
        }

        List<DataSetVariableMetadata> dataSetVariableMetadata = 
            new ArrayList<DataSetVariableMetadata>();            
        Iterator iter = dataSetVariableMetadataRepository.findAll().iterator();
        CollectionUtils.addAll(dataSetVariableMetadata, iter);            
        return dataSetVariableMetadata;                     
	}
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody DataSetVariableMetadata getDataSetVariableMetadata(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        DataSetVariableMetadata foundDataSetVariableMetadata = 
            dataSetVariableMetadataRepository.findOne(id);

        if (foundDataSetVariableMetadata == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundDataSetVariableMetadata;
    } 
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody DataSetVariableMetadata createDataSetVariableMetadata(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,           
        @RequestBody DataSetVariableMetadata dataSetVariableMetadata) 
    {
        try {
            dataSetVariableMetadataRepository.save(dataSetVariableMetadata);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return dataSetVariableMetadataRepository.findOne(
            dataSetVariableMetadata.getDataSetVariableMetadataId());
    }   
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody DataSetVariableMetadata updateDataSetVariableMetadata(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,           
        @PathVariable(ID_URL_PATH_VAR) Integer id, 
        @RequestBody DataSetVariableMetadata dataSetVariableMetadata) 
    {
        // find the requested resource
        DataSetVariableMetadata foundDataSetVariableMetadata = 
            dataSetVariableMetadataRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundDataSetVariableMetadata == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = 
            dataSetVariableMetadata.getDataSetVariableMetadataId();
        if (updateID == null) {
            dataSetVariableMetadata.setDataSetVariableMetadataId(id);
        } else if (
            !dataSetVariableMetadata.getDataSetVariableMetadataId().equals(
            foundDataSetVariableMetadata.getDataSetVariableMetadataId())) 
        {
            throw new ConflictException(
                dataSetVariableMetadata.getDataSetVariableMetadataId(),
                foundDataSetVariableMetadata.getDataSetVariableMetadataId()); 
        }
  
        try {
            dataSetVariableMetadataRepository.save(dataSetVariableMetadata);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return dataSetVariableMetadataRepository.findOne(
            dataSetVariableMetadata.getDataSetVariableMetadataId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeDataSetVariableMetadata(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,           
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        // check that the user can perform the delete
        if (!registryService.userIsSuperuser(loginName)) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SUPERUSER_ROLE_REQUIRED);
        }            
        if (!dataSetVariableMetadataRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        dataSetVariableMetadataRepository.delete(id);
    } 
  
}
