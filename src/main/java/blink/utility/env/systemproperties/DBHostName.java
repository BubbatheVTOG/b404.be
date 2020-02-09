package blink.utility.env.systemproperties;

public class DBHostName implements EnvironmentProperty {

    private final String key = "DB_NAME";
    private String value = "db";

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String setValue(String propertyName) {
        this.value = propertyName;
        return propertyName;
    }

    @Override
    public void getSystemValue() {
        String tempVal = System.getenv(key);
        if (tempVal != null) {
            if (tempVal.length() > 0) {
                value = tempVal;
            }
        }
    }
}
