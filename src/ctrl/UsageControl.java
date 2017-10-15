/**
*Class:             UsageControl.java
*Project:          	Usage Visualizer
*Author:            Jason Van Kerkhoven
*Date of Update:    15/10/2017
*Version:           1.0.0
*
*Purpose:           Main runnable class for Usage Visualizer.
*					Synchronizes everything, and manages updating the graph.
*					Handles any user inputs from UI.
*					
* 
*Update Log			v1.0.0
*						- null
*/
package ctrl;



//import external libraries
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.List;

//import packages
import ui.UsageView;
import datatypes.UsageSeries;



public class UsageControl implements Runnable
{
	//declaring static constants
	private static final String GRAPH_TITLE = "Reported Power Usage";
	private static final String X_AXIS = "Date";
	private static final String Y_AXIS = "Power Usage (kWh)";
	
	//declaring local instance variables
	private DatabaseReader db;
	private UsageView ui;
	private double updatePeriod;
	
	
	//generic constructor
	public UsageControl(String dbPath, double updatePeriod) throws FileNotFoundException, SQLException
	{
		db = new DatabaseReader(dbPath, 100);
		ui = new UsageView(false);
		this.updatePeriod = updatePeriod;
	}
	

	@Override
	//exactly what it says on the tin
	public void run() 
	{	
		try
		{
			//get initial set of datapoints
			System.out.println("Querying maximum <" + db.MAX_SAMPLES + "> most-recent DataPoints per distinct id(s)");
			
			db.open();
			db.initialize();
			db.close();
			
			List<UsageSeries> samples = db.getSeries();
			for(UsageSeries series : samples)
			{
				System.out.println("Found " + 
									series.getItemCount() + "/" + 
									series.getMaximumItemCount() + 
									" DataPoints for ID=" + series.getHouseId());
				System.out.println(series.toString());
			}
			System.out.println("Done!");
			
			//enable gui
			ui.displayDateLineChart(db.getCollection(), GRAPH_TITLE, X_AXIS, Y_AXIS);
			ui.setVisible(true);
			
			//setup update frequency for graph  TODO THIS IS BAD FIX THIS GOOD FOR DEMO ONLY
			while(true)
			{
				Thread.sleep(500);
				
				db.open();			//TODO DO NOT INIT EACH TIME BAD FOR EFFICIENCY WHY STOP
				db.initialize();
				db.close();
				
				System.out.println("updated!");
				ui.displayDateLineChart(db.getCollection(), GRAPH_TITLE, X_AXIS, Y_AXIS);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
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
				UsageControl ctrl = new UsageControl(args[0], Double.parseDouble(args[1]));
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
