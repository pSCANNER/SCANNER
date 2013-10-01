/*
 */
package edu.isi.misd.scanner.network.base.worker.processors;
import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.base.ServiceRequestStateType;
import edu.isi.misd.scanner.network.types.base.ServiceResponse;
import edu.isi.misd.scanner.network.types.base.ServiceResponseData;
import edu.isi.misd.scanner.network.types.base.SimpleMap;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is basically a NOOP processor which should be replaced with 
 * application-specific code. Functionally, it just echoes the expected 
 * {@link edu.isi.misd.scanner.network.types.base.SimpleMap} input parameter.
 */
public class BaseComputeProcessor implements Processor
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseComputeProcessor.class);
        
    @Override
    public void process(Exchange exchange) throws Exception 
    {     
        ServiceResponse response = new ServiceResponse();        
        response.setServiceResponseMetadata(
            MessageUtils.createServiceResponseMetadata(
                    exchange, 
                    ServiceRequestStateType.COMPLETE,
                    BaseConstants.STATUS_COMPLETE));
        SimpleMap request = 
            (SimpleMap)exchange.getIn().getBody(SimpleMap.class);         
        if (request != null) {
            ServiceResponseData responseData = new ServiceResponseData();
            responseData.setAny(request);
            response.setServiceResponseData(responseData);
        }        
        exchange.getIn().setBody(response);
    }              
}
