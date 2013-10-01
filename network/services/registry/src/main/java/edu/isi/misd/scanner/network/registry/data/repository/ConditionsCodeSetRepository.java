package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.ConditionsCodeSet;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */
public interface ConditionsCodeSetRepository 
    extends CrudRepository<ConditionsCodeSet, Integer> 
{
    @Query("SELECT c FROM ConditionsCodeSet c ORDER BY conceptName ASC")
    List<ConditionsCodeSet> findAllOrderByConceptNameAsc(); 
    
    List<ConditionsCodeSet> 
        findByConceptNameIgnoreCaseContainingOrderByConceptNameAsc(
            String searchTerm);
}
