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
	private String passwordHash;
	private String salt;
	private String fName;
	private String lName;
	private String userName;
	private String email;
	private String title;
	private int companyID;
	private int accessLevelID;

	//Fully parameterised constructor
	public Person(String UUID, String passwordHash, String salt, String fName, String lName, String userName, String email, String title, int companyID, int accessLevelID) {
		this.UUID = UUID;
		this.passwordHash = passwordHash;
		this.salt = salt;
		this.fName = fName;
		this.lName = lName;
		this.userName = userName;
		this.email = email;
		this.title = title;
		this.companyID = companyID;
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

	public String getFName() { return this.fName; };

	public String getLName() { return this.lName; };

	public String getUserName() { return this.userName; }

	public String getEmail() { return this.email; }

	public String getTitle() {
		return this.title;
	}

	public int getCompanyID() { return this.companyID; }

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

	public void setLName(String fName) { this.lName = lName; }

	public void setUserName(String userName) { this.userName = userName; }

	public void setEmail(String email) { this.email = email; }

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public void setAccessLevelID(int accessLevelID) {
		this.accessLevelID = accessLevelID;
	}

	/**
	 * Puts the Person object into valid json format
	 * @return JSON format String
	 */
	public String toJSON() {
		return "{" +
				"\"UUID\": " + this.UUID + "," +
				"\"passwordHash\": \"" + this.passwordHash + "\"," +
				"\"salt\": \"" + this.salt + "\"," +
				"\"fName\": \"" + this.fName + "\"," +
				"\"lName\": \"" + this.lName + "\"," +
				"\"userName\": \"" + this.userName + "\"," +
				"\"email\": \"" + this.email + "\"," +
				"\"title\": \"" + this.title + "\"," +
				"\"companyID\": " + this.companyID + "," +
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
				"\"UUID\": " + this.UUID + "," +
				"\"fName\": \"" + this.fName + "\"," +
				"\"lName\": \"" + this.lName + "\"," +
				"\"userName\": \"" + this.userName + "\"," +
				"\"email\": \"" + this.email + "\"," +
				"\"title\": \"" + this.title + "\"," +
				"\"companyID\": " + this.companyID + "," +
				"\"accessLevelID\": " + this.accessLevelID +
				"}";
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