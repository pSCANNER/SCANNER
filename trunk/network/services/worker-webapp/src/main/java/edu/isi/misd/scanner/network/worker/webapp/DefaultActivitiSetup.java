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
package edu.isi.misd.scanner.network.worker.webapp; 

import java.util.List;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Mike D'Arcy 
 */
public class DefaultActivitiSetup 
{
    protected static final Logger log = 
        LoggerFactory.getLogger(DefaultActivitiSetup.class);

    protected transient ProcessEngine processEngine;
    protected transient IdentityService identityService;
    protected transient RepositoryService repositoryService;

    protected boolean createDefaultProcessDefinitions;

    public void init() 
    {
      this.identityService = processEngine.getIdentityService();
      this.repositoryService = processEngine.getRepositoryService();

      if (createDefaultProcessDefinitions) {
        log.info("Initializing default process definitions");
        initProcessDefinitions();
      }
    }

    public void setProcessEngine(ProcessEngine processEngine) {
      this.processEngine = processEngine;
    }


    public void setCreateDefaultProcessDefinitions(boolean createDefaultProcessDefinitions) {
      this.createDefaultProcessDefinitions = createDefaultProcessDefinitions;
    }
  
    protected void initProcessDefinitions() 
    {
        String reportDeploymentName = "Default reports";
        List<Deployment>deploymentList = repositoryService.createDeploymentQuery().deploymentName(reportDeploymentName).list();
        if (deploymentList == null || deploymentList.isEmpty()) {
          repositoryService.createDeployment()
            .name(reportDeploymentName)
            .addClasspathResource("edu/isi/misd/scanner/network/worker/webapp/process/reports/taskDurationForProcessDefinition.bpmn20.xml")
            .addClasspathResource("edu/isi/misd/scanner/network/worker/webapp/process/reports/processInstanceOverview.bpmn20.xml")
            .addClasspathResource("edu/isi/misd/scanner/network/worker/webapp/process/reports/employeeProductivity.bpmn20.xml")
            .deploy();   
        }
    }
  
}
