package datatypes;

import java.util.Date;

public class UsageSample 
{
	//declaring public instance constants
	public final Date date;
	public final double usage;
	
	
	//generic constructor
	public UsageSample(Date date, double usage)
	{
		this.date = date;
		this.usage = usage;
	}
}
