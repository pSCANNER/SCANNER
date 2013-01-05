package edu.isi.misd.scanner.network.base.master.aggregators;

import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import java.util.ArrayList;
import org.apache.camel.Exchange;
import org.apache.camel.component.http.HttpOperationFailedException;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class BaseResultsAggregator implements AggregationStrategy 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseResultsAggregator.class); 
    
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) 
    {  
        String newBody = newExchange.getIn().getBody(String.class); 
        
        Throwable cause =
            newExchange.getProperty(
                Exchange.EXCEPTION_CAUGHT,
                Throwable.class);
        if (cause != null) {
            if (cause instanceof HttpOperationFailedException) {
                newBody =  
                    ((HttpOperationFailedException)cause).getResponseBody();
                ErrorUtils.setHttpError(newExchange, cause, 500, false);                
            } else {
                ErrorUtils.setHttpError(newExchange, cause, 500, false);                
                newBody = ErrorUtils.formatErrorResponse(newExchange, cause);
            }
        }
              
        ArrayList<String> list;
        if (oldExchange == null) 
        {
            list = new ArrayList<String>();
            list.add(newBody);
            newExchange.getIn().setBody(list);
            return newExchange;
        }
        else 
        {
            list = oldExchange.getIn().getBody(ArrayList.class);
            list.add(newBody);
            return oldExchange;
        }
    }    
}
