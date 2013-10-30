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

import edu.isi.misd.scanner.network.registry.data.domain.StudyManagementPolicy;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Mike D'Arcy 
 */
public interface StudyManagementPolicyRepository 
    extends CrudRepository<StudyManagementPolicy, Integer> 
{
    List<StudyManagementPolicy> findByStudyStudyId(Integer studyId);
    List<StudyManagementPolicy> findByStudyRoleRoleId(Integer roleId);
    List<StudyManagementPolicy> 
        findByStudyRoleScannerUsersUserId(Integer userId);      
    List<StudyManagementPolicy> 
        findByStudyStudyIdAndStudyRoleScannerUsersUserId(
            Integer studyId, Integer userId);   
    List<StudyManagementPolicy> 
        findByStudyRoleScannerUsersUserName(String userName);      
    List<StudyManagementPolicy> 
        findByStudyStudyIdAndStudyRoleScannerUsersUserName(
            Integer studyId, String userName);      
}
