package blink.datalayer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import blink.utility.objects.Company;
import blink.utility.objects.File;
import blink.utility.objects.Person;

public class PersonDB {
    private DBConn dbConn;

    public PersonDB(){
        this.dbConn = new DBConn();
    }

    /**
     * Connect to database and retrieve all content of person table
     * @return ArrayList of all Person objects
     * @throws SQLException Error connecting to database or executing query
     */
    public List<Person> getAllPeople() throws SQLException {
        String getAllPeopleQuery = "SELECT * FROM person;";
        String getAllCompaniesForPersonQuery = "SELECT * FROM person " +
                "JOIN personCompany ON (person.UUID = personCompany.UUID) " +
                "JOIN company ON (personCompany.companyID = company.companyID) " +
                "WHERE person.UUID = ?";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement getAllPeopleStatement = conn.prepareStatement(getAllPeopleQuery);
            ResultSet result = getAllPeopleStatement.executeQuery();
            PreparedStatement getAllCompaniesForPersonStatement = conn.prepareStatement(getAllCompaniesForPersonQuery)) {

            ArrayList<Person> people = new ArrayList<>();

            while (result.next()) {

                //Pull response content and map into a Person object
                Person person = new Person(result.getString("UUID"),
                        result.getString("username"),
                        result.getString("passwordHash"),
                        result.getString("salt"),
                        result.getString("fName"),
                        result.getString("lName"),
                        result.getString("email"),
                        result.getString("title"),
                        result.getInt("accessLevelID"));

                //Get all companies and add them to person companies list
                getAllCompaniesForPersonStatement.setString(1, person.getUuid());

                try (ResultSet companiesResult = getAllCompaniesForPersonStatement.executeQuery()) {
                    ArrayList<Company> companies = new ArrayList<>();
                    while (companiesResult.next()) {
                        companies.add(new Company(companiesResult.getInt("companyID"),
                                companiesResult.getString("name"))
                        );
                    }
                    person.setCompanies(companies);
                    people.add(person);
                }
            }

            return people;
        }
    }

    /**
     * Connect to database and retrieve entry by username
     * @param username Username to search database for
     * @return Person object or null if not found
     * @throws SQLException Error connecting to database or executing query
     */
    public Person getPersonByUsername(final String username) throws SQLException {

        //Prepare sql statements
        String getPersonQuery = "SELECT * FROM person WHERE person.username = ?";
        String getCompanyQuery = "SELECT * FROM person " +
                        "JOIN personCompany ON (person.UUID = personCompany.UUID) " +
                        "JOIN company ON (personCompany.companyID = company.companyID) " +
                        "WHERE person.UUID = ?";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement getAllPeopleStatement = conn.prepareStatement(getPersonQuery)){

            getAllPeopleStatement.setString(1, username);

            try(ResultSet result = getAllPeopleStatement.executeQuery();
                PreparedStatement getCompaniesStatement = conn.prepareStatement(getCompanyQuery)) {

                Person person = null;

                while (result.next()) {

                    //Pull response content and map into a Person object
                    person = new Person(result.getString("UUID"),
                            result.getString("username"),
                            result.getString("passwordHash"),
                            result.getString("salt"),
                            result.getString("fName"),
                            result.getString("lName"),
                            result.getString("email"),
                            result.getString("title"),
                            result.getInt("accessLevelID"));

                    //Get all companies and add them to person companies list
                    getCompaniesStatement.setString(1, person.getUuid());

                    try (ResultSet companiesResult = getCompaniesStatement.executeQuery()) {
                        ArrayList<Company> companies = new ArrayList<>();
                        while (companiesResult.next()) {
                            companies.add(new Company(companiesResult.getInt("companyID"),
                                    companiesResult.getString("name"))
                            );
                        }
                        person.setCompanies(companies);
                    }
                }

                return person;
            }
        }
    }

    /**
     * Connect to database and retrieve entry by UUID
     * @param UUID UUID to search database for
     * @return Person object or null if not found
     * @throws SQLException Error connecting to database or executing query
     */
    public Person getPersonByUUID(final String UUID) throws SQLException {
        //Prepare sql statement
        String getPersonQuery = "SELECT * FROM person WHERE person.UUID = ?;";
        String getCompanyQuery = "SELECT * FROM person " +
                "JOIN personCompany ON (person.UUID = personCompany.UUID) " +
                "JOIN company ON (personCompany.companyID = company.companyID) " +
                "WHERE person.UUID = ?";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(getPersonQuery)) {

            preparedStatement.setString(1, UUID);

            try (ResultSet result = preparedStatement.executeQuery();
                 PreparedStatement getCompaniesStatement = conn.prepareStatement(getCompanyQuery)) {

                Person person = null;

                while (result.next()) {

                    //Pull response content and map into a Person object
                    person = new Person(result.getString("UUID"),
                            result.getString("username"),
                            result.getString("passwordHash"),
                            result.getString("salt"),
                            result.getString("fName"),
                            result.getString("lName"),
                            result.getString("email"),
                            result.getString("title"),
                            result.getInt("accessLevelID"));

                    //Get all companies and add them to person companies list
                    getCompaniesStatement.setString(1, person.getUuid());

                    try (ResultSet companiesResult = getCompaniesStatement.executeQuery()) {
                        ArrayList<Company> companies = new ArrayList<>();
                        while (companiesResult.next()) {
                            companies.add(new Company(companiesResult.getInt("companyID"),
                                    companiesResult.getString("name"))
                            );
                        }
                        person.setCompanies(companies);
                    }
                }

                return person;
            }
        }
    }

    /**
     * Connect to database and retrieve entry by UUID
     * @param person existing person object to append signature information to
     * @return Person object with signature information or null if not found
     * @throws SQLException Error connecting to database or executing query
     */
    public Person getPersonSignature(final Person person) throws SQLException {
        //Prepare sql statement
        String getPersonSignatureQuery = "SELECT signature FROM person " +
                                            "WHERE person.UUID = ?;";

        try(Connection conn = this.dbConn.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(getPersonSignatureQuery)) {

            preparedStatement.setString(1, person.getUuid());

            try (ResultSet result = preparedStatement.executeQuery()){

                while (result.next()) {

                    //Pull response content and map into a Person object
                    person.setSignature(File.decodeBase64(File.blobToEncodedString(result.getBlob("signature"))));
                }

                return person;
            }
        }
    }

    /**
     * Connect to database and add a new person
     * @param UUID new Person UUID
     * @param username new person username
     * @param password new person password
     * @param salt new person salt
     * @param email new person email
     * @param title new person title
     * @param accessLevelID new person accessLevelID
     * @throws SQLException error connecting to database or executing query
     */
    public void insertPerson(final String UUID, final String username, final String password, final String salt, final String fName, final String lName, final String email, final String title, final int accessLevelID, final String signature) throws SQLException {
        //Prepare sql statement
        String query = "INSERT INTO person (UUID, username, passwordHash, salt, fName, lName, email, title, accessLevelID, signature) VALUES (?,?,?,?,?,?,?,?,?,?);";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            Blob blob = conn.createBlob();
            String encodedSignature = File.encodeBase64(signature);
            if(encodedSignature != null && encodedSignature.length() != 0) {
                blob.setBytes(1, encodedSignature.getBytes());
            }

            //Set parameters and execute query
            preparedStatement.setString(1, UUID);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, salt);
            preparedStatement.setString(5, fName);
            preparedStatement.setString(6, lName);
            preparedStatement.setString(7, email);
            preparedStatement.setString(8, title);
            preparedStatement.setInt(9, accessLevelID);
            preparedStatement.setBlob(10, blob);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Connect to database and update person using UUID
     * @param UUID UUID of person to update
     * @param username new person username
     * @param password new person password
     * @param email new person email
     * @param title new person title
     * @param accessLevelID new person accessLevelID
     * @throws SQLException error connecting to database or executing query
     */
    public void updatePerson(final String UUID, final String username, final String password, final String fName, final String lName, final String email, final String title, final int accessLevelID, final String signature) throws SQLException {
        //Prepare sql statement
        String query = "UPDATE person SET username = ?, passwordHash = ?, fName = ?, lName = ?,  email = ?, title = ?, accessLevelID = ?, signature = ? WHERE UUID = ?;";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {


            Blob blob = conn.createBlob();
            String encodedSignature = File.encodeBase64(signature);
            if(encodedSignature != null && encodedSignature.length() != 0) {
                blob.setBytes(1, encodedSignature.getBytes());
            }

            //Set parameters and execute query
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, fName);
            preparedStatement.setString(4, lName);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, title);
            preparedStatement.setInt(7, accessLevelID);
            preparedStatement.setBlob(8, blob);
            preparedStatement.setString(9, UUID);

            preparedStatement.executeUpdate();
        }
    }

    /**
     * Connect to database and delete a person by UUID
     * @param UUID UUID to delete from database
     * @return number of rows deleted
     * @throws SQLException Error connecting to database or executing query
     */
    public int deletePersonByUUID(final String UUID) throws SQLException {
        //Prepare sql statement
        String query = "DELETE FROM person WHERE person.UUID = ?;";

        try (Connection conn = this.dbConn.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            //Set parameters and execute query
            preparedStatement.setString(1, UUID);
            return preparedStatement.executeUpdate();
        }
    }

    /**
     * Converts an encoded base64 String into a blob
     * @param signature
     * @param conn
     * @return
     * @throws SQLException
     */
    public Blob encodedStringToBlob(String signature, Connection conn) throws SQLException {
        Blob blob = conn.createBlob();
        blob.setBytes(1, signature.getBytes());
        return blob;
    }
}
