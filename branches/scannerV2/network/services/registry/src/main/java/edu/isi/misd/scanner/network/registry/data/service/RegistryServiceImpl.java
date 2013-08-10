package edu.isi.misd.scanner.network.registry.data.service;

import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import edu.isi.misd.scanner.network.registry.data.repository.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
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
    public List<ToolLibrary> getToolLibraries()
    {
        List<ToolLibrary> toolLibraries = new ArrayList<ToolLibrary>();
        Iterator iter = toolLibraryRepository.findAll().iterator();
        CollectionUtils.addAll(toolLibraries, iter);

        return toolLibraries;        
    }

    @Override
    @Transactional    
    public ToolLibrary getToolLibrary(Integer ID) {
        return toolLibraryRepository.findOne(ID);
    }
}
