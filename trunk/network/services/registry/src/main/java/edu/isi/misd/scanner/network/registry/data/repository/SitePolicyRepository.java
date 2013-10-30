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

import edu.isi.misd.scanner.network.registry.data.domain.SitePolicy;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *  @author Mike D'Arcy 
 */
public interface SitePolicyRepository 
    extends CrudRepository<SitePolicy, Integer> 
{
    List<SitePolicy> findBySiteSiteId(Integer siteId);
    List<SitePolicy> findByStudyRoleStudyStudyId(Integer studyId);    
    List<SitePolicy> findByStudyRoleRoleId(Integer roleId);

    @Query("SELECT DISTINCT sp FROM SitePolicy sp " + 
           "JOIN sp.studyRole r JOIN r.userRoles ur JOIN ur.user u " +
           "WHERE u.userName = :userName ")    
    List<SitePolicy> findByUserName(@Param("userName") String userName);
    
    @Query("SELECT DISTINCT sp FROM SitePolicy sp " + 
           "JOIN sp.site s JOIN sp.studyRole r " + 
           "JOIN r.userRoles ur JOIN ur.user u " +
           "WHERE s.siteId = :siteId AND u.userName = :userName ")    
    List<SitePolicy> findBySiteIdAndUserName(
        @Param("siteId") Integer siteId, @Param("userName") String userName);
}
