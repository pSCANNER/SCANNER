package edu.isi.misd.scanner.network.modules.test.glore;

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
public class GloreIntegrationTest extends CamelSpringTestSupport 
{
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
            "edu/isi/misd/scanner/network/modules/test/glore/GLoreIntegrationTestContext.xml");
    }

    @Test
    public void testGloreLogisticRegressionXML() throws Exception 
    {
        MockEndpoint resultEndpoint = this.getMockEndpoint("mock:result");
        
        // test to ensure correct endpoints in registry
        assertNotNull("Endpoint not present", context.hasEndpoint("direct:edu.isi.misd.scanner.network.modules.master.routes.glore.GloreRoute"));
        assertNotNull("Endpoint not present", context.hasEndpoint("direct:edu.isi.misd.scanner.network.modules.worker.routes.glore.GloreRoute"));

        // test the endpoint using file sample
        InputStream input = this.getClass().getResourceAsStream("GloreIntegrationTestInput.xml");
        assertNotNull("The test input inputStream should not be null", input);
        
        InputStream output = this.getClass().getResourceAsStream("GloreIntegrationTestOutput.xml");
        assertNotNull("The test output inputStream should not be null", output);
        String outBody = IOUtils.toString(output, "UTF-8");        
        
        // Validate the body
        resultEndpoint.expectedBodiesReceived(outBody);
                
        HashMap headers = new HashMap();
        headers.put(Exchange.HTTP_METHOD, "POST");
        headers.put(Exchange.CONTENT_TYPE, "application/xml");

        String targets = context.resolvePropertyPlaceholders(
            "http://{{worker.address}}:{{worker.port}}/{{worker.appDomain}}/{{worker.appContext}}/glore/lr?dataSource=ca_part1.csv,"+
            "http://{{worker.address}}:{{worker.port2}}/{{worker.appDomain}}/{{worker.appContext}}/glore/lr?dataSource=ca_part2.csv,"+
            "http://{{worker.address}}:{{worker.port3}}/{{worker.appDomain}}/{{worker.appContext}}/glore/lr?dataSource=ca_part3.csv");
        headers.put(BaseConstants.TARGETS, targets);
        
        template.sendBodyAndHeaders(
            "http4://{{master.address}}:{{master.port}}/{{master.appDomain}}/{{master.appContext}}/glore/lr",
            input, headers);
        
        // Validate the headers.
        Exchange exchange = resultEndpoint.getReceivedExchanges().get(0);
        String id = (String)exchange.getIn().getHeader(BaseConstants.ID);
        assertNotNull("Header " + BaseConstants.ID + " is null",id); 
        resultEndpoint.expectedHeaderReceived(Exchange.CONTENT_TYPE, "application/xml");
        
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS); 
        
        //template.send("stream:out",exchange);
    }

    @Test
    public void testGloreLogisticRegressionJSON() throws Exception 
    {
        MockEndpoint resultEndpoint = this.getMockEndpoint("mock:result");
        
        // test to ensure correct endpoints in registry
        assertNotNull("Endpoint not present", context.hasEndpoint("direct:edu.isi.misd.scanner.network.modules.master.routes.glore.GloreRoute"));
        assertNotNull("Endpoint not present", context.hasEndpoint("direct:edu.isi.misd.scanner.network.modules.worker.routes.glore.GloreRoute"));

        InputStream input = this.getClass().getResourceAsStream("GloreIntegrationTestInput.json");
        assertNotNull("The test input inputStream should not be null", input);

        InputStream output = this.getClass().getResourceAsStream("GloreIntegrationTestOutput.json");
        assertNotNull("The test output inputStream should not be null", output);
        String outBody = IOUtils.toString(output, "UTF-8");        
        
        // Validate the body
        resultEndpoint.expectedBodiesReceived(outBody);
        
        HashMap headers = new HashMap();
        headers.put(Exchange.HTTP_METHOD, "POST");
        headers.put(Exchange.CONTENT_TYPE, "application/json");

        String targets = context.resolvePropertyPlaceholders(
            "http://{{worker.address}}:{{worker.port}}/{{worker.appDomain}}/{{worker.appContext}}/glore/lr?dataSource=ca_part1.csv,"+
            "http://{{worker.address}}:{{worker.port2}}/{{worker.appDomain}}/{{worker.appContext}}/glore/lr?dataSource=ca_part2.csv,"+
            "http://{{worker.address}}:{{worker.port3}}/{{worker.appDomain}}/{{worker.appContext}}/glore/lr?dataSource=ca_part3.csv");
        headers.put(BaseConstants.TARGETS, targets);
        
        sendBody(
            "http4://{{master.address}}:{{master.port}}/{{master.appDomain}}/{{master.appContext}}/glore/lr", 
            input, headers);
        
        // Validate the headers.
        Exchange exchange = resultEndpoint.getReceivedExchanges().get(0);
        String id = (String)exchange.getIn().getHeader(BaseConstants.ID);
        assertNotNull("Header " + BaseConstants.ID + " is null",id);
        resultEndpoint.expectedHeaderReceived(Exchange.CONTENT_TYPE, "application/json");
        
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);
        
        //template.send("stream:out",exchange);
    }    
}
