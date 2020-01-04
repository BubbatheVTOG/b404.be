package main.java.b404.business;

import main.java.b404.utility.BadRequestException;

/**
 * Business layer for functionality related to person
 * Includes login as well as business operations for people
 */
public class PersonBusiness {

    public String[] login(String user, String password) throws BadRequestException{
        if(user == "" || user == null){ throw new BadRequestException("Invalid username"); }
        if(password == "" || password == null){ throw new BadRequestException("Invalid password"); }


        /*
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String jws = Jwts.builder().setSubject("Joe").signWith(SignatureAlgorithm.HS256, key).compact();

        assert Jwts.parser().setSigningKey(key).parseClaimsJws(jws).getBody().getSubject().equals("Joe");
        */

        return null;
    }
}
