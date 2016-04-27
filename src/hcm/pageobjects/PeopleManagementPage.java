package hcm.pageobjects;

import org.openqa.selenium.WebDriver;


public class PeopleManagementPage extends WorkforceStructureTasksPage{

	public PeopleManagementPage(WebDriver driver){
		super(driver);
	}	
	
	@Override
    public String getPageId()
    {
        return "/hcmCore/faces/HcmIntWA";
    }
}
