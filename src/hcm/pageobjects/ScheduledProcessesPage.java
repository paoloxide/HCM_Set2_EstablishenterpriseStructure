package hcm.pageobjects;

import org.openqa.selenium.WebDriver;

import common.BasePage;

public class ScheduledProcessesPage extends BasePage{

	public ScheduledProcessesPage(WebDriver driver){
		super(driver);
	}
	
	@Override
    public String getPageId()
    {
		//login-aufsn4x0cba.oracleoutsourcing.com
        return "/essUi/faces/EssUIShell";
    }
	
}
