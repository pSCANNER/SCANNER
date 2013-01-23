/*
 */
package edu.isi.misd.scanner.network.base.worker.processors;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.FileUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class BaseCacheReadProcessor implements Processor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseCacheReadProcessor.class);
        
    /**
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception 
    {
        MessageUtils.setTimestamp(exchange);          
        FileUtils.readFile(exchange, BaseConstants.WORKER_OUTPUT_DIR);
    }              
}
