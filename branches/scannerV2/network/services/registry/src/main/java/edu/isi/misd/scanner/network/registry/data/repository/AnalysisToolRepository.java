package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisTool;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 */

public interface AnalysisToolRepository 
    extends CrudRepository<AnalysisTool, Integer> 
{
    @Query("SELECT DISTINCT t FROM AnalysisTool t " + 
           "JOIN t.studyPolicyStatements p " +
           "JOIN p.studyRole r JOIN r.userRoles ur " +
           "WHERE ur.user.userName = :userName " +
           "AND p.study.studyName = :studyName " +
           "AND p.dataSetDefinition.dataSetName = :dataSetName " +
           "AND t.toolParentLibrary.libraryId = :libraryId " +        
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<AnalysisTool> findAnalysisToolByStudyPolicyStatement(
        @Param("userName") String userName,
        @Param("studyName") String studyName,
        @Param("dataSetName") String dataSetName,
        @Param("libraryId") Integer libraryId);
}
