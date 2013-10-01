package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.DrugsCodeSet;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */
public interface DrugsCodeSetRepository 
    extends CrudRepository<DrugsCodeSet, Integer> 
{
    @Query("SELECT d FROM DrugsCodeSet d ORDER BY conceptName ASC")
    List<DrugsCodeSet> findAllOrderByConceptNameAsc(); 
    
    List<DrugsCodeSet> 
        findByConceptNameIgnoreCaseContainingOrderByConceptNameAsc(
            String searchTerm);    
}
