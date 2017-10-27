/**
*Class:             DatabaseReader.java
*Project:          	Usage Visualizer
*Author:            Jason Van Kerkhoven
*Date of Update:    26/10/2017
*Version:           1.1.0
*
*Purpose:           Read information from an SQLite3 database configured to 
*					store usage data in a single table.
*					
*					Keep database updated if run on own thread.
* 
*Update Log			v1.1.0
*						- added method to check for update on IDS
*						- added map datatype to hold most-recent samples per house
*						- removed getMostRecent() method
*						- restructured initialize() method into self and method getSamples()
*						- added verbose-ness
*						- added method to update only series that need updating
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import datatypes.UsageSample;
//import local packages
import datatypes.UsageSeries;



public class DatabaseReader
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
	public final boolean VERBOSE;
	
	//declaring instance variables
	private String dbPath;
	private Connection connection;
	private XYSeriesCollection series;
	private TreeMap<Integer, UsageSample> lastReadSamples;
	
	
	//v1.1.0 constructor
	public DatabaseReader(String dbPath) throws FileNotFoundException, SQLException
	{
		this(dbPath, UsageSeries.DEFAULT_MAX_ITEMS, true);
	}
	//full constructor
	public DatabaseReader(String dbPath, int datapoints, boolean verbose) throws FileNotFoundException, SQLException
	{
		File db = new File(dbPath);
		if (db.exists())
		{
			this.dbPath = dbPath;
			this.series = new XYSeriesCollection();
			this.lastReadSamples = new TreeMap<Integer, UsageSample>();
			
			this.MAX_SAMPLES = datapoints;
			this.VERBOSE = verbose;
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
		catch (SQLException e){}	//if this exception occurs ???
	}
	
	
	//open the connection to db
	public void open() throws SQLException
	{
		//connection
		connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
		connection.setAutoCommit(false);
	}
	
	
	/*
	 * get the current house_ids in use
	 * returns an array of the house_ids found, empty array if no entries
	 */
	public int[] getDistinctIds() throws SQLException
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
		
		//return
		Integer[] arr = distinctIds.toArray(new Integer[0]);
		return Arrays.stream(arr).mapToInt(Integer::intValue).toArray();
	}
	
	
	//get total number of distinct IDs
	public int getSeriesCount()
	{
		return series.getSeriesCount();
	}
	
	
	//get all series
	public List<UsageSeries> getSeries()
	{
		return series.getSeries();
	}
	
	
	//get collection
	public XYSeriesCollection getCollection()
	{
		return series;
	}
	
	
	/*
	 * read up to MAX_SAMPLES of usages values per house_id and 
	 * Default max samples is 100, so if 10 house's will load 1000 usage values
	 * 
	 * THIS IS A VERY COSTLY OPERATION AND SHOULD BE USED SPARINGLY
	 */
	public void initialize() throws SQLException
	{
		if (VERBOSE)
		{
			System.out.println("Begin init...");
			System.out.println("Querying maximum <" + this.MAX_SAMPLES + "> most-recent DataPoints per distinct id(s)...");
		}
		
		//get all ids and prep series
		int[] distinctIds = this.getDistinctIds();
		series = new XYSeriesCollection();
		
		//get most recent MAX_SAMPLES entries for each house_id
		for (int i=0; i<distinctIds.length; i++)
		{
			//get most recent values from target id
			UsageSeries samples = getMostRecentSamples(distinctIds[i], MAX_SAMPLES);
			//add returnable
			series.addSeries(samples);
		}
		
		if (VERBOSE)
		{
			List<UsageSeries> samples = this.getSeries();
			for(UsageSeries series : samples)
			{
				System.out.println("Found " + 
									series.getItemCount() + "/" + 
									series.getMaximumItemCount() + 
									" DataPoints for ID=" + series.getHouseId());
				System.out.println(series.toString());
			}
			System.out.println("Init complete!\n");
		}
		
		this.checkUpdates(distinctIds);
	}
	
	
	/*
	 * update a particular house_ids sample series
	 * TODO document this but better
	 */
	public UsageSeries getMostRecentSamples(int houseId, int quantity) throws SQLException
	{
		//open database input statement for queries
		Statement s = connection.createStatement();
		
		//create SQL query string and query database
		String query = "SELECT " + 
						COLUMN_NAMES[0] + "," + COLUMN_NAMES[1] + "," + COLUMN_NAMES[3] + 
						" FROM " + TABLE_NAME +
						" WHERE " + COLUMN_NAMES[2] + "=" + houseId +
						" ORDER BY " + COLUMN_NAMES[0] + " DESC, " +
						COLUMN_NAMES[1] + " DESC " +
						"LIMIT " + MAX_SAMPLES;
		ResultSet results = s.executeQuery(query);
		//parse results
		UsageSeries data = new UsageSeries("id=" + houseId, false, true, houseId);
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
		
		//close reasources and return
		s.close();
		results.close();
		return data;
	}
	
	
	
	/*
	 * check to see if database has updated since last read
	 * return an array of
	 */
	public boolean[] checkUpdates(int[] ids) throws SQLException
	{
		//open database input statement for queries and prep returnable
		Statement s = connection.createStatement();
		boolean[] updated = new boolean[ids.length];
		
		for (int i=0; i<ids.length; i++)
		{
			//create SQL query string and query database
			String query = "SELECT " + 
							COLUMN_NAMES[0] + "," + COLUMN_NAMES[1] + "," + COLUMN_NAMES[3] + 
							" FROM " + TABLE_NAME +
							" WHERE " + COLUMN_NAMES[2] + "=" + ids[i] +
							" ORDER BY " + COLUMN_NAMES[0] + " DESC, " +
							COLUMN_NAMES[1] + " DESC " +
							"LIMIT 1";
			ResultSet results = s.executeQuery(query);
			
			//parse results and return
			results.next();
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
			
			//check if newly read value differs from old
			UsageSample read = new UsageSample(date, usage);
			UsageSample prev = lastReadSamples.get(ids[i]);
			
			//first instance of sample for this house ID found
			if (prev == null)
			{
				lastReadSamples.put(ids[i], read);
				updated[i] = true;
			}
			//latest sample has not changed
			else if (read.equals(prev))
			{
				updated[i] = false;
			}
			//latest sample has changed
			else
			{
				updated[i] = true;
				prev.setDate(read.getDate());
				prev.setUsage(read.getUsage());
			}
		}
		return updated;
	}


	/*
	 * check if series needs to be updated
	 * will return true if ANY series in collection updated
	 */
	public boolean updateSeries() throws SQLException
	{
		boolean r = false;
		int[] ids = this.getDistinctIds();
		
		if (VERBOSE) 
			System.out.println("Checking database for updates...");
		
		boolean[] updated = this.checkUpdates(ids);
		for (int i=0; i<ids.length; i++)
		{
			if (updated[i])
			{
				if (VERBOSE)
					System.out.println("ID=" + ids[i] + " -- New sample found ");
				
				boolean swapFlag = false;
				UsageSeries updatedSeries = this.getMostRecentSamples(ids[i], MAX_SAMPLES);
				for (int c=0; c<series.getSeriesCount(); c++)
				{
					UsageSeries target = (UsageSeries)series.getSeries(c);
					if (target.getHouseId() == ids[i]);
					{
						series.removeSeries(target);
						series.addSeries(updatedSeries);
						swapFlag = true;
						r = true;
						break;
					}
				}
				if (!swapFlag)
				{
					series.addSeries(updatedSeries);
					r = true;
				}
			}
		}
		
		if (VERBOSE)
			System.out.println("Update complete!\n");
		
		return r;
	}
}
