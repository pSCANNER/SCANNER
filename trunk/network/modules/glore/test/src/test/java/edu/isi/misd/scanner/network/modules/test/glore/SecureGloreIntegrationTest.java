package edu.isi.misd.scanner.network.modules.test.glore;

import edu.isi.misd.scanner.network.base.test.BaseIntegrationTest;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Tests end-to-end GLORE functionality with a simple set of variables using (HTTPS).
 */
public class SecureGloreIntegrationTest extends BaseIntegrationTest 
{   
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
            "edu/isi/misd/scanner/network/modules/test/glore/SecureGloreIntegrationTestContext.xml");
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        
        // test to ensure correct endpoints in registry
        String masterRoute = 
            "direct:edu.isi.misd.scanner.network.modules.master.routes.glore.GloreRoute";
        assertNotNull(
            "Endpoint not present: " + masterRoute,
            context.hasEndpoint(masterRoute));
        String workerRoute = 
            "direct:edu.isi.misd.scanner.network.modules.worker.routes.glore.GloreRoute";           
        assertNotNull(
            "Endpoint not present: " + workerRoute,
            context.hasEndpoint(workerRoute));
    }
    
    @Override
    protected String getMasterUrl() throws Exception
    {
        String targets = 
             context.resolvePropertyPlaceholders(
                "https4://{{master.address}}:{{master.ssl.port}}/{{master.appDomain}}/{{master.appContext}}/glore/lr?sslContextParametersRef=sslContextParametersClient");            
        return targets;
    }
    
    @Override
    protected String getWorkerUrls() throws Exception
    {
        String targets =  
            context.resolvePropertyPlaceholders(
                "https4://{{worker.address}}:{{worker.ssl.port}}/{{worker.appDomain}}/{{worker.appContext}}/glore/lr?dataSource=ca_part1.csv&sslContextParametersRef=sslContextParametersMaster,"+
                "https4://{{worker.address}}:{{worker.ssl.port2}}/{{worker.appDomain}}/{{worker.appContext}}/glore/lr?dataSource=ca_part2.csv&sslContextParametersRef=sslContextParametersMaster,"+
                "https4://{{worker.address}}:{{worker.ssl.port3}}/{{worker.appDomain}}/{{worker.appContext}}/glore/lr?dataSource=ca_part3.csv&sslContextParametersRef=sslContextParametersMaster");
        return targets;
    }  
    
    //@Test
    public void testSecureGloreLogisticRegressionXML() throws Exception 
    {
        doPost("application/xml",
               "GloreIntegrationTestInput.xml",
               "GloreIntegrationTestOutput.xml");
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);         
    }
    
    @Test
    public void testSecureGloreLogisticRegressionJSON() throws Exception 
    {
        doPost("application/json",
               "GloreIntegrationTestInput.json",
               "GloreIntegrationTestOutput.json");
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);   
    }      
}
