package ctrl;



//import external libraries
import java.io.FileNotFoundException;
import java.sql.*;

//import packages
import ui.UsageView;



public class UsageControl implements Runnable
{
	//declaring local instance variables
	DatabaseReader db;
	UsageView ui;
	
	
	//generic constructor
	public UsageControl(String dbPath) throws FileNotFoundException, SQLException
	{
		db = new DatabaseReader(dbPath);
		ui = new UsageView();
	}
	

	@Override
	//exactly what it says on the tin
	public void run() 
	{
		ui.setVisible(true);
	}
	
	
	//main
	public static void main(String[] args) 
	{
		if (args.length == 1)
		{
			try 
			{
				UsageControl ctrl = new UsageControl(args[0]);
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
			System.out.println("ERROR: Must call program with argument 1 as path/database.db");
		}
	}
}
