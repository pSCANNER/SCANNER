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
           "WHERE s.studyId = :studyId " +
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<DataSetDefinition> findDataSetsForStudyId(
        @Param("studyId")Integer studyId);
    
 
    @Query("SELECT DISTINCT d FROM DataSetDefinition d " +  
           "JOIN d.studyPolicyStatements p JOIN p.study s " + 
           "JOIN p.studyRole r JOIN r.userRoles ur JOIN ur.user u " +
           "WHERE s.studyId = :studyId AND u.userName = :userName " +
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<DataSetDefinition> findDataSetsForStudyIdAndUserName(
        @Param("studyId")Integer studyId,
        @Param("userName")String userName);    
}
