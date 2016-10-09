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
	
	public static void main(String[] args) throws Exception {
		
		inputCommandLine icl = new inputCommandLine();
		//take input from user	
		BufferedReader commandline = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Input command line:");
		String inputUser = commandline.readLine();

		//extracts the time out value ,(optional) default: 5
		String timeOutValue = icl.TimeOutInputMapping(inputUser);
		System.out.println("time out value " + timeOutValue);
		
		
		//extracts the max retries values, (optional) default: 3
		String maxRetriesValue = icl.maxRetriesInputMapping(inputUser);
		System.out.println("max out value : " + maxRetriesValue);
		
		
		//extracts type of Querry (optional) default : Type A
		String typeQuerry = icl.typeQuerryInputMapping(inputUser);
		System.out.println("type of Querry : " + typeQuerry);
		
		//extracts ip address (mandatory)
		String ipAddress = icl.ipAddressInputMapping(inputUser)[0];
		System.out.println("ip address : " + ipAddress);
		
		//extracts host domain (mandatory)
		String name = icl.ipAddressInputMapping(inputUser)[1];
		System.out.println("name of website is : " + name );
		
		//if in any of the previous methods, something went wrong that does not met the format, then an error will occur and program will not continue
		
		
		if(icl.getError()){
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
	

	
}