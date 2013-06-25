package edu.isi.misd.scanner.network.base;

/**
 * Shared constants used by both the Master and Worker nodes of the network.
 */
public class BaseConstants 
{
    /**
     *  The header name for a transaction ID of a network request
     */
    public static final String ID               = "id";
    /**
     *  The header name for a timestamp value
     */
    public static final String TIMESTAMP        = "timestamp";
    /**
     *  The header name for the list of targets that the network request will be issued against
     */
    public static final String TARGETS          = "targets";
    /**
     *  The header name for the source of a response
     */
    public static final String SOURCE           = "source"; 
    /**
     *  The header name for the data source parameter
     */
    public static final String DATASOURCE       = "datasource";
    
    /**
     *  Status string for a completed transaction
     */
    public static final String STATUS_COMPLETE  = "complete";
    /**
     *  Status string for a canceled transaction*
     */
    public static final String STATUS_CANCELED  = "canceled";
    /**
     *  Status string for an error transaction result
     */
    public static final String STATUS_ERROR     = "error"; 
    
    /**
     *  The property name of master node data input directory
     */
    public static final String MASTER_INPUT_DIR_PROPERTY =
        "{{master.inputBaseDir}}";    
    /**
     *  The property name of master node data output directory
     */
    public static final String MASTER_OUTPUT_DIR_PROPERTY = 
        "{{master.outputBaseDir}}";  
    /**
     *  The property name of worker node data input directory
     */
    public static final String WORKER_INPUT_DIR_PROPERTY = 
        "{{worker.inputBaseDir}}";    
    /**
     *  The property name of worker node data input directory
     */
    public static final String WORKER_OUTPUT_DIR_PROPERTY = 
        "{{worker.outputBaseDir}}";  

    /**
     *  The property name of the node's Site ID
     */
    public static final String SITE_ID_PROPERTY = 
        "{{network.siteID}}";  
    /**
     *  The property name of the node's Site Name
     */
    public static final String SITE_NAME_PROPERTY = 
        "{{network.siteName}}";    
    /**
     *  The property name of the node's Site Description
     */
    public static final String SITE_DESC_PROPERTY = 
        "{{network.siteDescription}}";         
}
