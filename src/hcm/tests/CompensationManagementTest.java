package hcm.tests;

import org.testng.annotations.Test;


import common.BaseTest;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;

import static util.ReportLogger.logFailure;

public class CompensationManagementTest extends BaseTest {
	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		managePerf();
	  
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

	public void managePerf() throws Exception{
			
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
		
		task.clickTask("Manage Implementation Projects");
		Thread.sleep(3000);
		takeScreenshot();
		
		startTableRep("Validation of the Current Screen");
		assertTextInElementPresent("//td[2]/div/h1", "Manage Implementation Projects");
		assertPageElementPresent("//span/div/div[2]", "Search");
		assertPageElementPresent("//span/div[2]/div[2]", "Search Results");
		endTableRep();

		task.clickCreateIcon();
		Thread.sleep(3000);
		
		waitForElement("pt1:USma:0:MAnt1:1:pt1:r1:1:ap1:soc3::content", "id");
		
		startTableRep("Validate that the created project is automatically assigned to the current user.");
		assertDefaultValue("pt1:USma:0:MAnt1:1:pt1:r1:1:ap1:soc3::content", 5);
		endTableRep();
			
		Thread.sleep(3000);
		task.clickNextButton();
		Thread.sleep(3000);
		takeScreenshot();
		
		if (is_element_visible("//tbody/tr/td/div[contains(text(),'"+getExcelData(7)+"')]/span/a[contains(@title,'Expand')]", "xpath")){
		
		task.clickExpandFolder(7);
		Thread.sleep(3000);
		}
		
		takeScreenshot();
		
		startTableRep("Validation of created tasks");
		assertTextInElementPresent("//tr/td/div/table/tbody/tr/td/div[contains(text(),'"+getExcelData(8)+"')]", 8);
		assertTextInElementPresent("//tr/td/div/table/tbody/tr/td/div[contains(text(),'"+getExcelData(9)+"')]", 9);
		assertTextInElementPresent("//tr/td/div/table/tbody/tr/td/div[contains(text(),'"+getExcelData(10)+"')]", 10);
		endTableRep();
		
		//task.clickMainTaskCheckbox(7);
		//task.clickSubTaskCheckbox(7,8);
		//task.clickSubTaskCheckbox(7,9);
		//task.clickSubTaskCheckbox(7,10);
		takeScreenshot();
		
		task.clickSaveAndOpenProjectButton();
		waitForElement("//button[text()='Edit']", "xpath");
		takeScreenshot();
				
		}
		 	  
	  }

