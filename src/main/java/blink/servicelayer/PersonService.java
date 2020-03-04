package blink.servicelayer;

import blink.businesslayer.Authorization;
import blink.businesslayer.PersonBusiness;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import blink.utility.exceptions.ConflictException;
import blink.utility.objects.Person;
import blink.utility.security.JWTUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Service Layer entity responsible for receiving requests having to do with person information
 */
@Path("person")
@Api(value = "/person")
public class PersonService {
    private PersonBusiness personBusiness = new PersonBusiness();
    private Gson gson =  new GsonBuilder().serializeNulls().create();

    /**
     * Get all people from database
     * @param jwt JSON web token for authorization
     * @return HTTP Response: 200 OK for people returned
     *                         401 UNAUTHORIZED for invalid JSON Web Token in header
     *                         500 INTERNAL SERVER ERROR for backend error
     */
    @GET
    @Operation(summary = "getAllPeople", description = "Gets all people")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of Person objects which each contain keys (UUID, name, email, title, companyID, accessLevelID)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPeople(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<Person> people = personBusiness.getAllPeople();

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(people));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Gets a Person by UUID
     * @param uuid username from POST request body
     * @return HTTP Response: 200 OK for person found and returned
     *                         401 UNAUTHORIZED for invalid JSON Web Token in header
     *                         404 NOT FOUND if no user with that UUID exists
     *                         500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/id/{id}")
    @GET
    @Operation(summary = "getPerson", description = "Gets a user's information by UUID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Person object which contains keys (UUID, name, email, title, companyID, accessLevelID)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 404, message = "{error: No user with that id exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPersonByUUID(@Parameter(in = ParameterIn.PATH, description = "id", required = true) @PathParam("id") String uuid,
                                      @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            Person person = personBusiness.getPersonByUUID(uuid);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(person));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch(BadRequestException bre){
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        }
        catch(NotFoundException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Insert a person into the database
     * @param username New person's username
     * @param password New person's password
     * @param fName New person's first name
     * @param lName New person's last name
     * @param email New person's email; can be null
     * @param title New person's title; can be null
     * @param accessLevelID New person's accessLevelID; can be null
     * @param jwt JSON Web Token for authorization; must be valid and not expired
     * @return HTTP Response: 200 OK for person inserted successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 FORBIDDEN if requester does not have access to the endpoint
     *                          404 NOT FOUND for non-existent accessLevelID
     *                          409 CONFLICT for username conflict
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @POST
    @Operation(summary = "insertPerson", description = "Insert a new person")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Person object which contains keys (UUID, name, email, title, companyID, accessLevelID)"),
            @ApiResponse(code = 400, message = "{error: specific error message.} (invalid parameters provided)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 404, message = "{error: accessLevelID was not found.}"),
            @ApiResponse(code = 409, message = "{error: A user with that username already exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertPerson(@RequestBody(description = "username", required = true)      @FormParam("username") String username,
                                 @RequestBody(description = "password", required = true)      @FormParam("password") String password,
                                 @RequestBody(description = "fName", required = true)         @FormParam("fName") String fName,
                                 @RequestBody(description = "lName", required = true)         @FormParam("lName") String lName,
                                 @RequestBody(description = "email")                          @FormParam("email") String email,
                                 @RequestBody(description = "title")                          @FormParam("title") String title,
                                 @RequestBody(description = "accessLevelID", required = true) @FormParam("accessLevelID") String accessLevelID,
                                      @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            Authorization.isAdmin(jwt);

            //Send parameters to business layer and store response
            Person person = personBusiness.insertPerson(username, password, fName, lName, email, title, accessLevelID);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(person));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch(BadRequestException bre){
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        }
        catch(ForbiddenException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.FORBIDDEN, nfe.getMessage());
        }
        catch(NotFoundException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(ConflictException ce){
            return ResponseBuilder.buildErrorResponse(Response.Status.CONFLICT, ce.getMessage());
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Update an existing person in the database
     * @param uuid Existing person's UUID
     * @param username Person's new username
     * @param password Person's new plaintext password
     * @param fName updated person first name; can be null
     * @param lName updated person last name; can be null
     * @param email Person's new email; can be null
     * @param title Person's new title; can be null
     * @param accessLevelID Person's new accessLevelID
     * @param jwt JWT for authorization; must be valid and not expired
     * @return HTTP Response: 200 OK for person updated successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 FORBIDDEN if requester does not have access to the endpoint
     *                          404 NOT FOUND for non-existent accessLevelID
     *                          409 CONFLICT for username conflict
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @PUT
    @Operation(summary = "insertPerson", description = "Insert a new person")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Inserted person object which contains keys (UUID, name, email, title, companyID, accessLevelID)"),
            @ApiResponse(code = 400, message = "{error: specific error message.} (invalid parameters provided)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 404, message = "{error: user/accessLevelID was not found.}"),
            @ApiResponse(code = 409, message = "{error: A user with that username already exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePerson(@RequestBody(description = "id", required = true)      @FormParam("id") String uuid,
                                 @RequestBody(description = "username")      @FormParam("username") String username,
                                 @RequestBody(description = "password")      @FormParam("password") String password,
                                 @RequestBody(description = "fName")         @FormParam("fName") String fName,
                                 @RequestBody(description = "lName")         @FormParam("lName") String lName,
                                 @RequestBody(description = "email")         @FormParam("email") String email,
                                 @RequestBody(description = "title")         @FormParam("title") String title,
                                 @RequestBody(description = "accessLevelID") @FormParam("accessLevelID") String accessLevelID,
                                 @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            Authorization.isAdminOrSelf(jwt, uuid);

            //Check that this user has the authority to access this endpoint
            Person requester = personBusiness.getPersonByUUID(JWTUtility.getUUIDFromToken(jwt));
            if(requester.getAccessLevelID() > 1){
                return ResponseBuilder.buildErrorResponse(Response.Status.FORBIDDEN, ResponseBuilder.FORBIDDEN_MESSAGE);
            }

            //Send parameters to business layer and store response
            Person person = personBusiness.updatePerson(uuid, username, password, fName, lName, email, title, accessLevelID);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(person));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch(BadRequestException bre){
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        }
        catch(ForbiddenException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.FORBIDDEN, nfe.getMessage());
        }
        catch(NotFoundException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(ConflictException ce){
            return ResponseBuilder.buildErrorResponse(Response.Status.CONFLICT, ce.getMessage());
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     *
     * Delete a person from the database
     * @param uuid UUID of user to delete from the database
     * @param jwt JSON Web Token for authorizing request
     * @return HTTP Response: 200 OK for person deleted successfully
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 FORBIDDEN if requester does not have access to the endpoint
     *                          404 NOT FOUND when no user with provided UUID exists
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/id/{id}")
    @DELETE
    @Operation(summary = "deletePerson", description = "delete a person's information form the database by UUID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "{success: Successfully deleted person."),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 404, message = "{error: No user with that id exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePersonByUUID(@Parameter(in = ParameterIn.PATH, description = "id", required = true) @PathParam("id") String uuid,
                                      @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isAdmin(jwt);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            JsonObject returnObject = new JsonObject();
            returnObject.addProperty("success", personBusiness.deletePersonByUUID(uuid));
            return ResponseBuilder.buildSuccessResponse(returnObject.toString());
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch(BadRequestException bre){
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        }
        catch(ForbiddenException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.FORBIDDEN, nfe.getMessage());
        }
        catch(NotFoundException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }
}