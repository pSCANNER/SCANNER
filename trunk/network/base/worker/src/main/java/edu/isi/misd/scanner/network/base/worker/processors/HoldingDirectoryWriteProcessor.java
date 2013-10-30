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
import java.io.File;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 *  This class writes the results of a processing pipeline to a local file in the configured holding directory.
 *
 *  @author Mike D'Arcy 
 */
public class HoldingDirectoryWriteProcessor implements Processor
{
    public static final String FILE_PATH = "filePath";
    public static final String HOLDING_PATH = "holdingPath";
    
    @Override
    public void process(Exchange exchange) throws Exception 
    {
        String fileName = 
            (String)exchange.getIn().getHeader(BaseConstants.ID);              

        String outputDirName = 
            FileUtils.getDirPathForRequest(
                exchange,BaseConstants.WORKER_OUTPUT_DIR_PROPERTY);              

        File outputFile = new File(outputDirName, fileName);        
        exchange.setProperty(FILE_PATH, outputFile.getAbsolutePath());               

        String holdingDirName = 
            FileUtils.getHoldingDirPathForRequest(
                exchange,BaseConstants.WORKER_OUTPUT_HOLDING_DIR_PROPERTY); 

        File holdingFile = new File(holdingDirName, fileName);             
        exchange.setProperty(HOLDING_PATH, holdingFile.getAbsolutePath()); 

        FileUtils.writeFile(exchange, holdingDirName);                 
    }  
}