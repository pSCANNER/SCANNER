/*
 */
package edu.isi.misd.scanner.network.base.worker.processors;

import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.base.SimpleMap;
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
        // it just echoes the passed-in SimpleMap and adds SiteInfo to it
        SimpleMap request = 
            (SimpleMap)exchange.getIn().getBody(SimpleMap.class); 
        if (request != null) {
            request.setSiteInfo(MessageUtils.getSiteInfo(exchange));
        } 
        exchange.getIn().setBody(request);
    }              
}
