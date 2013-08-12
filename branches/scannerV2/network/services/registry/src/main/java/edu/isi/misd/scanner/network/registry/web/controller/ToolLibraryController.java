package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import edu.isi.misd.scanner.network.registry.data.repository.AnalysisToolRepository;
import edu.isi.misd.scanner.network.registry.data.repository.ToolLibraryRepository;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
import java.util.ArrayList;
import java.util.Iterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 
 */
@Controller
public class ToolLibraryController 
{
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
      
      // Do null check for toolLib
      return toolLib;
    }    
    
    @RequestMapping(value = "/libraries", method = RequestMethod.POST)
    public @ResponseBody ToolLibrary createToolLibrary(
        @RequestBody ToolLibrary library) 
    {
        registryService.createToolLibrary(library);
        return library;
    }        
}
