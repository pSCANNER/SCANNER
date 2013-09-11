
package edu.isi.misd.scanner.network.modules.worker.routes.glore;

import edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute;
import java.util.Map;

/**
 * Overrides {@link DefaultRoute#getComputeProcessorRef()} and 
 * {@link DefaultRoute#getJAXBContext()} to provide GLORE-specific functionality.
 */
public class GloreRoute extends DefaultRoute 
{
    @Override
    public String getComputeProcessorRef() {
        return "GloreProcessor";
    }
    
    @Override
    public String getJAXBContext() {
        return super.getJAXBContext() + ":" + 
            "edu.isi.misd.scanner.network.types.glore";
    }    
    
    @Override
    public Map<String,String> getXmlNamespacePrefixMap()
    {
        this.xmlNamespacePrefixMap.put(
            "http://scanner.misd.isi.edu/network/types/glore", "glore");
        return this.xmlNamespacePrefixMap;
    }      
}
