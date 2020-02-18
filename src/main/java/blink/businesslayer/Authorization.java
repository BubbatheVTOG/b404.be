package blink.businesslayer;

import blink.utility.objects.Person;
import blink.utility.security.JWTUtility;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import java.util.Arrays;
import java.util.List;

public class Authorization {
    private static PersonBusiness personBusiness = new PersonBusiness();
    //TODO discuss the access levels of internal and external users
    static final List<Integer> INTERNAL_USER_LEVELS = Arrays.asList(1,2,3);
    static final List<Integer> EXTERNAL_USER_LEVELS = Arrays.asList(4,5,6);

    private Authorization(){
        //this is not used as this class is meant to be used as a static utility class
    }

    /**
     * Checks that a user is logged in properly by validating the JSON Web Token
     * @param jwt - JSON Web token
     * @throws NotAuthorizedException - JWT is either invalid or expired
     */
    public static void isLoggedIn(String jwt) throws NotAuthorizedException{
        //Check that JSON Web Token is valid
        if(Boolean.FALSE.equals(JWTUtility.validateToken(jwt))){
            throw new NotAuthorizedException("Invalid JSON Web Token provided.");
        }
    }

    /**
     * Checks that a user is logged in and is and administrator
     * @param jwt - JSON Web Token
     * @throws NotAuthorizedException - User provided invalid or expired JWT
     * @throws ForbiddenException - User is not an administrator
     */
    public static void isAdmin(String jwt) throws NotAuthorizedException, ForbiddenException {
        isLoggedIn(jwt);

        //Check that this user has the authority to access this endpoint
        Person requester = personBusiness.getPersonByUUID(JWTUtility.getUUIDFromToken(jwt));
        if(requester.getAccessLevelID() != 1){
            throw new ForbiddenException("You do not have access to this functionality");
        }
    }

    /**
     * Checks that a user is logged in and is either an administrator or changing their own information
     * @param jwt - JSON Web Token
     * @param targetUUID - The UUID of person information that is trying to be altered
     * @throws NotAuthorizedException - User provided invalid or expired JWT
     * @throws ForbiddenException - User is not an administrator or the target person
     */
    public static void isAdminOrSelf(String jwt, String targetUUID) throws NotAuthorizedException, ForbiddenException {
        isLoggedIn(jwt);

        //Check if the requester is trying alter their own information

        //Check that this user has the authority to access this endpoint
        Person requester = personBusiness.getPersonByUUID(JWTUtility.getUUIDFromToken(jwt));

        if(!requester.getUUID().equals(targetUUID) && requester.getAccessLevelID() != 1) {
            throw new ForbiddenException("You do not have access to this functionality");
        }
    }
}
