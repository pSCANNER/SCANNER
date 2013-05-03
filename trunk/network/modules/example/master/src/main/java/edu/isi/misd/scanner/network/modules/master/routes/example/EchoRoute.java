package edu.isi.misd.scanner.network.modules.master.routes.example;

import edu.isi.misd.scanner.network.base.master.routes.DefaultRoute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class EchoRoute extends DefaultRoute 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(EchoRoute.class);    
    /**
     *
     * @return The name of the processor to use.  It must be found at runtime in the Camel registry.
     */
    @Override
    protected String getCacheReadProcessorRef() {
        return "BaseNoOpProcessor";
    }

    /**
     *
     * @return The name of the processor to use.  It must be found at runtime in the Camel registry.
     */
    @Override
    protected String getCacheWriteProcessorRef() {
        return "BaseNoOpProcessor";
    }     
}
