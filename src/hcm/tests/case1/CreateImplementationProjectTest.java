package hcm.tests.case1;

import static util.ReportLogger.log;
import static util.ReportLogger.logFailure;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.annotations.Test;

import common.BaseTest;
import common.CustomRunnable;
import common.DuplicateEntryException;
import common.TaskUtilities;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;

public class CreateImplementationProjectTest extends BaseTest{
	private static final int MAX_TIME_OUT = 30;
	private int inputLabel = 10;
	private int inputs = 11;
	private int colNum = 7;
	private int projectCol = 7;
	private String type, labelTag;
	private String projectName = "", sumMsg = "";
	
	private String labelLocator, labelLocatorPath, dataLocator, dataLocatorPath, dataToUpperCase;
	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		createProject();
	  
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
	
	public void createProject() throws Exception{
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
		
		TaskUtilities.customWaitForElementVisibility("//a[text()='Manage Implementation Projects']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//a[text()='Manage Implementation Projects']");
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Implementation Projects']", MAX_TIME_OUT);
		
		sumMsg += "===============\n R E P O R T  S U M M A R Y\n===============\n";
		
		while(getExcelData(inputs, colNum, "text").length() > 0){
			try{		
					TaskUtilities.jsFindThenClick("//a/img[@title='Create']");
					TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Create Implementation Project')]", MAX_TIME_OUT);
					
					createImplementation(task);
					sumMsg += "	Implementation Project: "+projectName+ " has been created.\n";
					
				} catch(DuplicateEntryException e){
					takeScreenshot();
					sumMsg += "	Failed to create Implementation Project: "+projectName+". Project already exist. Refer to screenshots for details.\n";
					TaskUtilities.jsFindThenClick("//button[text()='ancel']");
					TaskUtilities.customWaitForElementVisibility("//button[text()='D']", MAX_TIME_OUT);
				}
			colNum = 7;
			inputs += 1;
		}
		
		Thread.sleep(5000);
		log(sumMsg+"\n");
		System.out.println(sumMsg+"\n");
		log("Department Creation has been finished...");
		System.out.print("Department Creation has been finished...");
	}
	
	public void createImplementation(TaskListManagerTopPage task) throws Exception{
		String nextLabelLocator="", nextLabelLocatorPath ="";
		if(colNum <1) colNum = 7;
		projectName = getExcelData(inputs, projectCol, "text");
		
		//Set All Data Values
		setdataloop:
		while(getExcelData(inputLabel, colNum, "text").length() > 0){
			System.out.println("Preparing labels and inputs...");
			if((nextLabelLocatorPath != null && !nextLabelLocatorPath.isEmpty()) 
					&& !nextLabelLocator.contentEquals("Status")){
					
					System.out.println("Previous labels reusable...");
					labelLocator = nextLabelLocator;
					labelLocatorPath = nextLabelLocatorPath;
				} else {
					System.out.println("Setting new label values...");
					labelLocator = getExcelData(inputLabel, colNum, "text");
					labelLocator = TaskUtilities.filterDataLocator(labelLocator);
					labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
				}
			
			labelTag = driver.findElement(By.xpath(labelLocatorPath)).getTagName();
			//Define input value
			type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(inputs, colNum, type);
			
			if(!labelTag.contentEquals("select")){
					TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
					
				} else if(labelTag.contentEquals("select") && labelLocator.contentEquals("Assigned To")){
					dataToUpperCase = dataLocator.toUpperCase();
					dataLocatorPath = labelLocatorPath+"/option[@title='Search']";
					TaskUtilities.retryingFindClick(By.xpath(dataLocatorPath));
					TaskUtilities.customWaitForElementVisibility("//div[text()='Search and Select: User']", MAX_TIME_OUT);
					String selectLabel = "E-Mail";
					String selectLabelPath = TaskUtilities.retryingSearchInput(selectLabel);
					
					TaskUtilities.consolidatedInputEncoder(task, selectLabelPath, dataLocator);
					driver.findElement(By.xpath(selectLabelPath)).sendKeys(Keys.ENTER);
					TaskUtilities.jsFindThenClick("//button[text()='Search']");
					TaskUtilities.fluentWaitForElementInvisibility("//div[text()='No search conducted.']", "No search conducted.", MAX_TIME_OUT);
					Thread.sleep(750);
					
					//TaskUtilities.customWaitForElementVisibility("//td[@title='"+dataLocator+"']", MAX_TIME_OUT);
					TaskUtilities.retryingFindClick(By.xpath("//div/span[text()='E-Mail']"));
					TaskUtilities.retryingFindClick(By.xpath("//td[text()='"+dataLocator+"']"));
					TaskUtilities.jsFindThenClick("//button[text()='OK']");
					TaskUtilities.customWaitForElementVisibility("//select[@title='"+dataToUpperCase+"']", MAX_TIME_OUT);			
					
				} else {
					TaskUtilities.consolidatedInputSelector(labelLocatorPath, dataLocator);
				}
			
			if(labelLocator.contentEquals("Name") || labelLocator.contentEquals("Code")) Thread.sleep(5000);
			TaskUtilities.jsCheckMissedInput();
			TaskUtilities.jsCheckMessageContainer();
			nextLabelLocator = getExcelData(inputLabel, colNum+1, "text");
			nextLabelLocator = TaskUtilities.filterDataLocator(nextLabelLocator);
			if(nextLabelLocator.contentEquals("Status")){
				colNum += 2;
				continue setdataloop;
			}
			try{
					nextLabelLocatorPath = TaskUtilities.retryingSearchInput(nextLabelLocator);
				}catch(StaleElementReferenceException e){
					nextLabelLocatorPath = TaskUtilities.retryingSearchInput(nextLabelLocator);
				}
			
			if(nextLabelLocatorPath == null || nextLabelLocatorPath.isEmpty()){ 
				colNum += 1;
				break setdataloop;
			}
			
			colNum += 1;
			Thread.sleep(250);
		}

		TaskUtilities.jsFindThenClick("//button[text()='Ne']");
		if(projectName == null || projectName.isEmpty()){
			projectName = getExcelData(inputs, projectCol, "text");
		}
		takeScreenshot();
		TaskUtilities.customWaitForElementVisibility("//td[text()='"+projectName+"']", MAX_TIME_OUT, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				TaskUtilities.jsCheckMissedInput();
				TaskUtilities.jsCheckMessageContainer();
			}
		});
		
		//Iterate Over Tasks to implement
		taskloop:
		while(getExcelData(inputLabel, colNum, "text").length()>0){
			labelLocator = getExcelData(inputLabel, colNum, "text");
			System.out.println("Now processing: "+labelLocator);
			dataLocator = getExcelData(inputs, colNum, "text");
			
			if(dataLocator.equalsIgnoreCase("Yes")){
				//TaskUtilities.jsFindThenClick("//div[text()='"+labelLocator+"']");
				TaskUtilities.retryingFindClick(By.xpath("//div[text()='"+labelLocator+"']"));
				
				if(!is_element_visible("//div[text()='"+labelLocator+"']//a", "xpath")){
					
					if(!TaskUtilities.is_element_visible("//div[text()='"+labelLocator+"']/../.."+"//input[@checked='']", "xpath")){
							System.out.println("Ticking all visible folder");
							TaskUtilities.retryingFindClick(By.xpath("//div[text()='"+labelLocator+"']/../.."+"//input"));
						}
					takeScreenshot();
					colNum += 1;
					continue taskloop;
				}
				
				Thread.sleep(500);
				TaskUtilities.retryingFindClick(By.xpath("//img[@title='Show as Top']/..[@href='#']"));
				TaskUtilities.retryingFindClick(By.xpath("//div[text()='"+labelLocator+"']"));
				TaskUtilities.customWaitForElementVisibility("//a[@title='Show Hierarchy']", MAX_TIME_OUT);
				
				//String uniqueID = task.findMainTaskUniqueID(inputLabel, colNum);
				List<String> visibileTask = task.tickExpandVisibleTasks(MAX_TIME_OUT);
				TaskUtilities.scrollDownToElement(false, "any");
				
				for(String visTask: visibileTask){
					
					String visTaskPath = visTask;
					//Tick checkbox...
					TaskUtilities.retryingFindClick(By.xpath(visTaskPath));
					TaskUtilities.jsScrollIntoView(visTaskPath);
					TaskUtilities.retryingFindClick(By.xpath(visTaskPath));
					System.out.println("Ticking checkbox");
					if(!TaskUtilities.is_element_visible(visTaskPath+"//input[@checked='']", "xpath")){
						System.out.println("Ticking all visible folder");
						TaskUtilities.retryingFindClick(By.xpath(visTaskPath+"//input"));
					}
				}
				
				takeScreenshot();
				TaskUtilities.scrollDownToElement(false, "any");
				if(is_element_visible("//div[text()='"+labelLocator+"']"+"//a[@title='Collapse']", "xpath")){
					TaskUtilities.jsFindThenClick("//div[text()='"+labelLocator+"']");
					//TaskUtilities.jsFindThenClick("//div[text()='"+labelLocator+"']"+"//a[@title='Collapse']");
					TaskUtilities.retryingFindClick(By.xpath("//div[text()='"+labelLocator+"']"+"//a[@title='Collapse']"));
					TaskUtilities.customWaitForElementVisibility("//div[text()='"+labelLocator+"']"+"//a[@title='Expand']", MAX_TIME_OUT);
				}
				
				TaskUtilities.jsFindThenClick("//a/img[@title='Go to Top']");
				TaskUtilities.fluentWaitForElementInvisibility("//a[@title='Show Hierarchy']", "", MAX_TIME_OUT);
				
			}
			
			colNum += 1;
		}
		
		TaskUtilities.jsFindThenClick("//span[text()='Save and Open Project']/../../..//a[not(@href='#')]");
		TaskUtilities.customWaitForElementVisibility("//td[text()='Save and Close']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//td[text()='Save and Close']");
		TaskUtilities.customWaitForElementVisibility("//button[text()='D']", MAX_TIME_OUT);
	}
	
}
