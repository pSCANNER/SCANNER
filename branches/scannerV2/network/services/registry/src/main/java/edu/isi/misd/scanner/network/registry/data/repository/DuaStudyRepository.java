package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.DuaStudy;
import edu.isi.misd.scanner.network.registry.data.domain.DuaStudyPK;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface DuaStudyRepository 
    extends CrudRepository<DuaStudy, DuaStudyPK> 
{
    
}
