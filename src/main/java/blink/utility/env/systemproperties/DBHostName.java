package blink.utility.env.systemproperties;

public class DBHostName implements EnvironmentProperty {

    private static final String KEY = EnvKeyValues.DB_HOSTNAME;
    private String value = "db";

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
    public void getSystemValue() {
        String tempVal = System.getenv(KEY);
        if (tempVal != null) {
            if (tempVal.length() > 0) {
                this.value = tempVal;
            }
        }
    }
}
