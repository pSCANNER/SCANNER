package edu.isi.misd.scanner.network.base.master.processors;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.UUID;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.util.ObjectHelper;

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
    
    // consider making this an externally configurable value
    private static final boolean USE_CAMEL_UUID_GENERATOR = false;
    
    @Override
    public void process(Exchange exchange) throws Exception 
    {        
        String id = MessageUtils.getID(exchange);        
        Object body = exchange.getIn().getBody();
        MessageUtils.setTimestamp(exchange);
        
        String requestURL = MessageUtils.getRequestURL(exchange.getIn());
        exchange.setProperty(BaseConstants.REQUEST_URL, requestURL);  
        
        String httpMethod = 
                (String)exchange.getIn().getHeader(Exchange.HTTP_METHOD);
        
        if ("POST".equalsIgnoreCase(httpMethod)) 
        {
            if (body instanceof String && ((String)body).isEmpty()) {
                IllegalArgumentException iae = 
                    new IllegalArgumentException(
                        "Message body cannot be empty when invoking POST");                
                ErrorUtils.setHttpError(exchange, iae, 500);
                return;
            }
            
            if (!checkTargets(exchange, true)) {
                return;
            }
            
            if (id == null) {  // TODO: remove this check 
                id = (USE_CAMEL_UUID_GENERATOR) ? 
                    exchange.getContext().getUuidGenerator().generateUuid() :
                    UUID.randomUUID().toString();
                MessageUtils.setID(exchange, id);
            }
        } 
        else if ("GET".equalsIgnoreCase(httpMethod)) 
        {                       
            if (id == null) { 
                id = MessageUtils.parseIdFromUrlPath(
                    (String)exchange.getProperty(BaseConstants.REQUEST_URL));
                if (id.isEmpty()) {
                    IllegalArgumentException iae = 
                        new IllegalArgumentException("ID cannot be null");
                    ErrorUtils.setHttpError(exchange, iae, 500);
                    return;
                }
            } 
            if (!checkTargets(exchange, false)) {
                return;
            }            
        }
        
        String contentType = 
            (String)exchange.getIn().getHeader(Exchange.CONTENT_TYPE);
        if (contentType == null) {
            exchange.getIn().setHeader(
                Exchange.CONTENT_TYPE, "application/xml"); 
        }
      
    }
    
    private static boolean checkTargets(Exchange exchange, boolean mustExist) 
    {
        String targets = MessageUtils.getTargets(exchange);
        if ((targets == null) || ((targets != null) && targets.isEmpty())) {
            if (mustExist) 
            {
                IllegalArgumentException iae = 
                    new IllegalArgumentException(
                        "Must specify at least one remote target URL using " + 
                        "\"targets=x,y,z\" as query parameter or message header");                
                ErrorUtils.setHttpError(exchange, iae, 500);     
                return false;
            }
        }
        
        Iterator iter = ObjectHelper.createIterator(targets);
        while (iter.hasNext()) 
        {
            String target = (String)iter.next();
            try {
                target = target.trim();
                URI uri = new URI(target);                          
            } catch (URISyntaxException use) {
                ErrorUtils.setHttpError(
                    exchange, use, 500, 
                    "Error: invalid target URI specified");  
                return false;
            }
        }        
        return true;
    }
}
