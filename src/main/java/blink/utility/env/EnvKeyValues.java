package blink.utility.env;

import blink.utility.env.EnvManager;

/**
 * An interface for providing key values for the EnvManager.
 */
public final class EnvKeyValues {

    // Empty constructor to hide the implicit one.
    private EnvKeyValues() {}

    // Database environment key values.
    public static final String DB_HOSTNAME = "DB_HOSTNAME";
    public static final String DB_USER_NAME = "DB_USER_NAME";
    public static final String DB_USER_PASS = "DB_USER_PASSWD";
    public static final String DB_TABLE_NAME = "DB_TABLE_NAME";

    // JWT environment key values.
    public static final String JWT_KEY = "JWT_KEY";
    public static final String JWT_ISSUER = "JWT_ISSUER";
    public static final String JWT_EXPIRE_DURATION = "JWT_EXPIRE_DURATION";
}
