package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.DataSetDefinition;
import edu.isi.misd.scanner.network.registry.data.repository.DataSetDefinitionRepository;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
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
public class DataSetDefinitionController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(DataSetDefinitionController.class.getName());

    public static final String REQUEST_PARAM_STUDY_NAME = "studyName";
    public static final String REQUEST_PARAM_USER_NAME = "userName";
    
    @Autowired    
    private RegistryService registryService;    
    @Autowired
    private DataSetDefinitionRepository dataSetDefinitionRepository;   
    
	@RequestMapping(value = "/datasets", method = RequestMethod.GET)
	public @ResponseBody List<DataSetDefinition> getDataSetDefinitions(
           @RequestParam Map<String, String> paramMap)        
    {
        if (!paramMap.isEmpty()) 
        {
            String studyName = paramMap.remove(REQUEST_PARAM_STUDY_NAME);
            String userName = paramMap.remove(REQUEST_PARAM_USER_NAME);
            if (!paramMap.isEmpty()) {
                throw new BadRequestException(paramMap.keySet());
            }
            if ((studyName != null) && (userName != null)) {
                return
                    dataSetDefinitionRepository.
                        findDataSetsForStudyNameAndUserName(
                            studyName, userName);
            }
            if (studyName != null) {
                return 
                    dataSetDefinitionRepository.
                        findDataSetsForStudyName(studyName);
            }
            if (studyName == null) {
                throw new BadRequestException(
                    "Required parameter missing: " + REQUEST_PARAM_STUDY_NAME);                
            }
        }

        List<DataSetDefinition> dataSetDefinitions = 
            new ArrayList<DataSetDefinition>();            
        Iterator iter = dataSetDefinitionRepository.findAll().iterator();
        CollectionUtils.addAll(dataSetDefinitions, iter);            
        return dataSetDefinitions;                     
	}
    
    @RequestMapping(value = "/datasets", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody DataSetDefinition createDataSetDefinition(
           @RequestBody DataSetDefinition dataSet) 
    {
        try {
            registryService.saveDataSetDefinition(dataSet);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return dataSetDefinitionRepository.findOne(
            dataSet.getDataSetDefinitionId());
    }  
    
    @RequestMapping(value = "/datasets/{id}", method = RequestMethod.GET)
    public @ResponseBody DataSetDefinition getDataSetDefinition(
           @PathVariable("id") Integer id) 
    {
        DataSetDefinition foundDataSet = 
            dataSetDefinitionRepository.findOne(id);

        if (foundDataSet == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundDataSet;
    }  
    
    @RequestMapping(value = "/datasets/{id}", method = RequestMethod.PUT)
    public @ResponseBody DataSetDefinition updateDataSetDefinition(
           @PathVariable("id") Integer id, 
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
                "Update failed: specified object ID (" + 
                dataSet.getDataSetDefinitionId() + 
                ") does not match referenced ID (" + 
                foundDataSet.getDataSetDefinitionId() + ")"); 
        }
        // ok, good to go
        try {
            registryService.saveDataSetDefinition(dataSet);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return dataSetDefinitionRepository.findOne(
            dataSet.getDataSetDefinitionId());
    }     
    
    @RequestMapping(value = "/datasets/{id}", method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeDataSetDefinition(@PathVariable("id") Integer id) 
    {
        if (!dataSetDefinitionRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        dataSetDefinitionRepository.delete(id);
    } 
  
}