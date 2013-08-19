package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.Study;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface StudyRepository 
    extends CrudRepository<Study, Integer> 
{
    Study findByStudyName(String studyName);
}
