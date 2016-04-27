package hcm.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.TimeoutException;

import common.BasePage;
import common.TaskUtilities;
import static common.BaseTest.TestCaseRow;
import static common.ExcelUtilities.getCellData;
import static util.ReportLogger.log;

public class TaskListManagerTopPage extends BasePage{
	
	public TaskListManagerTopPage(WebDriver driver){
		super(driver);
	}
	
	@Override
    public String getPageId()
    {
        return "/setup/faces/TaskListManagerTop";
    }

	public void clickTask(String menu){
		
		clickByXpath("//li/a[contains(text(),'"+ menu +"')]");		
		log("Clicking " + menu +"...");
		System.out.println("Clicking " + menu +"...");

	}
	
	public void enterSearch(int colNum) throws Exception{
		String value = getCellData(TestCaseRow, colNum);
		enterText("pt1:USma:0:MAnt1:1:pt1:r1:0:resultAppPanel:q1:value00::content", value); //
		log("Entered in the Search box...");
		System.out.println("Entered in the Search box...");
	}
	
	public void clickSearchButton(){
		
		clickByXpath("//button[text()='Search']");		
		log("Clicking Search button...");
		System.out.println("Clicking Search button...");

	}
	
	public void clickNextButton(){
		
		clickByXpath("//button[text()='Ne']");		
		log("Clicking Next button...");
		System.out.println("Clicking Next button...");

	}
	
	public void clickExpandFolder(int colNum) throws Exception{
		
		String folder = getCellData(TestCaseRow, colNum);
		clickByXpath("//tbody/tr/td/div[text() = '"+folder+"']/span/a");
		Thread.sleep(3000);	
		log("Clicking expand...");
		System.out.println("Clicking expand...");
	}
	
	public void clickExpandFolder(String commonPath) throws Exception{
		
		clickByXpath(commonPath+"/span/a");
		//Thread.sleep(3000);	
		waitForElementToBeClickable(10, commonPath+"/span/a[contains(@title,'Collapse')]");
		waitForElementToBeInvisible("//div[text()='Fetching Data...']", "Fetching Data...", 10);
		log("Clicking expand...");
		System.out.println("Clicking expand...");
	}
	
	public void clickCollapseFolder(String commonPath) throws Exception{
		clickByXpath(commonPath+"/span/a");
		//Thread.sleep(3000);
		waitForElementToBeClickable(10, commonPath+"/span/a[contains(@title,'Expand')]");
		waitForElementToBeInvisible("//div[text()='Fetching Data...']", "Fetching Data...", 10);
		log("Clicking collapse...");
		System.out.println("Clicking collapse...");
	}
	
	public void clickCollapseFolder(int colNum) throws Exception{
		
		String folder = getCellData(TestCaseRow, colNum);
		clickByXpath("//tbody/tr/td/div[text() = '"+folder+"']/span/a");
		Thread.sleep(3000);
		log("Clicking collapse...");
		System.out.println("Clicking collapse...");
	}
	
	public void clickSaveandOpenProject() throws Exception{
		clickByXpath("//span[text()='Save and Open Project']");
		Thread.sleep(3000);	
		log("Saving Project...");
		System.out.println("Saving Project...");
		
	}

	//button paths
	public String getApplyButtonPath(){
		return "//button[text()='App']";
	}
	
	public String getSearchButtonPath(){
		return "//button[text()='Search']";
	}
	
	public String getCancelButtonPath(){
		return "//button[text()='ancel']";
	}
	
	//link paths
	public String getSelectAndAddLinkPath(){
		return "//img[@title='Select and Add']/..[@href='#']";
	}
	
	
	public boolean jsScrollDown(boolean isScrollingDown) throws Exception{
		
		int scrollValue = 460;
		System.out.println("Scroll is now moving....");	
		JavascriptExecutor js = (JavascriptExecutor)driver;
		boolean scrollDownAgain = (boolean) js.executeScript( 
				
				"taskFolderArray=[];"+
				"taskFolderInt = -255;"+
				"queryFolderName = [];"+
				"oldScrollerValue = 0;"+
				"queryFolderName = document.querySelectorAll('div');"+

				"for(var i=0; i<queryFolderName.length;i++){"+
				"	curFolderId = queryFolderName[i].id;"+
				"	curFolderId1 = queryFolderName[i].style.overflow;"+
				"	curFolderId2 = queryFolderName[i].style.position;"+
				"	if(taskFolderInt < 0)taskFolderInt = -1;"+
				"	if(curFolderId.contains('scroller')){"+
				"		taskFolderInt += 1;	"+
				"		taskFolderArray[taskFolderInt] = [curFolderId, curFolderId1, curFolderId2];"+
				"}}"+
	      
				"for(var j =0; j<taskFolderArray.length;j++){"+
				"	  newScroller = document.getElementById(taskFolderArray[j][0]);"+
				"	  if(newScroller.scrollTop != undefined){"+
				"			if("+isScrollingDown+") {"+
				"				if(taskFolderArray[j][0].contains('scroller')){"+
				"					oldScrollerValue = newScroller.scrollTop;}"+
				
				"				newScroller.scrollTop += "+scrollValue+";}"+
				"			else if(!"+isScrollingDown+") newScroller.scrollTop = 0;"+
				"			if(oldScrollerValue == newScroller.scrollTop"+
				"				&& taskFolderArray[j][0].contains('scroller')"+
				"					&& oldScrollerValue > 0)"+
				"					return false;"+
				"	  }"+
				"}return true;"
			);
			Thread.sleep(1000);
			//WebDriverWait waitLoadHandler = new WebDriverWait(driver, 4);
			Wait<WebDriver> waitLoadHandler = new FluentWait<WebDriver>(driver)
					.withTimeout(5, TimeUnit.SECONDS)
					.pollingEvery(500, TimeUnit.MILLISECONDS)
					.ignoring(NoSuchElementException.class)
					.ignoring(StaleElementReferenceException.class);
			try{
				waitLoadHandler.until(ExpectedConditions.invisibilityOfElementWithText(By.xpath("//div[text()='Fetching Data...']"), "Fetching Data..."));
				System.out.println("Page loading has been finished.....");
			}catch(TimeoutException e){
				e.printStackTrace();
			}
			return scrollDownAgain;
	}
	
	public void scrollElementIntoView(int rowNum, int colNum) throws Exception{
		
		if(rowNum == -1)rowNum = TestCaseRow;
		String folder = getCellData(rowNum, colNum);
		driver.manage().timeouts().implicitlyWait(100, TimeUnit.MILLISECONDS);
		System.out.println("Centering view.....");
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript(
			"	taskFolderArray=[];"									+
			"	taskFolderInt = -255;"									+
			"	queryFolderName = [];"									+
			"	scrollerFolderArray = [];"								+
			"	scrollerFolderInt = 0;"									+
			"	queryFolderName = document.querySelectorAll('div');"	+

			"for(var i=0; i<queryFolderName.length;i++){"				+
			"	curFolderId  = queryFolderName[i].className;"			+
			"	curFolderId1 = queryFolderName[i].textContent;"			+
			"	curFolderId2 = queryFolderName[i].style.overflow;"		+
			"	curFolderId3 = queryFolderName[i].style.position;"		+
			"	curFolderId4 = queryFolderName[i].id;"					+
			"	if(taskFolderInt < 0)taskFolderInt = -1;"				+
			"	if(curFolderId1 == '"+folder+"'){"						+
			"		taskFolderInt += 1;	"								+
			"		taskFolderArray[taskFolderInt] = [curFolderId1, curFolderId];"			+
			"	}if(curFolderId2 == 'auto' && curFolderId3 == 'absolute'){"					+
			"		scrollerFolderArray[scrollerFolderInt] = curFolderId4;"					+
			"		scrollerFolderInt += 1;}"												+
			"}"																				+
			
			//Originally harcoded;;;
			"divTaskClass = taskFolderArray[0][1];"											+
			//Upgraded to softcode;;
			"scrollerID2 = ''+scrollerFolderArray[0];"										+
			"outerdivScroller = document.getElementsByTagName('div')[scrollerID2];"			+
			"if( outerdivScroller != undefined )"											+
			"	outerdivScroller.scrollTop = outerdivScroller.scrollHeight;"				+
			"divTasks = document.getElementsByClassName(divTaskClass);"						+
			"for(var i =0; i < divTasks.length; i++){"										+
			"	if(divTasks[i].textContent === '"+folder+"')"								+
			"		divTasks[i].scrollIntoView(false);"										+ 
			"	}"
		);
		
	}
	
	public void scrollElementIntoView(String commonPath) throws Exception{
		
		System.out.println("Centering view.....");
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript(
			"	queryFolderName = [];"									+
			"	scrollerFolderArray = [];"								+
			"	scrollerFolderInt = 0;"									+
			"	queryFolderName = document.querySelectorAll('div');"	+

			"for(var i=0; i<queryFolderName.length;i++){"				+
			"	curFolderId2 = queryFolderName[i].style.overflow;"		+
			"	curFolderId3 = queryFolderName[i].style.position;"		+
			"	curFolderId4 = queryFolderName[i].id;"					+
			"	if(curFolderId2 == 'auto' && curFolderId3 == 'absolute'){"					+
			"		scrollerFolderArray[scrollerFolderInt] = curFolderId4;"					+
			"		scrollerFolderInt += 1;}"												+
			"}"																				+
			
			"function getElementByXPath(xPath){"+
			"	return document.evaluate(xPath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"+
			"}"+
			"taskHolder = getElementByXPath(\""+commonPath+"\");"+
			"if(taskHolder != 'null')"+
			"	taskHolder.scrollIntoView(false);"+
			
			//Upgraded to softcode;;
			"scrollerID2 = ''+scrollerFolderArray[0];"										+
			"outerdivScroller = document.getElementsByTagName('div')[scrollerID2];"			+
			"if( outerdivScroller != undefined ){"											+
			"	outerdivScroller.scrollTop = outerdivScroller.scrollHeight;}"
		);
		
		Thread.sleep(250);
		try{
				TaskUtilities.fluentWaitForElementInvisibility("//div[text()='Fetching Data...']", "Fetching Data...", 15);
			} catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public void waitForElementToBeInvisible(String xPath, String textValue, int waitTime) throws Exception{
		WebDriverWait waitLoadHandler = new WebDriverWait(driver, waitTime);
		try{
			waitLoadHandler.until(ExpectedConditions.invisibilityOfElementWithText(By.xpath(xPath), textValue));
		}catch(TimeoutException e){
			e.printStackTrace();
		}
	}
	
	public void waitForElementToBeClickable(int waitTime, String elementPath) throws Exception{
		
		WebDriverWait wait = new WebDriverWait(driver, waitTime);
		try{
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(elementPath)));
		}catch(TimeoutException e){
			System.out.println("Waiting for element has timed out... No alternative method available.");
		}
		System.out.println("Check element presence is now finised...");
	}

	public void fluentWaitForElementPresence(int waitTime, By by) throws Exception{
		
		Thread.sleep(250); //Momentary pause.....
		Wait<WebDriver> waitLoadHandler = new FluentWait<WebDriver>(driver)
				.withTimeout(waitTime, TimeUnit.SECONDS)
				.pollingEvery(500, TimeUnit.MILLISECONDS)
				.ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class);
		
		waitLoadHandler.until(ExpectedConditions.presenceOfElementLocated(by));
		System.out.println("Element is now present in the DOM.....");
	}
	
	public String findMainTaskUniqueID(int rowNum, int colNum)throws Exception{
		
		String mainTaskUniqueID = null;
		
		System.out.println("Cracking Folder Schema....");
		if(rowNum == -1)rowNum = TestCaseRow;
		String folder = getCellData(rowNum, colNum);
		
		JavascriptExecutor js = (JavascriptExecutor)driver;
		
		while(mainTaskUniqueID == null){
			System.out.println("Obtaining valid unique main task id....");
			mainTaskUniqueID = (String)js.executeScript(
			
				"taskFolderArray=[];"+
				"taskFolderInt = -255;"+
				"queryFolderName = [];"+
				"queryFolderName = document.querySelectorAll('tr');"+
				"cInt = 0;"+
				"retArray = [];"+
	
				"for(var i=0; i<queryFolderName.length;i++){"+
				"	curFolderId = queryFolderName[i].attributes._afrap;"+
				"	curFolderId1 = queryFolderName[i].attributes._afrrk;"+
				"	if(taskFolderInt < 0)taskFolderInt = -1;"+
				"	if(curFolderId === undefined){"+
				"		taskFolderInt += 1;	"+
				"		taskFolderArray[taskFolderInt] = [queryFolderName[i].textContent, curFolderId, curFolderId1];"+
				"		}}"+
	
				"for(var j=0; j<taskFolderArray.length;j++){"+
				"	if(taskFolderArray[j][2] != undefined && taskFolderArray[j][0].contains('"+folder+"')){"+
				"	  retArray[cInt] = taskFolderArray[j];"+
				"	  return retArray[0][2].textContent}}"
				);
			
			driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
		}
			
		System.out.println("UniqueID is now....."+mainTaskUniqueID);
		return mainTaskUniqueID;
	}
	
	public ArrayList<String> queryAllVisibleTasks(ArrayList<String> oldArray) throws Exception{
		java.util.List<WebElement> queryFolder = driver.findElements(By.xpath("//table[contains(@summary, 'Offerings')]/tbody/tr"));
		if(queryFolder.size()<2) queryFolder = driver.findElements(By.xpath("//tr"));
		//Make sure queryFolder has more than 2 elements...
		
		ArrayList<String> allTasksTempHolder = new ArrayList<String>();
		
		System.out.println("\n**********");
		String afrrkAttr, afrapAttr, textIn;
		int tasksIndex= 0;
		int size = queryFolder.size();
		int oldArraysize = oldArray.size();
		int lastNullCounter = 0;
		System.out.println("Folder size: "+size);
		
		queryFolderloop:
		for(WebElement task :queryFolder){
		//for(int i=0; i<size; i++){
			tasksIndex += 1;
			textIn = task.getText();
			
			if(tasksIndex < size && textIn.equals("") && textIn.isEmpty()){
				if(tasksIndex >= size/2) lastNullCounter+=1;
				continue queryFolderloop;
			}
			
			afrrkAttr = task.getAttribute("_afrrk");
			afrapAttr = task.getAttribute("_afrap");
			
			if((afrapAttr == null && afrrkAttr != null || afrapAttr != null) 
					&& !textIn.equals("") && !textIn.isEmpty()){
				
				allTasksTempHolder = filterThenAddTaskString(textIn, allTasksTempHolder);	
			}
			
			//if(tasksIndex >= size & oldArraysize != 0){
				if(oldArraysize != 0 && allTasksTempHolder.size() == 2){
					if(oldArray.get(1).contentEquals(allTasksTempHolder.get(1))){
						return null;
					}
				}
			//}
		}
		
		if(lastNullCounter >= 1){
			jsScrollDown(true);
			queryFolder = driver.findElements(By.xpath("//table[contains(@summary, 'Offerings')]/tbody/tr"));
			int startIndex = queryFolder.size() - lastNullCounter - 1;
			int lastIndex = queryFolder.size();
			for(WebElement task : queryFolder.subList(startIndex, lastIndex)){
				
				afrrkAttr = task.getAttribute("_afrrk");
				afrapAttr = task.getAttribute("_afrap");
				textIn = task.getText();
				
				if((afrapAttr == null && afrrkAttr != null || afrapAttr != null) 
						&& !textIn.equals("") && !textIn.isEmpty()){
					allTasksTempHolder = filterThenAddTaskString(textIn, allTasksTempHolder);
				}
			}
		}
		
		return allTasksTempHolder;
	}
	
	public ArrayList<String> filterThenAddTaskString(String textIn, ArrayList<String> allTasksTempHolder) throws Exception{
		
		String caughtTask;
		int entIndex = 0;
		
		System.out.println("Now holding: '"+textIn+"'");
		int index = textIn.indexOf("Yes");
		if(index == -1){
			index = textIn.indexOf("No");
		}
			if(index == -1){
				index = textIn.indexOf("Implemented");
			}
				if(index == -1){
					index = textIn.indexOf("Not Started");
				}	
					if(index == -1){
						index = textIn.indexOf("In Progress");
					}
		
		//Clean up InnerText Value.....
		if(!textIn.isEmpty() && !textIn.equals("") && index != -1){
			System.out.println("Cleaning up entry.....");
			caughtTask = textIn.substring(0,index).substring(2);
			entIndex = caughtTask.lastIndexOf("\n");
			if(entIndex != -1){
				caughtTask = caughtTask.substring(0,entIndex);
				System.out.println("Filtered text:'"+caughtTask+"'");
				allTasksTempHolder.add(caughtTask);
				System.out.println("Now holding: "+allTasksTempHolder);
			}
		}
		
		return allTasksTempHolder;
	} 
	
	public void clickElementByXPath(String commonPath){
	    WebDriverWait wait = new WebDriverWait(driver, 5); 
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(commonPath)));
	    driver.findElement(By.xpath(commonPath)).click();
	}
	
	public void clickMainTaskCheckbox(int colNum, String commonPath) throws Exception{
		
		String folder = getCellData(TestCaseRow, colNum);
		//clickByXpath("//tbody/tr/td/div[text() = '"+folder+"']/../../td/span/span/span/input");
		clickByXpath(commonPath+"/../../td/span/span/span/input");
		log("Selecting "+folder+" task...");
		System.out.println("Selecting "+folder+" task...");
	}
	
	public void clickMainTaskCheckbox(int rowNum, int colNum, String commonPath) throws Exception{
		
		String folder = getCellData(rowNum, colNum);
		//clickByXpath("//tbody/tr/td/div[text() = '"+folder+"']/../../td/span/span/span/input");
		clickByXpath(commonPath+"/../../td/span/span/span/input");
		log("Selecting "+folder+" task...");
		System.out.println("Selecting "+folder+" task...");
	}
	
	public void clickSubTaskCheckbox(int colNum, String commonPath) throws Exception{
		
		String task = getCellData(TestCaseRow, colNum);
		
		clickByXpath(commonPath+"/../../td/span/span/span/input");	
		
		log("Selecting "+task+" sub task...");
		System.out.println("Selecting sub "+task+" task...");
	}
	
	public void clickSubTaskCheckbox(int colNum, int colNum2) throws Exception{
		
		String folder = getCellData(TestCaseRow, colNum);
		String subfolder = getCellData(TestCaseRow, colNum2);
		
		//clickByXpath(commonPath+"/../../td/span/span/span/input");	
		
		log("Selecting "+folder+" sub task...");
		System.out.println("Selecting sub "+folder+" task...");
	}
	
	public void clickSubTaskCheckbox(int rowNum, int colNum, String commonPath) throws Exception{
		
		String subTask = getCellData(rowNum, colNum);
		//String subfolder = getCellData(TestCaseRow, colNum2);
		
		clickByXpath(commonPath+"/../../td/span/span/span/input");	
		
		driver.manage().timeouts().implicitlyWait(250, TimeUnit.MILLISECONDS);
		log("Selecting "+subTask+" sub task...");
		System.out.println("Selecting "+subTask+" sub task...");
	}
	
	public void clickTaskStatus(String commonPath) throws Exception{
		
		clickByXpath(commonPath+"/../../td/span/a");
		log("Clicked task status...");
		System.out.println("Clicked task status...");
		
	}
	
	public void changeSubTaskStatus(int colNum, int colNum2, int colNum3) throws Exception{
		
		String folder = getCellData(TestCaseRow, colNum);
		String subfolder = getCellData(TestCaseRow, colNum2);
		
		//clickByXpath(commonPath+"/../../td/span/a");
			
		log("Clicked "+folder+" subtask status...");
		System.out.println("Clicked "+folder+" subtask status...");
		
		String status = getCellData(TestCaseRow, colNum3);
		clickByXpath("//div/label[text()='"+status+"']/../span/input");
		log("Clicked "+status+"...");
		System.out.println("Clicked "+status+"...");
		
		clickByXpath("//button[text()='Save']");
		Thread.sleep(5000);
		log("Clicked Saved button...");
		System.out.println("Clicked Saved button...");
		
		//clickByXpath("//button[@title='Save and Close']");
		//Thread.sleep(5000);
		//log("Clicked Saved and Closed button...");
		//System.out.println("Clicked Saved and Closed button...");
			
		try{
			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button/span[text()='Y']"))).click();
		} catch (Exception e){
			
		}
	}
	
	public void changeSubTaskStatus(int colNum, int colNum2, int colNum3, String commonPath) throws Exception{
		
		String folder = getCellData(TestCaseRow, colNum);
		String subfolder = getCellData(TestCaseRow, colNum2);
		System.out.println("entered new command...");
		
		clickByXpath(commonPath+"/../../td/span/a");
			
		log("Clicked "+folder+" subtask status...");
		System.out.println("Clicked "+folder+" subtask status...");
		
		String status = getCellData(TestCaseRow, colNum3);
		clickByXpath("//div/label[text()='"+status+"']/../span/input");
		log("Clicked "+status+"...");
		System.out.println("Clicked "+status+"...");
		
		clickByXpath("//button[@title='Save and Close']");
		Thread.sleep(5000);
		log("Clicked Saved and Closed button...");
		System.out.println("Clicked Saved and Closed button...");
		
		if(status.contentEquals("Not Started")){
			clickByXpath("//button[text()='es']");
			Thread.sleep(5000);
			log("Clicked Yes button...");
			System.out.println("Clicked Yes button...");
		}
		
		try{
			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button/span[text()='Y']"))).click();
		} catch (Exception e){
			
		}
	}
	
	public void changeSubTaskStatus(int rowNum, int colNum, int colNum2, int colNum3, String commonPath) throws Exception{
		
		String folder = getCellData(rowNum, colNum);
		String subfolder = getCellData(rowNum, colNum2);
		System.out.println("entered new command...");
		
		String prevStatus = driver.findElement(By.xpath(commonPath+"/../../td/span/a")).getText();
		
		clickByXpath(commonPath+"/../../td/span/a");
		Thread.sleep(1000);
		if(prevStatus.contentEquals("Not Started")){
			try{
					if(!driver.findElement(By.xpath("//button[text()='es']")).isDisplayed()){
					clickByXpath("//button[text()='es']");
					}
				}catch(Exception e){}
		}
			
		log("Clicked "+folder+" subtask status...");
		System.out.println("Clicked "+folder+" subtask status...");
		
		String status = getCellData(rowNum, colNum3);
		clickByXpath("//div/label[text()='"+status+"']/../span/input");
		log("Clicked "+status+"...");
		System.out.println("Clicked "+status+"...");
		
		clickByXpath("//button[@title='Save and Close']");
		Thread.sleep(5000);
		log("Clicked Saved and Closed button...");
		System.out.println("Clicked Saved and Closed button...");
		
		if(status.contentEquals("Not Started")){
			clickByXpath("//button[text()='es']");
			Thread.sleep(5000);
			log("Clicked Yes button...");
			System.out.println("Clicked Yes button...");
		}
		
		try{
			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button/span[text()='Y']"))).click();
		} catch (Exception e){
			
		}
	}
	
	public void clickExpandWorkForceDevelopment(){
		
		click("pt1:USma:0:MAnt1:1:pt1:r1:1:topAppPanel:applicationsTable1:_ATTp:table1:2::di");
		log("Clicking expand...");
		System.out.println("Clicking expand...");
	}
	
	public void clickDoneButton() throws InterruptedException{
		
		//clickByXpath("//button[text()='D']");
		TaskUtilities.jsFindThenClick("//button[text()='D']");
		log("Clicked Done button...");
		System.out.println("Clicked Done button...");
		Thread.sleep(2000);
	}
	
	public void clickCancelButton() throws InterruptedException{
		
		clickByXpath("//button/span[text()='C']");
		log("Clicked Cancel button...");
		System.out.println("Clicked Cancel button...");
		Thread.sleep(2000);
	}
	
	public void clickSaveAndOpenProjectButton() throws InterruptedException{
		
		clickByXpath("//span[text()='Save and Open Project']");
		log("Clicked Save and Open Project button...");
		System.out.println("Clicked Save and Open Project button...");
		Thread.sleep(5000);
	}
	
	public void enterSearchName(int colNum) throws Exception{
		String value = getCellData(TestCaseRow, colNum);
		enterText("pt1:USma:0:MAnt1:0:AP2:r3:0:qryId1:value10::content", value); 
		log("Entered value in the Name...");
		System.out.println("Entered value in the Name...");
	}
	
	public void clickGoToTaskIcon(){
		
		clickByXpath("//td/a/img[contains(@src,'gototask')]");
		log("Clicked Go to Task icon...");
		System.out.println("Clicked Go to Task icon...");
		
	}
	
	public void clickCreateIcon(){
		
		clickByXpath("//div/a/img[contains(@src,'new_ena')]");
		log("Clicked Create icon...");
		System.out.println("Clicked Create icon...");
	}

	public void enterRole(int colNum) throws Exception{
		String value = getCellData(TestCaseRow, colNum);
		enterText("pt1:USma:0:MAnt2:1:r2:0:dynamicRegion1:1:AP1:inputText1::content", value); //
		log("Entered value in the Role...");
		System.out.println("Entered value in the Role...");
	}
	
	public void enterDescription(int colNum) throws Exception{
		String value = getCellData(TestCaseRow, colNum);
		enterText("pt1:USma:0:MAnt2:1:r2:0:dynamicRegion1:1:AP1:inputText2::content", value); //
		log("Entered value in the Description...");
		System.out.println("Entered value in the Description...");
	}
	
	public void enterFromDate(String date) throws Exception{
		enterText("pt1:USma:0:MAnt2:1:r2:0:dynamicRegion1:1:AP1:inputDate1::content", date); //
		log("Entered value in the From Date...");
		System.out.println("Entered value in the From Date...");
	}
	
	public void enterToDate(String date) throws Exception{
		enterText("pt1:USma:0:MAnt2:1:r2:0:dynamicRegion1:1:AP1:inputDate2::content", date); //
		log("Entered value in the To Date...");
		System.out.println("Entered value in the To Date...");
	}
	
	public void selectStatus(int colNum) throws Exception{
		String value = getCellData(TestCaseRow, colNum);
		selectDropdownByVisibleText("pt1:USma:0:MAnt2:1:r2:0:dynamicRegion1:1:AP1:soc2::content", value); //
		log("Selected value in Status...");
		System.out.println("Selected value in Status...");
	}

	public void clickSaveAndCloseButton() throws InterruptedException{
		
		clickByXpath("//button/span[text()='S']");
		Thread.sleep(5000);
		log("Clicked Save and Close button...");
		System.out.println("Clicked Save and Close button...");
		
	}
	
	public void clickSaveButton() throws InterruptedException{
		
		clickByXpath("//button[text()='Save']");
		Thread.sleep(5000);
		log("Clicked Save button...");
		System.out.println("Clicked Save button...");
		
	}
	
	public void clickOkButton() throws InterruptedException{
		
		clickByXpath("//button[text()='OK']");
		Thread.sleep(5000);
		log("Clicked OK button...");
		System.out.println("Clicked OK button...");
		
	}

	public String[][] queryAllTaskMainFolder() throws Exception{
		
		java.util.List<String> mainTaskHolder = new ArrayList<String>();
		ArrayList<String> mainTask_afrrk = new ArrayList<String>();
		String[][] possibleMainTasks = {};
		String mainTask, afrrkAttr, afrapAttr;
		int index, entIndex, tasksIndex = 0;
		
		java.util.List<WebElement> queryFolder = driver.findElements(By.tagName("tr"));
		
		for(WebElement tasks:queryFolder){
			mainTask = "";
			afrrkAttr = tasks.getAttribute("_afrrk");
			afrapAttr = tasks.getAttribute("_afrap");
			String loopmain = "//tr[@_afrrk='"+afrrkAttr+"']";
			
			if(afrapAttr == null && afrrkAttr != null){
				TaskUtilities.retryingFindClick(By.xpath(loopmain));
				
				String textIn = tasks.getText();
				System.out.println("Now holding: '"+textIn+"'");
				index = textIn.indexOf("Yes");
				if(index == -1){
					index = textIn.indexOf("No");
				} 
				//Clean up InnerText Value.....
				if(!textIn.isEmpty()){
					System.out.println("Cleaning up entry.....");
					mainTask = textIn.substring(0,index).substring(2);
					entIndex = mainTask.lastIndexOf("\n");
					mainTask = mainTask.substring(0,entIndex);
					System.out.println("Filtered text:'"+mainTask+"'");
					mainTaskHolder.add(mainTask);
					mainTask_afrrk.add(afrrkAttr);
					System.out.println("Now holding: "+mainTaskHolder);
				}
			}
			
			tasksIndex += 1;				
			//Clean up combining results into one array...
			if(tasksIndex == queryFolder.size()){
				possibleMainTasks = new String[mainTaskHolder.size()][2];
				System.out.println("Max holder size: "+mainTaskHolder.size());
				
				int indexMain = 0;
				for(String mainT: mainTaskHolder){
					possibleMainTasks[indexMain][0] = mainT;
					possibleMainTasks[indexMain][1] = mainTask_afrrk.get(indexMain);
					indexMain += 1;
				}
			}
		}	
		
		return possibleMainTasks;
	}	
	
	public String[][] queryAllTaskMainFolder(String[][] mainTaskAttrHolder) throws Exception{
		
		java.util.List<String> mainTaskHolder = new ArrayList<String>();
		ArrayList<String> mainTask_afrrk = new ArrayList<String>();
		String[][] possibleMainTasks = {};
		String mainTask, afrrkAttr, afrapAttr;
		int index, entIndex;
		boolean hasBeenFound = false, extractData = false;
		
		java.util.List<WebElement> queryFolder = driver.findElements(By.tagName("tr"));
		
		for(WebElement tasks:queryFolder){
			mainTask = "";
			afrrkAttr = tasks.getAttribute("_afrrk");
			afrapAttr = tasks.getAttribute("_afrap");
			String loopmain = "//tr[@_afrrk='"+afrrkAttr+"']";
			
			if(afrapAttr == null && afrrkAttr != null){
				TaskUtilities.retryingFindClick(By.xpath(loopmain));
				
				String textIn = tasks.getText();
				System.out.println("Now holding: '"+textIn+"'");
				index = textIn.indexOf("Yes");
				if(index == -1){
					index = textIn.indexOf("No");
				} 
				//Clean up InnerText Value.....
				if(!textIn.isEmpty()){
					System.out.println("Cleaning up entry.....");
					mainTask = textIn.substring(0,index).substring(2);
					entIndex = mainTask.lastIndexOf("\n");
					mainTask = mainTask.substring(0,entIndex);
					System.out.println("Filtered text:'"+mainTask+"'");
					mainTaskHolder.add(mainTask);
					mainTask_afrrk.add(afrrkAttr);
					System.out.println("Now holding: "+mainTaskHolder);
					
					if(hasBeenFound){
							extractData = true;
						}
						else if(mainTaskAttrHolder != null && mainTaskAttrHolder[mainTaskAttrHolder.length-1][0].contentEquals(mainTask)){
							System.out.println("Similar values has been found");	
							hasBeenFound = true;
						}
				}
			}
			
			//System.out.println(possibleMainTasks.toString());
			if(mainTaskAttrHolder == null && mainTaskHolder.size()>0){
					possibleMainTasks = new String[mainTaskHolder.size()][2];
					System.out.println("Max holder size: "+mainTaskHolder.size());
					
					int indexMain = 0;
					for(String mainT: mainTaskHolder){
						possibleMainTasks[indexMain][0] = mainT;
						possibleMainTasks[indexMain][1] = mainTask_afrrk.get(indexMain);
						indexMain += 1;
					}
					
					System.out.println("Now exiting the function.....");
					return possibleMainTasks;
				}else if(extractData && mainTaskHolder.size()>0){
					
					possibleMainTasks = new String[mainTaskHolder.size()][2];
					System.out.println("Max alternate holder size: "+mainTaskHolder.size());
					
					int indexMain = 0;
					for(String mainT: mainTaskHolder){
						possibleMainTasks[indexMain][0] = mainT;
						System.out.println("Now adding mainT....."+mainT);
						possibleMainTasks[indexMain][1] = mainTask_afrrk.get(indexMain);
						indexMain += 1;
					}
					
					System.out.println("Now exiting the alternative function.....");
					return possibleMainTasks;
				}
			
		}
		return possibleMainTasks;
	}	
	
	public java.util.List<String> queryAllTableInputs() throws Exception{
		java.util.List<WebElement> queryFolder = new ArrayList<WebElement>();//driver.findElements(By.tagName("tr"));
		List<String> tableRowInputs = new ArrayList<String>();
		String afrrkAttr;
		int attempts = 0;
		
		//Clean up entry.....
		System.out.println("Atempting to obtain all table inputs.....");
		while(attempts < 5){
			try{
					queryFolder = driver.findElements(By.tagName("tr"));
					
					for(WebElement entry: queryFolder){
						afrrkAttr = entry.getAttribute("_afrrk");
						if(afrrkAttr != null){
							System.out.println("Now adding....."+entry.getText());
							tableRowInputs.add(afrrkAttr);
						}
					}		
					
					System.out.println("Queried values....."+tableRowInputs);
					return tableRowInputs;
					
				} catch(StaleElementReferenceException e){
					//Ignores StaleElementReference.....
					System.out.println("Attempt failed.....\n**********");
				}
			attempts += 1;
		}
		
		log("Failed to scan all table input. Throwing error...");
		throw new StaleElementReferenceException("Failed to scan all table inputs....");
	}
	
	public String[][] queryAllsubtaskFolder(String afrrk_main) throws Exception{
		System.out.println("Entered the sub task function....."+afrrk_main);
		
		java.util.List<String> subTaskHolder = new ArrayList<String>();
		ArrayList<String> subTask_afrrk = new ArrayList<String>();
		ArrayList<String> subTask_afrap = new ArrayList<String>();
		String[][] possiblesubTasks = {};
		String subTask, afrrkAttr, afrapAttr, subPath;
		int index, entIndex, tasksIndex = 0;
		int attempts = 0;
		
		java.util.List<WebElement> queryFolder = new ArrayList<WebElement>();
		
		while(attempts < 5){
			try{
				System.out.println("Attempting to filter all subtask folders.....");
				queryFolder = driver.findElements(By.xpath("//tr[contains(@_afrap,'"+afrrk_main+"')]"));
				
						for(WebElement subs:queryFolder){
							subTask = "";
							afrrkAttr = (String)subs.getAttribute("_afrrk");
							afrapAttr = (String)subs.getAttribute("_afrap");
							subPath = "//tr[@_afrrk='"+afrrkAttr+"' and @_afrap='"+afrapAttr+"']";
							System.out.println(afrrkAttr+":"+afrapAttr+" vs."+afrrk_main);
							
							//retryingFindClick(By.xpath(subPath));
							scrollElementIntoView(subPath);
							waitForElementToBeInvisible("//div[text()='Fetching Data...']", "Fetching Data...", 10);
							//retryingFindClick(By.xpath(subPath));
							
							if(afrapAttr != null)
							System.out.println(""+((String)afrapAttr).equals((String)afrrk_main)+((String)afrapAttr).contentEquals((String)afrrk_main)+afrapAttr.contains(afrrk_main));
							
							//afrapAttr
							if(afrapAttr != null && (afrapAttr.contains(afrrk_main) || afrapAttr.contains(afrrk_main+"_"))){ // && afrapAttr.contentEquals(afrap_) 
								String textIn = subs.getText();
								System.out.println("Raw data: '"+textIn+"'");
								index = textIn.indexOf("In Progress");
								if(index == -1){
									index = textIn.indexOf("Implemented");
									if(index == -1){
										index = textIn.indexOf("Not Started");
									}
									if(index == -1){
										index = textIn.lastIndexOf("\n");
									}
								} 
								//Clean up InnerText Value.....
								if(!textIn.isEmpty()){
									System.out.println("Cleaning up entry.....");
									subTask = textIn.substring(0,index).substring(2);
									entIndex = subTask.lastIndexOf("\n");
									if(entIndex != -1) subTask = subTask.substring(0,entIndex);
									System.out.println("Filtered text:'"+subTask+"'");
									subTaskHolder.add(subTask);
									subTask_afrrk.add(afrrkAttr);
									subTask_afrap.add(afrapAttr);
									System.out.println("Now holding: "+subTaskHolder);
								}
							}
							
							tasksIndex += 1;				
							//Clean up combining results into one array...
							if(tasksIndex == queryFolder.size()){
								possiblesubTasks = new String[subTaskHolder.size()][3];
								System.out.println("Max holder size: "+subTaskHolder.size());
								
								int indexMain = 0;
								for(String mainT: subTaskHolder){
									possiblesubTasks[indexMain][0] = mainT;
									possiblesubTasks[indexMain][1] = subTask_afrrk.get(indexMain);
									possiblesubTasks[indexMain][2] = subTask_afrap.get(indexMain);
									indexMain += 1;
								}
							}
						}
						
						System.out.println(possiblesubTasks.toString());
						return possiblesubTasks;
				} catch(StaleElementReferenceException e){
					System.out.println("Failed to execute the method....\n**********");
				}
			attempts += 1;
		}
		
		log("Failed to locate all visible subtask folders. Throwing error...");
		throw new StaleElementReferenceException("Failed to carry out the execution....");
	}
	
	public String[] queryAlltaskstatusFolder() throws Exception{
		
		String[] possibleStatus = {};
		java.util.List<String> statusHolder = new ArrayList<String>();
		java.util.List<WebElement> queryFolder = driver.findElements(By.xpath("//div/label"));
		
		for(WebElement status: queryFolder){
			String textIn = status.getText();
			System.out.println("Raw Data: "+textIn);
			if(textIn != null){
				statusHolder.add(textIn);
			}
			System.out.println("Now Holding: "+statusHolder);
			
			possibleStatus = new String[statusHolder.size()];
			
			int i = 0;
			for(String stats: statusHolder){
				possibleStatus[i] = stats;
				i += 1;
			}
		}
		
		return possibleStatus;
	}
	
	public java.util.List<String> tickExpandVisibleTasks(int MAX_TIME_OUT) throws Exception{
		int attempts = 0, i = 0;
		boolean expanded = true;
		String comPath, afrap ="", afrrk="";
		java.util.List<String> taskfolder = new ArrayList<String>();;

		System.out.println("Checking all visible folder");
		while(expanded){
			expanded = false;
			//Expands Expandable Sub Tasks...
			System.out.println("Querying all visible folder");
			java.util.List<WebElement> queryFolder = driver.findElements(By.xpath("//table[contains(@summary, 'Offerings')]/tbody/tr"));
			System.out.println("Querying folder size: "+queryFolder.size());
			
			//for(WebElement elem: queryFolder){
			while(attempts < 3){
			try{
					while(i < queryFolder.size()){
							WebElement elem = queryFolder.get(i);
							System.out.println("the query starts here...");
							System.out.println("Current attempts value: "+attempts);
							
									System.out.println("Now Holding: "+elem.getText());
									afrrk = (String)elem.getAttribute("_afrrk");
									afrap = (String)elem.getAttribute("_afrap");
									System.out.println("afrrk : afrap "+afrrk +":"+afrap);
			
							System.out.println("Setting common path...");
							if(afrap == null || afrap.isEmpty()){
									comPath = "//tr[@_afrrk='"+afrrk+"']";
									TaskUtilities.retryingFindClick(By.xpath(comPath));
									System.out.println("new comPath: "+comPath);
								} else {
									System.out.println("afrrk : afrap "+afrrk +":"+afrap);
									comPath = "//tr[@_afrrk='"+afrrk+"' and @_afrap='"+afrap+"']";
									System.out.println("new comPath: "+comPath);
									TaskUtilities.retryingFindClick(By.xpath(comPath));
								}
							
							TaskUtilities.jsScrollIntoView(comPath);
							taskfolder.add(comPath);
							//Expands if expandable...
							System.out.println("Expanding folder...");
							if(TaskUtilities.is_element_visible(comPath+"//a[@title='Expand']", "xpath")){
								System.out.println("Expanding all visible expandable folder");
								TaskUtilities.retryingFindClick(By.xpath(comPath+"//a"));
								TaskUtilities.customWaitForElementVisibility(comPath+"//a[@title='Collapse']", MAX_TIME_OUT);
								java.util.List<WebElement> subfolder = driver.findElements(By.xpath("//tr[@_afrrk='"+afrrk+"' and contains(@_afrap,'"+afrap+"_')]"));
								
								String  sub_afrrk ="", sub_afrap ="", subcomPath ="";
								for(WebElement sub : subfolder){
									System.out.println("Now Holding: "+sub.getText());
									sub_afrrk = (String)sub.getAttribute("_afrrk");
									sub_afrap = (String)sub.getAttribute("_afrap");
									System.out.println("afrrk : afrap "+sub_afrrk +":"+sub_afrap);
			
									System.out.println("Setting common path...");
									if(sub_afrap == null || sub_afrap.isEmpty()){
										subcomPath = "//tr[@_afrrk='"+sub_afrrk+"']";
											TaskUtilities.retryingFindClick(By.xpath(subcomPath));
											System.out.println("new comPath: "+subcomPath);
										} else {
											System.out.println("afrrk : afrap "+sub_afrrk +":"+sub_afrap);
											subcomPath = "//tr[@_afrrk='"+sub_afrrk+"' and @_afrap='"+sub_afrap+"']";
											System.out.println("new comPath: "+subcomPath);
											TaskUtilities.retryingFindClick(By.xpath(subcomPath));
										}
									taskfolder.add(subcomPath);
								}
								
								expanded = true;
							}
							i += 1;
							System.out.println("Current attempts value: "+attempts);
						}
						attempts = 4;
					} catch(StaleElementReferenceException e){
						System.out.print("error occured attempting to re-query tasks...");
						attempts += 1;
					}
			}
				attempts = 0;
				if(expanded) TaskUtilities.scrollDownToElement(true, "small");
				System.out.println("looping over...");
			}
		System.out.println("taskfolder values: "+taskfolder);
		return taskfolder;
	}
}