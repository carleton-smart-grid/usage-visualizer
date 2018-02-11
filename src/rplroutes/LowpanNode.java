package rplroutes;

public class LowpanNode 
{
	//declaring static constants
	public static final int MAX_RANK = 65535;
	public static final int ROOT_RANK = 256;
	
	//declaring local instance constancs
	private final String NAME;
	
	//declaring local instance variables
	private int rank;
	private LowpanNode parent;
	
	
	//null constructor
	public LowpanNode(String name)
	{
		this(name, MAX_RANK, null);
	}
	
	//generic constructor
	public LowpanNode(String name, int rank, LowpanNode parent)
	{
		this.NAME = name;
		this.rank = rank;
		this.parent = parent;
	}
	
	
	//generic setters
	public void setRank(int rank)
	{
		this.rank = rank;
	}
	public void setParent(LowpanNode parent)
	{
		this.parent = parent;
	}
	
	
	//generic getters
	public String getName()
	{
		return NAME;
	}
	public int getRank()
	{
		return rank;
	}
	public LowpanNode getParent()
	{
		return parent;
	}
	public boolean isRoot()
	{
		return (rank == ROOT_RANK);
	}
	
	
	//printable
	@Override
	public String toString()
	{
		return NAME + " @ rank: " + rank + ", parent: " + parent.NAME;
	}
}
