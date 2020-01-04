package main.java.b404.service;

import main.java.b404.business.PersonBusiness;
import main.java.b404.utility.*;

/**
 * Service layer entity responsible only for fielding login attempts
 */
@Path("login")
public class Login {
    private PersonBusiness userBusiness = new PersonBusiness();

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response submit(@FormParam("user") String user, @FormParam("password") String password) {
        try {
            String[] result = userBusiness.login(user, password);

            return Response.ok("{\"success\":\"You have logged in!\"}").build();
        }
        catch(BadRequestException bre){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + bre.getMessage() + "\"}").build();
        }

    }
}
