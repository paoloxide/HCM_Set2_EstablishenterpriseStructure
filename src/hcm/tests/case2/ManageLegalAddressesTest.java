package hcm.tests.case2;

import static util.ReportLogger.logFailure;
import static util.ReportLogger.log;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import common.BaseTest;
import common.CustomRunnable;
import common.DuplicateEntryException;
import common.ExcelUtilities;
import common.ReporterManager;
import common.StringCustomRunnable;
import common.TaskUtilities;

public class ManageLegalAddressesTest extends BaseTest{
	private static final int MAX_TIME_OUT = 30;
	
	private static final int defaultcolNum = 7;
	private static final int defaultinputs = 10;
	private static final int navRow = 2;
	private static int intLoc = 0;
	private static String currentLoc = "";
	
	private String projectName = "Default";
	private String sumMsg = "", errMsg="";
	private int projectRowNum = TestCaseRow;
	
	
	private String searchData, labelLocator, labelLocatorPath, dataLocator, legalAddress;
	private String SSlabelLocator, SSlabelLocatorPath, currencyNamePath;
	private int label = 9;
	private int inputs = defaultinputs;
	private int colNum = defaultcolNum;
	private int projectSheetcolNum = 7;
	
	private boolean hasManagedLAddress = false;
	
	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		manageLegalAddress();
	  
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
	
	public void manageLegalAddress() throws Exception{
		LoginPage login = new LoginPage(driver);
		takeScreenshot();
		login.enterUserID(5);
		login.enterPassword(6);
		login.clickSignInButton();
		
		FuseWelcomePage welcome = new FuseWelcomePage(driver);
		//takeScreenshot();
		String navigationPath = getExcelData(navRow, defaultcolNum, "text");
		currentLoc = TaskUtilities.manageNavigation(navigationPath, intLoc);
		welcome.clickNavigator("More...");
		clickNavigationLink(currentLoc);
			
		TaskListManagerTopPage task = new TaskListManagerTopPage(driver);
		//takeScreenshot();
		
		while(!hasManagedLAddress && !projectName.isEmpty() && !projectName.contentEquals("")){
			projectName = selectProjectName();
			
			if(projectName.contains("*")){
				projectRowNum += 1;
				continue;
			}
			
			hasManagedLAddress = manageLAddress(task);
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

	private boolean manageLAddress(TaskListManagerTopPage task) throws Exception{
		locateManageLegalAddressPage(task);
		sumMsg += "\n====================== R E P O R T   S U M M A R Y =====================\n";
		while(getExcelData(inputs, defaultcolNum, "text").length()>0){
			legalAddress = 	getExcelData(inputs, defaultcolNum, "text") 	+", "+ 
							getExcelData(inputs, defaultcolNum+1, "text")	+", "+
							getExcelData(inputs, defaultcolNum+2, "text")	+", "+
							getExcelData(inputs, defaultcolNum+3, "text")	+", "+
							getExcelData(inputs, defaultcolNum+4, "text")	+", "+
							getExcelData(inputs, defaultcolNum+5, "text")	+", "+
							getExcelData(inputs, defaultcolNum+6, "text")	+", "+
							getExcelData(inputs, defaultcolNum+7, "text");
			
			try{
				createLegalAddress(task);
				sumMsg += "[SUCCESS] Legal Address: "+legalAddress+" has been created successfully.\n";
			} catch(DuplicateEntryException de){
				cancelTask();
				errMsg += ReporterManager.trimErrorMessage(de+errMsg);
				errMsg = errMsg+"\n";
				sumMsg += "[FAILED] Unable to create Legal Address: "+legalAddress+"...\n"+errMsg;
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
	
	private void locateManageLegalAddressPage(TaskListManagerTopPage task) throws Exception{
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
		
		String navigationPath = getExcelData(navRow, defaultcolNum, "text");
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
		
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Legal Addresses']", MAX_TIME_OUT);
	}
	private void createLegalAddress(TaskListManagerTopPage task) throws Exception{
		String labelTag, labelId;
		String parentPath = "//div[contains(@id,'popup')]";
		
		TaskUtilities.jsFindThenClick("//img[@title='Create']/..");
		TaskUtilities.customWaitForElementVisibility("//div[text()='Location Create']", MAX_TIME_OUT);
		
		while(getExcelData(label, colNum, "text").length()>0){
			labelLocator = getExcelData(label, colNum, "text");
			labelLocator = TaskUtilities.filterDataLocator(labelLocator);
			translateLabel();
			labelLocatorPath = TaskUtilities.retryingSearchfromDupInput(labelLocator, parentPath);
			labelTag = driver.findElement(By.xpath(labelLocatorPath)).getTagName();
			labelId = driver.findElement(By.xpath(labelLocatorPath)).getAttribute("id");
			String parentLinkPath = "";
			
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(inputs, colNum, type);
			
			if(labelTag.contentEquals("select")){
						TaskUtilities.consolidatedInputSelector(labelLocatorPath, dataLocator);
					} else{
						
						if(labelId.contains("inputText")){
									TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
								} else if(labelId.contains("inputCombobox")){
									TaskUtilities.consolidatedInputSearcherAndSelector(task, labelLocator, dataLocator,parentLinkPath, new StringCustomRunnable() {
										
										@Override
										public String customRun() throws Exception {
											// TODO Auto-generated method stub
											if(labelLocator.contentEquals("City")){
													SSlabelLocator = " City;//div[contains(@id,'inputComboboxListOfValues3')]";
												}else if(labelLocator.contentEquals("Postal Code")){
													SSlabelLocator = " Postal Code;//div[contains(@id,'inputComboboxListOfValues4')]";
												}else if(labelLocator.contentEquals("State")){
													SSlabelLocator = " State;//div[contains(@id,'inputComboboxListOfValues1')]";
												}
											return SSlabelLocator;
										}
									}, new CustomRunnable() {
										
										@Override
										public void customRun() throws Exception {
											// TODO Auto-generated method stub
											if(labelLocator.contentEquals("City")){
														TaskUtilities.jsFindThenClick("//button[text()='OK'][not(contains(@id,'cancel'))]"
																+ "[contains(@id,'inputComboboxListOfValues3')]");
													}else if(labelLocator.contentEquals("Postal Code")){
														TaskUtilities.jsFindThenClick("//button[text()='OK'][not(contains(@id,'cancel'))]"
																+ "[contains(@id,'inputComboboxListOfValues4')]");
													}else if(labelLocator.contentEquals("State")){
														TaskUtilities.jsFindThenClick("//button[text()='OK'][not(contains(@id,'cancel'))]"
																+ "[contains(@id,'inputComboboxListOfValues1')]");
													}
										}
									});
								}
						
					}
			colNum += 1;
		}
		takeScreenshot();
		
		//Save address...
		TaskUtilities.jsFindThenClick("//button[text()='O']");
		TaskUtilities.retryingWrapper(new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				TaskUtilities.jsCheckMissedInput();
				TaskUtilities.jsCheckMessageContainer();
				Thread.sleep(2250);
				driver.findElement(By.xpath("//h1[text()='Manage Legal Addresses']")).click();
			}
		});
		//TaskUtilities.jsFindThenClick("//button[text()='Save']");
		TaskUtilities.jsFindThenClick("//span[text()='ave and Close']/..");
		TaskUtilities.customWaitForElementVisibility("//div[text()='Manage Legal Addresses']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//div[text()='Manage Legal Addresses']"+"/../..//a[@title='Go to Task']");
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Legal Addresses']", MAX_TIME_OUT);
		
	}
	private void translateLabel() throws Exception{
		if(labelLocator.contentEquals("Address Line 1")){
					labelLocator = "Delivery address";
				}else if(labelLocator.contentEquals("Address Line 2")){
					labelLocator = "Suite Number";
				}else if(labelLocator.contentEquals("Address Line 3")){
					labelLocator = "PO Box";
				}
		System.out.println("labelLocator is now: "+labelLocator);
	}
	private void cancelTask() throws Exception{
		
		TaskUtilities.jsFindThenClick("//div[contains(@id,'popup')]//button[text()='ancel']");
		TaskUtilities.retryingWrapper(new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				Thread.sleep(2250);
				driver.findElement(By.xpath("//h1[text()='Manage Legal Addresses']")).click();
			}
		});
	}
}
