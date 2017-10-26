/**
*Class:             DatabaseReader.java
*Project:          	Usage Visualizer
*Author:            Jason Van Kerkhoven
*Date of Update:    26/10/2017
*Version:           2.0.0
*
*Purpose:           Read information from an SQLite3 database configured to 
*					store usage data in a single table.
*					
*					Keep database updated if run on own thread.
* 
*Update Log			v2.0.0
*						-
*					v1.0.0
*						- null
*/
package ctrl;



//import external libraries
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jfree.data.xy.XYSeriesCollection;

//import local packages
import datatypes.UsageSeries;



public class DatabaseReader implements Runnable
{
	//declaring static class constants
	public static final String[] COLUMN_NAMES = {"date", "time", "house_id", "usage"};
	public static final String TABLE_NAME = "usages";
	
	//declaring used queries
	private static final String FIND_ALL_HOUSE_IDS = "SELECT DISTINCT " + 
													COLUMN_NAMES[2] + 
													" FROM " + TABLE_NAME;
	
	//declaring instance constants
	public final int MAX_SAMPLES;
	public final boolean DYNAMIC_IDS;
	
	//declaring instance variables
	private String dbPath;
	private boolean lockFlag;
	private Connection connection;
	private XYSeriesCollection series;
	private int[] liveIds;
	
	
	//v2.0.0 constructor
	public DatabaseReader(String dbPath, boolean dynamicIds) throws FileNotFoundException, SQLException
	{
		this(dbPath, UsageSeries.DEFAULT_MAX_ITEMS, dynamicIds);
	}
	//v1.0.0 constructor
	public DatabaseReader(String dbPath, int datapoints) throws FileNotFoundException, SQLException
	{
		this(dbPath, UsageSeries.DEFAULT_MAX_ITEMS, true);
	}	
	//full constructor
	public DatabaseReader(String dbPath, int datapoints, boolean dynamicIds) throws FileNotFoundException, SQLException
	{
		File db = new File(dbPath);
		if (db.exists())
		{
			this.dbPath = dbPath;
			this.lockFlag = false;
			this.series = new XYSeriesCollection();
			this.liveIds = new int[0];
			
			this.MAX_SAMPLES = datapoints;
			this.DYNAMIC_IDS = dynamicIds;
		}
		else
		{
			throw new FileNotFoundException("Cannot find file at path \"" + dbPath + "\"");
		}
	}
	
	
	//close connection to db
	public synchronized void close()
	{
		try
		{
			//wait for lock
			while (lockFlag)
			{
				this.wait();
			}
			
			connection.close();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		catch (SQLException e){}	//if this exception occurs ???
	}
	
	
	//open the connection to db
	public synchronized void open() throws SQLException
	{
		try
		{
			//wait for lock
			while (lockFlag)
			{
				this.wait();
			}
			
			//connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
			connection.setAutoCommit(false);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	/*
	 * read up to n samples of usage per house_id
	 * COSTLY OPERATION AS n INCREASES IN SIZE
	 */
	private synchronized XYSeriesCollection getNSamples(int n) throws SQLException
	{
		//wait for lock
		while (lockFlag)
		{
			try
			{
				this.wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				System.exit(0);
			}
		}
		
		//get all ids and prep container for return
		Integer[] distinctIds = this.getDistinctIds();
		lockFlag = true;
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
		lockFlag = false;
		this.notifyAll();
		return returnable;
	}
	
	
	/*
	 * get the current house_ids in use
	 * returns an array of the house_ids found, empty array if no entries
	 */
	public synchronized Integer[] getDistinctIds() throws SQLException
	{
		//wait for lock
		while (lockFlag)		//TODO DEADLOCK HERE
		{
			try
			{
				this.wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				System.exit(0);
			}
		}
		lockFlag = true;
		
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
		
		lockFlag = false;
		this.notifyAll();
		return distinctIds.toArray(new Integer[0]);
	}
	
	
	//get total number of distinct IDs
	public synchronized int getSeriesCount()
	{
		return series.getSeriesCount();
	}
	
	
	//get all series
	public synchronized List<UsageSeries> getSeries()
	{
		return series.getSeries();
	}
	
	
	//get collection
	public synchronized XYSeriesCollection getCollection()
	{
		return series;
	}
	
	/*
	 * read up to MAX_SAMPLES of usages values per house_id and 
	 * Default max samples is 100, so if 10 house's will load 1000 usage values
	 * 
	 * THIS IS A !_VERY_! COSTLY OPERATION AND SHOULD BE USED SPARINGLY
	 */
	public void initialize() throws SQLException
	{
		series = getNSamples(MAX_SAMPLES);
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
	
	
	/*
	 * check to see if database has updated for all house_ids
	 * return an array of
	 */
	public boolean[] checkUpdates(int[] ids)
	{
		boolean[] updated = new boolean[ids.length];
		for (int i=0; i<ids.length; i++)
		{
			//get most-recent value for ID
			
		}
	}

	
	@Override
	/*
	 * periodically check database for new entries to add to series
	 */
	public void run()
	{
		try 
		{
			this.open();
			this.initialize();
			
			
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
