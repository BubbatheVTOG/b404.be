package blink.utility.env;

import blink.utility.env.systemproperties.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for extracting environment variables from the operating environment.
 */
public class EnvManager {

    private static final Map<String, EnvironmentProperty> envProps = new HashMap<>();

    /**
     * Default constructor.
     */
    public EnvManager() {
        if (envProps.size() == 0) {
            this.fillWithProperties();
            this.getSystemEnvVars();
        }
    }

    /**
     * Fill the map with known good kv pairs of our environment properties.
     */
    private void fillWithProperties() {
        // Add properties to the map.
        envProps.put(EnvKeyValues.DB_HOSTNAME, new DBHostName());
        envProps.put(EnvKeyValues.DB_USER_NAME, new DBUserName());
        envProps.put(EnvKeyValues.DB_USER_PASS, new DBUserPass());
        envProps.put(EnvKeyValues.JWT_KEY, new JWTKey());
        envProps.put(EnvKeyValues.JWT_ISSUER, new JWTIssuer());
        envProps.put(EnvKeyValues.JWT_EXPIRE_TIME, new JWTExpireTime());
    }

    /**
     * If the operating environment contains a value, get it.
     */
    private void getSystemEnvVars() {
        envProps.forEach( (key,value) -> value.getSystemValue());
    }

    /**
     * Returns the value of the supplied key.
     * @param key The environment variable key.
     * @return The associated value of the environment variable. Empty string if key is not found.
     */
    public String getValue(String key){
        if (envProps.containsKey(key)) {
            return envProps.get(key).getValue();
        }

        return "";
    }
}
