package edu.isi.misd.scanner.network.modules.master.processors.oceans;

import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.base.ErrorDetails;
import edu.isi.misd.scanner.network.types.oceans.OceansLogisticRegressionResponse;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionOutput;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionResponse;
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
        ArrayList<OceansLogisticRegressionResponse> responses = 
            new ArrayList<OceansLogisticRegressionResponse>();
        ArrayList<LogisticRegressionOutput> output = 
            new ArrayList<LogisticRegressionOutput>();
        ArrayList<ErrorDetails> errors = new ArrayList<ErrorDetails>();        
        
        ArrayList results = exchange.getIn().getBody(ArrayList.class);
        if (results == null) {
            ErrorUtils.setHttpError(
                exchange, 
                new NullPointerException("Null OCEANS aggregate results array"), 500);
        }
        
        for (Object result : results)
        {
            if (result instanceof ErrorDetails)
            {
                errors.add((ErrorDetails)result);
            } 
            else 
            {
                OceansLogisticRegressionResponse lrResult = 
                    (OceansLogisticRegressionResponse)
                        MessageUtils.convertTo(
                            OceansLogisticRegressionResponse.class, result, exchange);

                if (lrResult != null) {
                    responses.add(lrResult);
                    output.addAll(
                        lrResult.getLogisticRegressionResponse().getOutput());
                    errors.addAll(
                        lrResult.getLogisticRegressionResponse().getError());
                }
            }
        }
        
        // we only aggregate the LogisticRegressionOutput and any ErrorDetails of the responses,
        // so it is safe to just reuse the first response and replace its output
        // with the aggregate result.  The rest of the data in all responses should 
        // be exactly the same (this is by design) so we dont want to repeat it.          
        OceansLogisticRegressionResponse aggregateResponse;
        if (!responses.isEmpty()) {
            aggregateResponse = responses.get(0);
        } else {
            aggregateResponse = new OceansLogisticRegressionResponse();
            aggregateResponse.setLogisticRegressionResponse(
                new LogisticRegressionResponse());
        }      
        aggregateResponse.getLogisticRegressionResponse().getOutput().clear();
        aggregateResponse.getLogisticRegressionResponse().getOutput().addAll(output);        
        aggregateResponse.getLogisticRegressionResponse().getError().clear();
        aggregateResponse.getLogisticRegressionResponse().getError().addAll(errors);            
        exchange.getIn().setBody(aggregateResponse);        
    }
}
