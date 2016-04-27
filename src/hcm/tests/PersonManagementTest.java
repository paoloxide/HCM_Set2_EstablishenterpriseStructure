package hcm.tests;

import static util.ReportLogger.log;
import static util.ReportLogger.logFailure;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriverException;
import org.testng.annotations.Test;

import common.BaseTest;
import common.CustomRunnable;
import common.DuplicateEntryException;
import common.TaskUtilities;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.WorkforceStructureTasksPage;

public class PersonManagementTest extends BaseTest{
	private static final int MAX_TIME_OUT = 30;
	private final int assignee = 7;
	private final int label = 7;
	private static int input = 8;
	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		assignDepartment();
	  
	  	}
	
        catch (AssertionError ae)
        {
            //takeScreenshot();
            logFailure(ae.getMessage());

            throw ae;
        }
        catch (Exception e)
        {
            //takeScreenshot();
            logFailure(e.getMessage());

            throw e;
        }
    }

	public void assignDepartment() throws Exception{
		
		LoginPage login = new LoginPage(driver);
		takeScreenshot();
		login.enterUserID(5);
		login.enterPassword(6);
		login.clickSignInButton();
		
		FuseWelcomePage welcome = new FuseWelcomePage(driver);
		takeScreenshot();
		welcome.clickNavigator("More...");
		clickNavigationLink("Workforce Structures");
		
		WorkforceStructureTasksPage task = new WorkforceStructureTasksPage(driver);
		takeScreenshot();
		
		while(getExcelData(input, assignee, "text").length()>0){
			TaskUtilities.customWaitForElementVisibility("//td/a[text()='Person Management']", MAX_TIME_OUT, new CustomRunnable() {
				
				@Override
				public void customRun() throws Exception {
					// TODO Auto-generated method stub
					try{
							TaskUtilities.jsFindThenClick("//img[@alt='Navigator']");
						} catch(WebDriverException e){
							Thread.sleep(750);
						}
				}
			}); Thread.sleep(2000);

			TaskUtilities.jsFindThenClick("//td/a[text()='Person Management']");
			TaskUtilities.customWaitForElementVisibility("//h2[text()='Search']", MAX_TIME_OUT);
		
			//Search for person credentials here...
			String person = getExcelData(input, assignee, "text");
			String labelLocator = "Name";
			String labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
			TaskUtilities.consolidatedInputEncoder(welcome, labelLocatorPath, person);
			takeScreenshot(); Thread.sleep(500);
			driver.findElement(By.xpath(labelLocatorPath)).sendKeys(Keys.ENTER);
			
			TaskUtilities.jsFindThenClick("//button[text()='Search']");
			TaskUtilities.customWaitForElementVisibility("//a[text()='"+person+"']", MAX_TIME_OUT);
			TaskUtilities.jsFindThenClick("//a[text()='"+person+"']");
			takeScreenshot();
			
			TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Person']", MAX_TIME_OUT);
			takeScreenshot();
			
			Thread.sleep(750);
			TaskUtilities.jsFindThenClick("//li/a[text()='Manage Employment']");
			TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Employment']", MAX_TIME_OUT);
			
			TaskUtilities.jsFindThenClick("//span[text()='Edit']/../../../td/div");
			TaskUtilities.customWaitForElementVisibility("//tr/td[text()='Update']", MAX_TIME_OUT);
			TaskUtilities.jsFindThenClick("//tr/td[text()='Update']");
			TaskUtilities.customWaitForElementVisibility("//div[contains(text(),'Update')]", MAX_TIME_OUT);
			
			//Assigning Action
			//String dataLocator = getExcelData(input, assignee+1, "text");
			String dataLocator = "Add New Department";
			//labelLocator = getExcelData(label, assignee+1, "text");
			labelLocator = "Action";
			labelLocator = task.filterDataLocator(labelLocator);
			labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
			TaskUtilities.consolidatedInputSelector(labelLocatorPath, dataLocator);
			driver.findElement(By.xpath(labelLocatorPath)).sendKeys(Keys.TAB);
			
			dataLocator = getExcelData(input, assignee+1, "date");
			labelLocator = getExcelData(label, assignee+1, "text");
			labelLocator = task.filterDataLocator(labelLocator);
			labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
			TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
			TaskUtilities.jsFindThenClick("//button[text()='O']");
			try{
					TaskUtilities.customWaitForElementVisibility("//span[text()='Review']", MAX_TIME_OUT, new CustomRunnable() {
						
						@Override
						public void customRun() throws Exception {
							// TODO Auto-generated method stub
							TaskUtilities.jsCheckMissedInput();
							TaskUtilities.jsCheckMessageContainer();
						}
					});
				} catch(DuplicateEntryException e){
					Date now = new Date();
	      	  		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					String toDate = sdf.format(now);
					
					dataLocator = toDate;
					TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
					TaskUtilities.jsFindThenClick("//button[text()='O']");
				}
			
			
			dataLocator = getExcelData(input, assignee+3, "text");
			TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Add New Department')]", MAX_TIME_OUT);
			TaskUtilities.customWaitForElementVisibility("//label[text()='Department']", MAX_TIME_OUT, new CustomRunnable() {
				
				@Override
				public void customRun() throws Exception {
					// TODO Auto-generated method stub
					driver.findElement(By.xpath("//span[text()='C']")).sendKeys(Keys.TAB);
				}
			});
	
			labelLocator = "Department";
			labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
			TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
			
			takeScreenshot();
			TaskUtilities.jsFindThenClick("//span[text()='Save']");
			TaskUtilities.customWaitForElementVisibility("//button[text()='O']", MAX_TIME_OUT);
			TaskUtilities.jsFindThenClick("//button[text()='O']"); Thread.sleep(7500);
			
			TaskUtilities.jsFindThenClick("//span[text()='Review']");
			TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Review')]", MAX_TIME_OUT, new CustomRunnable() {
				
				@Override
				public void customRun() throws Exception {
					// TODO Auto-generated method stub
					TaskUtilities.jsCheckMissedInput();
					TaskUtilities.jsCheckMessageContainer();
				}
			});
			
			
			takeScreenshot();
			TaskUtilities.jsFindThenClick("//span[text()='Sub']");
			TaskUtilities.customWaitForElementVisibility("//button[text()='es']", MAX_TIME_OUT, new CustomRunnable() {
				
				@Override
				public void customRun() throws Exception {
					// TODO Auto-generated method stub
					TaskUtilities.jsFindThenClick("//span[text()='Sub']"); Thread.sleep(2000);
				}
			});
			TaskUtilities.jsFindThenClick("//button[text()='es']");
			TaskUtilities.customWaitForElementVisibility("//button[text()='O']", MAX_TIME_OUT);
			TaskUtilities.jsFindThenClick("//button[text()='O']");
			Thread.sleep(2000);
			takeScreenshot();
			
			TaskUtilities.customWaitForElementVisibility("//button[text()='D']", MAX_TIME_OUT);
			TaskUtilities.jsFindThenClick("//button[text()='D']");
			
			TaskUtilities.customWaitForElementVisibility("//button[text()='Yes']", MAX_TIME_OUT);
			TaskUtilities.jsFindThenClick("//button[text()='Yes']");
			
			TaskUtilities.customWaitForElementVisibility("//span[text()='Sub']", MAX_TIME_OUT);
			TaskUtilities.jsFindThenClick("//span[text()='Sub']");
			
			TaskUtilities.customWaitForElementVisibility("//button[text()='es']", MAX_TIME_OUT);
			TaskUtilities.jsFindThenClick("//button[text()='es']");
			
			TaskUtilities.customWaitForElementVisibility("//button[text()='O']", MAX_TIME_OUT);
			TaskUtilities.jsFindThenClick("//button[text()='O']");
			
			input += 1;
		}
		
		Thread.sleep(5000);
		log("Department Assignment has been finished...");
		System.out.print("Department Assignment has been finished...");
	}

}
