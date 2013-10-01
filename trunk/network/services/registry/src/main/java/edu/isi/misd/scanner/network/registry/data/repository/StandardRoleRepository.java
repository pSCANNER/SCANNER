package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.StandardRole;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 *
 */
public interface StandardRoleRepository 
    extends CrudRepository<StandardRole, Integer> 
{
    StandardRole findByStandardRoleName(String roleName);
}
