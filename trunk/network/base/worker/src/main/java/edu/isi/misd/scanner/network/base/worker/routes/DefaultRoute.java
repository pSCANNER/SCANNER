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
package edu.isi.misd.scanner.network.base.worker.routes; 

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils.ErrorProcessor;
import edu.isi.misd.scanner.network.base.worker.processors.HoldingDirectoryWriteProcessor;
import edu.isi.misd.scanner.network.base.worker.workflow.ActivitiAuthorizationTaskCreationProcessor;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements most of the default pipeline processing for the worker 
 * network node.  Most "get" methods can be overridden to allow for delegation
 * of specific logic to custom components, rather than duplicating or 
 * re-implementing the core route logic when integrating custom code.
 *
 * @author Mike D'Arcy 
 */
public class DefaultRoute extends RouteBuilder 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(DefaultRoute.class);
        
    protected static final String HTTP_METHOD_GET_CLAUSE = 
        "${in.header.CamelHttpMethod} == 'GET'";
    
    protected static final String HTTP_METHOD_POST_CLAUSE = 
        "${in.header.CamelHttpMethod} == 'POST'";

    protected static final String ASYNC_INVOCATION_CLAUSE = 
        "${in.headers." + BaseConstants.ASYNC + "} == 'true'";

    protected static final String RELEASE_AUTH_INVOCATION_CLAUSE = 
        "${in.headers." + BaseConstants.RESULTS_RELEASE_AUTH_REQUIRED + 
        "} == 'true'";
        
    protected Map<String,String> xmlNamespacePrefixMap = 
        initXmlNamespacePrefixMap();
    
    protected String getRouteName() {
        return this.getClass().getName();
    }
 
    protected String getComputeRouteName() {
        return getRouteName() + "Compute";
    }
  
    protected String getQueueRouteName() {
        return getRouteName() + "Queue";
    }
       
    protected String getCacheRouteName() {
        return getRouteName() + "Cache";
    }
    
    protected String getGETRouteName() {
        return getRouteName() + "Get";
    }     
    
    protected String getPOSTRouteName() {
        return getRouteName() + "Post";
    } 
    
    public String getComputeProcessorRef() {
        return "BaseComputeProcessor";
    }      
       
    protected String getCacheReadProcessorRef() {
        return "BaseWorkerCacheReadProcessor";
    }

    protected String getCacheWriteProcessorRef() {
        return "BaseWorkerCacheWriteProcessor";
    }
    
    protected String getBaseRequestProcessorRef() {
        return "BaseRequestProcessor";
    }
    
    protected String getAsyncResponseProcessorRef() {
        return "BaseWorkerAsyncResponseProcessor";
    }
    
    public String getJAXBContext() {
        return "edu.isi.misd.scanner.network.types.base";
    }
    
    protected Map<String,String> initXmlNamespacePrefixMap() {
        return new HashMap<String,String>(
            BaseConstants.BASE_XML_NAMESPACE_PREFIX_MAP);
    }  
    
    protected Map<String,String> getXmlNamespacePrefixMap() {
        return xmlNamespacePrefixMap;
    }
    
    /**
     * Sets up the route using Camel Java DSL.  Creates routes to handle HTTP 
     * GET/POST requests and process those requests via delegated methods.
     * 
     * @throws Exception
     */
    @Override
    public void configure() throws Exception 
    {
        JaxbDataFormat jaxb = new JaxbDataFormat();     
        jaxb.setFragment(true);
        jaxb.setPrettyPrint(false);        
        jaxb.setIgnoreJAXBElement(false);
        jaxb.setContextPath(getJAXBContext());
        jaxb.setNamespacePrefix(getXmlNamespacePrefixMap());
        
        from("direct:" + getRouteName()).
            processRef(getBaseRequestProcessorRef()).
            choice().
                when().simple(HTTP_METHOD_GET_CLAUSE).
                    to("direct:" + getGETRouteName()).
                when().simple(HTTP_METHOD_POST_CLAUSE).   
                    to("direct:" + getPOSTRouteName()).
            end();
        
        from("direct:" + getGETRouteName()).
            onException(FileNotFoundException.class).
            maximumRedeliveries(0).
            handled(true).
            end().  
            processRef(getCacheReadProcessorRef()).
            stop();
        
        from("direct:" + getPOSTRouteName()).
            unmarshal(jaxb). 
            choice().
                when().simple(ASYNC_INVOCATION_CLAUSE).
                    to("seda:" + getQueueRouteName() +
                       "?waitForTaskToComplete=Never").
                    processRef(getAsyncResponseProcessorRef()).
                    marshal(jaxb).
                    processRef(getCacheWriteProcessorRef()).
                otherwise().
                    to("direct:" + getComputeRouteName()).
            end();
        
        from("seda:" + getQueueRouteName()).
            to("direct:" + getComputeRouteName()). 
            choice().
                when().simple(RELEASE_AUTH_INVOCATION_CLAUSE).
                    process(new ActivitiAuthorizationTaskCreationProcessor()).
                end().
            end();  
        
        // run the computation processor        
        from("direct:" + getComputeRouteName()).                    
            doTry().              
                processRef(getComputeProcessorRef()).             
            doCatch(Exception.class).
                process(new ErrorProcessor()).
                //stop().
            doFinally().
                removeHeader(BaseConstants.DATASOURCE).  
            end().
            to("direct:" + getCacheRouteName());
        
        // write the results to the output cache
        from("direct:" + getCacheRouteName()). 
            onException(Exception.class).
            maximumRedeliveries(0).
            handled(true).
            process(new ErrorProcessor()).
            stop().
            end().              
            marshal(jaxb).            
            choice().
                when().simple(RELEASE_AUTH_INVOCATION_CLAUSE).
                    process(new HoldingDirectoryWriteProcessor()).
                otherwise().
                    processRef(getCacheWriteProcessorRef()).
            end();                                                               
    }    
}