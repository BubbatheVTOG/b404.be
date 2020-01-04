package main.java.b404.servicelayer;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import main.java.b404.businesslayer.PersonBusiness;
import main.java.b404.utility.*;
import main.java.b404.utility.BadRequestException;
import main.java.b404.utility.InternalServerErrorException;

/**
 * Service layer entity responsible only for fielding login attempts
 */
@Path("login")
public class Login {
    private PersonBusiness personBusiness = new PersonBusiness();

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response submit(@FormParam("user") String user, @FormParam("password") String password) {
        try {
            String responseMessage = personBusiness.login(user, password);

            return Response.ok("{\"success\":\"" + responseMessage + "\"}").build();
        }
        catch(BadRequestException bre){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + bre.getMessage() + "\"}").build();
        }
        catch(InternalServerErrorException isee){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + isee.getMessage() + "\"}").build();
        }

    }
}
