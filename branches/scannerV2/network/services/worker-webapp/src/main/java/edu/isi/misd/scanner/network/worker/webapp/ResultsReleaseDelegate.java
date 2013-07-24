
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
    public static final String FILE_PATH = "filePath";
    public static final String HOLDING_PATH = "holdingPath";

    protected static final Logger log = 
      LoggerFactory.getLogger(ResultsReleaseDelegate.class);
  
    @Override
    public void execute(DelegateExecution execution) throws Exception 
    {
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
        
        if (outputFile.exists()) {
            log.warn(
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
        } catch (Exception e) {
            log.warn(
                "Unable to move file " + holdingFile.getCanonicalPath() + 
                " to " + outputFile.getCanonicalPath()); 
            throw e;
        }
    }  
}