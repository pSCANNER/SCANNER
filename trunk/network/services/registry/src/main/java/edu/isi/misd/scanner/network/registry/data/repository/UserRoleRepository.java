/*  
 * Copyright 2013 University of Southern California 
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *  
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */ 
package edu.isi.misd.scanner.network.registry.data.repository; 

import edu.isi.misd.scanner.network.registry.data.domain.UserRole;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *  @author Mike D'Arcy 
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
   List<UserRole> findByStudyRoleStudyStudyIdAndStudyRoleSitePoliciesSiteSiteId(
       Integer studyId, Integer siteId);    
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
