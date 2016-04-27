package common;

public class RoleAlreadyAssignedException extends Exception{

	public RoleAlreadyAssignedException(){}
	
	public RoleAlreadyAssignedException(String msg){
		super(msg);
	}

}
