package edu.isi.misd.scanner.network.base.utils;

import edu.isi.misd.scanner.network.types.base.ErrorDetails;
import java.nio.charset.Charset;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.http4.HttpOperationFailedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions for creating and formatting error responses.
 */
public class ErrorUtils 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(ErrorUtils.class);  
    
    /**
     * Creates and formats a serializable error message of the type 
     * {@link edu.isi.misd.scanner.network.types.base.ErrorDetails} based on
     * information in the current {@link org.apache.camel.Exchange} and the 
     * thrown exception.
     * 
     * @param exchange The current exchange
     * @param e The caused exception
     * @return The formatted ErrorDetails
     */
    public static ErrorDetails formatErrorResponse(Exchange exchange, 
                                                   Throwable e) 
    {
        ErrorDetails error = new ErrorDetails();
        String errorSource = 
            exchange.getProperty(Exchange.FAILURE_ENDPOINT, String.class);
        if ((errorSource == null) || 
            ((errorSource != null) && (errorSource.isEmpty()))) {
            errorSource = 
                exchange.getProperty(Exchange.TO_ENDPOINT, String.class);            
        }
        error.setErrorSource(errorSource);
        error.setErrorType(e.getClass().getName());        
        if (e instanceof HttpOperationFailedException) { 
            String desc = " " + 
                ((HttpOperationFailedException)e).getStatusText() + ": " +
                ((HttpOperationFailedException)e).getResponseBody();
            error.setErrorDescription(desc);
            error.setErrorCode(
                Integer.toString(
                    ((HttpOperationFailedException)e).getStatusCode()));
        } else {         
            error.setErrorDescription(e.getLocalizedMessage());
        }
        try {
            error.setSiteInfo(MessageUtils.getSiteInfo(exchange));
        } catch (Exception ex) {
            log.warn("Exception while getting local SiteInfo: " + ex.toString());
        }        
        return error;    
    }  
    
    /**
     * Sets the HTTP error and propagates it to the caller.
     * @see edu.isi.misd.scanner.network.base.utils.ErrorUtils#setHttpError(org.apache.camel.Exchange, java.lang.Throwable, int, java.lang.String, boolean) 
     */
    public static void setHttpError(Exchange exchange,
                                    Throwable ex, 
                                    int httpResponseCode)
    {
        setHttpError(exchange,ex,httpResponseCode,null,true);
    }
    
    /**
     * Sets the HTTP error and propagates it to the caller, using the provided error description.
     * @see edu.isi.misd.scanner.network.base.utils.ErrorUtils#setHttpError(org.apache.camel.Exchange, java.lang.Throwable, int, java.lang.String, boolean) 
     */
    public static void setHttpError(Exchange exchange,
                                    Throwable ex, 
                                    int httpResponseCode,
                                    String errorDesc)
    {
        setHttpError(exchange,ex,httpResponseCode,errorDesc,true);
    }
    
    /**
     * Sets the current Camel exception as an HTTP error that will be propagated,
     * as an HTTP error message to the caller, or just logs the passed-in Exception.
     * 
     * @param exchange The current exchange
     * @param ex The exception to set
     * @param httpResponseCode The HTTP response code to set
     * @param errorDesc An optionally provided description, or the the exception message if not provided.
     * @param setException Whether to have Camel propagate and return the HTTP error to the client, or just log the error to the error log.
     */
    public static void setHttpError(Exchange exchange,
                                    Throwable ex, 
                                    int httpResponseCode,                                  
                                    String errorDesc,
                                    boolean setException)
    {                
        exchange.getIn().setHeader(
            Exchange.HTTP_RESPONSE_CODE, httpResponseCode);      
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE,
            "text/plain;charset="+Charset.defaultCharset());        
        String desc = 
            ((errorDesc != null) ? errorDesc : "") + " " + ex.toString();
        exchange.getIn().setBody(desc);             
        if (setException) {
            exchange.setException(ex);
        } else {
            log.error(desc,ex);
        }
    }
    
    /**
     *  A Camel processor that can be used to return errors in the processing 
     *  pipeline as 500 errors with the message body being the Exception detail 
     *  message.
     */
    public static class ErrorProcessor implements Processor
    {
        /**
         *
         * @param exchange
         * @throws Exception
         */
        @Override
        public void process(Exchange exchange) 
            throws Exception 
        {
            Throwable cause =
                exchange.getProperty(Exchange.EXCEPTION_CAUGHT,Throwable.class);
            ErrorUtils.setHttpError(exchange, cause, 500);
        }
    }
}