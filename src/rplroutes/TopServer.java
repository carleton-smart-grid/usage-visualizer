package rplroutes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class TopServer
{
	//declaring static constants
	public static final int DEFAULT_PORT = 2034;
	public static final int MAX_PACKET_SIZE = 512;
	
	//declaring local instance variables
	private DatagramSocket socket;
	
	
	//default port constructor
	public TopServer() throws SocketException
	{
		this(DEFAULT_PORT);
	}
	//generic constructor
	public TopServer(int port) throws SocketException
	{
		socket = new DatagramSocket(port);
	}
	
	
	//receive UDP datagram and unpack
	public String[] receive()
	{
		//receive packet
		DatagramPacket packet = new DatagramPacket(new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);
		try 
		{
			socket.receive(packet);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}
		
		//parse packet for data
		String raw = new String(packet.getData());
		String[] dat = raw.split(",");
		
		//return data from packet
		if (dat.length == 3)
		{
			return dat;
		}
		else
		{
			System.out.println("ERROR: invalid number of values in packet (3 expected; " + dat.length + " received)");
			return null;
		}
	}
}
