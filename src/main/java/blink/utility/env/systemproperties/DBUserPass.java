package blink.utility.env.systemproperties;

import blink.utility.env.EnvKeyValues;

import java.util.logging.Logger;

public class DBUserPass implements EnvironmentProperty {

    private static final String KEY = EnvKeyValues.DB_USER_PASS;
    private String value = "b404";
    private boolean valueFromEnvironment = false;

    public DBUserPass() {
        this.getValueFromSystem();
        Logger logger = Logger.getLogger(this.getClass().getName());
        if (this.valueFromEnvironment) {
            logger.info("DB_USER_PASS determined to be <REDACTED> from the environment.");
        } else {
            logger.info("DB_USER_PASS determined to be <REDACTED> from the default configuration.");
        }
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
                this.valueFromEnvironment = true;
                this.value = tempVal;
            }
        }
    }
}
