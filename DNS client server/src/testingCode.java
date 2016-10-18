
public class testingCode {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
         
		int a = 5;
		String b =  Integer.toBinaryString(a);
		byte demo = Byte.valueOf(b);
		String ss1 = String.format("%8s", Integer.toBinaryString(demo & 0xFF)).replace(' ', '0');
		System.out.println("//////////" + ss1);
		
	
		byte yy = 0x0;
		System.out.println("This is :" + yy);
		
		/*
		System.out.println(byt[0]);
		System.out.println(byt[1]);
		
		String ss1 = String.format("%8s", Integer.toBinaryString(byt[0] & 0xFF)).replace(' ', '0');
		String ss2 = String.format("%8s", Integer.toBinaryString(byt[1] & 0xFF)).replace(' ', '0');
		
		System.out.println(ss1);
		System.out.println(ss2);
		*/
		System.out.println("//////////");
		short dd =(short) 0xFF55;
		byte [] dnsheader = new byte [5];
		dnsheader [0] =(byte)(dd >>> 8);
		dnsheader[1] = (byte)(dd);
		String s1 = String.format("%8s", Integer.toBinaryString(dnsheader[0] & 0xFF)).replace(' ', '0');
		String s2 = String.format("%8s", Integer.toBinaryString(dnsheader[1] & 0xFF)).replace(' ', '0');
		System.out.println(s1); // 10000001
		System.out.println(s2);
		byte ee =  (byte)0x03 << 1;
		byte [] gg = new byte [5];
		 gg[0] = (byte) (dd | ee) ;
		System.out.println("this is" + Integer.toBinaryString(gg[0]));
		
		
		
		
		
	}

}
