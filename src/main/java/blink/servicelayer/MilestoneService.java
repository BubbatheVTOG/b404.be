package blink.servicelayer;

import blink.businesslayer.Authorization;
import blink.businesslayer.MilestoneBusiness;
import blink.utility.objects.Milestone;
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
 * Service Layer entity responsible for receiving requests having to do with milestone information
 */
@Path("milestone")
@Api(value = "/milestone")
public class MilestoneService {
    private MilestoneBusiness milestoneBusiness = new MilestoneBusiness();
    private Gson gson = new GsonBuilder().setDateFormat("MMM d, yyy HH:mm:ss").serializeNulls().create();

    /**
     * Get all milestones
     * @param jwt JSON web token for authorization
     * @return HTTP Response: 200 OK for milestones returned
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @GET
    @Operation(summary = "getAllMilestones", description = "Gets all milestones in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of milestone objects which each contain keys (milestoneID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, companyID)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMilestones(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<Milestone> milestoneList = milestoneBusiness.getAllMilestones(JWTUtility.getUUIDFromToken(jwt));

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(milestoneList));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch (NotAuthorizedException nae) {
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
            //return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Get active milestones
     * @param jwt JSON web token for authorization
     * @return HTTP Response: 200 OK for active milestones returned
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/active")
    @GET
    @Operation(summary = "getActiveMilestones", description = "Gets all active milestones")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of active milestone objects which each contain keys (milestoneID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, companyID)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActiveMilestones(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<Milestone> milestoneList = milestoneBusiness.getActiveMilestones(JWTUtility.getUUIDFromToken(jwt));

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
     * @param jwt JSON web token for authorization
     * @return HTTP Response: 200 OK for archived milestones returned
    *                           401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/archived")
    @GET
    @Operation(summary = "getArchivedMilestones", description = "Gets all archived milestones")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of archived milestone objects which each contain keys (milestoneID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, companyID)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArchivedMilestones(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<Milestone> milestoneList = milestoneBusiness.getArchivedMilestones(JWTUtility.getUUIDFromToken(jwt));

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
     * Get a milestone by milestoneID
     * @param milestoneID ID of milestone to retrieve
     * @param jwt JSON web token for authorization
     * @return HTTP Response: 200 OK for milestone returned
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          404 NOT_FOUND for MilestoneID not found
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/{id}")
    @GET
    @Operation(summary = "getMilestoneByID", description = "Gets a specific milestone by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Milestone object which contains keys (milestoneID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, companyID)"),
            @ApiResponse(code = 400, message = "{error: MilestoneID must be a valid integer.}"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 404, message = "{error: No milestone with that ID exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMilestoneByID(@Parameter(in = ParameterIn.PATH, name = "id") @PathParam("id") String milestoneID,
                                     @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            Milestone milestone = milestoneBusiness.getMilestoneByID(JWTUtility.getUUIDFromToken(jwt), milestoneID);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(milestone));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch (BadRequestException bre) {
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        } catch (NotAuthorizedException nae) {
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        } catch (NotFoundException nfe) {
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
            //return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Insert a milestone into the database
     * @param name New milestone's name
     * @param description New milestone's description
     * @param startDate New milestone's start date
     * @param deliveryDate New milestone's delivery date
     * @param companyID Company to assign the milestone to
     * @param jwt JSON Web Token for authorization; must be valid and not expired
     * @return HTTP Response: 200 OK for milestone inserted successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 FORBIDDEN if requester does not have access to the endpoint
     *                          404 NOT FOUND for non-existent companyID
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @POST
    @Operation(summary = "insertMilestone", description = "Insert a new milestone")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Milestone object which each contains keys (milestoneID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, companyID)"),
            @ApiResponse(code = 400, message = "{error: specific error message.} (invalid parameters provided)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 404, message = "{error: No company with that ID exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertMilestone(@RequestBody(description = "name", required = true)             @FormParam("name") String name,
                                    @RequestBody(description = "description", required = true)      @FormParam("description") String description,
                                    @RequestBody(description = "startDate", required = true)        @FormParam("startDate") String startDate,
                                    @RequestBody(description = "deliveryDate", required = true)     @FormParam("deliveryDate") String deliveryDate,
                                    @RequestBody(description = "companyID", required = true)        @FormParam("companyID") String companyID,
                                    @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            Authorization.isAdmin(jwt);

            //Send parameters to business layer and store response
            Milestone milestone = milestoneBusiness.insertMilestone(name, description, startDate, deliveryDate, companyID);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(milestone));
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

    /**
     * Update a milestone into the database
     * @param name New milestone name
     * @param description New milestone description
     * @param startDate New milestone start date
     * @param deliveryDate New milestone delivery date
     * @param companyID Company to assign the milestone to
     * @param jwt JSON Web Token for authorization; must be valid and not expired
     * @return HTTP Response: 200 OK for milestone updated successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 FORBIDDEN if requester does not have access to the endpoint
     *                          404 NOT FOUND for non-existent companyID
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @PUT
    @Operation(summary = "updateMilestone", description = "Update an existing milestone")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Milestone object which each contains keys (milestoneID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, companyID)"),
            @ApiResponse(code = 400, message = "{error: specific error message.} (invalid parameters provided)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 404, message = "{error: No company/milestone with that ID exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMilestone(@RequestBody(description = "id", required = true)  @FormParam("id") String milestoneID,
                                    @RequestBody(description = "name")                 @FormParam("name") String name,
                                    @RequestBody(description = "description")          @FormParam("description") String description,
                                    @RequestBody(description = "startDate")            @FormParam("startDate") String startDate,
                                    @RequestBody(description = "deliveryDate")         @FormParam("deliveryDate") String deliveryDate,
                                    @RequestBody(description = "companyID")            @FormParam("companyID") String companyID,
                                    @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            Authorization.isAdmin(jwt);

            //Send parameters to business layer and store response
            Milestone milestone = milestoneBusiness.updateMilestone(milestoneID, name, description, startDate, deliveryDate, companyID);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(milestone));
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

    /**
     *
     * Delete a person from the database
     * @param id ID of milestone to delete
     * @param jwt JSON Web Token for authorizing request
     * @return HTTP Response: 200 OK for milestone deleted successfully
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 FORBIDDEN if requester does not have access to the endpoint
     *                          404 NOT FOUND when no user with provided UUID exists
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/{id}")
    @DELETE
    @Operation(summary = "deleteMilestone", description = "delete a milestone from the database by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "{success: Successfully deleted milestone."),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 404, message = "{error: No milestone with that id exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMilestoneByID(@Parameter(in = ParameterIn.PATH, description = "id", required = true) @PathParam("id") String id,
                                       @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isAdmin(jwt);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            JsonObject returnObject = new JsonObject();
            returnObject.addProperty("success", milestoneBusiness.deleteMilestoneByID(id));
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

    /**
     * Archive a milestone
     * @param milestoneID ID of milestone to archive
     * @return HTTP Response: 200 OK for milestone archived successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 FORBIDDEN if requester does not have access to the endpoint
     *                          404 NOT FOUND for non-existent milestoneID
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/archive")
    @PUT
    @Operation(summary = "archiveMilestone", description = "Archive an existing milestone")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "{success: successfully archived milestone}"),
            @ApiResponse(code = 400, message = "{error: Milestone ID must be a valid integer.}"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 404, message = "{error: No milestone with that ID exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response archiveMilestone(@RequestBody(description = "id", required = true) @FormParam("id") String milestoneID,
                                    @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            Authorization.isAdmin(jwt);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            JsonObject returnObject = new JsonObject();
            returnObject.addProperty("success", milestoneBusiness.archiveMilestone(milestoneID));
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

    /**
     * Unarchive a milestone
     * @param milestoneID ID of milestone to unarchive
     * @return HTTP Response: 200 OK for milestone unarchived successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 FORBIDDEN if requester does not have access to the endpoint
     *                          404 NOT FOUND for non-existent milestoneID
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/unarchive")
    @PUT
    @Operation(summary = "unarchiveMilestone", description = "Unarchive an existing milestone")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "{success: successfully unarchived milestone}"),
            @ApiResponse(code = 400, message = "{error: Milestone ID must be a valid integer.}"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 404, message = "{error: No milestone with that ID exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response unarchiveMilestone(@RequestBody(description = "id", required = true) @FormParam("id") String milestoneID,
                                     @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            Authorization.isAdmin(jwt);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            JsonObject returnObject = new JsonObject();
            returnObject.addProperty("success", milestoneBusiness.unarchiveMilestone(milestoneID));
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
