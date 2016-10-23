
/*
 * *
 @Authors: Rodrigo Mendoza Arbizu & Kawtar Berkouk
 *
 */
package DNS_Package;
import java.nio.charset.*;
import java.nio.*;
import java.util.*;

public class Packet {
	//Constants 
	public final int TYPE_A_RR = 0X0001;
	public final int TYPE_NS_RR = 0x0002;
	public final int TYPE_CNAME_RR =0x005;
	public final int TYPE_MX_RR = 0x000f;
	private Random randomID;
	private String name;
	private int type; 
	
	//header fields
	private short id;
	private short flags; 
	private short qdCount; 
	private short anCount; 
	private short nsCount; 
	private short arCount; 

	//question fields
	private byte[] qName; 
	private short qType;
	private short qClass; 
	//RR fields
	private int answerAA;
	private int answerType;
	private int answerClass;
	private int answerTTL;
	private int answerRdLength;
	private String answerRdata;
	private String answerCname;
	private int answerPreference;
	private String answerExchange;
	//cached size
	private int qNameSize;
	private int cNameSize;
	private final int HEADER_SIZE = 12;

	//Constructor
	public Packet() {

	}
	//passed the values of domain name and the type of Query
	public Packet(String name, int type) {
		this.name = name;
		this.type = type;
	}
	//initiates the header variables
	private void packetHeader() {
		this.id = idGenerator(); 
		this.flags = 0x0100; //256
		this.qdCount = 0x0001;//1 question
		this.anCount=this.nsCount=this.arCount = 0x0000; //defaulted to 0
	}
	
	/**
	 * Generates random 16 bit id number
	 * @return
	 */
	private short idGenerator() {
		randomID = new Random();
		short id = (short) randomID.nextInt(Short.MAX_VALUE);
		return id; 
		}
		
	//initiate the question variables
	private void packetQuestion(String name, int type ) {
		this.qName = writeName(name);
		this.qType = writeType(type);
		this.qClass = 0x0001;
	}
 /**
  * 
  * @return
  */
	public byte[] data() {
		String name = this.name; 
		int type = this.type; 
		packetHeader();
		packetQuestion(name,type);
		ByteBuffer data =  ByteBuffer.allocate(520);
		data.putShort(id);
		data.putShort(flags);
		data.putShort(qdCount);
		data.putShort(anCount);
		data.putShort(nsCount);
		data.putShort(arCount);
		data.put(qName);
		data.putShort(qType);
		data.putShort(qClass);
		return truncateBytebuffer(data).array();
	}
	
	/**
	 * Truncates extra space in the bytebuffer
	 * @param buffer
	 * @return
	 */
	private ByteBuffer truncateBytebuffer (ByteBuffer buffer){
		ByteBuffer copyOfBuffer = buffer.duplicate();
		buffer.flip(); //flip it otherwise buffer will be empty since position = limit
		int size = 0;
		while (buffer.hasRemaining()) { //get size of buffer
			buffer.get();
			size++;
		}
		ByteBuffer truncatedBuffer = ByteBuffer.allocate(size);//truncate byte buffer at its limit
		copyOfBuffer.flip();
		while (copyOfBuffer.hasRemaining()) {
			truncatedBuffer.put(copyOfBuffer.get());
		}
		return truncatedBuffer;
		}
	

		/**
		 * Converts the giving query type to their respective hexa value
		 * @param the numerical type of the query type
		 * @return
		 */
	private short writeType(int type) {
		short value = 0;
		switch (type) {
				case DNS_Package.Request.A_QUERY:
					value = 0x0001;
					break;
				case DNS_Package.Request.NS_QUERY:
					value = 0x0002;
					break;
				case DNS_Package.Request.MX_QUERY:
					value = 0x000f;
					break;
			}
			return value;
		}

		
		/**
		 * 
		 * Converts domain name into a byte array
		 * @param String--> domain name
		 * @return
		 */
	private byte[] writeName (String name) {
		ByteBuffer data =  ByteBuffer.allocate(32);
		char[] buffer= new char[63];
		int nameSize = name.toCharArray().length;
		byte count = 0 ;
		int charCount = 0;
		for (char charData : name.toCharArray() ) {
			charCount++;
			if ( charData == '.'){
				data.put(count);
				for (int j = 0; j<count; j++ ) {
				 	data.put(String.valueOf(buffer[j]).getBytes(StandardCharsets.US_ASCII));
				 } 
			buffer= new char[63];
				count =0;
			}
			else{
				buffer[count] = charData;
				count ++;
			}
			if ( charCount == nameSize) {
				data.put(count);
				for (int j = 0; j<count; j++ ) {
				 	data.put(String.valueOf(buffer[j]).getBytes(StandardCharsets.US_ASCII));
				 } 
				buffer= new char[63];
				count =0;
			}
		}
		data.put(count);
		this.qNameSize = truncateBytebuffer(data).capacity(); 
		return truncateBytebuffer(data).array() ; //returns array backing bytebuffer
	}
	
	/**
	 * Interprets the received packet. This method extracts the answers of the query and handles compression
	 * @param received packet in []byte format
	 */
	public void defineReceivedPacket (byte[] receivedPacket) {
		int qNameSize = this.qNameSize;
		int byteCounter = 0;
		String[] binaryPacket = new String[1500];
		byte byte1;
		ByteBuffer dup = ByteBuffer.wrap(receivedPacket);    //converts byte[] into buffer

		while(dup.hasRemaining()) {
			byte1 = dup.get();
			binaryPacket[byteCounter] = String.format("%8s",Integer.toBinaryString(Byte.toUnsignedInt(byte1))).replace(' ', '0');
			byteCounter++;
		}
		//gets the counts values from the received packet
		int answerCount = Integer.valueOf(binaryPacket[6].concat(binaryPacket[7]),2);
		int authorityCount = Integer.valueOf(binaryPacket[8].concat(binaryPacket[9]),2);
		int addResCount = Integer.valueOf(binaryPacket[10].concat(binaryPacket[11]),2);		
		
		//sets the byteCounter index at the start of the answer section
		byteCounter = HEADER_SIZE+qNameSize+4;
		StringBuilder sbuilder = new StringBuilder();
		//a counter to reach the RCODE from the flags
		int rcodeCounter = 0;
		//iteration to find the RCODE
		for (char c : binaryPacket[3].toCharArray() ) {
			rcodeCounter ++;
			//when gets to 4 bits, save rcode to sb
			if (rcodeCounter > 4) {
				sbuilder.append(c);
			}
		}
		//if  RCODE != 0 error occurred
		int RCODE_STATUS = Integer.valueOf(sbuilder.toString(),2);
		rCODE_ERROR_CHECK r_E_C = new rCODE_ERROR_CHECK();
		if(!(r_E_C.checkDNSTypeError(RCODE_STATUS))){
			return;
		}
		 
		//if RCODE is 0 check whether there are answers
		else if (answerCount+authorityCount+addResCount > 0){
			if (answerCount >= 0) {
				System.out.println("***Answer Section ("+ answerCount +" records)***");	
			}
			if (authorityCount > 0) {
				System.out.println("***Contains "+ authorityCount+ " Authoritative Resource Records ***");
			}
			//Iteration of the answer resources records
			
			for (int pi = 0; pi < answerCount+authorityCount+addResCount ; pi++) {
				// The program does not require to output authority section but needs additional answer if any
				// iterate authority resources records
				if( pi == (answerCount+authorityCount)) {
					System.out.println("***Addtional Section ("+ addResCount +" records)***");
					
				}
				if( Integer.valueOf(binaryPacket[byteCounter]) == 11000000  ) {
					//gets Flags bytes
					int flagCounter=0; 
					for (char c : binaryPacket[2].toCharArray() ) {
						flagCounter++;
						if (flagCounter == 6){
							//gets the AA flag
							this.answerAA= Integer.valueOf(new String(new char[] {c}));
						}
					}
					//next byte is the pointer HEADER_SIZE+qNameSize+4+1
					byteCounter  = byteCounter+2;
					//gets Type bytes
					this.answerType = Integer.valueOf(binaryPacket[byteCounter].concat(binaryPacket[byteCounter+1]),2);
					byteCounter = byteCounter+2;
					//gets Class bytes
					this.answerClass = Integer.valueOf(binaryPacket[byteCounter].concat(binaryPacket[byteCounter+1]),2);
					//gets TTL bytes
					byteCounter = byteCounter+2;
					sbuilder = new StringBuilder ();
					for ( int i= 0; i <4 ; i++ ) {
						sbuilder.append(binaryPacket[byteCounter+i]);
					}
					byteCounter= byteCounter+4;
					this.answerTTL = Integer.valueOf(sbuilder.toString(),2);
					//gets RDlength bytes
					this.answerRdLength = Integer.valueOf(binaryPacket[byteCounter].concat(binaryPacket[byteCounter+1]),2);
					byteCounter= byteCounter+2;
					//Rdata cases
					
					//A resource record-------------------------------------------
					//if its a Type A address only the IP address bytes will be taken and store into answerRdata global variable
					if (this.answerType == TYPE_A_RR) {
						//4 octets
						sbuilder = new StringBuilder();
						for (int i = 0; i<4 ; i++ ) {
							sbuilder.append(Integer.valueOf(binaryPacket[byteCounter+i],2).toString());
							if( i<3) {
								sbuilder.append(".");
							}
						}
						this.answerRdata = sbuilder.toString();
						byteCounter = byteCounter+4;
					}
					//NS resource record---------------------------------------
					if (this.answerType == TYPE_NS_RR) {
						int pointer =0; 
						boolean pointerFlag= false;
						loop:
						for(int i =0 ; i<63; i++){
							// if  pointer
							if (Integer.valueOf(binaryPacket[byteCounter+i]) == 11000000){
								byteCounter = byteCounter+i;
								pointer = Integer.valueOf(binaryPacket[byteCounter+1],2);
								byteCounter = byteCounter+1;
								this.cNameSize = i;
								pointerFlag = true;
								break loop;
							}
							// if no pointer
							if (Integer.valueOf(binaryPacket[byteCounter+i]) == 00000000) {
								byteCounter = byteCounter+i;
								this.cNameSize = i;
								break loop;								
							}
						}
						//updates the CNAME size if there's a pointer or not
						int cSize = pointerFlag ? (this.cNameSize+1): this.cNameSize;	
						//gets the string for the cname
						String cname = new String(Arrays.copyOfRange(receivedPacket,byteCounter-cSize,byteCounter),StandardCharsets.US_ASCII);
						int namesize = 0;
						//loop until the end
						loop:
						for (int w = 0; w < 63 ; w++ ) {
							namesize++;
							if (Integer.valueOf(binaryPacket[pointer+w]) == 00000000) {
								break loop;
							}
						}
						//if pointer concatenate the pointer string to the original
						if (pointerFlag == true) {
							String pointerString = new String(Arrays.copyOfRange(receivedPacket,pointer+1,pointer+namesize),StandardCharsets.US_ASCII);
							cname = cname.concat(pointerString);
						}
						sbuilder = new StringBuilder();
						//updates the answerCname variable
						this.answerCname = formatAcsiiString(cname);
						byteCounter = byteCounter+1;					
					}
					//CNAME resource record----------------------------------------------
					//handling same way as the NS records
					if (this.answerType == TYPE_CNAME_RR) {
						int pointer =0; 
						boolean pointerFlag= false;
						loop:
						for(int i =0 ; i<63; i++){
							// if  pointer
							if (Integer.valueOf(binaryPacket[byteCounter+i]) == 11000000){
								byteCounter = byteCounter+i;
								pointer = Integer.valueOf(binaryPacket[byteCounter+1],2);
								byteCounter = byteCounter+1;
								this.cNameSize = i;
								pointerFlag = true;
								break loop;
							}
							// if no pointer
							if (Integer.valueOf(binaryPacket[byteCounter+i]) == 00000000) {
								byteCounter = byteCounter+i;
								this.cNameSize = i;
								break loop;								
							}
						}
						int cSize = pointerFlag ? (this.cNameSize+1): this.cNameSize;	
						String cname = new String(Arrays.copyOfRange(receivedPacket,byteCounter-cSize,byteCounter),StandardCharsets.US_ASCII);
						int namesize = 0;
						loop:
						for (int k = 0; k < 63 ; k++ ) {
							namesize++;
							if (Integer.valueOf(binaryPacket[pointer+k]) == 00000000) {
								break loop;
							}
						}
						String pointerString = new String(Arrays.copyOfRange(receivedPacket,pointer+1,pointer+namesize),StandardCharsets.US_ASCII);
						cname = cname.concat(pointerString);
						sbuilder = new StringBuilder(); 
						this.answerCname = formatAcsiiString(cname);
						byteCounter = byteCounter+1;
					}
					//MAX has 1 extra field to be handled (preference) 
					if (this.answerType == TYPE_MX_RR) {
						int pointer =0; 
						boolean pointerFlag= false;
						//gets the 16 bit Preference
						this.answerPreference = Integer.valueOf(binaryPacket[byteCounter].concat(binaryPacket[byteCounter+1]),2);
						byteCounter = byteCounter+2;
						loop:
						for(int i =0 ; i<63; i++){
							// if  pointer
							if (Integer.valueOf(binaryPacket[byteCounter+i]) == 11000000){
								byteCounter = byteCounter+i;
								pointer = Integer.valueOf(binaryPacket[byteCounter+1],2);
								byteCounter = byteCounter+1;
								this.cNameSize = i;
								pointerFlag = true;
								break loop;
							}
							// if no pointer
							if (Integer.valueOf(binaryPacket[byteCounter+i]) == 00000000) {
								byteCounter = byteCounter+i;
								this.cNameSize = i;
								break loop;								
							}
						}
						int cSize = pointerFlag ? (this.cNameSize+1): this.cNameSize;	
						String cname = new String(Arrays.copyOfRange(receivedPacket,byteCounter-cSize,byteCounter),StandardCharsets.US_ASCII);
						int namesize = 0;
						loop:
						for (int w = 0; w < 63 ; w++ ) {
							namesize++;
							if (Integer.valueOf(binaryPacket[pointer+w]) == 00000000) {
								break loop;
							}
						}
						String pointerString = new String(Arrays.copyOfRange(receivedPacket,pointer+1,pointer+namesize),StandardCharsets.US_ASCII);
						cname = cname.concat(pointerString);
						sbuilder = new StringBuilder(); 
						this.answerCname = formatAcsiiString(cname);
						byteCounter = byteCounter+1;
					}
					printResults(this.answerType);
				}
			}
		}
		//if it cannot find the answer resources record
		else {
			System.out.println("NOTFOUND");
		}		
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	public String formatAcsiiString (String s ) {
		StringBuilder sb = new StringBuilder(); 
		int length = s.length()-1; 
		int counter = 0;
		for (char c : s.toCharArray() ) {
			if (Character.isLetterOrDigit(c)) {
				sb.append(new String(new char [] {c} ));
			}
			else{
				if(counter != 0 && counter != length ) {
					sb.append(".");
				}
			}
			counter++;
		}	
		return sb.toString();
	}

	
	/**
	 * Prints all corresponding results depending on the type of query 
	 * @param type
	 */
	public void printResults(int type) {
		if (type == TYPE_A_RR){
			System.out.println("IP 		" + this.answerRdata + " 		"+this.answerTTL+"		"+((this.answerAA == 0)? "Non-Authoritative":"Authoritative"));
		}
		if (type == TYPE_CNAME_RR) {
			System.out.println("CNAME 		" + this.answerCname +" 		"+this.answerTTL+"		"+((this.answerAA == 0)? "Non-Authoritative":"Authoritative"));
		}
		if (type == TYPE_NS_RR) {
			System.out.println("NS 		" + this.answerCname + " 		"+this.answerTTL+"		"+((this.answerAA == 0)? "Non-Authoritative":"Authoritative"));
		}
		if (type == TYPE_MX_RR) {
			System.out.println("MX 		" + this.answerCname + "		" + this.answerPreference + " 		"+this.answerTTL+"		"+ ((this.answerAA == 0)? "Non-Authoritative":"Authoritative"));
		}
	}

	

}

