package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.StudyRequestedSite;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface StudyRequestedSiteRepository 
    extends CrudRepository<StudyRequestedSite, Integer> 
{
    List<StudyRequestedSite> findBySiteSiteId(Integer siteId);  
    List<StudyRequestedSite> findByStudyStudyId(Integer studyId);    
}
