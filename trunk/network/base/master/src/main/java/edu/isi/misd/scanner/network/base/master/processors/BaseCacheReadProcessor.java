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

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.FileUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class sets a message response body by reading it from a 
 * local file cache.
 *
 * @author Mike D'Arcy 
 */
public class BaseCacheReadProcessor implements Processor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseCacheReadProcessor.class);    
    
    @Override
    public void process(Exchange exchange) throws Exception 
    {
        MessageUtils.setTimestamp(exchange);          
        String dirName = 
            FileUtils.getDirPathForRequest(
                exchange, BaseConstants.MASTER_OUTPUT_DIR_PROPERTY);            
        FileUtils.readFile(exchange, dirName);         
    }             
}

