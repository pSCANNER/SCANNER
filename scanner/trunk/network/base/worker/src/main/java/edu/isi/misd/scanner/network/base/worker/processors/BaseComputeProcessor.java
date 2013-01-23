/*
 */
package edu.isi.misd.scanner.network.base.worker.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class BaseComputeProcessor implements Processor
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseComputeProcessor.class);
        
    /**
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception 
    {                
        // this processor should be replaced with app-specific code
        // it simply 'echoes' the input message and logs it 
        log.debug("NOOP on message: " + exchange.getIn().getBody(String.class));
    }              
}
