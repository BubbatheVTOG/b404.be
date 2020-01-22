package b404.servicelayer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import b404.businesslayer.PersonBusiness;
import b404.utility.customexceptions.BadRequestException;
import b404.utility.customexceptions.InternalServerErrorException;

import b404.utility.customexceptions.UnauthorizedException;
import b404.utility.objects.Person;
import b404.utility.security.JWTUtility;
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
            @ApiResponse(code = 200, message = "Person object which contains keys (userID, name, email, title, companyID, accessLevelID)"),
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

            String jwtToken = JWTUtility.generateToken(Integer.toString(person.getUserID()));

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return Response.ok(person.toSecureJSON())
                    .header("Authorization", jwtToken)
                    .build();
        }
        //Catch an UnauthorizedException and return Unauthorized response with message from error
        catch(UnauthorizedException ue){
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + "Sorry, an unexpected issue has occurred." + "\"}").build();
        }
    }
}
