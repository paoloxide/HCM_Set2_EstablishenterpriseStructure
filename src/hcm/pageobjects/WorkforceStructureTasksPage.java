package hcm.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.NoSuchElementException;

import org.openqa.selenium.TimeoutException;

import common.BasePage;
import common.TaskUtilities;
import static common.BaseTest.TestCaseRow;
import static util.ReportLogger.log;

public class WorkforceStructureTasksPage extends BasePage {
	
	public WorkforceStructureTasksPage(WebDriver driver){
		super(driver);
	}
	
	@Override
    public String getPageId()
    {
        return "/hcmCoreSetup/faces/HcmWSIntWA";
    }
	
	public void clickTask(String menu){
		
		//clickByXpath("//li/a[contains(text(),'"+ menu +"')]");		
		TaskUtilities.jsFindThenClick("//li/a[contains(text(),'"+ menu +"')]");
		log("Clicking " + menu +"...");
		System.out.println("Clicking " + menu +"...");

	}
	
	public void clickCreate() throws InterruptedException{
		//driver.findElement(By.xpath("//a/span[text()=' Create']/../../../td/div/a")).click();;
		//driver.findElement(By.xpath("//td[text()='Create']")).click();;
		clickByXpath("//a/span[text()=' Create']/../../../td/div/a");  
		Thread.sleep(2000);
		clickByXpath("//td[text()='Create']");
		log("Clicking Create...");
		System.out.println("Clicking Create...");

	}
	
	public void clickNext(){
		
		//clickByXpath("//a/span[text()='Ne']");
		//driver.findElement(By.xpath("//a/span[text()='Ne']")).click();
		TaskUtilities.jsFindThenClick("//a/span[text()='Ne']");
		log("Clicking Next...");
		System.out.println("Clicking Next...");

	}
	
	public void clickOKButton(){
		
		//clickByXpath("//button[text()='O']");
		//driver.findElement(By.xpath("//button[text()='O']")).click();
		TaskUtilities.jsFindThenClick("//button[text()='O']");
		log("Clicking OK...");
		System.out.println("Clicking OK...");

	}
	
	public void clickYesButton(){
		
		//clickByXpath("//button[text()='es']");
		//driver.findElement(By.xpath("//button[text()='es']")).click();
		TaskUtilities.jsFindThenClick("//button[text()='es']");
		log("Clicking Yes...");
		System.out.println("Clicking Yes..."); 

	}
	
	public void clickSubmitButton(){
		
		//clickByXpath("//a/span[text()='Submit']");
		//driver.findElement(By.xpath("//a/span[text()='Submit']")).click();
		TaskUtilities.jsFindThenClick("//a/span[text()='Submit']");
		log("Clicking Submit...");
		System.out.println("Clicking Submit...");

	}
	
	public void clickSaveandCloseButton() throws InterruptedException{
		
		clickByXpath("//span[text()='Save']/../../../td/div/a");	
		Thread.sleep(3000);
		clickByXpath("//td[text()='ave and Close']");
		Thread.sleep(3000);
		log("Clicking Submit...");
		System.out.println("Clicking Save and Close button...");

	}
	
	public void clickSearchButton(){
		
		//clickByXpath("//button[text()='Search']");
		//driver.findElement(By.xpath("//button[text()='Search']")).click();
		TaskUtilities.jsFindThenClick("//button[text()='Search']");
		log("Clicking Search...");
		System.out.println("Clicking Search...");

	}
	
	public void enterData(String dataLocator, String value) throws Exception{
		
		String inputBoxPath = "//td/label[text()='"+dataLocator+"']/../../td/input";
		clickByXpath(inputBoxPath);
		enterTextByXpath(inputBoxPath, "\b\b\b\b\b\b\b\b"+value);
		log("Entered "+dataLocator+"..");
		System.out.println("Entered "+dataLocator+"...");
	}
	
	public void enterDropdownData(String dataLocator, String value) throws Exception{
		
		String inputBoxPath = "//td/label[text()='"+dataLocator+"']/../../td/span/input";
		clickByXpath(inputBoxPath);
		enterTextByXpath(inputBoxPath, value);
		log("Entered "+dataLocator+"..");
		System.out.println("Entered "+dataLocator+"...");
		
	}
	
	public void enterDropdownLocation(String dataLocator, String value) throws Exception{
		
		String inputBoxPath = "//td/label[text()='"+dataLocator+"']/../../td/span/span/input";
		clickByXpath(inputBoxPath);
		enterTextByXpath(inputBoxPath, value);
		log("Entered "+dataLocator+"..");
		System.out.println("Entered "+dataLocator+"...");
		
	}
	
	public void selectFromDropdown(String dataLocator, String value) throws Exception{
		
		String inputBoxPath = "//td/label[text()='"+dataLocator+"']/../../td/select";
		clickByXpath(inputBoxPath);
		clickByXpath(inputBoxPath+"/option['"+value+"']");
		log("Selected "+dataLocator+"..");
		System.out.println("Selected "+dataLocator+"...");
		
	}
	
	public void enterSearchData(String dataLocator, String value) throws Exception{
		
		String inputBoxPath = "//td/label[text()='"+dataLocator+"']/../../td/table/tbody/tr/td/table/tbody/tr/td/span/input";
		clickByXpath(inputBoxPath);
		enterTextByXpath(inputBoxPath, "\b\b\b\b\b\b\b"+value);
		log("Entered "+dataLocator+"..");
		System.out.println("Entered "+dataLocator+"...");
	}
	
	public void waitForElementToBeClickable(int waitTime, String elementPath) throws Exception{

		WebDriverWait wait = new WebDriverWait(driver, waitTime);
		try{
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(elementPath)));
			System.out.println("Check element presence is now finised...");
			
		}catch(TimeoutException e){
			System.out.println("Waiting for element has timed out... No alternative method available.");
		}
	}
	
	public boolean jsScrollDown(boolean isScrollingDown){
		
		int scrollValue = 400;
		boolean scrollDownAgain;
		
		JavascriptExecutor js = (JavascriptExecutor)driver;
		scrollDownAgain = (boolean)js.executeScript(
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
						"	if((curFolderId1 === 'auto' && curFolderId2 === 'absolute') || curFolderId.contains('scroller')){"+
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
		return scrollDownAgain;
	}
	
	public String jsGetInputValue(String dataPath){
		
		String inputValue = null;
		
		JavascriptExecutor js = (JavascriptExecutor)driver;
		inputValue = (String)js.executeScript(
			"function getElementByXPath(xPath){"+
					"	return document.evaluate(xPath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"+
					"}"+
			"input = getElementByXPath(\""+dataPath+"\").value;"+
			"return input;"
		);
		
		return inputValue;
		
	}
	public void retryingFindClick(By by) throws Exception{

        int attempts = 0;
        
        while(attempts < 11) {
            try {
            	System.out.println("Validating element.....retries:"+attempts);
                driver.findElement(by).click();
                System.out.println("Element has been refreshed.....");
                return ;
            } catch(StaleElementReferenceException e) {
            
            } catch(NoSuchElementException e){
            
            } catch(ElementNotVisibleException e){
            
            }
            attempts++;
        }
        
        System.out.println("Throwing Error.....");
        //throw new StaleElementReferenceException("The Element cannot be found...");
	}
		
	public String[] findAllAvailableLabels() throws Exception{

		int i = 0;
		java.util.List<WebElement> queryFolder = 
				driver.findElements(By.xpath("//label[contains(@for,'content')]"));
		
		for(WebElement element: queryFolder){
			if(!element.getText().equals("")) i += 1;
		}
		
		String[] visibleLabelHolder = new String[i]; i = 0;	
		for(WebElement element: queryFolder){
			if(!element.getText().equals("")){
				System.out.println("Adding: "+element.getText());
				visibleLabelHolder[i] = element.getText();
				i += 1;
			}
		}
		
		return visibleLabelHolder;
	}
	public boolean scanLabelPresence(String[] labelHolder, String labelName) throws Exception{
		
		for(int i =0; i<labelHolder.length; i++){
				System.out.println("Now checking..."+labelHolder[i]);
				if(labelHolder[i].contentEquals(labelName)){
						System.out.println("Match has been found...");
						return true;
					}
			}
		System.out.println("No matches found...");
		return false;
	}
	public String filterDataLocator(String dataLocator) throws Exception{

		dataLocator = dataLocator.replaceAll("\\*", "");
		System.out.println("Now holding: "+dataLocator);
		
		//Second Level Filtering:
		if(dataLocator.indexOf("-") != -1){
			int dashIndex   = dataLocator.indexOf("-");
				dataLocator = dataLocator.substring(dashIndex+2);
		}
		
		return dataLocator;
		
	}
}