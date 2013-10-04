package edu.isi.misd.scanner.network.modules.test.ptr;

import edu.isi.misd.scanner.network.base.test.BaseIntegrationTest;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Tests end-to-end Prep to Research functionality.
 */
public class PrepToResearchIntegrationTest extends BaseIntegrationTest 
{
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
            "edu/isi/misd/scanner/network/modules/test/ptr/PrepToResearchIntegrationTestContext.xml");
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
                "http4://{{master.address}}:{{master.port}}/{{master.appDomain}}/{{master.appContext}}/ptr");
        return targets;
    }
    
    @Override
    protected String getWorkerUrls() throws Exception
    {
        String targets =              
            context.resolvePropertyPlaceholders(
                "http4://{{worker.address}}:{{worker.port}}/{{worker.appDomain}}/{{worker.appContext}}/ptr?dataSource=ptr_test_sample_1.csv,"+
                "http4://{{worker.address}}:{{worker.port2}}/{{worker.appDomain}}/{{worker.appContext}}/ptr?dataSource=ptr_test_sample_1.csv,"+
                "http4://{{worker.address}}:{{worker.port3}}/{{worker.appDomain}}/{{worker.appContext}}/ptr?dataSource=ptr_test_sample_1.csv,"+
                "http4://{{worker.address}}:{{worker.port4}}/{{worker.appDomain}}/{{worker.appContext}}/ptr?dataSource=ptr_test_sample_1.csv");  
        return targets;
    }  
    
    @Test
    public void testPrepToResearchXML() throws Exception 
    {
        doPost("application/xml",
               "PrepToResearchIntegrationTestInput.xml",
               "PrepToResearchIntegrationTestOutput.xml");
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);         
    }
    
    @Test
    public void testPrepToResearchJSON() throws Exception 
    {
        doPost("application/json",
               "PrepToResearchIntegrationTestInput.json",
               "PrepToResearchIntegrationTestOutput.json");
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);   
    }       
}
