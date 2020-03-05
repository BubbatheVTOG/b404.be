package blink.servicelayer;

import blink.businesslayer.Authorization;
import blink.businesslayer.VerbBusiness;
import blink.utility.objects.Verb;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
 * Service Layer entity responsible for receiving requests to retrieve verbs from the database
 */
@Path("verb")
@Api(value = "/verb")
public class VerbService {
    private VerbBusiness verbBusiness = new VerbBusiness();
    private Gson gson =  new GsonBuilder().serializeNulls().create();

    /**
     * Get all verbs from database
     * @param jwt JSON web token for authorization
     * @return HTTP Response: 200 OK for verbs returned
     *                         401 UNAUTHORIZED for invalid JSON Web Token in header
     *                         500 INTERNAL SERVER ERROR for backend error
     */
    @GET
    @Operation(summary = "getAllVerbs", description = "Gets all verbs")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of verb objects which each contain keys (verbID, name, description)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllVerbs(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<Verb> verbList = verbBusiness.getAllVerbs();

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(verbList));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }
}
