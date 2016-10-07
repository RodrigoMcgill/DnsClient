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
		
		
		//take input form user	
		BufferedReader commandline = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Input command line:");
		String inputUser = commandline.readLine();

		//extracts the time out value ,(optional) default: 5
		String timeOutValue = TimeOutInputMapping(inputUser);
		System.out.println("time out value " + timeOutValue);
		
		
		//extracts the max retries values, (optional) default: 3
		String maxRetriesValue = maxRetriesInputMapping(inputUser);
		System.out.println("max out value : " + maxRetriesValue);
		
		
		//extracts type of Querry (optional) default : Type A
		String typeQuerry = typeQuerryInputMapping(inputUser);
		System.out.println("type of Querry : " + typeQuerry);
		
		//extracts ip address (mandatory)
		String ipAddress = ipAddressInputMapping(inputUser)[0];
		System.out.println("ip address : " + ipAddress);
		
		//extracts host domain (mandatory)
		String name = ipAddressInputMapping(inputUser)[1];
		System.out.println("name of website is : " + name );
		
		//if in any of the previous methods, something went wrong that does not met the format, then an error will occur and program will not continue
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
	
	/**
	 * 
	 * @param inputUser
	 * @return time out number
	 * @throws IOException
	 */
	public static String TimeOutInputMapping(String inputUser) throws IOException{
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
	
	/**
	 * 
	 * @param inputUser
	 * @return max retries before server gives up
	 * @throws IOException
	 */
	public static String maxRetriesInputMapping(String inputUser) throws IOException{
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
	/**
	 * @param inputUser
	 * @return the type of querry
	 */
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

	//self remainder: need to come up with an efficient way
	/**
	 * 
	 * @param input from command line
	 * @return both the ip address and name
	 */
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