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
 * This class is basically a NOOP processor which should be replaced with 
 * application-specific code. Functionally, it just echoes an expected 
 * {@link edu.isi.misd.scanner.network.types.base.SimpleMap} parameter and adds 
 * a populated {@link edu.isi.misd.scanner.network.types.base.SiteInfo} to it.
 */
public class BaseComputeProcessor implements Processor
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseComputeProcessor.class);
        
    @Override
    public void process(Exchange exchange) throws Exception 
    {     
        SimpleMap request = 
            (SimpleMap)exchange.getIn().getBody(SimpleMap.class); 
        if (request != null) {
            request.setSiteInfo(MessageUtils.getSiteInfo(exchange));
        } 
        exchange.getIn().setBody(request);
    }              
}
