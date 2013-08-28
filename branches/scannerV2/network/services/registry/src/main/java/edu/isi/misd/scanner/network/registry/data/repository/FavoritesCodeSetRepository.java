package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.FavoritesCodeSet;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */
public interface FavoritesCodeSetRepository 
    extends CrudRepository<FavoritesCodeSet, Integer> 
{
    @Query("SELECT f FROM FavoritesCodeSet f ORDER BY conceptName ASC")
    List<FavoritesCodeSet> findAllOrderByConceptNameAsc(); 
    
    List<FavoritesCodeSet> 
        findByConceptNameIgnoreCaseContainingOrderByConceptNameAsc(
            String searchTerm);       
}
