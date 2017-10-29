/**
*Class:             UsageControl.java
*Project:          	Usage Visualizer
*Author:            Jason Van Kerkhoven
*Date of Update:    26/10/2017
*Version:           1.0.1
*
*Purpose:           Main runnable class for Usage Visualizer.
*					Synchronizes everything, and manages updating the graph.
*					Handles any user inputs from UI.
*					
* 
*Update Log			v1.0.1
*						- run method rewritten to use DatabaseReader v1.1.0
*					v1.0.0
*						- null
*/
package ctrl;



//import external libraries
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.List;

import org.sqlite.SQLiteException;

//import packages
import ui.UsageView;
import datatypes.UsageSeries;



public class UsageControl implements Runnable
{
	//declaring static constants
	private static final String TITLE = "Usage Visualizer v1.0.1";
	private static final String GRAPH_TITLE = "Reported Power Usage";
	private static final String X_AXIS = "Date";
	private static final String Y_AXIS = "Power Usage (kWh)";
	
	//declaring local instance variables
	private DatabaseReader db;
	private UsageView ui;
	private long updatePeriod;
	
	
	//generic constructor
	public UsageControl(String dbPath, long updatePeriod) throws FileNotFoundException, SQLException
	{
		db = new DatabaseReader(dbPath);
		ui = new UsageView(false, TITLE);
		this.updatePeriod = updatePeriod;
	}
	

	@Override
	//exactly what it says on the tin
	public void run() 
	{	
		try
		{
			//initialize db
			db.open();
			db.initialize();
			db.close();
			
			//enable gui and setup
			ui.updateLineTab(db.getCollection());
			ui.setVisible(true);
			
			//main loop
			while(true)
			{	
				//check for database updates
				db.open();
				if (db.updateSeries())
				{
					//update graph
					ui.updateLineTab(db.getCollection());
				}
				db.close();
				
				try {
					Thread.sleep(updatePeriod);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	//main
	public static void main(String[] args) 
	{
		if (args.length == 2)
		{
			try 
			{
				UsageControl ctrl = new UsageControl(args[0], Integer.parseInt(args[1])*1000);
				ctrl.run();
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("ERROR: Must call program with arg1=path/database.db arg2=updatePeriod");
		}
	}
}
