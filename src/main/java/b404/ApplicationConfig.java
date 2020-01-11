package b404;

import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
public class ApplicationConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return getRestResourceClasses();
    }

    private Set<Class<?>> getRestResourceClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(b404.Ping.class);
        resources.add(b404.servicelayer.LoginService.class);
        resources.add(b404.servicelayer.PersonService.class);

        resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        return resources;
    }
}