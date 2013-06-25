
package edu.isi.misd.scanner.network.modules.worker.routes.example;

import edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  This route overrides 
 * {@link edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute#getCacheReadProcessorRef()}
 * and {@link edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute#getCacheWriteProcessorRef()}
 * to use the {@link edu.isi.misd.scanner.network.base.processors.BaseNoOpProcessor}
 * so that echo service responses are not written to nor read from the cache.
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
