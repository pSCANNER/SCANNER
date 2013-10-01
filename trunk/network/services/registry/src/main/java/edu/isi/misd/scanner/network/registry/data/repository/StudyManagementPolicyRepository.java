package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.StudyManagementPolicy;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */
public interface StudyManagementPolicyRepository 
    extends CrudRepository<StudyManagementPolicy, Integer> 
{
    List<StudyManagementPolicy> findByStudyStudyId(Integer studyId);
    List<StudyManagementPolicy> findByStudyRoleRoleId(Integer roleId);
    List<StudyManagementPolicy> 
        findByStudyRoleScannerUsersUserId(Integer userId);      
    List<StudyManagementPolicy> 
        findByStudyStudyIdAndStudyRoleScannerUsersUserId(
            Integer studyId, Integer userId);   
    List<StudyManagementPolicy> 
        findByStudyRoleScannerUsersUserName(String userName);      
    List<StudyManagementPolicy> 
        findByStudyStudyIdAndStudyRoleScannerUsersUserName(
            Integer studyId, String userName);      
}
