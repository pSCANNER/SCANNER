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
    @Query("SELECT DISTINCT i from DataSetInstance i " +  
           "JOIN i.dataSetDefinition d JOIN i.policyStatements p " + 
           "JOIN p.role r JOIN r.userRoles ur JOIN ur.user u " +
           "WHERE d.dataSetDefinitionId = :dataSetId " + 
           "AND u.userName = :userName " +
           "AND p.policyStatusType.policyStatusTypeName = 'active'")
    List<DataSetInstance> findDataSetInstancesForDataSetIdAndUserName(
        @Param("dataSetId")Integer dataSetId,
        @Param("userName")String userName); 

    @Query("SELECT DISTINCT i from DataSetInstance i " +  
           "JOIN i.dataSetDefinition d JOIN i.policyStatements p " + 
           "JOIN p.role r JOIN r.userRoles ur JOIN ur.user u " +
           "WHERE d.dataSetName = :dataSetName " + 
           "AND u.userName = :userName " +
           "AND p.policyStatusType.policyStatusTypeName = 'active'")
    List<DataSetInstance> findDataSetInstancesForDataSetNameAndUserName(
        @Param("dataSetName")String dataSetName,
        @Param("userName")String userName); 
}
