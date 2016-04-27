package hcm.tests.case1;

import static util.ReportLogger.logFailure;
import static util.ReportLogger.log;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import common.BaseTest;
import common.CustomRunnable;
import common.ExcelUtilities;
import common.RoleAlreadyAssignedException;
import common.TaskUtilities;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.OracleIdentityManagerPage;
import hcm.pageobjects.ScheduledProcessesPage;
import hcm.pageobjects.TaskListManagerTopPage;

public class ProvisionRolesToImpUser extends BaseTest{
	private static final int MAX_TIME_OUT = 30;
	private static final int defaultcolNum = 7;
	private static final int defaultinputs = 10;
	private static final int navRowNum = 2;
	
	private String searchData, labelLocator, labelLocatorPath, dataLocator, type;
	private String navLocator, slicedNavLocator;
	private int processID;
	
	private boolean hasProvisionedRoles = false;
	
	private String projectName = "Default";
	private int projectRowNum = TestCaseRow;
	private int projectSheetcolNum = 7;

	private int inputs = defaultinputs;
	private int colNum = defaultcolNum;

	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{	
			
		provisionRolesToImpUser();
	  
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
	
	public void provisionRolesToImpUser() throws Exception{
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
		
		navLocator = getExcelData(navRowNum, defaultcolNum+1, "text");
		
		while(!hasProvisionedRoles && !projectName.isEmpty() && !projectName.contentEquals("")){
			projectName = selectProjectName();
			
			if(projectName.contains("*")){
				projectRowNum += 1;
				continue;
			}
			
			hasProvisionedRoles = provisionRoles(task, welcome);
		}
		Thread.sleep(5000);
		log("Role Provisioning to Implementation User has been finished.");
		System.out.println("Role Provisioning to Implementation User has been finished.");
	}
	
	private String selectProjectName() throws Exception{
		System.out.println("Setting Project to be edited...RowNum: "+projectRowNum+" vs. "+TestCaseRow);
		final String projectSheetName = "Create Implementation Project";

		XSSFSheet projectSheet = ExcelUtilities.ExcelWBook.getSheet(projectSheetName);
		XSSFCell projectCell;
		String newProjectName ="";
		
		if(projectRowNum <= 0){
			projectRowNum = TestCaseRow;
		}
		
	  	try{	        	   
	  		projectCell = projectSheet.getRow(projectRowNum).getCell(projectSheetcolNum);      	  
	  		projectCell.setCellType(projectCell.CELL_TYPE_STRING);
	  		newProjectName = projectCell.getStringCellValue();
	            
	            }catch (Exception e){
	            	e.printStackTrace();
	            	newProjectName="";
	            }
	  	
		System.out.println("New Project Name is now..."+newProjectName);
				
		return newProjectName;
	}

	private boolean provisionRoles(TaskListManagerTopPage task, FuseWelcomePage welcome) throws Exception{
		
		while(getExcelData(inputs, defaultcolNum, "text").length()>0){
			
			TaskUtilities.customWaitForElementVisibility("//a[text()='Manage Implementation Projects']", MAX_TIME_OUT);
			TaskUtilities.jsFindThenClick("//a[text()='Manage Implementation Projects']");
			TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Implementation Projects']", MAX_TIME_OUT);
			
			searchData = projectName;
			labelLocator = "Name";
			labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
			
			TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, searchData);
			TaskUtilities.jsFindThenClick("//button[text()='Search']");
			Thread.sleep(3500);
			TaskUtilities.customWaitForElementVisibility("//a[text()='"+searchData+"']", MAX_TIME_OUT);
			TaskUtilities.jsFindThenClick("//a[text()='"+searchData+"']");
		
			//slicedNavLocator = navPathSlicer(1, navLocator);
			TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'"+searchData+"')]", MAX_TIME_OUT);
			TaskUtilities.customWaitForElementVisibility("//div[text()='Workforce Deployment']", MAX_TIME_OUT);
			//TaskUtilities.customWaitForElementVisibility("//div[text()='"+slicedNavLocator+"']", MAX_TIME_OUT);
			
			if(is_element_visible("//div[text()='Workforce Deployment']"+"//a[@title='Expand']", "xpath")){
			//if(is_element_visible("//div[text()='"+slicedNavLocator+"']"+"//a[@title='Expand']", "xpath")){
				TaskUtilities.retryingFindClick(By.xpath("//div[text()='Workforce Deployment']"+"//a[@title='Expand']"));
				//TaskUtilities.retryingFindClick(By.xpath("//div[text()='"+slicedNavLocator+"']"+"//a[@title='Expand']"));
				TaskUtilities.customWaitForElementVisibility("//div[text()='Workforce Deployment']"+"//a[@title='Collapse']", MAX_TIME_OUT);
				//TaskUtilities.customWaitForElementVisibility("//div[text()='"+slicedNavLocator+"']"+"//a[@title='Collapse']", MAX_TIME_OUT);
			}
			
			TaskUtilities.customWaitForElementVisibility("//div[text()='Define Common Applications Configuration for Human Capital Management']", MAX_TIME_OUT);
			
			if(is_element_visible("//div[text()='Define Common Applications Configuration for Human Capital Management']"+"//a[@title='Expand']", "xpath")){
				//TaskUtilities.jsFindThenClick("//div[text()='Define Common Applications Configuration for Human Capital Management']"+"//a[@title='Expand']");
				TaskUtilities.retryingFindClick(By.xpath("//div[text()='Define Common Applications Configuration for Human Capital Management']"+"//a[@title='Expand']"));
				TaskUtilities.customWaitForElementVisibility("//div[text()='Define Common Applications Configuration for Human Capital Management']"+"//a[@title='Collapse']", MAX_TIME_OUT);
			}
			
			TaskUtilities.customWaitForElementVisibility("//div[text()='Define Implementation Users']", MAX_TIME_OUT);
			
			if(is_element_visible("//div[text()='Define Implementation Users']"+"//a[@title='Expand']", "xpath")){
				//TaskUtilities.jsFindThenClick("//div[text()='Define Implementation Users']"+"//a[@title='Expand']");
				TaskUtilities.retryingFindClick(By.xpath("//div[text()='Define Implementation Users']"+"//a[@title='Expand']"));
				TaskUtilities.customWaitForElementVisibility("//div[text()='Define Implementation Users']"+"//a[@title='Collapse']", MAX_TIME_OUT);
			}
			
			TaskUtilities.customWaitForElementVisibility("//div[text()='Provision Roles to Implementation Users']", MAX_TIME_OUT);
			//Task lists...
			try{
				openAdminLink();
				synchronizeRoles(task);
				checkScheduledProcess(welcome);
				returnToOriginalPage(welcome);
			} catch(RoleAlreadyAssignedException e){
				
				Thread.sleep(750);
				//After this is done to go back;;ERROR here...
				WebElement oracleBody = driver.findElement(By.cssSelector("body"));
				String closeTab = Keys.chord(Keys.CONTROL, "w");
				oracleBody.sendKeys(closeTab);
				
				TaskUtilities.customWaitForElementVisibility("//button[text()='D']", MAX_TIME_OUT);
				TaskUtilities.jsFindThenClick("//button[text()='D']");
				TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Implementation Projects']", MAX_TIME_OUT);
				TaskUtilities.jsFindThenClick("//button[text()='D']");
				TaskUtilities.customWaitForElementVisibility("//h1[text()='Overview']", MAX_TIME_OUT);
			}
		
			colNum = defaultcolNum;
			inputs += 1;
		}
		
		return true;
	}
	
	public void openAdminLink() throws Exception{
		
		String adminUrl = driver.findElement(By.xpath("//div[text()='Provision Roles to Implementation Users']/../..//a[@title='Go to Task']")).getAttribute("href");
		
		WebElement body = driver.findElement(By.cssSelector("body"));
		//String newTabAction = Keys.chord(Keys.COMMAND, "t");
		String newTabAction = Keys.chord(Keys.CONTROL, "t");
        body.sendKeys(newTabAction);
         
        String chooseTab = Keys.chord(Keys.COMMAND, "2");//on my pc 3 others should be 2;
		//String switchTab = Keys.chord(Keys.CONTROL, Keys.TAB);
        body.sendKeys(chooseTab);
        
        driver.get(adminUrl);        
		
		OracleIdentityManagerPage userPage = new OracleIdentityManagerPage(driver);
		takeScreenshot();
		
		userPage.clickAdminLink();
		TaskUtilities.customWaitForElementVisibility("//a[text()='Administration']", MAX_TIME_OUT);
		
		labelLocatorPath = "//table[contains(@id,'SEARCH')]//span/input";
		type = TaskUtilities.getdataLocatorType(labelLocatorPath);
		dataLocator = getExcelData(inputs, colNum, type);
		
		TaskUtilities.retryingInputEncoder(userPage, labelLocatorPath, dataLocator);
		driver.findElement(By.xpath(labelLocatorPath)).sendKeys(Keys.ENTER);
		TaskUtilities.customWaitForElementVisibility("//a[text()='"+dataLocator+"']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//a[text()='"+dataLocator+"']");
		TaskUtilities.customWaitForElementVisibility("//h1[text()='"+dataLocator+"']", MAX_TIME_OUT);
		
		//TaskUtilities.jsFindThenClick("//div/a[text()='Roles']");
		TaskUtilities.retryingFindClick(By.xpath("//div/a[text()='Roles']"));
		driver.findElement(By.xpath("//div/a[text()='Roles']")).sendKeys(Keys.ENTER);
		TaskUtilities.customWaitForElementVisibility("//a/span[text()=' Assign']/..", MAX_TIME_OUT);
		
		//Wait for the Element within 10 seconds...
		try{
			TaskUtilities.customWaitForElementVisibility("//div[text()='"+getExcelData(inputs, colNum+1, "text")+"']", 10);
			throw new RoleAlreadyAssignedException();
		} catch(TimeoutException e){
			//Move thru...
		}
		
		//TaskUtilities.jsFindThenClick("//a/span[text()=' Assign']/..");
		TaskUtilities.retryingFindClick(By.xpath("//a/span[text()=' Assign']/.."));
		driver.findElement(By.xpath("//a/span[text()=' Assign']/..")).sendKeys(Keys.ENTER);
		TaskUtilities.customWaitForElementVisibility("//div[text()='Add Role']", MAX_TIME_OUT);
		
		labelLocator = "Display Name";
		labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
		dataLocator = getExcelData(inputs, colNum+1, "text");
		
		TaskUtilities.retryingInputEncoder(userPage, labelLocatorPath, dataLocator);
		driver.findElement(By.xpath(labelLocatorPath)).sendKeys(Keys.ENTER);
		//TaskUtilities.jsFindThenClick("//button[text()='Search']");
		TaskUtilities.customWaitForElementVisibility("//td[text()='"+dataLocator+"']", MAX_TIME_OUT);
		
		TaskUtilities.retryingFindClick(By.xpath("//td[text()='"+dataLocator+"']"));
		TaskUtilities.retryingFindClick(By.xpath("//button[text()='Add ']"));
		driver.findElement(By.xpath("//button[text()='Add ']")).sendKeys(Keys.ENTER);
		TaskUtilities.customWaitForElementVisibility("//label[contains(text(),'selected role')][contains(text(),'assigned')][contains(text(),'successfully')]", MAX_TIME_OUT);
		TaskUtilities.customWaitForElementVisibility("//td[text()='"+dataLocator+"']", MAX_TIME_OUT);
		takeScreenshot();
		
		Thread.sleep(750);
		//After this is done to go back;;ERROR here...
		WebElement oracleBody = driver.findElement(By.cssSelector("body"));
		String closeTab = Keys.chord(Keys.CONTROL, "w");
		oracleBody.sendKeys(closeTab);
		
		oracleBody = null;
		closeTab = null;
	}

	public void synchronizeRoles(TaskListManagerTopPage task) throws Exception{
		
		TaskUtilities.customWaitForElementVisibility("//button[text()='D']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//button[text()='D']");
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Implementation Projects']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//button[text()='D']");
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Overview']", MAX_TIME_OUT);
		
		labelLocator = "Name";
		labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
		dataLocator = "LDAP";
		
		TaskUtilities.retryingInputEncoder(task, labelLocatorPath, dataLocator);
		driver.findElement(By.xpath(labelLocatorPath)).sendKeys(Keys.ENTER);
		TaskUtilities.customWaitForElementVisibility("//button[text()='Search']", MAX_TIME_OUT);
		TaskUtilities.customWaitForElementVisibility("//span[text()='Define Synchronization of Users and Roles from LDAP']", MAX_TIME_OUT);
		if(is_element_visible("//span[text()='Define Synchronization of Users and Roles from LDAP']/../../..//a[@title='Expand']", "xpath")){
			//TaskUtilities.jsFindThenClick("//span[text()='Define Synchronization of Users and Roles from LDAP']/../../..//a");
			TaskUtilities.retryingFindClick(By.xpath("//span[text()='Define Synchronization of Users and Roles from LDAP']/../../..//a"));
			TaskUtilities.customWaitForElementVisibility("//span[text()='Define Synchronization of Users and Roles from LDAP']/../../..//a[@title='Collapse']", MAX_TIME_OUT);
		}
		
		TaskUtilities.jsFindThenClick("//span[text()='Run User and Roles Synchronization Process']");
		TaskUtilities.jsFindThenClick("//span[text()='Run User and Roles Synchronization Process']/../../..//a[@title='Go to Task']");

		TaskUtilities.customWaitForElementVisibility("//td[text()='Retrieve Latest LDAP Changes']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//span[text()='Sub']");
		TaskUtilities.customWaitForElementVisibility("//label[contains(text(),'Process')][contains(text(),'was submitted.')]", MAX_TIME_OUT);
		String processIDString = driver.findElement(By.xpath("//label[contains(text(),'Process')][contains(text(),'was submitted.')]")).getText();
		processID = Integer.parseInt(processIDString.replaceAll("\\D+", ""));
		takeScreenshot();
		TaskUtilities.jsFindThenClick("//button[text()='OK']");
	}
	
	public void checkScheduledProcess(final FuseWelcomePage welcome) throws Exception{
		//TaskUtilities.retryingFindClick(By.xpath("//a[@title='Navigator']"));
		//TaskUtilities.customWaitForElementVisibility("//a[text()='Scheduled Processes']", MAX_TIME_OUT);
		//TaskUtilities.retryingFindClick(By.xpath("//td/a[text()='Scheduled Processes']"));
		Thread.sleep(10000);
		TaskUtilities.retryingWrapper(new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				Thread.sleep(1000);
				welcome.clickNavigator("More...");
				clickNavigationLink("Scheduled Processes");
			}
		});
		
		ScheduledProcessesPage schedTask = new ScheduledProcessesPage(driver);
		TaskUtilities.jsFindThenClick("//img[@title='Refresh']/..");
		TaskUtilities.customWaitForElementVisibility("//td[text()='"+processID+"']/../td/a[text()='Succeeded']", 630, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				TaskUtilities.jsFindThenClick("//img[@title='Refresh']/..");
				Thread.sleep(750);
			}
		});
		
		Thread.sleep(3000);
		takeScreenshot();
		//Clean up;;
		schedTask = null;
	}
	
	public void returnToOriginalPage(FuseWelcomePage welcome) throws Exception{
		welcome.clickNavigator("More...");
		clickNavigationLink("Setup and Maintenance");
		TaskListManagerTopPage task = new TaskListManagerTopPage(driver);
		task = null;
	}
	
	public String navPathSlicer(int index, String navLocator){
		String newNavLocator = "";
		int indexNum = 0;
		int gtIndex;
		
		while(indexNum < index){
			if(indexNum == 0){
				gtIndex = navLocator.indexOf(">");
				newNavLocator = navLocator.substring(0, gtIndex);
				
			} else {
				gtIndex = newNavLocator.indexOf(">");
				newNavLocator = newNavLocator.substring(1, gtIndex);
			}
			
			indexNum += 1;
		}
		return newNavLocator;
	}
}
