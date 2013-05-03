package edu.isi.misd.scanner.network.base.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  This class is used to introduce NOOP logic into existing route definitions
 *  where a certain piece of functionality is not needed.  For example, the 
 *  Echo service does not need to cache its output, so it's 
 *  BaseCache(Read/Write)Processors are replaced with the BaseNoOpProcessor.
 */
public class BaseNoOpProcessor implements Processor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseNoOpProcessor.class);   

    @Override
    public void process(Exchange exchng) throws Exception 
    {
        if (log.isDebugEnabled()) {
            log.debug("NOOP Processor invoked");
        }
    }
        
}
