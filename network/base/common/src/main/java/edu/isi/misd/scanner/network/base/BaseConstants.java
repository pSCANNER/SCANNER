package edu.isi.misd.scanner.network.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
     *  The URL of the resource being requested
     */
    public static final String REQUEST_URL      = "requestURL";    
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
    public static final String STATUS_COMPLETE  = "The request completed successfully";
    /**
     *  Status string for an in-progress transaction*
     */
    public static final String STATUS_PROCESSING  = "The request is being processed";
    /**
     *  Status string for an held transaction result
     */
    public static final String STATUS_HELD      = "The results of this request are being held pending document release approval at the remote site."; 
    /**
     *  Status string for a completed transaction
     */
    public static final String STATUS_REJECTED  = "The publishing of the result document was rejected by an authority the remote site.";    
    /**
     *  Boolean parameter indicating async mode should be used
     */
    public static final String ASYNC            = "async";     
    /**
     *  Boolean parameter indicating results are held for authorization
     */
    public static final String RESULTS_RELEASE_AUTH_REQUIRED = "resultsReleaseAuthReq";     

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
     *  The property name of worker node data output directory
     */
    public static final String WORKER_OUTPUT_DIR_PROPERTY = 
        "{{worker.outputBaseDir}}";  

    /**
     *  The property name of worker node data output holding directory
     */
    public static final String WORKER_OUTPUT_HOLDING_DIR_PROPERTY = 
        "{{worker.outputHoldingDir}}";  
     
    /**
     *  The property name of the node's Site Name
     */
    public static final String SITE_NAME_PROPERTY = 
        "{{network.siteName}}";  
   
    /**
     *  The property name of the node's externally visible name
     */
    public static final String NODE_NAME_PROPERTY = 
        "{{network.nodeName}}";  
    
    /**
     *  The base XML namespace prefix map, used for JAXB serialization.
     */    
    public static final Map<String,String> BASE_XML_NAMESPACE_PREFIX_MAP;
    static {
        HashMap<String, String> map = new HashMap<String,String>();
        map.put("http://scanner.misd.isi.edu/network/types/base", "");
        map.put("http://scanner.misd.isi.edu/network/types/regression","regr");
        BASE_XML_NAMESPACE_PREFIX_MAP = Collections.unmodifiableMap(map);
    }    
}
