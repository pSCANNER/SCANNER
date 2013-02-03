package edu.isi.misd.scanner.network.base.worker.routes;

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
            when().simple("${in.headers.CamelHttpMethod} == 'GET'").
                processRef(getCacheReadProcessorRef()).
                stop().    
            when().simple("${in.headers.CamelHttpMethod} == 'POST'").
                doTry().
                    unmarshal(jaxb).              
                    processRef(getComputeProcessorRef()).
                    marshal(jaxb).                  
                doCatch(Exception.class).
                    process(new ErrorProcessor()).
                    stop().
                end().                 
                processRef(getCacheWriteProcessorRef()).
        end();
    }    
}