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
    /**
     * The User repository.
     */
    @Autowired
    private UsersRepository usersRepository;
    /**
     * The Study repository.
     */
    @Autowired
    private StudyRepository studyRepository;
    /**
     * The DataSetDefinition repository.
     */
    @Autowired
    private DataSetDefinitionRepository dataSetDefinitionRepository;
    /**
     * The DataSetInstance repository.
     */
    @Autowired
    private DataSetInstanceRepository dataSetInstanceRepository;
    /**
     * The DuaStudy repository.
     */
    @Autowired
    private DuaStudyRepository duaStudyRepository;
    /**
     * The PolicyRegistry repository.
     */
    @Autowired
    private PolicyRegistryRepository policyRegistryRepository;
    /**
     * The SourceDataWarehouseRepository repository.
     */
    @Autowired
    private SourceDataWarehouseRepository sourceDataWarehouseRepository;
    /**
     * The AnalysisTools repository.
     */
    @Autowired
    private AnalysisToolsRepository analysisToolsRepository;   

    /**
     * The ToolLibrary repository.
     */
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
}
