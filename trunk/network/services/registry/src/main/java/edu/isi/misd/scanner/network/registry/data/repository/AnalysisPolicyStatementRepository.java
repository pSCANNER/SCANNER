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

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisPolicyStatement;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Mike D'Arcy 
 */
public interface AnalysisPolicyStatementRepository 
    extends CrudRepository<AnalysisPolicyStatement, Integer> 
{               
    @Query("SELECT p FROM AnalysisPolicyStatement p " +
           "JOIN p.studyRole r JOIN r.userRoles ur JOIN ur.user u " +
           "WHERE u.userName = :userName " + 
           "AND p.dataSetInstance.dataSetInstanceId = :instanceId " +
           "AND p.analysisTool.toolId = :toolId " + 
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<AnalysisPolicyStatement> 
        findAnalysisPolicyStatementByUserNameAndInstanceIdAndToolId(
            @Param("userName") String userName,
            @Param("instanceId") Integer instanceId,
            @Param("toolId") Integer toolId); 
}
