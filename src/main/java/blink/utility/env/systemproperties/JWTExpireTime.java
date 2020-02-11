package blink.utility.env.systemproperties;

public class JWTExpireTime implements EnvironmentProperty {

    // 8 Hours
    private String value = Integer.toString(8 * 60 * 60 * 1000);
    private static final String KEY = EnvKeyValues.JWT_EXPIRE_TIME;

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

        try {
            Integer.parseInt(tempVal);
        // This cannot return an invalid number. We need to validate when querying the environment.
        } catch (NumberFormatException nfe) {
            tempVal = null;
        }

        if (tempVal != null) {
            if (tempVal.length() > 0) {
                this.value = tempVal;
            }
        }
    }
}
