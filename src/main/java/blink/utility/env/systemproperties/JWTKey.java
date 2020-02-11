package blink.utility.env.systemproperties;

import blink.utility.env.EnvKeyValues;

import java.security.SecureRandom;

public class JWTKey implements EnvironmentProperty {

    private static final String KEY = EnvKeyValues.JWT_KEY;
    private static final int KEY_LENGTH = 64;
    private String value;

    public JWTKey() {
        this.getSystemValue();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
        if (this.value == null) {
            this.value = this.createRandomString(KEY_LENGTH);
        }
        return this.value;
    }

    @Override
    public String setValue(String propertyName) {
        this.value = propertyName;
        return this.value;
    }

    @Override
    public void getSystemValue() {
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
