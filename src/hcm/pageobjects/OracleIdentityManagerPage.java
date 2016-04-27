package hcm.pageobjects;

import org.openqa.selenium.WebDriver;

import common.BasePage;
import common.TaskUtilities;

public class OracleIdentityManagerPage extends BasePage{
	
	public OracleIdentityManagerPage(WebDriver driver){
		super(driver);
	}
	
	@Override
    public String getPageId()
    {
		//login-aufsn4x0cba.oracleoutsourcing.com
        return "login-aufsn4x0cba.oracleoutsourcing.com/oim/faces/pages/Self.jspx";
    }
	
	public void clickAdminLink(){
		TaskUtilities.jsFindThenClick("//span[text()='Administration ']");
	}

}
