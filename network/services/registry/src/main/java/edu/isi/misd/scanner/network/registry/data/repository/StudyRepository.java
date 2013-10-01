package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.Study;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 */

public interface StudyRepository 
    extends CrudRepository<Study, Integer> 
{
    Study findByStudyName(String studyName);
          
    @Query("SELECT DISTINCT s FROM UserRole ur " + 
           "JOIN ur.studyRole r JOIN r.study s " +
           "WHERE ur.user.userName = :userName " +
           "ORDER BY s.studyId")
    List<Study> findStudiesForUserName( @Param("userName")String userName);   
}
