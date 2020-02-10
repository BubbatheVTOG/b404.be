package b404.utility.env.systemproperties;

public class DBUserPass implements EnvironmentProperty {

    private final String key = "DB_USER_PASSWD";
    private String value = "b404";

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
        return this.value;
    }

    @Override
    public void getSystemValue() {
        String tempVal = System.getenv(key);
        if (tempVal != null) {
            if (tempVal.length() > 0) {
                this.value = tempVal;
            }
        }
    }
}
