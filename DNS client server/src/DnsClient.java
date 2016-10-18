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
	private static String IPAddress;
	private static String domain;
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
			IPAddress = icl.ipAddressInputMapping(inputUser)[0];
			System.out.println("ip address : " + IPAddress);

			//extracts host domain (mandatory)
			domain = icl.ipAddressInputMapping(inputUser)[1];
			System.out.println("name of website is : " + domain );
			
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
	     System.out.println("----checkpoint reached-----");
	          
	     preparingPacket();
	      
	     }
    }
	
	
	public static void preparingPacket() throws IOException{
		
		//allows bytes to be written into the byteArrayOutputStream so that it can be sent to the DNS server
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(byteOutputStream);

       //converts ip address into IP object
        InetAddress ipAddress = InetAddress.getByName(IPAddress);
        
        //--------BEGINNING PACCKET HEADER-------//
       //creates a 16 bit random number;
        Random ranShortNumber = new Random();
 		short id = (short) ranShortNumber.nextInt(Short.MAX_VALUE + 1);
	     
 		//testing//
 		 String s1 = String.format("%8s", Integer.toBinaryString( id & 0xFFFF)).replace(' ', '0');
		System.out.println("Random number is(bin): " + s1);
		System.out.println("Random number is(dec) : "+ id);
        //testing//
		
		//write ID into buffer 
		dos.writeShort(id);
        
		// Write QR = 0 | OPCODE = 0000 AA = 0  TC =0    RD = 1=> 00000001
        dos.writeByte(0x01);
        //write	 | RA = 0 | Z =000  } RCODE = 0000 = > 00000000
        dos.writeByte(0x00);
       //testing//
		String s2 = String.format("%8s", Integer.toBinaryString( 0x01 & 0xFFFF)).replace(' ', '0');
		System.out.println("Recursion is = : " + s2);
       //testing//
         
         
         
        // QDCOUNT
        dos.writeShort(0x0001);
        
        // ANCOUNT.
        dos.writeShort(0x0000);

        // NSCOUNT
        dos.writeShort(0x0000);

        //ARCOUNT.
        dos.writeShort(0x0000);

        
        //--------BEGINNING OF DNS QUESTION------
        // TODO: write query
        String[] domainParts = domain.split("\\.");
        System.out.println(domain + " has " + domainParts.length + " parts");

        for (int i = 0; i<domainParts.length; i++) {
            byte[] domainBytes = domainParts[i].getBytes("UTF-8");
            dos.writeByte(domainBytes.length);
            dos.write(domainBytes);
        }

        // MArks end of Question => 00000000
        dos.writeByte(0x00);

        // Write Querry = either A/NS/MX
        dos.writeShort(getTypeQuerry());

        // Class 0x01 = IN
        dos.writeShort(0x0001);

        //converts outputStream into array of bytes to forward it to the socket
        byte[] dnsPacketFrame = byteOutputStream.toByteArray();

        System.out.println("Sending: " + dnsPacketFrame.length + " bytes");
        for (int i =0; i< dnsPacketFrame.length; i++) {
            System.out.print("0x" + String.format("%x", dnsPacketFrame[i]) + " " );
        }

        // *** Send DNS Request Frame ***
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket dnsReqPacket = new DatagramPacket(dnsPacketFrame, dnsPacketFrame.length, ipAddress, port);
        socket.send(dnsReqPacket);

        // Await response from DNS server
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        System.out.println("!---------Packet sent------------!");
        socket.receive(packet);
        System.out.println("!---------Response received------!");
        
        
       socket.close();
	}
	
	
	public static short getTypeQuerry(){
		short typeQ = 0x0000  ;
		
		switch (typeQuerry){
			case "A" : 
				typeQ = 0x0001;
				break;
			case "NS":
				typeQ = 0x0002;
				break;
			case "MX":
				typeQ = 0x000F;
				break;
		}
		//testing
		String s3 = String.format("%8s", Integer.toBinaryString( typeQ & 0xFFFF)).replace(' ', '0');
		System.out.println("TYPE Q is (bin) = : " + s3);
		//testing
		return  typeQ;
	}
	
	
	
	
}
					