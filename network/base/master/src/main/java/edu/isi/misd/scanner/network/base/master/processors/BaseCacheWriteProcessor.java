package edu.isi.misd.scanner.network.base.master.processors;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.FileUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 *  This class writes the results of a processing pipeline to a local file.
 */
public  class BaseCacheWriteProcessor implements Processor
{
    @Override
    public void process(Exchange exchange) throws Exception 
    {        
        String dirName = 
            FileUtils.getDirPathForRequest(
                exchange, BaseConstants.MASTER_OUTPUT_DIR_PROPERTY);            
        FileUtils.writeFile(exchange, dirName);               
    }  
}