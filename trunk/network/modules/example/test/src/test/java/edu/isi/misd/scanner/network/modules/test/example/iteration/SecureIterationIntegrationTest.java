/*  
 * Copyright 2013 University of Southern California 
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *  
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */ 
package edu.isi.misd.scanner.network.modules.test.example.iteration; 

import edu.isi.misd.scanner.network.base.test.BaseIntegrationTest;
import java.util.concurrent.TimeUnit;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Integration test for example Iteration service (HTTPS).
 *
 * @author Mike D'Arcy 
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
