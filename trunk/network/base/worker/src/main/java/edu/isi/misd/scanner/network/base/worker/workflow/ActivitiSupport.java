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
package edu.isi.misd.scanner.network.base.worker.workflow; 

import java.util.Arrays;
import java.util.List;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.repository.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *  Initializes Activiti for use and sets some defaults.
 *  TODO: The user and group account defaults probably need to be read from an 
 *  external script first, then fallback to these if that script is not present.
 * 
 *  The spring bean for this class has to be loaded even if Activiti is not 
 *  used.  This is because of a Spring issue which makes conditional bean 
 *  loading difficult when trying to use only context properties to define
 *  the condition. 
 * 
 *
 *  @author Mike D'Arcy 
 */
public class ActivitiSupport implements ApplicationContextAware 
{
    protected static final Logger log = 
        LoggerFactory.getLogger(ActivitiSupport.class);

    private ApplicationContext applicationContext;
    
    protected transient ProcessEngine processEngine;
    protected transient IdentityService identityService;
    protected transient RepositoryService repositoryService;
    protected String databaseURL;
    
    protected boolean activitiSupportEnabled;  
    protected boolean createDefaultUsersAndGroups;
    protected boolean createDefaultProcessDefinitions;
    protected boolean createDefaultModels;
    protected boolean generateReportData;
    
    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.applicationContext = ac;
    }
    
    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
    }
    
    public void setActivitiSupportEnabled(boolean activitiSupportEnabled) {
        this.activitiSupportEnabled = activitiSupportEnabled;
    }

    public void setCreateDefaultUsersAndGroups(boolean createDefaultUsersAndGroups) {
        this.createDefaultUsersAndGroups = createDefaultUsersAndGroups;
    }

    public void setCreateDefaultProcessDefinitions(boolean createDefaultProcessDefinitions) {
        this.createDefaultProcessDefinitions = createDefaultProcessDefinitions;
    }
    
    public void init() 
    {
        if (!activitiSupportEnabled) {
            return;
        }

        // if we are using H2, spawn the server from this process
        if (this.databaseURL != null) {
            if (databaseURL.startsWith("jdbc:h2:tcp")) {
                log.info("Starting H2 database server for Activiti...");
                applicationContext.getBean("h2Server");
            }
        }
        
        log.info("Initializing Activiti engine");
        this.processEngine =
            applicationContext.getBean("processEngine",ProcessEngine.class);
        this.processEngine = ProcessEngines.getDefaultProcessEngine();
        this.identityService = processEngine.getIdentityService();
        this.repositoryService = processEngine.getRepositoryService();

        if (createDefaultUsersAndGroups) {
            log.info("Initializing default groups");
            initDefaultGroups();
            log.info("Initializing default users");
            initDefaultUsers();
        }

        if (createDefaultProcessDefinitions) {
            log.info("Initializing default process definitions");
            initProcessDefinitions();
        }
    }  

    protected void initDefaultGroups() 
    {
        String[] assignmentGroups = new String[] {"results-authorization"};
        for (String groupId : assignmentGroups) {
            createGroup(groupId, "assignment");
        }

        String[] securityGroups = new String[] {"user", "admin"}; 
        for (String groupId : securityGroups) {
            createGroup(groupId, "security-role");
        }
    }

    protected void createGroup(String groupId, String type) 
    {
        if (identityService.createGroupQuery().groupId(groupId).count() == 0) {
            Group newGroup = identityService.newGroup(groupId);
            newGroup.setName(groupId.substring(0, 1).toUpperCase() + groupId.substring(1));
            newGroup.setType(type);
            identityService.saveGroup(newGroup);
        }
    }

    protected void initDefaultUsers() 
    {
        createUser(
            "admin",
            "System",
            "Administrator",
            "admin",
            "root@localhost", 
            null,
            Arrays.asList("results-authorization", "user", "admin"),
            Arrays.asList("description", 
                          "The system administrator account", 
                          "contactInformation",
                          ""));
        createUser(
            "scanner",
            "Scanner",
            "User",
            "scanner",
            "scanner@localhost", 
            null,
            Arrays.asList("results-authorization", "user"),
            Arrays.asList(
                "description", 
                "The default user account", 
                "contactInformation",""));
    }

    protected void createUser(String userId,
                              String firstName,
                              String lastName,
                              String password, 
                              String email,
                              String imageResource, 
                              List<String> groups, 
                              List<String> userInfo) 
    {
        if (identityService.createUserQuery().userId(userId).count() == 0) 
        {  
            // Following data can already be set by default setup script

            User user = identityService.newUser(userId);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(password);
            user.setEmail(email);
            identityService.saveUser(user);

            if (groups != null) {
                for (String group : groups) {
                    identityService.createMembership(userId, group);
                }
            }
      }

      // Following data is not set by default setup script

      // image
      if (imageResource != null) {
        byte[] pictureBytes = 
            IoUtil.readInputStream(
                this.getClass().getClassLoader().getResourceAsStream(
                    imageResource), null);
        Picture picture = new Picture(pictureBytes, "image/jpeg");
        identityService.setUserPicture(userId, picture);
      }

      // user info
      if (userInfo != null) {
        for(int i=0; i<userInfo.size(); i+=2) {
          identityService.setUserInfo(
              userId, userInfo.get(i), userInfo.get(i+1));
        }
      }

    }

    protected void initProcessDefinitions() 
    {
      String deploymentName = "Default processes";
      List<Deployment> deploymentList = 
          repositoryService.createDeploymentQuery().deploymentName(
            deploymentName).list();
      if (deploymentList == null || deploymentList.isEmpty()) {
        repositoryService.createDeployment()
          .name(deploymentName)
          .addClasspathResource("edu/isi/misd/scanner/network/base/worker/queryResultsAuthorization.bpmn20.xml")
          .deploy();
      }
    }
  
}
