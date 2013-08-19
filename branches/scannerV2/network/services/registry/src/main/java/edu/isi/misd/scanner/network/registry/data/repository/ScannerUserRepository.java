package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.ScannerUser;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface ScannerUserRepository 
    extends CrudRepository<ScannerUser, Integer> 
{
    ScannerUser findByUserName(String userName);
}
