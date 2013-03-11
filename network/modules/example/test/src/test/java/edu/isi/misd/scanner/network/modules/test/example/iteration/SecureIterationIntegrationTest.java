package edu.isi.misd.scanner.network.modules.test.example.iteration;

import edu.isi.misd.scanner.network.base.test.BaseIntegrationTest;
import java.util.concurrent.TimeUnit;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 */
public class SecureIterationIntegrationTest extends BaseIntegrationTest 
{
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
            "edu/isi/misd/scanner/network/modules/test/example/iteration/SecureIterationIntegrationTestContext.xml");
    }   
    
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        
        // test to ensure correct endpoints in registry
        String masterRoute = 
            "direct:edu.isi.misd.scanner.network.modules.master.routes.example.IterationRoute";
        assertNotNull(
            "Endpoint not present: " + masterRoute,
            context.hasEndpoint(masterRoute));
        String workerRoute = 
            "direct:edu.isi.misd.scanner.network.modules.worker.routes.example.IterationRoute";        
        assertNotNull(
            "Endpoint not present: " + workerRoute,
            context.hasEndpoint(workerRoute));   
    }
    
    @Override
    protected String getMasterUrl() throws Exception
    {
        String targets = 
            context.resolvePropertyPlaceholders(
                "https4://{{master.address}}:{{master.ssl.port}}/{{master.appDomain}}/{{master.appContext}}/example/iteration?sslContextParametersRef=sslContextParametersClient");
        return targets;
    }
    
    @Override
    protected String getWorkerUrls() throws Exception
    {
        String targets = 
            context.resolvePropertyPlaceholders(
                "https4://{{worker.address}}:{{worker.ssl.port}}/{{worker.appDomain}}/{{worker.appContext}}/example/iteration?sslContextParametersRef=sslContextParametersMaster,"+
                "https4://{{worker.address}}:{{worker.ssl.port2}}/{{worker.appDomain}}/{{worker.appContext}}/example/iteration?sslContextParametersRef=sslContextParametersMaster,"+
                "https4://{{worker.address}}:{{worker.ssl.port3}}/{{worker.appDomain}}/{{worker.appContext}}/example/iteration?sslContextParametersRef=sslContextParametersMaster,"+
                "https4://{{worker.address}}:{{worker.ssl.port4}}/{{worker.appDomain}}/{{worker.appContext}}/example/iteration?sslContextParametersRef=sslContextParametersMaster");
        return targets;
    }    

    protected void checkOutput() throws Exception
    {
        MockEndpoint resultEndpoint = this.getMockEndpoint("mock:result");         
        // set the body validation to test for the presence of the status=complete value        
        resultEndpoint.expectedBodyReceived().body().contains("'\"status\":\"complete\"'");        
        assertMockEndpointsSatisfied(300, TimeUnit.SECONDS);            
    } 
    
    @Test
    public void testSecureIterationXML() throws Exception 
    {
        doPost("application/xml","IterationIntegrationTestInput.xml");
        checkOutput();
    }
        
    @Test
    public void testSecureIterationJSON() throws Exception 
    {
        doPost("application/json","IterationIntegrationTestInput.json");
        checkOutput();       
    }           
}
