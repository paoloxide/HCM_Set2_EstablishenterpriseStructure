package common;

public class ReporterManager {
	
	public static String trimErrorMessage(String errMsg) {
		int enterIndex = errMsg.indexOf("\n");
		if(enterIndex != -1) errMsg = errMsg.substring(enterIndex);
		
		int tabIndex = errMsg.indexOf("\t");
		if(tabIndex != -1) errMsg = errMsg.substring(tabIndex);
		
		enterIndex = errMsg.indexOf("\n");
		if(enterIndex != -1) errMsg = errMsg.substring(enterIndex);
		
		int lastEnterIndex = errMsg.indexOf("\n");
		if(lastEnterIndex != -1 && lastEnterIndex-1 >= errMsg.length()){
			errMsg.substring(0, lastEnterIndex-1);
		}
		
		errMsg = errMsg.replaceAll("\\n", "\n[DIAGNOSTIC] ")
				.replaceAll("null", "")
				.replaceAll("\\t", "")
				.replaceAll("  ", "")
				.replaceAll("    ", "");
		
		return errMsg;
		
	}
}
