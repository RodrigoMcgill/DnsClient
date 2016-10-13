import java.util.Random;

public class dnsPacketStructure {
private int id_packet; 
	
	public dnsPacketStructure(String qtype_entry,String domain_name){
		Random ranI = new Random();
		 short id_packet =  (short) ranI.nextInt(65536);  //chooses a number between 0 and 65636(16bit)
		
		byte QR = 0; //response
		
		byte OPCODE = 0;  //not use in sending set this to 0
		
		byte AA = 0; //not use in sending
		
		byte TC = 0; //not use in sending
		
		byte RD = 1;
		
		byte RA = 0;
		
		byte Z = 000; //not use in sending set to 0
		
		byte RCODE = 0000;   //not use in sending
		
		
		short QDCOUNT = 0x0001;  //16 bit
		
		short ANCOUNT = 0x0000;  //16 bit
		
		short NSCOUNT = 0x0000; //16 bit
		
		short ARCOUNT = 0x0000; //16 bit
				
		//-----question section-----//
		//encodes domain_name into byte array
		byte [] name = qnameEncoding(domain_name).clone();
		
		short QTYPE;
		switch(qtype_entry){
				case "A" :	QTYPE = 0x0001;
				case "MX":  QTYPE = 0x0002;
				case "NS":  QTYPE = 0x000f;
		}
		
		short QCLASS = 0x0001;
	
		//------Answers----//
		
		//byte[] NAME ??//
		
		short CLASS = 0x0001;
		
		int TTL ;
		
		short RDLENGTH;  //specifies ...
		
		byte[] RDATA; // specifies all data!!
		
	}
	
	
	
	
	public static byte[] qnameEncoding(String domain_name){
		byte clone[] = new byte[domain_name.length() + 2];  //algo
		int current_memory = 0;
		int past_pointer = 0;
		int future_pointer = 0;
		
		try{
		for(int i=0; i < domain_name.length(); i++){
			if(i == domain_name.length() -1){   //exit condition
				future_pointer = i;
				int num_char = domain_name.substring(past_pointer, future_pointer + 1).length();
				clone[current_memory] = (byte)num_char;
				current_memory++;
				for(past_pointer = past_pointer; past_pointer < future_pointer + 1; past_pointer++){
					clone[current_memory] = (byte) domain_name.charAt(past_pointer);
					current_memory++;
				}
			}
			else if(domain_name.charAt(i) == '.'){
				future_pointer = i;
				int num_char = domain_name.substring(past_pointer, future_pointer).length();
				clone[current_memory] = (byte)num_char;
				current_memory ++;
				for(past_pointer = past_pointer; past_pointer < future_pointer; past_pointer++){
					clone[current_memory] = (byte) domain_name.charAt(past_pointer);
					current_memory++;
				}
				past_pointer += 1;
			}
		}
		clone[current_memory] = (byte)0;
		
		}catch(Exception e){
			System.out.println(e);
		}
		return clone;
	}
	
}
