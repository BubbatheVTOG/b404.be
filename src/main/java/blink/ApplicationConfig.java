package blink;

import blink.datalayer.DBInit;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
public class ApplicationConfig extends Application {

    private final Logger logger = Logger.getLogger(this.getClass().getName());


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
        resources.add(blink.servicelayer.MilestoneService.class);
        resources.add(blink.servicelayer.WorkflowService.class);
        resources.add(blink.servicelayer.VerbService.class);

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

            for (int attempts = 1; attempts <= DBInit.MAX_ATTEMPTS; attempts++) {
                if (db.canConnect()) {
                    logger.info("DB connected.");
                    db.initializeDB();
                    logger.info("DB completed initialization.");
                    break;
                } else {
                    String msg = "DB connection could not be initialized. Attempt "
                            + attempts +"/" + DBInit.MAX_ATTEMPTS
                            + ". Trying again in 5 seconds...";
                    logger.info(msg);
                    Thread.sleep(TimeUnit.SECONDS.toMillis(5));
                }
            }

            if (!db.canConnect()) {
                throw new SQLException("Could not connect to database in allotted attempts.");
            }
        } catch (Exception E) {
        // If we are here we're completely hosed.
            StringWriter sw = new StringWriter();
            E.printStackTrace(new PrintWriter(sw));
            logger.log(Level.SEVERE, sw.toString());
            System.exit(1);
        }
    }
}