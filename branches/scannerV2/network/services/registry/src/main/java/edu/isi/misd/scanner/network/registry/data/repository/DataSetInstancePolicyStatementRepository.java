package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.DataSetInstancePolicyStatement;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface DataSetInstancePolicyStatementRepository 
    extends CrudRepository<DataSetInstancePolicyStatement, Integer> 
{    
   
    @Query("SELECT p from DataSetInstancePolicyStatement p " + 
           "WHERE p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<DataSetInstancePolicyStatement> getActivePolicy();
}
