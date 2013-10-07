package edu.isi.misd.scanner.network.modules.master.processors.ptr;

import edu.isi.misd.scanner.network.base.master.processors.BaseAggregateProcessor;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.base.ServiceResponseMetadata;
import edu.isi.misd.scanner.network.types.base.ServiceRequestStateType;
import edu.isi.misd.scanner.network.types.base.ServiceResponse;
import edu.isi.misd.scanner.network.types.base.ServiceResponseData;
import edu.isi.misd.scanner.network.types.base.ServiceResponses;
import edu.isi.misd.scanner.network.types.ptr.PrepToResearchRequest;
import edu.isi.misd.scanner.network.types.ptr.PrepToResearchResponse;
import edu.isi.misd.scanner.network.types.ptr.PrepToResearchRecord;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.ArrayUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
    
/**
 * This class aggregates PrepToResearch result sets into a single result set.
 */
public class PrepToResearchAggregateProcessor extends BaseAggregateProcessor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(PrepToResearchAggregateProcessor.class);
    
    
    /**
     * Camel {@link org.apache.camel.Processor} implementation -- 
     * the majority of the work is handled in this function implementation.
     */
    @Override
    public void process(Exchange exchange) throws Exception
    {
        // creates the aggregate ServiceResponses object
        super.process(exchange);
        
        List<ServiceResponse> errorResponses = getErrorResponses(exchange);
        if (!errorResponses.isEmpty()) 
        {
            ServiceResponses responses = new ServiceResponses();                     
            for (ServiceResponse errorResponse : errorResponses) {      
                responses.getServiceResponse().add(errorResponse);
            }
            exchange.getIn().setBody(responses);                      
            return;
        }
        
        List<PrepToResearchResponse> ptrResponseList = 
            getTypedResponses(exchange, PrepToResearchResponse.class);
        if (ptrResponseList.isEmpty()) {
            ErrorUtils.setHttpError(
                exchange, 
                new NullPointerException("Null PTR aggregate results"), 500);
            return;
        }
            
        PrepToResearchResponse ptrResponse = 
            this.performAggregation(ptrResponseList);
        
        // format the service response objects and set as the result body
        ServiceResponseData responseData = new ServiceResponseData();           
        responseData.setAny(ptrResponse);            
        ServiceResponse response = new ServiceResponse();
        response.setServiceResponseData(responseData); 
        ServiceResponseMetadata responseMetadata = 
            MessageUtils.createServiceResponseMetadata(
                exchange, ServiceRequestStateType.COMPLETE,
                "The distributed Prep to Research analysis completed successfully.");
        response.setServiceResponseMetadata(responseMetadata);
        ServiceResponses responses = new ServiceResponses();              
        responses.getServiceResponse().add(response);
        exchange.getIn().setBody(responses);   
            
    }

    // NOT IMPLEMENTED YET
    private PrepToResearchResponse performAggregation(
            List<PrepToResearchResponse> ptrResponses)
        throws Exception
    {
        PrepToResearchResponse response = new PrepToResearchResponse();
        return response;
    }
}
