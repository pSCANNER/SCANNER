package edu.isi.misd.scanner.network.base.worker.routes;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils.ErrorProcessor;
import edu.isi.misd.scanner.network.base.worker.workflow.ActivitiAuthorizationTaskCreationProcessor;
import java.io.FileNotFoundException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements most of the default pipeline processing for the worker 
 * network node.  Most "get" methods can be overridden to allow for delegation
 * of specific logic to custom components, rather than duplicating or 
 * re-implementing the core route logic when integrating custom code.
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
    
    protected String getRouteName() {
        return this.getClass().getName();
    }
 
    protected String getComputeRouteName() {
        return getRouteName() + "Compute";
    }
  
    protected String getQueueRouteName() {
        return getRouteName() + "Queue";
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
        jaxb.setPrettyPrint(true);        
        jaxb.setIgnoreJAXBElement(false);
        jaxb.setContextPath(getJAXBContext());           
        
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
        
        from("direct:" + getComputeRouteName()).                    
            // 1. run the computation processor
            doTry().              
                processRef(getComputeProcessorRef()).
                marshal(jaxb).              
            doCatch(Exception.class).
                process(new ErrorProcessor()).
                stop().
            doFinally().
                removeHeader(BaseConstants.DATASOURCE).  
            end().  
            // 2. write the results to the output cache
            doTry().             
                processRef(getCacheWriteProcessorRef()).
            doCatch(Exception.class).
                process(new ErrorProcessor()).
                stop(). 
            end();                                                      
    }    
}