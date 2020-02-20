package blink.servicelayer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import blink.businesslayer.PersonBusiness;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import blink.utility.objects.Person;
import blink.utility.security.JWTUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Service layer entity responsible only for fielding login attempts
 */
@Path("login")
@Api(value = "/login")
public class LoginService {
    private PersonBusiness personBusiness = new PersonBusiness();
    private Gson gson =  new GsonBuilder().serializeNulls().create();

    /**
     * Checks that a persons username and password match values stored in database
     * @param username - username from POST request body
     * @param password - password from POST request body
     * @return - HTTP Response: 200 OK for accepted login
     *                         400 BAD REQUEST for invalid username or password syntax
     *                         401 UNAUTHORIZED for non-existent username or incorrect password for user
     *                         500 INTERNAL SERVER ERROR for backend error
     */
    @POST
    @Operation(summary = "Login", description = "Authenticates the user by username and password")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Person object which contains keys (UUID, name, email, title, companyID, accessLevelID)"),
            @ApiResponse(code = 400, message = "{error: Invalid username/password syntax}"),
            @ApiResponse(code = 401, message = "{error: Invalid login credentials.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@RequestBody(description = "username", required = true) @FormParam("username") String username,
                          @RequestBody(description = "password", required = true) @FormParam("password") String password) {
        try {
            //Send parameters to business layer and store response
            Person person = personBusiness.login(username, password);

            String jwt = JWTUtility.generateToken(person.getUUID());

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(person), jwt);
        }
        //Catch an UnauthorizedException and return Unauthorized response with message from error
        catch(NotAuthorizedException ue){
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"" + ue.getMessage() + "\"}").build();
        }
        //Catch a BadRequestException and return Bad Request response with message from error
        catch(BadRequestException bre){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + bre.getMessage() + "\"}").build();
        }
        //Catch an InternalServerErrorException and return Internal Server Error response with message from error
        catch(InternalServerErrorException isee){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Sorry, could not process your request at this time.\"}").build();
        }
        //Catch All to ensure no unexpected internal server errors are being returned to client
        catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Sorry, an unexpected issue has occurred.\"}").build();
        }
    }
}
