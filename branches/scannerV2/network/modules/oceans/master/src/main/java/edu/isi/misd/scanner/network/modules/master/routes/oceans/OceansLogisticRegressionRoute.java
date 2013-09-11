package edu.isi.misd.scanner.network.modules.master.routes.oceans;

import edu.isi.misd.scanner.network.base.master.routes.DefaultRoute;
import java.util.Map;

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
    
    @Override
    public Map<String,String> getXmlNamespacePrefixMap()
    {
        this.xmlNamespacePrefixMap.put(
            "http://scanner.misd.isi.edu/network/types/oceans", "oceans");
        return this.xmlNamespacePrefixMap;
    }
}


