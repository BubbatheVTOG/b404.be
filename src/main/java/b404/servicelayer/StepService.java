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
import java.util.ArrayList;

@Path("step")
@Api(value = "/step")
public class StepService {
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
    public Response getSteps(@Parameter(in = ParameterIn.HEADER, description = "workflowID", required = true) @PathParam("id") String workflowID,
                             @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String JWT) {
        try {
            if(!JWTUtility.validateToken(JWT )){
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid JSON Web Token provided\"}").build();
            }

            //Send parameters to business layer and store response
            ArrayList<Step> steps = stepBusiness.getSteps(workflowID);

            //Construct response message
            String responseMessage = "[";
            for(Step step : steps){
                responseMessage += step.toJSON() + ",";
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
}
