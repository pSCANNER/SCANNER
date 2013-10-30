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
import edu.isi.misd.scanner.network.base.utils.FileUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 *  This class writes the results of a processing pipeline to a local file.
 *
 *  @author Mike D'Arcy 
 */
public class BaseCacheWriteProcessor implements Processor
{    
    @Override
    public void process(Exchange exchange) throws Exception 
    {
        String dirName = 
            FileUtils.getDirPathForRequest(
                exchange,BaseConstants.WORKER_OUTPUT_DIR_PROPERTY);            
        FileUtils.writeFile(exchange, dirName);                   
    }  
}