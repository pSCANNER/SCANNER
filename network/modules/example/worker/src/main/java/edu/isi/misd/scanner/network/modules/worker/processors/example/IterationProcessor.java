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
package edu.isi.misd.scanner.network.modules.worker.processors.example; 

import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.types.example.ExampleIterationMessage;
import java.util.Random;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This processor simply generates a random number between 1 and 10 and returns
 * it as a value of a 
 * {@link edu.isi.misd.scanner.network.types.example.ExampleIterationMessage}.
 *
 * @author Mike D'Arcy 
 */
public class IterationProcessor implements Processor
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(IterationProcessor.class); 
    /**
     * Camel {@link org.apache.camel.Processor} implementation,
     * invokes {@link IterationProcessor#executeAnalysis(org.apache.camel.Exchange)}.
     */
    @Override
    public void process(Exchange exchange) throws Exception 
    {        
        try {                      
            exchange.getIn().setBody(this.executeAnalysis(exchange)); 
        }
        catch (Exception e) {
            ErrorUtils.setHttpError(exchange, e, 500);
        }                
    }
    
    /**
     * Generates a random number from 1 to 10.
     * @param exchange
     * @return The formatted response message.
     * @throws Exception 
     */
    protected ExampleIterationMessage executeAnalysis(Exchange exchange) 
        throws Exception
    {
        ExampleIterationMessage message = 
            (ExampleIterationMessage)
                exchange.getIn().getBody(
                    ExampleIterationMessage.class); 
       
        Random random = new Random();
        message.setRandom(random.nextInt(9)+1);
        
        return message;
    }
}
