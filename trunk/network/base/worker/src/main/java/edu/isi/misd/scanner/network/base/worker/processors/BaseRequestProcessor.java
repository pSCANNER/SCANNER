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
import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class inspects certain header values on inbound requests and performs
 * some basic validation and syntax checking.
 *
 * @author Mike D'Arcy 
 */
public class BaseRequestProcessor implements Processor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseRequestProcessor.class);     
        
    @Override
    public void process(Exchange exchange) throws Exception 
    {           
        String requestURL = MessageUtils.getRequestURL(exchange.getIn());
        exchange.setProperty(BaseConstants.REQUEST_URL, requestURL);
        
        String httpMethod = 
                (String)exchange.getIn().getHeader(Exchange.HTTP_METHOD);
        
        if ("POST".equalsIgnoreCase(httpMethod)) 
        {
            Object body = exchange.getIn().getBody();            
            if (body instanceof String && ((String)body).isEmpty()) {
                IllegalArgumentException iae = 
                    new IllegalArgumentException(
                        "Message body cannot be empty when invoking POST");                
                ErrorUtils.setHttpError(exchange, iae, 500);
            }            
        }           
        
    }    
}
