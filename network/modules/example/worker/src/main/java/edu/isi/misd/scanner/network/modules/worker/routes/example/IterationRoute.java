
package edu.isi.misd.scanner.network.modules.worker.routes.example;

import edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute;

/**
 *
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
