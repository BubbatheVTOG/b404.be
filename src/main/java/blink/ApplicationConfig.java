package blink;

import blink.datalayer.DBInit;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
public class ApplicationConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        this.dBCheck();
        return this.getRestResourceClasses();
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

    /**
     * This will check for the database connection.
     * If we cannot connect we will wait here until we can.
     *  - If we can connect we will inspect the database for our our database.
     *    - If we can't fine it, we must exit.
     *    - If we can find we will inspect it.
     *      - If it is empty we will fill it with the required tables.
     *      - If it is not empty we will leave it alone.
     */
    private void dBCheck() {
        try {
            DBInit db = new DBInit();

            while (!db.tryDBConnect()) {
                System.out.println("DB connection could not be initialized. Trying again in 5 seconds...");
                Thread.sleep(TimeUnit.SECONDS.toMillis(5));
            }

            db.initializeDB();
        } catch (Exception E) {
        // If we are here we're completely hosed.
            E.printStackTrace();
            System.exit(1);
        }
    }
}