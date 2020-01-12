package b404.utility;

/*
import java.sql.*;
import java.util.*;

import b404.datalayer.PersonDB;
*/

/**
 * Utility class for representing a Person interacting with this system
 */
public class Person {

	private int userID;
	private String name;
	private String email;
	private String passwordHash;
	private String salt;
	private String title;
	private int companyID;
	private int accessLevelID;

	//Default constructor
	public Person() {}

	//Constructor with primary keys only
	public Person(int userID) {
		this.userID = userID;
	}


	//Constructor for all non-null values
    public Person(int userID, String name, String password, int companyID, int accessLevelID){
		this.userID = userID;
		this.name = name;
		this.passwordHash = password;
		this.companyID = companyID;
		this.accessLevelID = accessLevelID;
	}

	//Fully parameterised constructor
	public Person(int userID, String name, String email, String passwordHash, String salt, String title, int companyID, int accessLevelID) {
		this.userID = userID;
		this.name = name;
		this.email = email;
		this.passwordHash = passwordHash;
		this.salt = salt;
		this.title = title;
		this.companyID = companyID;
		this.accessLevelID = accessLevelID;
	}

	public int getUserID() {
		return this.userID;
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

	public void setUserID(int userID) {
		this.userID = userID;
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

	public void setAccessLevelID(int accessLevelID) {
		this.accessLevelID = accessLevelID;
	}

	public String toString() {
		return this.userID + ' ' + this.name + ' ' + this.email + ' ' + this.passwordHash + ' ' + this.salt + ' ' + this.title + ' ' + this.companyID + ' ' + this.accessLevelID;
	}

	/*
	public boolean fetch() {
		PersonDB api = new PersonDB();
		String query = "SELECT name, email, passwordHash, salt, title, companyID, accessLevelID FROM Person WHERE userID LIKE ?";
		ArrayList<String> values = new ArrayList<String>();
		values.add(userID);
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
			+ "WHERE userID LIKE ?;";
		ArrayList<String> values = new ArrayList<String>();
		values.add(name);
		values.add(email);
		values.add(passwordHash);
		values.add(salt);
		values.add(title);
		values.add(companyID);
		values.add(accessLevelID);
		values.add(userID);
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
		values.add(userID);
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
		String query = "DELETE FROM Person WHERE userID LIKE ?;";
		ArrayList<String> values = new ArrayList<String>();
		values.add(userID);
		int rc = api.setData(query, values);
		return rc;
	}
	*/

} // End of class Person