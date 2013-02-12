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
 *
 */
public class BaseRecipientList 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(BaseRecipientList.class); 
    
    private static String options = "bridgeEndpoint=true";
    
    /**
     *
     * @param targetURLS
     * @param body
     * @return
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
                results.add(target);                
            } catch (URISyntaxException e) {
                log.warn("Invalid URI syntax: " + target,e);
                continue;
            }
        }
        log.debug("Recipient list created: " + results);
        return results;
    }    
}
