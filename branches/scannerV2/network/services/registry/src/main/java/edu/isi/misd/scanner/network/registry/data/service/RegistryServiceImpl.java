package edu.isi.misd.scanner.network.registry.data.service;

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisTool;
import edu.isi.misd.scanner.network.registry.data.domain.DataSetDefinition;
import edu.isi.misd.scanner.network.registry.data.domain.DataSetInstance;
import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import edu.isi.misd.scanner.network.registry.data.repository.*;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Service("registryService")
public class RegistryServiceImpl implements RegistryService 
{

    @Autowired
    private DataSetDefinitionRepository dataSetDefinitionRepository;
    @Autowired
    private ToolLibraryRepository toolLibraryRepository;   

    
    @Override
    @Transactional
    public ToolLibrary saveToolLibrary(ToolLibrary library) 
    {
        List<AnalysisTool> toolList = library.getAnalysisTools();
        if ((toolList != null) && (!toolList.isEmpty())) {
            library.setAnalysisTools(null);
            toolLibraryRepository.save(library);              
            for (AnalysisTool tool : toolList) {
                tool.setToolParentLibrary(library);
            }
            library.setAnalysisTools(toolList);            
        }
        return toolLibraryRepository.save(library);
    } 
    
    @Override
    @Transactional
    public DataSetDefinition saveDataSetDefinition(DataSetDefinition dataSet) 
    {
        Set<DataSetInstance> dataSetInstances = dataSet.getDataSetInstances();
        if ((dataSetInstances != null) && (!dataSetInstances.isEmpty())) {
            dataSet.setDataSetInstances(dataSetInstances);
            dataSetDefinitionRepository.save(dataSet);              
            for (DataSetInstance instance : dataSetInstances) {
                instance.setDataSetDefinition(dataSet);
            }
            dataSet.setDataSetInstances(dataSetInstances);            
        }
        return dataSetDefinitionRepository.save(dataSet);
    }     
}
