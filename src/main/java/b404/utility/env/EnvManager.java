package b404.utility.env;

import b404.utility.env.systemproperties.DBHostName;
import b404.utility.env.systemproperties.EnvironmentProperty;

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
        // Environment Properties here.
        EnvironmentProperty dbName = new DBHostName();

        // Add properties to the map.
        envProps.put(dbName.getKey(), dbName);
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
