package ctrl;



//import external libraries
import java.io.FileNotFoundException;
import java.sql.*;

//import packages
import ui.UsageView;
import datatypes.UsageSeries;



public class UsageControl implements Runnable
{
	//declaring local instance variables
	private DatabaseReader db;
	private UsageView ui;
	private UsageSeries[] samples;
	private double updatePeriod;
	
	
	//generic constructor
	public UsageControl(String dbPath, double updatePeriod) throws FileNotFoundException, SQLException
	{
		db = new DatabaseReader(dbPath, 5);
		ui = new UsageView();
		samples = null;
		this.updatePeriod = updatePeriod;
	}
	

	@Override
	//exactly what it says on the tin
	public void run() 
	{	
		try
		{
			//get initial set of datapoints
			System.out.println("Querying maximum <" + db.MAX_SAMPLES + "> most-recent DataPoints per <" + db.getDistinctIds().length + "> distinct id(s)");
			samples = db.getMaxSamples();
			for(UsageSeries series : samples)
			{
				System.out.println("Found " + String.format("%03d",series.getSize()) + "/" + series.getMaxDataPoints() + " DataPoints for ID=" + series.getHouseId());
			}
			System.out.println("Done!");
			
			//enable gui
			ui.setVisible(true);
			
			//update displayed graph
			//TODO
			
			//setup update frequency for graph
			
		}
		catch (SQLException e)
		{
			//TODO handle this
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
