package hcm.tests;

import static util.ReportLogger.logFailure;

import java.util.List;

import org.testng.annotations.Test;

import common.BaseTest;
import common.CustomRunnable;
import common.TaskUtilities;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;

import org.openqa.selenium.By;

import static util.ReportLogger.log;

public class ManageWorkerGoalSettingLookups extends BaseTest{
	
	private final static int MAX_TIME_OUT = 60; //in seconds....
		
		@Test
		public void a_test() throws Exception  {
			testReportFormat();
		
		try{
			manageWorkerGoalSetting();
		  
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
	
	public void manageWorkerGoalSetting() throws Exception{
		
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
		//takeScreenshot();
		
		//Set Project Name...
		String projectName = TaskUtilities.getProjectName();
		if(!projectName.contentEquals("") || !projectName.isEmpty()){
			TaskUtilities.setProjectName(projectName, BaseTest.ExcelPath, 2);
		}
				
		int inputs = 2;
		int inputLabel = 1;
		int curTestData = 7;
		int firstInput = 13;
		String dataLocator, dataPath, rawDataLocPath, dataLocPath, gotoTaskPath, inputValue, type;
		List<String> tableInputs = null;
		
		task.waitForElementToBeClickable(MAX_TIME_OUT, "//button[text()='D']");
		Thread.sleep(2000);
		//task.retryingFindClick(By.xpath("//h1[text()='Implementation Project: Implementation Project-8']"));
		
		//Search for the Project Name.....
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
		//takeScreenshot();
				
		TaskUtilities.customWaitForElementVisibility(searchDataLink, MAX_TIME_OUT);
		TaskUtilities.retryingFindClick(By.xpath("//h2[text()='Search Results']"));
		Thread.sleep(1000);
		System.out.println("Now Clicking: "+searchDataLink);
		driver.findElement(By.xpath(searchDataLink+"/..")).click();
		TaskUtilities.retryingFindClick(By.linkText(searchData));
		Thread.sleep(1000);
		
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
		task.waitForElementToBeClickable(MAX_TIME_OUT, dataPath+"/span/a[contains(@title,'Collapse')]");
		//takeScreenshot();
		
		
		curTestData += 1;
		dataLocator = getExcelData(inputs, curTestData, "text");
		dataPath = "//div[text()='"+dataLocator+"']";
		if(is_element_visible(dataPath+"/span/a[contains(@title,'Expand')]","xpath")){
			
			TaskUtilities.retryingFindClick(By.xpath(dataPath));	
			task.clickExpandFolder(dataPath);
			Thread.sleep(1000);
		}		
		task.waitForElementToBeClickable(MAX_TIME_OUT, dataPath+"/span/a[contains(@title,'Collapse')]");
		//takeScreenshot();
		//END OF FOLDER EXPANSION
		
		//Moving the cursor.....
		curTestData += 1;
		dataLocator = getExcelData(inputs, curTestData, "text");
		dataPath = "//div[text()='"+dataLocator+"']";
		gotoTaskPath = dataPath+"/../../td/a[@title='Go to Task']";
		TaskUtilities.customWaitForElementVisibility(gotoTaskPath, MAX_TIME_OUT);
		System.out.println("Now clicking Go to Task....."+ gotoTaskPath);
		TaskUtilities.retryingFindClick(By.xpath(gotoTaskPath));
		
		//This is for L4 tasks;;
		curTestData += 1;
		
		//Moving the cursor again..
		curTestData += 1; 
		dataLocator = getExcelData(inputs, curTestData, "text");
		dataPath = "//a[text()='"+dataLocator+"']";
		TaskUtilities.customWaitForElementVisibility(dataPath, MAX_TIME_OUT);
		if(is_element_visible(dataPath,"xpath")){
			System.out.println("Now clicking link.....");
			TaskUtilities.jsFindThenClick(dataPath);
			Thread.sleep(1000);
		}
		
		TaskUtilities.customWaitForElementVisibility("//h2[text()='"+dataLocator+": Lookup Codes']", MAX_TIME_OUT);
		Thread.sleep(1000);
		takeScreenshot();
		
		//Start the Lookup Code
		
		//Check current table inputs.....
		tableInputs = task.queryAllTableInputs();
		TaskUtilities.retryingFindClick(By.xpath("//tr[@_afrrk='"+tableInputs.get(tableInputs.size()-1)+"']"));
		int lastTableInputCount = Integer.parseInt(tableInputs.get(tableInputs.size()-1));
		
		driver.findElement(By.xpath("//div[contains(@id,'create') and not(contains(@class,'Disabled'))]")).click();
		System.out.println("Adding new items....");

		TaskUtilities.customWaitForElementVisibility("//tr[@_afrrk='"+(lastTableInputCount+1)+"']", MAX_TIME_OUT);
		
		//Get details of recently added table row...
		String rowPath = "//tr[@_afrrk='"+(lastTableInputCount+1)+"']";
		Thread.sleep(1000);
		TaskUtilities.retryingFindClick(By.xpath(rowPath));
		
		//Start the lookup of values...
		for(int i = firstInput; getExcelData(inputLabel, i).length()> 0;i++){
			
			System.out.println("Input has been added.....");
			dataLocator = getExcelData(inputLabel, i, "text");
			rawDataLocPath = "//td/span/label[text()='"+dataLocator+"']/../input";
			dataLocPath = rowPath + rawDataLocPath;
			
			if(dataLocator.contains("Date")){
					type = "date";
				}else if(dataLocator.contains("Time")){
					type = "time";
				}else{
					type = "text";
				}
			
			inputValue = getExcelData(inputs, i, type);
			System.out.println("Now Clicking....."+dataLocPath);
			TaskUtilities.jsScrollIntoView(dataLocPath);
			TaskUtilities.retryingFindClick(By.xpath(dataLocPath));
			takeScreenshot();
			driver.findElement(By.xpath(dataLocPath)).clear();
			task.enterTextByXpath(dataLocPath, inputValue);
		}
		
		Thread.sleep(2000);
		takeScreenshot();
		TaskUtilities.jsFindThenClick("//button[text()='ave and Close']");
		Thread.sleep(500);
		
		TaskUtilities.customWaitForElementVisibility("//button[text()='D']", MAX_TIME_OUT, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				TaskUtilities.jsCheckMessageContainer();
			}
		});
		TaskUtilities.jsFindThenClick("//button[text()='D']");
		takeScreenshot();
		
		Thread.sleep(500);
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Implementation Project: "+searchData+"']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//button[text()='D']");
		takeScreenshot();
		
		Thread.sleep(10000);
		
		System.out.println("Manage Worker Goal Setting has been finised.");
		log("Manage Worker Goal Setting has been finised.");
		
	}
	
}