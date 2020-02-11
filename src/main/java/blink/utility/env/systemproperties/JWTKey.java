package blink.utility.env.systemproperties;

import blink.utility.env.EnvKeyValues;

import java.security.SecureRandom;

public class JWTKey implements EnvironmentProperty {

    private static final String KEY = EnvKeyValues.JWT_KEY;
    private static final int KEY_LENGTH = 64;
    private String value;

    /**
     * Default Constructor
     */
    public JWTKey() {
        this.getValueFromSystem();
    }

    /**
     * Returns the key value of this property.
     * @return The key value of this property.
     */
    @Override
    public String getKey() {
        return KEY;
    }

    /**
     * Returns the value of this property.
     * @return The value of this property.
     */
    @Override
    public String getValue() {
        if (this.value == null) {
            this.value = this.createRandomString(KEY_LENGTH);
        }
        return this.value;
    }

    /**
     * Sets the property value.
     * @param propertyName The value of the property.
     * @return The property value.
     */
    @Override
    public String setValue(String propertyName) {
        this.value = propertyName;
        return this.value;
    }

    /**
     * Get the value from the system.
     */
    @Override
    public void getValueFromSystem() {
        String tempVal = System.getenv(KEY);
        if (tempVal != null) {
            if (tempVal.length() > 0) {
                this.value = tempVal;
            }
        }
    }

    public String createRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(Integer.toHexString(random.nextInt()));
        }
        return sb.toString().toUpperCase();
    }
}
