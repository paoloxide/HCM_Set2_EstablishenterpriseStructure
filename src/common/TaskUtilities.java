package common;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.text.html.parser.TagElement;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import bsh.StringUtil;
import common.DuplicateEntryException;
import hcm.exception.InvalidLegalAddressException;
import hcm.exception.InvalidLocationException;
import common.BaseTest;
import static util.ReportLogger.log;

/**
 * This class consolidate all commonly used methods
 * 
 * 
 * @author jerrick.m.falogme
 */

public class TaskUtilities{
	
	protected static WebDriver driver = BaseTest.driver;
	
	public static boolean is_element_visible(String locator, String locType) {
		try {

			if (locType.equalsIgnoreCase("id")) {
				driver.findElement(By.id(locator)).isDisplayed();

			} else if (locType.equalsIgnoreCase("name")) {
				driver.findElement(By.name(locator)).isDisplayed();

			} else {
				driver.findElement(By.xpath(locator)).isDisplayed();
			}
			return true;
		}

		catch (NoSuchElementException e) {
			return false;
		}

	}
	
	public static void clickNavLink(By by){

		WebDriverWait wait = new WebDriverWait(driver, 30L);

		wait.until(ExpectedConditions.presenceOfElementLocated(by));
		driver.findElement(by).click();
		System.out.println("Career Development" + " link has been clicked...");
		log("Career Development" + " link has been clicked...");
	}
	
	public static boolean scrollDownToElement(boolean isScrollingDown, String scrollType) throws Exception{
		
		System.out.println("Initializing scroll down....");
		int scrollValue;
		
		switch(scrollType){
			case "small":
				scrollValue = 50;
				break;
			case "normal":
				scrollValue = 150;
				break;
			case "big":
				scrollValue = 400;
				break;
			default:
				scrollValue = 150;
		}
		
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
			"	if((curFolderId1 === 'auto' && curFolderId2 === 'absolute') || curFolderId.contains('scroller')){"+
			"		taskFolderInt += 1;	"+
			"		taskFolderArray[taskFolderInt] = [curFolderId, curFolderId1, curFolderId2];"+
			"}}"+
      
			"for(var j =0; j<taskFolderArray.length;j++){"+
			"	  newScroller = document.getElementById(taskFolderArray[j][0]);"+
			"	  if(newScroller.scrollTop != undefined && newScroller != 'null'){"+
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
		//SLOW INTERNET CONNECTION might REQUIRE -- Higher Wait time: Recommended(5*2)
		driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
		fluentWaitForElementInvisibility("//div[text()='Fetching Data...']", "Fetching Data...", 10);
		
		return scrollDownAgain;
	}
	
	public static void fluentWaitForElementInvisibility(String xPath, String textValue, int waitTime) throws Exception{
		
		Thread.sleep(250); //Momentary pause.....
		Wait<WebDriver> waitLoadHandler = new FluentWait<WebDriver>(driver)
				.withTimeout(waitTime, TimeUnit.SECONDS)
				.pollingEvery(500, TimeUnit.MILLISECONDS)
				.ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class);
		
		waitLoadHandler.until(ExpectedConditions.invisibilityOfElementWithText(By.xpath(xPath), textValue));
		System.out.println("Page loading has been finished.....");
		log("Page loading has been finished.....");
	}
	
	public static void customWaitForElementVisibility(String elemPath, int waitTime, CustomRunnable runner) throws Exception{
		
		long startTime = System.currentTimeMillis();
		waitTime = waitTime * 1000;
		while(!is_element_visible(elemPath, "xpath")){
			try{
					runner.customRun();
				} catch(StaleElementReferenceException e){
					//Skips...
				}
			
			if(System.currentTimeMillis() - startTime > waitTime){
				log(waitTime + " second/s has elapsed after waiting for: "+elemPath+"\nNow throwing error.....\n");
				throw new TimeoutException(waitTime/1000 + " second/s has elapsed after waiting for: "+elemPath);
			}
			
		}		
		System.out.println("Element is now visible after "+(System.currentTimeMillis() - startTime)/1000+" second/s.....");
		log("Element is now visible after "+(System.currentTimeMillis() - startTime)/1000+" second/s.....");
	}
	
	public static void customWaitForElementVisibility(String elemPath, int waitTime) throws Exception{
		
		long startTime = System.currentTimeMillis();
		waitTime = waitTime * 1000;
		
		while(!is_element_visible(elemPath, "xpath")){
			//Just wait here...
			if(System.currentTimeMillis() - startTime > waitTime){
				log(waitTime/1000 + " second/s has elapsed after waiting for: "+elemPath+"\nNow throwing error.....\n");
				throw new TimeoutException(waitTime/1000 + " second/s has elapsed after waiting for: "+elemPath);
			}
			
		}
		Thread.sleep(250);
		System.out.println("Element is now visible after "+(System.currentTimeMillis() - startTime)/1000+" second/s.....");
		log("Element is now visible after "+(System.currentTimeMillis() - startTime)/1000+" second/s.....");
	}

	public static void customWaitForElementEnablement(By by, int waitTime) throws Exception{
		
		long startTime = System.currentTimeMillis();
		waitTime = waitTime * 1000;
		int tries = 0;
		
		attemptloop:
		while(tries < 5){
			try{
					while(Boolean.parseBoolean(driver.findElement(by).getAttribute("disabled"))){
						//Just wait here...
						if(System.currentTimeMillis() - startTime > waitTime){
							log(waitTime/1000 + " second/s has elapsed after waiting for: "+by+"\nNow throwing error.....\n");
							throw new TimeoutException(waitTime/1000 + " second/s has elapsed after waiting for: "+by);
						}
					}
					
					break attemptloop;
					
					} catch(StaleElementReferenceException e){
						//Skips..
					}
			tries += 1;
		}
			
		Thread.sleep(250);
		System.out.println("Element is now visible after "+(System.currentTimeMillis() - startTime)/1000+" second/s.....");
		log("Element is now visible after "+(System.currentTimeMillis() - startTime)/1000+" second/s.....");
	}
	
	public static void customWaitForElementEnablement(By by, int waitTime, CustomRunnable runner) throws Exception{
		
		long startTime = System.currentTimeMillis();
		waitTime = waitTime * 1000;
		
		while(Boolean.parseBoolean(driver.findElement(by).getAttribute("disabled"))){
			try{
					runner.customRun();
				} catch(StaleElementReferenceException e){
					//Skips...
				}
			if(System.currentTimeMillis() - startTime > waitTime){
					log(waitTime/1000 + " second/s has elapsed after waiting for: "+by+"\nNow throwing error.....\n");
					throw new TimeoutException(waitTime/1000 + " second/s has elapsed after waiting for: "+by);
				}
			
		}
		Thread.sleep(250);
		System.out.println("Element is now visible after "+(System.currentTimeMillis() - startTime)/1000+" second/s.....");
		log("Element is now visible after "+(System.currentTimeMillis() - startTime)/1000+" second/s.....");
	}

	public static void timedLoop(int waitTime, BooleanCustomRunnable booleanRunner) throws Exception{
		boolean condition = true;
		long startTime = System.currentTimeMillis();
		waitTime = waitTime * 1000;
		
		while(condition){
			try{
					condition = booleanRunner.customRun();
				} catch(StaleElementReferenceException e){
					condition = true; //Then skips...
				}
			if(System.currentTimeMillis() - startTime > waitTime){
					log(waitTime/1000 + " second/s has elapsed since the loop started...\nNow throwing error.....\n");
					throw new TimeoutException(waitTime/1000 + " second/s has elapsed since the loop started...");
				}
			
		}
		Thread.sleep(250);
		System.out.println("The loop has ended after "+(System.currentTimeMillis() - startTime)/1000+" second/s.....");
		log("The loop has ended after "+(System.currentTimeMillis() - startTime)/1000+" second/s.....");
	}
	
	/**
	 * A 3-in-1 customized method
	 * that finds, clicks, clears
	 * and inputs data on a given inputbox. 
	 * 
	 * @author jerrick.m.falogme
	 */
	public static void consolidatedInputEncoder(BasePage bpInstance, String labelLocatorPath, String dataLocator) throws Exception{
		int attempts = 0;
		while(attempts < 3){
		
			try{ 
					jsScrollIntoView(labelLocatorPath);
					retryingFindClick(By.xpath(labelLocatorPath));
					//takeScreenshot();
					driver.findElement(By.xpath(labelLocatorPath)).clear();
					bpInstance.enterTextByXpath(labelLocatorPath, dataLocator);
					driver.findElement(By.xpath(labelLocatorPath)).sendKeys(Keys.TAB);
					return;
				} catch(StaleElementReferenceException e){
					//Skips...
				}
			attempts += 1;
		}
		   
        System.out.println("Failed to input dataLocator: "+dataLocator+ "to path: "+labelLocatorPath);
        System.out.println("Throwing Error.....");
        log("Failed to input dataLocator: "+dataLocator+ "to path: "+labelLocatorPath);
        log("Throwing Error.....");
        throw new StaleElementReferenceException("The input is no longer in the DOM\n");
		
	}
	
	public static void retryingInputEncoder(BasePage bpInstance, String labelLocatorPath, String dataLocator) throws Exception{
		int attempts = 0;
		while(attempts < 10){
			try{
					consolidatedInputEncoder(bpInstance, labelLocatorPath, dataLocator);
					return;
				} catch(Exception e){
					
				}
			attempts += 1;
			Thread.sleep(750);
		}
		
		throw new StaleElementReferenceException("Unable to input data...");
	}
	
	public static void consolidatedStrictInputEncoder(final BasePage bpInstance, final String labelLocatorPath, final String dataLocator) throws Exception{
		
		timedLoop(15, new BooleanCustomRunnable() {
			
			@Override
			public boolean customRun() throws Exception {
				// TODO Auto-generated method stub
				jsScrollIntoView(labelLocatorPath);
				retryingFindClick(By.xpath(labelLocatorPath));
				//takeScreenshot();
				try{
					driver.findElement(By.xpath(labelLocatorPath)).clear();
				} catch(StaleElementReferenceException e){
					//Skips clearance...
				}
				bpInstance.enterTextByXpath(labelLocatorPath, dataLocator);
				Thread.sleep(100);
				
				return !driver.findElement(By.xpath(labelLocatorPath))
						.getAttribute("value").contentEquals(dataLocator);
			}
		});
		System.out.println("Input has been successfully encoded...");
	}
	
	/**
	 * A 3-in-1 customized method
	 * that finds, clicks
	 * and selects option on a given select box. 
	 * 
	 * @author jerrick.m.falogme
	 */
	public static void consolidatedInputSelector(final String labelLocatorPath, final String dataLocator) throws Exception{
		
		retryingWrapper(new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				
				jsScrollIntoView(labelLocatorPath);
				//String dataLocatorPath = labelLocatorPath+"/option[text()='"+dataLocator+"']";
				String dataLocatorPath = labelLocatorPath+"/option[@title='"+dataLocator+"']";
				retryingFindClick(By.xpath(labelLocatorPath));
				if(!dataLocator.isEmpty() && !dataLocator.contentEquals("")){
						customWaitForElementVisibility(dataLocatorPath, 30);
						retryingFindClick(By.xpath(dataLocatorPath));
					} else{
						//Skips the sequence...
					}
				driver.findElement(By.xpath(labelLocatorPath)).sendKeys(Keys.TAB);
			}
		});
	}

	public static void consolidatedInputSearcherAndSelector(BasePage task, String labelLocator, String dataLocator, String parentLinkPath, StringCustomRunnable searchLabels, CustomRunnable okButtons) throws Exception{
		//TAGS: //div[contains(@id,'InternalTable')]
		
		String SSlabelLocator="", SSlabelLocatorPath="";
		final int MAX_TIME_OUT = 10;
		
		labelLocator = filterDataLocator(labelLocator);
		String labelLocatorPath = retryingSearchInput(labelLocator);
		final String labelLocatorPath_final = labelLocatorPath;
		
		//Skips the search...
		if(dataLocator.isEmpty() || dataLocator.contentEquals("")) return;
		
		jsScrollIntoView("//a[contains(@title,'"+labelLocator+"')]"+parentLinkPath);
		jsFindThenClick("//a[contains(@title,'"+labelLocator+"')]"+parentLinkPath);
		
		
		//Select label sets...
		SSlabelLocator = searchLabels.customRun();
		String rootPath = SSlabelLocator.substring(SSlabelLocator.indexOf(";")+1);
		SSlabelLocator = SSlabelLocator.substring(0, SSlabelLocator.indexOf(";"));
		System.out.println("SSLabelLocator is now: "+SSlabelLocator);
		
		retryingWrapper(new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				Thread.sleep(2250);
				jsFindThenClick("//a[text()='Search...']");
			}
		});
		
		//SSlabelLocatorPath = retryingSearchInput(SSlabelLocator);
		SSlabelLocatorPath = retryingSearchfromDupInput(SSlabelLocator, rootPath);
		final String SSlabelLocatorPath_final = SSlabelLocatorPath;
		
		retryingWrapper(new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				Thread.sleep(2000);
				driver.findElement(By.xpath(SSlabelLocatorPath_final)).click();
			}
		});
		retryingInputEncoder(task, SSlabelLocatorPath, dataLocator);
		driver.findElement(By.xpath(SSlabelLocatorPath)).sendKeys(Keys.ENTER);
				
		customWaitForElementVisibility("//div[contains(@id,'InternalTable')]//td[text()='"+dataLocator+"']", MAX_TIME_OUT);
		jsFindThenClick("//div[contains(@id,'InternalTable')]//td[text()='"+dataLocator+"']");
		//jsFindThenClick("//tbody//td[text()='"+dataLocator+"']");
		//Click appropriate OK from the set...
		okButtons.customRun();
		
		retryingWrapper(new CustomRunnable() {
			
			@Override
			public void customRun() throws Exception {
				// TODO Auto-generated method stub
				Thread.sleep(2250);
				driver.findElement(By.xpath(labelLocatorPath_final)).click();
			}
		});
	}
	
	//Attempt type functions....
	public static String retryingSearchInput(String dataLocator) throws Exception{

        int attempts = 0;
        int labelAttempts = 0;
        
        String[] labelStructArray = {
        		"//td//label[text() ='"+dataLocator+"']"
        		//"//td/span/label[text()='"+dataLocator+"']",
        };
        
        String[] inputTypesArray = {
        		"/../input",
        		"/../../td/input",
        		"/../../td/span/input",
        		"/../../td/span/span/input",
        		"/../../td/select",
        		"/../../td/table/tbody/tr/td/table/tbody/tr/td/span/select",
        		"/../../td/table/tbody/tr/td/input",
        		"/../../td/table/tbody/tr/td/table/tbody/tr/td/span/input",
        		"/../..//td/textarea",
        		"/../..//input",
        		"/..//input",
        		"/..//select",
        };
        
        if(dataLocator.isEmpty() || dataLocator == null){
	        	System.out.println("The dataLocator: "+dataLocator+" cannot be a label.");
	        	return null;
	        }
	    
		System.out.println("Validating if dataLocator: "+dataLocator+" is visible.");
        labelloop:
        while(labelAttempts < labelStructArray.length){
	        try{
		        	customWaitForElementVisibility(labelStructArray[labelAttempts], 10);
		        	break labelloop;
		        } catch(TimeoutException e){
		        	if(labelAttempts >= labelStructArray.length){
		        		
		            	System.out.println("The dataLocator: "+dataLocator+" cannot be a label.");
		            	return null;
		        	}
		        	labelAttempts += 1;
		        }
        }
        
        System.out.println("Attempting to find known valid path for dataLocator: "+dataLocator);
        while(attempts < inputTypesArray.length) {
            try {
            		String compoundPath = labelStructArray[labelAttempts]+inputTypesArray[attempts];
            		TaskUtilities.jsScrollIntoView(compoundPath);
	                driver.findElement(By.xpath(compoundPath)).click();
	                System.out.println("Valid Input path has been found"+" after "+attempts+" tries...");
	                System.out.println("Assigned path: \n"+compoundPath);
	                return compoundPath;
	                
	            } catch(Exception e){
	            	
	            }
            attempts++;
        }
        System.out.println("No valid path can be assigned after "+attempts+" tries...");
        return null;
	}
	
	public static String retryingSearchInputwithAttr(String dataLocator, String attributes) throws Exception{

        int attempts = 0;
        int labelAttempts = 0;
        
        String[] labelStructArray = {
        		"//td//label[text() ='"+dataLocator+"']",
        };
        
        String[] inputTypesArray = {
        		"/../input",
        		"/../../td/input",
        		"/../../td/span/input",
        		"/../../td/span/span/input",
        		"/../../td/select",
        		"/../../td/table/tbody/tr/td/table/tbody/tr/td/span/select",
        		"/../../td/table/tbody/tr/td/input",
        		"/../../td/table/tbody/tr/td/table/tbody/tr/td/span/input",
        		"/../..//td/textarea",
        		"/../..//input",
        		"/..//input",
        		"/../..//select",
        		"/..//select",
        		
        };
        
        if(dataLocator.isEmpty() || dataLocator == null){
	        	System.out.println("The dataLocator: "+dataLocator+" cannot be a label.");
	        	return null;
	        }
	    
		System.out.println("Validating if dataLocator: "+dataLocator+" is visible.");
        labelloop:
        while(labelAttempts < labelStructArray.length){
	        try{
		        	customWaitForElementVisibility(labelStructArray[labelAttempts], 10);
		        	break labelloop;
		        } catch(TimeoutException e){
		        	if(labelAttempts >= labelStructArray.length){
		        		
		            	System.out.println("The dataLocator: "+dataLocator+" cannot be a label.");
		            	return null;
		        	}
		        	labelAttempts += 1;
		        }
        }
        
        System.out.println("Attempting to find known valid path for dataLocator: "+dataLocator);
        while(attempts < inputTypesArray.length) {
            try {
            		String compoundPath = labelStructArray[labelAttempts]+inputTypesArray[attempts]+attributes;
	                driver.findElement(By.xpath(compoundPath)).click();
	                System.out.println("Valid Input path has been found"+" after "+attempts+" tries...");
	                System.out.println("Assigned path: \n"+compoundPath);
	                return compoundPath;
	                
	            } catch(Exception e){
	            	
	            }
            attempts++;
        }
        System.out.println("No valid path can be assigned after "+attempts+" tries...");
        return null;
	}
	
	public static String retryingSearchfromDupInput(String dataLocator, String parentPath) throws Exception{

        int attempts = 0;
        int labelAttempts = 0;
        
        String[] labelStructArray = {
        		parentPath+"//td//label[text() ='"+dataLocator+"']",
        };
        
        String[] inputTypesArray = {
        		"/../input",
        		"/../../td/input",
        		"/../../td/span/input",
        		"/../../td/span/span/input",
        		"/../../td/select",
        		"/../../td/table/tbody/tr/td/table/tbody/tr/td/span/select",
        		"/../../td/table/tbody/tr/td/input",
        		"/../../td/table/tbody/tr/td/table/tbody/tr/td/span/input",
        		"/../..//td/textarea",
        		"/../..//input",
        		"/..//input",
        		"/../..//select",
        		"/..//select",
        		
        };
        
        if(dataLocator.isEmpty() || dataLocator == null){
	        	System.out.println("The dataLocator: "+dataLocator+" cannot be a label.");
	        	return null;
	        }
	    
		System.out.println("Validating if dataLocator: "+dataLocator+" is visible.");
        labelloop:
        while(labelAttempts < labelStructArray.length){
	        try{
		        	customWaitForElementVisibility(labelStructArray[labelAttempts], 10);
		        	jsScrollIntoView(labelStructArray[labelAttempts]);
		        	break labelloop;
		        } catch(TimeoutException e){
		        	if(labelAttempts >= labelStructArray.length){
		        		
		            	System.out.println("The dataLocator: "+dataLocator+" cannot be a label.");
		            	return null;
		        	}
		        	labelAttempts += 1;
		        }
        }
        
        System.out.println("Attempting to find known valid path for dataLocator: "+dataLocator);
        while(attempts < inputTypesArray.length) {
            try {
            		String compoundPath = labelStructArray[labelAttempts]+inputTypesArray[attempts];
            		jsScrollIntoView(compoundPath);
	                driver.findElement(By.xpath(compoundPath)).click();
	                System.out.println("Valid Input path has been found"+" after "+attempts+" tries...");
	                System.out.println("Assigned path: \n"+compoundPath);
	                return compoundPath;
	                
	            } catch(Exception e){
	            	
	            }
            attempts++;
        }
        System.out.println("No valid path can be assigned after "+attempts+" tries...");
        return null;
	}
	/*public static String retryingSearchInput2(String dataLocator) throws Exception{

        int labelAttempts = 0;
        String inputReference = "";
        String newReference = "";
        boolean notYetFound = true;
		
        String[] labelStructArray = {
        		"//label[text()=' "+dataLocator+"']",
        		"//label[text()='"+dataLocator+"']"
        };
        
        if(dataLocator.isEmpty() || dataLocator == null){
        	System.out.println("The dataLocator: "+dataLocator+" cannot be a label.");
        	return null;
        }
    
		System.out.println("Validating if dataLocator: "+dataLocator+" is visible.");
	    labelloop:
	    while(labelAttempts < labelStructArray.length){
	        try{
		        	customWaitForElementVisibility(labelStructArray[labelAttempts], 10);
		        	System.out.println(dataLocator+ " is now visible...");
		        	break labelloop;
		        } catch(TimeoutException e){
		        	if(labelAttempts >= labelStructArray.length){
		        		
		            	System.out.println("The dataLocator: "+dataLocator+" cannot be a label.");
		            	return null;
		        	}
		        	labelAttempts += 1;
		        }
	    }
	    
	    System.out.println("Attempting to find known valid path for dataLocator: "+dataLocator);
	    java.util.List<WebElement> dataFolder = driver.findElements(By.xpath(labelStructArray[labelAttempts]));
	    System.out.println("Assigned label path: "+labelStructArray[labelAttempts]);
	    dataloop:
	    for(WebElement data : dataFolder){
	    	inputReference = data.getAttribute("for");
	    	System.out.println(data.getAttribute("class"));
		    System.out.println("Now holding: "+inputReference);
	    	if(inputReference != null && !inputReference.isEmpty())
	    		break dataloop;
	    	
	    }
	    //String inputReference = driver.findElement(By.xpath(labelStructArray[labelAttempts])).getAttribute("for");
	    System.out.println("Input reference id is "+inputReference);
	    
	    if(!inputReference.isEmpty() && !inputReference.contentEquals("")){
	    		while(notYetFound && inputReference.indexOf(":") != -1){
	    			inputReference = inputReference.substring(inputReference.indexOf(":")+1);
	    			try{
		    				driver.findElement(By.xpath("//input[contains(@id,'"+inputReference+"')]")).click();
		    				newReference = "//input[contains(@id,'"+inputReference+"')]";
		    				notYetFound = false;
		    				
		    			} catch(StaleElementReferenceException e){
		    				driver.findElement(By.xpath("//input[contains(@id,'"+inputReference+"')]")).click();
		    			} catch (Exception e) {
						}
	    			
	    			try{
		    				driver.findElement(By.xpath("//select[contains(@id,'"+inputReference+"')]")).click();
		    				newReference = "//select[contains(@id,'"+inputReference+"')]";
		    				notYetFound = false;
		    				
		    			} catch(StaleElementReferenceException e){
		    				driver.findElement(By.xpath("//select[contains(@id,'"+inputReference+"')]")).click();
		    			} catch (Exception e) {
						}
	    			
	    		}
	    	
		    	System.out.println("Valid input path has been found...");
		    	return newReference;
		    } else {
		    	System.out.println("No valid path can be assigned...");
		    	return null;
		    }
        
	}*/
	
	public static String filterDataLocator(String dataLocator) throws Exception{

		dataLocator = dataLocator.replaceAll("\\*", "");
		System.out.println("Now holding: "+dataLocator);
		
		//Second Level Filtering:
		if(dataLocator.indexOf("-") != -1){
			int dashIndex   = dataLocator.indexOf("-");
				dataLocator = dataLocator.substring(dashIndex+2);
		}
		
		if(dataLocator.indexOf("/") != -1){
			int slashIndex =  dataLocator.indexOf("/");
				dataLocator = dataLocator.substring(slashIndex+1);
		}
		
		if(dataLocator.indexOf(" ") == 0){
			int spaceIndex = dataLocator.indexOf(" ");
				dataLocator = dataLocator.substring(spaceIndex);
		}
		
		if(dataLocator.lastIndexOf(" ")+1 == dataLocator.length()){
			int lastSpace = dataLocator.lastIndexOf(" ");
				dataLocator = dataLocator.substring(0, lastSpace+1);
		}
		
		return dataLocator;
		
	}
	
	public static void retryingFindClick(By by) throws Exception{

		String text, tag;
        int attempts = 0;
        boolean scrollDown = true;
        
        System.out.println("Attempting to catch element.....");
        while(attempts < 11) {
            try {
                text = driver.findElement(by).getText();
        		tag = driver.findElement(by).getTagName();
                driver.findElement(by).click();
                System.out.println("Element has been refreshed after "+attempts+" tries.....");
        		
        		if(tag.contentEquals("a")){
	        			tag = "link";
	        		} else if(tag.contentEquals("span")){
	        			tag = "button";
	        		}
        		
        		if(text.indexOf("\n") != -1){
        			int enterIndex = text.indexOf("\n");
        			text = text.substring(0, enterIndex);
        		}
        		
        		log("Clicked "+text+" "+tag+"...");
        		System.out.println("Clicked "+text+" "+tag+"...");
                
                return ;
            } catch(org.openqa.selenium.StaleElementReferenceException e) {
            
            } catch(NoSuchElementException | ElementNotVisibleException e){
            	
            	try{
	            		scrollDown = scrollDownToElement(scrollDown, "");
	            	} catch(WebDriverException we){
	            		
	            	}
            } catch(WebDriverException e){
            	
            }
            attempts++;
        }
        
        System.out.println("Failed to find path: "+by+"");
        System.out.println("Throwing Error.....");
        log("Failed to find path: "+by+"");
        log("Throwing Error.....");
        throw new StaleElementReferenceException("The Element cannot be clicked...\n");
	}
	
	public static void retryingWrapper(CustomRunnable runner) throws Exception{
		int attempts = 0;
		while(attempts < 5){
			try{
					runner.customRun();
					return;
				} catch(Exception e){
					//Skips
					attempts += 1;
				}
		}
		throw new StaleElementReferenceException("Unable to execute command...");
	}
	
	//JS functions
	public static void jsCheckMessageContainer() throws Exception{
		int attempts = 0;
		String container = "dummy";
		String errMsg = "";
		
		JavascriptExecutor js = (JavascriptExecutor)driver;

		System.out.println("Verifying container value.....");	
		while(attempts < 5 && (!container.isEmpty() || !container.contentEquals(""))){
			container = (String)js.executeScript(
			"var msgContainer = document.getElementById('d1::msgCtr');"+
			" return msgContainer.innerHTML;"
			);
		
		attempts += 1;
		Thread.sleep(100);
		}
		System.out.println("Last container value: '"+container+"'\nafter "+attempts+" tries.");
		String tempMsg = driver.findElement(By.id("d1::msgDlg")).getText();
		
		if((container.isEmpty() || container.contentEquals("")) && tempMsg.contains("Error")){
			errMsg = driver.findElement(By.id("d1::msgDlg")).getText().replaceAll("OK", "").replace("Error", "");
			log(errMsg);
			
			if(tempMsg.contains("Legal Address")){
						throw new InvalidLegalAddressException(errMsg);
					} else if(tempMsg.contains("Location")){
						throw new InvalidLocationException(errMsg);
					} else{
						throw new DuplicateEntryException("Error FOUND: \n"+errMsg);
					}
		}
		
	}
	
	public static void jsCheckMissedInput() throws Exception{
		int attempts = 0;
		String container = "dummy";
		String errMsg = "";
		String msgPath = "//td[contains(@class,'NoteWindow') and not (contains(@class,'Border'))]";
		
		JavascriptExecutor js = (JavascriptExecutor)driver;

		System.out.println("Verifying container value.....");	
		while(attempts < 5 && (!container.isEmpty() || !container.contentEquals(""))){
			container = (String)js.executeScript(

			"function getElementByXPath(xPath){"+
			"	return document.evaluate(xPath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"+
			"}"+
			"var msgContainer = getElementByXPath(\""+msgPath+"\");"+
			"if(msgContainer != null)"+		
			"{return msgContainer.textContent;}"+
			"else{return '';}"
			);
		
		attempts += 1;
		Thread.sleep(100);
		}
		System.out.println("Last container value: '"+container+"'\nafter "+attempts+" tries.");
		
		if(container.contains("Error")){
			errMsg = driver.findElement(By.xpath(msgPath)).getText().replaceAll("OK", "");
			log(errMsg);
			throw new DuplicateEntryException("Error FOUND: \n"+errMsg);
		}
		
	}
	
	public static void jsFindThenClick(String dataPath){
		String text = "", tag = "", id = "";
		
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript(
			"function getElementByXPath(xPath){"+
					"	return document.evaluate(xPath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"+
					"}"+
			"getElementByXPath(\""+dataPath+"\").click();"
		);
		
		try{
				text = driver.findElement(By.xpath(dataPath)).getText();
				tag = driver.findElement(By.xpath(dataPath)).getTagName();
				id = driver.findElement(By.xpath(dataPath)).getAttribute("id");
			}catch(Exception e){
				//Skips the process..
			}
		
		if(tag.contentEquals("a")){
				tag = "link";
				if(text.isEmpty() || text.contains("")){//new addtion to link
					text = driver.findElement(By.xpath(dataPath)).getAttribute("title");
				}
			} else if(tag.contentEquals("span")){
				tag = "button";
			} else if(tag.contentEquals("select")){
				text = "\b";
			} else if(tag.contentEquals("input")){
				try{
						text = driver.findElement(By.xpath("//label[contains(@for,'"+id+"')]")).getText();
					} catch(NoSuchElementException e){
						text = "checkbox";
					}
			}
		
		if(text.indexOf("\n") != -1){
			int enterIndex = text.indexOf("\n");
			text = text.substring(0, enterIndex);
		}
		
		System.out.println("adding tags done...");
		log("Clicking "+text+" "+tag+"...");
		System.out.println("Clicking "+text+" "+tag+"...");
	}

	public static void jsScrollIntoView(String dataPath) throws Exception{
		//System.out.println("Adjusting view...");
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript(
			"function getElementByXPath(xPath){"+
					"	return document.evaluate(xPath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"+
					"}"+
			"getElementByXPath(\""+dataPath+"\").scrollIntoView(true);"
		);
		
		driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
		fluentWaitForElementInvisibility("//div[text()='Fetching Data...']", "Fetching Data...", 10);
	}

	public static boolean jsSideScroll(boolean isScrollingLeft){
		
		int scrollValue = 50;
		boolean scrollLeftAgain = true;
		String scrollerPath = "//div[contains(@id,'scroller')]";
		List<WebElement> scrollers = driver.findElements(By.xpath(scrollerPath));
		String sID;
		
		JavascriptExecutor js = (JavascriptExecutor)driver;
		for(WebElement scroller: scrollers){
			sID = scroller.getAttribute("id");
			
			scrollLeftAgain = (boolean)js.executeScript(
				"scrollMove = document.getElementById(\""+sID+"\").scrollLeft;"+
				"var oldScrollValue = scrollMove;"+
				"var newScrollValue = 0;"+
				"if(scrollMove != null || scrollMove != undefined){"+
				"	if("+isScrollingLeft+"){"+
				"		scrollMove += "+scrollValue+";"+
				"		newScrollValue = scrollMove;"+
				"	} else if(!"+isScrollingLeft+"){ scrollMove = 0; }"+
				
				"	if(oldScrollValue == newScrollValue){"+
				"		return false;}"+
				"}"+
				"return true;"
			);
		}
		
		return scrollLeftAgain;
	}
	
	public static String jsGetInputValue(String elemPath){
		String value ="";
		JavascriptExecutor js = (JavascriptExecutor)driver;
		value = (String)js.executeScript(
				"function getElementByXPath(xPath){"+
				"	return document.evaluate(xPath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"+
				"}"+
				"var inputContainer = getElementByXPath(\""+elemPath+"\");"+
				"var inputValue = inputContainer.value;"+
				"return inputValue;"
				);
		return value;
	}
	
	//Input Box Utilities...
	public static String getdataLocatorType(String dataLocator){
		String type = "";
		
		if(dataLocator.contains("Date")){
				type = "date";
			}else if(dataLocator.contains("Time") && !dataLocator.contains("Zone")){
				type = "time";
			}else{
				type = "text";
			}
			
		return type;
	}

	public static String manageNavigation(String navigationPath, int navPointer) throws Exception{
		int gtIndex, navLoc = -1;
		String targetNavPath = "";
		int gtCount = StringUtils.countMatches(navigationPath, ">");
		navigationPath = navigationPath .replaceAll("-> ", ">")
										.replaceAll("->", ">")
										.replaceAll(" ->", ">")
										.replaceAll("HCM", "Human Capital Management");
		//Skips the whole sequence...
		if(navPointer > gtCount)
			return "";
		
		gtIndex = navigationPath.indexOf(">");
		while(navLoc < navPointer){
			
			if(gtIndex != -1){
						targetNavPath = navigationPath.substring(0, gtIndex);
						navigationPath = navigationPath.substring(gtIndex+1);
					} else if(gtIndex == -1){
						targetNavPath = navigationPath;
					}	
			
			gtIndex = navigationPath.indexOf(">");
			navLoc += 1;
		}
		
		return targetNavPath;
	}
	
	public static String getProjectName(){
		System.out.println("Obtaining project name...");
		String rawText = "";
		
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
        	if(envName.contentEquals("HCM_PROJECT")){//PROJECT_NAME
                rawText = env.get(envName);
        	}
        }

        System.out.println(rawText);
        return rawText;
	}
	
	public static void setProjectName(String projectName, String ExcelFilePath, int inputRow) throws Exception{
		ExcelUtilities.setCellData(projectName, inputRow, BaseTest.defaultProjectNameValue, ExcelFilePath);
		System.out.println("Project Name has been updated.");
	}
}
