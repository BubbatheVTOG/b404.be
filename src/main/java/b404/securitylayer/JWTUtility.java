package main.java.b404.securitylayer;

import io.jsonwebtoken.Claims;
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
        this.JWT_TOKEN_VALIDITY_DURATION = 5 * 60 * 60 * 1000;
    }

    //Externally accessible function for creating a JWT using a userID
    public String generateToken(String userID){
        Map<String, Object> claims = new HashMap<>();
        return this.constructToken(claims, userID);
    }

    private String constructToken(Map<String, Object> claims, String subject) {
        Date currDate = new Date(System.currentTimeMillis());
        Date expirationDate = new Date(System.currentTimeMillis() + this.JWT_TOKEN_VALIDITY_DURATION);

        return Jwts.builder().setClaims(claims)
                             .setSubject(subject)
                             .setIssuer(this.issuer)
                             .setIssuedAt(currDate)
                             .setExpiration(expirationDate)
                             //using HMAC-SHA256 for signature hashing algorithm
                             .signWith(SignatureAlgorithm.HS256, this.SECRET_KEY)
                             .compact();
    }


    //Retrieves all claims from token body
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(this.SECRET_KEY).parseClaimsJws(token).getBody();
    }

    //Get userID from token
    public String getUserIDFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    //Get Expiration date from token
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    //Check if the token has expired
    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        Date currDate = new Date();

        return expiration.before(currDate);
    }

    //Validate that token has not expired and is for desired userID
    public Boolean validateToken(String token, String userID) {
        String tokenUsername = getUserIDFromToken(token);
        return (tokenUsername.equals(userID) && !isTokenExpired(token));
    }

}
