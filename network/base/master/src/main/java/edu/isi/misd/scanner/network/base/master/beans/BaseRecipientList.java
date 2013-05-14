package edu.isi.misd.scanner.network.base.master.beans;

import edu.isi.misd.scanner.network.base.BaseConstants;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.camel.Header;
import org.apache.camel.util.ObjectHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class prepares a {@link org.apache.camel.RecipientList} from a
 * comma-delimited list of URLs as specified in the 
 * {@link edu.isi.misd.scanner.network.base.BaseConstants#TARGETS} header.  
 * It decorates the base URLs with the appropriate Camel-specific HTTP Client 
 * URLs, and also does some basic validity checks of the URL syntax.
 */
public class BaseRecipientList 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseRecipientList.class); 
    
    private static String options = 
        "bridgeEndpoint=true&httpClient.soTimeout=20000";
    private static String sslOptions = 
        "sslContextParametersRef=sslContextParameters";
    
    /**
     *
     * @param targetURLS The list of targets from the {@link edu.isi.misd.scanner.network.base.BaseConstants#TARGETS} header.
     * @param body The message body (currently ignored)
     * @return An ArrayList of decorated URLs suitable for use with the {@link org.apache.camel.RecipientList} processor.
     */
    public ArrayList<String> list(
        @Header(BaseConstants.TARGETS) String targetURLS, String body) 
    {
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
