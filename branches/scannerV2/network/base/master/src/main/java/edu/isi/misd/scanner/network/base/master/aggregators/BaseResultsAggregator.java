package edu.isi.misd.scanner.network.base.master.aggregators;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.base.ServiceRequestStateType;
import edu.isi.misd.scanner.network.types.base.ServiceResponse;
import edu.isi.misd.scanner.network.types.base.ServiceResponseMetadata;
import java.util.ArrayList;
import org.apache.camel.Exchange;
import org.apache.camel.component.http4.HttpOperationFailedException;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class serves as the default 
 * {@link org.apache.camel.processor.aggregate.AggregationStrategy} for the 
 * master network node. It aggregates either valid, deserializable string
 * responses or {@link edu.isi.misd.scanner.network.types.base.ErrorDetails}
 * objects into an ArrayList, and sets the current exchange body to be the
 * aggregate ArrayList before continuing with the route processing.
 */
public class BaseResultsAggregator implements AggregationStrategy 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseResultsAggregator.class); 
    
    /**
     *
     * @see org.apache.camel.processor.aggregate.AggregationStrategy
     * @param oldExchange The previous exchange
     * @param newExchange The current exchange
     * @return The aggregated exchange
     */
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) 
    {  
        Object newBody = newExchange.getIn().getBody(String.class);
        
        String failureEndpoint = 
            newExchange.getProperty(Exchange.FAILURE_ENDPOINT, String.class);
        
        if (failureEndpoint != null) 
        {
            String failureDesc = "";
            Throwable cause =
                newExchange.getProperty(
                    Exchange.EXCEPTION_CAUGHT,
                    Throwable.class);
            if (cause == null) { 
                cause = new RuntimeException("Source: " + failureEndpoint);
            }            
            if (log.isDebugEnabled()) {
                log.debug(
                    "Caught exception during aggregation: " + cause.toString());
            }            
//            if (cause instanceof HttpOperationFailedException) { 
//                 errorDesc = " " + 
//                    ((HttpOperationFailedException)cause).getStatusText() + ": " +
//                    ((HttpOperationFailedException)cause).getResponseBody();
//            } 
//            else
            {
                failureDesc = cause.toString();
            }
            
            ServiceResponse response = new ServiceResponse();
            ServiceResponseMetadata responseMetadata =
                MessageUtils.createServiceResponseMetadata(
                    newExchange,
                    ServiceRequestStateType.ERROR,
                    "An exception occurred during query execution: " + 
                    failureDesc);
            responseMetadata.setRequestURL(
                failureEndpoint.substring(0,failureEndpoint.indexOf('?')));
            response.setServiceResponseMetadata(responseMetadata);
            newBody = response;
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
