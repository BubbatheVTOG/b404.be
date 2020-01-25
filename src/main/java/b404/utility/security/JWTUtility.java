package b404.utility.security;

import io.jsonwebtoken.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for constructing, decoding and parsing JSON Web Tokens
 */
public class JWTUtility {
    //TODO: insert secret key into properties file along with db connection information
    private static final String SECRET_KEY= "7BA7D93955DCF9B08CDAED92CDF38F659EDFDECD933CF0EAFC0DD3D35F503FCC";
    private static final String ISSUER = "venture_creations";
    private static final int JWT_TOKEN_VALIDITY_DURATION = 8 * 60 * 60 * 1000;

    //Externally accessible function for creating a JWT using a UUID
    public static String generateToken(String UUID){
        Map<String, Object> claims = new HashMap<>();
        return constructToken(claims, UUID);
    }

    private static String constructToken(Map<String, Object> claims, String subject) {
        Date currDate = new Date(System.currentTimeMillis());
        Date expirationDate = new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY_DURATION);

        return Jwts.builder().setClaims(claims)
                .setSubject(subject)
                .setIssuer(ISSUER)
                .setIssuedAt(currDate)
                .setExpiration(expirationDate)
                //using HMAC-SHA256 for signature hashing algorithm
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    //Retrieves all claims from token body
    private static Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    //Get UUID from token
    public static String getUUIDFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    //Get Expiration date from token
    public static Date getExpirationDateFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    //Check if the token has expired
    private static Boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            Date currDate = new Date();

            return expiration.before(currDate);
        }
        catch(ExpiredJwtException ejwte){
            return true;
        }
    }

    //Validate that token has not expired
    public static Boolean validateToken(String token) {
        try {
            if(token == null || token.isEmpty()){
                return false;
            }
            return !isTokenExpired(token);
        }
        catch(MalformedJwtException mje){
            return false;
        }
    }
}