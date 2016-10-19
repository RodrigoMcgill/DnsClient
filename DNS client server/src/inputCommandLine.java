import java.io.IOException;
//https://github.com/examplecode/gfw_dns_resolver/blob/master/GFWDnsResolver.java
public class inputCommandLine {
	
	private boolean error = false;
	private String error_message;
	
	public  String TimeOutInputMapping(String inputUser) throws IOException{
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
						error_message = "Wrong format on time out value";
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
	public  String maxRetriesInputMapping(String inputUser) throws IOException{
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
						error_message = "Wrong format on maximum retries";
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
	public  String typeQuerryInputMapping(String inputUser){
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

	/**
	 * 
	 * @param input from command line
	 * @return both the ip address and name
	 */
	public  String[] ipAddressInputMapping(String inputUser){
		String []ipAddress_Name = new String[2];
		int numberDots = 0;
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
        			  		numberDots++;
        			  	}
        			  	else if (Character.isDigit(inputUser.charAt(j))){
        			  		ipAddress_Name[0] += inputUser.charAt(j);
            		    }
        			  	else{
        			  		error_message ="Wrong ip address format";
        			  	    error =true;
        			  	    break;
        			  	}
        	       }
        	   }        	 
           }
		try{
				for(next++; next < inputUser.length(); next++){
						if(inputUser.charAt(next) != ' '){
							ipAddress_Name[1] += inputUser.charAt(next);
						}
				}
				if(ipAddress_Name[1] == null){
						error_message = "Domain name missing!";
						error = true;
				}
				else if(numberDots != 3){
						error_message = "Wrong format in the ip_address";
						error = true;
				}
		}catch(Exception e){
			System.out.println(e);
			error = true;
		}
		
			return ipAddress_Name;
	}
	
		
	public void setError(boolean default_error){
		error = default_error;
	}
	
	public boolean getError(){
		return error;
	}
	
	public String getMessageError(){
		return error_message;
	}
}