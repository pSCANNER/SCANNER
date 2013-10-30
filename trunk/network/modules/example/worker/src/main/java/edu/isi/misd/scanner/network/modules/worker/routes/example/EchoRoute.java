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
package edu.isi.misd.scanner.network.modules.worker.routes.example; 

import edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  This route overrides 
 *  {@link edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute#getCacheReadProcessorRef()}
 *  and {@link edu.isi.misd.scanner.network.base.worker.routes.DefaultRoute#getCacheWriteProcessorRef()}
 *  to use the {@link edu.isi.misd.scanner.network.base.processors.BaseNoOpProcessor}
 *  so that echo service responses are not written to nor read from the cache.
 *
 *  @author Mike D'Arcy 
 */
public class EchoRoute extends DefaultRoute 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(EchoRoute.class);    
    /**
     *
     * @return The name of the processor to use.  It must be found at runtime in the Camel registry.
     */
    @Override
    protected String getCacheReadProcessorRef() {
        return "BaseNoOpProcessor";
    }

    /**
     *
     * @return The name of the processor to use.  It must be found at runtime in the Camel registry.
     */
    @Override
    protected String getCacheWriteProcessorRef() {
        return "BaseNoOpProcessor";
    }     
}
