package i.c0d.eu;

import i.c0d.eu.rest.FileResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

/**
 * Created by antonio on 18/05/2016.
 */
@Component
@ApplicationPath("/rest")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(FileResource.class);
        register(MultiPartFeature.class);
        register(ObjectMapperContextResolver.class);
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
    }

}