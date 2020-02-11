package blink.utility.env.systemproperties;

import java.util.Map;

public interface EnvironmentProperty extends Map.Entry<String,String> {

    /**
     * Returns The key value for the entry.
     * @return The key value for the entry.
     */
    String getKey();

    /**
     * Returns the value from the entry.
     * @return The value from the entry.
     */
    String getValue();

    /**
     * Sets the value of the entry.
     * @param propertyName The value for the entry.
     * @return The value of the entry.
     */
    String setValue(String propertyName);

    /**
     * Mutates the object with the value from the system.
     */
    void getValueFromSystem();
}
