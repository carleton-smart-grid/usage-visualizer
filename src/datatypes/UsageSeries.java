package datatypes;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

public class UsageSeries extends XYSeries
{
	//declaring class constants
	public static final int DEFAULT_MAX_ITEMS = 100;
	
	//declaring local instance variables
	private final int houseId;
	
	
	public UsageSeries(Comparable key, boolean autoSort, boolean allowDuplicateXValues,int id) 
	{
		super(key, autoSort, allowDuplicateXValues);
		this.houseId = id;
		this.setMaximumItemCount(DEFAULT_MAX_ITEMS);
	}
	
	
	public int getHouseId()
	{
		return houseId;
	}
	
	public void add(Date date, double usage)
	{
		this.add(new XYDataItem(new Long(date.getTime()), new Double(usage)));
	}
	
	@Override
	public String toString()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		String s = "ID=" + houseId + "values={";
		for (Object point : this.data)
		{
			XYDataItem item = (XYDataItem)point;
			s += "[" + format.format(new Date(item.getX().longValue())) + ","
					+ item.getYValue() + "], ";
		}
		return s;
	}

}
