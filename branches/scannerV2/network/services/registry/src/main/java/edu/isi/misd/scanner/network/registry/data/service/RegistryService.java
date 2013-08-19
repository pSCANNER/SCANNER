package edu.isi.misd.scanner.network.registry.data.service;

import edu.isi.misd.scanner.network.registry.data.domain.DataSetDefinition;
import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import java.util.List;

/**
 *
 */
public interface RegistryService 
{
    /**
     * Creates or updates a single ToolLibrary, with optional creation of AnalysisTool child relations.
     * @param library
     */
    public ToolLibrary saveToolLibrary(ToolLibrary library);
    
    /**
     * Creates or updates a single DataSetDefinition, with optional creation of DataSetInstance child relations.
     * @param dataSet
     */
    public DataSetDefinition saveDataSetDefinition(DataSetDefinition dataSet);          
}
