package edu.isi.misd.scanner.network.base.worker.processors;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.FileUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 *
 */
public  class BaseCacheWriteProcessor implements Processor
{
    /**
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception 
    { 
        FileUtils.writeFile(exchange, BaseConstants.WORKER_OUTPUT_DIR_PROPERTY);                
    }  
}