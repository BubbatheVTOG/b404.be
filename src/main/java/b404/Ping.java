package b404;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * A simple endpoint used for up status.
 */
@Path("ping")
public class Ping {

    /**
     * Returns "pong" when the user "pings" us.
     * @return 200 - "success:pong"
     */
    @GET
    @Produces("application/json")
    public Response ping() {
        return Response.ok().entity("{\"success\":\"pong\"}").build();
    }
}
