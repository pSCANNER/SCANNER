package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.UserRole;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface UserRoleRepository 
    extends CrudRepository<UserRole, Integer> 
{
   List<UserRole> findByUserUserId(Integer userId);  
   List<UserRole> findByStudyRoleStudyStudyId(Integer studyId);       
   List<UserRole> findByUserUserIdAndStudyRoleStudyStudyId(
       Integer userId, Integer studyId);     
}
