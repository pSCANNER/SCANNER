package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.DataSetVariableMetadata;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */
public interface DataSetVariableMetadataRepository 
    extends CrudRepository<DataSetVariableMetadata, Integer> 
{   
    List<DataSetVariableMetadata> 
        findByDataSetDefinitionDataSetDefinitionId(Integer dataSetDefinitionId);
      
}
