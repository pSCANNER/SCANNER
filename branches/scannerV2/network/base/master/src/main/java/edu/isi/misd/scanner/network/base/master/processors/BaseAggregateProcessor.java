package edu.isi.misd.scanner.network.base.master.processors;

import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.base.BaseResponse;
import edu.isi.misd.scanner.network.types.base.ErrorDetails;
import edu.isi.misd.scanner.network.types.base.SimpleMap;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class combines either {@link edu.isi.misd.scanner.network.types.base.SimpleMap} 
 * or {@link edu.isi.misd.scanner.network.types.base.ErrorDetails} instances 
 * into a {@link edu.isi.misd.scanner.network.types.base.BaseResponse} object 
 * and sets it as the response body for the message exchange.
 */
public class BaseAggregateProcessor implements Processor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseAggregateProcessor.class); 

    @Override
    public void process(Exchange exchange) throws Exception 
    {                        
        List results = getResults(exchange);           
        ArrayList<SimpleMap> mapResults = new ArrayList<SimpleMap>();
        ArrayList<ErrorDetails> errors = new ArrayList<ErrorDetails>();
        for (Object result : results)
        {          
            if (result instanceof ErrorDetails)
            {
                errors.add((ErrorDetails)result);
            } 
            else 
            {            
                SimpleMap mapResult = 
                    (SimpleMap)MessageUtils.convertTo(
                        SimpleMap.class, result, exchange);

                if (mapResult != null) {
                    mapResults.add(mapResult);
                } 
            }
        }
        BaseResponse response = new BaseResponse();        
        response.getSimpleMap().addAll(mapResults);        
        response.getError().addAll(errors);         
        exchange.getIn().setBody(response);                
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
