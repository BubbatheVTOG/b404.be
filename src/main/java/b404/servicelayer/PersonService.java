package b404.servicelayer;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

// https://dennis-xlc.gitbooks.io/restful-java-with-jax-rs-2-0-2rd-edition/en/part1/chapter15/securing_jax_rs.html

/**
 * Service Layer entity responsible for receiving requests having to do with person information
 */
@Path("person")
public class PersonService {

    //TODO: this is web token code Bubba had added which is not currently being used but is maintained here for future reference
        /*
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String jws = Jwts.builder().setSubject("Joe").signWith(SignatureAlgorithm.HS256, key).compact();

        assert Jwts.parser().setSigningKey(key).parseClaimsJws(jws).getBody().getSubject().equals("Joe");
        */

}