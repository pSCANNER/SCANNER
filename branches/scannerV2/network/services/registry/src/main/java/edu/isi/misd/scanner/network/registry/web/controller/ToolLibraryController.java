package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import edu.isi.misd.scanner.network.registry.data.repository.AnalysisToolRepository;
import edu.isi.misd.scanner.network.registry.data.repository.ToolLibraryRepository;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
import edu.isi.misd.scanner.network.registry.web.errors.ErrorMessage;
import edu.isi.misd.scanner.network.registry.web.errors.BadRequestException;
import edu.isi.misd.scanner.network.registry.web.errors.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    private AnalysisToolRepository analysisToolRepository;   
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
    
    @RequestMapping(value = "/libraries/{id}", method = RequestMethod.GET)
    public @ResponseBody ToolLibrary getToolLibrary(
        @PathVariable("id") Integer id) 
    {
      // Do null check for id
      ToolLibrary toolLib = toolLibraryRepository.findOne(id);
      
      if (toolLib == null) {
          throw new ResourceNotFoundException(
              "A resource could not be found matching the ID: " + id);
      }
      return toolLib;
    }    
    
    @RequestMapping(value = "/libraries", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody ToolLibrary createToolLibrary(
        @RequestBody ToolLibrary library) 
    {
        try {
            registryService.createToolLibrary(library);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            // throwing nested MostSpecificCause should probably be replaced
            // eventually for privacy reasons, but for now it is good for debugging
            throw new BadRequestException(e.getMostSpecificCause());
        }
        return library;
    }        
}
