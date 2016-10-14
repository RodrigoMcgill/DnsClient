import java.nio.ByteBuffer;
import java.util.Random;

public class dnsPacketStructure {
private int id_packet; 
	
	public dnsPacketStructure(String qtype_entry,String domain_name){
		 Random ranI = new Random();
		 		
		 ByteBuffer buff = ByteBuffer.allocate(56);
		 //creates array with 2 bytes = 16 bits
		 byte[] seq = new byte[2];
		 
		  ranI.nextBytes(seq);
		  
		  buff.put(seq);
		  
		  byte[] header	= {0x00,0x00,0x00,0x00,0x01,0x00,0x00,0x00,0x0001,0x0000,0x0000,0x0000};
		  
		  buff.put(header);
		  
		  
		//-----question section-----//
		//encodes domain_name and adds it to the buffer.: encodes  number of characters and then the characters themselves
		  
		String[] parts = domain_name.split("\\.");
		for(int i= 0; i < parts.length;i++) {
			buff.put((byte) parts[i].length());
			buff.put(parts[i].getBytes());
		}
		//putting 0 meaning the end of the question
		buff.put((byte)0x00);
		
		//qtype entry 16 bits
		switch(qtype_entry){
				case "A" :	buff.put((byte)0x0001);	
				case "MX":  buff.put((byte)0x0002);
				case "NS":  buff.put((byte)0x000f);
		}
		
		//QCLass 16 bits
		buff.put((byte)0x0001);
		
	// I believe this marks the end of the DNS querry. nothing more to add. No  need to add **AUTHORITY** and ** ADDITIONAL** since this is a querry	
	}
	
	
	
	// Test code to extract domain name..may not have to use it but I overcomplicated it
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
