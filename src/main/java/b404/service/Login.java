package main.java.b404.service;

import main.java.b404.business.PersonBusiness;

@Path("login")
public class Login {
    private PersonBusiness userBusiness = new PersonBusiness();

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response submit(@FormParam("user") String user, @FormParam("password") String password) {
        String[] result = userBusiness.login(user, password);

        return Response.ok("{\"success\":\"You have logged in!\"}").build();

    }
}
