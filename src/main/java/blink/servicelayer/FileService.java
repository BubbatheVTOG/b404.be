package blink.servicelayer;

import blink.businesslayer.FileBusiness;
import blink.businesslayer.Authorization;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import blink.businesslayer.StepBusiness;
import blink.utility.objects.File;
import blink.utility.objects.Step;
import blink.utility.security.JWTUtility;
import com.google.gson.*;
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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Service Layer entity responsible for receiving requests having to do with file information
 */
@Path("file")
@Api(value = "/file")
public class FileService {
    private FileBusiness fileBusiness = new FileBusiness();
    private Gson gson = new GsonBuilder().setDateFormat("MMM d, yyy HH:mm:ss").serializeNulls().create();

    /**
     * Get file from the database
     * @Param jwt JSON web token for authorization
     * @return HTTP Response: 200 OK for file returned
     *                        401 UNAUTHORIZED for invalid JSON Web Token in header
     *                        500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/id/{id}")
    @GET
    @Operation(summary = "getFile", description = "Gets a file in base64 format")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File object containing fileID, name, file, confidential and stepID"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFile(@Parameter(in = ParameterIn.PATH, description = "id", required = true) @PathParam("id") String fileID,
                            @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            File file = fileBusiness.getFile(fileID, JWTUtility.getUUIDFromToken(jwt));

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(file));
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
     * Construct archive from files retrieved by milestoneID
     * @Param jwt JSON web token for authorization
     * @return HTTP Response: 200 OK for file returned
     *                        401 UNAUTHORIZED for invalid JSON Web Token in header
     *                        500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/archive/{milestoneID}")
    @GET
    @Operation(summary = "getArchive", description = "Gets all files from the specified milestone in zip format")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File object containing the file name and archive"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArchive(@Parameter(in = ParameterIn.PATH, description = "milestoneID", required = true) @PathParam("milestoneID") String milestoneID,
                            @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            File file = fileBusiness.zipAllFilesByMilestone(milestoneID, JWTUtility.getUUIDFromToken(jwt));

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(file));
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
     * Get all concrete files from the database
     * @Param jwt JSON web token for authorization
     * @return HTTP Response: 200 OK for file returned
     *                        401 UNAUTHORIZED for invalid JSON Web Token in header
     *                        500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/concrete")
    @GET
    @Operation(summary = "getAllConcreteFiles", description = "Gets all concrete files relevant to the user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of File objects containing fileID, name, file, confidential and stepID"),
            @ApiResponse(code = 400, message = "{error: No user with that userID exists.}"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllConcreteFiles(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<File> files = fileBusiness.getAllConcreteFiles(JWTUtility.getUUIDFromToken(jwt));

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(files));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch(BadRequestException bre){
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Get all template files from the database
     * @Param jwt JSON web token for authorization
     * @return HTTP Response: 200 OK for file returned
     *                        401 UNAUTHORIZED for invalid JSON Web Token in header
     *                        500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/template")
    @GET
    @Operation(summary = "getAllTemplateFiles", description = "Gets all template files")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File object containing fileID, name, file, confidential and stepID"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTemplateFiles(@Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<File> files = fileBusiness.getAllTemplateFiles(JWTUtility.getUUIDFromToken(jwt));

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(files));
        }
        //Catch error exceptions and return relevant Response using ResponseBuilder
        catch(BadRequestException bre){
            return ResponseBuilder.buildErrorResponse(Response.Status.BAD_REQUEST, bre.getMessage());
        }
        catch(NotAuthorizedException nae){
            return ResponseBuilder.buildErrorResponse(Response.Status.UNAUTHORIZED, nae.getMessage());
        }
        catch(Exception e){
            return ResponseBuilder.buildInternalServerErrorResponse();
        }
    }

    /**
     * Get all files by milestoneID from the database
     * @Param jwt JSON web token for authorization
     * @return HTTP Response: 200 OK for file returned
     *                        401 UNAUTHORIZED for invalid JSON Web Token in header
     *                        500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/milestone/{milestoneID}")
    @GET
    @Operation(summary = "getAllFilesByMilestone", description = "Gets all files by milestoneID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File object containing fileID, name, file, confidential and stepID"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFilesByMilestone(@Parameter(in = ParameterIn.PATH, description = "milestoneID", required = true) @PathParam("milestoneID") String milestoneID,
                            @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<File> files = fileBusiness.getAllFilesByMilestone(milestoneID, JWTUtility.getUUIDFromToken(jwt));

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(files));
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
     * Get all files by companyID from the database
     * @Param jwt JSON web token for authorization
     * @return HTTP Response: 200 OK for file returned
     *                        401 UNAUTHORIZED for invalid JSON Web Token in header
     *                        500 INTERNAL SERVER ERROR for backend error
     */
    @Path("/company/{companyID}")
    @GET
    @Operation(summary = "getAllFilesByCompany", description = "Gets all files by companyID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File object containing fileID, name, file, confidential and stepID"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllFilesByCompany(@Parameter(in = ParameterIn.PATH, description = "companyID", required = true) @PathParam("companyID") String companyID,
                                           @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            //Send parameters to business layer and store response
            List<File> files = fileBusiness.getAllFilesByCompany(companyID, JWTUtility.getUUIDFromToken(jwt));

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(files));
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
     * Insert a file into the database
     * @Param file json object containing name, file and confidential
     * @Param jwt JSON web token for authorization
     * @return HTTP Response: 200 OK for successfully inserting a file
     *                        401 UNAUTHORIZED for invalid JSON Web Token in header
     *                        403 FORBIDDEN if requester does not have access to the endpoint
     *                        500 INTERNAL SERVER ERROR for backend error
     */
    @POST
    @Operation(summary = "insertFile", description = "Insert a new file into the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File Object which contains name, file, confidential"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertFile(@RequestBody(description = "json object containing name, file, confidential", required = true) String json,
                               @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {
        try {
            Authorization.isLoggedIn(jwt);

            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

            File file = fileBusiness.insertFile(jsonObject);

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(file));
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

    @PUT
    @Operation(summary = "updateFile", description = "Update an existing file")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated a file object with name, file, and confidential"),
            @ApiResponse(code = 400, message = "{error: specific error message.} (invalid parameters provided)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateFile(@RequestBody(description = "json object containing name, file, stepID(optional), confidential", required = true) String json,
                               @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            Authorization.isLoggedIn(jwt);

            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

            File file = fileBusiness.updateFile(jsonObject, JWTUtility.getUUIDFromToken(jwt));

            //If no errors are thrown in the business layer, it was successful and OK response can be sent with message
            return ResponseBuilder.buildSuccessResponse(gson.toJson(file));
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

    @Path("/{id}")
    @DELETE
    @Operation(summary = "deleteFile", description = "delete an existing file")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated a file object with name, file, and confidential"),
            @ApiResponse(code = 400, message = "{error: specific error message.} (invalid parameters provided)"),
            @ApiResponse(code = 401, message = "{error: Invalid JSON Web Token provided.}"),
            @ApiResponse(code = 403, message = "{error: You do not have access to that request.}"),
            @ApiResponse(code = 500, message = "{error: Sorry, cannot process your request at this time.}")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFile(@Parameter(in = ParameterIn.PATH, required = true) @PathParam("id") String fileID,
                               @Parameter(in = ParameterIn.HEADER, name = "Authorization") @HeaderParam("Authorization") String jwt) {

        try {
            Authorization.isLoggedIn(jwt);

            int numDeletedFiles = fileBusiness.deleteFile(fileID, JWTUtility.getUUIDFromToken(jwt));

            if(numDeletedFiles > 0) {
                return ResponseBuilder.buildSuccessResponse(gson.toJson("Successfully deleted " + numDeletedFiles + " files."));
            } else {
                return ResponseBuilder.buildErrorResponse(Response.Status.NOT_FOUND, gson.toJson("Could not find any files for deletion."));
            }
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