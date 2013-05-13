
package edu.isi.misd.scanner.network.base.master.routes;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils.ErrorProcessor;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Map;
import org.apache.camel.Body;
import org.apache.camel.Properties;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ValueBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.dataformat.xmljson.XmlJsonDataFormat;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class DefaultRoute extends RouteBuilder 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(DefaultRoute.class);    

    protected static final String HTTP_METHOD_GET_CLAUSE = 
        "${in.headers.CamelHttpMethod} == 'GET'";
    
    protected static final String HTTP_METHOD_POST_CLAUSE = 
        "${in.headers.CamelHttpMethod} == 'POST'";
    
    protected static final String TARGET_PARAMS_NULL_CLAUSE =     
        "${in.headers." + BaseConstants.TARGETS + "} == null";

    protected static final String JSON_CONTENT_TYPE_CLAUSE = 
        "${in.headers.Content-Type} contains 'application/json'";
    
    protected static final String ROUTE_COMPLETE_CLAUSE = 
        "${property.status} contains 'complete'";    
    
    protected JaxbDataFormat jaxb = new JaxbDataFormat(); 
    protected JacksonDataFormat json = new JacksonDataFormat();        
    protected XmlJsonDataFormat xmlToJson = new XmlJsonDataFormat();    

    /**
     *
     * @return
     */
    protected String getRouteName() {
        return getClass().getName();
    }

    /**
     *
     * @return
     */
    protected String getAggregatorRouteName() {
        return getClass().getSimpleName() + "Aggregator";
    } 
    
    /**
     *
     * @return
     */
    protected String getGETRouteName() {
        return getClass().getSimpleName() + "GET";
    }     
    
    /**
     *
     * @return
     */
    protected String getPOSTRouteName() {
        return getClass().getSimpleName() + "POST";
    }   
    
    /**
     *
     * @return
     */
    protected String getRequestProcessorRef() {
        return "BaseRequestProcessor";
    }
    
    /**
     *
     * @return
     */
    protected String getCacheReadProcessorRef() {
        return "BaseCacheReadProcessor";
    }

    /**
     *
     * @return
     */
    protected String getCacheWriteProcessorRef() {
        return "BaseCacheWriteProcessor";
    } 
       
    /**
     *
     * @return
     */
    protected String getAggregationStrategyRef() {
        return "BaseResultsAggregator";
    }
   
    /**
     *
     * @return
     */
    protected String getPostAggregationProcessorRef() {
        return "BaseAggregateProcessor";
    }

    /**
     *
     * @return
     */
    public String getJAXBContext() {
        return "edu.isi.misd.scanner.network.types.base";
    }
    
    /**
     *
     * @return
     */
    public String getJSONUnmarshallType() {
        return "edu.isi.misd.scanner.network.types.base.SimpleMap";
    }
    
    /**
     *
     * @return
     */
    protected ValueBuilder getRecipientList() {
        return method("BaseRecipientList","list");
    }

    /**
     *
     * @return
     */
    protected ValueBuilder getDynamicRouter() {
        return method(getClass(),"defaultRoutingSlip");
    }
    
    /**
     *
     * @throws Exception
     */
    @Override
    public void configure() throws Exception 
    {    
        jaxb.setFragment(true);
        jaxb.setPrettyPrint(false);        
        jaxb.setIgnoreJAXBElement(false);
        jaxb.setContextPath(getJAXBContext());               
        json.setUnmarshalType(Class.forName(getJSONUnmarshallType()));

        xmlToJson.setForceTopLevelObject(false);
        xmlToJson.setTrimSpaces(true);
        xmlToJson.setSkipNamespaces(true);
        xmlToJson.setRemoveNamespacePrefixes(true);

        from("direct:" + getGETRouteName()).
            choice().
                when().simple(TARGET_PARAMS_NULL_CLAUSE).
                    processRef(getCacheReadProcessorRef()).
                    choice().
                        when().simple(JSON_CONTENT_TYPE_CLAUSE).
                            marshal(xmlToJson).
                        endChoice().
                    stop().
                endChoice().
            end();
        
        from("direct:" + getPOSTRouteName()).
            choice().
                when().simple(JSON_CONTENT_TYPE_CLAUSE).
                    doTry().
                        unmarshal(json).             
                    doCatch(Exception.class).
                        process(new ErrorProcessor()).
                        stop().
                    end().
                    marshal(jaxb).
                endChoice().
            end();
        
        from("direct:" + getRouteName()).
            convertBodyTo(String.class).
            processRef(getRequestProcessorRef()).            
            choice().
                when().simple(HTTP_METHOD_GET_CLAUSE).
                    to("direct:" + getGETRouteName()).
                when().simple(HTTP_METHOD_POST_CLAUSE).
                    to("direct:" + getPOSTRouteName()).
            end().
        dynamicRouter(getDynamicRouter());
        
        multicast();
    }

    /**
     *
     * TODO: make redeliveries and redeliveryDelay for onException clauses 
     *  externally configurable via master.properties
     * @throws Exception
     */
    protected void multicast() throws Exception
    {
        from("direct:" + getAggregatorRouteName()).
            // dont retry connects for now
            onException(HttpHostConnectException.class).
            maximumRedeliveries(0).
            redeliveryDelay(1000).            
            continued(true).
            end().            
            // do retry SocketException (by default once)
            onException(SocketException.class).
            maximumRedeliveries(1).            
            redeliveryDelay(1000).
            continued(true).
            end().
            // do retry SocketTimeoutExceptions (by default twice)
            onException(SocketTimeoutException.class).
            maximumRedeliveries(2).
            redeliveryDelay(1000).
            continued(true).
            end().            
            // don't retry anything else
            onException(Exception.class).
            maximumRedeliveries(0).
            continued(true).
            end().            
            // recipientList does the actual multicast
            recipientList(getRecipientList()).          
                parallelProcessing().
                aggregationStrategyRef(getAggregationStrategyRef()). 
            processRef(getPostAggregationProcessorRef()).
            marshal(jaxb).
            processRef(getCacheWriteProcessorRef()).            
            choice().
                when().simple(ROUTE_COMPLETE_CLAUSE). 
                    choice().
                        when().simple(JSON_CONTENT_TYPE_CLAUSE).
                            marshal(xmlToJson).
                        endChoice().
                end().
            end();
    }
        
    /**
     *
     * @param body
     * @param properties
     * @return
     */
    public String defaultRoutingSlip(@Body String body, 
                                     @Properties Map<String, Object> properties) 
    {
        String status = (String)properties.get("status");
        // no more so return null -- MUST return null to stop the route
        if ("complete".equalsIgnoreCase(status)) {
            return null;
        } else { 
            // default implementation runs the route once then stops
            properties.put("status", "complete");
            return "direct:" + getAggregatorRouteName();     
        }
    }   
    
    /**
     *
     * @param body
     * @param properties
     * @return
     */
    public String loopingRoutingSlip(@Body String body, 
                                     @Properties Map<String, Object> properties) 
    {
        String status = (String)properties.get("status");            
        if ("complete".equalsIgnoreCase(status)) {
            return null;
        } else { 
            return "direct:" + this.getAggregatorRouteName();            
        }
    }      
}
