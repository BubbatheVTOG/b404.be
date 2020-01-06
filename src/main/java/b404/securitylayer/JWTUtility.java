package main.java.b404.securitylayer;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for constructing, decoding and parsing JSON Web Tokens
 */
public class JWTUtility {
    private String SECRET_KEY;
    private String issuer;
    private int JWT_TOKEN_VALIDITY_DURATION;

    public JWTUtility() {
        //TODO: insert secret key into properties file along with db connection information
        //Secret key for creating the JWT
        this.SECRET_KEY = "7BA7D93955DCF9B08CDAED92CDF38F659EDFDECD933CF0EAFC0DD3D35F503FCC";

        //Issuer for all JWTs will be venture_creations
        this.issuer = "venture_creations";

        //Tokens will be valid for 5 minutes
        this.JWT_TOKEN_VALIDITY_DURATION = 5 * 60 * 60;
    }

    private String generateToken(String userID){
        Map<String, Object> claims = new HashMap<>();
        return constructToken(claims, userID);
    }

    private String constructToken(Map<String, Object> claims, String subject) {
        Date currDate = new Date(System.currentTimeMillis());
        Date expirationDate = new Date(System.currentTimeMillis() + this.JWT_TOKEN_VALIDITY_DURATION * 1000);

        return Jwts.builder().setClaims(claims)
                             .setSubject(subject)
                             .setIssuer(this.issuer)
                             .setIssuedAt(currDate)
                             .setExpiration(expirationDate)
                             .signWith(SignatureAlgorithm.HS512, this.SECRET_KEY)
                             .compact();
    }

}
