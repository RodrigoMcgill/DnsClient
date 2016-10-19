/*
 * @Author: Rodrigo M , Katwar Be
 * DNS client server
 * Network lab.................
 */

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
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

	private static int UDP_PORT = 53;
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
	    	System.out.println(icl.getMessageError());
	    	System.exit(0);
		}
	     else{
	     System.out.println("No error found in extracting the values from command line");
	     
	     System.out.println("\n" + "\n");
	     System.out.println("-------Summarizing input------");
	     System.out.println("DNSClient sending request for " + domain );
	     System.out.println("Server: " + IPAddress);
	     System.out.println("Request Type: " + typeQuerry);
	     System.out.println("-------Summarizing input------");
	     
	     
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
        try{
        // *** Send DNS Request Frame ***
        DatagramSocket socket = new DatagramSocket();
        
        DatagramPacket dnsReqPacket = new DatagramPacket(dnsPacketFrame, dnsPacketFrame.length, ipAddress, UDP_PORT);
        
        socket.send(dnsReqPacket);
        

        // Await response from DNS server
        byte[] buf = new byte[512];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        System.out.println("!---------Packet sent------------!");
        socket.receive(packet);
        
        System.out.println("\n \n ");
        System.out.println("!---------Response received--------!");
        //print out all bytes received 
        System.out.println("\n\nReceived: " + packet.getLength() + " bytes");

        for (int i = 0; i < packet.getLength(); i++) {
            System.out.print(" 0x" + String.format("%x", buf[i]) + " " );
        }
        System.out.println("\n");


        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(buf));
        System.out.println("Transaction ID: 0x" + String.format("%x", inputStream.readShort()));
        System.out.println("Flags: 0x" + String.format("%x", inputStream.readShort()));
        System.out.println("Questions: 0x" + String.format("%x", inputStream.readShort()));
        System.out.println("Answers RRs: 0x" + String.format("%x", inputStream.readShort()));
        System.out.println("Authority RRs: 0x" + String.format("%x", inputStream.readShort()));
        System.out.println("Additional RRs: 0x" + String.format("%x", inputStream.readShort()));

        int point = 0;
        while ((point = inputStream.readByte()) > 0) {
            byte[] record = new byte[point];

            for (int i = 0; i < point; i++) {
                record[i] = inputStream.readByte();
            }

            System.out.println("Record: " + new String(record, "UTF-8"));
        }

        System.out.println("Record Type: 0x" + String.format("%x", inputStream.readShort()));
        System.out.println("Class: 0x" + String.format("%x", inputStream.readShort()));
        System.out.println("Field: 0x" + String.format("%x", inputStream.readShort()));
        System.out.println("Type: 0x" + String.format("%x", inputStream.readShort()));
        System.out.println("Class: 0x" + String.format("%x", inputStream.readShort()));
        System.out.println("TTL: 0x" + String.format("%x", inputStream.readInt()));

        short addrLen = inputStream.readShort();
        System.out.println("Len: 0x" + String.format("%x", addrLen));

        System.out.print("Address: ");
        for (int i = 0; i < addrLen; i++ ) {
            System.out.print("" + String.format("%d", (inputStream.readByte() & 0xFF)) + ".");
        }
        
        //closing socket
       socket.close();
        }catch(Exception err){
        	System.out.println("Error in the process of sending the package " + err);
        }
        
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
		return  typeQ;
	}
	
	
	
	
}
					