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
 * Base class for end-to-end integration tests.  Submits a request to a master 
 * node using a specified input file as the message body, and validates the 
 * resulting output against a specified output file.
 */
public abstract class BaseIntegrationTest extends CamelSpringTestSupport
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseIntegrationTest.class);
    
    /**
     * 
     * @return The URL of the master node to invoke.
     * @throws Exception 
     */
    protected abstract String getMasterUrl() throws Exception;
    
    /**
     * 
     * @return The URLs of the worker nodes to set as the targets.
     * @throws Exception 
     */
    protected abstract String getWorkerUrls() throws Exception;
    
    /**
     * Issues an HTTP POST but does not validate the response.
     * @param contentType The HTTP ContentType for the request and response.
     * @param inputFileName The input file name to use as the parameters.
     * @throws Exception 
     */
    protected void doPost(String contentType,
                          String inputFileName) 
        throws Exception
    {
        doPost(contentType, inputFileName, null, false);
    }  
    
    /**
     * Issues an HTTP POST and validates the response.
     * @param contentType The HTTP ContentType for the request and response.
     * @param inputFileName The input file name to use as the parameters.
     * @param outputFileName The output file name to use for the validation of output.
     * @throws Exception 
     */
    protected void doPost(String contentType,
                          String inputFileName,
                          String outputFileName) 
        throws Exception
    {
        doPost(contentType, inputFileName, outputFileName, false);        
    }    
    
    /**
     * Issues an HTTP POST and validates the response.
     * @param contentType The HTTP ContentType for the request and response.
     * @param inputFileName The input file name to use as the parameters.
     * @param outputFileName The output file name to use for the validation of output.
     * @throws Exception 
     */
    protected void doPost(String contentType,
                          String inputFileName,
                          String outputFileName,
                          boolean asynchronous) 
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
        
        headers.put(BaseConstants.ID, this.getClass().getName());
        String targets = this.getWorkerUrls();
        headers.put(BaseConstants.TARGETS, targets);

        String masterUrl = this.getMasterUrl();
        if (asynchronous) {
            headers.put(BaseConstants.ASYNC, "true");
        }
        try {
            template.sendBodyAndHeaders(masterUrl,input,headers);
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
