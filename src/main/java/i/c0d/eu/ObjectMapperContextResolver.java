package i.c0d.eu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Created by antonio on 18/05/2016.
 */

@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper;

    public ObjectMapperContextResolver() {
        mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}

