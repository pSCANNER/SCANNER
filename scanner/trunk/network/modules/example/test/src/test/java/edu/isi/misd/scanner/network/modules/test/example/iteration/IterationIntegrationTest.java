package edu.isi.misd.scanner.network.modules.test.example.iteration;

import edu.isi.misd.scanner.network.base.BaseConstants;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 */
public class IterationIntegrationTest extends CamelSpringTestSupport 
{
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
            "edu/isi/misd/scanner/network/modules/test/example/iteration/IterationIntegrationTestContext.xml");
    }

    @Test
    public void testIterationXML() throws Exception 
    {
        MockEndpoint resultEndpoint = this.getMockEndpoint("mock:result");    
        
        // test to ensure correct endpoints in registry
        assertNotNull(context.hasEndpoint("direct:edu.isi.misd.scanner.network.modules.master.routes.example.IterationRoute"));
        assertNotNull(context.hasEndpoint("direct:edu.isi.misd.scanner.network.modules.worker.routes.example.IterationRoute"));

        // test the endpoint using file sample
        InputStream input = this.getClass().getResourceAsStream("IterationIntegrationTestInput.xml");
        assertNotNull("The test input inputStream should not be null", input);
         
        // Validate the body
        resultEndpoint.expectedBodyReceived().body().contains("'<status>complete</status>'"); 
                
        HashMap headers = new HashMap();
        headers.put(Exchange.HTTP_METHOD, "POST");
        headers.put(Exchange.CONTENT_TYPE, "application/xml");

        String targets = context.resolvePropertyPlaceholders(
            "http://{{worker.address}}:{{worker.port}}/{{worker.appDomain}}/{{worker.appContext}}/example/iteration,"+
            "http://{{worker.address}}:{{worker.port2}}/{{worker.appDomain}}/{{worker.appContext}}/example/iteration,"+
            "http://{{worker.address}}:{{worker.port3}}/{{worker.appDomain}}/{{worker.appContext}}/example/iteration,"+
            "http://{{worker.address}}:{{worker.port4}}/{{worker.appDomain}}/{{worker.appContext}}/example/iteration");
        headers.put(BaseConstants.TARGETS, targets);
        
        template.sendBodyAndHeaders(
            "http4://{{master.address}}:{{master.port}}/{{master.appDomain}}/{{master.appContext}}/example/iteration",
            input, headers);
        
        // Validate the headers.
        Exchange exchange = resultEndpoint.getReceivedExchanges().get(0);
        String id = (String)exchange.getIn().getHeader(BaseConstants.ID);
        assertNotNull("Header " + BaseConstants.ID + " is null",id); 
        resultEndpoint.expectedHeaderReceived(Exchange.CONTENT_TYPE, "application/xml");
        
        assertMockEndpointsSatisfied(300, TimeUnit.SECONDS);        
    }

    @Test
    public void testIterationJSON() throws Exception 
    {
        MockEndpoint resultEndpoint = this.getMockEndpoint("mock:result"); 
        
        // test to ensure correct endpoints in registry
        assertNotNull(context.hasEndpoint("direct:edu.isi.misd.scanner.network.modules.master.routes.example.IterationRoute"));
        assertNotNull(context.hasEndpoint("direct:edu.isi.misd.scanner.network.modules.worker.routes.example.IterationRoute"));

        InputStream input = this.getClass().getResourceAsStream("IterationIntegrationTestInput.json");
        assertNotNull("The test input inputStream should not be null", input);  
   
        // Validate the body
        resultEndpoint.expectedBodyReceived().body().contains("'\"status\":\"complete\"'");
        
        HashMap headers = new HashMap();
        headers.put(Exchange.HTTP_METHOD, "POST");
        headers.put(Exchange.CONTENT_TYPE, "application/json");

        String targets = context.resolvePropertyPlaceholders(
            "http://{{worker.address}}:{{worker.port}}/{{worker.appDomain}}/{{worker.appContext}}/example/iteration,"+
            "http://{{worker.address}}:{{worker.port2}}/{{worker.appDomain}}/{{worker.appContext}}/example/iteration,"+
            "http://{{worker.address}}:{{worker.port3}}/{{worker.appDomain}}/{{worker.appContext}}/example/iteration,"+
            "http://{{worker.address}}:{{worker.port4}}/{{worker.appDomain}}/{{worker.appContext}}/example/iteration");
        headers.put(BaseConstants.TARGETS, targets);
        
        sendBody(
            "http4://{{master.address}}:{{master.port}}/{{master.appDomain}}/{{master.appContext}}/example/iteration", 
            input, headers);
        
        // Validate the headers.
        Exchange exchange = resultEndpoint.getReceivedExchanges().get(0);
        String id = (String)exchange.getIn().getHeader(BaseConstants.ID);
        assertNotNull("Header " + BaseConstants.ID + " is null",id);
        resultEndpoint.expectedHeaderReceived(Exchange.CONTENT_TYPE, "application/json");
        
        assertMockEndpointsSatisfied(300, TimeUnit.SECONDS);                
    }    
}
