package blink.utility.env.systemproperties;

import blink.utility.env.EnvKeyValues;

import java.util.logging.Logger;

public class JWTIssuer implements EnvironmentProperty {

    private static final String KEY = EnvKeyValues.JWT_ISSUER;
    private String value = "venture_creations";

    public JWTIssuer(){
        this.getValueFromSystem();
        Logger logger = Logger.getLogger(this.getClass().getName());
        String msg = String.format("JWT_ISSUER determined to be %s", this.value);
        logger.info(msg);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public String getValue() {
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
}
