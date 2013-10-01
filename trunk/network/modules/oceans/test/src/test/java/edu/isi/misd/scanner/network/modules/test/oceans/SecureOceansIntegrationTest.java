package edu.isi.misd.scanner.network.modules.test.oceans;

import edu.isi.misd.scanner.network.base.test.BaseIntegrationTest;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Tests end-to-end OCEANS functionality using HTTPS.
 */
public class SecureOceansIntegrationTest extends BaseIntegrationTest 
{
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
            "edu/isi/misd/scanner/network/modules/test/oceans/SecureOceansIntegrationTestContext.xml");
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
                "https4://{{master.address}}:{{master.ssl.port}}/{{master.appDomain}}/{{master.appContext}}/oceans/lr?sslContextParametersRef=sslContextParametersClient");
        return targets;
    }
    
    @Override
    protected String getWorkerUrls() throws Exception
    {
        String targets =              
            context.resolvePropertyPlaceholders(
                "https4://{{worker.address}}:{{worker.ssl.port}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka1.csv&sslContextParametersRef=sslContextParametersMaster,"+
                "https4://{{worker.address}}:{{worker.ssl.port2}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka2.csv&sslContextParametersRef=sslContextParametersMaster,"+
                "https4://{{worker.address}}:{{worker.ssl.port3}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka3.csv&sslContextParametersRef=sslContextParametersMaster,"+
                "https4://{{worker.address}}:{{worker.ssl.port4}}/{{worker.appDomain}}/{{worker.appContext}}/oceans/lr?dataSource=OutcomePredictionWeka4.csv&sslContextParametersRef=sslContextParametersMaster");
        return targets;
    }  
    
    //@Test
    public void testSecureOceansLogisticRegressionXML() throws Exception 
    {
        doPost("application/xml",
               "OceansLogisticRegressionIntegrationTestInput.xml",
               "SecureOceansLogisticRegressionIntegrationTestOutput.xml");
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);         
    }
    
    @Test
    public void testSecureOceansLogisticRegressionJSON() throws Exception 
    {
        doPost("application/json",
               "OceansLogisticRegressionIntegrationTestInput.json",
               "SecureOceansLogisticRegressionIntegrationTestOutput.json");
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);   
    }       
}
