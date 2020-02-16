package blink.servicelayer;

import blink.businesslayer.Authorization;
import blink.businesslayer.MilestoneBusiness;
import blink.utility.objects.Milestone;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

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
     * 401 UNAUTHORIZED for invalid JSON Web Token in header
     * 500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/all")
    @GET
    @Operation(summary = "getAllMilestones", description = "Gets a user's information by UUID")
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
}
