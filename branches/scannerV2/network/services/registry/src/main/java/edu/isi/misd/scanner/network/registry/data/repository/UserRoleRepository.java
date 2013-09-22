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
   List<UserRole> findByUserUserName(String userName);  
   List<UserRole> findByStudyRoleRoleId(Integer roleId);
   List<UserRole> findByStudyRoleStudyStudyId(Integer studyId);       
   List<UserRole> findByUserUserNameAndStudyRoleStudyStudyId(
       String userName, Integer studyId); 
   List<UserRole> findByUserUserNameAndStudyRoleSitePoliciesSiteSiteId(
       String userName, Integer siteId);    
   List<UserRole> findByUserUserNameAndStudyRoleSitePoliciesSiteNodesNodeId(
       String userName, Integer nodeId); 
   List<UserRole> findByUserUserNameAndStudyRoleSitePoliciesSiteNodesDataSetInstancesDataSetInstanceId(
       String userName, Integer dataSetInstanceId); 
   
   Integer countByUserUserNameAndStudyRoleStudyStudyId(
       String userName, Integer studyId); 
   Integer countByUserUserNameAndStudyRoleSitePoliciesSiteSiteId(
       String userName, Integer siteId);    
   Integer countByUserUserNameAndStudyRoleSitePoliciesSiteNodesNodeId(
       String userName, Integer nodeId); 
   Integer countByUserUserNameAndStudyRoleSitePoliciesSiteNodesDataSetInstancesDataSetInstanceId(
       String userName, Integer dataSetInstanceId); 
}
