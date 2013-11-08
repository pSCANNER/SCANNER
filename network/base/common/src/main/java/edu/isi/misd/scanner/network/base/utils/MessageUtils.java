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
package edu.isi.misd.scanner.network.base.utils;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.types.base.ServiceRequestStateType;
import edu.isi.misd.scanner.network.types.base.ServiceResponseMetadata;
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
 *
 * @author Mike D'Arcy 
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
    public static <T extends Object> T convertTo(Class<T> toType,
                                                 Object fromType,                                 
                                                 Exchange exchange)
    {
        // avoid the overhead of type conversion if already same type
        if (toType.isInstance(fromType)) {
            return toType.cast(fromType);
        }

        TypeConverter converter = exchange.getContext().getTypeConverter();
        return converter.tryConvertTo(toType, exchange, fromType);
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
     * Gets the request URL from the HTTPServletRequest.
     * @param message The inbound Camel message.
     * @return The request URL.
     * @throws Exception
     */
    public static String getRequestURL(Message message) 
        throws Exception
    {
        HttpServletRequest req = message.getBody(HttpServletRequest.class);  
        return req.getRequestURL().toString();         
    }
    
    public static String getResultURL(Exchange exchange) 
        throws Exception
    {
        String resultURL = 
            (String)exchange.getProperty(BaseConstants.REQUEST_URL);
        String id = (String)exchange.getIn().getHeader(BaseConstants.ID);
        if ((resultURL == null) || (id == null)) {
            throw new Exception(
                "Unable to create result URL from header values");            
        }
        resultURL += 
            "/id/" + (String)exchange.getIn().getHeader(BaseConstants.ID);
        return resultURL;
    }
    
    /**
     * Returns the site name of the node with a value resolved from the 
     * properties of the current {@link org.apache.camel.CamelContext}.
     * 
     * @param exchange The current exchange.
     * @return The site name, or null.
     */
    public static String getSiteName(Exchange exchange) 
    {
        CamelContext context = exchange.getContext();
        String siteName = null;
        try {
            siteName = context.resolvePropertyPlaceholders(
                BaseConstants.SITE_NAME_PROPERTY); 
        } catch (Exception e) {
            log.warn("Exception trying to resolve property " + 
                      BaseConstants.SITE_NAME_PROPERTY + " - " + e);
        }
        return siteName;
    }
    
    /**
     * Returns the external name of the node with a value resolved from the 
     * properties of the current {@link org.apache.camel.CamelContext}.
     * 
     * @param exchange The current exchange.
     * @return The site name, or null.
     */
    public static String getNodeName(Exchange exchange) 
    {
        CamelContext context = exchange.getContext();
        String siteName = null;
        try {
            siteName = context.resolvePropertyPlaceholders(
                BaseConstants.NODE_NAME_PROPERTY); 
        } catch (Exception e) {
            log.warn("Exception trying to resolve property " + 
                      BaseConstants.NODE_NAME_PROPERTY + " - " + e);
        }
        return siteName;
    }
    
    public static ServiceResponseMetadata createServiceResponseMetadata(
        Exchange exchange,
        ServiceRequestStateType state,
        String stateDetail)
    {
        ServiceResponseMetadata serviceResponseMetadata = 
            new ServiceResponseMetadata();
        serviceResponseMetadata.setRequestID(MessageUtils.getID(exchange));  
        serviceResponseMetadata.setRequestURL(
            exchange.getProperty(BaseConstants.REQUEST_URL,String.class));             
        serviceResponseMetadata.setRequestState(state);
        serviceResponseMetadata.setRequestStateDetail(stateDetail);  
        serviceResponseMetadata.setRequestSiteName(
            MessageUtils.getSiteName(exchange));        
        serviceResponseMetadata.setRequestNodeName(
            MessageUtils.getNodeName(exchange));
        return serviceResponseMetadata;
    }
}
