package blink.servicelayer;

import blink.businesslayer.Authorization;
import blink.businesslayer.WorkflowBusiness;
import blink.utility.objects.Workflow;
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

@Path("workflow")
@Api(value = "/workflow")
public class WorkflowService {
    private WorkflowBusiness workflowBusiness = new WorkflowBusiness();
    private static Gson gson =  new GsonBuilder().serializeNulls().create();

    /**
     * Get all workflows
     * @param jwt - JSON web token for authorization
     * @return - HTTP Response: 200 OK for workflows returned
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @GET
    @Operation(summary = "getAllWorkflows", description = "Gets all workflows in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of workflow objects which each contain keys (workflowID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, milestoneID, companyID, percentComplete, steps)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllWorkflows(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<Workflow> workflowList = workflowBusiness.getAllWorkflows(JWTUtility.getUUIDFromToken(jwt));

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(workflowList));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch (NotAuthorizedException nae) {
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Get active workflows
     * @param jwt - JSON web token for authorization
     * @return - HTTP Response: 200 OK for active workflows returned
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/active")
    @GET
    @Operation(summary = "getActiveWorkflows", description = "Gets all active workflows")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of active workflow objects which each contain keys (workflowID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, milestoneID, companyID, percentComplete, steps)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActiveWorkflows(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<Workflow> workflowList = workflowBusiness.getActiveWorkflows(JWTUtility.getUUIDFromToken(jwt));

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(workflowList));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch (NotAuthorizedException nae) {
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Get archived workflows
     * @param jwt - JSON web token for authorization
     * @return - HTTP Response: 200 OK for archived workflows returned
     *                           401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/archived")
    @GET
    @Operation(summary = "getArchivedWorkflows", description = "Gets all archived workflows")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of archived workflow objects which each contain keys (workflowID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, milestoneID, companyID, percentComplete, steps)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArchivedWorkflows(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<Workflow> workflowList = workflowBusiness.getArchivedWorkflows(JWTUtility.getUUIDFromToken(jwt));

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(workflowList));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch (NotAuthorizedException nae) {
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Get a workflow by workflowID
     * @param workflowID - ID of workflow to retrieve
     * @param jwt - JSON web token for authorization
     * @return - HTTP Response: 200 OK for archived workflows returned
     *                           401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/{id}")
    @GET
    @Operation(summary = "getWorkflowByID", description = "Gets a specific workflow by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Workflow object which contains keys (workflowID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, milestoneID, companyID, percentComplete, steps)"),
            @ApiResponse(code = 400, message = "{error: WorkflowID must be a valid integer.)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 404, message = "{error: No workflow with that ID exists.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWorkflowByID(@Parameter(in = ParameterIn.PATH, name = "id") @PathParam("id") String workflowID,
                                     @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            Workflow workflow = workflowBusiness.getWorkflowByID(JWTUtility.getUUIDFromToken(jwt), workflowID);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(workflow));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch (BadRequestException bre) {
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        } catch (NotAuthorizedException nae) {
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        } catch (NotFoundException nfe) {
            return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, nfe.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Insert a template workflow into the database
     * @param workflowJson -
     * @param jwt - JSON Web Token for authorization; must be valid and not expired
     * @return - HTTP Response: 200 OK for workflow inserted successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 FORBIDDEN if requester does not have access to the endpoint
     *                          404 NOT FOUND for non-existent companyID
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/template")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "insertTemplateWorkflow", description = "Insert a new template workflow")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Workflow object which each contains keys (workflowID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, milestoneID, companyID, percentComplete, steps)"),
            @ApiResponse(code = 400, message = "{error: specific error message.} (invalid parameters provided)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 404, message = "{error: No company with that ID exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertTemplateWorkflow(@RequestBody(description = "Workflow Json Object", required = true) @FormParam("workflow") String workflowJson,
                                           @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            Authorization.isAdmin(jwt);

            //Send parameters to business layer and store response
            Workflow workflow = workflowBusiness.insertTemplateWorkflow(workflowJson);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(workflow));
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
     * Insert a concrete workflow into the database
     * @param workflowJson - String representation of workflow json object
     * @param jwt - JSON Web Token for authorization; must be valid and not expired
     * @return - HTTP Response: 200 OK for workflow inserted successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 FORBIDDEN if requester does not have access to the endpoint
     *                          404 NOT FOUND for non-existent companyID
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/concrete")
    @POST
    @Operation(summary = "insertConcreteWorkflow", description = "Insert a new concrete workflow")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Workflow object which each contains keys (workflowID, name, description, createdDate, lastUpdatedDate, startDate, deliveryDate, completedDate, archived, milestoneID, companyID, percentComplete, steps)"),
            @ApiResponse(code = 400, message = "{error: specific error message.} (invalid parameters provided)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 404, message = "{error: No company/milestone with that ID exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertConcreteWorkflow(@RequestBody(description = "workflow", required = true)          @FormParam("workflow") String workflowJson,
                                           @Parameter(in = ParameterIn.HEADER, name = "Authorization")  @HeaderParam("Authorization") String jwt) {

        try {
            Authorization.isAdmin(jwt);

            //Send parameters to business layer and store response
            Workflow workflow = workflowBusiness.insertWorkflow(workflowJson);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(workflow));
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
     * @param id - ID of workflow to delete
     * @param jwt - JSON Web Token for authorizing request
     * @return - HTTP Response: 200 OK for workflow deleted successfully
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 FORBIDDEN if requester does not have access to the endpoint
     *                          404 NOT FOUND when no user with provided UUID exists
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/{id}")
    @DELETE
    @Operation(summary = "deleteWorkflow", description = "delete a workflow from the database by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "{success: Successfully deleted workflow."),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 404, message = "{error: No workflow with that id exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteWorkflowByID(@Parameter(in = ParameterIn.PATH, description = "id", required = true) @PathParam("id") String id,
                                        @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isAdmin(jwt);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            JsonObject returnObject = new JsonObject();
            returnObject.addProperty("success", workflowBusiness.deleteWorkflowByID(id));
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
     * Archive a workflow
     * @param workflowID - ID of workflow to archive
     * @return - HTTP Response: 200 OK for workflow archived successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 FORBIDDEN if requester does not have access to the endpoint
     *                          404 NOT FOUND for non-existent workflowID
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/archive")
    @PUT
    @Operation(summary = "archiveWorkflow", description = "Archive an existing workflow")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "{success: successfully archived workflow}"),
            @ApiResponse(code = 400, message = "{error: Workflow ID must be a valid integer.}"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 404, message = "{error: No workflow with that ID exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response archiveWorkflow(@RequestBody(description = "id", required = true) @FormParam("id") String workflowID,
                                     @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            Authorization.isAdmin(jwt);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            JsonObject returnObject = new JsonObject();
            returnObject.addProperty("success", workflowBusiness.archiveWorkflow(workflowID));
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
     * Unarchive a workflow
     * @param workflowID - ID of workflow to unarchive
     * @return - HTTP Response: 200 OK for workflow unarchived successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 FORBIDDEN if requester does not have access to the endpoint
     *                          404 NOT FOUND for non-existent workflowID
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/unarchive")
    @PUT
    @Operation(summary = "unarchiveWorkflow", description = "Unarchive an existing workflow")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "{success: successfully unarchived workflow}"),
            @ApiResponse(code = 400, message = "{error: Workflow ID must be a valid integer.}"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 404, message = "{error: No workflow with that ID exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response unarchiveWorkflow(@RequestBody(description = "id", required = true) @FormParam("id") String workflowID,
                                       @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            Authorization.isAdmin(jwt);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            JsonObject returnObject = new JsonObject();
            returnObject.addProperty("success", workflowBusiness.unarchiveWorkflow(workflowID));
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
