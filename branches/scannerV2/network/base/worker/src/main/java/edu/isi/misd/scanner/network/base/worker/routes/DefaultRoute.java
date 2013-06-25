package edu.isi.misd.scanner.network.base.worker.routes;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils.ErrorProcessor;
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

    protected String getRouteName() {
        return this.getClass().getName();
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
            choice().
                when().simple(HTTP_METHOD_GET_CLAUSE).
                    processRef(getCacheReadProcessorRef()).
                    stop().    
                when().simple(HTTP_METHOD_POST_CLAUSE).
                    doTry().
                        unmarshal(jaxb).              
                        processRef(getComputeProcessorRef()).
                        marshal(jaxb).    
                        removeHeader(BaseConstants.DATASOURCE).            
                    doCatch(Exception.class).
                        process(new ErrorProcessor()).
                        stop().
                    end().                 
                    processRef(getCacheWriteProcessorRef()).
                stop().
            end();
    }    
}