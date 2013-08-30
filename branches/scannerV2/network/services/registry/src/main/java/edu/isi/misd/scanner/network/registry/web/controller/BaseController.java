package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.web.errors.BadRequestException;
import edu.isi.misd.scanner.network.registry.web.errors.ConflictException;
import edu.isi.misd.scanner.network.registry.web.errors.ErrorMessage;
import edu.isi.misd.scanner.network.registry.web.errors.MethodNotAllowedException;
import edu.isi.misd.scanner.network.registry.web.errors.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 */
public class BaseController 
{
    private static final Log log = 
        LogFactory.getLog(BaseController.class.getName());
    
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleGenericException(Exception e) 
    {
        log.warn(
            "Caught unhandled exception (returning response HTTP 500): " + e);
        return new ErrorMessage(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),                    
            e.getLocalizedMessage());
    } 
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    @ResponseStatus(value=HttpStatus.NOT_FOUND)    
    public ErrorMessage handleResourceNotFoundException(Exception e) 
    {
        return new ErrorMessage(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),                    
            e.getLocalizedMessage());
    }   
    
    @ExceptionHandler({BadRequestException.class, 
                       HttpMessageNotReadableException.class, 
                       TypeMismatchException.class})
    @ResponseBody
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)      
    public ErrorMessage handleBadRequestException(Exception e) 
    {
        return new ErrorMessage(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),                    
            e.getLocalizedMessage());
    }      
    
    @ExceptionHandler({ConflictException.class,
                       DataIntegrityViolationException.class})
    @ResponseBody
    @ResponseStatus(value=HttpStatus.CONFLICT)      
    public ErrorMessage handleConflictException(Exception e) 
    {
        return new ErrorMessage(
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),                    
            e.getLocalizedMessage());
    } 
    
    @ExceptionHandler({MethodNotAllowedException.class,
                       UnsupportedOperationException.class})
    @ResponseBody
    @ResponseStatus(value=HttpStatus.METHOD_NOT_ALLOWED)      
    public ErrorMessage handleMethodNotAllowedException(Exception e) 
    {
        return new ErrorMessage(
            HttpStatus.METHOD_NOT_ALLOWED.value(),
            HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),                    
            e.getLocalizedMessage());
    }
    
    public static Integer validateIntegerParameter(String param, String value)
        throws BadRequestException
    {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            throw new BadRequestException(
                String.format("Invalid format for parameter [%s] %s", 
                    param, nfe.toString()));
        }        
    }
}
