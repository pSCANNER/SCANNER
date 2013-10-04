
package edu.isi.misd.scanner.network.modules.worker.routes.ptr;

import edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute;
import java.util.Map;

/**
 * Overrides {@link DefaultRoute#getComputeProcessorRef()} and 
 * {@link DefaultRoute#getJAXBContext()} to provide Prep to Research-specific functionality.
 */
public class PrepToResearchRoute extends DefaultRoute 
{
    @Override
    public String getComputeProcessorRef() {
        return "PrepToResearchProcessor";
    }
    
    @Override
    public String getJAXBContext() {
        return super.getJAXBContext() + 
            ":edu.isi.misd.scanner.network.types.ptr";
    }
    
    @Override
    public Map<String,String> getXmlNamespacePrefixMap()
    {
        this.xmlNamespacePrefixMap.put(
            "http://scanner.misd.isi.edu/network/types/ptr", "ptr");
        return this.xmlNamespacePrefixMap;
    }    
}
