package edu.isi.misd.scanner.network.modules.master.processors.oceans;

import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.oceans.OceansLogisticRegressionResponse;
import edu.isi.misd.scanner.network.types.oceans.OceansLogisticRegressionResponseArray;
import java.util.ArrayList;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 *
 */
public class OceansLogisticRegressionAggregateProcessor implements Processor 
{
    /**
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception 
    {
        OceansLogisticRegressionResponseArray oceansLRResponseArray = 
            new OceansLogisticRegressionResponseArray();
                
        ArrayList<String> results = exchange.getIn().getBody(ArrayList.class);
        if (results == null) {
            ErrorUtils.setHttpError(
                exchange, 
                new NullPointerException("Null aggregator results array"), 500);
        }
        
        for (Object result : results)
        {           
            OceansLogisticRegressionResponse lrResult = 
                (OceansLogisticRegressionResponse)
                    MessageUtils.convertTo(
                        OceansLogisticRegressionResponse.class, result, exchange);

            if (lrResult != null) {
                oceansLRResponseArray.getOceansLogisticRegressionResponse().add(lrResult);
            }
        }
        exchange.getIn().setBody(oceansLRResponseArray);        
    }
}
