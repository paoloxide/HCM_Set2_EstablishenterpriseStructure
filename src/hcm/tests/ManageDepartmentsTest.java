package hcm.tests;

import static util.ReportLogger.log;
import static util.ReportLogger.logFailure;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.testng.annotations.Test;

import common.BaseTest;
import common.CustomRunnable;
import common.DuplicateEntryException;
import common.TaskUtilities;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.WorkforceStructureTasksPage;

public class ManageDepartmentsTest extends BaseTest{
	private static final int MAX_TIME_OUT = 30;
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		createDepartment();
	  
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

	public void createDepartment() throws Exception{
			
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
		
		//Enable task
		int inputLabel = 7;
		int inputs = 8;
		int departmentName = 10;
		boolean isScrollingDown = true;
		String btnYesPath = "//button[text()='es']";
		String btnOKPath = "//button[text()='O']";
		String btnSearchPath = "//button[text()='Search']";
		String nextDataLocator = "", searchData = "";
		String type, inputValue, dataLocator, dataPath, nextDataPath, dataLocatorPath;
		
		int attempts = getLastNonNullRow(7);
		String confMsg = "\n===============\nReport Summary:\n";
		String inputLocator = getExcelData(inputs, 7, "text");
		attemptloop:
		while(attempts >= 0){
			try{	
					
					deptloop:
					while(!inputLocator.isEmpty() && !inputLocator.contentEquals("")){
					
						task.waitForElementToBeClickable(60, "//li/a[text()='Manage Departments']");
						task.clickTask("Manage Departments");
						
						//takeScreenshot();
						TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Departments']", MAX_TIME_OUT);
						
						dataLocator = "Name";
						dataLocatorPath = TaskUtilities.retryingSearchInput(dataLocator);
						inputValue = getExcelData(inputs, departmentName, "text");
						
						TaskUtilities.consolidatedInputEncoder(task, dataLocatorPath, inputValue);
						driver.findElement(By.xpath(dataLocatorPath)).sendKeys(Keys.ENTER);
						TaskUtilities.jsFindThenClick("//button[text()='Search']");
						try{
								TaskUtilities.customWaitForElementVisibility("//a[text()='"+inputValue+"']", 20);
								inputs += 1;
								inputLocator = getExcelData(inputs, 7, "text");
								confMsg += "Department "+inputValue+" already exists.\n";
								log("Department "+inputValue+" already exists.\n");
								System.out.println("Department "+inputValue+" already exists.\n");
								takeScreenshot();
								continue deptloop;
								
							} catch(TimeoutException e){
								//Skips
							}
						
						task.waitForElementToBeClickable(10, "//a[@href='#']/span[text()=' Create']");
						TaskUtilities.retryingFindClick(By.xpath("//span[text()=' Create']"));
						System.out.println("Clicking Create icon...");
								
						TaskUtilities.customWaitForElementVisibility("//td/label[text()='"+
								task.filterDataLocator(getExcelData(inputLabel, 8, "text"))+
								"']/../../td/input", MAX_TIME_OUT);
						
						inputloop:
						for(int colNum = 8;getExcelData(inputLabel, colNum, "text").length()>0; colNum++){
							System.out.println("\n**********");
							dataLocator = getExcelData(inputLabel, colNum, "text");
							System.out.println("Filtering text....."+dataLocator);
							dataLocator = task.filterDataLocator(dataLocator);
							type =TaskUtilities.getdataLocatorType(dataLocator);
								
							inputValue = getExcelData(inputs, colNum, type);
							System.out.println("Input value....."+inputValue);
							
							//Proceed on Iterating all the inputs....
							dataPath =  TaskUtilities.retryingSearchInput(dataLocator);
							
							if(dataPath.indexOf("select") != -1){
									TaskUtilities.consolidatedInputSelector(dataPath, inputValue);
								} else{
									TaskUtilities.consolidatedInputEncoder(task, dataPath, inputValue);
								}
							
							//Triggering Next Button.....
							System.out.println("Checking next data locator.....");
							nextDataLocator = getExcelData(inputLabel, colNum+1, "text");
							nextDataLocator = task.filterDataLocator(nextDataLocator);
							nextDataPath = TaskUtilities.retryingSearchInput(nextDataLocator);
							
							if(nextDataLocator.isEmpty() || nextDataLocator.contentEquals("")){
									break inputloop;
								
								}else if(nextDataPath == null){
										Thread.sleep(500); task.clickNext();
										TaskUtilities.customWaitForElementVisibility("//td/label[text()='"+nextDataLocator+"']", MAX_TIME_OUT, new CustomRunnable() {
											
											@Override
											public void customRun() throws Exception {
												// TODO Auto-generated method stub
												TaskUtilities.jsCheckMessageContainer();
												TaskUtilities.jsCheckMissedInput();
											}
										});
									}
						}
					
						Thread.sleep(1000);
						task.clickSubmitButton();
						TaskUtilities.customWaitForElementVisibility(btnYesPath, MAX_TIME_OUT, new CustomRunnable() {
							
							@Override
							public void customRun() throws Exception {
								// TODO Auto-generated method stub
								TaskUtilities.jsCheckMissedInput();
								TaskUtilities.jsCheckMessageContainer();
							}
						});
						task.clickYesButton();
						TaskUtilities.customWaitForElementVisibility(btnOKPath, MAX_TIME_OUT);
						task.clickOKButton();
						//task.clickSaveandCloseButton();
						Thread.sleep(5000); 
						
						//Verifying if the department has been added.....
						task.clickTask("Manage Departments");
						TaskUtilities.customWaitForElementVisibility(btnSearchPath, MAX_TIME_OUT);
						searchData = getExcelData(inputs, 10, "text");
						//searchData = task.filterDataLocator(searchData);
						String dataSearchPath = TaskUtilities.retryingSearchInput("Name");
						TaskUtilities.consolidatedStrictInputEncoder(task, dataSearchPath, searchData);
						
						//Test Case Verification process...
						TaskUtilities.jsScrollIntoView(dataSearchPath);
						TaskUtilities.retryingFindClick(By.xpath(dataSearchPath));
						driver.findElement(By.xpath(dataSearchPath)).clear();
						Thread.sleep(500);
						task.enterTextByXpath(dataSearchPath, searchData);
						Thread.sleep(1000);
						task.clickSearchButton();
						Thread.sleep(2000);
						task.clickSearchButton();
						TaskUtilities.customWaitForElementVisibility("//a[text()='"+searchData+"']", MAX_TIME_OUT);
						takeScreenshot();
						
						confMsg += "Department "+searchData+" has been created.\n";
						log("Department "+searchData+" has been created.\n");
						System.out.println("Department "+searchData+" has been created.\n");
						
						inputs += 1;
						inputLocator = getExcelData(inputs, 7, "text");
					}
			
					break attemptloop;
				
				} catch(DuplicateEntryException e){	
					
					e.printStackTrace();
					String eMsg = e.toString().substring(e.toString().indexOf(".")+1, e.toString().indexOf(":"));
					searchData = getExcelData(inputs, 10, "text");
					searchData = task.filterDataLocator(searchData);
					confMsg += "Failed to create Department "+searchData+". caused by: "+eMsg+"...\n";
					log("Failed to create Department "+searchData+". caused by: "+eMsg+"...\n");
					System.out.println("Failed to create Department "+searchData+". caused by: "+eMsg+"...\n");
					takeScreenshot();
					
					if(is_element_visible("//span[text()='ancel']", "xpath")){
						TaskUtilities.jsFindThenClick("//span[text()='ancel']");
						TaskUtilities.customWaitForElementVisibility("//button[text()='es']", MAX_TIME_OUT);
						TaskUtilities.jsFindThenClick("//button[text()='es']");

					}
					inputs += 1;
					inputLocator = getExcelData(inputs, 7, "text");
					attempts -= 1;
				}
		}
		
		dataLocator = "Name";
		dataLocatorPath = TaskUtilities.retryingSearchInput(dataLocator);
		searchData = getExcelData(inputs-1, departmentName, "text");
		TaskUtilities.consolidatedInputEncoder(task, dataLocatorPath, searchData);
		
		driver.findElement(By.xpath(dataLocatorPath)).sendKeys(Keys.ENTER);
		TaskUtilities.jsFindThenClick("//button[text()='Search']");
		
		TaskUtilities.customWaitForElementVisibility("//a[text()='"+searchData+"']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//a[text()='"+searchData+"']");
		takeScreenshot();
		
		TaskUtilities.customWaitForElementVisibility("//h2[contains(text(),'Organization Information EFF: Department Details')]", MAX_TIME_OUT);
		TaskUtilities.jsScrollIntoView("//h2[contains(text(),'Organization Information EFF: Department Details')]");
		Thread.sleep(2500);
		takeScreenshot();
		
		isScrollingDown = task.jsScrollDown(isScrollingDown);
		takeScreenshot();
		
		//Thread.sleep(3000);
		//task.clickSaveandCloseButton();
		//Thread.sleep(10000);
		
		//Ending message::
		Thread.sleep(5000);
		//takeScreenshot();
			
		System.out.println("Department Creation Completed\n***************\n");
		log("Department Creation Completed");
			
		Thread.sleep(1500);
		//takeScreenshot();
		
		confMsg += "===============\n";
		System.out.println(confMsg);
	}
}