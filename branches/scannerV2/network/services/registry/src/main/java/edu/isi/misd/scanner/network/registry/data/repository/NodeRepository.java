package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.Node;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */

public interface NodeRepository 
    extends CrudRepository<Node, Integer> 
{
    List<Node> findByIsMaster(Boolean isMaster);     
    List<Node> findBySiteSitePoliciesStudyRoleUserRolesUserUserName(
        String userName);
    List<Node> 
        findBySiteSitePoliciesStudyRoleUserRolesUserUserNameAndIsMaster(
            String userName, Boolean isMaster);   
}
