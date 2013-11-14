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
import edu.isi.misd.scanner.network.types.base.ServiceResponseData;
import edu.isi.misd.scanner.network.types.base.SimpleMap;
import java.util.Calendar;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is basically a NOOP processor which should be replaced with 
 * application-specific code. Functionally, it just echoes the expected 
 * {@link edu.isi.misd.scanner.network.types.base.SimpleMap} input parameter.
 *
 * @author Mike D'Arcy 
 */
public class BaseComputeProcessor implements Processor
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseComputeProcessor.class);
        
    @Override
    public void process(Exchange exchange) throws Exception 
    {    
        Calendar start = Calendar.getInstance();
        ServiceResponse response = new ServiceResponse();        
        SimpleMap request = 
            (SimpleMap)exchange.getIn().getBody(SimpleMap.class);         
        if (request != null) {
            ServiceResponseData responseData = new ServiceResponseData();
            responseData.setAny(request);
            response.setServiceResponseData(responseData);
        }   
        Calendar end = Calendar.getInstance();
        response.setServiceResponseMetadata(
            MessageUtils.createServiceResponseMetadata(
                    exchange, 
                    ServiceRequestStateType.COMPLETE,
                    BaseConstants.STATUS_COMPLETE,
                    MessageUtils.formatEventDuration(start, end)));        
        exchange.getIn().setBody(response);
    }              
}
