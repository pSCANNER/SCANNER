package edu.isi.misd.scanner.network.base.worker.processors;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class inspects certain header values on inbound requests and performs
 * some basic validation and syntax checking.
 */
public class BaseRequestProcessor implements Processor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseRequestProcessor.class);     
        
    @Override
    public void process(Exchange exchange) throws Exception 
    {           
        String requestURL = MessageUtils.getRequestURL(exchange.getIn());
        exchange.setProperty(BaseConstants.REQUEST_URL, requestURL);
        
        String httpMethod = 
                (String)exchange.getIn().getHeader(Exchange.HTTP_METHOD);
        
        if ("POST".equalsIgnoreCase(httpMethod)) 
        {
            Object body = exchange.getIn().getBody();            
            if (body instanceof String && ((String)body).isEmpty()) {
                IllegalArgumentException iae = 
                    new IllegalArgumentException(
                        "Message body cannot be empty when invoking POST");                
                ErrorUtils.setHttpError(exchange, iae, 500);
            }            
        }           
        
    }    
}
