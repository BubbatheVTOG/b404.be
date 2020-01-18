package b404.servicelayer;

import b404.businesslayer.PersonBusiness;
import b404.utility.customexceptions.BadRequestException;
import b404.utility.customexceptions.InternalServerErrorException;
import b404.utility.customexceptions.NotFoundException;
import b404.utility.customexceptions.UnauthorizedException;
import b404.utility.objects.Person;
import b404.utility.security.JWTUtility;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Service Layer entity responsible for receiving requests having to do with person information
 */
@Path("person")
public class PersonService {
    private PersonBusiness personBusiness = new PersonBusiness();

    /**
     * Gets a Person object by userID
     * @param userID - username from POST request body
     * @return - HTTP Response: 200 OK for person found and returned
     *                         400 BAD REQUEST for invalid username or password syntax
     *                         401 UNAUTHORIZED for invalid JSON Web Token in header
     *                         500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/id/{id}")
    @GET
    @Operation(summary = "id", description = "Gets a user's information by userID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Person object which contains keys (userID, name, email, title, companyID, accessLevelID)"),
            @ApiResponse(code = 400, message = "{error: Invalid username/password syntax}"),
            @ApiResponse(code = 401, message = "{error: Invalid login credentials.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPersonByUserID(@RequestBody(description = "id", required = true) @PathParam("id") String userID,
                                      @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String JWT) {
        try {
            if(!JWTUtility.validateToken(JWT, userID)){
                throw new UnauthorizedException("Invalid JSON Web Token provided.");
            }

            //Send parameters to business layer and store response
            Person person = personBusiness.getPersonByUserID(userID);

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
        //Catch a NotFoundException and return Not Found response with message from error
        catch(NotFoundException nfe){
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"" + nfe.getMessage() + "\"}").build();
        }
        //Catch a BadRequestException and return Bad Request response with message from error
        catch(BadRequestException bre){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + bre.getMessage() + "\"}").build();
        }
        //Catch an InternalServerErrorException and return Internal Server Error response with standard message
        catch(InternalServerErrorException isee){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Sorry, could not process your request at this time.\"}").build();
        }
        //Catch All to ensure no unexpected internal server errors are being returned to client
        catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + "Sorry, an unexpected issue has occurred." + "\"}").build();
        }
    }
}