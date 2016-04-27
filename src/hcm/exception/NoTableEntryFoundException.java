package hcm.exception;

public class NoTableEntryFoundException extends Exception{
	public NoTableEntryFoundException(){}
	
	public NoTableEntryFoundException(String msg){
		super(msg);
	}
}
