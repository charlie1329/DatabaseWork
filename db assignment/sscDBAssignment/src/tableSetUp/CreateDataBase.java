package tableSetUp;
import java.sql.*;

/**this class will make a connection to the database
 * it will then wipe the database of all tables and reinitialise them,
 * giving a clean slate for which to work on
 * @author Charlie Street
 */
public class CreateDataBase implements Runnable
{
	private Connection dbConn;
	private String dbName;
	private final String USERNAME = "cxs412";//user name and password for access to SoCS database server
	private final String PASSWORD = "crudopha";
	
	
	/**constructor will initialise the dbName
	 * @param dbName the string of the path to the database
	 * for the purpose of this exercise the path will be:
	 * "jdbc:postgresql://dbteach2.cs.bham.ac.uk/"
	 */
	public CreateDataBase(String dbName)
	{
		this.dbName = dbName;//initialising file path name
		this.dbConn = null;//Initialising to null; will be set later
	}
	
	/**method carries out the operations of wiping and creating the database
	 * 
	 */
	public void run()
	{
		this.dbConn = createConnection();//if i get past this point, the connection is valid and set up
		this.wipeTables();//wiping database
		this.createTables();//initialising database tables
		//I do not want to close the database in this class after creation
		//This can be done externally from another class due to me wanting to populate the database on this connection
	}
	
	/**this method will set up the connection to the database
	 * System.exit convention in this program is:
	 * -1 = failed to load driver
	 * -2 = failed to make database connection
	 * @return the connection object
	 */
	private Connection createConnection()
	{
		Connection newConn = null;//need to return something regardless
		try//checking the driver has loaded properly first
		{
			Class.forName("org.postgresql.Driver");
			try//now trying to create the connection
			{
				newConn = DriverManager.getConnection(this.getDBName(),this.USERNAME, this.PASSWORD);
			}
			catch(SQLException e)
			{
				System.err.println("Can't connect to the database at this time.");
				e.printStackTrace();
				System.exit(-2);//ought to exit, i really don't want to work with a null connection
			}
		}
		catch(ClassNotFoundException e)
		{
			System.err.println("Driver Not Found!!");
			e.printStackTrace();
			System.exit(-1);//the system will exit out of my control if i kept trying after this, so i might as well do it myself cleanly
		}
		return newConn;
	}
	
	/**this method will wipe all tables from the database
	 * this allows me to restart the program cleanly for testing purposes
	 */
	private void wipeTables()
	{
		PreparedStatement prepStmt = null;
		try
		{
			ResultSet tableNames = this.getConnection().getMetaData().getTables(null, null, "%", new String[]{"TABLE"});//getting table names in db
			
			while(tableNames.next())
			{
				prepStmt = this.getConnection().prepareStatement("DROP TABLE " + tableNames.getString("TABLE_NAME")+ " CASCADE");//changing statement per table name
				prepStmt.execute();//the cascade is used for foreign key constraint and allowing everything to be deleted from the database
			}
			tableNames.close();//closing ResultSet and PreparedStatement after use*/
			if(prepStmt != null)//don't do if null
			{
				prepStmt.close();
			}
		}
		catch(SQLException e)
		{
			System.err.println("Error In Wiping Database. The following is the stack trace: ");
			e.printStackTrace();
			this.closeDataBase(-3);
		}
	}
	
	/**this method will set up all 9 tables in the database with all the constraints
	 * there isn't really a shortcut to doing this so ultimately this will be a long method
	 */
	private void createTables()
	{
		try
		{
			
			String[] tableDefinitions = new String[9];//array for definitions
			
			tableDefinitions[0] = "CREATE TABLE Titles ("//Titles Table
							+ "titleID INTEGER, "
							+ "titleString CHAR(10) UNIQUE NOT NULL, "
							+ "CONSTRAINT titleKey PRIMARY KEY(titleID), "
							+ "CHECK(titleID > 0) )";

			tableDefinitions[1] = "CREATE TABLE RegistrationType ("//RegistrationType Table
								+ "registrationTypeID INTEGER, "
								+ "description CHAR(30) UNIQUE NOT NULL, "
								+ "CONSTRAINT registerKey PRIMARY KEY (registrationTypeID), "
								+ "CHECK(registrationTypeID > 0) )";
			
			tableDefinitions[2] = "CREATE TABLE Student (" //Student Table
								+ "studentID INTEGER, "
								+ "titleID INTEGER NOT NULL, "
								+ "foreName CHAR(40) NOT NULL, "
								+ "familyName CHAR(40), "
								+ "dateOfBirth DATE NOT NULL,"
								+ "CONSTRAINT studentsKey PRIMARY KEY(studentID), "
								+ "FOREIGN KEY(titleID) REFERENCES Titles(titleID) "
								+ "ON DELETE RESTRICT "
								+ "ON UPDATE RESTRICT, "
								+ "CHECK(dateOfBirth > '1900-01-01' AND dateOfBirth < current_date), "
								+ "CHECK(studentID > 0 AND titleID > 0) )";
			
			tableDefinitions[3] = "CREATE TABLE Lecturer (" //Lecturer Table
								+ "lecturerID INTEGER, "
								+ "titleID INTEGER NOT NULL, "
								+ "foreName CHAR(40) NOT NULL, "
								+ "familyName CHAR(40), "
								+ "CONSTRAINT lecturerKey PRIMARY KEY(lecturerID), "
								+ "FOREIGN KEY(titleID) REFERENCES Titles(titleID) "
								+ "ON DELETE RESTRICT "
								+ "ON UPDATE RESTRICT, "
								+ "CHECK(lecturerID > 0 AND titleID > 0) )";
			
			tableDefinitions[4] = "CREATE TABLE StudentRegistration (" //StudentRegistration Table
								+ "studentID INTEGER, "
								+ "yearOfStudy INTEGER NOT NULL, "
								+ "registrationTypeID INTEGER NOT NULL, "
								+ "CONSTRAINT stuRegisterKey PRIMARY KEY(studentID), "
								+ "FOREIGN KEY(studentID) REFERENCES Student(studentID) "
								+ "ON DELETE CASCADE "
								+ "ON UPDATE CASCADE, "
								+ "FOREIGN KEY(registrationTypeID) REFERENCES RegistrationType(registrationTypeID) "
								+ "ON DELETE RESTRICT "
								+ "ON UPDATE RESTRICT, "
								+ "CHECK(yearOfStudy >= 1 AND yearOfStudy <= 5), "
								+ "CHECK(studentID > 0 AND yearOfStudy > 0 AND registrationTypeID > 0) )";
			
			tableDefinitions[5] = "CREATE TABLE StudentContact (" //StudentContact Table
								+ "studentID INTEGER, "
								+ "eMailAddress CHAR(254) UNIQUE NOT NULL, "
								+ "postalAddress CHAR(300) NOT NULL, "
								+ "CONSTRAINT contactKey PRIMARY KEY(studentID), "
								+ "FOREIGN KEY(studentID) REFERENCES Student(studentID) "
								+ "ON DELETE CASCADE "
								+ "ON UPDATE CASCADE, "
								+ "CHECK(eMailAddress LIKE '_%@_%'), "
								+ "CHECK(studentID > 0) )";
			
			tableDefinitions[6] = "CREATE TABLE NextOfKinContact ("//NextOfKinContact Table
								+ "studentID INTEGER, "
								+ "name CHAR(80) NOT NULL, "
								+ "eMailAddress CHAR(254) UNIQUE NOT NULL, "
								+ "postalAddress CHAR(300) NOT NULL, "
								+ "CONSTRAINT kinKey PRIMARY KEY(studentID), "
								+ "FOREIGN KEY(studentID) REFERENCES Student(studentID) "
								+ "ON DELETE CASCADE "
								+ "ON UPDATE CASCADE, "
								+ "CHECK(eMailAddress LIKE '_%@_%'), "
								+ "CHECK(studentID > 0) )";
			
			tableDefinitions[7] = "CREATE TABLE lecturerContact (" //lecturerContact Table
								+ "lecturerID INTEGER, "
								+ "office CHAR(7) UNIQUE NOT NULL, "
								+ "eMailAddress CHAR(254) UNIQUE NOT NULL, "
								+ "CONSTRAINT lecKey PRIMARY KEY(lecturerID), "
								+ "FOREIGN KEY(lecturerID) REFERENCES Lecturer(lecturerID) "
								+ "ON DELETE CASCADE "
								+ "ON UPDATE CASCADE, "
								+ "CHECK(eMailAddress LIKE '_%@_%'), "
								+ "CHECK(lecturerID > 0) )";
			
			tableDefinitions[8] = "CREATE TABLE Tutor (" //Tutor Table
								+ "studentID INTEGER, "
								+ "lecturerID INTEGER NOT NULL, "
								+ "CONSTRAINT tutorKey PRIMARY KEY(studentID), "
								+ "FOREIGN KEY(studentID) REFERENCES Student(studentID) "
								+ "ON DELETE CASCADE "
								+ "ON UPDATE CASCADE, "
								+ "FOREIGN KEY(lecturerID) REFERENCES Lecturer(lecturerID) "
								+ "ON DELETE CASCADE "
								+ "ON UPDATE CASCADE, "
								+ "CHECK(studentID > 0), "
								+ "CHECK(lecturerID > 0) )";
			
			
			PreparedStatement create = null;
			
			for(int i = 0; i < tableDefinitions.length; i++)//looping round inserting tables into database one at a time
			{
				create = this.getConnection().prepareStatement(tableDefinitions[i]);
				create.executeUpdate();
			}
			
			create.close();//closing prepared statement after use
			
			
		}
		catch(SQLException e)
		{
			System.err.println("Error In Creating The Tables. Please refer to the stack trace: ");
			e.printStackTrace();
			this.closeDataBase(-3);
		}
	}
	
	/**simply put this method tries to close the database connection, if necessary, i.e failure
	 * if everything successful in this class, it will be used with another class to set up database
	 * and so the database won't be closed until finished elsewhere as connection is passed around
	 * System will then exit, hence the error code parameter
	 * -3 means unexpected SQLException
	 * 1 means code successful and so the system SHOULD NOT exit and continue to execute further code after database if necessary
	 * @param errorCode, the suitable error code for System.exit
	 */
	public void closeDataBase(int errorCode)
	{
		try
		{
			this.getConnection().close();
		}
		catch(SQLException e)
		{
			System.out.println("Unable to close Database. Please refer to the stack trace: ");
			e.printStackTrace();
		}
		finally
		{
			if(errorCode != 1)
			{
				System.exit(errorCode);
			}
		}
	}
	
	/**get method for dbName in CreateDataBase
	 * 
	 * @return the database name path
	 */
	public String getDBName()
	{
		return this.dbName;
	}
	
	/**this method returns the database connection object for use by other code
	 * 
	 * @return the database connection being used
	 */
	public Connection getConnection()
	{
		return this.dbConn;
	}
}
