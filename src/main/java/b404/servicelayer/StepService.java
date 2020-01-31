package b404.servicelayer;

import b404.businesslayer.StepBusiness;
import b404.utility.ConflictException;
import b404.utility.objects.Step;
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
import java.util.List;

@Path("step")
@Api(value = "/step")
public class StepService {
    private static final String ERROR_STRING = "{\"error\":\"";
    private StepBusiness stepBusiness = new StepBusiness();

    /**
     * Get all steps by workflowID from the database
     * @return - HTTP Response: 200 OK for steps found and returned
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/id/{id}")
    @GET
    @Operation(summary = "getSteps", description = "Gets all steps by workflowID from the database.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of Step objects which each contain a list of related step objects."),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.)"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSteps(@Parameter(in = ParameterIn.PATH, description = "workflowID", required = true) @PathParam("id") String workflowID,
                             @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            if(Boolean.TRUE.equals(JWTUtility.validateToken(jwt))){
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid JSON Web Token provided\"}").build();
            }

            //Send parameters to business layer and store response
            List<Step> steps = stepBusiness.getSteps(workflowID);

            //Construct response message
            StringBuilder responseMessage = new StringBuilder("[");
            for(Step step : steps){
                responseMessage.append(step.toJSON() + ",");
            }
            //remove trailing comma and add closing bracket
            responseMessage = new StringBuilder(responseMessage.substring(0,responseMessage.length() - 1) + "]");

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return Response.ok(responseMessage)
                    .build();
        }
        //Catch an InternalServerErrorException and return Internal Server Error response with standard message
        catch(InternalServerErrorException isee){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ERROR_STRING + "Sorry, could not process your request at this time.\"}").build();
        }
        //Catch All to ensure no unexpected internal server errors are being returned to client
        catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ERROR_STRING + "Sorry, an unexpected issue has occurred." + "\"}").build();
        }
    }

    /**
     * Insert a new step into the database
     * @param orderNumber - New step's orderNumber
     * @param isHighestLevel - New step's isHighestLevel truth value
     * @param description - New step's descriptions
     * @param relatedStep - New step's relatedStep
     * @param uuid - New step's UUID
     * @param verbID - New step's verbID
     * @param fileID - New step's fileID
     * @param workflowID - New step's workflowID
     * @param jwt - JSON Web Token for authorization; must be valid and not expired
     * @return - HTTP Response: 200 OK for step inserted successfully
     *                          400 BAD REQUEST for invalid parameters
     *                          401 UNAUTHORIZED for invalid JSON Web Token in header
     *                          403 CONFLICT for parameter conflict
     *                          404 NOT FOUND for non-existent step
     *                          500 INTERNAL SERVER ERROR for backend error
     */
    @POST
    @Operation(summary = "insertStep", description = "Insert a new step.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Step object which contains keys (stepID, orderNumber, isHighestLevel, UUID, verbID, fileID, workflowID)"),
            @ApiResponse(code = 400, message = "{error: A step UUID, verbID, fileID and workflowID must be provided.} (invalid parameters provided)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: A step with that ID already exists.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCompany(@RequestBody(description = "orderNumber", required = true) @FormParam("orderNumber") String orderNumber,
                                  @RequestBody(description = "isHighestLevel", required = true) @FormParam("isHighestLevel") String isHighestLevel,
                                  @RequestBody(description = "description", required = false) @FormParam("description") String description,
                                  @RequestBody(description = "relatedStep", required = false) @FormParam("relatedStep") String relatedStep,
                                  @RequestBody(description = "UUID", required = true) @FormParam("UUID") String uuid,
                                  @RequestBody(description = "verbID", required = true) @FormParam("verbID") String verbID,
                                  @RequestBody(description = "fileID", required = true) @FormParam("fileID") String fileID,
                                  @RequestBody(description = "workflowID", required = true) @FormParam("workflowID") String workflowID,
                                  @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            if(Boolean.TRUE.equals(JWTUtility.validateToken(jwt))){
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid JSON Web Token provided\"}").build();
            }

            //Send parameters to business layer and store response
            Step step = stepBusiness.insertStep(orderNumber, isHighestLevel, description, relatedStep, uuid, verbID, fileID, workflowID);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return Response.ok(step.toJSON())
                    .build();
        }
        //Catch all business logic related errors and return relevant response with message from error
        catch(ConflictException | NotFoundException | BadRequestException e) {
            return Response.status(Response.Status.CONFLICT).entity(ERROR_STRING + e.getMessage() + "\"}").build();
        }
        //Catch an InternalServerErrorException and return Internal Server Error response with standard message
        catch(InternalServerErrorException isee){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ERROR_STRING + "Sorry, could not process your request at this time.\"}").build();
        }
        //Catch All to ensure no unexpected internal server errors are being returned to client
        catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ERROR_STRING + "Sorry, an unexpected issue has occurred." + "\"}").build();
        }
    }
}
