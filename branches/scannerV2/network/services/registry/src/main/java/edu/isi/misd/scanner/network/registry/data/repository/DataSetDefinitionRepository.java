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
    @Query("SELECT DISTINCT d FROM DataSetDefinition d " +  
           "JOIN d.studyPolicyStatements p JOIN p.study s " + 
           "WHERE s.studyName = :studyName " +
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<DataSetDefinition> findDataSetsForStudyName(
        @Param("studyName")String studyName);
    
 
    @Query("SELECT DISTINCT d FROM DataSetDefinition d " +  
           "JOIN d.studyPolicyStatements p JOIN p.study s " + 
           "JOIN p.studyRole r JOIN r.userRoles ur JOIN ur.user u " +
           "WHERE s.studyName = :studyName AND u.userName = :userName " +
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<DataSetDefinition> findDataSetsForStudyNameAndUserName(
        @Param("studyName")String studyName,
        @Param("userName")String userName);    
}
