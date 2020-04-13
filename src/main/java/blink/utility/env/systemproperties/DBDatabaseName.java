package blink.utility.env.systemproperties;

import blink.utility.env.EnvKeyValues;

import java.util.logging.Logger;

public class DBDatabaseName implements EnvironmentProperty {

    private static final String KEY = EnvKeyValues.DB_DATABASE;
    private String value = "venture_creations";

    public DBDatabaseName() {
        this.getValueFromSystem();
        Logger logger = Logger.getLogger(this.getClass().getName());
        String msg = String.format("DB_DATABASE determined to be: %s",this.value);
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
