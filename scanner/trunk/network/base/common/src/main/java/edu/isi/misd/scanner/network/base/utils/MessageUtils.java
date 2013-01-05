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
    
    public static String getID(Exchange exchange)
    {
        return (String)exchange.getIn().getHeader(BaseConstants.ID);
    }
    
    public static void setID(Exchange exchange, String id) 
    {
        exchange.getIn().setHeader(BaseConstants.ID, id);
    }
    
    public static String getTimestamp(Exchange exchange) 
    {
       return (String)exchange.getIn().getHeader(BaseConstants.TIMESTAMP);
    }

    public static void setTimestamp(Exchange exchange) 
    {
        setTimestamp(exchange, Calendar.getInstance().getTime());
    }
    
    public static void setTimestamp(Exchange exchange, Date date) 
    {
        SimpleDateFormat sdf = 
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); // ISO 8601
        exchange.getIn().setHeader(BaseConstants.TIMESTAMP, sdf.format(date));
    }
    
    public static String getSource(Exchange exchange)
    {
        return (String)exchange.getIn().getHeader(BaseConstants.SOURCE);
    }
    
    public static void setSource(Exchange exchange)
    {
        setSource(exchange, false);
    }
    
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

    public static String getTargets(Exchange exchange)
    {
        return (String)exchange.getIn().getHeader(BaseConstants.TARGETS);
    }
    
    public static void setTargets(Exchange exchange, String targetURLS) 
    {
        exchange.getIn().setHeader(BaseConstants.TARGETS, targetURLS);
    }  

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
   
    public static String getHostFromMessageURL(Message message) 
        throws Exception
    {
            URL url = 
                new URL(
                    message.getHeader(Exchange.HTTP_URL).toString());  
            return url.getHost();
    }
    
    public static int getPortFromMessageURL(Message message) 
        throws Exception
    {
            URL url = 
                new URL(
                    message.getHeader(Exchange.HTTP_URL).toString());  
            return url.getPort();
    }
    
    public static String getPathFromMessageURL(Message message) 
        throws Exception
    {
            URL url = 
                new URL(
                    message.getHeader(Exchange.HTTP_URL).toString());  
            return url.getPath();
    }
    
}
