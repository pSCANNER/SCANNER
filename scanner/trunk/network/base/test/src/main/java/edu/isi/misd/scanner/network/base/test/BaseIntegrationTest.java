package edu.isi.misd.scanner.network.base.test;

import edu.isi.misd.scanner.network.base.BaseConstants;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public abstract class BaseIntegrationTest extends CamelSpringTestSupport
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseIntegrationTest.class);
    
    protected abstract String getMasterUrl() throws Exception;
    
    protected abstract String getWorkerUrls() throws Exception;
    
    protected void doPost(String contentType,
                          String inputFileName) 
        throws Exception
    {
        doPost(contentType, inputFileName, null);
    }  
    
    protected void doPost(String contentType,
                          String inputFileName,
                          String outputFileName) 
        throws Exception
    {
        MockEndpoint resultEndpoint = this.getMockEndpoint("mock:result");    
            
        InputStream input = this.getClass().getResourceAsStream(inputFileName);
        assertNotNull("The test input inputStream should not be null", input);

        if (outputFileName != null) {
            InputStream output = this.getClass().getResourceAsStream(outputFileName);
            assertNotNull("The test output inputStream should not be null", output);
            String outBody = IOUtils.toString(output, "UTF-8");     

            // use the specified file as the body validation data
            resultEndpoint.expectedBodiesReceived(outBody);
        }
        
        HashMap headers = new HashMap();
        headers.put(Exchange.HTTP_METHOD, "POST");
        headers.put(Exchange.CONTENT_TYPE, contentType);
        resultEndpoint.expectedHeaderReceived(Exchange.CONTENT_TYPE, contentType); 
        
        String targets = this.getWorkerUrls();
        headers.put(BaseConstants.TARGETS, targets);

        try {
            template.sendBodyAndHeaders(
                this.getMasterUrl(),input,headers);
        } catch (CamelExecutionException e) {
            log.error(e.getCause().getMessage());
        }
        
        // Validate the headers.
        List<Exchange> exchanges = resultEndpoint.getReceivedExchanges();
        assertFalse("No response data received", exchanges.isEmpty());
        Exchange exchange = exchanges.get(0);
        String id = (String)exchange.getIn().getHeader(BaseConstants.ID);
        assertNotNull("Header " + BaseConstants.ID + " is null",id);           
    }     
    
}
