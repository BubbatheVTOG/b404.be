package b404.utility.objects;

/*
import java.sql.*;
import java.util.*;
import b404.datalayer.PersonDB;
*/

/**
 * Utility class for representing a Person interacting with this system
 */
public class Person {

	private String UUID;
	private String name;
	private String email;
	private String passwordHash;
	private String salt;
	private String title;
	private int companyID;
	private String companyName;
	private int accessLevelID;
	private String accessLevelName;

	//Fully parameterised constructor
	public Person(String UUID, String name, String passwordHash, String salt, String email, String title, int companyID, int accessLevelID) {
		this.UUID = UUID;
		this.name = name;
		this.passwordHash = passwordHash;
		this.salt = salt;
		this.email = email;
		this.title = title;
		this.companyID = companyID;
		this.accessLevelID = accessLevelID;
	}

	public String getUUID() {
		return this.UUID;
	}

	public String getName() {
		return this.name;
	}

	public String getEmail() {
		return this.email;
	}

	public String getPasswordHash() {
		return this.passwordHash;
	}

	public String getSalt() {
		return this.salt;
	}

	public String getTitle() {
		return this.title;
	}

	public int getCompanyID() {
		return this.companyID;
	}

	public int getAccessLevelID() {
		return this.accessLevelID;
	}

	public void setUUID(String UUID) {
		this.UUID = UUID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setAccessLevelID(int accessLevelID) {
		this.accessLevelID = accessLevelID;
	}

	public void setAccessLevelName(String accessLevelName) {
		this.accessLevelName = accessLevelName;
	}

	/**
	 * Puts the Person object into valid json format
	 * @return JSON format String
	 */
	public String toJSON() {
		return "{" +
				"\"UUID\": \"" + this.UUID + "\"," +
				"\"username\": \"" + this.name + "\"," +
				"\"passwordHash\": \"" + this.passwordHash + "\"," +
				"\"salt\": \"" + this.salt + "\"," +
				"\"email\": \"" + this.email + "\"," +
				"\"title\": \"" + this.title + "\"," +
				"\"companyID\": " + this.companyID + "," +
				"\"companyName\": \"" + this.companyName + "," + "\"" +
				"\"accessLevelID\": " + this.accessLevelID +
				"\"accessLevelName\": \"" + this.accessLevelName + "\"" +
				"}";
	}

	/**
	 * Puts the Person object into valid json format and removes passwordHash and salt
	 * @return JSON format String
	 */
	public String toSecureJSON() {
		return "{" +
				"\"UUID\": \"" + this.UUID + "\"," +
				"\"username\": \"" + this.name + "\"," +
				"\"email\": \"" + this.email + "\"," +
				"\"title\": \"" + this.title + "\"," +
				"\"companyID\": " + this.companyID + "," +
				"\"accessLevelID\": " + this.accessLevelID +
				"}";
		//TODO: discuss if company and accesslevelID are sufficient in return
		/*
		return "{" +
				"\"UUID\": \"" + this.UUID + "\"," +
				"\"username\": \"" + this.name + "\"," +
				"\"email\": \"" + this.email + "\"," +
				"\"title\": \"" + this.title + "\"," +
				"\"companyName\": \"" + this.companyName +"\"," +
				"\"accessLevelName\": \"" + this.accessLevelName + "\"," +
				"}";
		*/
	}

	/*
	public boolean fetch() {
		PersonDB api = new PersonDB();
		String query = "SELECT name, email, passwordHash, salt, title, companyID, accessLevelID FROM Person WHERE UUID LIKE ?";
		ArrayList<String> values = new ArrayList<String>();
		values.add(UUID);
		api.connect();
		ArrayList<ArrayList<String>> results = api.getData(query, values);
		api.close();
		if(!results.isEmpty()) {
			setName(results.get(0).get(0));
			setEmail(results.get(0).get(1));
			setPasswordHash(results.get(0).get(2));
			setSalt(results.get(0).get(3));
			setTitle(results.get(0).get(4));
			setCompanyID(results.get(0).get(5));
			setAccessLevelID(results.get(0).get(6));
			return true;
		}
		else {
			return false;
		}
	}
	public int put() {
		DatabaseAPI api = new DatabaseAPI();
		String query = "UPDATE Person SET "
			+ "name = ?, "

			+ "email = ?, "

			+ "passwordHash = ?, "

			+ "salt = ?, "

			+ "title = ?, "

			+ "companyID = ?, "

			+ "accessLevelID = ? "
			+ "WHERE UUID LIKE ?;";
		ArrayList<String> values = new ArrayList<String>();
		values.add(name);
		values.add(email);
		values.add(passwordHash);
		values.add(salt);
		values.add(title);
		values.add(companyID);
		values.add(accessLevelID);
		values.add(UUID);
		int rc = 0;
		try {
			rc = api.setData(query, values);
		} catch (NullPointerException NPE) {
			NPE.printStackTrace();
		}
		return rc;
	}
	public int post() {
		DatabaseAPI api = new DatabaseAPI();
		String query = "INSERT INTO Person VALUES ("
			+ "?, "
			+ "?, "
			+ "?, "
			+ "?, "
			+ "?, "
			+ "?, "
			+ "?, "
			+ "?);";
		ArrayList<String> values = new ArrayList<String> ();
		values.add(UUID);
		values.add(name);
		values.add(email);
		values.add(passwordHash);
		values.add(salt);
		values.add(title);
		values.add(companyID);
		values.add(accessLevelID);
		int rc = api.setData(query, values);
		return rc;
	}
	public int delete() {
		DatabaseAPI api = new DatabaseAPI();
		String query = "DELETE FROM Person WHERE UUID LIKE ?;";
		ArrayList<String> values = new ArrayList<String>();
		values.add(UUID);
		int rc = api.setData(query, values);
		return rc;
	}
	*/

} // End of class Person