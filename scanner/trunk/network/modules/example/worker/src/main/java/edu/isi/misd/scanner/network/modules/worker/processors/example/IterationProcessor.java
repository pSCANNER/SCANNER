package edu.isi.misd.scanner.network.modules.worker.processors.example;

import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.types.example.ExampleIterationMessage;
import java.util.Random;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class IterationProcessor implements Processor
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(IterationProcessor.class); 
        
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
    
    private ExampleIterationMessage executeAnalysis(Exchange exchange) 
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
