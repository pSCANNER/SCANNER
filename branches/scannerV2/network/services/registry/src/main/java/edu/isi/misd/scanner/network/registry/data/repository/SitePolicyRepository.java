package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.SitePolicy;

import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface SitePolicyRepository 
    extends CrudRepository<SitePolicy, Integer> 
{
    SitePolicy findBySiteSiteName(String siteName);      
}
