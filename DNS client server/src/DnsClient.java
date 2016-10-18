/*
 * @Author: Rodrigo M , Katwar Be
 * DNS client server
 * Network lab.................
 */

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public class DnsClient {

	private static int port = 53;
	private static  byte [] receiveData = new byte[500];
	private static String timeOutValue;
	private static String maxRetriesValue;
	private static String typeQuerry;
	private static String ipAddress;
	private static String domainName;
	private static boolean error = false;
	private static int maxUDPSize = 512; //bytes
	
	
	//Main method
	public static void main(String[] args) throws Exception {
		DnsClient dnsc = new DnsClient();
		dnsc.run(args);
	}
	
	
	public void run(String[] args) throws IOException{
    
		String inputUser = "";
			for(int arg = 0; arg < args.length; arg++){
				inputUser += args[arg] + " ";
			}
		
			if(inputUser == ""){
				System.out.println("No arguments provided");
				System.exit(0);
				}
			
			else{		
				acquiringParameters(inputUser); 
					//if in any of the previous methods, something went wrong that does not meet the format, then an error will occur and program will not continue
				}
			try{
								
								//dnsPacketStructure dnsPS = new dnsPacketStructure(typeQuerry,name);
			
								System.out.println("checkpoint good");
						
								InetAddress ip = InetAddress.getByName(ipAddress);
			
								DatagramSocket clientSocket = new DatagramSocket();
						}catch(Exception e){
							
						
					}
				
	}

	
	public static void acquiringParameters(String inputUser) throws IOException{
		
		inputCommandLine icl = new inputCommandLine();
		
		try{
			//extracts the time out value ,(optional) default: 5
			timeOutValue = icl.TimeOutInputMapping(inputUser);
			System.out.println("time out value " + timeOutValue);

			//extracts the max retries values, (optional) default: 3
			maxRetriesValue = icl.maxRetriesInputMapping(inputUser);
			System.out.println("max out value : " + maxRetriesValue);

			//extracts type of Querry (optional) default : Type A
			typeQuerry = icl.typeQuerryInputMapping(inputUser);
			System.out.println("type of Querry : " + typeQuerry);

			//extracts ip address (mandatory)
			ipAddress = icl.ipAddressInputMapping(inputUser)[0];
			System.out.println("ip address : " + ipAddress);

			//extracts host domain (mandatory)
			domainName = icl.ipAddressInputMapping(inputUser)[1];
			System.out.println("name of website is : " + domainName );
			
			}catch(Exception e){
				System.out.println("something went wrong in extrating the values");
				error = false;
		}
		
	    if(icl.getError()){
	    	error = true;
	    	System.out.println(icl.getMessageError());
	    	System.exit(0);
		}
	     else{
	     System.out.println("checkpoint reached");
	     acquiringPacket();
	     }
    }
	
	public static void acquiringPacket() throws IOException{
		
		dnsPacketStructure dnsPS = new dnsPacketStructure(typeQuerry, domainName);
	    
		int DNSAnswerSize = maxUDPSize - dnsPS.getdnsPacketHeader().length - dnsPS.getByteBuffer().position() ;
		
		System.out.println("size of DNSAnswerSize is "  +DNSAnswerSize);
		
		//creates buffer does encapsulates everything
		ByteBuffer dns_packetBuffer =  ByteBuffer.allocate(maxUDPSize);
		
		byte convertionArray[] = new byte[dnsPS.getByteBuffer().flip().remaining()];
		System.out.println("Size of convertion array is : " + convertionArray.length);
		dnsPS.getByteBuffer().get(convertionArray);
		
		byte [] dnsAnswer = new byte[DNSAnswerSize];
		
		dns_packetBuffer.put(dnsPS.getdnsPacketHeader());
		dns_packetBuffer.put(convertionArray);
		dns_packetBuffer.put(dnsAnswer);
		System.out.println("dns_packetBuffer's size is :  " +dns_packetBuffer.position());
		
		send_Packet(dns_packetBuffer);
		
	}
	
	public static void send_Packet(ByteBuffer dns_packetBuffer) throws IOException{
		
		byte[] dnsData = dns_packetBuffer.array();
		
		DatagramSocket clientSocket = new DatagramSocket();
		// Create the dnsPacket to be sent
		DatagramPacket dnsPacket = new DatagramPacket(dnsData, dnsData.length, convertIPAddress(ipAddress),port);

		// Create the dnsPacket to be received
		
		
		System.out.println("SENDING PACKET !!!!!!");
		clientSocket.send(dnsPacket);
		
		DatagramPacket dnsReceive = new DatagramPacket(dnsData, dnsData.length);
		
		clientSocket.receive(dnsReceive);
		System.out.println("RECEIVED SOMETHING !!!!!!");
			
		
	}
	
	
	
	
	public static InetAddress convertIPAddress(String ipA) throws UnknownHostException{
		
		String[] ip = ipA.split("[.]");

		// Ensure that the IP parts are a byte of information [0, 255]
		for (String i : ip) {
			int ipValue = Integer.parseInt(i);
			if (ipValue < 0 || ipValue > 255)
				throw new NumberFormatException(
						"The ipAddress has one or more dotted-decimal entries > 255 or < 0");
		}
        System.out.println("The ip address still " + ipA);
		InetAddress ipAddress = InetAddress.getByName(ipA);

		return ipAddress;
	}
}
					