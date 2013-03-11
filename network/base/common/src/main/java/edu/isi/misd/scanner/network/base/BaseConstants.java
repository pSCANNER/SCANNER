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
    
    public static final String MASTER_INPUT_DIR     = "{{master.inputBaseDir}}";    
    public static final String MASTER_OUTPUT_DIR    = "{{master.outputBaseDir}}";  
    public static final String WORKER_INPUT_DIR     = "{{worker.inputBaseDir}}";    
    public static final String WORKER_OUTPUT_DIR    = "{{worker.outputBaseDir}}";  
    
    public static final String STATUS_COMPLETE  = "complete";
    public static final String STATUS_CANCELED  = "canceled";
    public static final String STATUS_ERROR     = "error";    
}
