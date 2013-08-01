
package edu.isi.misd.scanner.network.worker.webapp;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ResultsReleaseDelegate implements JavaDelegate 
{  
    public static final String ID = "id";    
    public static final String APPROVED = "approved";
    public static final String FILE_PATH = "filePath";
    public static final String HOLDING_PATH = "holdingPath";

    protected static final Logger log = 
      LoggerFactory.getLogger(ResultsReleaseDelegate.class);
  
    @Override
    public void execute(DelegateExecution execution) throws Exception 
    {
        if (!execution.hasVariable(ID)) {
            throw new Exception(
                "Variable not present: " + ID);            
        }
        
        if (!execution.hasVariable(APPROVED)) {
            throw new Exception(
                "Variable not present: " + APPROVED);            
        }
        
        if (!execution.hasVariable(FILE_PATH)) {
            throw new Exception(
                "Variable not present: " + FILE_PATH);            
        }
        
        if (!execution.hasVariable(HOLDING_PATH)) {
            throw new Exception(
                "Variable not present: " + HOLDING_PATH);            
        }
        
        File outputFile = 
            new File((String)execution.getVariable(FILE_PATH));       
        File holdingFile = 
            new File((String) execution.getVariable(HOLDING_PATH));
        
        String id = (String)execution.getVariable(ID);
        String approved = (String)execution.getVariable(APPROVED);
        if (!Boolean.parseBoolean(approved)) {
            try {
                FileUtils.forceDelete(holdingFile);
                log.info("Release approval rejected for request [" + id + "]." + 
                         "  The following file will be deleted: " +
                         holdingFile.getCanonicalPath());
            } catch (Exception e) {
                log.warn("Could not delete output file from holding directory: " + 
                          holdingFile.getCanonicalPath());  
                throw e;
            }
            return;
        }
        
        if (outputFile.exists()) {
            log.info(
                "The output file " + outputFile.getCanonicalPath() + 
                " already exisits.  The file will be deleted.");
            try {
                FileUtils.forceDelete(outputFile);
            } catch (Exception e) {
                log.warn("Could not delete output file: " + 
                          outputFile.getCanonicalPath());  
                throw e;
            }
        }
        
        try {
            FileUtils.moveFile(holdingFile,outputFile);
            log.info("Release approval accepted for request [" + id + "]." + 
                     "  The results file was moved from " +
                     holdingFile.getCanonicalPath() + " to " +
                     outputFile.getCanonicalPath());            
        } catch (Exception e) {
            log.warn(
                "Unable to move file " + holdingFile.getCanonicalPath() + 
                " to " + outputFile.getCanonicalPath()); 
            throw e;
        }
    }  
}