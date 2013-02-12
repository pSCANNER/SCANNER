package edu.isi.misd.scanner.network.base.test;

import edu.isi.misd.scanner.network.base.BaseConstants;
import java.io.InputStream;
import java.util.HashMap;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.apache.commons.io.IOUtils;


/**
 *
 */
public abstract class BaseIntegrationTest extends CamelSpringTestSupport
{
  
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

        String targets = this.getWorkerUrls();
        headers.put(BaseConstants.TARGETS, targets);

        sendBody(this.getMasterUrl(),input,headers);
        
        // Validate the headers.
        Exchange exchange = resultEndpoint.getReceivedExchanges().get(0);
        String id = (String)exchange.getIn().getHeader(BaseConstants.ID);
        assertNotNull("Header " + BaseConstants.ID + " is null",id);
        resultEndpoint.expectedHeaderReceived(Exchange.CONTENT_TYPE, contentType);            
    }     
    
}
