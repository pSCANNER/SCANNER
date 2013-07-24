package edu.isi.misd.scanner.network.base.worker.processors;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.FileUtils;
import java.io.File;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 *  This class writes the results of a processing pipeline to a local file.
 */
public class BaseCacheWriteProcessor implements Processor
{
    public static final String FILE_PATH = "filePath";
    public static final String HOLDING_PATH = "holdingPath";
    
    @Override
    public void process(Exchange exchange) throws Exception 
    {
        Boolean doReleaseAuth = 
            ((Boolean)exchange.getIn().getHeader(
                BaseConstants.RESULTS_RELEASE_AUTH_REQUIRED,Boolean.class));

        if ((doReleaseAuth !=null) && (doReleaseAuth.booleanValue()==true)) 
        {
            String fileName = 
                (String)exchange.getIn().getHeader(BaseConstants.ID);              
            
            String outputDirName = 
                FileUtils.getDirPathForRequest(
                    exchange,BaseConstants.WORKER_OUTPUT_DIR_PROPERTY);              
            
            File outputFile = new File(outputDirName, fileName);        
            exchange.setProperty(FILE_PATH, outputFile.getAbsolutePath());               
          
            String holdingDirName = 
                FileUtils.getHoldingDirPathForRequest(
                    exchange,BaseConstants.WORKER_OUTPUT_HOLDING_DIR_PROPERTY); 
            
            File holdingFile = new File(holdingDirName, fileName);             
            exchange.setProperty(HOLDING_PATH, holdingFile.getAbsolutePath()); 
            
            FileUtils.writeFile(exchange, holdingDirName);                 
        } 
        else 
        {
            String dirName = 
                FileUtils.getDirPathForRequest(
                    exchange,BaseConstants.WORKER_OUTPUT_DIR_PROPERTY);            
            FileUtils.writeFile(exchange, dirName);                   
        }
    }  
}