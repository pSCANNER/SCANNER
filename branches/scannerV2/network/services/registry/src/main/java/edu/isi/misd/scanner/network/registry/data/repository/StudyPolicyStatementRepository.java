package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.StudyPolicyStatement;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface StudyPolicyStatementRepository 
    extends CrudRepository<StudyPolicyStatement, Integer> 
{
    
}
