package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import edu.isi.misd.scanner.network.registry.data.repository.ToolLibraryRepository;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


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
}
