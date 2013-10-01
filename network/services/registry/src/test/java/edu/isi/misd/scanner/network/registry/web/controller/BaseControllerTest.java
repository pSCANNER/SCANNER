package edu.isi.misd.scanner.network.registry.web.controller;

import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 *
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
