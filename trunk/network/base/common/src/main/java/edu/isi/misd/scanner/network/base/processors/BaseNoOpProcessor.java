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
package edu.isi.misd.scanner.network.base.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  This class is used to introduce NOOP logic into existing route definitions
 *  where a certain piece of functionality is not needed.  For example, the 
 *  Echo service does not need to cache its output, so it's 
 *  BaseCache(Read/Write)Processors are replaced with the BaseNoOpProcessor.
 *
 * @author Mike D'Arcy 
 */
public class BaseNoOpProcessor implements Processor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseNoOpProcessor.class);   

    /**
     * Does nothing, logs the invocation to the debug log.
     * @throws Exception 
     */
    @Override
    public void process(Exchange exchng) throws Exception 
    {
        if (log.isDebugEnabled()) {
            log.debug("NOOP Processor invoked");
        }
    }
        
}
