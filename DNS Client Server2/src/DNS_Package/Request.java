/*
 * *
 @Authors: Rodrigo Mendoza Arbizu & Kawtar Berkouk
 *
 */
package DNS_Package;
import java.io.*;  
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {

	//default values and format of the IP address
	public static final int TIMEOUT_DEFAULT = 5000; 
	public static final int MAX_RETRIES_DEFAULT = 3;
	public static final int PORT_DEFAULT = 53; 
	public static final int A_QUERY=0;
	public static final int NS_QUERY=1;
	public static final int MX_QUERY=2;
	private int type,timeout,port,maxRetries;
	private String ipAddress;
	private boolean error= false;
	private int typeError = 0;
	//regular expression to validate entered IPs
	public static final String IPADDRESS_PATTERN = 
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	//set default values once constructor is called on
	public Request() {
		this.type =A_QUERY;
		this.timeout = TIMEOUT_DEFAULT; 
		this.port = PORT_DEFAULT;
		this.maxRetries = MAX_RETRIES_DEFAULT; 		
	}
	
	
	/**
	 * Extracts ip address from arguments
	 * @param inputUser
	 * @return ip address
	 */
	public  String ipAddressInputMapping(String inputUser){
		String getIP = "";
		for(int i=0;i<inputUser.length(); i++){
        	  if(inputUser.charAt(i)== '@'){
        		  for(int j=i+1; j<inputUser.length();j++){
        			  	if(inputUser.charAt(j) == ' '){
        				  	break;
        			  	}else if(inputUser.charAt(j) == '.'){
        			  		getIP += ".";
        			  	}
        			  	else if (Character.isDigit(inputUser.charAt(j))){
        			  		getIP += inputUser.charAt(j);
            		    }
        			  	else{
        			  		error = true;
        			  		this.typeError = 1;
        			  	    break;
        			  	}
        	       }
        	   }
           }		
	return getIP;
	}
	
	
	/**
	 * Extracts maximum time out value to wait for a responds before trying again. Default value: 5
	 * @param inputUser
	 * @throws and handles IOException
	 */
	public  void TimeOutInputValue(String inputUser) throws IOException{
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
						error = true;
					}
					if(isNumeric(timeOutValue)){
						try{
						this.timeout = Integer.valueOf(timeOutValue)*1000;
						}catch (java.lang.NumberFormatException e){
						System.out.println("ERROR : Please insert integer numbers");
						}
						if(this.timeout == 0){
							this.typeError = 4;
							error = true;
							return;
						}
						
					}
					else if(!isNumeric(timeOutValue)){
						error=true;
						this.typeError = 1;
						break;
					}
				}
			}
		}
	
	/**
	 * Extracts maximum retry value that message should try before giving up: Default value: 3
	 * @param inputUser
	 * @throws IOException
	 */
	public  void maxRetriesInputValue(String inputUser) throws IOException{
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
						
						error = true;
					}
					if(isNumeric(maxRetries)){
						this.maxRetries = Integer.valueOf(maxRetries);
						if(this.maxRetries > 50){
							this.typeError = 2;
							error = true;
							return;
						}
						if(this.maxRetries == 0){
							this.typeError = 3;
							error = true;
							return;
						}
					}
					else if(!isNumeric(maxRetries)){
						error=true;
					}
				}
			 }	
	}
	
	
	/**
	 * Extracts and sets the type of Query. If none provided in the arg, then default is type A
	 * @param inputUser --->command line
	 */
	public  void typeQuerryInputValue(String inputUser){
		for(int i=0; i < inputUser.length(); i++){
			if(inputUser.charAt(i)== '-' && inputUser.charAt(i+1) == 'm' && inputUser.charAt(i+2) == 'x'){
				this.type =  MX_QUERY;
        	 }
			else if(inputUser.charAt(i)== '-' && inputUser.charAt(i+1) == 'n' && inputUser.charAt(i+2) == 's'){
				this.type = NS_QUERY;
        	 }
			else if(inputUser.charAt(i)== '-' && inputUser.charAt(i+1) == 'A'){
				this.type =  A_QUERY;
        	 }
		}
	}

	
	/**
	 * Change String IP address to InetAddress object
	 */
	public InetAddress stringToInetAddress () {
		String ipAddr = this.ipAddress;
		int[] ipArray = new int[4];
		int count=0;
		for (String splittedIP : ipAddr.split("\\.") ) {
			try{
			ipArray[count]= Integer.valueOf(splittedIP);
			}catch(java.lang.NumberFormatException e){
                System.out.print("ERROR: dns server's name missing");
                System.exit(1);
			}
			count++;
		}
		InetAddress address = null;
		try {
			address = InetAddress.getByAddress(new byte [] {(byte)ipArray[0],
					(byte)ipArray[1],(byte)ipArray[2],(byte)ipArray[3]});
		} catch (UnknownHostException e) {
			System.out.println("ERROR: IP entered does not match IPV4 pattern");
			//isIPValid();
			if(!ipPatternCheck(this.ipAddress)){
				System.out.println("ERROR: IP entered does not match IPV4 pattern");
			}
		}
		return address;
	}
    
	
	/**
	 * Verifies that the domain name is present and not empty
	 * @param name
	 * @return
	 */
	public boolean verifyDomainName(String name){
		if(name.charAt(0) != '@'){
			return true;
		}
		return false;
	}
	
	//Verify pattern of IP address (IPv4 format)
	/**
	 * Verifies that the IP address provided adheres with IPv4 format using the
	 * IPADDRESS_PATTERN string model located at the beginning of this class
	 * @param ip
	 * @returns true or false
	 */
	public boolean ipPatternCheck(String ip) {
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN); 
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches(); //if true then matches at 100%
	}
	
	/**
	 * Verifies that the giving -t -r -p values are indeed numerical values
	 * @param str
	 * @returns true or false
	 */
	public boolean isNumeric(String str)  
	{  
	  try  	  {  
	    Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException e)  
	  {  
		System.out.println("ERROR 	Incorrect Options Syntax: Options [-t] [-r] [-p] only take numeric values"); 
	    return false;  
	  }  
	  return true;  
	}
	
   //set and get methods to easily set or extract values either to continue program or debugging
   public void setIp(String ipAddress){
		this.ipAddress = ipAddress;
	}
   
   public boolean getError(){
	   return error;
   }
	
	public int getTimeOut () {
		return this.timeout;
	}

	public int getMaxRetries () { 
		return this.maxRetries;  
	}

	public void setPort (int port) {
		this.port = port ; 
	}
	
	public int getPort () {
		return this.port; 
	}

	public void setType (int type) { 
		this.type = type ; 
	}
	
	public int getType() { 
		return this.type; 
	}
	
	public String getStringType () {
		if (getType() == NS_QUERY ) {
			return "NS";
		}
		if (getType() == MX_QUERY ) {
			return "MX";
		}
		return "A";
	}
	public int getTypeError(){
		return typeError;
	}
	public String getIpAddr () { 
		return this.ipAddress; 
		}

}