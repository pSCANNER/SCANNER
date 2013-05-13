package edu.isi.misd.scanner.network.base.master.aggregators;

import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import java.util.ArrayList;
import org.apache.camel.Exchange;
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
    
    /**
     *
     * @param oldExchange
     * @param newExchange
     * @return
     */
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) 
    {  
        Object newBody = newExchange.getIn().getBody(String.class);
        
        String failureEndpoint = 
            newExchange.getProperty(Exchange.FAILURE_ENDPOINT, String.class); 
        
        String endpoint = (failureEndpoint != null) ? failureEndpoint :
            newExchange.getProperty(Exchange.TO_ENDPOINT, String.class);
        
        if (failureEndpoint != null) {
            Throwable cause =
                newExchange.getProperty(
                    Exchange.EXCEPTION_CAUGHT,
                    Throwable.class);

            newBody = 
                ErrorUtils.formatErrorResponse(
                    newExchange,
                    (cause != null) ? cause : 
                    new RuntimeException("Source: " + failureEndpoint));          
        }
        
        ArrayList list;
        if (oldExchange == null) 
        {
            list = new ArrayList();
            list.add(newBody);
            newExchange.getIn().setBody(list);
            return newExchange;
        }
        else 
        {
            list = oldExchange.getIn().getBody(ArrayList.class);
            if (list != null) {
                list.add(newBody);
            } 
            return oldExchange;
        }
    }    
}
