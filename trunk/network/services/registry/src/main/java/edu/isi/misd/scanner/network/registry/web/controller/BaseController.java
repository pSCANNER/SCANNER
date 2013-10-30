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
package edu.isi.misd.scanner.network.registry.web.controller; 

import edu.isi.misd.scanner.network.registry.data.service.RegistryServiceConstants;
import edu.isi.misd.scanner.network.registry.web.errors.BadRequestException;
import edu.isi.misd.scanner.network.registry.web.errors.ConflictException;
import edu.isi.misd.scanner.network.registry.web.errors.ErrorMessage;
import edu.isi.misd.scanner.network.registry.web.errors.ForbiddenException;
import edu.isi.misd.scanner.network.registry.web.errors.MethodNotAllowedException;
import edu.isi.misd.scanner.network.registry.web.errors.ResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
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
 *
 * @author Mike D'Arcy 
 */
public class BaseController 
{
    private static final Log log = 
        LogFactory.getLog(BaseController.class.getName());
    
    public static final String ID_URL_PATH_VAR= "id"; 
    public static final String ID_URL_PATH = "/{" + ID_URL_PATH_VAR + "}";
    
    public static final String HEADER_LOGIN_NAME = "loginName";    
    public static final String HEADER_JSON_MEDIA_TYPE = 
        "application/json;charset=UTF-8";
    public static final String HEADER_TEXT_MEDIA_TYPE = 
        "text/plain;charset=UTF-8";
    
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
    
    @ExceptionHandler(ForbiddenException.class)
    @ResponseBody
    @ResponseStatus(value=HttpStatus.FORBIDDEN)    
    public ErrorMessage handleForbiddenException(Exception e) 
    {
        return new ErrorMessage(
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.FORBIDDEN.getReasonPhrase(),                    
            e.getLocalizedMessage());
    }
    
    @ExceptionHandler({BadRequestException.class,
                       IllegalArgumentException.class,
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
    
    public static String nullVariableMsg(String variableName) {
        return 
            String.format(
                RegistryServiceConstants.MSG_NULL_VARIABLE, variableName);
    }
    
    public static Map<String,String> validateParameterMap(
        Map<String,String> paramMap, String ... paramNames)
        throws BadRequestException
    {
        HashMap<String,String> validParams = new HashMap<String,String>();        
        if (!paramMap.isEmpty()) 
        {
            String paramValue;
            for (String paramName : paramNames) {
               paramValue = paramMap.remove(paramName);
               if (paramValue != null) {
                   validParams.put(paramName, paramValue);
               }
            }
           
            if (!paramMap.isEmpty()) {
                throw new BadRequestException(paramMap.keySet());
            }
        }    
        return validParams;        
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
