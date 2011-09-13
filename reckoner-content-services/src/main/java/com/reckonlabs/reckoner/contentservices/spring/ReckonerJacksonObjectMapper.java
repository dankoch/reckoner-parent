package com.reckonlabs.reckoner.contentservices.spring;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationProblemHandler;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reckonlabs.reckoner.contentservices.client.GoogleAuthClient;

public class ReckonerJacksonObjectMapper extends ObjectMapper {
	// Configures the JacksonMapper as used for the Reckoner Services.
	
	private static final Logger log = LoggerFactory
			.getLogger(ReckonerJacksonObjectMapper.class);
	
	public ReckonerJacksonObjectMapper() {
	  // We want to use the JAXB annotations where possible.  This adds JAXB interpretation
	  // to both marshalling and unmarshalling.
	  final AnnotationIntrospector introspector
      = new JaxbAnnotationIntrospector();
  
	  super.getDeserializationConfig()
       .setAnnotationIntrospector(introspector);
  
	  super.getSerializationConfig()
       .setAnnotationIntrospector(introspector);
	  
	  // When unmarshalling, if we get any properties we don't recognize, just go with it.
	  super.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, 
			  false);
	  
	  // When marshalling, we want to add the parent element.
	  // Actually, we DON'T for the time being.
	  // super.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
    }

}
