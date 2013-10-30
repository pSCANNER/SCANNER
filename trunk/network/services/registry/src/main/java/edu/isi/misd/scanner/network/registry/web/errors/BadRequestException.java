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
package edu.isi.misd.scanner.network.registry.web.errors; 

import java.util.Collection;

/**
 * 
 * @author Mike D'Arcy
 */
public final class BadRequestException extends RuntimeException 
{

    public BadRequestException() {
    }
    
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }
    
    public BadRequestException(Collection badParams) {
        super("Invalid parameter(s) specified: " + badParams);
    }
    
    public BadRequestException(String entityNameRequested,
                               Integer entityIdNotFound) {
        super(String.format(
            "A valid [%s] could not be found matching the requested ID: %s", 
            entityNameRequested, entityIdNotFound));
    }      
}
