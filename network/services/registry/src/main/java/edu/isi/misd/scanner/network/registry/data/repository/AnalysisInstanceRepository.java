package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisInstance;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 */

public interface AnalysisInstanceRepository 
    extends CrudRepository<AnalysisInstance, Integer> 
{
    List<AnalysisInstance> 
        findByStudyStudyId(@Param("studyId")Integer studyId);
    
    @Query("SELECT DISTINCT i FROM AnalysisInstance i " +  
           "JOIN i.study s JOIN s.studyRoles r " + 
           "JOIN r.userRoles ur JOIN ur.user u " +
           "WHERE s.studyId = :studyId " +        
           "AND u.userName = :userName ")
    List<AnalysisInstance> 
        findByStudyIdAndUserNameFilteredByStudyRole(
            @Param("studyId")Integer studyId,        
            @Param("userName")String userName); 
}
