package ctrl;



//import external libraries
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

//import packages
import ui.UsageView;
import datatypes.UsageSeries;



public class UsageControl implements Runnable
{
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
			db.initialize();
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
			ui.displayDateLineChart(db.getCollection(), "Reported Power Usage", "Date", "Power Usage (kWh)");
			ui.setVisible(true);
			ui.setInfoText("hello");
			
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
