/*  
 * Copyright 2013 University of Southern California 
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *  
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */ 
package edu.isi.misd.scanner.network.modules.master.routes.ptr; 

import edu.isi.misd.scanner.network.base.master.routes.DefaultRoute;
import java.util.Map;

/**
 * This route overrides various methods of {@link DefaultRoute} to facilitate
 * the processing of aggregate results specific to the Prep to Research module.
 *
 * @author Mike D'Arcy 
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


