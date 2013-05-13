package edu.isi.misd.scanner.network.base.worker.routes;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils.ErrorProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class DefaultRoute extends RouteBuilder 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(DefaultRoute.class);
        
    protected static final String HTTP_METHOD_GET_CLAUSE = 
        "${in.header.CamelHttpMethod} == 'GET'";
    
    protected static final String HTTP_METHOD_POST_CLAUSE = 
        "${in.header.CamelHttpMethod} == 'POST'";
    
    /**
     *
     * @return
     */
    protected String getRouteName() {
        return this.getClass().getName();
    }
    
    /**
     *
     * @return
     */
    public String getComputeProcessorRef() {
        return "BaseComputeProcessor";
    }      
       
    /**
     *
     * @return
     */
    protected String getCacheReadProcessorRef() {
        return "BaseWorkerCacheReadProcessor";
    }

    /**
     *
     * @return
     */
    protected String getCacheWriteProcessorRef() {
        return "BaseWorkerCacheWriteProcessor";
    }
    
    /**
     *
     * @return
     */
    public String getJAXBContext() {
        return "edu.isi.misd.scanner.network.types.base";
    }
    
    /**
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