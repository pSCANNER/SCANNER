package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.SitePolicy;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
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
