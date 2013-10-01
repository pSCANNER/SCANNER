package edu.isi.misd.scanner.network.registry.data.service;

/**
 *
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
