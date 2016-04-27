package hcm.tests.case2;

import static util.ReportLogger.log;
import static util.ReportLogger.logFailure;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import common.BaseTest;
import common.CustomRunnable;
import common.DuplicateEntryException;
import common.ExcelUtilities;
import common.ReporterManager;
import common.TaskUtilities;
import hcm.exception.DuplicateEnterpriseNameException;
import hcm.exception.InvalidLegalAddressException;
import hcm.exception.InvalidLocationException;
import hcm.exception.NoTableEntryFoundException;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;

public class EstablishEnterpriseStructuresTest extends BaseTest{
	private static final int MAX_TIME_OUT = 30;	
	
	private static final int defaultcolNum = 7;
	private static final int defaultcolNum2 = defaultcolNum + 5;
	private static final int defaultinputs = 10;
	private static final int defaultlabel = 9;
	private static final int navRow = 2;
	private static int intLoc = 0;
	private static String currentLoc = "";
	
	private static int 	divisionlabel, divisioninputs, legalEntitylabel, legalEntityinputs,
						busUnitlabel, busUnitinputs, refSetlabel, refSetinputs,
						manageBusUnitAsslabel, manageBusUnitAssinputs, locRefSetlabel, locRefSetinputs;
	
	private String projectName = "Default";
	private String sumMsg = "", errMsg ="";
	private String divRootPath= "", locationName ="";
	private int projectRowNum = TestCaseRow;
	
	private String searchData, labelLocator, labelLocatorPath, dataLocator, locationLabel;
	private String enterpriseConfig;
	private int label = defaultlabel;
	private int inputs = defaultinputs;
	private int colNum = defaultcolNum;
	private int projectSheetcolNum = 7;
	private int lastInputs;
	private int inputCount = 0;
	
	private boolean hasEstablishedEnterprise = false;
	private boolean hasLegalEntitiesCreated = false;
	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{
		establishEnterprise();
	  
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

	public void establishEnterprise() throws Exception{
		
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
		
		//default labels...:: + 1 for space; + 1 for group label; + 1 for main label
		getInputCount();
		divisionlabel = label + inputCount + 1 + 1 + 1;
		legalEntitylabel = divisionlabel + inputCount + 1 + 1 + 1;
		busUnitlabel = legalEntitylabel + inputCount + 1 + 1 + 1;
		
		while(!hasEstablishedEnterprise && !projectName.isEmpty() && !projectName.contentEquals("")){
			projectName = selectProjectName();
			
			if(projectName.contains("*")){
				projectRowNum += 1;
				continue;
			}
			
			hasEstablishedEnterprise = establishEntStructures(task);
		}
		
		System.out.println(sumMsg);
		System.out.println(errMsg);
		log("Enterprise Strucutures has been created.");
		System.out.println("Legislative Data Groups has been created.");
		
	}
	
	private int getInputCount() throws Exception{
		while(getExcelData(inputs, defaultcolNum, "text").length()>0){
			inputCount += 1;
			inputs += 1;
		}
		inputs = defaultinputs;
		return lastInputs;
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
	
	private boolean establishEntStructures(TaskListManagerTopPage task) throws Exception{
		locateEnterpriseConfigPage(task);
		sumMsg += "\n====================== R E P O R T   S U M M A R Y =====================\n";
		enterpriseConfig = getExcelData(inputs, defaultcolNum, "text");
		
		try{
				createEnterpriseConfig(task);
				submitEnterprise(task);
				sumMsg += "[SUCCESS] Enterprise Configuration: "+enterpriseConfig+" has been created successfully.\n";
			} catch(DuplicateEntryException de){
				cancelTask();
				errMsg += ReporterManager.trimErrorMessage(de+errMsg);
				errMsg = errMsg+"\n";
				sumMsg += "[FAILED] Unable to create Enterprise Configuration: "+enterpriseConfig+"...\n"+errMsg;
			} catch(DuplicateEnterpriseNameException dene){
				cancelConfiguration();
				errMsg += ReporterManager.trimErrorMessage(dene+errMsg);
				errMsg = errMsg+"\n";
				sumMsg += "[FAILED] Unable to create Enterprise Configuration: "+enterpriseConfig+"...\n"+errMsg;
			}
		
		errMsg += "";
		sumMsg += "====================== E N D   O F   R E P O R T =======================\n";
		return true;
	}

	private void locateEnterpriseConfigPage(TaskListManagerTopPage task) throws Exception{
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
		
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Enterprise Configuration']", MAX_TIME_OUT);
	}
	
	private void createEnterpriseConfig(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.jsFindThenClick("//img[@title='Create']/..");
		
		try{
				setEnterpriseNameAndDesc(task);
			}catch(TimeoutException te){
				throw new DuplicateEnterpriseNameException();
			}
		
		manageEnterprise(task);
		manageDivisions(task);
		manageLegalEntities(task);
		manageCreateBusinessUnits(task, "AUTO");
		manageBusUnits(task);
		manageReferenceDataSets(task);
		manageBusUnitAssignments(task);
		manageLocationRefSets(task);
		takeScreenshot();

	}
	
	private void setEnterpriseNameAndDesc(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.customWaitForElementVisibility("//div[text()='Create Enterprise Configuration']", MAX_TIME_OUT);
		
		while(getExcelData(label, colNum, "text").length()>0){
			labelLocator = getExcelData(label, colNum, "text");
			labelLocator = TaskUtilities.filterDataLocator(labelLocator);
			labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
			
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(inputs, colNum, type);
			
			TaskUtilities.retryingInputEncoder(task, labelLocatorPath, dataLocator);
			
			colNum += 1;
		}
		
		TaskUtilities.jsFindThenClick("//button[text()='O']");
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
			+ "[contains(text(),'Manage Enterprise')]", MAX_TIME_OUT);
		
	}
	
	private void manageEnterprise(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
				+ "[contains(text(),'Manage Enterprise')]", MAX_TIME_OUT);
		try{
				setMEEnterpriseInfo(task);
				setMELegalInfo(task);
				setEnterpriseConfigReq(task);
				
			} catch(InvalidLegalAddressException ie){
				createLocation(task, "");
			}
	}
	private void setMEEnterpriseInfo(TaskListManagerTopPage task) throws Exception{
		colNum += 1;
		
		while(!getExcelData(label, colNum, "text").contains("Location Name")){
			labelLocator = getExcelData(label, colNum, "text");
			labelLocator = TaskUtilities.filterDataLocator(labelLocator);
			labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
			
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(inputs, colNum, type);
			
			String labelTag = driver.findElement(By.xpath(labelLocatorPath)).getTagName();
			
			if(labelTag.contentEquals("select")){
					TaskUtilities.consolidatedInputSelector(labelLocatorPath, dataLocator);
				}else{
					TaskUtilities.retryingInputEncoder(task, labelLocatorPath, dataLocator);
				}
			
			colNum += 1;
		}
	}
	private void setMELegalInfo(TaskListManagerTopPage task) throws Exception{
		int legalColNum = defaultcolNum;
		locationLabel = "Legal Address";
		legalEntityinputs = legalEntitylabel + (inputs - defaultinputs) + 1;
		System.out.println("legal label : "+legalEntitylabel+" :legal input "+legalEntityinputs);
		
		legalColNum += 1;
		setMEEIloop:
		while(getExcelData(legalEntitylabel, legalColNum, "text").length()>0){
			
			labelLocator = getExcelData(legalEntitylabel, legalColNum, "text");
			labelLocator = TaskUtilities.filterDataLocator(labelLocator);
			
			//Skips Division Tab...
			if(labelLocator.contentEquals("Division") || labelLocator.contentEquals("Ultimate Holding Company")){
				legalColNum += 1;
				continue setMEEIloop;
			}
			
			//Updates Name to Legal Name...
			if(labelLocator.contentEquals("Name")) labelLocator = "Legal Name";
			
			labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			
			dataLocator = getExcelData(legalEntityinputs, legalColNum, type);
			TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
		
			legalColNum += 1;
		}
		
		hasLegalEntitiesCreated = true;
		
	}
	private void setEnterpriseConfigReq(TaskListManagerTopPage task) throws Exception{
		labelLocator = "Continue with the interview to set up more legal entities";
		labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
		
		TaskUtilities.jsFindThenClick(labelLocatorPath);
		TaskUtilities.jsFindThenClick("//button[text()='Ne']");
		
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
				+ "[contains(text(),'Manage Divisions')]", MAX_TIME_OUT, new CustomRunnable() {
					
					@Override
					public void customRun() throws Exception {
						// TODO Auto-generated method stub
						TaskUtilities.jsCheckMissedInput();
						TaskUtilities.jsCheckMessageContainer();
					}
				});
	}
	
	private void manageDivisions(TaskListManagerTopPage task) throws Exception{
		boolean hasCreatedLocation = false;
		int afrrkInt = 0;
		TaskUtilities.jsSideScroll(false);
		System.out.println("Now managing divisions...");
		manageDloop:
		while(!hasCreatedLocation){
			try{
				manageDivision(task, afrrkInt);
				return;
			} catch(InvalidLocationException | DuplicateEntryException | WebDriverException e){
				e.printStackTrace();
				createLloop:
				while(!hasCreatedLocation){
					try{
							createLocation(task, divRootPath);
							hasCreatedLocation = true;
						} catch(DuplicateEntryException | WebDriverException me){
							me.printStackTrace();
							//Naturally skips...
							if((""+me).contains(".AM")){
								TaskUtilities.jsFindThenClick("//img[@title='Delete']/..");
								afrrkInt = Integer.parseInt(divRootPath);
								while(is_element_visible(divRootPath, "xpath")){
									//Trap here...
								}
								continue manageDloop;
							}
						}
				}
				TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
			}
		}
		
		TaskUtilities.jsFindThenClick("//button[text()='Ne']");
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
			+ "[contains(text(),'Manage Legal Entities')]", 10, new CustomRunnable() {
				
				@Override
				public void customRun() throws Exception {
					// TODO Auto-generated method stub
					TaskUtilities.jsCheckMissedInput();
					TaskUtilities.jsCheckMessageContainer();
				}
		});
		
	}
	private void manageDivision(TaskListManagerTopPage task, int afrrkInt) throws Exception{
		colNum = defaultcolNum;
		locationLabel = "Location";
		divisioninputs = divisionlabel + 1;
		
		System.out.println("Managing single divisions...");
			
			TaskUtilities.jsFindThenClick("//img[@title='Add Row']/..");
			divRootPath = "//tr[@_afrrk='"+afrrkInt+"']";
			TaskUtilities.customWaitForElementVisibility(divRootPath, MAX_TIME_OUT);
			TaskUtilities.jsSideScroll(false);
			
			while(getExcelData(divisionlabel, colNum, "text").length()>0){
				labelLocator = getExcelData(divisionlabel, colNum, "text");
				labelLocator = TaskUtilities.filterDataLocator(labelLocator);
				labelLocatorPath = TaskUtilities.retryingSearchfromDupInput(labelLocator, divRootPath);
				String labelTag = driver.findElement(By.xpath(labelLocatorPath)).getTagName();
				
				String type = TaskUtilities.getdataLocatorType(labelLocator);
				dataLocator = getExcelData(divisioninputs, colNum, type);
				
				if(labelLocator.contentEquals("Location")){
					locationName = dataLocator;
				}
				
				if(labelTag.contentEquals("select")){
						TaskUtilities.consolidatedInputSelector(labelLocatorPath, dataLocator);
					}else{
						TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
					}
				
				colNum += 1;
			}
		
		TaskUtilities.jsFindThenClick("//button[text()='Ne']");
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
				+ "[contains(text(),'Manage Legal Entities')]", MAX_TIME_OUT, new CustomRunnable() {
					
					@Override
					public void customRun() throws Exception {
						// TODO Auto-generated method stub
						TaskUtilities.jsCheckMissedInput();
						TaskUtilities.jsCheckMessageContainer();
					}
				});
	}
	
	
	private void manageLegalEntities(TaskListManagerTopPage task) throws Exception{
		int afrrkInt = 0;
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
				+ "[contains(text(),'Manage Legal Entities')]", MAX_TIME_OUT);
		TaskUtilities.jsSideScroll(false);
		
		colNum = defaultcolNum;
		locationLabel = "Legal Address";
		legalEntityinputs = legalEntitylabel + 1;
			
		//Verify data...
		afrrkInt = verifyLegalEntityPresence(task);
		if(!hasLegalEntitiesCreated){
			createLegalEntity(task, afrrkInt);
		}
		
		TaskUtilities.jsFindThenClick("//button[text()='Ne']");
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
				+ "[contains(text(),'Create Business Units')]", 15, new CustomRunnable() {
					
					@Override
					public void customRun() throws Exception {
						// TODO Auto-generated method stub
						TaskUtilities.jsCheckMissedInput();
						TaskUtilities.jsCheckMessageContainer();
					}
				});
		
	}
	private int  verifyLegalEntityPresence(TaskListManagerTopPage task) throws Exception{
		int afrrkInt = 0;
		
		afrrkInt = surveyCurrentTableInputs("Manage Legal Entities");
		
		colNum = defaultcolNum;
		locationLabel = "Legal Address";
		legalEntityinputs = legalEntitylabel + 1;
		String leRootPath = "//tr[@_afrrk='"+afrrkInt+"']";
		
		if(!is_element_visible(leRootPath, "xpath")){
			return -1;
		}
		
		dataLocator = getExcelData(legalEntityinputs, colNum, "text");
		try{
				TaskUtilities.customWaitForElementVisibility("//span[text()='"+dataLocator+"']", MAX_TIME_OUT);
			}catch(TimeoutException te){
				hasLegalEntitiesCreated = false;
				return afrrkInt; //creates new...
			}
		
		colNum += 1;
		leloop:
		while(getExcelData(legalEntitylabel, colNum, "text").length()>0){
			labelLocator = getExcelData(legalEntitylabel, colNum, "text");
			labelLocator = TaskUtilities.filterDataLocator(labelLocator);
			TaskUtilities.jsSideScroll(true);
			
			if(labelLocator.contentEquals("Ultimate Holding Company")){
				if(getExcelData(legalEntityinputs, colNum, "text").contentEquals("Yes")){
							TaskUtilities.jsScrollIntoView("//a[@title='"+labelLocator+"']");
							try{
										TaskUtilities.customWaitForElementVisibility("//img[contains(@src,'checkmark')]", MAX_TIME_OUT);
										colNum += 1;
										continue leloop;
									} catch(TimeoutException te){
										hasLegalEntitiesCreated = false;
										return afrrkInt;
									}
				}else{
					colNum += 1;
					continue leloop;
				}
			}
			
			if(labelLocator.contentEquals("Division")){
				if(getExcelData(legalEntityinputs, colNum, "text").isEmpty()){
					colNum += 1;
					continue leloop;
				}
			}
			
			labelLocatorPath = TaskUtilities.retryingSearchfromDupInput(labelLocator, leRootPath);
			
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(legalEntityinputs, colNum, type);
			
			String inputValue = TaskUtilities.jsGetInputValue(labelLocatorPath);
			System.out.println("Comparing input values...");
			if(inputValue.contentEquals(dataLocator)){
					//Skips
				} else{
					System.out.println("Mismatch found...");
					if(labelLocator.contentEquals("Legal Address")){
						if(hasLegalEntitiesCreated){
								TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
							}else{
								createLocation(task, "");
							}
						return afrrkInt;
					}
					
					hasLegalEntitiesCreated = false;
					return afrrkInt; //stops verification create new...
				}
			
			colNum += 1;
		}
		return afrrkInt;
	}
	private void createLegalEntity(TaskListManagerTopPage task, int afrrkInt) throws Exception{
		if(afrrkInt == -1) afrrkInt = 0;
		colNum = defaultcolNum;
		locationLabel = "Legal Address";
		legalEntityinputs = legalEntitylabel + 1;
		String leRootPath = "//tr[@_afrrk='"+(afrrkInt+1)+"']";
		
		TaskUtilities.jsFindThenClick("//img[@title='Add Row']/..");
		TaskUtilities.customWaitForElementVisibility(leRootPath, MAX_TIME_OUT);
		
		colNum += 1;
		leloop:
		while(getExcelData(legalEntitylabel, colNum, "text").length()>0){
			labelLocator = getExcelData(legalEntitylabel, colNum, "text");
			labelLocator = TaskUtilities.filterDataLocator(labelLocator);
			
			if(labelLocator.contentEquals("Ultimate Holding Company")){
				colNum += 1;
				continue leloop;
			}
			
			if(labelLocator.contentEquals("Division")){
				if(getExcelData(legalEntityinputs, colNum, "text").isEmpty()){
					colNum += 1;
					continue leloop;
				}
			}
			
			labelLocatorPath = TaskUtilities.retryingSearchfromDupInput(labelLocator, leRootPath);
			String labelTag = driver.findElement(By.xpath(labelLocatorPath)).getTagName();
			
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(legalEntityinputs, colNum, type);
			
			if(labelTag.contentEquals("select")){
					TaskUtilities.consolidatedInputSelector(labelLocatorPath, dataLocator);
				}else{
					TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
				}
			
			colNum += 1;
		}
	}

	
	private void manageCreateBusinessUnits(TaskListManagerTopPage task, String mode) throws Exception{
		//This will be set to automatic by default
		colNum = defaultcolNum;
		busUnitinputs = busUnitlabel + 1;
		
		String auto = "Automatically generate business units";
		String manual = "Manually create business units";
		String defaultBusUnitLevel = "Legal Entity";
		String tablePath = "//table[contains(@summary,'Create Business Units')]";
		TaskUtilities.jsSideScroll(false);
		
		if(mode.contentEquals("AUTO")){
				labelLocator = auto;
				labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
				TaskUtilities.jsFindThenClick(labelLocatorPath);
				
				labelLocator = "Business Unit Level";
				labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
				dataLocator = defaultBusUnitLevel;
				TaskUtilities.consolidatedInputSelector(labelLocatorPath, dataLocator);
				
				dataLocator = getExcelData(busUnitinputs, colNum, "text");
				try{
						TaskUtilities.customWaitForElementVisibility(tablePath+"//td[text()='"+dataLocator+"']", 15);
					} catch(TimeoutException te){
						busManualCreate(task);
					}
				TaskUtilities.jsFindThenClick("//td[text()='"+dataLocator+"']/../td//input");
				
			}else{
				//No codes yet...
				busManualCreate(task);
			}
		
		TaskUtilities.jsFindThenClick("//button[text()='Ne']");
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
				+ "[contains(text(),'Manage Business Units')]", MAX_TIME_OUT, new CustomRunnable() {
					
					@Override
					public void customRun() throws Exception {
						// TODO Auto-generated method stub
						TaskUtilities.jsCheckMissedInput();
						TaskUtilities.jsCheckMessageContainer();
					}
				});
		
	}
	private void busManualCreate(TaskListManagerTopPage task) throws Exception{
		String manual = "Manually create business units";
		//MANUAL -- for future reference...
	}
	
	
	private void manageBusUnits(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.jsSideScroll(false);
		try{
				manageBusUnit(task);
			}catch(NoTableEntryFoundException ntfe){
				createBusUnit(task);
			}
		
		TaskUtilities.jsFindThenClick("//button[text()='Ne']");
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
				+ "[contains(text(),'Manage Reference Data Sets')]", MAX_TIME_OUT, new CustomRunnable() {
					
					@Override
					public void customRun() throws Exception {
						// TODO Auto-generated method stub
						TaskUtilities.jsCheckMissedInput();
						TaskUtilities.jsCheckMessageContainer();
					}
				});
	}
	private void manageBusUnit(TaskListManagerTopPage task) throws Exception{
		String inputData;
		int afrrkInt = 0;
		int tinputs = 0;
		
		busUnitlabel = getGroupLabelRowNum("Business Unit") + 1;
		busUnitinputs = busUnitlabel + 1;
		colNum = defaultcolNum;
		
		labelLocator = TaskUtilities.filterDataLocator(getExcelData(busUnitlabel, colNum, "text"));
		dataLocator = getExcelData(busUnitinputs, colNum, "text");		
		afrrkInt = surveyCurrentTableInputs("Manage Business Units");
		
		tableloop:	
		for(int i=0; i<=afrrkInt; i++){
			try{
					driver.findElement(By.xpath("//tr[@_afrrk='"+i+"']//span[text()='"+dataLocator+"']")).click();
					afrrkInt = i;
					break tableloop;
				} catch(WebDriverException we){
					//Skips error...
				}
			//
			if(i+1>afrrkInt){
				throw new NoTableEntryFoundException();
			}
		}
		
		String rootTablePath = "//tr[@_afrrk='"+afrrkInt+"']";
		colNum += 1;
		
		while(getExcelData(busUnitlabel, colNum, "text").length()>0){
			
			labelLocator = TaskUtilities.filterDataLocator(getExcelData(busUnitlabel, colNum, "text"));
			if(labelLocator.contentEquals("Country")){
				labelLocator = "CountryCode";
			}
			
			labelLocatorPath = TaskUtilities.retryingSearchfromDupInput(labelLocator, rootTablePath);
			String labelTag = driver.findElement(By.xpath(labelLocatorPath)).getTagName();
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(busUnitinputs, colNum, type);
			
			if(labelTag.contentEquals("select")){
					inputData = driver.findElement(By.xpath(labelLocatorPath)).getAttribute("title");
				if(!inputData.contentEquals(dataLocator)){
					TaskUtilities.consolidatedInputSelector(labelLocatorPath, dataLocator);
				}
				
			} else if(!labelTag.contentEquals("select")){
					inputData = driver.findElement(By.xpath(labelLocatorPath)).getAttribute("value");
				if(!inputData.contentEquals(dataLocator)){
					TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
				}
			}
			
			colNum += 1;
		}
		
	}
	private void createBusUnit(TaskListManagerTopPage task) throws Exception{}
	
	
	private void manageReferenceDataSets(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
				+ "[contains(text(),'Manage Reference Data Sets')]", MAX_TIME_OUT);

		TaskUtilities.jsSideScroll(false);
		manageReferenceDataSet(task);
		
		TaskUtilities.jsFindThenClick("//button[text()='Ne']");
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
				+ "[contains(text(),'Manage Business Unit Set Assignment')]", MAX_TIME_OUT, new CustomRunnable() {
					
					@Override
					public void customRun() throws Exception {
						// TODO Auto-generated method stub
						TaskUtilities.jsCheckMissedInput();
						TaskUtilities.jsCheckMessageContainer();
					}
				});
	}
	private void manageReferenceDataSet(TaskListManagerTopPage task) throws Exception{
		int afrrkInt;
		String tableRootPath = "", inputValue = "";
		boolean isASpan = false, hasFoundName = false;
		
		refSetlabel = getGroupLabelRowNum("Reference Set") + 1;
		refSetinputs = refSetlabel + 1;
		colNum = defaultcolNum;
		
		afrrkInt = surveyCurrentTableInputs("Manage Reference Data Sets");
		while(getExcelData(refSetinputs, defaultcolNum, "text").length()>0){
			
			labelLocator = TaskUtilities.filterDataLocator(getExcelData(refSetlabel, defaultcolNum+1, "text"));
			dataLocator = getExcelData(refSetinputs, defaultcolNum+1, "text");
			System.out.println("datalocator is now: "+dataLocator);
			
			rdscheckloop:
			for(int i = 0; i<=afrrkInt; i++){
				tableRootPath = "//tr[@_afrrk='"+i+"']";
				
				labelLocatorPath = TaskUtilities.retryingSearchfromDupInput(labelLocator, tableRootPath);
				if(is_element_visible(tableRootPath+"//span[text()='"+dataLocator+"']", "xpath")){
						isASpan = true;
						hasFoundName = true;
						break rdscheckloop;
					}else if(labelLocatorPath.isEmpty()){
						continue rdscheckloop;
					}
				
				inputValue = TaskUtilities.jsGetInputValue(labelLocatorPath);
				
				if(inputValue.contentEquals(dataLocator)){
					hasFoundName = true;
					break rdscheckloop;
				}
			}
			
			System.out.println("Is a span is "+isASpan);
			if(hasFoundName){
					verifyUpdateRefDataSet(task, tableRootPath, isASpan);
				}else{
					createNewReferenceDataSet(task, afrrkInt);
				}
			
			refSetinputs += 1;
		}
	}
	private void verifyUpdateRefDataSet(TaskListManagerTopPage task, String tableRootPath, boolean isASpan) throws Exception{
		colNum = defaultcolNum;
		System.out.println("Is a span is "+isASpan+ " all inputs should be skipped...");
		TaskUtilities.jsSideScroll(false);
		
		while(getExcelData(refSetlabel, colNum, "text").length()>0){
			labelLocator = TaskUtilities.filterDataLocator(getExcelData(refSetlabel, colNum, "text"));
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(refSetinputs, colNum, type);
			
			if(isASpan){
					if(is_element_visible(tableRootPath+"//span[text()='"+dataLocator+"']", "xpath")){
						return;
					}
				}else{
					labelLocatorPath = TaskUtilities.retryingSearchfromDupInput(labelLocator, tableRootPath);
					String inputValue = driver.findElement(By.xpath(labelLocatorPath)).getAttribute("value");
					if(inputValue.contentEquals(dataLocator)){
							//Skips...
						}else{
							TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
						}
				}
			
			colNum += 1;
		}
		
	}
	private void createNewReferenceDataSet(TaskListManagerTopPage task, int afrrkInt) throws Exception{
		int i = afrrkInt + 1;
		String tableRootPath = "//tr[@_afrrk='"+i+"']";
		colNum = defaultcolNum;
		
		TaskUtilities.jsFindThenClick("//img[@title='Add Row']/..");
		TaskUtilities.customWaitForElementVisibility(tableRootPath, MAX_TIME_OUT);
		TaskUtilities.jsSideScroll(false);
		
		while(getExcelData(refSetlabel, colNum, "text").length()>0){
			labelLocator = TaskUtilities.filterDataLocator(getExcelData(refSetlabel, colNum, "text"));
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(refSetinputs, colNum, type);
			labelLocatorPath = TaskUtilities.retryingSearchfromDupInput(labelLocator, tableRootPath);
			
			TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
			
			colNum += 1;
		}
		
	}
	
	
	private void manageBusUnitAssignments(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
				+ "[contains(text(),'Manage Business Unit Set Assignment')]", MAX_TIME_OUT);

		TaskUtilities.jsSideScroll(false);
		manageBusUnitAssignment(task);
		
		TaskUtilities.jsFindThenClick("//button[text()='Ne']");
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
				+ "[contains(text(),'Manage Location Reference Set')]", MAX_TIME_OUT, new CustomRunnable() {
					
					@Override
					public void customRun() throws Exception {
						// TODO Auto-generated method stub
						TaskUtilities.jsCheckMissedInput();
						TaskUtilities.jsCheckMessageContainer();
					}
				});
	}
	private void manageBusUnitAssignment(TaskListManagerTopPage task) throws Exception{
		String tableRootPath;
		int afrrkInt = 0;
		
		manageBusUnitAsslabel = getGroupLabelRowNum("Manage Business Unit Assignment")+ 1;
		manageBusUnitAssinputs = manageBusUnitAsslabel + 1;
		colNum = defaultcolNum;
		
		afrrkInt = surveyCurrentTableInputs("Manage Business Unit Set Assignment");
		
		while(getExcelData(manageBusUnitAssinputs, defaultcolNum, "text").length()>0){
			labelLocator = TaskUtilities.filterDataLocator(getExcelData(manageBusUnitAsslabel, defaultcolNum+1, "text"));
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(manageBusUnitAssinputs, defaultcolNum+1, type);
			
			trsearcherloop:
			for(int i = 0 ; i <= afrrkInt; i++){
				tableRootPath = "//tr[@_afrrk='"+i+"']";
				
				if(is_element_visible(tableRootPath+"//span[text()='"+dataLocator+"']", "xpath") ||
						is_element_visible(tableRootPath+"//td[text()='"+dataLocator+"']", "xpath")){
					break trsearcherloop;
				}
				
			}
			
			verifyBusUnitAssignment(afrrkInt);
			manageBusUnitAssinputs += 1;
			colNum = defaultcolNum;
		}
		
	}
	private void verifyBusUnitAssignment(int afrrkInt) throws Exception{
		String nextlabelLocator;
		int matches = 0;
		String tableRootPath = "//tr[@_afrrk='"+afrrkInt+"']";
		TaskUtilities.jsSideScroll(false);
		
		buassignmentloop:
		while(getExcelData(manageBusUnitAsslabel, colNum, "text").length()>0){
			
			labelLocator = TaskUtilities.filterDataLocator(getExcelData(manageBusUnitAsslabel, colNum, "text"));
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(manageBusUnitAssinputs, colNum, type);
			
			nextlabelLocator = getExcelData(manageBusUnitAsslabel, colNum+1, "text");
			
			if(is_element_visible(tableRootPath+"//span[text()='"+dataLocator+"']", "xpath") ||
					is_element_visible(tableRootPath+"//td[text()='"+dataLocator+"']", "xpath")){
					matches += 1;
					colNum += 1;
					continue buassignmentloop;
				
				}else if(!nextlabelLocator.isEmpty()){
					System.out.println("No match found for the given input..");
					break buassignmentloop;
				}
			
			if(getExcelData(manageBusUnitAsslabel, colNum+1, "text").isEmpty() &&
					((colNum - defaultcolNum) == matches) && labelLocator.contentEquals("Default Reference Data Set")){
					labelLocatorPath = TaskUtilities.retryingSearchfromDupInput(labelLocator, tableRootPath);
					TaskUtilities.consolidatedInputSelector(labelLocatorPath, dataLocator);
				}
			
			colNum += 1;
		}
		
	}
	
	
	private void manageLocationRefSets(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
				+ "[contains(text(),'Manage Location Reference Set')]", MAX_TIME_OUT);

		TaskUtilities.jsSideScroll(false);
		manageLocationRefSet(task);
		
		TaskUtilities.jsFindThenClick("//button[text()='Ne']");
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Establish Enterprise Structures')]"
				+ "[contains(text(),'Review Results')]", MAX_TIME_OUT, new CustomRunnable() {
					
					@Override
					public void customRun() throws Exception {
						// TODO Auto-generated method stub
						TaskUtilities.jsCheckMissedInput();
						TaskUtilities.jsCheckMessageContainer();
					}
				});
	}
	private void manageLocationRefSet(TaskListManagerTopPage task) throws Exception{
		String tableRootPath;
		int afrrkInt = 0;
		
		locRefSetlabel = getGroupLabelRowNum("Manage Location Reference Set") + 1;
		locRefSetinputs = locRefSetlabel + 1;
		colNum = defaultcolNum2;
		
		afrrkInt = surveyCurrentTableInputs("Manage Location Reference Set");
		
		while(getExcelData(locRefSetinputs, defaultcolNum, "text").length()>0){
			labelLocator = TaskUtilities.filterDataLocator(getExcelData(locRefSetlabel, colNum, "text"));
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(locRefSetinputs, colNum, type);
			
			trsearcherloop:
			for(int i = 0 ; i <= afrrkInt; i++){
				tableRootPath = "//tr[@_afrrk='"+i+"']";
				
				if(is_element_visible(tableRootPath+"//span[text()='"+dataLocator+"']", "xpath") ||
						is_element_visible(tableRootPath+"//td[text()='"+dataLocator+"']", "xpath")){
					break trsearcherloop;
				}
				
			}
			
			catchRowThenSetLocRef(afrrkInt);
			locRefSetinputs += 1;
			colNum += defaultcolNum2;
		}
	}
	private void catchRowThenSetLocRef(int afrrkInt) throws Exception{
		String nextlabelLocator;
		int matches = 0;
		String tableRootPath = "//tr[@_afrrk='"+afrrkInt+"']";
		
		locrefloop:
		while(getExcelData(locRefSetlabel, colNum, "text").length()>0){
			
			labelLocator = TaskUtilities.filterDataLocator(getExcelData(locRefSetlabel, colNum, "text"));
			String type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(locRefSetinputs, colNum, type);
			
			nextlabelLocator = getExcelData(locRefSetlabel, colNum+1, "text");
			
			if(is_element_visible(tableRootPath+"//span[text()='"+dataLocator+"']", "xpath") ||
					is_element_visible(tableRootPath+"//td[text()='"+dataLocator+"']", "xpath")){
					matches += 1;
					colNum += 1;
					continue locrefloop;
				
				}else if(labelLocator.contentEquals("Description")){
					matches += 1;
					colNum += 1;
					continue locrefloop;
					
				}else if(!nextlabelLocator.isEmpty()){
					System.out.println("No match found for the given input..");
					break locrefloop;
				}
			
			if(nextlabelLocator.isEmpty() && ((colNum - defaultcolNum2) == matches) && 
				labelLocator.contentEquals("Reference Data Set")){
					labelLocator = "ReferenceDataSetId";
					labelLocatorPath = TaskUtilities.retryingSearchfromDupInput(labelLocator, tableRootPath);
					TaskUtilities.consolidatedInputSelector(labelLocatorPath, dataLocator);
				}
			
			colNum += 1;
		}
	}
	
	private void submitEnterprise(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.jsFindThenClick("//button[text()='Sub']");
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Enterprise Configuration']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//button[text()='Save and Close']");
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'"+searchData+"')]", MAX_TIME_OUT);
	}
	
	private int  getGroupLabelRowNum(String groupLabel){
		int rowNum = defaultlabel;
		int colNum = defaultcolNum;
		
		if(groupLabel.contentEquals("Manage Location Reference Set")){
			colNum = 12;
		}
		
		glloop:
		while(!getExcelData(rowNum, colNum, "text").contentEquals(groupLabel)){
			rowNum += 1;
			if(getExcelData(rowNum, colNum, "text").contentEquals("Manage Business Unit Assignment"))
				break glloop;
		}
		
		return rowNum;
	}
	private int  surveyCurrentTableInputs(String currentStep) throws Exception{
		int afrrkInt = -1;
		
		List<WebElement> queryFolder = driver.findElements(By.xpath("//table[contains(@summary,'Establish Enterprise Structures')][contains(@summary,'"+currentStep+"')]//tr"));
		System.out.println("folder size is "+queryFolder.size());
		for(WebElement inputEntry : queryFolder){
			
			String afrrk = inputEntry.getAttribute("_afrrk");
			System.out.println("afrrk is "+afrrk);
			
			if(afrrk != null && !afrrk.isEmpty() && !afrrk.contentEquals("")){
				if(Integer.parseInt(afrrk) > afrrkInt){
					afrrkInt =  Integer.parseInt(afrrk);
				}else{
					//Skips...
				}
			}
		}
		
		System.out.print("afrrkInt is now: "+afrrkInt);
		return afrrkInt;
	}
	private void createLocation(TaskListManagerTopPage task, String rootPath) throws Exception{
		final String cLRootPath = "//div[contains(@id,'createlocation')]";
		
		TaskUtilities.jsFindThenClick(rootPath+"//a[contains(@title,'Search')][contains(@title,'"+locationLabel+"')]");
		TaskUtilities.retryingWrapper(new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				Thread.sleep(2250);
				driver.findElement(By.xpath("//a[text()='Create']")).click();
			}
		});
		TaskUtilities.retryingWrapper(new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				Thread.sleep(2250);
				//TaskUtilities.jsCheckMessageContainer();
				driver.findElement(By.xpath(cLRootPath+"//div[text()='Create Location']")).click();
			}
		});
		String deliverAddress = "4000 Shoreline Court #210, San Francisco, California(CA) 94080, United States";
		
		TaskUtilities.consolidatedInputEncoder(task, TaskUtilities.retryingSearchfromDupInput("Name", cLRootPath), locationName);
		String llpath = TaskUtilities.retryingSearchfromDupInput("Delivery address", cLRootPath);
		TaskUtilities.consolidatedInputEncoder(task, llpath, deliverAddress);
		TaskUtilities.jsFindThenClick("//button[text()='O']");
		
		TaskUtilities.retryingWrapper(new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				Thread.sleep(2250);
				driver.findElement(By.xpath("//h1[contains(text(),'Establish Enterprise Structures')]")).click();
			}
		});

	}
	private void cancelTask() throws Exception{
		TaskUtilities.jsFindThenClick("//button[text()='ancel']");
		TaskUtilities.customWaitForElementVisibility("//button[text()='O']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//button[text()='O']");
		TaskUtilities.customWaitForElementVisibility("//div[text()='Create Enterprise Configuration']", MAX_TIME_OUT);
	}
	private void cancelConfiguration() throws Exception{
		TaskUtilities.jsFindThenClick("//div[contains(@id,'DetachDialog')]//a[@title='Close']");
		Thread.sleep(2500);
	}
}
