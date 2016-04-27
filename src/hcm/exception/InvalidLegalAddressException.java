package hcm.exception;

public class InvalidLegalAddressException extends Exception{
	
	public InvalidLegalAddressException(){}
	
	public InvalidLegalAddressException(String msg){
		super(msg);
	}
}
