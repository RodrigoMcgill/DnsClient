import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Random;

public class dnsPacketStructure {
	private short ID;
	private byte QR = 0x00;
	private byte OPCODE = 0x00; 
	private byte AA, TC = 0x00;
	private byte RD = 0x01;
	private byte RA, Z, RCODE =  0x00;
	private byte QDCOUNT, ANCOUNT = 0x0001;
	private byte NSCOUNT, ARCOUNT = 0x0000;
    private byte qtype_entryy;
    private byte QCLASS = 0x01;
	private String domain_name;
	private byte[] dnsPacketHeader = new byte[12];
	private byte[] dnsPacketQuestion;
	private ByteBuffer  buff;
	
	
	public dnsPacketStructure(String qtype_entry,String domain_name){
		
		//acquiring packet header//
		 Random ranI = new Random();
	     ID = (short) ranI.nextInt(Short.MAX_VALUE);
	     String s99 = String.format("%8s", Integer.toBinaryString(ID & 0xFFFF)).replace(' ', '0');
	     System.out.println("the random nmber is:  " + s99);
		
	   	
		//fills the buffer with these values
		dnsPacketHeader[0] = (byte)(ID >>> 8);
		dnsPacketHeader[1] = (byte)(ID);
		dnsPacketHeader[2] = (byte)((QR << 7) | ( OPCODE << 3) | ( AA << 2) | (TC << 1) | RD) ;
		dnsPacketHeader[3] = (byte) ((RA << 7 ) |(Z << 4) | RCODE);
		dnsPacketHeader[4] = (byte) (0x00);
		dnsPacketHeader[5] = (byte) (QDCOUNT);
		dnsPacketHeader[6] = (byte) (0x00);
		dnsPacketHeader[7] = (byte) (ANCOUNT);
		dnsPacketHeader[8] = (byte) (0x00);
		dnsPacketHeader[9] = (byte)  (NSCOUNT);
		dnsPacketHeader[10] = (byte) (0x00);
		dnsPacketHeader[11] = (byte) (ARCOUNT);
		
		//for debugging purposes
		System.out.println("checking inside bufffer");
		String s1 = String.format("%8s", Integer.toBinaryString(dnsPacketHeader[0] & 0xFF)).replace(' ', '0');
		System.out.println(s1);
		String s2 = String.format("%8s", Integer.toBinaryString(dnsPacketHeader[1] & 0xFF)).replace(' ', '0');
		System.out.println(s2);
		String s3 = String.format("%8s", Integer.toBinaryString(dnsPacketHeader[2] & 0xFF)).replace(' ', '0');
		System.out.println(s3);
		String s4 = String.format("%8s", Integer.toBinaryString(dnsPacketHeader[3] & 0xFF)).replace(' ', '0');
		System.out.println(s4);
		String s5 = String.format("%8s", Integer.toBinaryString(dnsPacketHeader[4] & 0xFF)).replace(' ', '0');
		System.out.println(s5);
		String s6 = String.format("%8s", Integer.toBinaryString(dnsPacketHeader[5] & 0xFF)).replace(' ', '0');
		System.out.println(s6);
		String s7 = String.format("%8s", Integer.toBinaryString(dnsPacketHeader[6] & 0xFF)).replace(' ', '0');
		System.out.println(s7);
		String s8 = String.format("%8s", Integer.toBinaryString(dnsPacketHeader[7] & 0xFF)).replace(' ', '0');
		System.out.println(s8);
		String s9 = String.format("%8s", Integer.toBinaryString(dnsPacketHeader[8] & 0xFF)).replace(' ', '0');
		System.out.println(s9);
		String s10 = String.format("%8s", Integer.toBinaryString(dnsPacketHeader[9] & 0xFF)).replace(' ', '0');
		System.out.println(s10);
		String s11 = String.format("%8s", Integer.toBinaryString(dnsPacketHeader[10] & 0xFF)).replace(' ', '0');
		System.out.println(s11);
		String s12 = String.format("%8s", Integer.toBinaryString(dnsPacketHeader[11] & 0xFF)).replace(' ', '0');
		System.out.println(s12);
		
		//acquiring packing data//
		switch(qtype_entry){
			case "A" :	this.qtype_entryy =(byte)0x01;   //0000001
						break;
			case "MX":  this.qtype_entryy =(byte)0x02;    // 00000010
						break;
			case "NS":  this.qtype_entryy = 0x0f;    // 00001111
						break;
		}
		
		//encodes domain_name and adds it to the buffer.: encodes  number of characters and then the characters themselves
				String[] parts = domain_name.split("\\.");
				System.out.println("veryfying String: " + parts[0] + " " + parts[1] +" " +parts[2]);
		        
				int byteSize = 0;
				for(int i=0 ; i < parts.length ; i++){
					for(int j=0; j < parts[i].length(); j++){
						byteSize ++;
					}
				}
				
				byteSize += parts.length + 1 + 4;
				System.out.println("bytesize is : " + byteSize);
				
				 buff = ByteBuffer.allocate(byteSize);
				
				for (String k : parts) {
					buff.put((byte) k.length());
					try {
						buff.put(k.getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						System.out.println(e.getMessage());
					}
				}
				
			   //marks end of message with a 0
				buff.put((byte) 0x00);
				
				//putting QTYPE and  QCLASS
				buff.put((byte) 0x00);
				buff.put((byte)qtype_entryy);
				buff.put((byte) 0x00);
				buff.put((byte)QCLASS);
	}
	
	
	
	public byte[] getdnsPacketHeader(){
		return dnsPacketHeader;
	}
	
   public ByteBuffer getByteBuffer(){
	   System.out.println("calling getByteBuffer size is " + buff.remaining());
	   return buff;
   }
	
	 
}
