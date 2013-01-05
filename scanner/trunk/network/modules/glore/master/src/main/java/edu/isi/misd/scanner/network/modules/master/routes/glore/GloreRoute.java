package edu.isi.misd.scanner.network.modules.master.routes.glore;

import edu.isi.misd.scanner.network.base.master.routes.DefaultRoute;
import java.util.Map;
import org.apache.camel.Body;
import org.apache.camel.Properties;
import org.apache.camel.builder.ValueBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        return "edu.isi.misd.scanner.network.types.glore";
    }

    @Override
    public String getJSONUnmarshallType() {
        return "edu.isi.misd.scanner.network.types.glore.GloreData";
    }    
    
    @Override    
    public ValueBuilder getDynamicRouter() {
        return method(getClass(),"loopingRoutingSlip");
    }      
}
