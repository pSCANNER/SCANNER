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
package edu.isi.misd.scanner.network.registry.data.service; 

/**
 * Registry Service constants.
 *
 * @author Mike D'Arcy 
 */
public class RegistryServiceConstants 
{    
    public static final String MSG_UNKNOWN_USER_NAME = 
        "The user was not found.";
 
    public static final String MSG_NULL_VARIABLE = 
        "The required message body variable [%s] was null.";
    
    public static final String MSG_INVALID_PARAMETER_VALUE = 
        "Invalid parameter value specified: [%s].";    
    
    public static final String MSG_SUPERUSER_ROLE_REQUIRED = 
        "The operation requires that the requesting user be a superuser.";
    
    public static final String MSG_STUDY_ROLE_REQUIRED = 
        "The operation requires that the requesting user have a study role in the specified study, or be a superuser.";
    
    public static final String MSG_STUDY_MANAGEMENT_ROLE_REQUIRED = 
        "The operation requires that the requesting user have a study management role in the specified study, or be a superuser.";
    
    public static final String MSG_SITE_MANAGEMENT_ROLE_REQUIRED = 
        "The operation requires that the requesting user have a site management role for the specified site, or be a superuser.";    
}
