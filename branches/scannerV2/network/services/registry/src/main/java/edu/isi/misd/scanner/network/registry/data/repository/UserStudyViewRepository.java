package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.UserStudyView;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

/**
 *
 */

public interface UserStudyViewRepository 
    extends Repository<UserStudyView, String> 
{                                                                                                       
    UserStudyView findOne(String ID);                                                                                                                       
    Iterable<UserStudyView> findAll();
    Long count();                                                                                                                   
    boolean exists(String ID);    
}
