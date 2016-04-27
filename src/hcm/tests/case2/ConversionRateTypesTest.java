package hcm.tests.case2;

import static util.ReportLogger.logFailure;
import static util.ReportLogger.log;

import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import common.BaseTest;
import common.BooleanCustomRunnable;
import common.CustomRunnable;
import common.DuplicateEntryException;
import common.ExcelUtilities;
import common.ReporterManager;
import common.TaskUtilities;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;

public class ConversionRateTypesTest extends BaseTest{
	private static final int MAX_TIME_OUT = 30;
	
	private static final int defaultcolNum = 7;
	private static final int defaultinputs = 11;
	
	private String projectName = "Default";
	private String sumMsg = "", errMsg= "";
	private int projectRowNum = TestCaseRow;
	
	private String searchData, labelLocator, labelLocatorPath, dataLocator, rateTypeName;
	private int label = 10;
	private int inputs = defaultinputs;
	private int colNum = defaultcolNum;
	private int projectSheetcolNum = 7;
	private int lastInput = 0;
	
	private boolean hasManagedConversionRates = false;
	private boolean isScrollingDown = true;
	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		manageConversionRate();
	  
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
	
	public void manageConversionRate() throws Exception{
		
		LoginPage login = new LoginPage(driver);
		takeScreenshot();
		login.enterUserID(5);
		login.enterPassword(6);
		login.clickSignInButton();
		
		FuseWelcomePage welcome = new FuseWelcomePage(driver);
		//takeScreenshot();
		welcome.clickNavigator("More...");
		clickNavigationLink("Setup and Maintenance");
			
		TaskListManagerTopPage task = new TaskListManagerTopPage(driver);
		//takeScreenshot();
		
		while(!hasManagedConversionRates && !projectName.isEmpty() && !projectName.contentEquals("")){
			projectName = selectProjectName();
			
			if(projectName.contains("*")){
				projectRowNum += 1;
				continue;
			}
			
			hasManagedConversionRates = manageConversionRates(task);
		}
		
		System.out.println(sumMsg);
		log("Conversion Rate Types has been finised.");
		System.out.println("Conversion Rate Types has been finised.");
		
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

	private boolean manageConversionRates(TaskListManagerTopPage task) throws Exception{
		
		locateCurrencyManagerPage(task);
		getLastCurrencyRateType();
		addNewRow();
		sumMsg += "\n====================== R E P O R T   S U M M A R Y =====================\n";
		while(getExcelData(inputs, defaultcolNum, "text").length()>0){
			try{ 
					createCurrencyRateType(task);
					lastInput += 1;
					addNewRow();
					takeScreenshot();
					sumMsg += "[SUCCESS] Rate Type: "+rateTypeName+" has been created successfully.\n";
				} catch(DuplicateEntryException de){
					Thread.sleep(750);
					TaskUtilities.jsFindThenClick("//button[text()='OK']");
					errMsg += ReporterManager.trimErrorMessage(de+errMsg);
					errMsg = errMsg+"\n";
					sumMsg += "[FAILED] Unable to create Rate Type: "+rateTypeName+"...\n";
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
	
	private void locateCurrencyManagerPage(TaskListManagerTopPage task) throws Exception{
		
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
		TaskUtilities.customWaitForElementVisibility("//div[text()='Workforce Deployment']", MAX_TIME_OUT);
		
		if(is_element_visible("//div[text()='Workforce Deployment']"+"//a[@title='Expand']", "xpath")){
			TaskUtilities.retryingFindClick(By.xpath("//div[text()='Workforce Deployment']"+"//a[@title='Expand']"));
			TaskUtilities.customWaitForElementVisibility("//div[text()='Workforce Deployment']"+"//a[@title='Collapse']", MAX_TIME_OUT);
		}
		
		TaskUtilities.customWaitForElementVisibility("//div[text()='Define Common Applications Configuration for Human Capital Management']", MAX_TIME_OUT);
		
		if(is_element_visible("//div[text()='Define Common Applications Configuration for Human Capital Management']"+"//a[@title='Expand']", "xpath")){
			TaskUtilities.retryingFindClick(By.xpath("//div[text()='Define Common Applications Configuration for Human Capital Management']"+"//a[@title='Expand']"));
			TaskUtilities.customWaitForElementVisibility("//div[text()='Define Common Applications Configuration for Human Capital Management']"+"//a[@title='Collapse']", MAX_TIME_OUT);
		}
		
		TaskUtilities.customWaitForElementVisibility("//div[text()='Define Currencies and Currency Rates']", MAX_TIME_OUT);
		
		if(is_element_visible("//div[text()='Define Currencies and Currency Rates']"+"//a[@title='Expand']", "xpath")){
			TaskUtilities.retryingFindClick(By.xpath("//div[text()='Define Currencies and Currency Rates']"+"//a[@title='Expand']"));
			TaskUtilities.customWaitForElementVisibility("//div[text()='Define Currencies and Currency Rates']"+"//a[@title='Collapse']", MAX_TIME_OUT);
		}
		
		TaskUtilities.customWaitForElementVisibility("//div[text()='Manage Conversion Rate Types']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//div[text()='Manage Conversion Rate Types']");
		TaskUtilities.jsFindThenClick("//div[text()='Manage Conversion Rate Types']/../..//a[@title='Go to Task']");
		
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Currency Rates Manager']", MAX_TIME_OUT);
	}

	private void getLastCurrencyRateType() throws Exception{
		while(isScrollingDown){
			isScrollingDown = TaskUtilities.scrollDownToElement(isScrollingDown, "big");
		}
		
		Thread.sleep(2000);
		//Query last table inputs;;
		java.util.List<WebElement> queryFolder = new ArrayList<WebElement>();
		queryFolder = driver.findElements(By.xpath("//table[@summary='Rate Type']//tr"));
		int qFsize = queryFolder.size();
		System.out.println("query folder size: "+qFsize);
		String afrrkAttr = "";
		
		for(WebElement last: queryFolder.subList(qFsize-1, qFsize)){
			afrrkAttr = last.getAttribute("_afrrk");
		}
		
		lastInput = Integer.parseInt(afrrkAttr);
		System.out.println("lastinput has been set to: "+lastInput);
		
	}

	private void addNewRow() throws Exception{

		TaskUtilities.scrollDownToElement(false, "");
		TaskUtilities.jsFindThenClick("//img[@title='Add Row']/..");
		TaskUtilities.customWaitForElementVisibility("//tr[@_afrrk='"+(lastInput+1)+"']", MAX_TIME_OUT, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				TaskUtilities.scrollDownToElement(false, "");
				Thread.sleep(750);
			}
		});
		takeScreenshot();
	}
	
	private void createCurrencyRateType(TaskListManagerTopPage task) throws Exception{
		Thread.sleep(750);
		
		cRateloop:
		while(getExcelData(inputs, colNum, "text").length()>0){
			
			labelLocator = getExcelData(label, colNum, "text");
			labelLocator = TaskUtilities.filterDataLocator(labelLocator);
			if(labelLocator.contentEquals("Name")){
				rateTypeName = getExcelData(inputs, colNum, "text");
			}
			
			if(labelLocator.contentEquals("Default Rate Type")){
				colNum += 1;
				tickCurrencyRateTypeCheckbox(task);
				continue cRateloop;
			}
				
			labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
			TaskUtilities.jsScrollIntoView("//tr[@_afrrk='"+(lastInput+1)+"']");
			labelLocatorPath = "//tr[@_afrrk='"+(lastInput+1)+"']"+labelLocatorPath;
			
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(inputs, colNum, type);
			
			String labelType = driver.findElement(By.xpath(labelLocatorPath)).getAttribute("type");
			
			if(labelType.contentEquals("text")){
					TaskUtilities.retryingInputEncoder(task, labelLocatorPath, dataLocator);
				}
			
			colNum += 1;
		}

		//Save Data
		TaskUtilities.scrollDownToElement(false, "");
		TaskUtilities.jsFindThenClick("//button[text()='Save']");
		TaskUtilities.customWaitForElementVisibility("//button[text()='ave and Close'][@disabled='']", MAX_TIME_OUT, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				TaskUtilities.jsCheckMissedInput();
				TaskUtilities.jsCheckMessageContainer();
			}
		});
		
		Thread.sleep(2000);
		takeScreenshot();
	}

	private void tickCurrencyRateTypeCheckbox(TaskListManagerTopPage task) throws Exception{
		int checkboxNum = 1;
		boolean crossRatesEnabled = false;
		
		cboxloop:
		while(!getExcelData(label, colNum, "text").contentEquals("Cross Rate Pivot Currency")){
			
			String cboxID =  "selectBooleanCheckbox"+checkboxNum;
			labelLocator = getExcelData(label, colNum, "text");
			labelLocatorPath ="//span[contains(@id,'"+cboxID+"')]//input";
			labelLocatorPath = "//tr[@_afrrk='"+(lastInput+1)+"']"+labelLocatorPath;
			System.out.println("Assigned path: \n"+labelLocatorPath);
			
			dataLocator = getExcelData(inputs, colNum, "text");
			
			String labelType = driver.findElement(By.xpath(labelLocatorPath)).getAttribute("type");
			boolean isInputDisabled = Boolean.parseBoolean(driver.findElement(By.xpath(labelLocatorPath)).getAttribute("disabled"));
			
			if(isInputDisabled){
				checkboxNum += 1;
				colNum += 1;
				continue cboxloop;
			}
			
			if(labelType.contentEquals("checkbox")) {
				
				if(dataLocator.equalsIgnoreCase("Yes") && 
								!is_element_visible(labelLocatorPath+"[@checked='']", "text")){
							
							TaskUtilities.jsFindThenClick(labelLocatorPath);
							if(labelLocator.contentEquals("Enable Cross Rates")){
									crossRatesEnabled = true;
									TaskUtilities.timedLoop(MAX_TIME_OUT, new BooleanCustomRunnable() {
										
										@Override
										public boolean customRun() throws Exception {
											// TODO Auto-generated method stub
											System.out.println("Waiting for "+getExcelData(label,colNum+1,"text")+" to be visible..."); Thread.sleep(1000);
											return is_element_visible("//tr[@_afrrk='"+(lastInput+1)+"']//span[contains(@id,'selectBooleanCheckbox3')]//input[@disabled='']", "xpath");
										}
									});
							}
						
						} else if(dataLocator.equalsIgnoreCase("No") &&
								is_element_visible(labelLocatorPath+"[@checked='']", "xpath")){
							
							TaskUtilities.jsFindThenClick(labelLocatorPath);
						} else{
							
						}
				
			}
			
			checkboxNum += 1;
			colNum += 1;
		}
		
		selectCrossRatePivotCurrency(task, crossRatesEnabled);
	}
	
	private void selectCrossRatePivotCurrency(TaskListManagerTopPage task, boolean isCrossRateEnabled) throws Exception{
		if(!isCrossRateEnabled) return;
		TaskUtilities.retryingWrapper(new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				labelLocator = getExcelData(label, colNum, "text");
				labelLocator = TaskUtilities.filterDataLocator(labelLocator);
				labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
				TaskUtilities.jsScrollIntoView("//div//span[text()='"+labelLocator+"']");
				labelLocatorPath = "//tr[@_afrrk='"+(lastInput+1)+"']"+labelLocatorPath;
				
				String type = TaskUtilities.getdataLocatorType(labelLocator);
				dataLocator = getExcelData(inputs, colNum, type);
				
				//Selector
				String dataLocatorPath = labelLocatorPath+"/option[@title='"+dataLocator+"']";
				TaskUtilities.jsScrollIntoView("//div//span[text()='"+labelLocator+"']"); Thread.sleep(1000);
				driver.findElement(By.xpath(labelLocatorPath)).click();
				if(!dataLocator.isEmpty() && !dataLocator.contentEquals("")){
						TaskUtilities.customWaitForElementVisibility(dataLocatorPath, 30);
						TaskUtilities.retryingFindClick(By.xpath(dataLocatorPath));
					} else{
						//Skips the sequence...
					}
			}
		});
		
	}
}
