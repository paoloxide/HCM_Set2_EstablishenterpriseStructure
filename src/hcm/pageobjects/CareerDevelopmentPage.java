package hcm.pageobjects;

import static util.ReportLogger.log;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import common.BasePage;
import common.TaskUtilities;

public class CareerDevelopmentPage extends BasePage{

	public CareerDevelopmentPage(WebDriver driver){
		super(driver);
	}

	@Override
    public String getPageId()
    {
        return "/hcmTalent/faces/FuseOverview";
    }
	
	public void clickAddGoal(){
		
		driver.findElement(By.xpath("//a[contains(@id,'popEl')]")).click();
		log("Clicking Add Goal...");
		System.out.println("Clicking Add Goal...");
		
	}
	
	public void selectNewGoal(){
		driver.findElement(By.xpath("//td[text()='New Goal']")).click();		
		log("Selecting Goal...");
		System.out.println("Selecting Goal...");
	}
	
	public void clickOKButton(){
		
		TaskUtilities.jsFindThenClick("//button[text()='O']");
		log("Clicking OK...");
		System.out.println("Clicking OK...");

	}
	
}
