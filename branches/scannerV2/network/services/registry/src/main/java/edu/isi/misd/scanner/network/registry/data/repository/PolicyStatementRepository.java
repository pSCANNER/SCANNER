package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.PolicyStatement;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface PolicyStatementRepository 
    extends CrudRepository<PolicyStatement, Integer> 
{    
   
    @Query("select s.* from PolicyStatement s " + 
           "join PolicyStatusType t on " + 
           "s.PolicyStatusTypeId = t.PolicyStatusTypeId " + 
           "where t.PolicyStatusTypeName = 'active'")
    List<PolicyStatement> getActivePolicy();
}
