
package edu.isi.misd.scanner.network.modules.worker.routes.oceans;

import edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute;

/**
 *
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
