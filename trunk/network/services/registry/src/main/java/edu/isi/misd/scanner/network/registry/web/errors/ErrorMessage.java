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

/**
 * 
 * @author Mike D'Arcy
 */
public class ErrorMessage
{
    private final int status;
    private final String message;
    private final String detail;

    public ErrorMessage(int status, String message) 
    {
        this(status, message, null);
    }
    
    public ErrorMessage(int status, String message, String detail) 
    {
        this.status = status;
        this.message = message;
        this.detail = detail;
    }

    public int getErrorStatusCode() {
        return status;
    }

    public String getErrorMessage() {
        return message;
    }
    
    public String getErrorDetail() {
        return detail;
    }    
}
