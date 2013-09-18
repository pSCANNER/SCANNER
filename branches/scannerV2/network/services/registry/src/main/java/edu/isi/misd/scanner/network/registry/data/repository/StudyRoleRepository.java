package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.StudyRole;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 *
 */
public interface StudyRoleRepository 
    extends CrudRepository<StudyRole, Integer> 
{
   List<StudyRole> findByStudyStudyId(Integer studyId);       
   List<StudyRole> findByScannerUsersUserName(String userName); 
   List<StudyRole> findByStudyStudyIdAndScannerUsersUserName(
       Integer studyId, String userName);   
}
