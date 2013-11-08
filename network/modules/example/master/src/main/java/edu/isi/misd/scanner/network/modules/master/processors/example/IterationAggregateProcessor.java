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
package edu.isi.misd.scanner.network.modules.master.processors.example; 

import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.example.ExampleIterationMessage;
import java.util.ArrayList;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
    
/**
 * An example aggregation processor that uses Camel's Dynamic Routing EIP to
 * loop and continuously process responses via a state machine, signaling 
 * a completion state after the appropriate condition is met, thereby terminating
 * the loop.  This example simply averages the result of random numbers generated
 * at the worker nodes and stops once the average is sufficiently narrowed.
 * 
 *
 * @author Mike D'Arcy 
 */
public class IterationAggregateProcessor implements Processor 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(IterationAggregateProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception 
    {
        ExampleIterationMessage newData = null;
        
        ArrayList results = exchange.getIn().getBody(ArrayList.class);
        
        for (Object result : results) 
        {
            ExampleIterationMessage currentData = 
                (ExampleIterationMessage)
                    MessageUtils.convertTo(
                        ExampleIterationMessage.class, result, exchange);
            
            if ("alpha".equalsIgnoreCase(currentData.getStatus()))
            {
                log.info("Alpha computation phase");
                newData = currentData;
                // do some alpha computation and then trigger the next phase
                newData.setStatus("beta");               
            } 
            else if ("beta".equalsIgnoreCase(currentData.getStatus()))
            {
                log.info("Beta computation phase");
                newData = currentData;
                // do some beta computation and then trigger the next phase                
                newData.setStatus("omega");               
            }
            else if ("omega".equalsIgnoreCase(currentData.getStatus()))
            {
                log.info("Omega computation phase");
                newData = currentData;
                // do some omega computation and then signal complete                
                newData.setStatus("complete");
                exchange.setProperty("status", "complete");                 
            }            
            else 
            {
                if (newData == null) {
                    newData = currentData;
                }

                if (newData.getIterations() == 0) {
                    log.info("Primary iteration phase");                    
                    newData.setIterations(1);
                    newData.setTotal(currentData.getRandom());
                    newData.setStatus("iteration");
                    continue;
                }
                int i = newData.getIterations() + 1;
                long t = newData.getTotal() + currentData.getRandom();
                double lastAverage = newData.getAverage();
                double currentAverage = (double)t / (double)i;

                newData.setTotal(t);        
                newData.setIterations(i);
                newData.setAverage(currentAverage);

                if (Math.abs(currentAverage-lastAverage) < 0.0001) {
                    newData.setStatus("alpha");                                       
                }
            }
        }
        exchange.getIn().setBody(newData);        
    }
}
