package edu.isi.misd.scanner.network.base.utils;

import edu.isi.misd.scanner.network.base.BaseConstants;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.apache.camel.Exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Utility class for common file reading and writing operations 
 *  based on the context of the current exchange.
 */
public class FileUtils 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(FileUtils.class);
        
    /**
     * Reads a file and returns the contents as the body of an HTTP response.
     * Used to read cached output of previously executed pipeline results.
     * 
     * @param exchange The current exchange
     * @param baseDir The base directory
     * @throws Exception
     */
    public static void readFile(Exchange exchange, String baseDir)
        throws Exception
    {
        try 
        {            
            String path = MessageUtils.getPathFromMessageURL(exchange.getIn());
            int idIndex = path.lastIndexOf("/" + BaseConstants.ID + "/");
            String dirName = 
                ConfigUtils.getBaseOutputDir(exchange, baseDir) + 
                    "/" + MessageUtils.getHostFromMessageURL(exchange.getIn()) +
                    "/" + MessageUtils.getPortFromMessageURL(exchange.getIn()) + 
                    "/" + ((idIndex > 0) ? path.substring(0,idIndex) : path);
            
            String fileName = MessageUtils.parseIdFromUrlPath(path);            
            File file = new File(dirName, fileName);
            
            BufferedInputStream bis = 
                new BufferedInputStream(new FileInputStream(file));                        
            exchange.getIn().setBody(bis);
            
        }
        catch (FileNotFoundException fnf) {
            ErrorUtils.setHttpError(exchange, fnf, 404);
        }
        catch (Exception e) {
            ErrorUtils.setHttpError(exchange, e, 500);
        }                
    }
    
    /**
     * Writes the body of the passed-in exchange to a file.  
     * Used for caching output of pipeline results.
     * 
     * @param exchange The current exchange
     * @param baseDir The base directory
     * @throws Exception
     */
    public static void writeFile(Exchange exchange, String baseDir)
        throws Exception
    {      
        String path = MessageUtils.getPathFromMessageURL(exchange.getIn());
        int idIndex = path.lastIndexOf("/" + BaseConstants.ID + "/");
        String dirName = 
            ConfigUtils.getBaseOutputDir(exchange, baseDir) + 
                "/" + MessageUtils.getHostFromMessageURL(exchange.getIn()) +
                "/" + MessageUtils.getPortFromMessageURL(exchange.getIn()) + 
                "/" + ((idIndex > 0) ? path.substring(0,idIndex) : path);
          
        String fileName = MessageUtils.parseIdFromUrlPath(path); 
        if (fileName.isEmpty()) {
            fileName = "${in.headers." + BaseConstants.ID + "}";
        }
        String outputURI = "file://" + dirName + "/?fileName=" + fileName;
        exchange.getContext().createProducerTemplate().sendBodyAndHeaders(
            outputURI,exchange.getIn().getBody(), exchange.getIn().getHeaders());            
    }
}
