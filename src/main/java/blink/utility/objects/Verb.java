package blink.utility.objects;

public class Verb {
    private int verbID;
    private String name;
    private String description;

    public Verb(int verbID, String name, String description) {
        this.verbID = verbID;
        this.name = name;
        this.description = description;
    }

    public Verb(int verbID) { this.verbID = verbID; }

    public Verb(String name) { this.name = name; }

    public int getVerbID() { return verbID; }

    public void setVerbID(int verbID) { this.verbID = verbID; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
}