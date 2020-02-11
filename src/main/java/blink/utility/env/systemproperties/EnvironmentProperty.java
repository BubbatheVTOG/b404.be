package blink.utility.env.systemproperties;

import java.util.Map;

public interface EnvironmentProperty extends Map.Entry<String,String> {

    String getKey();
    String getValue();
    String setValue(String propertyName);
    void getValueFromSystem();

}
