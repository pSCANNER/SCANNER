package edu.isi.misd.scanner.network.modules.test.example.echo;

import edu.isi.misd.scanner.network.base.test.BaseIntegrationTest;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 */
public class EchoIntegrationTest extends BaseIntegrationTest 
{ 
    @Override
    protected AbstractXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(
            "edu/isi/misd/scanner/network/modules/test/example/echo/EchoIntegrationTestContext.xml");
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
        String targets 
            =  context.resolvePropertyPlaceholders(
                "http4://{{master.address}}:{{master.port}}/{{master.appDomain}}/{{master.appContext}}/example/echo");
        return targets;
    }
    
    @Override
    protected String getWorkerUrls() throws Exception
    {
        String targets =
            context.resolvePropertyPlaceholders(
            "http4://{{worker.address}}:{{worker.port}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo,"+
            "http4://{{worker.address}}:{{worker.port2}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo,"+
            "http4://{{worker.address}}:{{worker.port3}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo,"+
            "http4://{{worker.address}}:{{worker.port4}}/{{worker.appDomain}}/{{worker.appContext}}/example/echo");
        return targets;
    } 
    
    @Test       
    public void testEchoXML() throws Exception 
    {
        doPost("application/xml",
               "EchoIntegrationTestInput.xml",
               "EchoIntegrationTestOutput.xml");
        assertMockEndpointsSatisfied(60, TimeUnit.SECONDS);         
    }
   
    @Test    
    public void testEchoJSON() throws Exception 
    {
        doPost("application/json",
               "EchoIntegrationTestInput.json",
               "EchoIntegrationTestOutput.json");
        assertMockEndpointsSatisfied(60, TimeUnit.SECONDS);   
    }         
}
