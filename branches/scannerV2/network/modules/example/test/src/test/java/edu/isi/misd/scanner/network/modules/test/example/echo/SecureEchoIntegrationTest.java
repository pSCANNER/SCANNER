package edu.isi.misd.scanner.network.modules.test.example.echo;

import edu.isi.misd.scanner.network.base.test.BaseIntegrationTest;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Integration test for Echo service (HTTPS)
 */
public class SecureEchoIntegrationTest extends BaseIntegrationTest 
{ 
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() 
    {
        return new ClassPathXmlApplicationContext(
            "edu/isi/misd/scanner/network/modules/test/example/echo/SecureEchoIntegrationTestContext.xml");
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        
        // test to ensure correct endpoints in registry
        String masterRoute = 
            "direct:edu.isi.misd.scanner.network.modules.master.routes.example.EchoRoute";
        assertNotNull(
            "Endpoint not present: " + masterRoute,
            context.hasEndpoint(masterRoute));
        String workerRoute = 
            "direct:edu.isi.misd.scanner.network.modules.worker.routes.example.EchoRoute";            
        assertNotNull(
            "Endpoint not present: " + workerRoute,
            context.hasEndpoint(workerRoute));
    }
    
    @Override
    protected String getMasterUrl() throws Exception
    {
        String targets =
            context.resolvePropertyPlaceholders(
                "https4://{{master.address}}:{{master.ssl.port}}/{{master.appDomain}}/{{master.appContext}}/example/echo?sslContextParametersRef=sslContextParametersClient");
        return targets;
    }
    
    @Override
    protected String getWorkerUrls() throws Exception
    {
        String targets =
            context.resolvePropertyPlaceholders(
                "https4://{{worker.address}}:{{worker.ssl.port}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo?sslContextParametersRef=sslContextParametersMaster,"+
                "https4://{{worker.address}}:{{worker.ssl.port2}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo?sslContextParametersRef=sslContextParametersMaster,"+
                "https4://{{worker.address}}:{{worker.ssl.port3}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo?sslContextParametersRef=sslContextParametersMaster,"+
                "https4://{{worker.address}}:{{worker.ssl.port4}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo?sslContextParametersRef=sslContextParametersMaster");
        return targets;
    } 
       
    @Test       
    public void testSecureEchoXML() throws Exception 
    {
        doPost("application/xml",
               "EchoIntegrationTestInput.xml",
               "SecureEchoIntegrationTestOutput.xml");
        assertMockEndpointsSatisfied(60, TimeUnit.SECONDS);         
    }
     
    @Test    
    public void testSecureEchoJSON() throws Exception 
    {
        doPost("application/json",
               "EchoIntegrationTestInput.json",
               "SecureEchoIntegrationTestOutput.json");
        assertMockEndpointsSatisfied(60, TimeUnit.SECONDS);   
    }         
}
