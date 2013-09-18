package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisPolicyStatement;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 */
public interface AnalysisPolicyStatementRepository 
    extends CrudRepository<AnalysisPolicyStatement, Integer> 
{               
    @Query("SELECT p FROM AnalysisPolicyStatement p " +
           "JOIN p.studyRole r JOIN r.userRoles ur JOIN ur.user u " +
           "WHERE u.userName = :userName " + 
           "AND p.dataSetInstance.dataSetInstanceId = :instanceId " +
           "AND p.analysisTool.toolId = :toolId " + 
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<AnalysisPolicyStatement> 
        findAnalysisPolicyStatementByUserNameAndInstanceIdAndToolId(
            @Param("userName") String userName,
            @Param("instanceId") Integer instanceId,
            @Param("toolId") Integer toolId); 
}
