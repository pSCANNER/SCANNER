package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisPolicyStatement;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface AnalysisPolicyStatementRepository 
    extends CrudRepository<AnalysisPolicyStatement, Integer> 
{    
   
    @Query("SELECT p from AnalysisPolicyStatement p " + 
           "WHERE p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<AnalysisPolicyStatement> getActivePolicy();
}
