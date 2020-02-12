package blink;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.*;

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

        StringBuilder sb = new StringBuilder();
        //InputStream inputStream = getClass().getResourceAsStream("/WEB-INF/classes/LameFile.txt");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("../LameFile.txt");
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String buf;
                while ((buf = reader.readLine()) != null) {
                    sb.append(buf);
                }
            } catch (IOException fnfe) {
            }
        } else {
            sb.append("fuck");
        }


        return Response.ok().entity("{\"success\":"+sb.toString()+"}").build();
    }
}
