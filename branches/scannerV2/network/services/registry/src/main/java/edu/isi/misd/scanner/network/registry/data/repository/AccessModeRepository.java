package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.AccessMode;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface AccessModeRepository 
    extends CrudRepository<AccessMode, Integer> 
{
    
}
