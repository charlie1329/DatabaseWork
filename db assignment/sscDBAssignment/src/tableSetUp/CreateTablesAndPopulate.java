package tableSetUp;

import java.sql.*;

/**this class will set up the database clean and then populate it with a combination of both real and synthetic data
 * 
 * @author Charlie Street
 *
 */
public class CreateTablesAndPopulate implements Runnable
{
	private String dbName;
	private Connection dbConn;
	private CreateDataBase creator;
	private PopulateDataBase populator;
	
	/** constructor initialises everything essentially
	 * 
	 * @param dbName the database path
	 */
	public CreateTablesAndPopulate(String dbName)
	{
		this.dbName = dbName;
		this.dbConn = null;//will be properly set in another method
		this.creator = null;
		this.populator = null;
	}
	
	/**method runs the creation and population of the database
	 * 
	 */
	public void run()
	{
		this.creator = new CreateDataBase(this.dbName);
		this.creator.run();
		this.dbConn = creator.getConnection();
		this.populator = new PopulateDataBase(this.dbConn);
		this.populator.run();
		this.creator.closeDataBase(1);//closing the database
	}
	
	public static void main(String[] args)
	{
		CreateTablesAndPopulate create = new CreateTablesAndPopulate("jdbc:postgresql://dbteach2.cs.bham.ac.uk/cxs412");
		create.run();
	}
}
