package hcm.exception;

public class DuplicateEnterpriseNameException extends Exception{
	
	public DuplicateEnterpriseNameException(){}
	
	public DuplicateEnterpriseNameException(String msg){
		super(msg);
	}
}
