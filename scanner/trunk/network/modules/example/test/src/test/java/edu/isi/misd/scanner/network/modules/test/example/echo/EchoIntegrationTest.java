package edu.isi.misd.scanner.network.modules.test.example.echo;

import edu.isi.misd.scanner.network.base.BaseConstants;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 */
public class EchoIntegrationTest extends CamelSpringTestSupport 
{
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
            "edu/isi/misd/scanner/network/modules/test/example/echo/EchoIntegrationTestContext.xml");
    }

    @Test
    public void testEchoXML() throws Exception 
    {
        // test to ensure correct endpoints in registry
        assertNotNull(context.hasEndpoint("direct:edu.isi.misd.scanner.network.base.master.routes.DefaultRoute"));
        assertNotNull(context.hasEndpoint("direct:edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute"));

        // test the endpoint using file sample
        InputStream input = this.getClass().getResourceAsStream("EchoIntegrationTestInput.xml");
        assertNotNull("The test input inputStream should not be null", input);
        
        InputStream output = this.getClass().getResourceAsStream("EchoIntegrationTestOutput.xml");
        assertNotNull("The test output inputStream should not be null", output);
        String outBody = IOUtils.toString(output, "UTF-8");
        MockEndpoint resultEndpoint = this.getMockEndpoint("mock:result");         
        
        // Validate the body
        resultEndpoint.expectedBodiesReceived(outBody);
                
        HashMap headers = new HashMap();
        headers.put(Exchange.HTTP_METHOD, "POST");
        headers.put(Exchange.CONTENT_TYPE, "application/xml");

        String targets = context.resolvePropertyPlaceholders(
            "http://{{worker.address}}:{{worker.port}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo,"+
            "http://{{worker.address}}:{{worker.port2}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo,"+
            "http://{{worker.address}}:{{worker.port3}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo,"+
            "http://{{worker.address}}:{{worker.port4}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo");
        headers.put(BaseConstants.TARGETS, targets);
        
        template.sendBodyAndHeaders(
            "http4://{{master.address}}:{{master.port}}/{{master.appDomain}}/{{master.appContext}}/example/echo",
            input, headers);
        
        // Validate the headers.
        Exchange exchange = resultEndpoint.getReceivedExchanges().get(0);
        String id = (String)exchange.getIn().getHeader(BaseConstants.ID);
        assertNotNull("Header " + BaseConstants.ID + " is null",id); 
        resultEndpoint.expectedHeaderReceived(Exchange.CONTENT_TYPE, "application/xml");
        
        assertMockEndpointsSatisfied(60, TimeUnit.SECONDS);      
    }

    @Test
    public void testEchoJSON() throws Exception 
    {
        // test to ensure correct endpoints in registry
        assertNotNull(context.hasEndpoint("direct:edu.isi.misd.scanner.network.base.master.routes.DefaultRoute"));
        assertNotNull(context.hasEndpoint("direct:edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute"));

        InputStream input = this.getClass().getResourceAsStream("EchoIntegrationTestInput.json");
        assertNotNull("The test input inputStream should not be null", input);

        InputStream output = this.getClass().getResourceAsStream("EchoIntegrationTestOutput.json");
        assertNotNull("The test output inputStream should not be null", output);
        String outBody = IOUtils.toString(output, "UTF-8");
        MockEndpoint resultEndpoint = this.getMockEndpoint("mock:result");         
        
        // Validate the body
        resultEndpoint.expectedBodiesReceived(outBody);
        
        HashMap headers = new HashMap();
        headers.put(Exchange.HTTP_METHOD, "POST");
        headers.put(Exchange.CONTENT_TYPE, "application/json");

        String targets = context.resolvePropertyPlaceholders(
            "http://{{worker.address}}:{{worker.port}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo,"+
            "http://{{worker.address}}:{{worker.port2}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo,"+
            "http://{{worker.address}}:{{worker.port3}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo,"+
            "http://{{worker.address}}:{{worker.port4}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo");
        headers.put(BaseConstants.TARGETS, targets);
        
        sendBody(
            "http4://{{master.address}}:{{master.port}}/{{master.appDomain}}/{{master.appContext}}/example/echo", 
            input, headers);
        
        // Validate the headers.
        Exchange exchange = resultEndpoint.getReceivedExchanges().get(0);
        String id = (String)exchange.getIn().getHeader(BaseConstants.ID);
        assertNotNull("Header " + BaseConstants.ID + " is null",id);
        resultEndpoint.expectedHeaderReceived(Exchange.CONTENT_TYPE, "application/json");
        
        assertMockEndpointsSatisfied(60, TimeUnit.SECONDS);
        
    }    
}
