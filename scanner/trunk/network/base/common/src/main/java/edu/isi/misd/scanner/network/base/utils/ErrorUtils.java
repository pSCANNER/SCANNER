package edu.isi.misd.scanner.network.base.utils;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 *
 */
public class ErrorUtils 
{
    public static String formatErrorResponse(Exchange exchange, Throwable e) 
    {
        String response = 
            "<head>"
            + "<title>Error</title>"
            + "</head>"
            + "<body>"
            + "<h2>HTTP ERROR: "
            + exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE)
            + "</h2>"
            + "<p><b>Error while accessing:</b><br/><br/>"
            + "&nbsp;&nbsp;&nbsp;&nbsp;" 
            + exchange.getIn().getHeader(Exchange.HTTP_URL) 
            + "<br/><br/>"
            + "<b>Reason:</b>"
            + "<pre>    " 
            + e.toString() 
            + "</pre></p>"
            + "<hr />"
            + "</body>";
        return response;    
    }  
    
    public static void setHttpError(Exchange exchange,
                                    Throwable ex, 
                                    int httpResponseCode)
    {
        setHttpError(exchange,ex,httpResponseCode,true);
    }
    
    public static void setHttpError(Exchange exchange,
                                    Throwable ex, 
                                    int httpResponseCode,
                                    boolean setException)
    {                
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, httpResponseCode);
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"text/html");
        exchange.getIn().setBody(ErrorUtils.formatErrorResponse(exchange,ex));
        if (setException) {
            exchange.setException(ex);
        }
    }
    
    public static class ErrorProcessor implements Processor
    {
        @Override
        public void process(Exchange exchange) 
            throws Exception 
        {
            Throwable cause =
                exchange.getProperty(Exchange.EXCEPTION_CAUGHT,Throwable.class);
            ErrorUtils.setHttpError(exchange, cause, 500, false);
        }
    }
}
