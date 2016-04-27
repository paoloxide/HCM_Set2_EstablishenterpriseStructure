package hcm.tests.testscripts.workforce.development;

import static util.ReportLogger.logFailure;
import static util.ReportLogger.log;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;

import common.BaseTest;
import common.CustomRunnable;
import common.TaskUtilities;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;

public class ManageGoalValueSets extends BaseTest{
	private final static int MAX_TIME_OUT = 30; //in seconds....
	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		manageGoalValueSets();
	  
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

	public void manageGoalValueSets() throws Exception{
		
		LoginPage login = new LoginPage(driver);
		//takeScreenshot();
		login.enterUserID(5);
		login.enterPassword(6);
		login.clickSignInButton();
		
		FuseWelcomePage welcome = new FuseWelcomePage(driver);
		//takeScreenshot();
		welcome.clickNavigator("More...");
		clickNavigationLink("Setup and Maintenance");
		
		TaskListManagerTopPage task = new TaskListManagerTopPage(driver);
		//takeScreenshot();
		
		task.clickTask("Manage Implementation Projects");
		Thread.sleep(1000);
		
		int inputs = TestCaseRow-1;
		int inputLabel = 2;
		int labelGroup = 1;
		int curTestData = 7;
		String labelLocator ="", labelLocatorPath="";
		String dataLocator, dataPath, gotoTaskPath, type;
		
		task.waitForElementToBeClickable(MAX_TIME_OUT, "//button[text()='D']");
		Thread.sleep(2000);
		
		//Search Project Name
		String searchName = "Name";
		String searchData = getExcelData(inputs, curTestData, "text");
		System.out.println("Now Holding: "+searchData);
		String searchDataLink = "//a[text()='"+searchData+"']";
		String dataSearchPath = TaskUtilities.retryingSearchInput(searchName);
		TaskUtilities.jsFindThenClick(dataSearchPath);
		System.out.println("New search path: "+dataSearchPath);
		task.enterTextByXpath(dataSearchPath, searchData);
				
		task.waitForElementToBeClickable(MAX_TIME_OUT, "//button[text()='Search']");
		task.clickSearchButton();
		Thread.sleep(250);
		task.clickSearchButton();
		takeScreenshot();
		
		TaskUtilities.customWaitForElementVisibility(searchDataLink, MAX_TIME_OUT);
		TaskUtilities.retryingFindClick(By.xpath("//h2[text()='Search Results']"));
		Thread.sleep(1000);
		System.out.println("Now Clicking: "+searchDataLink);
		driver.findElement(By.xpath(searchDataLink+"/..")).click();
		TaskUtilities.retryingFindClick(By.linkText(searchData));
		Thread.sleep(1000);
		//END Search for Project Name
		
		//Move cursor to folder name.....//START OF FOLDER EXPANSION
		curTestData += 1; //Expand Task Folder
		dataLocator = getExcelData(inputs, curTestData, "text");
		dataPath = "//div[text()='"+dataLocator+"']";
		TaskUtilities.customWaitForElementVisibility(dataPath, MAX_TIME_OUT);
		if(is_element_visible(dataPath+"/span/a[contains(@title,'Expand')]","xpath")){
			
			TaskUtilities.retryingFindClick(By.xpath(dataPath));
			task.clickExpandFolder(dataPath);
			Thread.sleep(1000);
			
		}
		
		curTestData += 1; //Expand Task Folder
		dataLocator = getExcelData(inputs, curTestData, "text");
		dataPath = "//div[text()='"+dataLocator+"']";
		TaskUtilities.customWaitForElementVisibility(dataPath, MAX_TIME_OUT);
		if(is_element_visible(dataPath+"/span/a[contains(@title,'Expand')]","xpath")){
			
			TaskUtilities.retryingFindClick(By.xpath(dataPath));	
			task.clickExpandFolder(dataPath);
			Thread.sleep(1000);
			
		}
		//END OF FOLDER EXPANSION
		
		//Moving the cursor.....
		curTestData += 1;
		dataLocator = getExcelData(inputs, curTestData, "text");
		dataPath = "//div[text()='"+dataLocator+"']";
		gotoTaskPath = dataPath+"/../../td/a[@title='Go to Task']";
		TaskUtilities.customWaitForElementVisibility(gotoTaskPath, MAX_TIME_OUT);
		System.out.println("Now clicking Go to Task....."+ gotoTaskPath);
		TaskUtilities.retryingFindClick(By.xpath(gotoTaskPath));
		
		TaskUtilities.customWaitForElementVisibility("//h1[text()='"+dataLocator+"']", MAX_TIME_OUT);
		Thread.sleep(1000);
		takeScreenshot();
		
		//This is for L4 tasks;;
		curTestData += 1;
		
		//Check if action is ADD/DELETE.....
		curTestData += 1;
		dataLocator = getExcelData(inputs, curTestData, "text");
		String actionLocator = dataLocator;
		
		if(dataLocator.contentEquals("ADD")){
				
				TaskUtilities.jsFindThenClick("//a/img[@title='Create']");
				TaskUtilities.customWaitForElementVisibility("//h1[text()='Create Value Set']", MAX_TIME_OUT);
				Thread.sleep(250);
			
				curTestData += 1;//Input all data...
				labelLocator = getExcelData(inputLabel, curTestData, "text");
				while(!labelLocator.contentEquals("Value") && getExcelData(inputLabel, curTestData, "text").length()>0){
					System.out.println("\n**********");
					String labelGroupLocator = getExcelData(labelGroup, curTestData, "text");
					type = TaskUtilities.getdataLocatorType(labelLocator);
					if(!labelGroupLocator.contentEquals("Independent Value Set")){
						
						labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
					} else{
						
						labelLocatorPath = "//tr[contains(@id,'independent')]/td/span/input";
					}

					dataLocator = getExcelData(inputs, curTestData, type);
					TaskUtilities.customWaitForElementVisibility(labelLocatorPath, MAX_TIME_OUT, new CustomRunnable() {
						
						@Override
						public void customRun() throws Exception {
							// TODO Auto-generated method stub
							TaskUtilities.jsCheckMissedInput();
							TaskUtilities.jsCheckMessageContainer();
						}
					});
					
					if(!labelLocatorPath.contains("select")){
						
							TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
							takeScreenshot();
							
						} else if(labelLocatorPath.contains("select")){
							
							TaskUtilities.consolidatedInputSelector(labelLocatorPath, dataLocator);
							
						}
					
					//Input on this label triggers others...
					if(labelLocator.contentEquals("Validation Type")){
							String newLabelLocator = getExcelData(inputLabel, curTestData+4, "text");
							TaskUtilities.customWaitForElementVisibility("//td/label[text()='"+newLabelLocator+"']", MAX_TIME_OUT);
						} else if(labelLocator.contentEquals("Value Data Type")){
							String newLabelLocator = getExcelData(inputLabel, curTestData+1, "text");
							TaskUtilities.customWaitForElementVisibility("//td/label[text()='"+newLabelLocator+"']", MAX_TIME_OUT);
						}
					
					curTestData += 1;
					labelLocator = getExcelData(inputLabel, curTestData, "text");
				}
				
				takeScreenshot();
				TaskUtilities.customWaitForElementVisibility("//button[text()='Save']", MAX_TIME_OUT, new CustomRunnable() {
					
					@Override
					public void customRun() throws Exception {
						// TODO Auto-generated method stub
						TaskUtilities.jsCheckMessageContainer();
						TaskUtilities.jsCheckMissedInput();
					}
				});
				Thread.sleep(250);
				TaskUtilities.jsFindThenClick("//button[text()='Save']");
				Thread.sleep(250);
				
				TaskUtilities.customWaitForElementEnablement(By.xpath("//button[text()='Manage Values']"), MAX_TIME_OUT, new CustomRunnable() {
					
					@Override
					public void customRun() throws Exception {
						TaskUtilities.jsCheckMissedInput();
						TaskUtilities.jsCheckMessageContainer();
					}
				});
				
				TaskUtilities.jsFindThenClick("//button[text()='Manage Values']");
				TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Values']", MAX_TIME_OUT);
				Thread.sleep(250);
				takeScreenshot();
				TaskUtilities.jsFindThenClick("//a/img[@title='Create']");
				TaskUtilities.customWaitForElementVisibility("//h1[text()='Create Value']", MAX_TIME_OUT);
				Thread.sleep(250);
				
				while(getExcelData(inputLabel, curTestData, "text").length()>0){
					System.out.println("\n**********");
					labelLocator = getExcelData(inputLabel, curTestData, "text");
					labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
					type = TaskUtilities.getdataLocatorType(labelLocator);
					dataLocator = getExcelData(inputs, curTestData, type);
					
					if(!labelLocatorPath.contains("select")){
							
							TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
							takeScreenshot();
						} else if(labelLocatorPath.contains("select")){
							
							TaskUtilities.consolidatedInputSelector(labelLocatorPath, dataLocator);
							takeScreenshot();
						}
					
					curTestData += 1;
				}
				Thread.sleep(250);
				takeScreenshot();
				TaskUtilities.jsFindThenClick("//button[text()='ave and Close']");
				TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Values']", MAX_TIME_OUT, new CustomRunnable() {
					
					@Override
					public void customRun() throws Exception {
						// TODO Auto-generated method stub
						TaskUtilities.jsCheckMessageContainer();
						TaskUtilities.jsCheckMissedInput();
					}
				});
				
				TaskUtilities.customWaitForElementVisibility("//button[text()='D']", MAX_TIME_OUT);
				TaskUtilities.jsFindThenClick("//button[text()='D']");	
				TaskUtilities.customWaitForElementVisibility("//h1[text()='Create Value Set']", MAX_TIME_OUT);
				Thread.sleep(250);
				TaskUtilities.jsFindThenClick("//button[text()='ave and Close']");
			
			} else if(dataLocator.contentEquals("DEL")){
				//DEL action goes here...
				labelLocator = getExcelData(inputLabel, 13, "text");
				labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
				type = TaskUtilities.getdataLocatorType(labelLocator);
				dataLocator = getExcelData(inputs, 13, type);
				TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
				driver.findElement(By.xpath(labelLocatorPath)).sendKeys(Keys.TAB);
				TaskUtilities.jsFindThenClick("//button[text()='Search']");
				Thread.sleep(1000);
				TaskUtilities.jsFindThenClick("//button[text()='Search']");
				TaskUtilities.customWaitForElementVisibility("//td[text()='"+dataLocator+"']", MAX_TIME_OUT);
				TaskUtilities.customWaitForElementVisibility("//a[@href='#']/img[@title='Delete']", MAX_TIME_OUT);
				TaskUtilities.jsFindThenClick("//a[@href='#']/img[@title='Delete']");
			}
		
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Goals Value Sets']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//button[text()='ave and Close']");
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'"+getExcelData(inputs, 7, "text")+"')]", MAX_TIME_OUT);
		TaskUtilities.retryingFindClick(By.xpath(gotoTaskPath));
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Goals Value Sets']", MAX_TIME_OUT);
		//In the end, search for the created goal
		labelLocator = getExcelData(inputLabel, 13, "text");
		labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
		type = TaskUtilities.getdataLocatorType(labelLocator);
		dataLocator = getExcelData(inputs, 13, type);
		TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
		TaskUtilities.jsFindThenClick("//button[text()='Search']");
		Thread.sleep(1000);
		TaskUtilities.jsFindThenClick("//button[text()='Search']");
		if(actionLocator.contentEquals("ADD")){
			TaskUtilities.customWaitForElementVisibility("//td[text()='"+dataLocator+"']", MAX_TIME_OUT);
		}
		takeScreenshot();
		Thread.sleep(1000);
		
		log("Manage Worker Value Goal Settings creation has been completed.");
		System.out.println("Manage Worker Value Goal Settings creation has been completed.");
		
	}
}
