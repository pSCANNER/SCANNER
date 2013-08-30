package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.DataSetVariableMetadata;
import edu.isi.misd.scanner.network.registry.data.repository.DataSetVariableMetadataRepository;
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
public class DataSetVariableMetadataController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(DataSetVariableMetadataController.class.getName());

    public static final String REQUEST_PARAM_DATASET_ID = "dataSetId";
    public static final String REQUEST_PARAM_DATASET_NAME = "dataSetName"; 
     
    @Autowired
    private DataSetVariableMetadataRepository dataSetVariableMetadataRepository;   
    
	@RequestMapping(value = "/variables", method = RequestMethod.GET)
	public @ResponseBody List<DataSetVariableMetadata> getDataSetVariableMetadatas(
           @RequestParam Map<String, String> paramMap)        
    {
        if (!paramMap.isEmpty()) 
        {
            String dataSetId = paramMap.remove(REQUEST_PARAM_DATASET_ID);
            String dataSetName = paramMap.remove(REQUEST_PARAM_DATASET_NAME);            
            if (!paramMap.isEmpty()) {
                throw new BadRequestException(paramMap.keySet());
            }
            if (dataSetId != null) {             
                return
                    dataSetVariableMetadataRepository.
                        findByDataSetDefinitionDataSetDefinitionId(
                            validateIntegerParameter(
                                REQUEST_PARAM_DATASET_ID, dataSetId));
            }
            if (dataSetName != null) {            
                return
                    dataSetVariableMetadataRepository.
                        findByDataSetDefinitionDataSetName(dataSetName);
            }            
            if ((dataSetId == null) && (dataSetName == null)) {
                throw new BadRequestException(
                    "Required parameter missing, must provide either " + 
                    REQUEST_PARAM_DATASET_ID + " or " + 
                    REQUEST_PARAM_DATASET_NAME);                
            }        
        }

        List<DataSetVariableMetadata> dataSetVariableMetadata = 
            new ArrayList<DataSetVariableMetadata>();            
        Iterator iter = dataSetVariableMetadataRepository.findAll().iterator();
        CollectionUtils.addAll(dataSetVariableMetadata, iter);            
        return dataSetVariableMetadata;                     
	}
    
    @RequestMapping(value = "/variables", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody DataSetVariableMetadata createDataSetVariableMetadata(
           @RequestBody DataSetVariableMetadata dataSetVariableMetadata) 
    {
        try {
            dataSetVariableMetadataRepository.save(dataSetVariableMetadata);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return dataSetVariableMetadataRepository.findOne(
            dataSetVariableMetadata.getDataSetVariableMetadataId());
    }  
    
    @RequestMapping(value = "/variables/{id}", method = RequestMethod.GET)
    public @ResponseBody DataSetVariableMetadata getDataSetVariableMetadata(
           @PathVariable("id") Integer id) 
    {
        DataSetVariableMetadata foundDataSetVariableMetadata = 
            dataSetVariableMetadataRepository.findOne(id);

        if (foundDataSetVariableMetadata == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundDataSetVariableMetadata;
    }  
    
    @RequestMapping(value = "/variables/{id}", method = RequestMethod.PUT)
    public @ResponseBody DataSetVariableMetadata updateDataSetVariableMetadata(
           @PathVariable("id") Integer id, 
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
        Integer updateID = dataSetVariableMetadata.getDataSetVariableMetadataId();
        if (updateID == null) {
            dataSetVariableMetadata.setDataSetVariableMetadataId(id);
        } else if (!dataSetVariableMetadata.getDataSetVariableMetadataId().equals(
                    foundDataSetVariableMetadata.getDataSetVariableMetadataId())) {
            throw new ConflictException(
                "Update failed: specified object ID (" + 
                dataSetVariableMetadata.getDataSetVariableMetadataId() + 
                ") does not match referenced ID (" + 
                foundDataSetVariableMetadata.getDataSetVariableMetadataId() + ")"); 
        }
        // ok, good to go
        try {
            dataSetVariableMetadataRepository.save(dataSetVariableMetadata);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return dataSetVariableMetadataRepository.findOne(
            dataSetVariableMetadata.getDataSetVariableMetadataId());
    }     
    
    @RequestMapping(value = "/variables/{id}", method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeDataSetVariableMetadata(@PathVariable("id") Integer id) 
    {
        if (!dataSetVariableMetadataRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        dataSetVariableMetadataRepository.delete(id);
    } 
  
}
