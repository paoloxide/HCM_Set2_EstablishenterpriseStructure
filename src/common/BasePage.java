package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import static util.ReportLogger.log;
import static util.ReportLogger.logDebug;
import static util.ReportLogger.tableRepLog;

public abstract class BasePage {

	/** The default timeout interval for waiting. */
    private static final long DEFAULT_TIMEOUT = 30L;

    /** Timeout for finding page element in seconds */
    protected static final long FIND_ELEMENT_TIMEOUT = 15L;

    /** The Selenium2 web driver. */
    protected WebDriver driver;
    
	static String driverPath;
	
	public Map<String, String> messages;

	/**
     * Constructs a new {@link BasePage} instance.
     * 
     * @param driver browser web driver
     */
    public BasePage(WebDriver driver)
    {
        this(driver, DEFAULT_TIMEOUT);
    }
	
	/**
     * Constructs a new {@link BasePage} instance.
     * 
     * @param driver browser web driver
     * @param url website URL
     */
    public BasePage(WebDriver driver, String url)
    {
        this(driver, url, DEFAULT_TIMEOUT);
    }
    
    /**
     * Constructs a new {@link BasePage} instance.
     * 
     * @param driver browser web driver
     * @param pageLoadTimeout page loading timeout in seconds
     */
    public BasePage(WebDriver driver, long pageLoadTimeout)
    {
        this.driver = driver;
        waitUntilPageLoaded(pageLoadTimeout);
    }
    
    /**
     * Constructs a new {@link BasePage} instance.
     * 
     * @param driver browser web driver
     * @param pageLoadTimeout page loading timeout in seconds
     * @param url website URL
     */
    public BasePage(WebDriver driver, String url, long pageLoadTimeout)
    {
        this.driver = driver;
        driver.get(url);
        waitUntilPageLoaded(pageLoadTimeout);
    }
	
	public String getPageTitle() {
		String title = driver.getTitle();
		return title;
	}
	
    
	
    /**
     * Read property file
     * 
     * @param elementId the element id
     * @return the element found
     */
 

    /**
     * Find the element in the DOM by id.
     * 
     * @param elementId the element id
     * @return the element found
     */
    public WebElement findById(String elementId)
    {
        try
        {
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.id(elementId)));
        }
        catch (StaleElementReferenceException e)
        {
            System.out.println("From findById: recovering from staleelement issue...");
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.id(elementId)));
        }
        catch (Exception e)
        {
            System.out
                    .println("From findById(AssertionError): recovering from staleelement issue...");
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.id(elementId)));
        }

    }

    /**
     * Find the element in the DOM by id.
     * 
     * @param elementId the element id
     * @return the element found
     */
    public WebElement findByName(String elementName)
    {
        try
        {
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.name(elementName)));
        }
        catch (StaleElementReferenceException e)
        {
            System.out.println("From findByName: recovering from staleelement issue...");
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.name(elementName)));
        }
        catch (Throwable e)
        {
            System.out
                    .println("From findByName(AssertionError): recovering from staleelement issue...");
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.name(elementName)));
        }

    }

    /**
     * Find the element in the DOM by xpath.
     * 
     * @param xpath the xpath expression
     * @return the element found
     */
    public WebElement findByXpath(String xpath)
    {
        try
        {
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath(xpath)));
        }
        catch (StaleElementReferenceException e)
        {
            System.out.println("From findByXpath: recovering from staleelement issue...");
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath(xpath)));
        }
        catch (Throwable e)
        {
            System.out
                    .println("From findByXpath(AssertionError): recovering from staleelement issue...");
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath(xpath)));
        }
    }

   
    /**
     * Find the element in the link text.
     * 
     * @param linkText The link text.
     * @return the element with the link text
     */
    public WebElement findByLinkText(String linkText)
    {
        try
        {
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.linkText(linkText)));
        }
        catch (StaleElementReferenceException e)
        {
            System.out.println("From findByLinkText: recovering from staleelement issue...");
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.linkText(linkText)));
        }
        catch (Throwable e)
        {
            System.out
                    .println("From findByLinkText(AssertionError): recovering from staleelement issue...");
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.linkText(linkText)));
        }

    }

    /**
     * Searches for the {@link WebElement} instance thru its locator using the
     * CSS selector method.
     * 
     * @param locator locator
     * @return {@link WebElement} instance; will throw a
     *         {@link NoSuchElementException} if there is no corresponding
     *         {@link WebElement} being represented by the locator
     */
    public WebElement findByCssSelector(String locator)
    {
        try
        {
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.cssSelector(locator)));
        }
        catch (StaleElementReferenceException e)
        {
            System.out.println("From findByCssSelector: recovering from staleelement issue...");
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.cssSelector(locator)));
        }
        catch (Throwable e)
        {
            System.out
                    .println("From findByClassname(AssertionError): recovering from staleelement issue...");
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.className(locator)));
        }

    }

    /**
     * Searches for the {@link WebElement} instance thru its locator using the
     * ClassName selector method.
     * 
     * @param locator locator
     * @return {@link WebElement} instance; will throw a
     *         {@link NoSuchElementException} if there is no corresponding
     *         {@link WebElement} being represented by the locator
     */
    public WebElement findByClassname(String locator)
    {
        try
        {
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.className(locator)));
        }
        catch (StaleElementReferenceException e)
        {
            System.out.println("From findByClassname: recovering from staleelement issue...");
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.className(locator)));
        }
        catch (Throwable e)
        {
            System.out
                    .println("From findByClassname(AssertionError): recovering from staleelement issue...");
            return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                    .visibilityOfElementLocated(By.className(locator)));
        }

    }
    
    /**
     * Waits for the {@link WebElement} to be clickable
     * 
     * @param locator locator
     * @return
     */
    public WebElement waitForElementToBeEnabled(String locator)
    {
        return new WebDriverWait(driver, FIND_ELEMENT_TIMEOUT).until(ExpectedConditions
                .elementToBeClickable(By.id(locator)));

    }
    
    /**
     * Determine whether the specified element is present.
     * 
     * @param by the find by criterial.
     * @return the element is present on the page.
     */
    public boolean isElementPresent(String by)
    {
        if (driver.findElements(By.id(by)).size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Wait until the ajax request has been completed
     */
    public void waitForA4jRequest()
    {
        (new WebDriverWait(driver, DEFAULT_TIMEOUT)).until(new WaitForA4jRequestHandler());
    }
    
    public void waitForAjax() {
    	try{
        new WebDriverWait(driver, DEFAULT_TIMEOUT).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                return (Boolean) js.executeScript("return jQuery.active == 0");
            	}
        	});
        
    	}catch (Exception e)
    	{
    		
    	}
    }

    /**
     * This method will click a web element using ID
     * 
     * @param locator
     * 
     * @author lenard.g.magpantay
     * 
     */
    public void click (String locator){
    	
    WebElement element = findById(locator);
    element.click();
    }
    
    /**
     * This method will click a web element using XPATH
     * 
     * @param locator
     * 
     * @author lenard.g.magpantay
     * 
     */
    public void clickByXpath (String locator){
    	
    WebElement element = findByXpath(locator);
    element.click();
    }
    
    /**
     * This method will click a web element using CSS
     * 
     * @param locator
     * 
     * @author lenard.g.magpantay
     * 
     */
    public void clickByCss (String locator){
    	
    WebElement element = findByCssSelector(locator);
    element.click();
    }
    
    /**
     * This method will click a web element using LINK TEXT
     * 
     * @param text
     * 
     * @author lenard.g.magpantay
     * 
     */
    public void clickByLink (String text){
    	
        WebElement element = findByLinkText(text);
        element.click();
        }
    
    /**
     * This method will click a web element using CLASS NAME
     * 
     * @param locator
     * 
     * @author lenard.g.magpantay
     * 
     */
    public void clickByClassName (String locator){
    	
        WebElement element = findByClassname(locator);
        element.click();
        }
    
    /**
     * This method will click a web element using NAME
     * 
     * @param locator
     * 
     * @author lenard.g.magpantay
     * 
     */
    public void clickByName (String locator){
    	
        WebElement element = findByName(locator);
        element.click();
        }

    /**
     * This method will enter text in a web element by ID
     * 
     * @param locator
     * @param value
     *
     * @author lenard.g.magpantay
        element.submit();
     * 
     */
    public void enterText(String locator, String value){
    	
    WebElement element = findById(locator);
    element.clear();
    element.sendKeys(value);
    }
    
    /**
     * This method will delete first the value using keyboard actions then input values
     * 
     * @param locator
     * @param value
     *
     * @author lenard.g.magpantay
     * 
     */
    public void enterTextSpecial_Name(String locator, String value){
    	
    WebElement element = findByName(locator);
    element.clear();
    element.sendKeys(Keys.chord(Keys.BACK_SPACE,Keys.BACK_SPACE),value);
    }
    
    public void enterTextSpecial(String locator, String value){
    	
        WebElement element = findById(locator);
        element.clear();
        element.sendKeys(Keys.chord(Keys.BACK_SPACE,Keys.BACK_SPACE),value);
        }
    
    /**
     * This method will enter text in a web element by NAME
     * 
     * @param locator
     * @param value
     *
     * @author lenard.g.magpantay
     * 
     */
    public void enterTextByName(String locator, String value){
    	
    WebElement element = findByName(locator);
    element.clear();
    element.sendKeys(value);
    }
    
    /**
     * This method will enter text in a web element by CSS
     * 
     * @param locator
     * @param value
     *
     * @author lenard.g.magpantay
     * 
     */
    public void enterTextByCss(String locator, String value){
    	
        WebElement element = findByCssSelector(locator);
        element.clear();
        element.sendKeys(value);
        }
    
    /**
     * This method will enter text in a web element by xpath
     * 
     * @param locator
     * @param value
     *
     * @author lenard.g.magpantay
     * 
     */
    public void enterTextByXpath(String locator, String value){
    	
        WebElement element = findByXpath(locator);
        element.clear();
        element.sendKeys(value);
        }
    
    /**
     * This method will select a text value from a dropdown by ID
     * 
     * @param locator
     * @param value
     *
     * @author lenard.g.magpantay
     * 
     */
    public void selectDropdownByVisibleText(String locator, String value){
    	
    new Select(findById(locator)).selectByVisibleText(value);
    }
    
    /**
     * This method will select a text value from a dropdown by XPATH
     * 
     * @param locator
     * @param value
     *
     * @author lenard.g.magpantay
     * 
     */
    public void selectDropdownByVisibleText_Xpath(String locator, String value){
    	
        new Select(findByXpath(locator)).selectByVisibleText(value);
        }
    
    /**
     * This method will select a text value from a dropdown by NAME
     * 
     * @param locator
     * @param value
     *
     * @author lenard.g.magpantay
     * 
     */
    public void selectDropdownByVisibleText_Name(String locator, String value){
    	
        new Select(findByName(locator)).selectByVisibleText(value);
        }
    
    /**
     * This method will select a text value from a dropdown by CLASS NAME
     * 
     * @param locator
     * @param value
     *
     * @author lenard.g.magpantay
     * 
     */
    public void selectDropdownByVisibleText_ClassName(String locator, String value){
    	
        new Select(findByClassname(locator)).selectByVisibleText(value);
        }
    
    /**
     * This method will select a value by index from a dropdown by ID
     * 
     * @param locator
     * @param value
     *
     * @author lenard.g.magpantay
     * 
     */
    public void selectDropdownByIndex(String locator, int index){
    	
        new Select(findById(locator)).selectByIndex(index);
        }
    
    /**
     * This method will select a value by index from a dropdown by Xpath
     * 
     * @param locator
     * @param value
     *
     * @author lenard.g.magpantay
     * 
     */
    public void selectDropdownByIndex_Xpath(String locator, int index){
    	
        new Select(findByXpath(locator)).selectByIndex(index);
        }
    
    /**
     * This method will select a value by index from a dropdown by Xpath
     * 
     * @param locator
     * @param value
     *
     * @author lenard.g.magpantay
     * 
     */
    public void selectDropdownByIndex_Name(String locator, int index){
    	
        new Select(findByName(locator)).selectByIndex(index);
        }
    
    /**
     * This method will multiple values from a dropdown by ID
     * 
     * @param locator
     * @param value
     *
     * @author lenard.g.magpantay
     * 
     */
    public void selectMany(String locator, String[] values)
    {
        watchA4jRequests();
        for (String value : values)
        {
            new Select(findById(locator)).selectByVisibleText(value);
        }
        clickSomewhere();
        waitForA4jRequest();
    }
    
    /**
     * Click anywhere on the page to trigger the ajax if there is any.
     */
    public void clickSomewhere()
    {
        WebElement appOriginator = getElementById("main:infoareaScreenTitle");

        // if the first locator is available, do the selectDropdown action in it
        try
        {
            if (appOriginator != null)
            {
                findById("main:infoareaScreenTitle").click();
            }
            // else do the selectDropdown on other locator
            else
            {
                findById("whiteShade").click();
            }
        }
        catch (Exception e)
        {

        }
    }
    
    /**
     * Searches for the element in the current page by the value of the
     * <i>id</i> attribute.
     * <p>
     * Do not use {@link WebDriver#findElement(By)} if you're not sure if the
     * element exists in the page as this will take some time to complete. Check
     * out <a href=
     * "https://groups.google.com/forum/#!topic/selenium-developers/kS1YCoMMc0U"
     * >this discussion</a>.
     * 
     * @param locator contains the value of the <i>id</i> attribute
     * @return a {@link WebElement} instance representing the page element;
     *         {@code null} if corresponding page element is not found
     */
    public WebElement getElementById(String locator)
    {
        List<WebElement> elements = driver.findElements(By.id(locator));

        if (elements.size() > 0)
        {
            return elements.get(0);
        }
        else
        {
            return null;
        }
    }
    
    
    /**
     * Wait for the specified time in ms
     * 
     * @param time the wait time
     */
    public void waitFor(long time)
    {
        driver.manage().timeouts().setScriptTimeout(time, TimeUnit.MILLISECONDS);
    }

    /**
     * Enable the monitoring of ajax request.
     */
    public void watchA4jRequests()
    {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor
                .executeScript("ajaxMonitor=new AjaxMonitor(); return ajaxMonitor.watchA4jRequests();");
    }

    /**
     * A blocking call which waits (for a period defined by DEFAULT_TIMEOUT)
     * until the current page is loaded before execution continue. The code
     * detects the page load by determining whether the URL of browser matches
     * the one it is expecting.
     */
    public void waitUntilPageLoaded()
    {
        waitUntilPageLoaded(DEFAULT_TIMEOUT);
    }
    
    /**
     * Retrieves the page ID of this page.
     * <p>
     * The page ID forms part of the URL in the browser. For example, it is the
     * section in bold in the following:<br/>
     * http://localhost:8080/lendfast/lendfast<b>/views/loandetails/
     * PropertyDetailsView.seam</b>?cid=38
     * </p>
     * 
     * @return The page ID as a String.
     */
    public abstract String getPageId();
    
    
    /**
     * Determines whether this page is the <i>Logon</i> page.
     * 
     * @return {@code true} if this page is the <i>Logon</i> page; {@code false}
     *         otherwise
     */    
    public boolean isLogonPage()
    {
        return getPageId().equals("/logon.jsp");
    }
    
    /**
     * Searches for the element in the current page using using the CSS selector
     * method.
     * <p>
     * Do not use {@link WebDriver#findElement(By)} if you're not sure if the
     * element exists in the page as this will take some time to complete. Check
     * out <a href=
     * "https://groups.google.com/forum/#!topic/selenium-developers/kS1YCoMMc0U"
     * >this discussion</a>.
     * 
     * @param locator CSS locator of the page element
     * @return a {@link WebElement} instance representing the page element;
     *         {@code null} if corresponding page element is not found
     */
    public WebElement getElementByCssSelector(String locator)
    {
        List<WebElement> elements = driver.findElements(By.cssSelector(locator));

        if (elements.size() > 0)
        {
            return elements.get(0);
        }
        else
        {
            return null;
        }
    }
    
    /**
     * A blocking call which wait until the current page is loaded before
     * execution continue. The code detects the page load by determining whether
     * the URL of browser matches the one it is expecting.
     * 
     * @param timeout
     */
    public void waitUntilPageLoaded(long timeout)
    {
        long startTime = System.currentTimeMillis();

        System.out.print("Checking landed page...  ");
        log("<strong>Checking landed page...  </strong>");

        try
        {
            new WebDriverWait(driver, timeout).until(new WaitForPageLoadedHandler(this));
        }
        catch (TimeoutException te)
        {
            if (isDebug())
            {
                logDebug("Duration of waitUntilPageLoaded() until TimeoutException was thrown: "
                        + (System.currentTimeMillis() - startTime));
            }

            if (checkForScreenErrors)
            {
                checkScreenError();
            }

            throw te;
        }

        System.out.println("We landed on the correct page: " + getPageId());
        log("We landed on the correct page: " + "<em>" + getPageId() + "</em>");
    }
    
    /** Checks for screen errors after <i>Next</i> button is clicked */
    private boolean checkForScreenErrors = Boolean.parseBoolean(System
            .getProperty("checkForScreenErrors"));
    
    /**
     * Fails the test if the expected {@link BasePage} instance does not
     * represent the current screen displayed.
     */
    private void checkScreenError()
    {
        long startTime = System.currentTimeMillis();
        String currentPageUrl = driver.getCurrentUrl();

        if (isDebug())
        {
            logDebug("Response time for driver.getCurrentUrl(): "
                    + (System.currentTimeMillis() - startTime));

            logDebug("currentPageUrl = " + currentPageUrl);
            logDebug("getPageId() = " + getPageId());

            startTime = System.currentTimeMillis();
        }

        if (currentPageUrl != null && !currentPageUrl.contains(getPageId()))
        {
            int lastIndex = currentPageUrl.indexOf("?");
            lastIndex = lastIndex > 0 ? lastIndex : currentPageUrl.length();
            String currentPageId = currentPageUrl.substring(currentPageUrl.lastIndexOf("/"),
                lastIndex);
            String failureMessage = null;

            if (isDebug())
            {
                startTime = System.currentTimeMillis();
            }

            String errorMessage = getErrorMessage();

            if (isDebug())
            {
                logDebug("Response time for checkScreenError()->getErrorMessage(): "
                        + (System.currentTimeMillis() - startTime));
            }

            if (errorMessage != null)
            {
                failureMessage = errorMessage + " [Current page ID = '" + currentPageId + "']";
            }
            else
            {
                failureMessage = "The expected page object (" + getClass().getSimpleName()
                        + ") in this test scenario does not represent the "
                        + "currently displayed screen with page ID '" + currentPageId + "'.";
            }

            System.out.println("Incorrect page, should be: .." + getPageId());
            log("<span style=\"color: #DC143C; font-weight:bold;\">Incorrect page,</span> should be: .."
                    + "<em>" + getPageId() + "</em>");
            Assert.fail(failureMessage);
        }
    }
    
    /** Debug mode */
    private boolean debug = Boolean.parseBoolean(System.getProperty("debug"));
    
    /**
     * Indicates if this test is being executed in debug mode.
     * 
     * @return {@code true} if this test is being executed in debug mode;
     *         {@code false} otherwise
     */
    protected boolean isDebug()
    {
        return debug;
    }
    
    /**
     * Returns the error message displayed in the current screen.
     * 
     * @param page current screen
     */
    public String getErrorMessage()
    {
        WebElement errorMessageElement = null;
        long startTime = System.currentTimeMillis();

        try
        {
            errorMessageElement = new WebDriverWait(driver, 0L)
                    .until(ExpectedConditions
                            .visibilityOfElementLocated(By
                                    .cssSelector("dt[class='errorMsg'] > span[class='rich-messages-label']")));
        }
        catch (TimeoutException te)
        {
        }

        if (isDebug())
        {
            logDebug("Response time for getErrorMessage()->getElementByCssSelector(): "
                    + (System.currentTimeMillis() - startTime));
        }

        if (errorMessageElement == null)
        {
            long waitStartTime = System.currentTimeMillis();

            try
            {
                errorMessageElement = new WebDriverWait(driver, 0L).until(ExpectedConditions
                        .visibilityOfElementLocated(By.cssSelector("div[class='alerts']")));
            }
            catch (TimeoutException te)
            {
            }

            if (isDebug())
            {
                logDebug("Response time for getErrorMessage()->getElementByCssSelector()[Logon error]: "
                        + (System.currentTimeMillis() - waitStartTime));
            }
        }

        if (isDebug())
        {
            logDebug("Response time for getErrorMessage(): "
                    + (System.currentTimeMillis() - startTime));
        }

        if (errorMessageElement != null)
        {
            return errorMessageElement.getText();
        }

        return null;
    }
    
    /**
     * This method will double-click a web element using ID
     * 
     * @param locator
     * 
     * @author maria.kris.p.manzano
     * 
     */

    public void doubleClick (String locator){
       
        WebElement element = findById(locator);
        Actions action = new Actions(driver);
              action.moveToElement(element).doubleClick(element).perform();
    }
    
    /**
     * This method will double-click a web element using XPATH
     * 
     * @param locator
     * 
     * @author maria.kris.p.manzano
     * 
     */

    public void doubleClickByXpath (String locator){
       
        WebElement element = findByXpath(locator);
        Actions action = new Actions(driver);
              action.moveToElement(element).doubleClick(element).perform();
    }
    
    
    /**
     * This method will double-click a web element using Name
     * 
     * @param locator
     * 
     * @author maria.kris.p.manzano
     * 
     */

    public void doubleClickByName(String locator){
       
        WebElement element = findByName(locator);
        Actions action = new Actions(driver);
              action.moveToElement(element).doubleClick(element).perform();
    }

    
    

}


