
package edu.isi.misd.scanner.network.modules.worker.routes.oceans;

import edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute;

/**
 * Overrides {@link DefaultRoute#getComputeProcessorRef()} and 
 * {@link DefaultRoute#getJAXBContext()} to provide OCEANS-specific functionality.
 */
public class OceansLogisticRegressionRoute extends DefaultRoute 
{
    @Override
    public String getComputeProcessorRef() {
        return "OceansLogisticRegressionProcessor";
    }
    
    @Override
    public String getJAXBContext() {
        return "edu.isi.misd.scanner.network.types.oceans";
    }
}
