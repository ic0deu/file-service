package i.c0d.eu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by antonio on 23/05/2016.
 */
@Configuration
public class StaticResourceConfiguration extends WebMvcConfigurerAdapter {

    @Value("${static_files}")
    private String staticFiles;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if ( ! staticFiles.isEmpty() ) {
            registry.addResourceHandler("/**").addResourceLocations("file:" + staticFiles);
        }
    }
}