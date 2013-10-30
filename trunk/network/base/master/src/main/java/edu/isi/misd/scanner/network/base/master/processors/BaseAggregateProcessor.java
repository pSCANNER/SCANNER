/*  
 * Copyright 2013 University of Southern California 
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *  
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */ 
package edu.isi.misd.scanner.network.base.master.processors; 

import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.base.ServiceRequestStateType;
import edu.isi.misd.scanner.network.types.base.ServiceResponse;
import edu.isi.misd.scanner.network.types.base.ServiceResponseData;
import edu.isi.misd.scanner.network.types.base.ServiceResponseMetadata;
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
 *
 * @author Mike D'Arcy 
 */
public class BaseAggregateProcessor implements Processor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseAggregateProcessor.class); 

    @Override
    public void process(Exchange exchange) throws Exception 
    {                        
        List results = getAggregateResults(exchange);   
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
    
    protected static List getAggregateResults(Exchange exchange)
    {
        ArrayList results = exchange.getIn().getBody(ArrayList.class);
        if (results == null) {
            ErrorUtils.setHttpError(
                exchange, 
                new NullPointerException(
                    "No result data (null aggregate results array)"), 500);
        }
        return results;
    } 
    
    public static <T> List<T> getTypedResponses(Exchange exchange, Class<T> T)
        throws Exception
    {
        ServiceResponses serviceResponses = 
            exchange.getIn().getBody(ServiceResponses.class);   
        if (serviceResponses == null) {
            throw new NullPointerException(
                "ServiceResponses aggregate structure was null");
        }        
        List<ServiceResponse> resultsInput = 
            serviceResponses.getServiceResponse();     
        
        ArrayList<T> resultsOutput = new ArrayList<T>();        
        for (ServiceResponse result : resultsInput) 
        {
            ServiceResponseData responseData = result.getServiceResponseData();
            if (responseData != null) {
                T request = 
                        (T)MessageUtils.convertTo(
                            T, responseData.getAny(), exchange);
                resultsOutput.add(request);
            } else {
                log.warn("A ServiceResponse was missing the expected ServiceResponseData containing requested object type: " + T.getName());
            }
        }        
        return resultsOutput; 
    }
    
    public static List<ServiceResponse> getErrorResponses(Exchange exchange)
        throws Exception
    {
        ServiceResponses serviceResponses = 
            exchange.getIn().getBody(ServiceResponses.class);   
        if (serviceResponses == null) {
            throw new NullPointerException(
                "ServiceResponses aggregate structure was null");
        }        
        List<ServiceResponse> resultsInput = 
            serviceResponses.getServiceResponse(); 
        
        ArrayList<ServiceResponse> errors = 
            new ArrayList<ServiceResponse>();         
        for (ServiceResponse result : resultsInput) 
        {
                ServiceResponseMetadata status = 
                    result.getServiceResponseMetadata();
                if (ServiceRequestStateType.ERROR.equals(
                    status.getRequestState())) {
                        errors.add(result);
                }
        }
        return errors;         
    }     
}
