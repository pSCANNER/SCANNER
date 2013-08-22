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
    @Query("SELECT DISTINCT d from DataSetDefinition d " +  
           "JOIN d.studyPolicyStatements p JOIN p.study s " + 
           "WHERE s.studyName = :studyName")
    List<DataSetDefinition> findDataSetsForStudyName(
        @Param("studyName")String studyName);
    
 
    @Query("SELECT DISTINCT d from DataSetDefinition d " +  
           "JOIN d.studyPolicyStatements p JOIN p.study s " + 
           "JOIN p.role r JOIN r.userRoles ur JOIN ur.user u " +
           "WHERE s.studyName = :studyName AND u.userName = :userName")
    List<DataSetDefinition> findDataSetsForStudyNameAndUserName(
        @Param("studyName")String studyName,
        @Param("userName")String userName);    
}
