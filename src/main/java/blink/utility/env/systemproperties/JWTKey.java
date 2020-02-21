package blink.utility.env.systemproperties;

import blink.utility.env.EnvKeyValues;

import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JWTKey implements EnvironmentProperty {

    private static final String KEY = EnvKeyValues.JWT_KEY;
    private static final int KEY_LENGTH = 64;
    private String value;
    private boolean valueFromEnvironment = false;

    public JWTKey() {
        this.getValueFromSystem();
        Logger logger = Logger.getLogger(this.getClass().getName());
        if (this.valueFromEnvironment) {
            logger.log(Level.INFO,"JWT_KEY determined to be <REDACTED> from the default configuration.");
        } else {
            logger.log(Level.INFO,"JWT_KEY determined to be <REDACTED> from the environment.");
        }
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        if (this.value == null) {
            this.value = this.createRandomString(KEY_LENGTH);
            this.valueFromEnvironment = true;
        }
        return this.value;
    }

    @Override
    public String setValue(String propertyName) {
        this.value = propertyName;
        return this.value;
    }

    @Override
    public void getValueFromSystem() {
        String tempVal = System.getenv(KEY);
        if (tempVal != null) {
            if (tempVal.length() > 0) {
                this.value = tempVal;
            }
        }
    }

    /**
     * Generates a hex string for the length specified.
     * @param length The length of the desired hex string.
     * @return A hex string.
     */
    private String createRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(Integer.toHexString(random.nextInt()));
        }
        return sb.toString().toUpperCase();
    }
}
