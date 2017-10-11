package ctrl;



import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;



public class DatabaseReader 
{
	//declaring static class constants
	public static final int DEFAULT_DATA_POINTS_PER_HOUSE = 100;
	public static final String[] COLUMN_NAMES = {"date", "time", "house_id", "usage"};
	public static final String TABLE_NAME = "usages";
	public static final int COLUMN_DATE_INDEX 		= 1;	// ARRAYS START AT 0 
	public static final int COLUMN_TIME_INDEX 		= 2;	// REEEEEEEEEE
	public static final int COLUMN_HOUSEID_INDEX 	= 3;
	public static final int COLUMN_USAGE_INDEX 		= 4;
	
	//declaring used queries
	private static final String FIND_ALL_HOUSE_IDS = "SELECT DISTINCT " + 
													COLUMN_NAMES[2] + 
													" FROM " + TABLE_NAME;
	
	//declaring local instance constants
	private final int MAX_SAMPLES;
	
	//declaring instance variables
	private Connection dbConnect;
	
	
	//default constructor
	public DatabaseReader(String dbPath) throws FileNotFoundException, SQLException
	{
		this(dbPath, DatabaseReader.DEFAULT_DATA_POINTS_PER_HOUSE);
	}
	
	//full constructor
	public DatabaseReader(String dbPath, int datapoints) throws FileNotFoundException, SQLException
	{
		File db = new File(dbPath);
		if (db.exists())
		{
			dbConnect = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
			dbConnect.setAutoCommit(false);
			this.MAX_SAMPLES = datapoints;
		}
		else
		{
			throw new FileNotFoundException("Cannot find file at path \"" + dbPath + "\"");
		}
	}
	
	
	//close connection to db
	public void close()
	{
		try
		{
			dbConnect.close();
		}
		catch (SQLException e){}
	}
	
	
	/*
	 * read up to MAX_SAMPLES of usages values per house_id and 
	 * Default max samples is 100, so if 10 house's will return 1000 usage values
	 * 
	 * THIS IS A COSTLY OPERATION AND SHOULD BE USED SPARINGLY
	 */
	public ResultSet select() throws SQLException
	{
		//check database for all distinct house_ids
		Statement session = dbConnect.createStatement();
		ResultSet results = session.executeQuery(FIND_ALL_HOUSE_IDS);
		
		//parse all IDs out
		ArrayList<Integer> distinctIds = new ArrayList<Integer>();
		while(results.next())
		{
			distinctIds.add(results.getInt(COLUMN_NAMES[2]));
		}
		
		//get most recent MAX_SAMPLES entries for each house_id
		for (Integer i : distinctIds)
		{
			//create SQL query string
			String query = "SELECT " + 
							COLUMN_NAMES[0] + "," + COLUMN_NAMES[1] + "," + COLUMN_NAMES[3] + 
							" FROM " + TABLE_NAME +
							" WHERE " + COLUMN_NAMES[2] + "=" + i +
							" ORDER BY " + COLUMN_NAMES[0] + " DESC, " +
							COLUMN_NAMES[1] + " DESC " +
							"LIMIT " + MAX_SAMPLES;
			results = session.executeQuery(query);
			System.out.println(query);
		}
		
		return null;
	}

	
	//TODO DELETE
	public static void main(String[] args) throws FileNotFoundException, SQLException 
	{
		DatabaseReader dbr = new DatabaseReader("dat/testdatabase.db", 5);
		dbr.select();
	}
}
