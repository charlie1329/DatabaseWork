package tableSetUp;

import java.sql.*;
import java.util.Random;


/**this class acts to populate the database using a mixture of
 * (mainly) synthetic data and also some real data for use by later parts in the assignment
 * @author Charlie Street
 *
 */
public class PopulateDataBase implements Runnable
{
	private Connection dbConn;
	private PreparedStatement insertStmt;
	private Random randomGenerator;
	
	/** this constructor takes the connection so we can use it and then
	 * sets up the random number generator
	 * it also creates the generic insert prepared statement
	 * @param dbConn the database connection being used
	 */
	public PopulateDataBase(Connection dbConn)
	{
		this.dbConn = dbConn;
		this.randomGenerator = new Random();
		this.insertStmt = null;//initially made null
		
	}
	
	/**method closes the database using same error code convention
	 * as in CreateDataBase
	 * @param errorCode the exit error code
	 */
	private void closeDataBase(int errorCode)
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
	
	/**returns connection in use
	 * 
	 * @return the current database connection object
	 */
	private Connection getConnection()
	{
		return this.dbConn;
	}
	
	/**inserts 5 new titles into the table Titles
	 * 
	 */
	private void createTitles()
	{
		try//inserting titles
		{
			this.insertStmt = this.getConnection().prepareStatement("INSERT INTO Titles VALUES (?,?)");//current statement for titles
			String[] titles = new String[]{"Mr", "Mrs", "Ms", "Miss", "Dr","Professor"};
			for(int i = 0; i < titles.length; i++)//looping round inserting into Titles table
			{
				this.insertStmt.setInt(1, i+1);
				this.insertStmt.setString(2, titles[i]);
				this.insertStmt.executeUpdate();//inserting to table
			}
		}
		catch(SQLException e)
		{
			System.err.println("Error Inserting Into Titles. Please refer to the stack trace.");
			e.printStackTrace();
			this.closeDataBase(-3);
		}
		
	}
	
	/**this method creates 3 new registration type for the database
	 * 
	 */
	private void createRegistrationTypes()
	{
		try//inserting registration types
		{
			this.insertStmt = this.getConnection().prepareStatement("INSERT INTO RegistrationType VALUES (?,?)");
			String[] types = new String[]{"Normal", "Repeat", "External"};
			for(int i = 0; i < types.length; i++)
			{
				this.insertStmt.setInt(1,i+1);
				this.insertStmt.setString(2,types[i]);
				this.insertStmt.executeUpdate();//inserting to table
				
			}
		}
		catch(SQLException e)
		{
			System.err.println("Error Inserting Into RegistrationType. Please refer to the stack trace: ");
			e.printStackTrace();
			this.closeDataBase(-3);
		}
	}
	
	/**method creates 1000 synthetic students 
	 * for the database
	 */
	private void createSyntheticStudents()
	{
		try
		{
			for(int i = 1; i <= 200; i++)
			{
				//generating the random values necessary
				int titleRandomID = this.randomGenerator.nextInt(6) + 1;//between 1 and 6
				int dOBYear = 1900+this.randomGenerator.nextInt(115);
				int dOBMonth = 1 + this.randomGenerator.nextInt(12);
				int dOBDay = 1 + this.randomGenerator.nextInt(28);
				
				String randomDOB = dOBYear+"-"+dOBMonth+"-"+dOBDay;
				int randomYearOfStudy = 1 + this.randomGenerator.nextInt(5);
				int randomRegisterType = 1 + this.randomGenerator.nextInt(3);
				
				this.insertStmt = this.getConnection().prepareStatement("INSERT INTO Student VALUES (?,?,?,?,?)");
				this.insertStmt.setInt(1,i);//inserting into student table
				this.insertStmt.setInt(2, titleRandomID);
				this.insertStmt.setString(3,"StudentForeName"+i);
				this.insertStmt.setString(4,"StudentFamilyName" + i  );
				this.insertStmt.setDate(5,Date.valueOf(randomDOB));
				this.insertStmt.executeUpdate();
				
				this.insertStmt = this.getConnection().prepareStatement("INSERT INTO StudentRegistration VALUES (?,?,?)");
				this.insertStmt.setInt(1,i);//inserting to student registration table
				this.insertStmt.setInt(2, randomYearOfStudy);
				this.insertStmt.setInt(3, randomRegisterType);
				this.insertStmt.executeUpdate();
				
				this.insertStmt = this.getConnection().prepareStatement("INSERT INTO StudentContact VALUES (?,?,?)");
				this.insertStmt.setInt(1, i);//inserting to student contact table
				this.insertStmt.setString(2,"XXX"+i+"@bham.ac.uk");
				this.insertStmt.setString(3,"Postal Address For Student:"+i);
				this.insertStmt.executeUpdate();
				
				this.insertStmt = this.getConnection().prepareStatement("INSERT INTO NextOfKinContact (studentID,name,eMailAddress,postalAddress) VALUES (?,?,?,?)");
				this.insertStmt.setInt(1, i);//inserting to next of kin table
				this.insertStmt.setString(2, "Next Of Kin Of Student: "+i);
				this.insertStmt.setString(3, "ABC"+i+"@gmail.com");
				this.insertStmt.setString(4,"Postal Address For Next Of Kin"+i);
				this.insertStmt.executeUpdate();
			}
		}
		catch(SQLException e)
		{
			System.err.println("Error Inserting Synthetic Students. Please refer to the stack trace for more info: ");
			e.printStackTrace();
			this.closeDataBase(-3);
		}
	}
	
	/**this method will create 20 synthetic lecturers for the database
	 * 
	 */
	private void createSyntheticLecturers()
	{
		String[] officeTypes = new String[]{"UG","LG","LLG","1","2"};//synthetic floors for the office to be on
		try
		{
			for(int i = 1; i <= 30; i++)
			{
				String randomOffice = officeTypes[this.randomGenerator.nextInt(officeTypes.length)]+i;
				int randomTitle = 1 + this.randomGenerator.nextInt(6);
				
				this.insertStmt = this.getConnection().prepareStatement("INSERT INTO Lecturer VALUES (?,?,?,?)");
				this.insertStmt.setInt(1,i);//inserting into lecturer table
				this.insertStmt.setInt(2,randomTitle);
				this.insertStmt.setString(3, "LecturerFirstName"+i);
				this.insertStmt.setString(4, "LecturerFamilyName"+i);
				this.insertStmt.executeUpdate();
				
				this.insertStmt = this.getConnection().prepareStatement("INSERT INTO LecturerContact VALUES (?,?,?)");
				this.insertStmt.setInt(1,i);//inserting into lecturer contact table
				this.insertStmt.setString(2, randomOffice);
				this.insertStmt.setString(3, "LEC"+i+"@bham.ac.uk");
				this.insertStmt.executeUpdate();
			}
		}
		catch(SQLException e)
		{
			System.err.println("Error Inserting Synthetic Students. Please refer to the stack trace for more info: ");
			e.printStackTrace();
			this.closeDataBase(-3);
		}
	}
	
	/**method generates a fairly random of number of tutor relationships
	 * to go into the Tutor table
	 */
	private void createSyntheticTutorRelations()
	{
		try
		{
			this.insertStmt = this.getConnection().prepareStatement("INSERT INTO Tutor VALUES (?,?)");//inserting into tutor table
			for(int i = 1; i <= 200; i++)
			{
				int randomLecturer = this.randomGenerator.nextInt(20) + 1;
				this.insertStmt.setInt(1,i);
				this.insertStmt.setInt(2,randomLecturer);
				this.insertStmt.executeUpdate();
			}
		}
		catch(SQLException e)
		{
			System.err.println("Error Inserting Synthetic Tutor Relations. Please refer to the stack trace for more info: ");
			e.printStackTrace();
			this.closeDataBase(-3);
		}
	}
	
	/**this method creates 10 'real' students 
	 * 
	 */
	private void createRealStudents()
	{
		try
		{
			this.insertStmt = this.getConnection().prepareStatement("INSERT INTO Student VALUES (?,?,?,?,?)");//inserting into students
			Object[][] realStudents = new Object[][]{{1001, 1, "Charlie", "Street", "1996-03-05"},
													{1002, 4, "Janet", "Smith", "1994-12-27"},
												    {1003, 2, "Catherine", "Jones", "1989-07-15"},
												    {1004, 1, "Ainsley", "Harriott", "1957-02-28"},
												    {1005, 5, "Brian", "Blessed", "1992-01-14"},
												    {1006, 6, "Kevin", "Bacon", "1984-09-13"},
												    {1007, 3, "Alena", "Irving", "1997-04-04"}, 
												    {1008, 6, "Graham", "Jones", "1990-05-07"},
												    {1009, 5, "Phillip", "Brown", "2000-06-30"},
												    {1010, 4, "Sarah", "Roberts", "1955-08-23"},
												    {1011, 1, "Jamie", "Taylor", "1996-04-30"},
												    {1012, 6, "Steven", "Wilson", "1967-03-29"},
												    {1013, 5, "Barry", "Chuckle", "1972-11-16"},
												    {1014, 1, "Oscar", "Scott", "1987-05-03"},
												    {1015, 5, "Guthrie", "Govan", "1970-06-06"}};
			for(int i = 0; i < realStudents.length; i++)
			{
				this.insertStmt.setInt(1,(Integer)realStudents[i][0]);
				this.insertStmt.setInt(2,(Integer)realStudents[i][1]);
				this.insertStmt.setString(3,(String)realStudents[i][2]);
				this.insertStmt.setString(4,(String)realStudents[i][3]);
				this.insertStmt.setDate(5,Date.valueOf((String)realStudents[i][4]));
				this.insertStmt.executeUpdate();
			}
			
			this.insertStmt = this.getConnection().prepareStatement("INSERT INTO StudentRegistration VALUES (?,?,?)");//creating real student registrations
			int[][] realRegistrations =  new int[][]{{1001, 2, 1},
													   {1002, 1, 2},
													   {1003, 3, 3},
													   {1004, 4, 1},
													   {1005, 5, 2},
													   {1006, 3, 1},
													   {1007, 1, 2},
													   {1008, 2, 1},
													   {1009, 4, 3},
													   {1010, 5, 3},
													   {1011, 1, 1},
													   {1012, 2, 3},
													   {1013, 3, 2},
													   {1014, 4, 1},
													   {1015, 5, 2}};
			
			for(int i = 0; i < realRegistrations.length; i++)
			{
				this.insertStmt.setInt(1,realRegistrations[i][0]);
				this.insertStmt.setInt(2,realRegistrations[i][1]);
				this.insertStmt.setInt(3,realRegistrations[i][2]);
				this.insertStmt.executeUpdate();
			}
			
			this.insertStmt = this.getConnection().prepareStatement("INSERT INTO StudentContact VALUES (?,?,?)");//inserting into student contact
			Object[][] realContacts = new Object[][]{{1001, "CXS412@student.cs.bham.ac.uk", "287 Dawlish Road, Birmingham. B29 7AU"},
												 {1002, "JCS123@student.phy.bham.ac.uk", "167 Tiverton Road, Birmingham. B29 7BV"},
												 {1003, "CatJones1@gmail.com", "32 Avenue Road, Weymouth, Dorset. DT4 7JU"},
												 {1004, "YeahhhBoiii@spicyMail.com", "31 Kings Road, London. E29 3BT"},
												 {1005, "flashGordonDive17@btinernet.com", "65 Charles Street, Nottingham. F45 2BR"},
												 {1006, "Footloose49@ee.net", "157 Queens Drive, Grimsby. G42 8IU."},
												 {1007, "ALI98@ma.bham.ac.uk", "489 Aries Drive, Queensland, Australia."},
												 {1008, "myNamesGraham@yahoo.co.uk", "3 Sea View Avenue, Plymouth. PO5 5HH"},
												 {1009, "pHBrownServices@live.co.uk", "54 Cobalt Avenue, Dublin, Ireland. IR78 B52"},
												 {1010, "sarRob198@student.mfl.bham.ac.uk", "21 Hubert Road, Birmingham. B27 F32"},
												 {1011, "jamjam1144@ask.com", "37 Pottery Avenue, Hull. H15 T75"},
												 {1012, "luminol@kscope.net", "65 Battersea Close, Hemel Hempstead. H32 U87"},
												 {1013, "toMe1@toYou2.com","42 Slackers Close, Torquay. T12 I36"},
												 {1014, "trueBlueOscar@chelsea.com", "37 Fulham Road, London. E12 G43"},
												 {1015, "guthrieGuitar@gmail.com", "42 Astoria Road, Essex. E98 X67"}};
												 
			for(int i = 0; i < realContacts.length; i++)
			{
				this.insertStmt.setInt(1,(Integer)realContacts[i][0]);
				this.insertStmt.setString(2,(String)realContacts[i][1]);
				this.insertStmt.setString(3,(String)realContacts[i][2]);
				this.insertStmt.executeUpdate();
			}
			
			this.insertStmt = this.getConnection().prepareStatement("INSERT INTO NextOfKinContact VALUES (?,?,?,?)");//inserting into next of kin table
			Object[][] realKin = new Object[][]{{1001,"Jackqueline Street", "jackie1.street@btinternet.com", "7 Cassiobury Road, Weymouth, Dorset. DT4 7JN"},
										  {1002, "Andrew Smith", "ams@yahoomail.com", "167 Tiverton Road, Birmingham. B29 7BV"},
										  {1003, "Michael Jones", "mxj@gmail.com", "32 Avenue Road, Weymouth, Dorest. DT4 7JU"},
										  {1004, "Rick Astley", "noStrangersToLove@gmail.com", "55 Oxford Circus, London. E28 T56"},
										  {1005, "John Prescott", "twoJags321@live.co.uk", "32 Kensington Road, London, E17 R76"},
										  {1006, "Tom Hanks", "txh78@bham.ac.uk", "72 Sunset Drive, California, USA."},
										  {1007, "John Irving", "JohnHIrving@btinternet.com","34 Valley Drive, Cardiff, Wales. WH6 7TR"},
										  {1008, "Gareth Thomas", "me@GarethThomas.net", "3 Sea View Avenue, Plymouth. P05 5HH"},
										  {1009, "Jessica Brown", "jessBrown@outlook.com", "47 Pewter Road, Dublin, Ireland. IR65 C32"},
										  {1010, "Hannah Roberts", "hannahR@acc.org", "156 Mountain Close, Aberdeen, Scotland. SR3 5RT"},
										  {1011, "Pamela Taylor", "pam452@live.co.uk","37 Pottery Avenue, Hull. H15 T75"},
										  {1012, "Gavin Harrison", "gavinPolygraph@btinternet.com","35 Tail CLose, Tilburg, Netherlands"},
										  {1013, "Paul Chuckle", "toYou1@toMe2.com", "43 Slackers Close, Torquay. T12 I36"},
										  {1014, "Jose Mourinho", "theSpecialOne@gmail.com", "43 austin drive, Lisbon, Portugal."},
										  {1015, "Marco Minnemann", "boing148@deutsche-mail.de", "72 Dove close, Berlin, Germany."}};
			
			for(int i = 0; i < realKin.length; i++)
			{
				this.insertStmt.setInt(1,(Integer)realKin[i][0]);
				this.insertStmt.setString(2,(String)realKin[i][1]);
				this.insertStmt.setString(3,(String)realKin[i][2]);
				this.insertStmt.setString(4,(String)realKin[i][3]);
				this.insertStmt.executeUpdate();
			}
		}
		catch(SQLException e)
		{
			System.err.println("Error Creating Real Student Data. Please refer to the stack trace for more info: ");
			e.printStackTrace();
			this.closeDataBase(-3);
		}
	}
	
	/**creates real lecturer data for 3 lecturers (sufficient for example of 10 real students
	 * 
	 */
	private void createRealLecturers()
	{
		try
		{
			this.insertStmt = this.getConnection().prepareStatement("INSERT INTO Lecturer VALUES (?,?,?,?)");//entering into lecturer table
			Object[][] realLecturers = new Object[][]{{301, 5,"Robert", "Hendley"},
													{302, 6,"Jon", "Rowe"},
													{303, 5,"Mark","Lee"}};
			
			for(int i = 0; i < realLecturers.length; i++)
			{
				this.insertStmt.setInt(1,(Integer)realLecturers[i][0]);
				this.insertStmt.setInt(2,(Integer)realLecturers[i][1]);
				this.insertStmt.setString(3,(String)realLecturers[i][2]);
				this.insertStmt.setString(4,(String)realLecturers[i][3]);
				this.insertStmt.executeUpdate();
			}
			
			this.insertStmt = this.getConnection().prepareStatement("INSERT INTO LecturerContact VALUES (?,?,?)");//inserting into lecturer contact
			Object[][] realContacts = new Object[][]{{301, "UG301", "RJH@cs.bham.ac.uk"},
												 {302, "312", "JXR@cs.bham.ac.uk"},
												 {303, "314", "MGL@cs.bham.ac.uk"}};
			
			for(int i = 0; i < realContacts.length; i++)
			{
				this.insertStmt.setInt(1,(Integer)realContacts[i][0]);
				this.insertStmt.setString(2,(String)realContacts[i][1]);
				this.insertStmt.setString(3,(String)realContacts[i][2]);
				this.insertStmt.executeUpdate();
			}
			
		}
		catch(SQLException e)
		{
			System.err.println("Error Creating Real Lecturer Data. Please refer to the stack trace for more info: ");
			e.printStackTrace();
			this.closeDataBase(-3);
		}
	}
	
	/**creates various tutor groups based upon the real student/lecturer data, not the synthetic stuff
	 * 
	 */
	private void createRealTutorGroups()
	{
		try
		{
			this.insertStmt = this.getConnection().prepareStatement("INSERT INTO Tutor VALUES (?,?)");//inserting to tutor table
			int[][] realTutors = new int[][]{{1001, 301},
											   {1002, 301},
											   {1003, 301},
											   {1004, 302},
											   {1005, 301},
											   {1006, 302},
											   {1007, 302},
											   {1008, 303},
											   {1009, 303},
											   {1010, 301},
											   {1011, 302},
											   {1012, 303},
											   {1013, 303},
											   {1014, 301},
											   {1015, 301}};
			
			for(int i = 0; i < realTutors.length; i++)
			{
				this.insertStmt.setInt(1,realTutors[i][0]);
				this.insertStmt.setInt(2,realTutors[i][1]);
				this.insertStmt.executeUpdate();
			}
		}
		catch(SQLException e)
		{
			System.err.println("Error Creating Real Tutor Groups. Please refer to the stack trace for more info: ");
			e.printStackTrace();
			this.closeDataBase(-3);
		}
	}
	
	/**this will run all of the methods required to populate the database
	 * 
	 */
	public void run()
	{
		this.createTitles();
		this.createRegistrationTypes();
		this.createSyntheticStudents();
		this.createSyntheticLecturers();
		this.createSyntheticTutorRelations();
		this.createRealStudents();
		this.createRealLecturers();
		this.createRealTutorGroups();
		
		try//finally going to close this prepared statement now I'm finally finished with it
		{
			this.insertStmt.close();
		}
		catch(SQLException e)
		{
			System.err.println("Error Closing Prepared Statement. Please refer to the stack trace for more information: ");
			e.printStackTrace();
			this.closeDataBase(-3);
		}
	}
}
