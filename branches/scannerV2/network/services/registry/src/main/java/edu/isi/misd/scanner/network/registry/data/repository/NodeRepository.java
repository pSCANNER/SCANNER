package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.Node;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface NodeRepository 
    extends CrudRepository<Node, Integer> 
{
    List<Node> findByIsMasterTrue();
    List<Node> findByIsMasterFalse();    
}
