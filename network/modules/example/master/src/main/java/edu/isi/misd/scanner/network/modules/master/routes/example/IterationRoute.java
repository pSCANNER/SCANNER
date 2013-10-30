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
package edu.isi.misd.scanner.network.modules.master.routes.example; 

import edu.isi.misd.scanner.network.base.master.routes.DefaultRoute;
import java.util.Map;
import org.apache.camel.Body;
import org.apache.camel.Properties;
import org.apache.camel.builder.ValueBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This route overrides various methods of {@link DefaultRoute} to facilitate
 * the processing of aggregate results.  It also makes use of a looping routing 
 * slip as part of Camel's Dynamic Router EIP, in order to provide the "iterating"
 * behavior.
 *
 * @author Mike D'Arcy 
 */
public class IterationRoute extends DefaultRoute 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(IterationRoute.class);
    
    @Override
    protected String getPostAggregationProcessorRef() {
        return "IterationAggregateProcessor";
    }    
    
    @Override
    public String getJAXBContext() {
        return "edu.isi.misd.scanner.network.types.example";
    }

    @Override
    public String getJSONUnmarshallType() {
        return "edu.isi.misd.scanner.network.types.example.ExampleIterationMessage";
    }    
    
    @Override    
    public ValueBuilder getDynamicRouter() {
        return method(getClass(),"loopingRoutingSlip");
    }   
}
