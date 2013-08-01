package edu.isi.misd.scanner.network.modules.test.oceans;

import edu.isi.misd.scanner.network.base.test.BaseIntegrationTest;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Tests end-to-end OCEANS functionality.
 */
public class OceansIntegrationTest extends BaseIntegrationTest 
{
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
            "edu/isi/misd/scanner/network/modules/test/oceans/OceansIntegrationTestContext.xml");
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        
        // test to ensure correct endpoints in registry
        String masterRoute = 
            "direct:edu.isi.misd.scanner.network.modules.master.routes.oceans.OceansLogisticRegressionRoute";
        assertNotNull(
            "Endpoint not present: " + masterRoute,
            context.hasEndpoint(masterRoute));
        String workerRoute = 
            "direct:edu.isi.misd.scanner.network.modules.worker.routes.oceans.OceansLogisticRegressionRoute";    
        assertNotNull(
            "Endpoint not present: " + workerRoute,
            context.hasEndpoint(workerRoute));
    }
    
    @Override
    protected String getMasterUrl() throws Exception
    {
        String targets =             
             context.resolvePropertyPlaceholders(
                "http4://{{master.address}}:{{master.port}}/{{master.appDomain}}/{{master.appContext}}/oceans/lr");
        return targets;
    }
    
    @Override
    protected String getWorkerUrls() throws Exception
    {
        String targets =              
            context.resolvePropertyPlaceholders(
                "http4://{{worker.address}}:{{worker.port}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka1.csv,"+
                "http4://{{worker.address}}:{{worker.port2}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka2.csv,"+
                "http4://{{worker.address}}:{{worker.port3}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka3.csv,"+
                "http4://{{worker.address}}:{{worker.port4}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka4.csv");  
        return targets;
    }  
    
    @Test
    public void testOceansLogisticRegressionXML() throws Exception 
    {
        doPost("application/xml",
               "OceansLogisticRegressionIntegrationTestInput.xml",
               "OceansLogisticRegressionIntegrationTestOutput.xml");
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);         
    }
    
    @Test
    public void testOceansLogisticRegressionJSON() throws Exception 
    {
        doPost("application/json",
               "OceansLogisticRegressionIntegrationTestInput.json",
               "OceansLogisticRegressionIntegrationTestOutput.json");
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);   
    }       
}
