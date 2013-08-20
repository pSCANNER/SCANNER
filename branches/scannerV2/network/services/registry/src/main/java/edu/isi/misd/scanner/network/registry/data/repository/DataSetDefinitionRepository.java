package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.DataSetDefinition;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 */

public interface DataSetDefinitionRepository 
    extends CrudRepository<DataSetDefinition, Integer> 
{
    @Query("select d from DataSetDefinition d " +  
           "where d.originatingStudy.studyName = :studyName")
    List<DataSetDefinition> findDataSetsForStudyName(
        @Param("studyName")String studyName);
}
