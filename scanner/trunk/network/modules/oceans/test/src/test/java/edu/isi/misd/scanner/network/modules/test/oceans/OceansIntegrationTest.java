package edu.isi.misd.scanner.network.modules.test.oceans;

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
public class OceansIntegrationTest extends CamelSpringTestSupport 
{
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
            "edu/isi/misd/scanner/network/modules/test/oceans/OceansIntegrationTestContext.xml");
    }

    @Test
    public void testOceansLogisticRegressionXML() throws Exception 
    {
        MockEndpoint resultEndpoint = this.getMockEndpoint("mock:result");
        
        // test to ensure correct endpoints in registry
        assertNotNull("Endpoint not present", context.hasEndpoint("direct:edu.isi.misd.scanner.network.modules.master.routes.oceans.OceansLogisticRegressionRoute"));
        assertNotNull("Endpoint not present", context.hasEndpoint("direct:edu.isi.misd.scanner.network.modules.worker.routes.oceans.OceansLogisticRegressionRoute"));

        // test the endpoint using file sample
        InputStream input = this.getClass().getResourceAsStream("OceansLogisticRegressionIntegrationTestInput.xml");
        assertNotNull("The test input inputStream should not be null", input);
        
        InputStream output = this.getClass().getResourceAsStream("OceansLogisticRegressionIntegrationTestOutput.xml");
        assertNotNull("The test output inputStream should not be null", output);
        String outBody = IOUtils.toString(output, "UTF-8");        
        
        // Validate the body
        resultEndpoint.expectedBodiesReceived(outBody);
                
        HashMap headers = new HashMap();
        headers.put(Exchange.HTTP_METHOD, "POST");
        headers.put(Exchange.CONTENT_TYPE, "application/xml");

        String targets = context.resolvePropertyPlaceholders(
            "http://{{worker.address}}:{{worker.port}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka1.csv,"+
            "http://{{worker.address}}:{{worker.port2}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka2.csv,"+
            "http://{{worker.address}}:{{worker.port3}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka3.csv,"+
            "http://{{worker.address}}:{{worker.port4}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka4.csv");
        headers.put(BaseConstants.TARGETS, targets);
        
        template.sendBodyAndHeaders(
            "http4://{{master.address}}:{{master.port}}/{{master.appDomain}}/{{master.appContext}}/oceans/lr",
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
    public void testOceansLogisticRegressionJSON() throws Exception 
    {
        MockEndpoint resultEndpoint = this.getMockEndpoint("mock:result");
        
        // test to ensure correct endpoints in registry
        assertNotNull("Endpoint not present", context.hasEndpoint("direct:edu.isi.misd.scanner.network.modules.master.routes.oceans.OceansLogisticRegressionRoute"));
        assertNotNull("Endpoint not present", context.hasEndpoint("direct:edu.isi.misd.scanner.network.modules.worker.routes.oceans.OceansLogisticRegressionRoute"));

        InputStream input = this.getClass().getResourceAsStream("OceansLogisticRegressionIntegrationTestInput.json");
        assertNotNull("The test input inputStream should not be null", input);

        InputStream output = this.getClass().getResourceAsStream("OceansLogisticRegressionIntegrationTestOutput.json");
        assertNotNull("The test output inputStream should not be null", output);
        String outBody = IOUtils.toString(output, "UTF-8");        
        
        // Validate the body
        resultEndpoint.expectedBodiesReceived(outBody);
        
        HashMap headers = new HashMap();
        headers.put(Exchange.HTTP_METHOD, "POST");
        headers.put(Exchange.CONTENT_TYPE, "application/json");

        String targets = context.resolvePropertyPlaceholders(
            "http://{{worker.address}}:{{worker.port}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka1.csv,"+
            "http://{{worker.address}}:{{worker.port2}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka2.csv,"+
            "http://{{worker.address}}:{{worker.port3}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka3.csv,"+
            "http://{{worker.address}}:{{worker.port4}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka4.csv");
        headers.put(BaseConstants.TARGETS, targets);
        
        sendBody(
            "http4://{{master.address}}:{{master.port}}/{{master.appDomain}}/{{master.appContext}}/oceans/lr", 
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
