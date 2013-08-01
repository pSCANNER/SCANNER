package edu.isi.misd.scanner.network.modules.master.routes.oceans;

import edu.isi.misd.scanner.network.base.master.routes.DefaultRoute;

/**
 * This route overrides various methods of {@link DefaultRoute} to facilitate
 * the processing of aggregate results specific to the OCEANS module.
 */
public class OceansLogisticRegressionRoute extends DefaultRoute 
{
    
    @Override
    public String getJAXBContext() {
        return super.getJAXBContext() + ":" +
            "edu.isi.misd.scanner.network.types.oceans";
    }

    @Override
    public String getJSONUnmarshallType() {
        return "edu.isi.misd.scanner.network.types.oceans.OceansLogisticRegressionRequest";
    }
    
    
}


