package edu.isi.misd.scanner.network.base.utils;

import edu.isi.misd.scanner.network.types.base.ErrorDetails;
import java.nio.charset.Charset;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.http4.HttpOperationFailedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 */
public class ErrorUtils 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(ErrorUtils.class);  
    /**
     *
     * @param exchange
     * @param e
     * @return
     */
    public static ErrorDetails formatErrorResponse(Exchange exchange, Throwable e) 
    {
        ErrorDetails error = new ErrorDetails();
        error.setErrorSource(
            exchange.getProperty(Exchange.FAILURE_ENDPOINT, String.class));
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
        return error;    
    }  
    
    /**
     *
     * @param exchange
     * @param ex
     * @param httpResponseCode
     */
    public static void setHttpError(Exchange exchange,
                                    Throwable ex, 
                                    int httpResponseCode)
    {
        setHttpError(exchange,ex,httpResponseCode,null,true);
    }
    
    /**
     *
     * @param exchange
     * @param ex
     * @param httpResponseCode
     */
    public static void setHttpError(Exchange exchange,
                                    Throwable ex, 
                                    int httpResponseCode,
                                    String errorDesc)
    {
        setHttpError(exchange,ex,httpResponseCode,errorDesc,true);
    }
    
    /**
     *
     * @param exchange
     * @param ex
     * @param httpResponseCode
     * @param setException
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
     *
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
