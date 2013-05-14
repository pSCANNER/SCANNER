package edu.isi.misd.scanner.network.modules.test.glore;

import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Tests end-to-end GLORE functionality with a complex set of variables.
 */
public class GloreVariableTest extends GloreIntegrationTest 
{       
    @Override
    protected String getWorkerUrls() throws Exception
    {
        String targets =              
            context.resolvePropertyPlaceholders(
            "http4://{{worker.address}}:{{worker.port}}/{{worker.appDomain}}/{{worker.appContext}}/glore/lr?dataSource=MTM_SIMULATED_1.csv,"+
            "http4://{{worker.address}}:{{worker.port2}}/{{worker.appDomain}}/{{worker.appContext}}/glore/lr?dataSource=MTM_SIMULATED_2.csv,"+
            "http4://{{worker.address}}:{{worker.port3}}/{{worker.appDomain}}/{{worker.appContext}}/glore/lr?dataSource=MTM_SIMULATED_3.csv");
        return targets;
    }  
    
    @Test
    @Override
    public void testGloreLogisticRegressionXML() throws Exception 
    {
        doPost("application/xml",
               "GloreVariableTestInput1.xml",
               "GloreVariableTestOutput1.xml");
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);         
    }
    
    @Test
    @Override
    public void testGloreLogisticRegressionJSON() throws Exception 
    {
        doPost("application/json",
               "GloreVariableTestInput1.json",
               "GloreVariableTestOutput1.json");
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);   
    }     
    
    @Test
    public void testGloreVariablesXML() throws Exception 
    {
        doPost("application/xml",
               "GloreVariableTestInput2.xml",
               "GloreVariableTestOutput2.xml");     
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);      
    }
    
    @Test
    public void testGloreVariablesJSON() throws Exception 
    {
        doPost("application/json",
               "GloreVariableTestInput2.json",
               "GloreVariableTestOutput2.json");    
        assertMockEndpointsSatisfied(120, TimeUnit.SECONDS);    
    }         
}
