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
package edu.isi.misd.scanner.network.base.master.beans; 

import edu.isi.misd.scanner.network.base.BaseConstants;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.apache.camel.Headers;
import org.apache.camel.util.ObjectHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class prepares a {@link org.apache.camel.RecipientList} from a
 * comma-delimited list of URLs as specified in the 
 * {@link edu.isi.misd.scanner.network.base.BaseConstants#TARGETS} header.  
 * It decorates the base URLs with the appropriate Camel-specific HTTP Client 
 * URLs, and also does some basic validity checks of the URL syntax.
 *
 * @author Mike D'Arcy 
 */
public class BaseRecipientList 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseRecipientList.class); 
    
    private static String options = 
        "bridgeEndpoint=true&httpClient.soTimeout=20000"; 
    private static String sslOptions = 
        "sslContextParametersRef=sslContextParameters";    
    private static String ASYNC_ENABLED = 
        BaseConstants.ASYNC + "=true";      
    private static String RELEASE_REQ_ENABLED = 
        BaseConstants.RESULTS_RELEASE_AUTH_REQUIRED + "=true";     
    /**
     *
     * @param headers The Camel message headers
     * @return An ArrayList of decorated URLs suitable for use with the {@link org.apache.camel.RecipientList} processor.
     */
    public ArrayList<String> list(@Headers Map headers) 
    {
        String targetURLS = (String)headers.get(BaseConstants.TARGETS);
        String async = (String)headers.get(BaseConstants.ASYNC);
        Iterator iter = ObjectHelper.createIterator(targetURLS);
        ArrayList<String> results = new ArrayList<String>();
        while (iter.hasNext()) 
        {
            String target = (String)iter.next();
            try {
                target = target.trim();
                URI uri = new URI(target);
                String queryParams = uri.getQuery();
                target += ((queryParams == null) ? "?" : "&") + options;
                if (target.contains(RELEASE_REQ_ENABLED)) {
                    if ((async == null) || 
                        ((async != null) && 
                        (!("true".equalsIgnoreCase(async))))) {
                        // TODO: make it work mixed sync/async, 
                        // but for now just set all to run async                    
                        target+= "&" + ASYNC_ENABLED; 
                        //headers.put(BaseConstants.ASYNC, "true");
                    }
                }
                String protocol = uri.getScheme();
                if ("http".equalsIgnoreCase(protocol)) {
                    target = target.replaceFirst("http://", "http4://");
                } else if ("https".equalsIgnoreCase(protocol)) {
                    target = target.replaceFirst("https://", "https4://"); 
                    if (target.indexOf("sslContextParametersRef=")==-1) {
                        target += "&" + sslOptions;
                    }
                }
                results.add(target);                
            } catch (URISyntaxException e) {
                log.warn("Invalid URI syntax: " + target,e);
                continue;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Recipient list created: " + results);
        }
        return results;
    }    
}
