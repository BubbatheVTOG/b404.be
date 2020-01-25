package b404.servicelayer;

import b404.businesslayer.PersonBusiness;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

import b404.utility.ConflictException;
import b404.utility.objects.Person;
import b404.utility.security.JWTUtility;
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
import java.util.ArrayList;

/**
 * Service Layer entity responsible for receiving requests having to do with person information
 */
@Path("person")
@Api(value = "/person")
public class PersonService {
    private PersonBusiness personBusiness = new PersonBusiness();

    /**
     * Get all people from database
     * @return - HTTP Response: 200 OK for person found and returned
     *                         400 BAD REQUEST for invalid username or password syntax
     *                         401 UNAUTHORIZED for invalid JSON Web Token in header
     *                         404 NOT FOUND when requested user is not found
     *                         500 INTERNAL SERVER ERROR for backend error
     */
    @GET
    @Operation(summary = "getPerson", description = "Gets a user's information by UUID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of Person objects which each contain keys (UUID, name, email, title, companyID, accessLevelID)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPeople(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String JWT) {
        try {
            if(!JWTUtility.validateToken(JWT )){
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid JSON Web Token provided\"}").build();
            }

            //Send parameters to business layer and store response
            ArrayList<Person> people = personBusiness.getAllPeople();

            //Construct response message
            String responseMessage = "[";
            for(Person person : people){
                responseMessage += person.toSecureJSON() + ",";
            }
            //remove trailing comma and add closing bracket
            responseMessage = responseMessage.substring(0,responseMessage.length() - 1) + "]";

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return Response.ok(responseMessage)
                    .build();
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

    /**
     * Gets a Person by UUID
     * @param UUID - username from POST request body
     * @return - HTTP Response: 200 OK for person found and returned
     *                         400 BAD REQUEST for invalid username or password syntax
     *                         401 UNAUTHORIZED for invalid JSON Web Token in header
     *                         404 UNAUTHORIZED for invalid JSON Web Token in header
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
    public Response getPersonByUUID(@Parameter(in = ParameterIn.PATH, description = "id", required = true) @PathParam("id") String UUID,
                                      @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String JWT) {
        try {
            if(!JWTUtility.validateToken(JWT )){
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid JSON Web Token provided\"}").build();
            }

            //Send parameters to business layer and store response
            Person person = personBusiness.getPersonByUUID(UUID);

            String jwtToken = JWTUtility.generateToken(person.getUUID());

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return Response.ok(person.toSecureJSON())
                    .header("Authorization", jwtToken)
                    .build();
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

    /**
     * Insert a person into the database
     * @param username - updated person username; can be null
     * @param password - updated person password; can be null
     * @param fName - updated person first name; can be null
     * @param lName - updated person last name; can be null
     * @param email - updated person email; can be null
     * @param title - updated person title; can be null
     * @param accessLevelID - updated person accessLevelID; can be null
     * @param JWT - JSON Web Token for authorization; must be valid and not expired
     * @return - HTTP Response: 200 OK for person inserted successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 CONFLICT for username conflict
     *                          404 NOT FOUND for non-existent companyName or accessLevelID
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @POST
    @Operation(summary = "insertPerson", description = "Insert a new person")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Person object which contains keys (UUID, name, email, title, companyID, accessLevelID)"),
            @ApiResponse(code = 400, message = "{error: specific error message.} (invalid parameters provided)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: A user with that username already exists.}"),
            @ApiResponse(code = 404, message = "{error: CompanyName/accessLevelID was not found.}"),
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
                                      @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String JWT) {

        try {
            if(!JWTUtility.validateToken(JWT )){
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid JSON Web Token provided\"}").build();
            }

            //Send parameters to business layer and store response
            Person person = personBusiness.insertPerson(username, password, fName, lName, email, title, accessLevelID);

            String jwtToken = JWTUtility.generateToken(person.getUUID());

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return Response.ok(person.toSecureJSON())
                    .header("Authorization", jwtToken)
                    .build();
        }
        //Catch all business logic related errors and return relevant response with message from error
        catch(ConflictException nfe){
            return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"" + nfe.getMessage() + "\"}").build();
        }
        catch(NotFoundException nfe){
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"" + nfe.getMessage() + "\"}").build();
        }
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

    /**
     * Update an existing person in the database
     * @param UUID -  Existing person's UUID
     * @param username - Person's new username
     * @param password - Person's new plaintext password
     * @param fName - updated person first name; can be null
     * @param lName - updated person last name; can be null
     * @param email - Person's new email; can be null
     * @param title - Person's new title; can be null
     * @param accessLevelID - Person's new accessLevelID
     * @param JWT - JWT for authorization; must be valid and not expired
     * @return - HTTP Response: 200 OK for person updated successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 CONFLICT for username conflict
     *                          404 NOT FOUND for non-existent companyName or accessLevelID
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @PUT
    @Operation(summary = "insertPerson", description = "Insert a new person")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Update person object which contains keys (UUID, name, email, title, companyID, accessLevelID)"),
            @ApiResponse(code = 400, message = "{error: specific error message.} (invalid parameters provided)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: A user with that username already exists.}"),
            @ApiResponse(code = 404, message = "{error: user/CompanyName/accessLevelID was not found.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePerson(@RequestBody(description = "id", required = true)      @FormParam("id") String UUID,
                                 @RequestBody(description = "username")      @FormParam("username") String username,
                                 @RequestBody(description = "password")      @FormParam("password") String password,
                                 @RequestBody(description = "fName")         @FormParam("password") String fName,
                                 @RequestBody(description = "lName")         @FormParam("password") String lName,
                                 @RequestBody(description = "email")         @FormParam("email") String email,
                                 @RequestBody(description = "title")         @FormParam("title") String title,
                                 @RequestBody(description = "accessLevelID") @FormParam("accessLevelID") String accessLevelID,
                                 @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String JWT) {

        try {
            if(!JWTUtility.validateToken(JWT )){
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid JSON Web Token provided\"}").build();
            }

            //Send parameters to business layer and store response
            Person person = personBusiness.updatePerson(UUID, username, password, fName, lName, email, title, accessLevelID);

            String jwtToken = JWTUtility.generateToken(person.getUUID());

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return Response.ok(person.toSecureJSON())
                    .header("Authorization", jwtToken)
                    .build();
        }
        //Catch all business logic related errors and return relevant response with message from error
        catch(ConflictException nfe){
            return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"" + nfe.getMessage() + "\"}").build();
        }
        catch(NotFoundException bre){
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"" + bre.getMessage() + "\"}").build();
        }
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

    /**
     *
     * Delete a person from the database
     * @param UUID - UUID of user to delete from the database
     * @param JWT - JSON Web Token for authorizing request
     * @return - HTTP Response: 200 OK for person inserted successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          404 NOT FOUND when no user with provided UUID exists
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/id/{id}")
    @DELETE
    @Operation(summary = "deletePerson", description = "delete a person's information form the database by UUID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "{success: Successfully deleted person."),
            @ApiResponse(code = 400, message = "{error: A user ID must be provided."),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 404, message = "{error: No user with that id exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePersonByUUID(@Parameter(in = ParameterIn.PATH, description = "id", required = true) @PathParam("id") String UUID,
                                      @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String JWT) {
        try {
            if(!JWTUtility.validateToken(JWT )){
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid JSON Web Token provided\"}").build();
            }

            //Send parameters to business layer and store response
            String responseMessage = personBusiness.deletePersonByUUID(UUID);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return Response.ok("{\"success\":\"" + responseMessage + "\"}")
                    .build();
        }
        catch(BadRequestException bre){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + bre.getMessage() + "\"}").build();
        }
        //Catch a NotFoundException and return Not Found response with message from error
        catch(NotFoundException nfe){
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"" + nfe.getMessage() + "\"}").build();
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