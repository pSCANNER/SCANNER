package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 
 */
@Controller
public class ToolLibraryController 
{
    private final RegistryService registryService;

    @Autowired
    public ToolLibraryController(RegistryService service) {
      Assert.notNull(service, "RegistryService must not be null!");
      registryService = service;
    }
    
	@RequestMapping(value = "/libraries", method = RequestMethod.GET)
	public @ResponseBody List<ToolLibrary> getToolLibraries() 
    {
        return registryService.getToolLibraries();
	}
    
    @RequestMapping(value = "/libraries/{id}", method = RequestMethod.GET)
    public @ResponseBody ToolLibrary getToolLibrary(
        @PathVariable("id") Integer id) 
    {
      // Do null check for id
      ToolLibrary toolLib = registryService.getToolLibrary(id);
      
      // Do null check for toolLib
      return toolLib;
    }    
}
