package edu.isi.misd.scanner.network.base;

/**
 *
 */
public class BaseConstants 
{
    public static final String ID               = "id";
    public static final String TIMESTAMP        = "timestamp";
    public static final String TARGETS          = "targets";
    public static final String SOURCE           = "source"; 
    public static final String DATASOURCE       = "datasource";
    
    public static final String STATUS_COMPLETE  = "complete";
    public static final String STATUS_CANCELED  = "canceled";
    public static final String STATUS_ERROR     = "error"; 
    
    public static final String MASTER_INPUT_DIR_PROPERTY =
        "{{master.inputBaseDir}}";    
    public static final String MASTER_OUTPUT_DIR_PROPERTY = 
        "{{master.outputBaseDir}}";  
    public static final String WORKER_INPUT_DIR_PROPERTY = 
        "{{worker.inputBaseDir}}";    
    public static final String WORKER_OUTPUT_DIR_PROPERTY = 
        "{{worker.outputBaseDir}}";  

    public static final String SITE_ID_PROPERTY = 
        "{{network.siteID}}";  
    public static final String SITE_NAME_PROPERTY = 
        "{{network.siteName}}";    
    public static final String SITE_DESC_PROPERTY = 
        "{{network.siteDescription}}";         
}
