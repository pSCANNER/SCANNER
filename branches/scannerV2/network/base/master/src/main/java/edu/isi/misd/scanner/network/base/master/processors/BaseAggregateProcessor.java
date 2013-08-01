package edu.isi.misd.scanner.network.base.master.processors;

import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.base.ServiceResponse;
import edu.isi.misd.scanner.network.types.base.ServiceResponses;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class aggregates {@link edu.isi.misd.scanner.network.types.base.ServiceResponse} 
 * objects into a {@link edu.isi.misd.scanner.network.types.base.ServiceResponses} 
 * object and sets the result as the response body for the message exchange.
 */
public class BaseAggregateProcessor implements Processor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseAggregateProcessor.class); 

    @Override
    public void process(Exchange exchange) throws Exception 
    {                        
        List results = getResults(exchange);   
        ServiceResponses serviceResponses = new ServiceResponses();           
        for (Object result : results)
        {  
            ServiceResponse response = 
                (ServiceResponse)MessageUtils.convertTo(
                    ServiceResponse.class, result, exchange);
            serviceResponses.getServiceResponse().add(response);
        }
        exchange.getIn().setBody(serviceResponses);                
    }
    
    protected static List getResults(Exchange exchange)
    {
        ArrayList results = exchange.getIn().getBody(ArrayList.class);
        if (results == null) {
            ErrorUtils.setHttpError(
                exchange, 
                new NullPointerException("No result data (null aggregate results array)"), 500);
        }
        return results;
    }        
}
