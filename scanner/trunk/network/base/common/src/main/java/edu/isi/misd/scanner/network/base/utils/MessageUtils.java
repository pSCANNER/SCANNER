package edu.isi.misd.scanner.network.base.utils;

import edu.isi.misd.scanner.network.base.BaseConstants;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.TypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class MessageUtils 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(MessageUtils.class);
    
    /**
     *
     * @param exchange The current message exchange.
     * @return
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
     * @return
     */
    public static String getTimestamp(Exchange exchange) 
    {
       return (String)exchange.getIn().getHeader(BaseConstants.TIMESTAMP);
    }

    /**
     *
     * @param exchange The current message exchange.
     */
    public static void setTimestamp(Exchange exchange) 
    {
        setTimestamp(exchange, Calendar.getInstance().getTime());
    }
    
    /**
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
     *
     * @param exchange The current message exchange.
     * @return
     */
    public static String getSource(Exchange exchange)
    {
        return (String)exchange.getIn().getHeader(BaseConstants.SOURCE);
    }
    
    /**
     *
     * @param exchange The current message exchange.
     */
    public static void setSource(Exchange exchange)
    {
        setSource(exchange, false);
    }
    
    /**
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
     *
     * @param exchange The current message exchange.
     * @return A comma delimited string of target URLS that is the value of the 
     * header {@link edu.isi.misd.scanner.network.base.BaseConstants#TARGETS} or NULL/
     */
    public static String getTargets(Exchange exchange)
    {
        return (String)exchange.getIn().getHeader(BaseConstants.TARGETS);
    }
    
    /**
     *
     * @param exchange The current message exchange.
     * @param targetURLS
     */
    public static void setTargets(Exchange exchange, String targetURLS) 
    {
        exchange.getIn().setHeader(BaseConstants.TARGETS, targetURLS);
    }  

    /**
     *
     * @param toType
     * @param fromType
     * @param exchange The current message exchange.
     * @return
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
     *
     * @param path
     * @return
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
     *
     * @param message
     * @return
     * @throws Exception
     */
    public static String getHostFromMessageURL(Message message) 
        throws Exception
    {
            URL url = 
                new URL(
                    message.getHeader(Exchange.HTTP_URL).toString());  
            return url.getHost();
    }
    
    /**
     *
     * @param message
     * @return
     * @throws Exception
     */
    public static int getPortFromMessageURL(Message message) 
        throws Exception
    {
            URL url = 
                new URL(
                    message.getHeader(Exchange.HTTP_URL).toString());  
            return url.getPort();
    }
    
    /**
     *
     * @param message
     * @return
     * @throws Exception
     */
    public static String getPathFromMessageURL(Message message) 
        throws Exception
    {
            URL url = 
                new URL(
                    message.getHeader(Exchange.HTTP_URL).toString());  
            return url.getPath();
    }
    
}
