package datatypes;

import java.text.SimpleDateFormat;
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
	
	
	@Override
	//check if equals
	public boolean equals(Object o)
	{
		if (o instanceof DataPoint)
		{
			DataPoint p = (DataPoint)o;
			return (this.date.equals(p.date));
		}
		else
		{
			return false;
		}
	}
	
	
	@Override
	//print nicely
	public String toString()
	{
		return (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date) + ", " + usage);
	}
}
