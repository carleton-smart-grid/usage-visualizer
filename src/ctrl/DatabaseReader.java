package ctrl;



import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import datatypes.UsageSeries;



public class DatabaseReader 
{
	//declaring static class constants
	public static final int DEFAULT_MAX_SAMPLES = 100;
	public static final String[] COLUMN_NAMES = {"date", "time", "house_id", "usage"};
	public static final String TABLE_NAME = "usages";
	
	//declaring used queries
	private static final String FIND_ALL_HOUSE_IDS = "SELECT DISTINCT " + 
													COLUMN_NAMES[2] + 
													" FROM " + TABLE_NAME;
	
	//declaring instance constants
	public final int MAX_SAMPLES;
	
	//declaring instance variables
	private Connection connection;
	
	
	//default constructor
	public DatabaseReader(String dbPath) throws FileNotFoundException, SQLException
	{
		this(dbPath, DatabaseReader.DEFAULT_MAX_SAMPLES);
	}
	
	//full constructor
	public DatabaseReader(String dbPath, int datapoints) throws FileNotFoundException, SQLException
	{
		File db = new File(dbPath);
		if (db.exists())
		{
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
			connection.setAutoCommit(false);
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
			connection.close();
		}
		catch (SQLException e){}
	}
	
	
	/*
	 * get the current house_ids in use
	 * returns an array of the house_ids found, empty array if no entries
	 */
	public Integer[] getDistinctIds() throws SQLException
	{
		//check database for all distinct house_ids
		Statement session = connection.createStatement();
		ResultSet results = session.executeQuery(FIND_ALL_HOUSE_IDS);
		
		//parse all IDs out
		ArrayList<Integer> distinctIds = new ArrayList<Integer>();
		while(results.next())
		{
			distinctIds.add(results.getInt(COLUMN_NAMES[2]));
		}
		
		//close active objects
		session.close();
		results.close();
		
		return distinctIds.toArray(new Integer[0]);
	}
	
	/*
	 * read up to n samples of usage per house_id
	 * COSTLY OPERATION AS n INCREASES IN SIZE
	 */
	private XYSeriesCollection getNSamples(int n) throws SQLException
	{
		//get all ids and prep container for return
		Integer[] distinctIds = this.getDistinctIds();
		XYSeriesCollection returnable = new XYSeriesCollection();
		
		//open database input statement for queries
		Statement s = connection.createStatement();
		
		//get most recent MAX_SAMPLES entries for each house_id
		for (int i=0; i<distinctIds.length; i++)
		{
			//create SQL query string and query database
			String query = "SELECT " + 
							COLUMN_NAMES[0] + "," + COLUMN_NAMES[1] + "," + COLUMN_NAMES[3] + 
							" FROM " + TABLE_NAME +
							" WHERE " + COLUMN_NAMES[2] + "=" + distinctIds[i] +
							" ORDER BY " + COLUMN_NAMES[0] + " DESC, " +
							COLUMN_NAMES[1] + " DESC " +
							"LIMIT " + n;
			ResultSet results = s.executeQuery(query);
			
			//parse results
			UsageSeries data = new UsageSeries("id=" + distinctIds[i], false, true, distinctIds[i]);
			while (results.next())
			{
				//parse row data into DataPoint
				String sDate = results.getString(COLUMN_NAMES[0]);
				String sTime = results.getString(COLUMN_NAMES[1]);
				double usage = results.getDouble(COLUMN_NAMES[3]);
				
				String[] dateComp = sDate.split("-");
				String[] timeComp = sTime.split(":");

				Date date = new Date(Integer.parseInt(dateComp[0])-1900,
									Integer.parseInt(dateComp[1]),
									Integer.parseInt(dateComp[2]),
									Integer.parseInt(timeComp[0]),
									Integer.parseInt(timeComp[1]));
				
				//add to series
				data.add(date,usage);
			}
			
			//add returnable
			returnable.addSeries(data);
			results.close();
		}
		
		//close active resources and return
		s.close();
		return returnable;
	}
	
	
	/*
	 * read up to MAX_SAMPLES of usages values per house_id and 
	 * Default max samples is 100, so if 10 house's will return 1000 usage values
	 * 
	 * THIS IS A COSTLY OPERATION AND SHOULD BE USED SPARINGLY
	 */
	public XYSeriesCollection getMaxSamples() throws SQLException
	{
		return this.getNSamples(MAX_SAMPLES);
	}
	
	
	/*
	 * get the most recent data for each house_id
	 * returns an array of n DataPoints, where n is the number of distinct house_ids
	 * in the database
	 */
	public XYSeriesCollection getMostRecent() throws SQLException
	{
		return this.getNSamples(1);
	}

	
	//TODO DELETE
	public static void main(String[] args) throws FileNotFoundException, SQLException 
	{
		DatabaseReader dbr = new DatabaseReader("dat/testdatabase.db", 5);
		dbr.getMaxSamples();
	}
}
