package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.DataSetInstance;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 */
public interface DataSetInstanceRepository 
    extends CrudRepository<DataSetInstance, Integer> 
{    
    List<DataSetInstance> 
        findByNodeSiteSitePoliciesStudyRoleUserRolesUserUserName(
            String userName);    
    
    @Query("SELECT DISTINCT i FROM DataSetInstance i " +  
           "JOIN i.dataSetDefinition d " + 
           "JOIN i.analysisPolicyStatements p " + 
           "JOIN p.studyRole r JOIN r.study s " + 
           "JOIN r.userRoles ur JOIN ur.user u " +
           "WHERE d.dataSetDefinitionId = :dataSetId " + 
           "AND s.studyId = :studyId " +        
           "AND u.userName = :userName " +
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<DataSetInstance> 
        findByDataSetIdAndStudyIdAndUserNameFilteredByAnalysisPolicy(
            @Param("dataSetId")Integer dataSetId,
            @Param("studyId")Integer studyId,        
            @Param("userName")String userName);    
 
}
