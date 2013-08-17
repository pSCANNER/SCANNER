package edu.isi.misd.scanner.network.registry.data.service;

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisTool;
import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import edu.isi.misd.scanner.network.registry.data.repository.*;
import java.util.List;
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
    private ScannerUserRepository scannerUserRepository;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private DataSetDefinitionRepository dataSetDefinitionRepository;
    @Autowired
    private DataSetInstanceRepository dataSetInstanceRepository;
    @Autowired
    private DuaRepository duaStudyRepository;
    @Autowired
    private PolicyStatusTypeRepository policyRegistryRepository;
    @Autowired
    private SourceDataWarehouseRepository sourceDataWarehouseRepository;
    @Autowired
    private AnalysisToolRepository analysisToolRepository;   
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
}
