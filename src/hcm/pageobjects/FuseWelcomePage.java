package hcm.pageobjects;

import org.openqa.selenium.WebDriver;
import common.BasePage;
import static util.ReportLogger.log;

public class FuseWelcomePage extends BasePage{
	
	public FuseWelcomePage(WebDriver driver){
		super(driver);
	}
	
	@Override
    public String getPageId()
    {
        return "/hcmCore/faces/FuseWelcome";
    }

	public void clickUserMenu(String menu){
		click("pt1:_UIScmil1u::icon");
		log("Clicked the Settings and Action...");
		System.out.println("Clicked the Settings and Action...");		
		
		clickByXpath("//div/a[contains(text(),'"+ menu +"')]");		
		log("Clicking " + menu +"...");
		System.out.println("Clicking " + menu +"...");
	}
	
	public void clickNavigator(String menu){
		
		clickByXpath("//img[@alt='Navigator']");
		log("Clicked the Navigator...");
		System.out.println("Clicked the Navigator...");	
		
	}

}
