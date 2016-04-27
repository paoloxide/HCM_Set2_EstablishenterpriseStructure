package hcm.tests.case1;

import static util.ReportLogger.log;
import static util.ReportLogger.logFailure;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.Test;

import common.BaseTest;
import common.CustomRunnable;
import common.TaskUtilities;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;

public class ConfigureImplementationOfferingsTest extends BaseTest{
	private static final int MAX_TIME_OUT = 30;
	private int testLabel = 9;
	private int labels = 10;
	private int inputs = 11;
	private int colNum = 7;
	
	private String dataLocator="", status="";
	private String taskLocator, taskLocatorPath, statusLinkPath;
	private String implementString;
	
	private boolean isScrollingDown = true;
	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		configureOfferings();
	  
	  	}
	
        catch (AssertionError ae)
        {
            takeScreenshot();
            logFailure(ae.getMessage());

            throw ae;
        }
        catch (Exception e)
        {
            takeScreenshot();
            logFailure(e.getMessage());

            throw e;
        }
    }
	
	public void configureOfferings() throws Exception{
		
		LoginPage login = new LoginPage(driver);
		takeScreenshot();
		login.enterUserID(5);
		login.enterPassword(6);
		login.clickSignInButton();
		
		FuseWelcomePage welcome = new FuseWelcomePage(driver);
		takeScreenshot();
		welcome.clickNavigator("More...");
		clickNavigationLink("Setup and Maintenance");
			
		TaskListManagerTopPage task = new TaskListManagerTopPage(driver);
		takeScreenshot();
		
		TaskUtilities.customWaitForElementVisibility("//a[text()='Configure Offerings']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//a[text()='Configure Offerings']");
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Configure Offerings']", MAX_TIME_OUT);
		
		while(!dataLocator.contentEquals("Select Offerings to Implement")){
			dataLocator = getExcelData(testLabel, colNum, "text");
			colNum += 1;
		}
		
		colNum -= 1;
		taskloop:
		while(getExcelData(labels, colNum, "text").length() > 0){
			taskLocator = getExcelData(labels, colNum, "text");
			taskLocatorPath = "//div[text()='"+taskLocator+"']";
			implementString = getExcelData(inputs, colNum, "text");
			
			if(implementString.equalsIgnoreCase("Yes")){
				TaskUtilities.customWaitForElementVisibility("//div[text()='"+taskLocator+"']", 60, new CustomRunnable() {
					
					@Override
					public void customRun() throws Exception {
						// TODO Auto-generated method stub
						isScrollingDown = TaskUtilities.scrollDownToElement(isScrollingDown, "big");
					}
				});
				
				TaskUtilities.jsScrollIntoView(taskLocatorPath);
				TaskUtilities.jsFindThenClick(taskLocatorPath);
				
				if(is_element_visible(taskLocatorPath+"/../..//a[@title='Collapse' or @title='Expand']", "xpath")){
						System.out.print("Available sub task found...");
						TaskUtilities.retryingFindClick(By.xpath("//img[@title='Show as Top']/..[@href='#']"));
						TaskUtilities.retryingFindClick(By.xpath(taskLocatorPath));
						TaskUtilities.customWaitForElementVisibility("//a[@title='Show Hierarchy']", MAX_TIME_OUT);
					} else{
						if(!is_element_visible(taskLocatorPath+"/../..//input[@checked='']", "xpath")){
							System.out.print("No sub task available...");
							TaskUtilities.jsFindThenClick(taskLocatorPath+"/../..//input");
						}
						
						colNum += 1;
						takeScreenshot();
						continue taskloop;
					}
				

				List<String> visibleTask = task.tickExpandVisibleTasks(MAX_TIME_OUT);
				String statsPath = "//span//a[contains(@id,'Status')]";

				TaskUtilities.scrollDownToElement(false, "any");		
				for(String visTask: visibleTask){
					
					String visTaskPath = visTask;
					//Tick checkbox...
					TaskUtilities.retryingFindClick(By.xpath(visTaskPath));
					TaskUtilities.jsScrollIntoView(visTaskPath);
					TaskUtilities.retryingFindClick(By.xpath(visTaskPath));
					
					statusLinkPath = visTaskPath+statsPath;
					try{
							status = driver.findElement(By.xpath(statusLinkPath)).getText();
						} catch(NoSuchElementException e){
							if(is_element_visible(visTaskPath+"//span[text()='Not Started']", "xpath")){
								status = "Not Started";
							}
						}
					
					if(status.contentEquals("Not Started")){
						if(!TaskUtilities.is_element_visible(visTaskPath+"//input[@checked='']", "xpath")){
								System.out.println("Ticking all visible folder");
								TaskUtilities.retryingFindClick(By.xpath(visTaskPath+"//input"));
							}
					}
				}
				takeScreenshot();
				//statusLinkPath = "//div[text()='"+taskLocator+"']/../..//span//a[contains(@id,'Status')]";
				//String status = driver.findElement(By.xpath(statusLinkPath)).getText();
				
				//Go back to main configuration sheet...
				TaskUtilities.jsFindThenClick("//a/img[@title='Go to Top']");
				TaskUtilities.fluentWaitForElementInvisibility("//a[@title='Show Hierarchy']", "", MAX_TIME_OUT);
			
			}//else skips all...
			
			colNum += 1;
		
		}
		
		TaskUtilities.jsFindThenClick("//button[text()='ave and Close']");
		Thread.sleep(5000);
		
		log("Offerings Configuration has been completed.");
		System.out.println("Offerings Configuration has been completed.");
		
	}
	
}
