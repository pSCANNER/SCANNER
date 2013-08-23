package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.DataSetVariableMetadata;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 */
public interface DataSetVariableMetadataRepository 
    extends CrudRepository<DataSetVariableMetadata, Integer> 
{   
    List<DataSetVariableMetadata> 
        findByDataSetDefinitionDataSetDefinitionId(Integer dataSetDefinitionId);
    
    List<DataSetVariableMetadata> 
        findByDataSetDefinitionDataSetName(String dataSetDefinitionName);    
}
