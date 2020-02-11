package blink.utility.security;

import blink.utility.env.EnvManager;
import blink.utility.env.systemproperties.EnvKeyValues;
import io.jsonwebtoken.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for constructing, decoding and parsing JSON Web Tokens
 */
public class JWTUtility {

    private static final EnvManager ENV_MANAGER = new EnvManager();
    private static final String SECRET_KEY = ENV_MANAGER.getValue(EnvKeyValues.JWT_KEY);
    private static final String ISSUER = ENV_MANAGER.getValue(EnvKeyValues.JWT_ISSUER);
    private static final int JWT_TOKEN_VALIDITY_DURATION = Integer.parseInt(ENV_MANAGER.getValue(EnvKeyValues.JWT_EXPIRE_TIME));

    //Empty constructor because this class is meant to be used statically
    private JWTUtility() {}

    //Externally accessible function for creating a JWT using a UUID
    public static String generateToken(final String UUID){
        Map<String, Object> claims = new HashMap<>();
        return constructToken(claims, UUID);
    }

    private static String constructToken(Map<String, Object> claims, final String subject) {
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
    private static Claims getAllClaimsFromToken(final String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    //Get UUID from token
    public static String getUUIDFromToken(final String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    //Get Expiration date from token
    public static Date getExpirationDateFromToken(final String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    //Check if the token has expired
    private static Boolean isTokenExpired(final String token) {
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
    public static Boolean validateToken(final String token) {
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