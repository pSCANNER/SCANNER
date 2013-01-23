package edu.isi.misd.scanner.network.modules.master.routes.oceans;

import edu.isi.misd.scanner.network.base.master.routes.DefaultRoute;

/**
 *
 */
public class OceansLogisticRegressionRoute extends DefaultRoute 
{

    @Override
    protected String getPostAggregationProcessorRef() {
        return "OceansLogisticRegressionAggregateProcessor";
    }
    
    @Override
    public String getJAXBContext() {
        return "edu.isi.misd.scanner.network.types.oceans";
    }

    @Override
    public String getJSONUnmarshallType() {
        return "edu.isi.misd.scanner.network.types.oceans.OceansLogisticRegressionParameters";
    }
    
    
}


