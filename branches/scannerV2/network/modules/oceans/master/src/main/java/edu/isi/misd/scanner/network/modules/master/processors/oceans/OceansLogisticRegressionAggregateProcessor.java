package edu.isi.misd.scanner.network.modules.master.processors.oceans;

import edu.isi.misd.scanner.network.base.master.processors.BaseAggregateProcessor;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.base.ErrorDetails;
import edu.isi.misd.scanner.network.types.oceans.OceansLogisticRegressionResponse;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionOutput;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionResponse;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * This class collects and collates the individual OCEANS processing 
 * responses of the remote worker nodes. It aggregates either 
 * {@link LogisticRegressionOutput} objects or 
 * {@link edu.isi.misd.scanner.network.types.base.ErrorDetails}
 * objects into an {@link OceansLogisticRegressionResponse}, 
 * and returns the response object to the caller.
 */
public class OceansLogisticRegressionAggregateProcessor extends BaseAggregateProcessor 
{
    /**
     * Camel {@link org.apache.camel.Processor} implementation -- 
     * the majority of the work is handled in this function implementation.
     */
    @Override
    public void process(Exchange exchange) throws Exception 
    {
        ArrayList<OceansLogisticRegressionResponse> responses = 
            new ArrayList<OceansLogisticRegressionResponse>();
        ArrayList<LogisticRegressionOutput> output = 
            new ArrayList<LogisticRegressionOutput>();
        ArrayList<ErrorDetails> errors = new ArrayList<ErrorDetails>();        
                          
        List results = BaseAggregateProcessor.getResults(exchange);         
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
                            OceansLogisticRegressionResponse.class,
                            result,
                            exchange);

                if (lrResult != null) {
                    responses.add(lrResult);
                    output.addAll(
                        lrResult.getLogisticRegressionResponse().getOutput());
                    errors.addAll(
                        lrResult.getLogisticRegressionResponse().getError());
                }
            }
        }
        
        // We only aggregate the LogisticRegressionOutput and any ErrorDetails of the responses,
        // so it is safe to just reuse the first response and replace its output
        // with the aggregate result.  The rest of the data in all the other 
        // responses should  be exactly the same (DataSetID, DataSetVariables, 
        // DataSetInput) so we dont want to repeat it, but we do want to preserve it.          
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
