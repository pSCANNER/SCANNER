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
package edu.isi.misd.scanner.network.base.worker.processors; 

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.base.ServiceRequestStateType;
import edu.isi.misd.scanner.network.types.base.ServiceResponse;
import edu.isi.misd.scanner.network.types.base.ServiceResponseMetadata;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This processor class returns ServiceRequestMetadata related to the request.
 *
 * @author Mike D'Arcy 
 */
public class BaseAsyncResponseProcessor implements Processor
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseAsyncResponseProcessor.class);
        
    @Override
    public void process(Exchange exchange) throws Exception 
    {
        ServiceResponse response = new ServiceResponse();
        ServiceResponseMetadata serviceResponseMetadata = 
            MessageUtils.createServiceResponseMetadata(
                exchange, 
                ServiceRequestStateType.PROCESSING, 
                BaseConstants.STATUS_PROCESSING);
        
        Boolean doReleaseAuth = 
            ((Boolean)exchange.getIn().getHeader(
                BaseConstants.RESULTS_RELEASE_AUTH_REQUIRED,Boolean.class));
        if ((doReleaseAuth !=null) && (doReleaseAuth.booleanValue()==true)) 
        {
            serviceResponseMetadata.setRequestState(
                ServiceRequestStateType.HELD);            
            serviceResponseMetadata.setRequestStateDetail(
                BaseConstants.STATUS_HELD);
        }

        response.setServiceResponseMetadata(serviceResponseMetadata);
        exchange.getIn().setBody(response);
        exchange.getIn().removeHeader(BaseConstants.DATASOURCE);
    }              
}
