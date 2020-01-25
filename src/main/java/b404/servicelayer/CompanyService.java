package b404.servicelayer;

import b404.businesslayer.CompanyBusiness;
import b404.utility.ConflictException;
import b404.utility.objects.Company;
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

@Path("company")
@Api(value = "/company")
public class CompanyService {
    private CompanyBusiness companyBusiness = new CompanyBusiness();

    /**
     * Get all companies from database
     * @return - HTTP Response: 200 OK for company found and returned
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @GET
    @Operation(summary = "getAllCompanies", description = "Gets all companies from the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of Company objects which each contain keys (companyID, name)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCompanies(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String JWT) {
        try {
            if(!JWTUtility.validateToken(JWT )){
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid JSON Web Token provided\"}").build();
            }

            //Send parameters to business layer and store response
            ArrayList<Company> companyList = companyBusiness.getAllCompanies();

            //Construct response message
            String responseMessage = "[";
            for(Company company : companyList){
                responseMessage += company.toJSON() + ",";
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
     * Get a company from the database by companyID
     * @return - HTTP Response: 200 OK for company found and returned
     *                          400 BAD REQUEST if companyID is not a valid integer
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          404 NOT FOUND if company id does not exist
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/id/{id}")
    @GET
    @Operation(summary = "getCompanyByID", description = "Gets a company from the database by companyID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The requested company object which each contain keys (companyID, name)"),
            @ApiResponse(code = 400, message = "{error: CompanyID must be a valid integer.)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 404, message = "{error: No company with that ID exists.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompanyByID(@Parameter(in = ParameterIn.PATH, description = "id", required = true) @PathParam("id") String companyID,
                                   @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String JWT) {
        try {
            if(!JWTUtility.validateToken(JWT )){
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid JSON Web Token provided\"}").build();
            }

            //Send parameters to business layer and store response
            Company company = companyBusiness.getCompanyByID(companyID);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return Response.ok(company.toJSON())
                    .build();
        }
        //Catch an InternalServerErrorException and return Internal Server Error response with standard message
        catch(BadRequestException bre){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + bre.getMessage() + "\"}").build();
        }
        //Catch an InternalServerErrorException and return Internal Server Error response with standard message
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

    /**
     * Get a company from the database by companyName
     * @return - HTTP Response: 200 OK for company found and returned
     *                          400 BAD REQUEST if company name was not provided
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          404 NOT FOUND if company name does not exist
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/name/{name}")
    @GET
    @Operation(summary = "getCompanyByName", description = "Gets a company from the database by companyName")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The requested company object which each contain keys (companyID, name)"),
            @ApiResponse(code = 400, message = "{error: Invalid company name provided.)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 404, message = "{error: No company with that name exists.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompanyByName(@Parameter(in = ParameterIn.PATH, description = "name", required = true) @PathParam("name") String companyName,
                                   @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String JWT) {
        try {
            if(!JWTUtility.validateToken(JWT )){
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid JSON Web Token provided\"}").build();
            }

            //Send parameters to business layer and store response
            Company company = companyBusiness.getCompanyByName(companyName);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return Response.ok(company.toJSON())
                    .build();
        }
        //Catch an InternalServerErrorException and return Internal Server Error response with standard message
        catch(BadRequestException bre){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + bre.getMessage() + "\"}").build();
        }
        //Catch an InternalServerErrorException and return Internal Server Error response with standard message
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


    /**
     * Insert a new company into the database
     * @param companyName - New company's name
     * @param JWT - JSON Web Token for authorization; must be valid and not expired
     * @return - HTTP Response: 200 OK for company inserted successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 CONFLICT for username conflict
     *                          404 NOT FOUND for non-existent companyName or accessLevelID
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @POST
    @Operation(summary = "insertCompany", description = "Insert a new company")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Company object which contains keys (companyID, companyName)"),
            @ApiResponse(code = 400, message = "{error: A company name must be provided.} (invalid parameters provided)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: A company with that name already exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCompany(@RequestBody(description = "companyName", required = true) @FormParam("companyName") String companyName,
                                 @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String JWT) {

        try {
            if(!JWTUtility.validateToken(JWT )){
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid JSON Web Token provided\"}").build();
            }

            //Send parameters to business layer and store response
            Company company = companyBusiness.insertCompany(companyName);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return Response.ok(company.toJSON())
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
                                 @RequestBody(description = "fName")         @FormParam("fName") String fName,
                                 @RequestBody(description = "lName")         @FormParam("lName") String lName,
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
     * Get a company from the database by companyName
     * @return - HTTP Response: 200 OK for company found and
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          404 NOT FOUND if company id does not exist
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/id/{id}")
    @DELETE
    @Operation(summary = "deleteCompanyByID", description = "Delete a company from the database by companyID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "{success: Company successfully deleted."),
            @ApiResponse(code = 400, message = "{error: CompanyID must be a valid integer.)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 404, message = "{error: No company with that ID exists.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCompanyByID(@Parameter(in = ParameterIn.PATH, description = "id", required = true) @PathParam("id") String companyID,
                                     @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String JWT) {
        try {
            if(!JWTUtility.validateToken(JWT )){
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid JSON Web Token provided\"}").build();
            }

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return Response.ok("{\"success\":\"" + companyBusiness.deleteCompanyByID(companyID) + "\"}")
                    .build();
        }
        //Catch an InternalServerErrorException and return Internal Server Error response with standard message
        catch(BadRequestException bre){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + bre.getMessage() + "\"}").build();
        }
        //Catch an InternalServerErrorException and return Internal Server Error response with standard message
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
