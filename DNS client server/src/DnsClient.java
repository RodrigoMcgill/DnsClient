/*
 * @Author: Rodrigo M , Katwar Be
 * DNS client server
 * Network lab.................
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class DnsClient {

	private static int port = 80;
	private static byte[] sendData;
	private static  byte [] receiveData;
	private static String  targetServer = "some ip address";
	public static void main(String[] args) throws Exception {
		
		try{
		//creates one datagram packet for this client
		DatagramSocket clientSocket = new DatagramSocket();
		
		//stores an ip address of choice --> 132.206.85.12 for McGill DN server    or ---> 8.8.8.8 for Googl DNS server
		byte[] ipAddr = new byte[] {8,8,8,8};
		
		//converts byte[] ipAddr into an ip object	
		InetAddress addr =  InetAddress.getByAddress(ipAddr);
		
		//creates both the send and receive entry of the packet
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr, port);
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		
		
		//terminates socket
		clientSocket.close();
		}catch (Exception e){
			System.out.println(e);
		}
	}

}
