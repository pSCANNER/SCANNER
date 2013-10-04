package edu.isi.misd.scanner.network.modules.master.routes.ptr;

import edu.isi.misd.scanner.network.base.master.routes.DefaultRoute;
import java.util.Map;

/**
 * This route overrides various methods of {@link DefaultRoute} to facilitate
 * the processing of aggregate results specific to the Prep to Research module.
 */
public class PrepToResearchRoute extends DefaultRoute 
{
    
    @Override
    public String getJAXBContext() {
        return super.getJAXBContext() + ":" +
            "edu.isi.misd.scanner.network.types.ptr";
    }

    @Override
    public String getJSONUnmarshallType() {
        return "edu.isi.misd.scanner.network.types.ptr.PrepToResearchRequest";
    }
    
    @Override
    public Map<String,String> getXmlNamespacePrefixMap()
    {
        this.xmlNamespacePrefixMap.put(
            "http://scanner.misd.isi.edu/network/types/ptr", "ptr");
        return this.xmlNamespacePrefixMap;
    }
}


