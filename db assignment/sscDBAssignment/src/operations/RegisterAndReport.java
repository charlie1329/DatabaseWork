package operations;

import java.util.ArrayList;
import java.sql.*;

import tableSetUp.CreateTablesAndPopulate;

/** this class will carry out the main grunt of the assignment as it will 
 * allow the registration of a new student, allow to add a new tutor
 * it will also produce reports for a student and a lecturer (with his tutees)
 * since this class will have an interface put on top of it most of the methods will have
 * thrown exceptions which will be caught by the interface so I can deal with them at that stage
 * (easier to show error messages in a GUI that way)
 * @author Charlie Street
 */
public class RegisterAndReport 
{
	private Connection dbConn;
	private String dbName;
	private final String USER_NAME = "cxs412";
	private final String PASSWORD = "crudopha";
	private PreparedStatement selectStmt;
	private PreparedStatement insertStmt;
	
	/**this constructor will set up the new connection,
	 * the new prepared statements and will also reset the tables
	 * @param dbName the database path
	 * @param setUp makes it easy to switch whether I want to create the tables or not
	 */
	public RegisterAndReport(String dbName, boolean setUp)
	{
		this.dbName = dbName;
		this.dbConn = null;
		
		if(setUp)//only create tables if true
		{
			CreateTablesAndPopulate creation = new CreateTablesAndPopulate(this.dbName);
			creation.run();//re-initialising tables
		}
		
		this.setUpConnection();
		
		this.selectStmt = null;
		this.selectStmt = null;
		
	}
	
	/**doing as has been done before and setting up a new connection to the database. This will be the second
	 * and last connection made to the database, the first being for the creation and wiping; I ideally want to keep these 
	 * things separately
	 * The same error code as CreateDataBase has been employed.
	 */
	private void setUpConnection()
	{
		try//checking the driver has loaded properly first
		{
			Class.forName("org.postgresql.Driver");
			try//now trying to create the connection
			{
				this.dbConn = DriverManager.getConnection(this.getDBName(),this.USER_NAME, this.PASSWORD);
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
	}
	
	/**returns the database file path
	 * 
	 * @return the database file path
	 */
	public String getDBName()
	{
		return this.dbName;
	}
	
	/**this method returns the connection object
	 * this will be highly useful when working in the GUI
	 * i.e due to the possibility of turning OFF auto commit 
	 * during the student insert
	 * @return
	 */
	public Connection getConnection()
	{
		return this.dbConn;
	}
	
	/**this method closes the database and then exits the system if appropriate
	 * 
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
	
	/**returns the inserting prepared statement
	 * 
	 * @return the inserting statement
	 */
	public PreparedStatement getInsertStmt()
	{
		return this.insertStmt;
	}
	
	/** returns the select statement
	 * (later allows me to close outside of this class
	 * which is very useful for the interface stuff)
	 * @return the select prepared statement
	 */
	public PreparedStatement getSelectStmt()
	{
		return this.selectStmt;
	}
	
	
	
	/**this method will insert a student into the database
	 * all values are given in a String[] from the gui
	 * if a value is "" it is assumed null and will be entered as such
	 * this may throw an SQLException which I can assure will be caught
	 * by the code dealing with the interface 
	 * (it will probably rollback too, meaning it ALL has to be correct to insert)
	 * @param valuesToEnter the user entered values taken from the interface
	 * @throws SQLException may be thrown due to not satisfying table constraints
	 * @throws NumberFormatException may be thrown if incorrect input by user
	 * @throws IllegalArgumentException if user specifies date in wrong format
	 * PLEASE NOTE: WE HAVE BEEN SPECIFICALLY BEEN ASKED TO HAVE THE DATABASE CHECK FOR ERRORS
	 * THE JAVA PROGRAM WILL THEREFORE HAVE NO SANITY CHECKS, as useful as that would be
	 */
	public void insertStudent (String[] valuesToEnter) throws SQLException,NumberFormatException,IllegalArgumentException
	{
		this.getConnection().setAutoCommit(false);//for period of insert student I want full control of transactions
		//this is because I want either whole insert to be completed (multiple inserts in one register) or none at all.
		
		for(int i = 0; i < valuesToEnter.length; i++)//i know this will have length 12 but i am being more general here
		{
			if(valuesToEnter[i].equals(""))
			{
				valuesToEnter[i] = null;;//setting empty values to null 
			}
		}
		
		//getting appropriate title id from name
		this.selectStmt = this.getConnection().prepareStatement("SELECT titleID FROM Titles WHERE titleString = ?");
		this.getSelectStmt().setString(1,(String)valuesToEnter[1]);
		String title = null;
		ResultSet rsTitle = this.getSelectStmt().executeQuery();
		while(rsTitle.next())//will be 0 or 1 rows assuming constraints hold
		{
			title = ""+rsTitle.getInt(1);
		}
		valuesToEnter[1] = title;
		rsTitle.close();//closing result set
		
		//getting correct registrationID; user enters description as no-one remembers id
		this.selectStmt = this.getConnection().prepareStatement("SELECT registrationTypeID FROM RegistrationType WHERE description = ?");
		this.getSelectStmt().setString(1, (String)valuesToEnter[6]);
		String registration = null;
		ResultSet rsRegister = this.getSelectStmt().executeQuery();
		while(rsRegister.next())
		{
			registration = "" + rsRegister.getInt(1);
		}
		valuesToEnter[6] = registration;
		rsRegister.close();
		
		//inserting values into student table
		this.insertStmt = this.getConnection().prepareStatement("INSERT INTO Student VALUES (?,?,?,?,?)");
		
		this.setInsertValue(valuesToEnter,1,0,Types.INTEGER);//checking for null
		this.setInsertValue(valuesToEnter,2,1,Types.INTEGER);
		this.setInsertValue(valuesToEnter,3,2,Types.CHAR);
		this.setInsertValue(valuesToEnter,4,3,Types.CHAR);
		this.setInsertValue(valuesToEnter,5,4,Types.DATE);

		this.getInsertStmt().executeUpdate();//adding to student table
		
		//inserting values into Student Registration table
		this.insertStmt = this.getConnection().prepareStatement("INSERT INTO StudentRegistration VALUES (?,?,?)");
		
		this.setInsertValue(valuesToEnter,1,0,Types.INTEGER);//checking for null
		this.setInsertValue(valuesToEnter,2,5,Types.INTEGER);
		this.setInsertValue(valuesToEnter,3,6,Types.INTEGER);
		
		this.getInsertStmt().executeUpdate();
		
		//inserting values into Student Contact table
		this.insertStmt = this.getConnection().prepareStatement("INSERT INTO StudentContact VALUES (?,?,?)");
		
		this.setInsertValue(valuesToEnter,1,0,Types.INTEGER);//checking for null
		this.setInsertValue(valuesToEnter,2,7,Types.CHAR);
		this.setInsertValue(valuesToEnter,3,8,Types.CHAR);
		
		this.getInsertStmt().executeUpdate();
		
		//inserting values into next of kin table
		this.insertStmt = this.getConnection().prepareStatement("INSERT INTO NextOfKinContact VALUES (?,?,?,?)");
		
		this.setInsertValue(valuesToEnter,1,0,Types.INTEGER);//checking for null
		this.setInsertValue(valuesToEnter,2,9,Types.CHAR);
		this.setInsertValue(valuesToEnter,3,10,Types.CHAR);
		this.setInsertValue(valuesToEnter,4,11,Types.CHAR);
		
		this.getInsertStmt().executeUpdate();
		
		this.getConnection().commit();//Committing now it is all finished (rollback will be used in error checking)
		this.getConnection().setAutoCommit(true);//resetting to true; this is the only stage I want control of transactions
	}
	
	
	
	/**this method sets the value in the insert statement to null if necessary and sets it to the correct null type
	 * 
	 * @param valuesToEnter the array of results
	 * @param prepIndex the prepared statement parameter
	 * @param arrIndex the index in the array
	 * @param type the type of the null value
	 * @throws SQLException may throw but will be caught by gui
	 * @throws NumberFormatException see above
	 * @throws IllegalArgumentException see above
	 */
	private void setInsertValue(String[] valuesToEnter, int prepIndex, int arrIndex, int type) throws SQLException,NumberFormatException,IllegalArgumentException
	{
		if(valuesToEnter[arrIndex] == null)
		{
			this.getInsertStmt().setNull(prepIndex,type);
		}
		else
		{
			if(type == Types.CHAR)//need to used correct insert statement
			{
				this.getInsertStmt().setString(prepIndex,valuesToEnter[arrIndex]);
			}
			else if(type == Types.INTEGER)
			{
				this.getInsertStmt().setInt(prepIndex,Integer.parseInt(valuesToEnter[arrIndex]));
			}
			else if(type == Types.DATE)
			{
				this.getInsertStmt().setDate(prepIndex, Date.valueOf(valuesToEnter[arrIndex]));
			}
		}
	}
	

	
	/**method will attempt to insert a new student into the tutor table in the database
	 * yet again this method will throw an sql exception if something goes wrong; this will be picked up by the GUI
	 * @param studentID the student ID
	 * @param tutorID the tutors ID
	 * @throws SQLException if necessary will be thrown but will be caught by gui code
	 * @throws NumberFormatExcpetion throws if something that isn't a number is entered as entry. This is incorrect input and so will be picked up
	 */
	public void insertNewTutorLink(String studentID, String tutorID) throws SQLException,NumberFormatException
	{
		
		this.insertStmt = this.getConnection().prepareStatement("INSERT INTO Tutor VALUES (?,?)");
		
		setIDValues(studentID,1);//setting values appropriately
		setIDValues(tutorID,2);
		
		this.getInsertStmt().executeUpdate();//inserting data into the tutor table
	}
	
	/**method is used to set the id values for tutor link appropriately
	 * 
	 * @param currentID the currentID to check
	 * @param prepIndex the index for the prepared statement
	 * @throws SQLException may be thrown but handled by the gui
	 * @throws NumberFormatException see above
	 */
	private void setIDValues(String currentID, int prepIndex) throws SQLException, NumberFormatException
	{
		if(currentID.equals(""))//formatting entry for database so errors can be returned if necessary
		{
			this.getInsertStmt().setNull(prepIndex, Types.INTEGER);//null if empty
		}
		else
		{
			this.getInsertStmt().setInt(prepIndex, Integer.parseInt(currentID));
		}
	}
	
	/**method will return a String[] representing the students report
	 * 
	 * @param studentID the studentID to look for in the database
	 * @return the array of strings returned representing the report
	 * @throws SQLException there is a chance such an exception could be thrown; this will be dealt with by the GUI
	 */
	public String[] studentReport(int studentID) throws SQLException
	{
		String[] report = new String[12];//array to store the information
		this.selectStmt = this.getConnection().prepareStatement("SELECT t.titleString, s.foreName, s.familyName, s.dateOfBirth, s.studentID "
																+ "FROM Student s, Titles t "
																+ "WHERE s.titleID = t.titleID AND s.studentID = ?");
		this.getSelectStmt().setInt(1,studentID);//selecting all appropriate info from student
		ResultSet studentTableRs = this.getSelectStmt().executeQuery();
		
		while(studentTableRs.next())//getting all data from student in the right format
		{
			report[0] = studentTableRs.getString(1);
			report[1] = studentTableRs.getString(2);
			report[2] = studentTableRs.getString(3);
			report[3] = studentTableRs.getDate(4).toString();
			report[4] = "" + studentTableRs.getInt(5);
		}
		
		this.selectStmt = this.getConnection().prepareStatement("SELECT s.yearOfStudy, r.description "//getting data from student registration
																+ "FROM StudentRegistration s, RegistrationType r "
																+ "WHERE r.registrationTypeID = s.registrationTypeID AND s.studentID = ?");
		this.getSelectStmt().setInt(1,studentID);
		ResultSet rsRegister = this.getSelectStmt().executeQuery();
		while(rsRegister.next())//getting data about registration
		{
			report[5] = ""+rsRegister.getInt(1);
			report[6] = rsRegister.getString(2);
		}
		
		
		this.selectStmt = this.getConnection().prepareStatement("SELECT eMailAddress, postalAddress "//getting info from student contact table
																+ "FROM StudentContact "
																+ "WHERE studentID = ?");
		this.getSelectStmt().setInt(1, studentID);//setting to studentID
		ResultSet rsContact = this.getSelectStmt().executeQuery();
		while(rsContact.next())//retrieving from student contact table
		{
			report[7] = rsContact.getString(1);
			report[8] = rsContact.getString(2);
		}
		
		this.selectStmt = this.getConnection().prepareStatement("SELECT name, eMailAddress, postalAddress "//query for next of kin table
																+ "FROM NextOfKinContact "
																+ "WHERE studentID = ?");
		this.getSelectStmt().setInt(1, studentID);
		ResultSet rsKin = this.getSelectStmt().executeQuery();
		while(rsKin.next())//getting next of kin data for student
		{
			report[9] = rsKin.getString(1);
			report[10] = rsKin.getString(2);
			report[11] = rsKin.getString(3);
		}
		
		return report;
	}
	
	/** this method will return a ArrayList<String[]> representing a lecturer report
	 * 
	 * @param lecturerID the id of the lecturer with whom to produce a report
	 * @return an array list of string arrays of the report
	 * @throws SQLException may be thrown in the case of an error. Will be picked up by the GUI code
	 */
	public ArrayList<String[]> lecturerReport(int lecturerID) throws SQLException
	{
		ArrayList<String[]> report = new ArrayList<String[]>();
		
		this.selectStmt = this.getConnection().prepareStatement("SELECT l.lecturerID, t.titleString, l.foreName, l.familyName "
																+ "FROM Lecturer l, Titles t "//setting up lecturer query
																+ "WHERE t.titleID = l.titleID AND l.lecturerID = ?");
		
		this.getSelectStmt().setInt(1, lecturerID);//setting id to lecturerID
		ResultSet rsLec = this.getSelectStmt().executeQuery();
		
		while(rsLec.next())//gets lecturer data
		{
			String[] lecDetails = new String[]{""+rsLec.getInt(1), rsLec.getString(2), rsLec.getString(3), rsLec.getString(4)};
			report.add(lecDetails);//one element of the result array list
		}
		
		this.selectStmt = this.getConnection().prepareStatement("SELECT t.studentID "//getting all students given by lecturer
																+ "FROM Tutor t, StudentRegistration r "
																+ "WHERE t.lecturerID = ? AND r.studentID = t.studentID "
																+ "ORDER BY r.yearOfStudy ASC");
		
		this.getSelectStmt().setInt(1,lecturerID);
		ResultSet rsStudents = this.getSelectStmt().executeQuery();
		while(rsStudents.next())//getting the students of that tutor
		{
			String[] current = studentReport(rsStudents.getInt(1));
			report.add(current);
		}
		
		return report;
	}
	
	/**method will shut down all database statements and connections after use
	 * 
	 */
	public void shutDown()
	{
		int shutDownCode = 1;
		try
		{
			this.getSelectStmt().close();
			this.getInsertStmt().close();
			closeDataBase(shutDownCode);
		}
		catch(SQLException e)
		{
			System.err.println("Unable to shut down system correctly. Please refer to the stack trace");
			e.printStackTrace();
			shutDownCode = -3;
		}
		finally
		{
			System.exit(shutDownCode);
		}
	}
	
	/**test main method
	 * IGNORE!!!
	 */
	public static void main(String[] args)
	{
		RegisterAndReport register = new RegisterAndReport("jdbc:postgresql://dbteach2.cs.bham.ac.uk/cxs412",true);
		try
		{
		register.insertStudent(new String[]{"1020","Mr","charlie", "street","2009-08-07","5","Normal","charlie.stree65t1@btinternet.com",
			"some address", "jackie street", "jackie.s65treet@btinternet.com","7 cassi65obury road"	
		});
		
		String[] test = register.studentReport(1020);
		for(int i = 0; i < test.length; i++)
		{
			System.out.println(test[i]);
		}
		
		System.out.println();
		register.insertNewTutorLink("1020", "301");
		ArrayList<String[]> lecReport = register.lecturerReport(301);
		
		for(int i = 0; i < lecReport.size(); i++)
		{
			for(int j = 0; j < lecReport.get(i).length; j++)
			{
				System.out.println(lecReport.get(i)[j]);
			}
			System.out.println();
		}
		
		
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
}
