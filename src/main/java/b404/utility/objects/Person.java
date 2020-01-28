package b404.utility.objects;
import com.google.gson.Gson;
import java.util.ArrayList;

/**
 * Utility class for representing a Person interacting with this system
 */
/**
 * Utility class for representing a Person interacting with this system
 */
public class Person {
	private transient Gson gson = new Gson();

	private String UUID;
	private String username;
	private transient String passwordHash;
	private transient String salt;
	private String fName;
	private String lName;
	private String email;
	private String title;
	private ArrayList<Company> companies;
	private int accessLevelID;

	//Fully parameterised constructor
	public Person(String UUID, String username, String passwordHash, String salt, String fName, String lName, String email, String title, int accessLevelID) {
		this.UUID = UUID;
		this.username = username;
		this.passwordHash = passwordHash;
		this.salt = salt;
		this.fName = fName;
		this.lName = lName;
		this.email = email;
		this.title = title;
		this.accessLevelID = accessLevelID;
	}

	public String getUUID() {
		return this.UUID;
	}

	public String getPasswordHash() {
		return this.passwordHash;
	}

	public String getSalt() {
		return this.salt;
	}

	public String getFName() { return this.fName; }

	public String getLName() { return this.lName; }

	public String getUsername() { return this.username; }

	public String getEmail() { return this.email; }

	public String getTitle() {
		return this.title;
	}

	public ArrayList<Company> getCompanies() {return companies;}

	public int getAccessLevelID() {
		return this.accessLevelID;
	}

	public void setUUID(String UUID) {
		this.UUID = UUID;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public void setFName(String fName) { this.fName = fName; }

	public void setLName(String lName) { this.lName = lName; }

	public void setUsername(String username) { this.username = username; }

	public void setEmail(String email) { this.email = email; }

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCompanies(ArrayList<Company> companies) { this.companies = companies; }

	public void setAccessLevelID(int accessLevelID) { this.accessLevelID = accessLevelID; }

	/**
	 * Puts the Person object into valid json format and removes passwordHash and salt
	 * @return JSON format String
	 */
	public String toSecureJSON() {
		return gson.toJson(this);
	}
}