package main.java.b404.service;

@Path("login")
public class Login {
    private UserBusiness userBusiness = new UserBusiness();

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response submit(@FormParam("user") String user, @FormParam("password") String password) {
        String[] result = userBusiness.login(user, password);

        return Response.ok("{\"success\":\"You have logged in!\"}").build();

    }
}
