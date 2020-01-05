package b404.servicelayer;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import b404.businesslayer.PersonBusiness;
import b404.utility.BadRequestException;
import b404.utility.InternalServerErrorException;

/**
 * Service layer entity responsible only for fielding login attempts
 */
@Path("login")
public class LoginService {
    private PersonBusiness personBusiness = new PersonBusiness();

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response login(@FormParam("user") String user, @FormParam("password") String password) {
        try {
            //Send parameters to business layer and store response
            String responseMessage = personBusiness.login(user, password);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return Response.ok("{\"success\":\"" + responseMessage + "\"}").build();
        }
        //Catch a BadRequestException and return Bad Request response with message from error
        catch(BadRequestException bre){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + bre.getMessage() + "\"}").build();
        }
        //Catch an InternalServerErrorException and return Internal Server Error response with message from error
        catch(InternalServerErrorException isee){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + isee.getMessage() + "\"}").build();
        }

    }
}