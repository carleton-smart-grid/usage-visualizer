package rplroutes;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Set;

public abstract class TopVisualizer 
{
	public static void main(String[] args)
	{
		//initialize server and node mapping
		HashMap<String, LowpanNode> top = new HashMap<String, LowpanNode>();
		TopServer server = null;
		try
		{
			server = new TopServer();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		while (true)
		{
			//wait for data
			String[] data = server.receive();
			if (data != null)
			{
				try
				{
					//parse data
					boolean toAdd = false;
					String name = data[0];
					int rank = Integer.parseInt(data[1]);
					String parentName = data[2];
					
					//check if node already in mapping
					LowpanNode node = null;
					if (top.containsKey(name))
					{
						node = top.get(name);
					}
					else
					{
						toAdd = true;
						node = new LowpanNode(name);
					}
					
					//check if special no-parent case
					if (rank == LowpanNode.MAX_RANK || rank == LowpanNode.ROOT_RANK)
					{
						node.setParent(null);
						node.setRank(rank);
					}
					//check if reported parent is valid
					else
					{
						if (top.containsKey(parentName))
						{
							node.setParent(top.get(parentName));
							node.setRank(rank);
						}
						else
						{
							System.out.println("ERROR: Non-existant parent, dropping packet");
							node = null;
						}
					}
					
					//add new node if non-updated
					if (toAdd && node != null)
					{
						top.put(node.getName(), node);
					}
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				//TODO handle error
			}
			
			//update display
			Set<String> keys = top.keySet();
			for (String key : keys)
			{
				LowpanNode node = top.get(key);
			}
		}
	}
}
