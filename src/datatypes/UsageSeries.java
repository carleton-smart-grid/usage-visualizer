package datatypes;

import java.util.LinkedList;



public class UsageSeries 
{
	//declaring local instance variables
	private int maxHold;
	private int id;
	private LinkedList<DataPoint> data;
	
	
	//generic constructor
	public UsageSeries(int houseId, int maxHold)
	{
		this.id = houseId;
		this.maxHold = maxHold;
		data = new LinkedList<DataPoint>();
	}
	
	
	//generic getters
	public int getHouseId()
	{
		return id;
	}
	public int getMaxDataPoints()
	{
		return maxHold;
	}
	public LinkedList<DataPoint> getDataSeries()
	{
		return data;		// note that modifying data from pointer outside 
							// of instance causes bad things
	}
	
	
	//generic setters
	public void setHouseId(int id)
	{
		this.id = id;
	}
	public void setMaxHold(int max)
	{
		this.maxHold = max;
		while (data.size() > maxHold)
		{
			data.removeFirst();
		}
	}
	
	
	/*
	 * add a new entry to data map
	 * if map is full, remove oldest entry
	 */
	public void add(DataPoint point)
	{
		if (point != null)
		{
			if(data.size() >= maxHold)
			{
				data.removeFirst();
			}
			data.add(point);
		}
	}
	
	
	//clear data
	public void clear()
	{
		data.clear();
	}
	
	
	//return total number of points
	public int getSize()
	{
		return data.size();
	}
	

	//return if full
	public boolean isFull()
	{
		return (data.size() >= maxHold);
	}
	
	
	@Override
	//return string (mostly for debug
	public String toString()
	{
		String s = "";
		for (DataPoint dat : data)
		{
			s += "[" + dat.date.toString() + "," + dat.usage + "], ";
		}
		
		return "id: " + id + ", max: " + maxHold + ", data:{" + s + "}";
	}
}
