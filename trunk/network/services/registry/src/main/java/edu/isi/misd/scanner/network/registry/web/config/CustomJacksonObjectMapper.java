package edu.isi.misd.scanner.network.registry.web.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import java.text.DateFormat;

public class CustomJacksonObjectMapper extends ObjectMapper 
{
    // we can use this class to set settings for all Jackson ObjectMapper handling
    public CustomJacksonObjectMapper() 
    {
        super();
                
        // do not serialize null valued properties or collections
        this.setSerializationInclusion(Include.NON_NULL);    
     
        // Custom DateTime format string for serializer
        this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.setDateFormat(
            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG));
        
        // enable Hibernate4 module to avoid serializing lazy-loaded objects
        Hibernate4Module hibernate4Module = new Hibernate4Module();
        hibernate4Module.configure(
            Hibernate4Module.Feature.FORCE_LAZY_LOADING, false);
        registerModule(hibernate4Module);        
        
        // use JAXB annotations (supposed to be able to use in place of @Json)
        // to support both xml and json at the same time only using JAXB annos
//        AnnotationIntrospector introspector = 
//            new JaxbAnnotationIntrospector(getTypeFactory());
//        // make deserializer use JAXB annotations 
//        this.getDeserializationConfig().with(introspector);
//        // make serializer use JAXB annotations 
//        this.getSerializationConfig().with(introspector);
    
        // can use this to wrap serialized objects with a root name value
        // automatically - this has a trade-off, since collections wind up
        // getting named with the collection class name instead of the annotated 
        // one - disabling until we can figure out a happy medium
//        this.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
//        this.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
    }
}
