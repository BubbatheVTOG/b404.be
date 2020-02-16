package blink.servicelayer;

import blink.businesslayer.Authorization;
import blink.businesslayer.MilestoneBusiness;
import blink.utility.exceptions.ConflictException;
import blink.utility.objects.Milestone;
import blink.utility.objects.Person;
import blink.utility.security.JWTUtility;
import com.google.gson.Gson;
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
@Path("milestone")
@Api(value = "/milestone")
public class MilestoneService {
    private MilestoneBusiness milestoneBusiness = new MilestoneBusiness();
    private static Gson gson = new Gson();

    /**
     * Get all milestones
     * @param jwt - JSON web token for authorization
     * @return - HTTP Response: 200 OK for milestones returned
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/all")
    @GET
    @Operation(summary = "getAllMilestones", description = "Gets all milestones in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of milestone objects which each contain keys (milestoneID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, companyID)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMilestones(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<Milestone> milestoneList = milestoneBusiness.getAllMilestones();

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(milestoneList));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch (NotAuthorizedException nae) {
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Get active milestones
     * @param jwt - JSON web token for authorization
     * @return - HTTP Response: 200 OK for active milestones returned
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/active")
    @GET
    @Operation(summary = "getActiveMilestones", description = "Gets all active milestones")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of active milestone objects which each contain keys (milestoneID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, companyID)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActiveMilestones(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<Milestone> milestoneList = milestoneBusiness.getActiveMilestones();

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(milestoneList));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch (NotAuthorizedException nae) {
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Get archived milestones
     * @param jwt - JSON web token for authorization
     * @return - HTTP Response: 200 OK for archived milestones returned
    *                           401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/archived")
    @GET
    @Operation(summary = "getArchivedMilestones", description = "Gets all archived milestones")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of archived milestone objects which each contain keys (milestoneID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, companyID)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArchivedMilestones(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<Milestone> milestoneList = milestoneBusiness.getArchivedMilestones();

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(milestoneList));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch (NotAuthorizedException nae) {
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Insert a person into the database
     * @param username - New person's username
     * @param password - New person's password
     * @param fName - New person's first name
     * @param lName - New person's last name
     * @param email - New person's email; can be null
     * @param title - New person's title; can be null
     * @param accessLevelID - New person's accessLevelID; can be null
     * @param jwt - JSON Web Token for authorization; must be valid and not expired
     * @return - HTTP Response: 200 OK for person inserted successfully
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
    public Response insertPerson(@RequestBody(description = "name", required = true)      @FormParam("username") String username,
                                 @RequestBody(description = "description", required = true)      @FormParam("password") String password,
                                 @RequestBody(description = "startDate", required = true)         @FormParam("fName") String fName,
                                 @RequestBody(description = "deliveryDate", required = true)         @FormParam("lName") String lName,
                                 @RequestBody(description = "companyID")                          @FormParam("email") String email,
                                 @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            Authorization.isAdmin(jwt);

            //Send parameters to business layer and store response
            Milestone milestone = milestoneBusiness.insertMilestone();

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return Response.ok(gson.toJson(person))
                    .header("Authorization", jwtToken)
                    .build();
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
}
