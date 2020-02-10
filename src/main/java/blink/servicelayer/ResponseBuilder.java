package blink.servicelayer;

import com.google.gson.JsonObject;
import javax.ws.rs.core.Response;

/**
 * Class intended to consolidate logic for building response objects in the service layer
 */
class ResponseBuilder {
    static final String FORBIDDEN_MESSAGE = "You do not have access to that request.";

    /**
     * default constructor
     */
    private ResponseBuilder(){
        //Do nothing as this is meant to be used as a static class
    }

    static Response buildSuccessResponse(String message, String jwt){
        return Response.ok(message)
                        .header("Authorization", jwt)
                        .build();
    }

    static Response buildSuccessResponse(String message){
        return Response.ok(message)
                        .build();
    }

    /**
     * Build an error response with a custom status and message
     * @param status - HTTP status from JAX-RS Response
     * @param message - message to append to HTTP response
     * @return Response object containing custom status and message
     */
    static Response buildErrorResponse(Response.Status status, String message){
        JsonObject json = new JsonObject();
        json.addProperty("error", message);
        return Response.status(status)
                        .entity(json.toString())
                        .build();
    }

    /**
     * Build a response for an internal server error
     * @return Response object containing Internal Server Error status and preset message
     */
    static Response buildInternalServerErrorResponse(){
        JsonObject json = new JsonObject();
        json.addProperty("error", "Sorry, could not process your request at this time.");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(json.toString())
                        .build();
    }

    /**
     * Build a response for an internal server error
     * Allows passing in of custom string
     * @return Response object containing Internal Server Error status and custom message
     */
    static Response buildInternalServerErrorResponse(String message){
        JsonObject json = new JsonObject();
        json.addProperty("error", message);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(json.toString())
                .build();
    }
}
