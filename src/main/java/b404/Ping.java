package b404;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * A simple endpoint used for up status.
 */
@Path("ping")
@Api
@Tag(name = "Ping")
public class Ping {

    /**
     * Returns "pong" when the user "pings" us.
     * @return 200 - "success:pong"
     */
    @GET
    @Produces("application/json")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json"
            ),
            description = "A pong."
    )
    public Response ping() {
        return Response.ok().entity("{\"success\":\"pong\"}").build();
    }
}
