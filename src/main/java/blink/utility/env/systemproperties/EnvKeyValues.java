package blink.utility.env.systemproperties;

import blink.utility.env.EnvManager;

/**
 * An interface for providing key values for the EnvManager.
 */
public final class EnvKeyValues {

    // Empty constructor to hide the implicit one.
    private EnvKeyValues() {}

    // Database key values.
    public static final String DB_HOSTNAME = "DB_HOSTNAME";
    public static final String DB_USER_NAME = "DB_USER_NAME";
    public static final String DB_USER_PASS = "DB_USER_PASSWD";

    // JWT key values.
    public static final String JWT_KEY = "JWT_KEY";
    public static final String JWT_ISSUER = "JWT_ISSUER";
    public static final String JWT_EXPIRE_TIME = "JWT_EXPIRE_TIME";
}
