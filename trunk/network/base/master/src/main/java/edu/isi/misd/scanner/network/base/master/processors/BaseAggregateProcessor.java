package edu.isi.misd.scanner.network.base.master.processors;

import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.base.SimpleMap;
import edu.isi.misd.scanner.network.types.base.SimpleMapArray;
import java.util.ArrayList;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class BaseAggregateProcessor implements Processor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseAggregateProcessor.class); 

    /**
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception 
    {                
        ArrayList<String> results = exchange.getIn().getBody(ArrayList.class);
        if (results == null) {
            ErrorUtils.setHttpError(
                exchange, 
                new NullPointerException("No result data (null aggregate results array)"), 500);
        }
        
        SimpleMapArray baseMapArray = new SimpleMapArray();        
        for (Object result : results)
        {           
            SimpleMapArray mapResult = 
                (SimpleMapArray)MessageUtils.convertTo(
                    SimpleMapArray.class, result, exchange);

            if (mapResult != null) {
                baseMapArray.getSimpleMap().addAll(mapResult.getSimpleMap());
            }
        }
        exchange.getIn().setBody(baseMapArray);                
    }
}
