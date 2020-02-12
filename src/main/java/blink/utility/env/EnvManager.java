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
        }
    }

    /**
     * Fill the map with known good kv pairs of our environment properties.
     */
    private void fillWithProperties() {
        // Add properties to the map.
        envProps.put(EnvKeyValues.DB_HOSTNAME, new DBHostName());
        envProps.put(EnvKeyValues.DB_TABLE_NAME, new DBTableName());
        envProps.put(EnvKeyValues.DB_USER_NAME, new DBUserName());
        envProps.put(EnvKeyValues.DB_USER_PASS, new DBUserPass());
        envProps.put(EnvKeyValues.JWT_KEY, new JWTKey());
        envProps.put(EnvKeyValues.JWT_ISSUER, new JWTIssuer());
        envProps.put(EnvKeyValues.JWT_EXPIRE_DURATION, new JWTExpireTime());
    }

    /**
     * Returns the value of the supplied key.
     * @param key The environment variable key.
     * @return The associated value of the environment variable. Empty string if key is not found.
     */
    public String getValue(final String key){
        if (envProps.containsKey(key)) {
            return envProps.get(key).getValue();
        }

        return "";
    }

    /**
     * Returns the environmental property.
     * @param key The key name of the environmental property.
     * @return A property object. Returns null if key is not found.
     */
    public EnvironmentProperty getEnvProp(final String key) {
        return envProps.get(key);
    }
}
