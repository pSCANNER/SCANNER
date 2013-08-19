package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.DataSetDefinition;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface DataSetDefinitionRepository 
    extends CrudRepository<DataSetDefinition, Integer> 
{
    @Query("select d from DataSetDefinition d " +  
           "where d.originatingStudy.studyName = ?1")
    List<DataSetDefinition> findDataSetsForStudyName(String studyName);
}
