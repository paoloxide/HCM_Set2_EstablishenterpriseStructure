package hcm.tests.case2;

import static util.ReportLogger.log;
import static util.ReportLogger.logFailure;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.testng.annotations.Test;

import common.BaseTest;
import common.BooleanCustomRunnable;
import common.CustomRunnable;
import common.DuplicateEntryException;
import common.ExcelUtilities;
import common.StringCustomRunnable;
import common.TaskUtilities;
import common.ReporterManager;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;

public class ManageLegislativeDataGroupsTest extends BaseTest{
	private static final int MAX_TIME_OUT = 30;
	
	private static final int defaultcolNum = 7;
	private static final int defaultinputs = 11;
	private static int intLoc = 0;
	private static String currentLoc = "";
	
	private String projectName = "Default";
	private String sumMsg = "", errMsg="";
	private int projectRowNum = TestCaseRow;
	
	
	private String searchData, labelLocator, labelLocatorPath, dataLocator, ldgroupName;
	private String SSlabelLocator, SSlabelLocatorPath, currencyNamePath;
	private int label = 10;
	private int inputs = defaultinputs;
	private int colNum = defaultcolNum;
	private int projectSheetcolNum = 7;
	
	private boolean hasManagedLDG = false;
	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		manageLegislativeDataGroups();
	  
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
	
	public void manageLegislativeDataGroups() throws Exception{
		
		LoginPage login = new LoginPage(driver);
		takeScreenshot();
		login.enterUserID(5);
		login.enterPassword(6);
		login.clickSignInButton();
		
		FuseWelcomePage welcome = new FuseWelcomePage(driver);
		//takeScreenshot();
		String navigationPath = getExcelData(2, defaultcolNum, "text");
		currentLoc = TaskUtilities.manageNavigation(navigationPath, intLoc);
		welcome.clickNavigator("More...");
		clickNavigationLink(currentLoc);
			
		TaskListManagerTopPage task = new TaskListManagerTopPage(driver);
		//takeScreenshot();
		
		while(!hasManagedLDG && !projectName.isEmpty() && !projectName.contentEquals("")){
			projectName = selectProjectName();
			
			if(projectName.contains("*")){
				projectRowNum += 1;
				continue;
			}
			
			hasManagedLDG = manageLDG(task);
		}
		
		System.out.println(sumMsg);
		System.out.println(errMsg);
		log("Legislative Data Groups has been managed.");
		System.out.println("Legislative Data Groups has been managed.");
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

	private boolean manageLDG(TaskListManagerTopPage task) throws Exception{
		locateManageLDGPage(task);
		sumMsg += "\n====================== R E P O R T   S U M M A R Y =====================\n";
		while(getExcelData(inputs, defaultcolNum, "text").length()>0){
			ldgroupName = getExcelData(inputs, defaultcolNum, "text");
			
			try{
					createLDGroups(task);
					submitDetails();
					sumMsg += "[SUCCESS] Legislative Data Group: "+ldgroupName+" has been created successfully.\n";
				} catch(DuplicateEntryException de){
					cancelTask();
					errMsg += ReporterManager.trimErrorMessage(de+errMsg);
					errMsg = errMsg+"\n";
					sumMsg += "[FAILED] Unable to create Legislative Data Group: "+ldgroupName+"...\n"+errMsg;
				}

			colNum = defaultcolNum;
			inputs += 1;
			errMsg = "";
			if(getExcelData(inputs, defaultcolNum, "text").length()>0)
				sumMsg += "------------------------------------------------------------------------\n";
		}
		
		sumMsg += "====================== E N D   O F   R E P O R T =======================\n";
		return true;
	}
	
	private void locateManageLDGPage(TaskListManagerTopPage task) throws Exception{
		String divPath;
		
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
	

		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'"+searchData+"')]", MAX_TIME_OUT);
		
		String navigationPath = getExcelData(2, defaultcolNum, "text");
		intLoc += 1;
		while(TaskUtilities.manageNavigation(navigationPath, intLoc).length() > 0){
			currentLoc = TaskUtilities.manageNavigation(navigationPath, intLoc);
			System.out.println("We are now at: "+currentLoc);
			divPath = "//div[text()='"+currentLoc+"']";
			
			TaskUtilities.customWaitForElementVisibility(divPath, MAX_TIME_OUT);
			
			if(is_element_visible(divPath+"//a[@title='Expand']", "xpath")){
				TaskUtilities.retryingFindClick(By.xpath(divPath+"//a[@title='Expand']"));
				TaskUtilities.customWaitForElementVisibility(divPath+"//a[@title='Collapse']", MAX_TIME_OUT);
			}
			
			if(is_element_visible(divPath+"/../..//a[@title='Go to Task']", "xpath")){
				TaskUtilities.jsFindThenClick(divPath);
				TaskUtilities.jsFindThenClick(divPath+"/../..//a[@title='Go to Task']");
			}

			intLoc += 1;
		}
		
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Legislative Data Groups']", MAX_TIME_OUT);
	}
	private void createLDGroups(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.jsFindThenClick("//span[text()=' Create']/..");
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Create Legislative Data Group']", MAX_TIME_OUT, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				try{
						Thread.sleep(2000);
						TaskUtilities.jsFindThenClick("//span[text()=' Create']/..");
					} catch(WebDriverException we){
						
					}
			}
		});
		

		labelLocator = getExcelData(label, colNum, "text");
		labelLocator = TaskUtilities.filterDataLocator(labelLocator);
		labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);

		String type = TaskUtilities.getdataLocatorType(labelLocator);
		dataLocator = getExcelData(inputs, colNum, type);
		
		TaskUtilities.retryingInputEncoder(task, labelLocatorPath, dataLocator);
		colNum += 1;
		searchFromSS(task);
		colNum += 1;
		searchCurrency(task);
		colNum += 1;
		searchFromSS(task);
		takeScreenshot();
	}
	
	private void searchFromSS(TaskListManagerTopPage task) throws Exception{
		labelLocator = getExcelData(label, colNum, "text");
		labelLocator = TaskUtilities.filterDataLocator(labelLocator);
		String type = TaskUtilities.getdataLocatorType(labelLocator);
		dataLocator = getExcelData(inputs, colNum, type);
		String parentLinkPath = "[contains(@title,'Search and Select')]";
		
		TaskUtilities.consolidatedInputSearcherAndSelector(task, labelLocator, dataLocator, parentLinkPath, new StringCustomRunnable() {
			
			public String customRun() throws Exception {
				// TODO Auto-generated method stub
				if(labelLocator.contentEquals("Country")){
							SSlabelLocator = " Country;//div[contains(@id,'')]";
						}else if(labelLocator.contentEquals("Cost Allocation Structure")){
							SSlabelLocator = " Key Flexfield Structure Instance Name;//div[contains(@id,'')]";
						}
				return SSlabelLocator;
			}
		}, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				if(labelLocator.contentEquals("Country")){
							TaskUtilities.jsFindThenClick("//button[text()='OK'][not(contains(@id,'cancel'))]"
									+ "[contains(@id,'territory')]");
						}else if(labelLocator.contentEquals("Cost Allocation Structure")){
							TaskUtilities.jsFindThenClick("//button[text()='OK'][not(contains(@id,'cancel'))]"
									+ "[contains(@id,'keyFlexStructure')]");
						}
			}
		});
		
	}
	
	private void searchCurrency(TaskListManagerTopPage task) throws Exception{
		labelLocator = getExcelData(label, colNum, "text");
		labelLocator = TaskUtilities.filterDataLocator(labelLocator);
		labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);

		String type = TaskUtilities.getdataLocatorType(labelLocator);
		dataLocator = getExcelData(inputs, colNum, type);
		//Skips the search...
		if(dataLocator.isEmpty() || dataLocator.contentEquals("")) return;
		
		TaskUtilities.jsFindThenClick("//a[contains(@title,'Search and Select')][contains(@title,'"+labelLocator+"')]");
		
		TaskUtilities.retryingWrapper(new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				Thread.sleep(2250);
				TaskUtilities.jsFindThenClick("//a[text()='Search...']");
			}
		});
		
		SSlabelLocator = "Currency Code";
		SSlabelLocatorPath = TaskUtilities.retryingSearchInput(SSlabelLocator);
		
		TaskUtilities.retryingWrapper(new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				Thread.sleep(2000);
				driver.findElement(By.xpath(SSlabelLocatorPath)).click();
			}
		});
		
		TaskUtilities.retryingInputEncoder(task, SSlabelLocatorPath, dataLocator);
		driver.findElement(By.xpath(SSlabelLocatorPath)).sendKeys(Keys.ENTER);
		try{
				
				TaskUtilities.customWaitForElementVisibility("//div[contains(@id,'InternalTable')]//td[text()='"+dataLocator+"']", MAX_TIME_OUT);
				currencyNamePath = "//div[contains(@id,'InternalTable')]//td[text()='"+dataLocator+"']/../td[not(contains(text(),'"+dataLocator+"'))]";
			} catch(TimeoutException e){
				
				SSlabelLocator = "Currency Name";
				SSlabelLocatorPath = TaskUtilities.retryingSearchInput(SSlabelLocator);
				TaskUtilities.retryingInputEncoder(task, SSlabelLocatorPath, dataLocator);
				TaskUtilities.customWaitForElementVisibility("//tbody//td[text()='"+dataLocator+"']", MAX_TIME_OUT);
				currencyNamePath = "//div[contains(@id,'InternalTable')]//td[text()='"+dataLocator+"']";
			}
		

		final String currencyName = driver.findElement(By.xpath(currencyNamePath)).getText();
		TaskUtilities.jsFindThenClick(currencyNamePath);
		
		final String last5String = labelLocator.substring(labelLocator.length()-5);
		TaskUtilities.jsFindThenClick("//button[text()='OK'][not(contains(@id,'cancel'))][contains(@id,'"+last5String+"')]");
		//Temporary Sol'n...
		
		TaskUtilities.retryingWrapper(new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				Thread.sleep(2250);
				driver.findElement(By.xpath(labelLocatorPath)).click();
			}
		});
		takeScreenshot();
	}

	private void submitDetails() throws Exception{
		TaskUtilities.jsFindThenClick("//span[text()='Sub']/..");
		TaskUtilities.customWaitForElementVisibility("//button[text()='es']", MAX_TIME_OUT, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				TaskUtilities.jsCheckMissedInput();
				TaskUtilities.jsCheckMessageContainer();
			}
		});
		TaskUtilities.jsFindThenClick("//button[text()='es']");
		TaskUtilities.customWaitForElementVisibility("//button[text()='O']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//button[text()='O']");
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Legislative Data Groups']", MAX_TIME_OUT);
	}

	private void cancelTask() throws Exception{
		TaskUtilities.jsFindThenClick("//span[text()='ancel']/..");
		TaskUtilities.customWaitForElementVisibility("//button[text()='es']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//button[text()='es']");
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Legislative Data Groups']", MAX_TIME_OUT);
		
	}
}
