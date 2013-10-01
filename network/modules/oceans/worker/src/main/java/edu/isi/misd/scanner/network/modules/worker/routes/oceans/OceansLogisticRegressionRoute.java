
package edu.isi.misd.scanner.network.modules.worker.routes.oceans;

import edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute;
import java.util.Map;

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
        return super.getJAXBContext() + 
            ":edu.isi.misd.scanner.network.types.oceans";
    }
    
    @Override
    public Map<String,String> getXmlNamespacePrefixMap()
    {
        this.xmlNamespacePrefixMap.put(
            "http://scanner.misd.isi.edu/network/types/oceans", "oceans");
        return this.xmlNamespacePrefixMap;
    }    
}
