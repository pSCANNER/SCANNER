package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.Site;

import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface SiteRepository 
    extends CrudRepository<Site, Integer> 
{
    Site findBySiteName(String siteName);    
}
