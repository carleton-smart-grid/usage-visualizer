/**
*Class:             UsageSample.java
*Project:          	Usage Visualizer
*Author:            Jason Van Kerkhoven
*Date of Update:    26/10/2017
*Version:           1.0.0
*
*Purpose:           Glorified struct.
*					Hold two values to make writing algorithms easier.
*					
* 
*Update Log			v1.0.0
*						- null
*/
package dbvis.datatypes;



//generic imports
import java.util.Date;



public class UsageSample 
{
	//declaring public instance constants
	private Date date;
	private double usage;
	
	
	//generic constructor
	public UsageSample(Date date, double usage)
	{
		this.date = date;
		this.usage = usage;
	}
	
	
	//generic getters and setters
	public Date getDate()
	{
		return date;
	}
	public double getUsage()
	{
		return usage;
	}
	public void setDate(Date date)
	{
		this.date = date;
	}
	public void setUsage(double usage)
	{
		this.usage = usage;
	}
	
	
	@Override
	//exactly what it says on the tin
	public boolean equals(Object o)
	{
		if (o instanceof UsageSample)
		{
			UsageSample s = (UsageSample)o;
			return (this.date.equals(s.date) && this.usage == s.usage);
		}
		else
		{
			return false;
		}
	}
}
