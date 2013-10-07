package edu.isi.misd.scanner.network.modules.test.ptr;

import edu.isi.misd.scanner.network.base.test.BaseIntegrationTest;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Tests end-to-end Prep to Research functionality using HTTPS.
 */
public class SecurePrepToResearchIntegrationTest extends BaseIntegrationTest 
{
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
            "edu/isi/misd/scanner/network/modules/test/ptr/SecurePrepToResearchIntegrationTestContext.xml");
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        
        // test to ensure correct endpoints in registry
        String masterRoute = 
            "direct:edu.isi.misd.scanner.network.modules.master.routes.ptr.PrepToResearchRoute";
        assertNotNull(
            "Endpoint not present: " + masterRoute,
            context.hasEndpoint(masterRoute));
        String workerRoute = 
            "direct:edu.isi.misd.scanner.network.modules.worker.routes.ptr.PrepToResearchRoute";    
        assertNotNull(
            "Endpoint not present: " + workerRoute,
            context.hasEndpoint(workerRoute));
    }
    
    @Override
    protected String getMasterUrl() throws Exception
    {
        String targets =             
             context.resolvePropertyPlaceholders(
                "https4://{{master.address}}:{{master.ssl.port}}/{{master.appDomain}}/{{master.appContext}}/ptr?sslContextParametersRef=sslContextParametersClient");
        return targets;
    }
    
    @Override
    protected String getWorkerUrls() throws Exception
    {
        String targets =              
            context.resolvePropertyPlaceholders(
                "https4://{{worker.address}}:{{worker.ssl.port}}/{{worker.appDomain}}/{{worker.appContext}}/ptr?dataSource=PTR_SIMULATED_ALTAMED.csv&sslContextParametersRef=sslContextParametersMaster,"+
                "https4://{{worker.address}}:{{worker.ssl.port2}}/{{worker.appDomain}}/{{worker.appContext}}/ptr?dataSource=PTR_SIMULATED_RAND.csv&sslContextParametersRef=sslContextParametersMaster,"+
                "https4://{{worker.address}}:{{worker.ssl.port3}}/{{worker.appDomain}}/{{worker.appContext}}/ptr?dataSource=PTR_SIMULATED_UCSD.csv&sslContextParametersRef=sslContextParametersMaster");
        return targets;
    }  
    
    @Test
    public void testSecurePrepToResearchXML() throws Exception 
    {
        doPost("application/xml",
               "PrepToResearchIntegrationTestInput.xml",
               "SecurePrepToResearchIntegrationTestOutput.xml");
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);         
    }
    
    @Test
    public void testSecurePrepToResearchJSON() throws Exception 
    {
        doPost("application/json",
               "PrepToResearchIntegrationTestInput.json",
               "SecurePrepToResearchIntegrationTestOutput.json");
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);   
    }       
}
