/*
 */
package edu.isi.misd.scanner.network.base.worker.processors;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This processor class returns the result URL of the processing response.
 */
public class BaseAsyncResponseProcessor implements Processor
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseAsyncResponseProcessor.class);
        
    @Override
    public void process(Exchange exchange) throws Exception 
    {     
        String resultURL = MessageUtils.getResultURL(exchange);        
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"text/uri-list");
        exchange.getIn().setBody(resultURL);
        exchange.getIn().removeHeader(BaseConstants.DATASOURCE);
    }              
}
