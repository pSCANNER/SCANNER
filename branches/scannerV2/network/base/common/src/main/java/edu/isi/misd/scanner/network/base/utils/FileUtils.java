package edu.isi.misd.scanner.network.base.utils;

import edu.isi.misd.scanner.network.base.BaseConstants;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
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
    public static void readFile(Exchange exchange, String dirName)
        throws Exception
    {
        try 
        {
            String fileName = getFileNameForRequest(exchange,dirName);
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
    public static void writeFile(Exchange exchange, String dirName)
        throws Exception
    {        
        String fileName = null;
        if (exchange.getIn().getHeader(BaseConstants.ID) != null) {
            fileName = "${in.headers." + BaseConstants.ID + "}";
        } else {
            fileName=getFileNameForRequest(exchange,dirName);
        }
        String outputURI = "file://" + dirName + "/?fileName=" + fileName;
        ProducerTemplate template = 
            exchange.getContext().createProducerTemplate();
        template.send(outputURI, exchange);            
    }
    
    public static String getDirPathForRequest(Exchange exchange,
                                              String baseDir)
        throws Exception
    {   
            String url = 
                (String)exchange.getProperty(BaseConstants.REQUEST_URL);
            URL requestURL = new URL(url);            
            String path = requestURL.getPath();
            int idIndex = path.lastIndexOf("/" + BaseConstants.ID + "/");
            String dirName = 
                ConfigUtils.getBaseOutputDir(exchange, baseDir) + 
                    "/" + requestURL.getHost() +
                    "/" + requestURL.getPort() + 
                    "/" + ((idIndex > 0) ? path.substring(0,idIndex) : path);
            
            return dirName;
    }    
    
    public static String getFileNameForRequest(Exchange exchange,
                                               String baseDir)
        throws Exception
    {
            String url = 
                (String)exchange.getProperty(BaseConstants.REQUEST_URL);
            URL requestURL = new URL(url);            
            String path = requestURL.getPath();            
            String fileName = MessageUtils.parseIdFromUrlPath(path);  
            
            return fileName;
    }
    
    public static String getHoldingDirPathForRequest(Exchange exchange,
                                                     String baseDir)
        throws Exception
    {            
            String url = 
                (String)exchange.getProperty(BaseConstants.REQUEST_URL);
            URL requestURL = new URL(url);            

            String dirName = 
                ConfigUtils.getBaseHoldingDir(exchange, baseDir) + 
                    "/" + requestURL.getHost() +
                    "/" + requestURL.getPort();
            
            return dirName;                  
    }
}
