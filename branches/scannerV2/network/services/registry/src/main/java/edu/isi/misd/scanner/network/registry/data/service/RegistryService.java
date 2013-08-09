package edu.isi.misd.scanner.network.registry.data.service;

import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import java.util.List;

/**
 *
 */
public interface RegistryService 
{
    public ToolLibrary getToolLibrary(Integer ID);
    public List<ToolLibrary> getToolLibraries();    
}
