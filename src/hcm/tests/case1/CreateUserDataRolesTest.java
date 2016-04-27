package hcm.tests.case1;

import static util.ReportLogger.log;
import static util.ReportLogger.logFailure;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.testng.annotations.Test;

import common.BaseTest;
import common.CustomRunnable;
import common.DuplicateEntryException;
import common.ExcelUtilities;
import common.TaskUtilities;
import hcm.pageobjects.FuseWelcomePage;
import hcm.pageobjects.LoginPage;
import hcm.pageobjects.TaskListManagerTopPage;

public class CreateUserDataRolesTest extends BaseTest{
	private static final int MAX_TIME_OUT = 30;
	private String searchData, labelLocator, labelLocatorPath, labelTag, dataLocator, type;
	private String groupLabelLocator, nextLabelLocator="", nextLabelLocatorPath="";
	private boolean hasCreatedDataRoles = false;
	private boolean isInputDisabled = false;
	
	private static final int defaultcolNum = 7;
	private static final int defaultinputs = 10;
	private static final int grouplabel = 8;
	
	private String projectName = "Default";
	
	//private int projectCol = 8;
	private int label = 9;
	private int inputs = defaultinputs; //For test + 3
	private int colNum = defaultcolNum;
	
	int colNumSP = defaultcolNum;
	int rowNumSP = 0;
	int groupLabelSP, labelSP, inputsSP;
	String labelLocatorSP, labelLocatorSPPath, dataLocatorSP, groupLabelLocatorSP, newgroupLabelLocatorSP;
	String labeltypeSP;
	String inputReferenceSP, inputDataReferenceSP="No";

	private int projectRowNum = TestCaseRow;
	private int projectSheetcolNum = 7;
	
	@Test
	public void a_test() throws Exception  {
		testReportFormat();
	
	try{	
			
			createUserDataRole();
	  
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

	public void createUserDataRole() throws Exception{
		
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
		
		
		while(!hasCreatedDataRoles && !projectName.isEmpty() && !projectName.contentEquals("")){
			projectName = selectProjectName();
			
			if(projectName.contains("*")){
				projectRowNum += 1;
				continue;
			}
			
			hasCreatedDataRoles = createDataRoles(task);
			TaskUtilities.jsFindThenClick("//span[text()='Done']");
			TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'"+projectName+"')]", MAX_TIME_OUT);
			TaskUtilities.jsFindThenClick("//button[text()='D']");
			TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Implementation Projects']", MAX_TIME_OUT);
		}
		
		log("Data Roles has been created..");
		System.out.println("User Data Roles has been created..");
		
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
	
	private boolean createDataRoles(final TaskListManagerTopPage task) throws Exception{
		
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
			//TaskUtilities.jsFindThenClick("//div[text()='Workforce Deployment']"+"//a[@title='Expand']");
			TaskUtilities.retryingFindClick(By.xpath("//div[text()='Workforce Deployment']"+"//a[@title='Expand']"));
			TaskUtilities.customWaitForElementVisibility("//div[text()='Workforce Deployment']"+"//a[@title='Collapse']", MAX_TIME_OUT);
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
		
		TaskUtilities.customWaitForElementVisibility("//div[text()='Create Data Role for Implementation Users']", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//div[text()='Create Data Role for Implementation Users']/../..//a[@title='Go to Task']");
		
		TaskUtilities.customWaitForElementVisibility("//h1[text()='Manage Data Roles and Security Profiles']", MAX_TIME_OUT);
		
		groupLabelLocator = getExcelData(grouplabel, colNum, "text");
		while(getExcelData(inputs, defaultcolNum, "text").length()>0){
			try{
				enterDataRoleData(task);
				assignSecurityProfileToRoles(task);
				submitDataRole();
				takeScreenshot();
				
			} catch(DuplicateEntryException e ){
				takeScreenshot();
				System.out.println("Data Role already exists..");
				TaskUtilities.jsFindThenClick("//button[text()='ancel']");
				TaskUtilities.customWaitForElementVisibility("//a/span[text()=' Create']/..", MAX_TIME_OUT, new CustomRunnable() {
					
					@Override
					public void customRun() throws Exception {
						// TODO Auto-generated method stub
						try{
								TaskUtilities.jsFindThenClick("//button[text()='Yes']");
							} catch(Exception e){
								Thread.sleep(750);
							}
					}
				});
				
				//Skips...
			}

			//Reset Values:
			inputs += 1;
			colNum = defaultcolNum;
			nextLabelLocatorPath = "";
			nextLabelLocator = "";
			groupLabelLocator = getExcelData(grouplabel, colNum, "text");
		}
		return true;
	}
	
	public void enterDataRoleData(final TaskListManagerTopPage task) throws Exception{
		final String attribute = "[@disabled='']";
		
		TaskUtilities.jsFindThenClick("//a/span[text()=' Create']/..");
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Create Data Role')]", MAX_TIME_OUT, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				try{
					TaskUtilities.jsFindThenClick("//a/span[text()=' Create']/..");
					Thread.sleep(1000);
				} catch(Exception e){
					//Skips
				}
			}
		});
		
		dataroleloop:
		while(getExcelData(label, colNum, "text").length()>0){
			
			if(!nextLabelLocatorPath.contentEquals("null") && !nextLabelLocatorPath.isEmpty()){
					labelLocator = nextLabelLocator;
					labelLocatorPath = nextLabelLocatorPath;
				} else{
					labelLocator = getExcelData(label, colNum, "text");
					labelLocator = TaskUtilities.filterDataLocator(labelLocator);
				}		
			type = TaskUtilities.getdataLocatorType(labelLocator);
			dataLocator = getExcelData(inputs, colNum, type);
			
			TaskUtilities.retryingWrapper(new CustomRunnable() {
				
				@Override
				public void customRun() throws Exception {
					// TODO Auto-generated method stub
					if(nextLabelLocatorPath.contentEquals("null") || nextLabelLocatorPath.isEmpty()) labelLocatorPath = TaskUtilities.retryingSearchInput(labelLocator);
					
					TaskUtilities.retryingFindClick(By.xpath(labelLocatorPath));
					labelTag = driver.findElement(By.xpath(labelLocatorPath)).getTagName();
					isInputDisabled = Boolean.parseBoolean(driver.findElement(By.xpath(labelLocatorPath)).getAttribute("disabled"));
					
					if(!labelTag.contentEquals("select") && !isInputDisabled){
							TaskUtilities.consolidatedInputEncoder(task, labelLocatorPath, dataLocator);
							if(labelLocator.contains("Security Profile")) Thread.sleep(5000);;
						} else if(labelTag.contentEquals("select") && !isInputDisabled){
							TaskUtilities.consolidatedInputSelector(labelLocatorPath, dataLocator);
						} else if(dataLocator.contains("blank")){
							//Skips blanks
						} else {
							//Skips others...
						}
				}
			});
			
			nextLabelLocator = getExcelData(label, colNum+1, "text");
			nextLabelLocator = TaskUtilities.filterDataLocator(nextLabelLocator);
			if(nextLabelLocator.isEmpty() || nextLabelLocator.contentEquals("")){
				colNum += 1;
				continue dataroleloop;
			}
			
			TaskUtilities.retryingWrapper(new CustomRunnable() {
				
				@Override
				public void customRun() throws Exception {
					// TODO Auto-generated method stub
					if(nextLabelLocator.contains("Security Profile")){
								groupLabelLocator = getExcelData(grouplabel, colNum+1, "text");
								System.out.println("group label is now: "+groupLabelLocator);
								nextLabelLocatorPath = ""+(TaskUtilities.retryingSearchInput(nextLabelLocator));
								//nextLabelLocatorPath = "//h2[text()='"+groupLabelLocator+"']/../../../../../../.."+nextLabelLocatorPath;
								if(groupLabelLocator.contentEquals("Public Person"))
									nextLabelLocatorPath = "//h2[text()='"+groupLabelLocator+"']/../../../../../../.."+nextLabelLocatorPath;
								
							} else if(!nextLabelLocator.contains("Security Profile") && !groupLabelLocator.contentEquals("Select Role")){
								nextLabelLocatorPath = ""+(TaskUtilities.retryingSearchInputwithAttr(nextLabelLocator, attribute));
								
							} else {
								nextLabelLocatorPath = ""+(TaskUtilities.retryingSearchInput(nextLabelLocator));
								
							}
				}
			});
			
			type = TaskUtilities.getdataLocatorType(nextLabelLocator);
			
			String newgroupLabelLocator = getExcelData(grouplabel, colNum+1, "text");
			if(!newgroupLabelLocator.isEmpty() || !newgroupLabelLocator.contentEquals("")){
				groupLabelLocator = newgroupLabelLocator;
				System.out.println("group label is updated to: "+groupLabelLocator);
			}
			
			System.out.println("nextLabelLocatorPath value: "+nextLabelLocatorPath);
			boolean clickNext = false;
			clickNext = checkNextLabelLocatorPresence(attribute);
			if(!clickNext) continue dataroleloop;
			
			colNum += 1;
		}
		
		System.out.println("Moving to next Assignment...");
		
	}
	
	public void assignSecurityProfileToRoles(TaskListManagerTopPage task) throws Exception{
		String labelSet;
		
		System.out.println("Now assigning security profiles to roles...");
		TaskUtilities.jsFindThenClick("//button[text()='Ne']");
		
		colNum = defaultcolNum;
		while(getExcelData(label, colNum, "text").length()>0){
			
			while(!getExcelData(label, colNum, "text").contains("Security Profile")){
				colNum += 1;
			}
			labelSet = getExcelData(grouplabel, colNum, "text");
			System.out.println("labelSet is now: "+labelSet);
			
			String gLabelLocator = getExcelData(label, colNum, "text");
			gLabelLocator = TaskUtilities.filterDataLocator(gLabelLocator);
			if(gLabelLocator.contains("LDG")) gLabelLocator = gLabelLocator.replace("LDG", "Legislative Data Group");
			if(gLabelLocator.contains("Person") && labelSet.contentEquals("Public Person")){
				gLabelLocator = gLabelLocator.replace("Person", "Public Person");
			}else if(gLabelLocator.contains("Flow Pattern")){
				gLabelLocator = groupLabelLocator+" Security Profile";
			}

			System.out.println("gLabelLocator is now: "+gLabelLocator);
			String gLabelLocatorPath = "//h1[contains(text(),'"+gLabelLocator+"')]";
			try{
					TaskUtilities.customWaitForElementVisibility(gLabelLocatorPath, MAX_TIME_OUT);
				} catch(TimeoutException e){
					TaskUtilities.jsFindThenClick("//button[text()='Ne']");
					TaskUtilities.customWaitForElementVisibility(gLabelLocatorPath, MAX_TIME_OUT, new CustomRunnable() {
						
						@Override
						public void customRun() throws Exception {
							// TODO Auto-generated method stub
							TaskUtilities.jsCheckMissedInput();
							TaskUtilities.jsCheckMessageContainer();
						}
					});
				}
			/*
				Nothing to do here yet...
			*/
			if(gLabelLocator.contentEquals("Person Security Profile")){
				manageSecurityProfile(task);
			}
			
			TaskUtilities.jsFindThenClick("//button[text()='Ne']");
			colNum += 1;
		}
		
	}
	
	public void submitDataRole() throws Exception{
		TaskUtilities.customWaitForElementVisibility("//h1[contains(text(),'Review')]", MAX_TIME_OUT);
		TaskUtilities.jsFindThenClick("//button[text()='Sub']");
		TaskUtilities.customWaitForElementVisibility("//a/span[text()=' Create']/..", 300, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				TaskUtilities.jsCheckMissedInput();
				TaskUtilities.jsCheckMessageContainer();
			}
		});
		
	}
	
	public boolean checkNextLabelLocatorPresence(String attribute) throws Exception{
		if(nextLabelLocatorPath.contentEquals("null") || nextLabelLocatorPath.isEmpty() || nextLabelLocatorPath.contentEquals("")){

			try{

					//TaskUtilities.retryingFindClick(By.xpath(nextLabelLocatorPath));
					TaskUtilities.jsScrollIntoView(nextLabelLocatorPath);
					colNum += 1;
					return false;
					
				} catch(WebDriverException e){
				//	try{
				//			if(nextLabelLocator.contains("Security Profile")){
				//				throw new WebDriverException();
				//			}
				//			nextLabelLocatorPath = ""+(TaskUtilities.retryingSearchInput(nextLabelLocator, attribute));
				//			TaskUtilities.jsScrollIntoView(nextLabelLocatorPath);
				//			colNum += 1;
							//continue dataroleloop;
				//			return false;
				//		} catch(WebDriverException e1){
							//Skips
				//		}
				}
			
			TaskUtilities.jsFindThenClick("//button[text()='Next']");
			TaskUtilities.customWaitForElementVisibility("//h2[contains(text(),'"+groupLabelLocator+"')]", MAX_TIME_OUT, new CustomRunnable() {
				
				@Override
				public void customRun() throws Exception {
					// TODO Auto-generated method stub
					TaskUtilities.jsCheckMissedInput();
					TaskUtilities.jsCheckMessageContainer();
				}
			});
		}
		return true;
	}

	
	public void manageSecurityProfile(TaskListManagerTopPage task) throws Exception{
		TaskUtilities.retryingFindClick(By.xpath("//a[contains(@title,'Person Security Profile')]"));
		TaskUtilities.customWaitForElementVisibility("//a[text()='Create New']", MAX_TIME_OUT, new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				try{
					TaskUtilities.retryingFindClick(By.xpath("//a[contains(@title,'Person Security Profile')]"));
				} catch(Exception e){
					//Skips
				}
				Thread.sleep(1000);
			}
		});
		TaskUtilities.jsFindThenClick("//a[text()='Create New']");
		TaskUtilities.customWaitForElementVisibility("//label[text()='Name']/../..//input", MAX_TIME_OUT);
		
		while(!getExcelData(rowNumSP, colNumSP, "text").contentEquals("*Name")){
			rowNumSP += 1;
		}
		
		groupLabelSP = rowNumSP - 1;
		labelSP = rowNumSP;
		inputsSP = rowNumSP + 1 + (inputs - defaultinputs);
		
		addBasicDetails(task); takeScreenshot();
		addPersonTypes(task); takeScreenshot();
		addManageHierarchy(task); takeScreenshot();
		addWorkforceStructures(task); takeScreenshot();
		addGlobalNameRange(task); takeScreenshot();
		addCustomCriteria(task); takeScreenshot();
		
		colNumSP = defaultcolNum;//Reset the pointer...
	}
	
	public void setAndTestLocators() throws Exception{
		int tries = 0;
		while(tries < 3){
			try{
					labelLocatorSP = getExcelData(labelSP, colNumSP, "text");
					labelLocatorSP = TaskUtilities.filterDataLocator(labelLocatorSP);
					
					labelLocatorSPPath = TaskUtilities.retryingSearchInput(labelLocatorSP);
					String type = TaskUtilities.getdataLocatorType(labelLocatorSP);
					dataLocatorSP = getExcelData(inputsSP, colNumSP, type);
					
					TaskUtilities.jsScrollIntoView(labelLocatorSPPath);
					//labeltypeSP = driver.findElement(By.xpath(labelLocatorSPPath)).getAttribute("type");
					//isInputChecked = Boolean.parseBoolean(driver.findElement(By.xpath(labelLocatorSPPath)).getAttribute("checked"));
					//isInputDisabledSP = Boolean.parseBoolean(driver.findElement(By.xpath(labelLocatorSPPath)).getAttribute("disabled"));
					tries = 4; //Ends loop;
				} catch(Exception e){
					//Skips
					tries += 1;
				}
			
		}
		tries = 0;
	}

	public void tickCheckbox() throws Exception{
		if(dataLocatorSP.contains("Yes") && is_element_visible(labelLocatorSPPath+"[@checked='']", "xpath")){
				TaskUtilities.jsFindThenClick(labelLocatorSPPath);
				Thread.sleep(750);
			} else if(!dataLocatorSP.contains("Yes") && !is_element_visible(labelLocatorSPPath+"[@checked='']", "xpath")){
				TaskUtilities.jsFindThenClick(labelLocatorSPPath);
				Thread.sleep(750);
				colNumSP += 1;
				while(getExcelData(groupLabelSP, colNumSP, "text").isEmpty()
						&& !getExcelData(labelSP, colNumSP, "text").isEmpty()){
					colNumSP += 1;
					//Skip...
				}
				colNumSP -= 1;//StepBack;;
				return;
			} else{
				//Does nothing...
			}
		colNumSP += 1;
	}
	
	public void addBasicDetails(TaskListManagerTopPage task) throws Exception{
		setAndTestLocators();
		TaskUtilities.consolidatedInputEncoder(task, labelLocatorSPPath, dataLocatorSP);
		colNumSP += 1;
		
		while(getExcelData(groupLabelSP, colNumSP, "text").isEmpty()){
			setAndTestLocators();

			if(dataLocatorSP.contains("Yes") && is_element_visible(labelLocatorSPPath+"[@checked='']", "xpath")){
					TaskUtilities.jsFindThenClick(labelLocatorSPPath);
					Thread.sleep(750);
				} else if(!dataLocatorSP.contains("Yes") && !is_element_visible(labelLocatorSPPath+"[@checked='']", "xpath")){
					TaskUtilities.jsFindThenClick(labelLocatorSPPath);
					Thread.sleep(750);
				} else{
					//Does nothing...
				}
			
			colNumSP += 1;
		}
		
	}

	public void addPersonTypes(TaskListManagerTopPage task) throws Exception{
		setAndTestLocators();
		
		if(!dataLocatorSP.contains("Yes") && !is_element_visible(labelLocatorSPPath+"[@checked='']", "xpath")){
				TaskUtilities.jsFindThenClick(labelLocatorSPPath);
				Thread.sleep(750);
				colNumSP += 1;
				while(getExcelData(groupLabelSP, colNumSP, "text").isEmpty()){
					colNumSP += 1;
					//Skip...
				}
				return;
			} else if(dataLocatorSP.contains("Yes") && is_element_visible(labelLocatorSPPath+"[@checked='']", "xpath")){
				TaskUtilities.jsFindThenClick(labelLocatorSPPath);
				Thread.sleep(750);
			} else{
				//Does nothing...
			}
		
		colNumSP += 1;
		while(getExcelData(groupLabelSP, colNumSP, "text").isEmpty()){
			//Should be doing something here...Not Now.
			colNumSP += 1;
		}
	}

	public void addManageHierarchy(TaskListManagerTopPage task) throws Exception{
		String labelTypeSP;
		setAndTestLocators();
		tickCheckbox();
		//colNumSP += 1; TEST
		while(getExcelData(groupLabelSP, colNumSP, "text").isEmpty()){
			setAndTestLocators();
			//Do something here.. Not now.
			String labelTagSP = driver.findElement(By.xpath(labelLocatorSPPath)).getTagName();
			
			if(!labelTagSP.contentEquals("select")){
				labelTypeSP = driver.findElement(By.xpath(labelLocatorSPPath)).getAttribute("type");
				
				if(!labelTypeSP.contentEquals("radio")){
						TaskUtilities.retryingInputEncoder(task, labelLocatorSPPath, dataLocatorSP);
					} else {
						TaskUtilities.jsFindThenClick(labelLocatorSPPath+"[text()='"+dataLocatorSP+"']");
					}
				
			} else if(labelTagSP.contentEquals("select")){
				TaskUtilities.consolidatedInputSelector(labelLocatorSPPath, dataLocatorSP);
			}
			colNumSP += 1;
		}
	}

	public void addWorkforceStructures(TaskListManagerTopPage task) throws Exception{
		workforceloop:
		while(getExcelData(groupLabelSP, colNumSP, "text").isEmpty()
				|| getExcelData(groupLabelSP, colNumSP, "text").contentEquals("Workforce Structures")){
			setAndTestLocators();
			
			if(dataLocatorSP.contains("Yes") && is_element_visible(labelLocatorSPPath+"[@checked='']", "xpath")){
					TaskUtilities.jsFindThenClick(labelLocatorSPPath);
					Thread.sleep(750);
				} else if(!dataLocatorSP.contains("Yes") && !is_element_visible(labelLocatorSPPath+"[@checked='']", "xpath")){
					TaskUtilities.jsFindThenClick(labelLocatorSPPath);
					Thread.sleep(750);
					colNumSP += 1;
					continue workforceloop;
				} else{
					//Does nothing...
				}
			
			//Do Something here.. Not now.
			String dataLocatorSP_input = dataLocatorSP.substring(dataLocatorSP.indexOf(",")+2);
			String labelLocatorSPPath_input = "//label[text()='"+labelLocatorSP+"']/../../../../../../..//input[@type='text']";
			System.out.println("Input path is: "+labelLocatorSPPath_input);
			TaskUtilities.retryingInputEncoder(task, labelLocatorSPPath_input, dataLocatorSP_input);
			
			colNumSP += 1;
			
		}
	}
	
	public void addGlobalNameRange(TaskListManagerTopPage task) throws Exception{
		setAndTestLocators();
		tickCheckbox();
		colNumSP += 1;
		//Do something here... Not now.
		while(getExcelData(groupLabelSP, colNumSP, "text").isEmpty()){
			setAndTestLocators();
			TaskUtilities.retryingInputEncoder(task, labelLocatorSPPath, dataLocatorSP);
			colNumSP += 1;
		}
	}
	
	public void addCustomCriteria(TaskListManagerTopPage task) throws Exception{
		setAndTestLocators();
		tickCheckbox();
		colNumSP += 1;
		//Do something here.. Not now.
		while(getExcelData(groupLabelSP, colNumSP, "text").isEmpty() 
				&& !getExcelData(labelSP, colNumSP, "text").isEmpty()){
				setAndTestLocators();
	
				labelLocatorSPPath = "//input[@type='textarea']";
				TaskUtilities.retryingInputEncoder(task, labelLocatorSPPath, dataLocatorSP);
				
				colNumSP += 1;
		}
	}
}
