package edu.isi.misd.scanner.network.base.master.processors;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.FileUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class sets a message response body by reading it from a 
 * local file cache.
 */
public class BaseCacheReadProcessor implements Processor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseCacheReadProcessor.class);    
    
    @Override
    public void process(Exchange exchange) throws Exception 
    {
        MessageUtils.setTimestamp(exchange);          
        String dirName = 
            FileUtils.getDirPathForRequest(
                exchange, BaseConstants.MASTER_OUTPUT_DIR_PROPERTY);            
        FileUtils.readFile(exchange, dirName);         
    }             
}

