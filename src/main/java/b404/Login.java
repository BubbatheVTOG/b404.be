package b404;

import javax.crypto.SecretKey;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

// https://github.com/jwtk/jjwt
// https://dennis-xlc.gitbooks.io/restful-java-with-jax-rs-2-0-2rd-edition/en/part1/chapter15/securing_jax_rs.html
@Path("login")
public class Login {


    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public void submit(@FormParam("value") String value) {

        /*
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String jws = Jwts.builder().setSubject("Joe").signWith(SignatureAlgorithm.HS256, key).compact();

        assert Jwts.parser().setSigningKey(key).parseClaimsJws(jws).getBody().getSubject().equals("Joe");
        */

        return Response.ok("{\"success\":\"You have logged in!\"}").build();

    }

}
