/*
 * @Author: Rodrigo M , Katwar Be
 * DNS client server
 * Network lab.................
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class DnsClient {

	private static int port = 53;
	private static byte[] sendData;
	private static  byte [] receiveData;
	private static String  targetServer = "some ip address";
	private static boolean error = false;
	
	public static void main(String[] args) throws Exception {
		
		
			
		BufferedReader commandline = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Input command line:");
		String inputUser = commandline.readLine();
		System.out.println(error);
			
		String timeOutValue = TimeOutInputMapping(inputUser);
		System.out.println("time out value " + timeOutValue);
		System.out.println(error);
		
		String maxRetriesValue = maxRetriesInputMapping(inputUser);
		System.out.println("max out value : " + maxRetriesValue);
		System.out.println(error);
		
		String typeQuerry = typeQuerryInputMapping(inputUser);
		System.out.println("type of Querry : " + typeQuerry);
		
		String ipAddress = ipAddressInputMapping(inputUser)[0];
		System.out.println("ip address : " + ipAddress);
		
		String name = ipAddressInputMapping(inputUser)[1];
		System.out.println("name of website is : " + name );
		System.out.println(error);
		if(error){
			System.out.println("The program will no longer continue forward due to errors");
		}
		
		else{
		//creates the client socket for this client
			/*
		DatagramSocket clientSocket = new DatagramSocket();
		
		//stores an ip address of choice --> 132.206.85.12 for McGill DN server    or ---> 8.8.8.8 for Google DNS server
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
	
	public static byte[] constructPAcket(){
		
		
		
		return receiveData;
		
	}
	*/
		}
	}
	
	
	public static String TimeOutInputMapping(String inputUser) throws IOException{
		//get input from command line
				
		String timeOutValue = "";

			for(int i=0; i < inputUser.length(); i++){
				if(inputUser.charAt(i)== '-' && inputUser.charAt(i + 1) == 't' ){
					int flagAtT = i + 1;
					int counter = 0;
					int flag = 1;
					int j = 2;
					while(flag == 1 ){
				    	if(inputUser.charAt(i+3) != ' '){
				         	counter++;
						    i++;
					    }
					    else{
						    flag = 0;
					    }				    	
					}
					while(counter != 0){
						timeOutValue += inputUser.charAt(flagAtT + j);
					    counter--;
					    j++;
					}
					try{
						Integer.parseInt(timeOutValue);
					}catch (Exception e){
						System.out.println("Wrong format on time out value");
						error = true;
					}
					return timeOutValue;
				}
			 }
		return "5";
}
	
	public static String maxRetriesInputMapping(String inputUser) throws IOException{
		//get input from command line
			
	
		String maxRetries = "";
         
			for(int i=0; i < inputUser.length(); i++){
				if(inputUser.charAt(i)== '-' && inputUser.charAt(i + 1) == 'r' ){
					int flagAtT = i + 1;
					int counter = 0;
					int flag = 1;
					int j = 2;
					while(flag == 1 ){
				    	if(inputUser.charAt(i+3) != ' ' ){
					    	counter++;
						    i++;
					    }
					    else{
						    flag = 0;
					    }
				    }
					while(counter != 0){
						maxRetries += inputUser.charAt(flagAtT + j);
					    counter--;
					    j++;
					}
					try{
						Integer.parseInt(maxRetries);
					}catch (Exception e){
						System.out.println("Wrong format on maximum retries");
						error = true;
					}
					return maxRetries;
				}
			 }
 	return "3";
}
	
	public static String typeQuerryInputMapping(String inputUser){
		String QuerryType = "";
		for(int i=0; i < inputUser.length(); i++){
			if(inputUser.charAt(i)== '-' && inputUser.charAt(i+1) == 'm' && inputUser.charAt(i+2) == 'x'){
				return "MX";
        	 }
			else if(inputUser.charAt(i)== '-' && inputUser.charAt(i+1) == 'n' && inputUser.charAt(i+2) == 's'){
				return "NS";
        	 }
			else if(inputUser.charAt(i)== '-' && inputUser.charAt(i+1) == 'A'){
				return "A";
        	 }
		}
		return "A";
	}

	//need to fix this
	public static String[] ipAddressInputMapping(String inputUser){
		String []ipAddress_Name = new String[2];
		ipAddress_Name[0] = "";
		ipAddress_Name[1]= "";
		int next = 0;
          for(int i=0;i<inputUser.length(); i++){
        	  if(inputUser.charAt(i)== '@'){
        		  for(int j=i+1; j<inputUser.length();j++){
        			  	if(inputUser.charAt(j) == ' '){
        			  		next = j;
        				  	break;
        			  	}else if(inputUser.charAt(j) == '.'){
        			  		ipAddress_Name[0] += ".";
        			  	}
        			  	else if (Character.isDigit(inputUser.charAt(j))){
        			  		ipAddress_Name[0] += inputUser.charAt(j);
            		    }
        			  	else{
        			  		System.out.println("Wrong ip address format");
        			  	    error =true;
        			  	    break;
        			  	}
        	       }
        		  
              }
           }
          for(next++; next < inputUser.length(); next++){
        	  if(inputUser.charAt(next) != ' '){
        		  ipAddress_Name[1] += inputUser.charAt(next);
        	  }
          }
          if(ipAddress_Name[1] == null){
        	  error = true;
          }
          return ipAddress_Name;
	}
	
}