
/*
 * *
 @Authors: Rodrigo Mendoza Arbizu & Kawtar Berkouk
 *
 */
package DNS_Package;
import java.io.*;  
import java.net.*;

public class Client {
    //create request and packet objects
	private static DNS_Package.Request request; 
	private static DNS_Package.Packet packet;
	    
	//               -----main method----
    public static void main (String[] args) throws IOException  {
	    Client dnscl = new Client();
		dnscl.run(args);
	}
	
	//                ----Run method----- 
	public void run(String[] args) throws IOException{
		String inputUser = "";
		for(int arg = 0; arg < args.length; arg++){
			inputUser += args[arg] + " ";
		}	
		if(inputUser == ""){
			System.out.println("No arguments provided, Please insert in the following format:");
			System.out.println("[-r] X [-t] X [-p] X [-ns|a|mx] @xxx.xxx.xxx domain_name");
			System.exit(0);
		}
		else{		
			settingParameters(inputUser, args); 
		}
	}
	
	/**
	 * This function verifies that all arguments have correct syntax and then sets those values in the Request.java
	 * else throw corresponding Error.
	 * Exception: some arguments could be empty. In that case, default values are set.
	 * @param inputUser ----> The complete String from user input
	 * @param args      ----> array String 
	 * @throws and handles IOException
	 */
	public static void settingParameters(String inputUser,String [] args) throws IOException{
		//constructor for setting up values
		request = new Request();
		//Verifies and sets ip address
		request.setIp(request.ipAddressInputMapping(inputUser)); 
		
		if(request.getError()){
			System.out.print("IP ERROR FORMAT: IP must be: @xxx.xxx.xxx format");
			return;
		}
	   //the following 3 lines verifies that all parameters adheres to correct format and conditions and
	   //then sets them in the Request.java class
		request.TimeOutInputValue(inputUser);
		request.maxRetriesInputValue(inputUser);
		request.typeQuerryInputValue(inputUser);
       	
		//if any errors were caught,an appropriate error message will be displayed using the printError()
		//method situated at the bottom of this class
		 if (request.getError()) {
			 printError();
			 return;
		 }
		 //check for domain errors. 
		String name = args[args.length -1];
		if(request.verifyDomainName(name))
			//---PASSING TO SENDING PACKET PHASE ---//
			sendingPacket(name);
		  else{
			  System.out.println("ERROR: No domain name present. Please write a domain name after the @xxx.xxx.xxx ");
			  return;
		  }
	}
		
	/**
	 * Starts assembling the packet. It sends and receives packets
	 * @param name -> domain name or the question
	 */
   public static void sendingPacket(String name){
		//create client socket
		DatagramSocket mySocket = null;
				try {
						mySocket = new DatagramSocket();
				} catch (SocketException e1) {
						System.out.println("Something went wrong creating the socket,Please try again");
				}
				//initialize a packet object with default values & entered arguments
				packet = new Packet(name, request.getType() ); 
				
				//turn the created packet into a byte array packet ready to send
				byte[] packetData = packet.data();
				//creates empty packet ready to receive the response packet	
				byte[] receiveData = new byte[1024];
				
				try{
					mySocket.setSoTimeout(request.getTimeOut());
				}catch (SocketException e1){
						System.out.println("client socket setSoTimout exception");
				}
				
				int counterRetries = 0;
				// this boolean value is true when there is a response
				boolean responseReceived = false; 
				
				//convert ip Address to IntetAddress type and make IN|OUT datagrams
				InetAddress ipAddress = request.stringToInetAddress();
				DatagramPacket datagramOut = new DatagramPacket(packetData, packetData.length, ipAddress, request.getPort());
				DatagramPacket datagramIn = new DatagramPacket(receiveData, receiveData.length);
				
				//initialization of the timer
				long startTime =0;
				long stopTime =0;
				
				loop:
				while (true) {
					try {
						
						System.out.println("DnsClient sending Request for "+name); 
						System.out.println("Server: "+ request.getIpAddr());
						System.out.println("Request Type: " + request.getStringType());
						
						
				        startTime = System.currentTimeMillis( );
			         
				        // Send Packet
				        mySocket.send(datagramOut);
		
				        //----Waiting to receive a response --- 
				        mySocket.receive(datagramIn);	
									
						responseReceived= true;
					}catch ( SocketTimeoutException e ){
						//if counter < maximum retry value continue and increase the counter variable by 1
						if (counterRetries == request.getMaxRetries()){
							System.out.println("ERROR: Maximum number of retries "+ request.getMaxRetries() + " reached");
							break loop;
						}
						System.out.println("Resend package...");
						counterRetries++;
					} catch (IOException e) {
						System.out.println("IO exception e");
					}
					//if received a response, print the time
					if( responseReceived ){
						stopTime = System.currentTimeMillis( );
					    System.out.println("Response received after "+(double)(stopTime-startTime)/1000+" seconds "+ counterRetries+" retries");		
					    //interpret received packet.		
						packet.defineReceivedPacket(datagramIn.getData());
						break loop;
					}
				}
				//always close socket when done
				mySocket.close();
	}
   
   /**
    * This method is used when the *error* parameter is *true* in the Packet.java when checking for arguments errors.
    * This method will be immediately called after and will then display the appropriate type of error ranging from [1-4]
    */
   public static void printError(){
	   	 if(request.getTypeError() == 1){
			 System.out.println("ERROR : Incorrect Syntax for [-t] [-r] [-p]. Please insert numeric values");
		 }
		 if(request.getTypeError() == 2){
			 System.out.println("ERROR : Maximum number of desired retries cannot exceed 50!");
		 }
		 if(request.getTypeError() == 3){
			 System.out.println("ERROR : Maximum retry value cannot be 0");
		 }
		 if(request.getTypeError() == 4){
			 System.out.println("ERROR : Maximum time out value cannot be 0");
		 }
		 return;
   }
		
}
	
	
