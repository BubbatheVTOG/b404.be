package b404.servicelayer;

import b404.businesslayer.CompanyBusiness;
import b404.utility.ConflictException;
import b404.utility.objects.Company;
import b404.utility.objects.Person;
import b404.utility.security.JWTUtility;
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
    public Response getAllCompanies(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            this.validateToken(jwt);

            //Send parameters to business layer and store response
            List<Company> companyList = companyBusiness.getAllCompanies();

            //Construct response message
            StringBuilder responseMessage = new StringBuilder();
            responseMessage.append("[");
            for(Company company : companyList){
                responseMessage.append(company.toJSON());
                responseMessage.append(",");
            }
            //remove trailing comma and add closing bracket
            responseMessage.setLength(responseMessage.length()-1);
            responseMessage.append("]");

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(responseMessage.toString());
        }
        //Catch all business logic related errors and return relevant response with message from error
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(InternalServerErrorException isee){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
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
                                   @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            this.validateToken(jwt);

            //Send parameters to business layer and store response
            Company company = companyBusiness.getCompanyByID(companyID);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(company.toJSON());
        }
        //Catch all business logic related errors and return relevant response with message from error
        catch(BadRequestException bre){
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(NotFoundException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        }
        catch(InternalServerErrorException isee){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
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
                                   @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            this.validateToken(jwt);

            //Send parameters to business layer and store response
            Company company = companyBusiness.getCompanyByName(companyName);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(company.toJSON());
        }
        catch(BadRequestException bre){
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(NotFoundException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        }
        catch(InternalServerErrorException isee){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Get all companies from database
     * @return - HTTP Response: 200 OK for company found and returned
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/people/{companyID}")
    @GET
    @Operation(summary = "getAllPeopleByCompany", description = "Gets all people who are a part of a company")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of Person objects which each contain keys (UUID, name, email, title, companyID, accessLevelID)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 404, message = "{error: No company with that ID exists.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPeopleByCompany(@Parameter(in = ParameterIn.PATH, description = "companyID", required = true) @PathParam("companyID") String companyID,
                                          @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            this.validateToken(jwt);

            //Send parameters to business layer and store response
            List<Person> personList = companyBusiness.getAllPeopleByCompany(companyID);

            //Construct response message
            StringBuilder responseMessage = new StringBuilder();
            responseMessage.append("[");
            for(Person person : personList){
                responseMessage.append(person.toJSON());
                responseMessage.append(",");
            }
            //remove trailing comma and add closing bracket
            responseMessage.setLength(responseMessage.length()-1);
            responseMessage.append("]");

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(responseMessage.toString());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(NotFoundException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        }
        catch(InternalServerErrorException isee){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }


    /**
     * Insert a new company into the database
     * @param companyName - New company's name
     * @param jwt - JSON Web Token for authorization; must be valid and not expired
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
    public Response insertCompany(@RequestBody(description = "name", required = true) @FormParam("name") String companyName,
                                 @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            this.validateToken(jwt);

            //Send parameters to business layer and store response
            Company company = companyBusiness.insertCompany(companyName);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(company.toJSON());
        }
        //Catch all business logic related errors and return relevant response with message from error
        catch(BadRequestException bre){
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(NotFoundException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        }
        catch(ConflictException ce){
            return ResponseBuilder.buildErrorResponse(Response.Status.CONFLICT, ce.getMessage());
        }
        catch(InternalServerErrorException isee){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Update an existing company in the database
     * @param companyID -  Existing company's UUID
     * @param companyName - company's new name; can be null
     * @param jwt - JWT for authorization; must be valid and not expired
     * @return - HTTP Response: 200 OK for company updated successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          404 NOT FOUND for non-existent companyID
     *                          409 CONFLICT for username conflict
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @PUT
    @Operation(summary = "updateCompany", description = "Update an existing company")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated company object which contains keys (companyID, companyName)"),
            @ApiResponse(code = 400, message = "{error: A company ID must be provided.}"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 404, message = "{error: No company with that ID exists.}"),
            @ApiResponse(code = 409, message = "{error: A company with that name already exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCompany(@RequestBody(description = "id", required = true)          @FormParam("id") String companyID,
                                 @RequestBody(description = "name")                          @FormParam("name") String companyName,
                                 @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            this.validateToken(jwt);

            //Send parameters to business layer and store response
            Company company = companyBusiness.updateCompany(companyID, companyName);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(company.toJSON());
        }
        //Catch all business logic related errors and return relevant response with message from error
        catch(BadRequestException bre){
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(NotFoundException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        }
        catch(ConflictException ce){
            return ResponseBuilder.buildErrorResponse(Response.Status.CONFLICT, ce.getMessage());
        }
        catch(InternalServerErrorException isee){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Delete a company from the database by companyID
     * @return - HTTP Response: 200 OK for company found and deleted
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
                                     @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            this.validateToken(jwt);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", companyBusiness.deleteCompanyByID(companyID));
            return ResponseBuilder.buildSuccessResponse(jsonResponse.toString());
        }
        //Catch all business logic related errors and return relevant response with message from error
        catch(BadRequestException bre){
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(NotFoundException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        }
        catch(InternalServerErrorException isee){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Delete a company from the database by companyName
     * @return - HTTP Response: 200 OK for company found and deleted
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          404 NOT FOUND if company id does not exist
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/id/{id}")
    @DELETE
    @Operation(summary = "deleteCompanyByName", description = "Delete a company from the database by companyID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "{success: Company successfully deleted."),
            @ApiResponse(code = 400, message = "{error: A company name must be provided.)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 404, message = "{error: No company with that ID exists.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCompanyByName(@Parameter(in = ParameterIn.PATH, description = "name", required = true) @PathParam("name") String companyName,
                                      @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            this.validateToken(jwt);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(companyBusiness.deleteCompanyByID(companyName));
        }
        //Catch all business logic related errors and return relevant response with message from error
        catch(BadRequestException bre){
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(NotFoundException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        }
        catch(InternalServerErrorException isee){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Add a person to an existing company in the database
     * @param companyID -  Company ID to add person to
     * @param personID - Person's UUID
     * @param jwt - JWT for authorization; must be valid and not expired
     * @return - HTTP Response: 200 OK for person added to company successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          404 NOT FOUND for non-existent companyId or personID
     *                          409 CONFLICT for company name conflict
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/person/add")
    @POST
    @Operation(summary = "addPersonToCompany", description = "Add an existing person to a company")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "{success: person successfully added to company"),
            @ApiResponse(code = 400, message = "{error: A companyID/personID must be provided.}"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 404, message = "{error: No company/person with that ID exists.}"),
            @ApiResponse(code = 409, message = "{error: That person is already a part of that company.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPersonToCompany(@RequestBody(description = "companyID", required = true)     @FormParam("companyID") String companyID,
                                       @RequestBody(description = "personID", required = true)      @FormParam("personID") String personID,
                                       @Parameter(in = ParameterIn.HEADER, name = "Authorization")  @HeaderParam("Authorization") String jwt) {

        try {
            this.validateToken(jwt);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(companyBusiness.deleteCompanyByID(companyID));
        }
        //Catch all business logic related errors and return relevant response with message from error
        catch(BadRequestException bre){
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(NotFoundException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        }
        catch(InternalServerErrorException isee){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Delete a person from a company
     * @param companyID -  Company ID to add person to
     * @param personID - Person's UUID
     * @param jwt - JWT for authorization; must be valid and not expired
     * @return - HTTP Response: 200 OK for person updated successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          404 NOT FOUND for non-existent companyName or accessLevelID
     *                          409 CONFLICT for username conflict
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/person/delete")
    @POST
    @Operation(summary = "deletePersonFromCompany", description = "Add an existing person to a company")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "{success: person successfully removed from company"),
            @ApiResponse(code = 400, message = "{error: A companyID/personID must be provided.}"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 404, message = "{error: No company/person with that ID exists.}"),
            @ApiResponse(code = 409, message = "{error: That person is already a part of that company.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response removePersonFromCompany(@RequestBody(description = "companyID", required = true)     @FormParam("companyID") String companyID,
                                            @RequestBody(description = "personID", required = true)      @FormParam("personID") String personID,
                                            @Parameter(in = ParameterIn.HEADER, name = "Authorization")  @HeaderParam("Authorization") String jwt) {

        try {
            this.validateToken(jwt);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(companyBusiness.deleteCompanyByID(companyID));
        }
        //Catch all business logic related errors and return relevant response with message from error
        catch(BadRequestException bre){
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(NotFoundException nfe){
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        }
        catch(InternalServerErrorException isee){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Checks that the jwt is valid and throws a notAuthorized exception if not valid
     * @param jwt - JSON Web Token to validate
     */
    private void validateToken(String jwt){
        if(Boolean.FALSE.equals(JWTUtility.validateToken(jwt))){
            throw new NotAuthorizedException("Invalid JSON Web Token provided.");
        }
    }
}
