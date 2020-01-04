package main.java.b404.business;

public class PersonBusiness {
    Validator validator = new Validator();

    //TODO: decide how to return response type and response message
    public String[] login(String user, String password){
        try {
            Validator.validateString("username", user);
            Validator.validateString("password", password);
        }
        catch(IllegalArgumentException e){
            return null;
        }
        /*
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String jws = Jwts.builder().setSubject("Joe").signWith(SignatureAlgorithm.HS256, key).compact();

        assert Jwts.parser().setSigningKey(key).parseClaimsJws(jws).getBody().getSubject().equals("Joe");
        */

        return null;
    }
}
