package edu.isi.misd.scanner.network.base.utils;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.types.base.SiteInfo;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.TypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of utility functions for working with Camel message exchanges
 * and the headers contained within those exchanges that are specific to the 
 * operation of the SCANNER Network.
 */
public class MessageUtils 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(MessageUtils.class);
    
    /**
     *
     * @param exchange The current message exchange.
     * @return The ID header.
     */
    public static String getID(Exchange exchange)
    {
        return (String)exchange.getIn().getHeader(BaseConstants.ID);
    }
    
    /**
     *
     * @param exchange The current message exchange.
     * @param id
     */
    public static void setID(Exchange exchange, String id) 
    {
        exchange.getIn().setHeader(BaseConstants.ID, id);
    }
    
    /**
     *
     * @param exchange The current message exchange.
     * @return The timestamp header.
     */
    public static String getTimestamp(Exchange exchange) 
    {
       return (String)exchange.getIn().getHeader(BaseConstants.TIMESTAMP);
    }

    /**
     * Sets the timestamp header using the current time.
     * @see MessageUtils#setTimestamp(org.apache.camel.Exchange, java.util.Date) 
     * 
     * @param exchange The current message exchange.
     */
    public static void setTimestamp(Exchange exchange) 
    {
        setTimestamp(exchange, Calendar.getInstance().getTime());
    }
    
    /**
     * Sets the timestamp header using ISO 8601 formatting.
     * For example: {@code 2013-05-13T14:45:00.799-0700}
     * 
     * @param exchange The current message exchange.
     * @param date
     */
    public static void setTimestamp(Exchange exchange, Date date) 
    {
        SimpleDateFormat sdf = 
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); // ISO 8601
        exchange.getIn().setHeader(BaseConstants.TIMESTAMP, sdf.format(date));
    }
    
    /**
     * Gets the source URL header from an exchange.
     * @param exchange The current message exchange.
     * @return The value of the {@link edu.isi.misd.scanner.network.base.BaseConstants#SOURCE} header.
     */
    public static String getSource(Exchange exchange)
    {
        return (String)exchange.getIn().getHeader(BaseConstants.SOURCE);
    }
    
    /**
     * Sets the source URL header on the exchange.
     * @param exchange The current message exchange.
     */
    public static void setSource(Exchange exchange)
    {
        setSource(exchange, false);
    }
    
    /**
     * Sets the source URL header on the exchange, optionally appending the 
     * transaction ID to the URL path.
     * 
     * @param exchange The current message exchange. 
     * @param appendID
     */
    public static void setSource(Exchange exchange, boolean appendID) 
    {
        String uri = exchange.getFromEndpoint().getEndpointUri();
        if (!uri.endsWith("/")) {
            uri += "/";
        }
        String sourceURL =
            uri.substring(0, uri.lastIndexOf("/")+1) + 
            ((appendID) ? "id/" + getID(exchange) : "");        
        exchange.getIn().setHeader(BaseConstants.SOURCE, sourceURL);
    } 

    /**
     * Gets the list of targets as specified by the 
     * {@link edu.isi.misd.scanner.network.base.BaseConstants#TARGETS} header.
     * 
     * @param exchange The current message exchange.
     * @return A comma delimited string of target URLS that is the value of the 
     * header {@link edu.isi.misd.scanner.network.base.BaseConstants#TARGETS} or NULL
     */
    public static String getTargets(Exchange exchange)
    {
        return (String)exchange.getIn().getHeader(BaseConstants.TARGETS);
    }
    
    /**
     * Sets the {@link edu.isi.misd.scanner.network.base.BaseConstants#TARGETS} header.
     * @param exchange The current message exchange.
     * @param targetURLS A comma-delimited list of target URLs
     */
    public static void setTargets(Exchange exchange, String targetURLS) 
    {
        exchange.getIn().setHeader(BaseConstants.TARGETS, targetURLS);
    }  

    /**
     * Uses the Camel type converter system to serialize and deserialize from 
     * one object type to another.
     * 
     * @param toType
     * @param fromType
     * @param exchange The current message exchange.
     * @return The converted object.
     * @throws Exception
     */
    public static Object convertTo(Class<?> toType,
                                   Object fromType,                                 
                                   Exchange exchange)
        throws Exception
    {
        // avoid the overhead of type conversion if already same type
        if (toType.isInstance(fromType)) {
            return toType.cast(fromType);
        }

        TypeConverter converter = exchange.getContext().getTypeConverter();
        return converter.mandatoryConvertTo(toType, exchange, fromType);
    }    
    
    /**
     * Parses only the transaction ID out of a full URL.
     * @param path The full URL path.
     * @return The parsed ID.
     */
    public static String parseIdFromUrlPath(String path) 
    {
        if (path.endsWith("/")) {
            path = path.substring(0, path.lastIndexOf('/'));
        }
        
        int idIndex = path.lastIndexOf("/" + BaseConstants.ID + "/");        
        return 
            ((idIndex > 0) ? 
            path.substring(idIndex + BaseConstants.ID.length()+2) : "");
    }
   
    /**
     * @see MessageUtils#parseIdFromUrlPath(String path) 
     * @param message The inbound Camel message.
     * @return The parsed ID.
     * @throws Exception 
     */
    public static String parseIdFromMessageURL(Message message) throws Exception
    {
        return parseIdFromUrlPath(getPathFromMessageURL(message));
    }
    
    /**
     * Gets the host name from the URL of the request message.
     * @param message The inbound Camel message.
     * @return The host name.
     * @throws Exception
     */
    public static String getHostFromMessageURL(Message message) 
        throws Exception
    {      
        HttpServletRequest req = message.getBody(HttpServletRequest.class);  
        return req.getLocalName();
    }
    
    /**
     * Gets the port from the URL of the request message.
     * @param message The inbound Camel message.
     * @return The port number.
     * @throws Exception
     */
    public static int getPortFromMessageURL(Message message) 
        throws Exception
    {
        HttpServletRequest req = message.getBody(HttpServletRequest.class);  
        return req.getLocalPort();        
    }
    
    /**
     * Gets the path component from the URL of the request message.
     * @param message The inbound Camel message.
     * @return The path component of the URL.
     * @throws Exception
     */
    public static String getPathFromMessageURL(Message message) 
        throws Exception
    {
        HttpServletRequest req = message.getBody(HttpServletRequest.class);  
        return req.getPathInfo();         
    }
    
    /**
     * Populates a {@link edu.isi.misd.scanner.network.types.base.SiteInfo}
     * object with values resolved from the properties of the current 
     * {@link org.apache.camel.CamelContext}.
     * 
     * 
     * @param exchange The current exchange.
     * @return The populated SiteInfo.
     * @throws Exception 
     */
    public static SiteInfo getSiteInfo(Exchange exchange) 
        throws Exception
    {
        CamelContext context = exchange.getContext();
        
        String siteID =
            context.resolvePropertyPlaceholders(
                BaseConstants.SITE_ID_PROPERTY);
        String siteName = 
            context.resolvePropertyPlaceholders(
                BaseConstants.SITE_NAME_PROPERTY); 
        String siteDesc = 
            context.resolvePropertyPlaceholders(
                BaseConstants.SITE_DESC_PROPERTY);
        
        SiteInfo siteInfo = new SiteInfo();
        siteInfo.setSiteID(siteID);
        siteInfo.setSiteName(siteName);
        siteInfo.setSiteDescription(siteDesc);
        
        return siteInfo;
    }
}
