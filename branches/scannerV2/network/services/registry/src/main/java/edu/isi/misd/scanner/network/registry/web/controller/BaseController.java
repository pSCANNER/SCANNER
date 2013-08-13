package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.web.errors.BadRequestException;
import edu.isi.misd.scanner.network.registry.web.errors.ErrorMessage;
import edu.isi.misd.scanner.network.registry.web.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 */
public class BaseController 
{
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorMessage handleException(Exception e) 
    {
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),                    
                                e.getLocalizedMessage());
    } 
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ErrorMessage handleResourceNotFoundException(ResourceNotFoundException e) 
    {
        return new ErrorMessage(HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),                    
                                e.getLocalizedMessage());
    }   
    
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ErrorMessage handleBadRequestException(BadRequestException e) 
    {
        return new ErrorMessage(HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),                    
                                e.getLocalizedMessage());
    }    
}
