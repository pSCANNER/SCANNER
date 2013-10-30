/*  
 * Copyright 2013 University of Southern California 
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *  
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */ 
package edu.isi.misd.scanner.network.registry.data.repository; 

import edu.isi.misd.scanner.network.registry.data.domain.DataSetInstance;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Mike D'Arcy 
 */
public interface DataSetInstanceRepository 
    extends CrudRepository<DataSetInstance, Integer> 
{    
    List<DataSetInstance> 
        findByNodeSiteSitePoliciesStudyRoleUserRolesUserUserName(
            String userName); 
    
    @Query("SELECT DISTINCT i FROM DataSetInstance i " +  
           "JOIN i.analysisPolicyStatements p " + 
           "JOIN p.studyRole r JOIN r.study s " + 
           "JOIN r.userRoles ur JOIN ur.user u " +
           "WHERE s.studyId = :studyId " +        
           "AND u.userName = :userName " +
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<DataSetInstance> 
        findByStudyIdAndUserNameFilteredByAnalysisPolicy(
            @Param("studyId")Integer studyId,        
            @Param("userName")String userName);   
    
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
