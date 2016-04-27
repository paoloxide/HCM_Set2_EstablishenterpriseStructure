package hcm.tests.testscripts.workforce.development;

import static util.ReportLogger.logFailure;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import common.BaseTest;
import common.CustomRunnable;
import common.TaskUtilities;
import hcm.pageobjects.CareerDevelopmentPage;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;

import static util.ReportLogger.log;

public class ManageWorkerGoalSettingValidation extends BaseTest{

	private static final int MAX_TIME_OUT = 60;
	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		validateWorkerGoalSetting();
	  
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

	public void validateWorkerGoalSetting() throws Exception{
		
		LoginPage login = new LoginPage(driver);
		//takeScreenshot();
		login.enterUserID(5);
		login.enterPassword(6);
		login.clickSignInButton();
		
		FuseWelcomePage welcome = new FuseWelcomePage(driver);
		//takeScreenshot();
		welcome.clickNavigator("More...");
		//clickNavigationLink("Career Development");
		TaskUtilities.clickNavLink(By.xpath("//span[text()='My Workforce']/../../div/table/tbody/tr/td/a[text()='Career Development']"));
		
		CareerDevelopmentPage task = new CareerDevelopmentPage(driver);
		takeScreenshot();
		
		//Variable Declaration
		final String addGoalLink = "//span[text()='Add Goal']";
		final String newGoalLink = "//td[text()='New Goal']";
		final String saveAndcloseButton = "//button[text()='ave and Close']";
		int inputLabel = 1;
		int inputs = 2;
		int testData = 7;
		String labelLocator, labelPath, dataLocator, dataPath;
		
		Thread.sleep(500);
		TaskUtilities.customWaitForElementVisibility(addGoalLink, MAX_TIME_OUT);
		
		task.clickAddGoal();
		TaskUtilities.customWaitForElementVisibility(newGoalLink, MAX_TIME_OUT);
		Thread.sleep(250);
		takeScreenshot();
		task.selectNewGoal();
		takeScreenshot();
		
		TaskUtilities.customWaitForElementVisibility(saveAndcloseButton, MAX_TIME_OUT);
		labelLocator = getExcelData(inputLabel, testData, "text");
		labelPath = TaskUtilities.retryingSearchInput(labelLocator);
		dataLocator = getExcelData(inputs, testData, "text");
		TaskUtilities.retryingFindClick(By.xpath(labelPath));
		task.enterTextByXpath(labelPath, dataLocator);
		takeScreenshot();
		
		testData += 1;
		labelLocator = getExcelData(inputLabel, testData, "text");
		labelPath = TaskUtilities.retryingSearchInput(labelLocator);
		dataLocator = getExcelData(inputs, testData, "text");
		dataPath = labelPath+"/option[text()='"+dataLocator+"']";
		TaskUtilities.customWaitForElementVisibility(labelPath, MAX_TIME_OUT);
		
		TaskUtilities.retryingFindClick(By.xpath(labelPath));
		Thread.sleep(500);
		takeScreenshot();
		TaskUtilities.retryingFindClick(By.xpath(dataPath));
		Thread.sleep(750);
		takeScreenshot();
		
		TaskUtilities.retryingFindClick(By.xpath(saveAndcloseButton));
		System.out.println("Clicking Save and Close...");
		Thread.sleep(500);
		takeScreenshot();
		
		TaskUtilities.customWaitForElementVisibility(addGoalLink, MAX_TIME_OUT, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				TaskUtilities.retryingFindClick(By.xpath(saveAndcloseButton));
				TaskUtilities.jsCheckMessageContainer(); 
				TaskUtilities.jsCheckMissedInput();
			}
		});
		Thread.sleep(1000);
		takeScreenshot();
		
		log("Worker Goal Setting Validation Completed...");
		System.out.println("Worker Goal Setting Validation Completed...");
		
	}
		
}