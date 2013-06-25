
package edu.isi.misd.scanner.network.modules.worker.routes.example;

import edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute;

/**
 * Overrides {@link DefaultRoute#getComputeProcessorRef()} and 
 * {@link DefaultRoute#getJAXBContext()}.
 */
public class IterationRoute extends DefaultRoute 
{
    @Override
    public String getComputeProcessorRef() {
        return "IterationProcessor";
    }
    
    @Override
    public String getJAXBContext() {
        return "edu.isi.misd.scanner.network.types.example";
    }
}
