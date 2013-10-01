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
    @Query("SELECT DISTINCT p FROM StudyPolicyStatement p " +
           "JOIN p.study st JOIN st.studyRequestedSites rs JOIN rs.site s " +
           "JOIN s.sitePolicies sp JOIN sp.studyRole r JOIN r.scannerUsers u " + 
           "WHERE u.userName = :userName " + 
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<StudyPolicyStatement>
        findByUserName(@Param("userName") String userName);
    
    @Query("SELECT DISTINCT p FROM StudyPolicyStatement p " +
           "JOIN p.study st JOIN st.studyRequestedSites rs JOIN rs.site s " +
           "JOIN s.sitePolicies sp JOIN sp.studyRole r JOIN r.scannerUsers u " + 
           "WHERE st.studyId = :studyId "  + 
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<StudyPolicyStatement>
        findByStudyId(@Param("studyId") Integer studyId);
    
    @Query("SELECT DISTINCT p FROM StudyPolicyStatement p " +
           "JOIN p.study st JOIN st.studyRequestedSites rs JOIN rs.site s " +
           "JOIN s.sitePolicies sp JOIN sp.studyRole r JOIN r.scannerUsers u " + 
           "WHERE u.userName = :userName " + 
           "AND st.studyId = :studyId " + 
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<StudyPolicyStatement>
        findByUserNameAndStudyId(
            @Param("userName") String userName,
            @Param("studyId") Integer studyId);
    
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
