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

import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 *
 * @author Mike D'Arcy 
 */
public abstract class BaseControllerTest 
{
    MappingJackson2HttpMessageConverter converter = 
        new MappingJackson2HttpMessageConverter();
    
    @Test
    public abstract void testJacksonMapping() throws Exception;

    protected void assertCanBeMapped(Class<?> classToTest)
    {
        String deser =
            String.format("%s is not deserializable by Jackson, check @Json annotations",
            classToTest.getName());
        assertTrue(deser, converter.canRead(classToTest, MediaType.APPLICATION_JSON));
        
        String ser =
            String.format("%s is not serializable by Jackson, check @Json annotations",
            classToTest.getName());
        assertTrue(ser, converter.canWrite(classToTest, MediaType.APPLICATION_JSON));        
    }    
}
