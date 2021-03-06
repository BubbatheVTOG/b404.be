package blink.utility.env.systemproperties;

import blink.utility.env.EnvKeyValues;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JWTExpirationDuration implements EnvironmentProperty {

    // ISO-8601
    // https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html#parse-java.lang.CharSequence-
    // 8hrs by default
    private static final String EIGHT_HOURS = "PT8H";
    private String value = Duration.parse(EIGHT_HOURS).toString();
    private static final String KEY = EnvKeyValues.JWT_EXPIRE_DURATION;

    public JWTExpirationDuration(){
        this.getValueFromSystem();
        Logger logger = Logger.getLogger(this.getClass().getName());
        String msg = String.format("JWT_EXPIRE_DURATION determined to be: %s", this.value);
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

        // Check to see if the systems environment has a value that we can parse.
        if (tempVal != null) {
            if (tempVal.length() > 0) {
                // This cannot return an invalid duration. We need to validate when querying the environment.
                try {
                    this.value = Duration.parse(tempVal).toString();
                } catch (Exception E) {
                    this.value = Duration.parse(EIGHT_HOURS).toString();
                }
            }
        }
    }
}
