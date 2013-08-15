package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import edu.isi.misd.scanner.network.registry.data.repository.ToolLibraryRepository;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
import edu.isi.misd.scanner.network.registry.web.errors.ConflictException;
import edu.isi.misd.scanner.network.registry.web.errors.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    
    @Autowired    
    private RegistryService registryService;
    @Autowired
    private ToolLibraryRepository toolLibraryRepository;   
    
	@RequestMapping(value = "/libraries", method = RequestMethod.GET)
	public @ResponseBody List<ToolLibrary> getToolLibraries() 
    {
        List<ToolLibrary> toolLibraries = new ArrayList<ToolLibrary>();
        Iterator iter = toolLibraryRepository.findAll().iterator();
        CollectionUtils.addAll(toolLibraries, iter);
        
        return toolLibraries;         
	}
    
    @RequestMapping(value = "/libraries", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody ToolLibrary createToolLibrary(
           @RequestBody ToolLibrary library) 
    {
        try {
            registryService.saveToolLibrary(library);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            // throwing nested MostSpecificCause should probably be replaced
            // eventually for privacy reasons, but for now it is good for debugging
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return toolLibraryRepository.findOne(library.getLibraryId());
    }  
    
    @RequestMapping(value = "/libraries/{id}", method = RequestMethod.GET)
    public @ResponseBody ToolLibrary getToolLibrary(
           @PathVariable("id") Integer id) 
    {
        ToolLibrary toolLib = toolLibraryRepository.findOne(id);

        if (toolLib == null) {
            throw new ResourceNotFoundException(id);
        }
        return toolLib;
    }  
    
    @RequestMapping(value = "/libraries/{id}", method = RequestMethod.PUT)
    public @ResponseBody ToolLibrary updateToolLibrary(
           @PathVariable("id") Integer id, @RequestBody ToolLibrary library) 
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
                "Update failed: specified object ID (" + 
                library.getLibraryId() + 
                ") does not match referenced ID (" + 
                toolLib.getLibraryId() + ")"); 
        }
        // ok, good to go
        try {
            registryService.saveToolLibrary(library);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return toolLibraryRepository.findOne(library.getLibraryId());
    }     
    
    @RequestMapping(value = "/libraries/{id}", method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeToolLibrary(@PathVariable("id") Integer id) 
    {
        if (!toolLibraryRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        toolLibraryRepository.delete(id);
    } 
  
}
