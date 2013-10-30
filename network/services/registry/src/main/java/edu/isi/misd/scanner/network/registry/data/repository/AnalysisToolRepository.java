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

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisTool;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *  @author Mike D'Arcy 
 */
public interface AnalysisToolRepository 
    extends CrudRepository<AnalysisTool, Integer> 
{
    @Query("SELECT DISTINCT t FROM AnalysisTool t " + 
           "JOIN t.studyPolicyStatements p " +
           "JOIN p.studyRole r JOIN r.userRoles ur " +
           "WHERE ur.user.userName = :userName " +
           "AND p.study.studyId = :studyId " +
           "AND p.dataSetDefinition.dataSetDefinitionId = :dataSetId " +
           "AND t.toolParentLibrary.libraryId = :libraryId " +        
           "AND p." + QueryConstants.ACTIVE_POLICY_CHECK)
    List<AnalysisTool> findAnalysisToolByStudyPolicyStatement(
        @Param("userName") String userName,
        @Param("studyId") Integer studyId,
        @Param("dataSetId") Integer dataSetId,
        @Param("libraryId") Integer libraryId);
}
