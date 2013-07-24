/*
 */
package edu.isi.misd.scanner.network.base.master.processors;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import java.util.ArrayList;
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
        StringBuilder resultBody = new StringBuilder();   
        if ("text/uri-list".equalsIgnoreCase(
            exchange.getIn().getHeader(Exchange.CONTENT_TYPE,String.class)))
        {
            exchange.getIn().removeHeader(BaseConstants.DATASOURCE);
            ArrayList results = exchange.getIn().getBody(ArrayList.class);
            if (results == null) {
                ErrorUtils.setHttpError(
                    exchange, 
                    new NullPointerException("No result data (null aggregate results array)"), 500);
            }

            for (Object result : results)
            {          
                if (result instanceof String) {
                    resultBody
                        .append((String)result)
                        .append("\r\n");
                }
            }
            exchange.getIn().setBody(resultBody.toString());                
        }
    }              
}
