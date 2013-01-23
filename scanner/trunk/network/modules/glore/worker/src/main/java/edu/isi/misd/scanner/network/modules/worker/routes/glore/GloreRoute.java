
package edu.isi.misd.scanner.network.modules.worker.routes.glore;

import edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute;

/**
 *
 */
public class GloreRoute extends DefaultRoute 
{
    @Override
    public String getComputeProcessorRef() {
        return "GloreProcessor";
    }
    
    @Override
    public String getJAXBContext() {
        return "edu.isi.misd.scanner.network.types.glore";
    }    
}
