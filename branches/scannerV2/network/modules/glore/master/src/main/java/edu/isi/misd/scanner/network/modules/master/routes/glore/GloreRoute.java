package edu.isi.misd.scanner.network.modules.master.routes.glore;

import edu.isi.misd.scanner.network.base.master.routes.DefaultRoute;
import java.util.Map;
import org.apache.camel.builder.ValueBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This route overrides various methods of {@link DefaultRoute} to facilitate
 * the processing of aggregate results specific to the GLORE module.
 * It also makes use of a looping routing slip as part of Camel's Dynamic Router
 * EIP, in order to provide the "iterating" behavior.
 */
public class GloreRoute extends DefaultRoute 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(GloreRoute.class);
    
    @Override
    protected String getPostAggregationProcessorRef() {
        return "GloreAggregateProcessor";
    }    
    
    @Override
    public String getJAXBContext() {
        return super.getJAXBContext() + ":" + 
            "edu.isi.misd.scanner.network.types.glore";
    }    

    @Override
    public String getJSONUnmarshallType() {
        return "edu.isi.misd.scanner.network.types.glore.GloreLogisticRegressionRequest";
    }    
    
    @Override
    public Map<String,String> getXmlNamespacePrefixMap()
    {
        this.xmlNamespacePrefixMap.put(
            "http://scanner.misd.isi.edu/network/types/glore", "glore");
        return this.xmlNamespacePrefixMap;
    }  
    
    @Override    
    public ValueBuilder getDynamicRouter() {
        return method(getClass(),"loopingRoutingSlip");
    }      
}
