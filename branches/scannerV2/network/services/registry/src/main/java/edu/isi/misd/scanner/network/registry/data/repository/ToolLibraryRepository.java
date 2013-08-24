package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 */
public interface ToolLibraryRepository 
    extends CrudRepository<ToolLibrary, Integer> 
{   
    @Query("SELECT DISTINCT l FROM ToolLibrary l " + 
           "JOIN l.analysisTools a JOIN a.studyPolicyStatements p " +
           "JOIN p.role r JOIN r.userRoles ur " +
           "WHERE ur.user.userName = :userName " +
           "AND p.study.studyName = :studyName " +
           "AND p.dataSetDefinition.dataSetName = :dataSetName " +
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<ToolLibrary> findToolLibraryByStudyPolicyStatement(
        @Param("userName")String userName,
        @Param("studyName") String studyName,
        @Param("dataSetName")String dataSetName);
}
