package blink;

import blink.datalayer.DBInit;
import blink.utility.exceptions.DBInitException;

import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
public class ApplicationConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        try {
            DBInit db = new DBInit();
            // If we throw here we're completely hosed.
            db.initializeDB();
        } catch (DBInitException dbie) {
            System.err.println(dbie.toString());
            //System.exit(1);
        }
        return getRestResourceClasses();
    }

    private Set<Class<?>> getRestResourceClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(Ping.class);
        resources.add(blink.servicelayer.LoginService.class);
        resources.add(blink.servicelayer.PersonService.class);
        resources.add(blink.servicelayer.CompanyService.class);

        resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        return resources;
    }
}