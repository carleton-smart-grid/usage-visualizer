package datatypes;

import java.util.Date;

public class DataPoint 
{
	//declaring local instance constants
	public final Date date;
	public final double usage;
	
	//generic constructor
	public DataPoint(Date date, double usage)
	{
		this.date = date;
		this.usage = usage;
	}
}
