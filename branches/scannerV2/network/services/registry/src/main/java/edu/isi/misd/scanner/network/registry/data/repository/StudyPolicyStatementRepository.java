package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.StudyPolicyStatement;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 */
public interface StudyPolicyStatementRepository 
    extends CrudRepository<StudyPolicyStatement, Integer> 
{  
    @Query("SELECT p FROM StudyPolicyStatement p " +
           "WHERE p.study.studyId = :studyId " + 
           "AND p.dataSetDefinition.dataSetDefinitionId = :dataSetId " +
           "AND p.analysisTool.toolId = :toolId " + 
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<StudyPolicyStatement> 
        findStudyPolicyStatementByStudyIdAndDataSetIdAndToolId(
            @Param("studyId") Integer studyId,
            @Param("dataSetId") Integer dataSetId,
            @Param("toolId") Integer toolId);     
}
