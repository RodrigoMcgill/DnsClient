/*
 * *
 @Authors: Rodrigo Mendoza Arbizu & Kawtar Berkouk
 *
 */
package DNS_Package;
public class rCODE_ERROR_CHECK {
    boolean error = true;
	int RCODE = 0;
	/**
	 * Function that takes as the RCODE from the dns packet.
	 * If RCODE is 0=>  means no error , return true and CONTINUE program
	 * Else verify the number and display appropriate error to the user	and STOP program
	 * @param RCODE_STATUS
	 * @return
	 */
	public boolean checkDNSTypeError(int RCODE_STATUS){
	
		if (RCODE_STATUS != 0) {
		
			switch(RCODE_STATUS){
				case '1':
						System.out.println("Error	Formal error  The name server was unbale to interpret the query");
						break;
				case '2':
						System.out.println("Error	Server Failure the name of the server was unable to process this query due to a problem with the name server");
						break;
				case '3':
						System.out.println("Error	Name error meaningful only for responses from an authoritative name server, this code signifies that the domain name referenced in the query does not exist");
						break;
				case '4':
						System.out.println("Error	Not implemented the name server does not support the resquested kind of query");
						break;
				case '5':
						System.out.println("Error	Refused The name server refuses to perform the requested operation for policy reasons");
						break;
			}
			error = false;
		}
		return error;
	}
}
