package common;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import java.util.Iterator;

import static util.ReportLogger.DEBUG_VERBOSITY_LEVEL;
import static util.ReportLogger.log;
import static util.ReportLogger.logDebug;
import static util.ReportLogger.tableRepLog;
import static common.ExcelUtilities.getCellData;
import static common.ExcelUtilities.setExcelFile;
import static common.ExcelUtilities.getRowContains;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xml.utils.Constants;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.beust.jcommander.Parameter;
import com.thoughtworks.selenium.webdriven.commands.WaitForPageToLoad;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class BaseTest {

	public static WebDriver driver;

	// Adding augmented driver....r
	public static WebDriver augmentedDriver;

	/** Firebug version */
	private static final String FIREBUG_VERSION = "1.9.0";

	/** Firebug file name */
	private static final String FIREBUG_FILENAME = "resources/firebug-1.9.0.xpi";

	/** Default folder where screenshots will be saved */
	protected static final String SCREENSHOT_PATH = "target//surefire-reports";

	/** Location of screenshot files */
	protected static String screenshotPath = null;

	/** Location of screenshot files */
	protected static String screenshotFileSimpleName = null;

	/** Build Version */
	protected String buildVersion = null;

	/** Location of screenshot files for ApplicationForm Class */
	protected String screenshotBuilderPath = null;

	/** Number of screenshots */
	protected static int screenshotCounter = 0;

	/** Verification errors */
	StringBuffer verificationErrors = new StringBuffer();

	/** Date format */
	protected DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	protected DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

	/** Timestamp build */
	protected String reporttime;

	/** TestSuite Name */
	protected String testSuiteName;

	/** URL to be used */
	String webappUrl = "";

	/** Browser to be used */
	String webBrowser = "ie";

	/** Check for screen errors */
	protected boolean checkForScreenErrors = false;

	/** added for additional method in checking screen errors */
	protected static boolean hasScreenErrors = false;

	/** The default timeout for waiting on an element's appearance */
	private static final long ELEMENT_APPEAR = 30L;

	private static final long PAGE_TIMEOUT = 30L;

	protected String infoMessageLocator = "";

	public static int TestCaseRow;
	
	public static String ExcelPath;

	public static String windowHandle = null;
	
	//Setting ExcelFileValue Locators.....
	public static final int defaultProjectNameValue = 7;
	public static final int defaultmainTaskValue = 9;
	public static final int defaultsubTaskValue  = 11;
	public static final int scanType = 8;
	public static final String mainTaskReference = "afrrk";
	public static final String subTaskReference  = "afrap";

	@Parameters({ "TestNo" })
	@AfterClass
	public void afterClass(String TestNo) throws IOException {
		String TestCaseName = this.getClass().getSimpleName();
		log("===== Terminating " + TestCaseName + " :" + TestNo + " =====");
		System.out.println("");
		System.out.println("===== Terminating " + TestCaseName + " :" + TestNo
				+ " =====");
		System.out.println("");

		driver.close();
		driver.quit();

		String verificationErrorString = verificationErrors.toString();

		if (!"".equals(verificationErrorString)) {
			Assert.fail(verificationErrorString);
		}
	}

	/**
	 * Call the browser to be used and open the url.
	 * 
	 * @param webappUrl
	 *            URL of the application to be used
	 * @param webBrowser
	 *            Web browser to be used
	 * @throws Exception
	 */
	@Parameters({ "ExcelFilePath", "ExcelSheetName", "TestNo" })
	@BeforeClass
	public int BeforeTest(String ExcelFilePath, String ExcelSheetName,
			String TestNo) throws Exception {

		String TestCaseName = this.getClass().getSimpleName();

		log("");
		log("===== Executing " + TestCaseName + " :" + TestNo + " =====");
		log("");
		System.out.println("");
		System.out.println("===== Executing " + TestCaseName + " :" + TestNo
				+ " =====");
		System.out.println("");

		this.checkForScreenErrors = Boolean.parseBoolean(System
				.getProperty("checkForScreenErrors"));
		log("Check For Screen Errors: " + checkForScreenErrors,
				DEBUG_VERBOSITY_LEVEL);

		// to enable output report assertion format
		testReportFormat();

		setExcelFile(ExcelFilePath, ExcelSheetName);
		TestCaseRow = getRowContains(TestNo, 0, TestCaseName, 1); //Code might break;; 3/28/2016
		ExcelPath = ExcelFilePath;
		String webBrowser = getCellData(TestCaseRow, 3);
		String webappUrl = getCellData(TestCaseRow, 4);

		try {
			if (webBrowser.equalsIgnoreCase("ie")) {
				File file = new File(
						"C:\\Users\\jerrick.m.falogme\\Desktop\\my_workspace\\HCM-Configs\\hcmselenium\\resources\\IEDriverServer.exe");
				System.setProperty("webdriver.ie.driver",
						file.getAbsolutePath());
				driver = new ExtendedInternetExplorerDriver(
						createCapabilities(webBrowser));
				System.out.println("Running Test in IE");
				log("Running Test in IE");
				driver.get(webappUrl);
			} else if (webBrowser.equalsIgnoreCase("firefox")) {
				 driver = new RemoteWebDriver(new
				 URL("http://selenium-hub:4444/wd/hub"),
				 DesiredCapabilities.firefox());
				//driver = new RemoteWebDriver(new URL(
				//		"http://10.251.120.22:4444/wd/hub"),
				//		DesiredCapabilities.firefox());
				//driver = new
				//ExtendedFirefoxDriver(createCapabilities(webBrowser));
				System.out.println("Running Test in FireFox");
				log("Running Test in FireFox");
				driver.get(webappUrl);
				// RemoteWebDriver does not implement the TakesScreenshot class
				// if the driver does have the Capabilities to take a screenshot
				// then Augmenter will add the TakesScreenshot methods to the
				// instance
				augmentedDriver = new Augmenter().augment(driver);
				// File screenshot =
				// ((org.openqa.selenium.TakesScreenshot)augmentedDriver).
				// getScreenshotAs(OutputType.FILE);
			} else {
				File file = new File(
						"C:\\Users\\jerrick.m.falogme\\Desktop\\my_workspace\\HCM-Configs\\hcmselenium\\resources\\chromedriver.exe");
				System.setProperty("webdriver.chrome.driver",
						file.getAbsolutePath());
				driver = new ExtendedChromeDriver(
						createCapabilities(webBrowser));
				System.out.println("Running Test in Chrome");
				log("Running Test in Chrome");
				driver.get(webappUrl);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		driver.manage().window().maximize();
		getScreenshotPath();

		this.debug = Boolean.parseBoolean(System.getProperty("debug"));
		log("Debug mode: " + debug, DEBUG_VERBOSITY_LEVEL);

		boolean logOutputEscaped = Boolean.parseBoolean(System
				.getProperty("org.uncommons.reportng.escape-output"));
		log("Escaped log output: " + logOutputEscaped, DEBUG_VERBOSITY_LEVEL);

		return TestCaseRow;

	}

	/**
	 * Create the firefox profile to include firebug.
	 * 
	 * @return created the firefox profile
	 */
	private FirefoxProfile createFireFoxProfile() {
		FirefoxProfile profile = null;
		try {
			profile = new FirefoxProfile();
			profile.addExtension(new File(FIREBUG_FILENAME));
			profile.setPreference("extensions.firebug.currentVersion",
					FIREBUG_VERSION);
			profile.setPreference("extensions.firebug.console.enableSites",
					true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return profile;
	}

	/**
	 * Create the capabilities of the browser. Add profile if browser is
	 * firefox.
	 * 
	 * @param browser
	 *            the browser used for testing
	 * @return the created capabilities
	 */
	protected Capabilities createCapabilities(String browser) {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setJavascriptEnabled(true);
		capabilities.setBrowserName(browser);

		if (browser.equals(DesiredCapabilities.firefox().getBrowserName())) {
			capabilities.setCapability(FirefoxDriver.PROFILE,
					createFireFoxProfile());
		} // else if
			// (browser.equals(DesiredCapabilities.internetExplorer().getBrowserName()))
		// {
		// capabilities.setCapability("ignoreZoomSetting", true);
		// capabilities.setCapability("nativeEvents",false);
		// capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
		// }

		return capabilities;
	}

	/**
	 * Captures the the current screen.
	 */
	protected void takeScreenshot() {
		takeScreenshot(this.getClass().getSimpleName(), "");
	}

	/**
	 * Captures the the current screen.
	 * 
	 * @param message
	 *            text that will be added to the snapshot link in the report log
	 */
	protected void takeScreenshot(String message) {
		takeScreenshot(this.getClass().getSimpleName(), message);
		System.out.println("Taking Screenshot");
	}

	/**
	 * Captures the browser output for the specified {@link BasePage}.
	 * 
	 * @param page
	 *            {@link BasePage} instance
	 */
	protected void takeScreenshot(BasePage page) {
		takeScreenshot(page, true);
	}

	protected void takeScreenshot(BasePage page, boolean isDisplayPageTitle) {
		String title = "";

		if (isDisplayPageTitle && !page.isLogonPage()) {
			title = getPageTitle(page);
		}

		takeScreenshot(page, title);
	}

	/**
	 * Captures the browser output for the specified {@link BasePage}.
	 * 
	 * @param page
	 *            {@link BasePage} instance
	 * @param message
	 *            text that will be added to the snapshot link in the report log
	 */
	protected void takeScreenshot(BasePage page, String message) {
		String pageName = page.getPageId();

		if (pageName.endsWith("seam")) {
			pageName = pageName.substring(1, pageName.length() - 5);
		} else if (pageName.endsWith("jsp")) {
			pageName = pageName.substring(1, pageName.length() - 4);
		} else {
			pageName = pageName.substring(1, pageName.length() - 0);
		}

		takeScreenshot(pageName, message);
	}

	public void printScreen() {
		createScreenshotUsingRobot(this.getClass().getSimpleName(), "");
	}

	public static void createScreenshotUsingRobot(String filename,
			String message) {

		String path = screenshotPath;
		BufferedImage screenshot = null;
		long startTime = 0L;

		if (isDebug()) {
			startTime = System.currentTimeMillis();
		}

		if (StringUtils.isBlank(path)) {
			throw new IllegalArgumentException("Invalid path.");
		}

		if (StringUtils.isBlank(filename)) {
			throw new IllegalArgumentException("Invalid filename.");
		}

		try {
			File dir = new File(path);

			if (dir != null && !dir.exists()) {
				dir.mkdir();
			}

			String datePrefix = new SimpleDateFormat("yyyyMMdd_HHmmssSSS")
					.format(new Date());

			// driver.manage().window().maximize();
			screenshot = new Robot().createScreenCapture(new Rectangle(Toolkit
					.getDefaultToolkit().getScreenSize()));

			File screenshotFile = new File(MessageFormat.format("{0}/{1}-{2}",
					path, datePrefix, filename + ".png"));
			screenshotFileSimpleName = datePrefix + "-" + filename + ".png";
			String screenshotFilePath = screenshotFile.getPath().replace("\\",
					"/");
			String screenshotId = datePrefix.substring(screenshotFile.getName()
					.indexOf("_") + 1);

			FileOutputStream outputStream = new FileOutputStream(screenshotFile);
			try {
				ImageIO.write(screenshot, "png", new File(screenshotFilePath));
			} finally {
				outputStream.close();
			}

			log("Screenshot "
					+ ++screenshotCounter
					+ ": <a style=\"border-bottom:1px dotted #000000; color:#000000; outline:none; text-decoration:none; position:relative;\""
					+ " onMouseOver=\"img"
					+ screenshotId
					+ ".style.display='block';\" onMouseOut=\"img"
					+ screenshotId
					+ ".style.display='none';\" href=\"javascript:window.open('../../../"
					+ screenshotFilePath
					+ "', '"
					+ screenshotFile.getName()
					+ "', 'height=768,width=1280')\">"
					+ filename
					+ "<span id='img"
					+ screenshotId
					+ "' style=\"position:absolute; display:none; z-index:99;\" ><img src=\"../../../"
					+ screenshotFilePath
					+ "\" width=\"800\" height=\"450\" /></span>" + "</a> "
					+ message);
		} catch (AWTException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			log(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log(e.getMessage());
			e.printStackTrace();
		}

		if (isDebug()) {
			logDebug("Response time for takeScreenshot(): "
					+ (System.currentTimeMillis() - startTime));
		}
	}

	/**
	 * Captures the browser output and writes the snapshot to a file with
	 * specified filename.
	 * 
	 * @param filename
	 *            filename of the image file to be created
	 * @param message
	 *            text that will be added to the snapshot link in the report log
	 */
	protected void takeScreenshot(String filename, String message) {
		String path = screenshotPath;
		long startTime = 0L;

		if (isDebug()) {
			startTime = System.currentTimeMillis();
		}

		if (StringUtils.isBlank(path)) {
			throw new IllegalArgumentException("Invalid path.");
		}

		if (StringUtils.isBlank(filename)) {
			throw new IllegalArgumentException("Invalid filename.");
		}

		try {
			File dir = new File(path);

			if (dir != null && !dir.exists()) {
				dir.mkdir();
			}

			String datePrefix = new SimpleDateFormat("yyyyMMdd_HHmmssSSS")
					.format(new Date());
			// 3/10/2016
			byte[] screenshot;
			// screenshot = ((TakesScreenshot)
			// driver).getScreenshot(OutputType.BYTES);
			screenshot = ((org.openqa.selenium.TakesScreenshot) augmentedDriver)
					.getScreenshotAs(OutputType.BYTES);

			File screenshotFile = new File(MessageFormat.format("{0}/{1}-{2}",
					path, datePrefix, filename + ".png"));
			screenshotFileSimpleName = datePrefix + "-" + filename + ".png";
			String screenshotFilePath = screenshotFile.getPath().replace("\\",
					"/");
			String screenshotId = datePrefix.substring(screenshotFile.getName()
					.indexOf("_") + 1);

			FileOutputStream outputStream = new FileOutputStream(screenshotFile);
			try {
				outputStream.write(screenshot);
			} finally {
				outputStream.close();
			}

			log("Screenshot "
					+ ++screenshotCounter
					+ ": <a style=\"border-bottom:1px dotted #000000; color:#000000; outline:none; text-decoration:none; position:relative;\""
					+ " onMouseOver=\"img"
					+ screenshotId
					+ ".style.display='block';\" onMouseOut=\"img"
					+ screenshotId
					+ ".style.display='none';\" href=\"javascript:window.open('../../../"
					+ screenshotFilePath
					+ "', '"
					+ screenshotFile.getName()
					+ "', 'height=768,width=1280')\">"
					+ filename
					+ "<span id='img"
					+ screenshotId
					+ "' style=\"position:absolute; display:none; z-index:99;\" ><img src=\"../../../"
					+ screenshotFilePath
					+ "\" width=\"800\" height=\"450\" /></span>" + "</a> "
					+ message);
		} catch (WebDriverException e) {
			log(e.getMessage());
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			log(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log(e.getMessage());
			e.printStackTrace();
		}

		if (isDebug()) {
			logDebug("Response time for takeScreenshot(): "
					+ (System.currentTimeMillis() - startTime));
		}
	}

	/** Debug mode */
	protected static boolean debug = false;

	/**
	 * Indicates if this test is being executed in debug mode.
	 * 
	 * @return {@code true} if this test is being executed in debug mode;
	 *         {@code false} otherwise
	 */
	protected static boolean isDebug() {
		return debug;
	}

	/**
	 * Gets the page title from the specified {@link BasePage}.
	 * 
	 * @param page
	 *            {@link BasePage} instance
	 */
	protected String getPageTitle(BasePage page) {
		String title = "";
		WebElement titleElement = null;
		long startTime = System.currentTimeMillis();

		if (isInsidePopupFrame()) {
			// page is inside the goto popup frame
			titleElement = page
					.getElementByCssSelector("div[class='popupInfoareaScreenTitle']");

			if (isDebug()) {
				logDebug("Response time for getPageTitle()->getElementByCssSelector()[IFRAME]: "
						+ (System.currentTimeMillis() - startTime));
			}
		} else {
			// page is in the main window
			titleElement = page
					.getElementByCssSelector("div[class='infoareaScreenTitle']");

			if (isDebug()) {
				logDebug("Response time for getPageTitle()->getElementByCssSelector()[MAIN WINDOW]: "
						+ (System.currentTimeMillis() - startTime));
			}
		}

		if (titleElement != null) {
			title = titleElement.getText();

			// remove the LendFast build number from the page title
			int indexOfLastLineFeed = title.lastIndexOf(0x0A);
			if (indexOfLastLineFeed > 0) {
				title = title.substring(0, indexOfLastLineFeed);
			}

			title = "- " + title;
		}

		return title;
	}

	/** Current page is in the popup frame. */
	private boolean insidePopupFrame = false;

	protected boolean isInsidePopupFrame() {
		return insidePopupFrame;
	}

	/**
	 * Returns the screenshot path for this test scenario.
	 * 
	 * @return screenshot path
	 */
	protected String getScreenshotPath() {
		File dir = null;

		if (screenshotPath == null) {
			String testOutputPath = System.getProperty("testReportDirectory");
			log("TestNG output folder: " + testOutputPath,
					DEBUG_VERBOSITY_LEVEL);

			if (testOutputPath != null) {
				dir = new File(testOutputPath);

				if (dir != null && !dir.exists()) {
					dir.mkdir();
				}

				screenshotPath = testOutputPath + "/screenshots";

				dir = new File(screenshotPath);

				if (dir != null && !dir.exists()) {
					dir.mkdir();
				}

				screenshotPath = screenshotPath + "/"
						+ this.getClass().getSimpleName();
			} else {
				screenshotPath = SCREENSHOT_PATH;
			}

			dir = new File(screenshotPath);

			if (dir != null && !dir.exists()) {
				dir.mkdir();
			}

			log("Screenshot path for this test: " + screenshotPath,
					DEBUG_VERBOSITY_LEVEL);
		}

		return screenshotPath;
	}

	/**
	 * Returns the screenshot path for the ApplicationForm Class
	 * 
	 * @return screenshot path
	 */
	protected String getPath() {
		screenshotBuilderPath = screenshotPath + "/"
				+ this.getClass().getSimpleName();
		return screenshotBuilderPath;
	}

	/**
	 * Automatically opens up the test report folder after the test
	 * 
	 * 
	 * @throws IOException
	 */
	protected void openTestReport() throws IOException {
		Desktop desktop = null;
		desktop = Desktop.getDesktop();
		this.reporttime = System.getProperty("reporttime");
		this.testSuiteName = System.getProperty("testSuiteName");
		File file = new File("C:\\workspace\\qaautomation\\test-reports\\"
				+ this.reporttime + "-" + this.testSuiteName);
		desktop.open(file);
	}

	/**
	 * @return today's date in MM/dd/yyyy format
	 */
	protected String todaysDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);

		// while (cal.get(Calendar.DAY_OF_WEEK) == (Calendar.SATURDAY |
		// Calendar.SUNDAY))
		while ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
			cal.add(Calendar.DATE, 1);
		}
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	protected String todaysDate2() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);

		// while (cal.get(Calendar.DAY_OF_WEEK) == (Calendar.SATURDAY |
		// Calendar.SUNDAY))
		while ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
			cal.add(Calendar.DATE, 1);
		}
		return dateFormat2.format(Calendar.getInstance().getTime());
	}

	protected String todaysDate_wordFormat() {
		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);

		// while (cal.get(Calendar.DAY_OF_WEEK) == (Calendar.SATURDAY |
		// Calendar.SUNDAY))
		while ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
			cal.add(Calendar.DATE, 1);
		}
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	/**
	 * @return yesterday's date in dd/MM/yyyy format
	 */
	protected String yesterdaysDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);

		return dateFormat.format(cal.getTime());
	}

	/**
	 * @return get the year last year in yyyy format
	 */
	protected String todaysMonth() {
		DateFormat dateFormat = new SimpleDateFormat("MM");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

	/**
	 * @return get the year last year in yyyy format
	 */
	protected String previousYear() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);

		return dateFormat.format(cal.getTime());
	}

	/**
	 * @return get the year last year in yyyy format
	 */
	protected String futureYear() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);

		return dateFormat.format(cal.getTime());
	}

	/**
	 * @return get the defined previous year in yyyy format
	 * @param int pyear - the year/s you want to deduct from the current year
	 */
	protected String previousYear(int pyear) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -pyear);

		return dateFormat.format(cal.getTime());
	}

	/**
	 * @return tomorrow's date in dd/MM/yyyy format
	 */
	protected String todaysDatePlus(int futuredate) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, futuredate);

		// while (cal.get(Calendar.DAY_OF_WEEK) == (Calendar.SATURDAY |
		// Calendar.SUNDAY))
		while ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
			cal.add(Calendar.DATE, 1);
		}

		// cal.getTime().equals(Calendar.DATE )
		return dateFormat.format(cal.getTime());
	}

	protected String todaysDatePlus2(int futuredate) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, futuredate);

		// while (cal.get(Calendar.DAY_OF_WEEK) == (Calendar.SATURDAY |
		// Calendar.SUNDAY))
		while ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
			cal.add(Calendar.DATE, 1);
		}

		// cal.getTime().equals(Calendar.DATE )
		return dateFormat2.format(cal.getTime());
	}

	/**
	 * @return tomorrow's date in dd/MM/yyyy format
	 */
	protected String tomorrowsDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);

		// while (cal.get(Calendar.DAY_OF_WEEK) == (Calendar.SATURDAY |
		// Calendar.SUNDAY))
		while ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
			cal.add(Calendar.DATE, 1);
		}

		// cal.getTime().equals(Calendar.DATE )
		return dateFormat.format(cal.getTime());
	}

	protected String tomorrowsDate2() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);

		// while (cal.get(Calendar.DAY_OF_WEEK) == (Calendar.SATURDAY |
		// Calendar.SUNDAY))
		while ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
			cal.add(Calendar.DATE, 1);
		}

		// cal.getTime().equals(Calendar.DATE )
		return dateFormat2.format(cal.getTime());
	}

	protected String urgentDate(String varDate) throws ParseException {

		Calendar cal = Calendar.getInstance();
		String cur = dateFormat.format(cal.getTime());

		Date date = dateFormat.parse(varDate);
		Date current = dateFormat.parse(cur);

		if (date.before(current) | date.equals(current)) {
			cal.setTime(current);
			cal.add(Calendar.DATE, 1);
			while ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
					| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
				cal.add(Calendar.DATE, 1);
			}
			return dateFormat.format(cal.getTime());

		} else {
			cal.setTime(date);
			cal.add(Calendar.DATE, -1);
			while ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
					| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
				cal.add(Calendar.DATE, -1);
			}
			return dateFormat.format(cal.getTime());
		}
	}

	protected String nonUrgentDate(String varDate) throws ParseException {

		Calendar cal = Calendar.getInstance();
		String cur = dateFormat.format(cal.getTime());

		Date date = dateFormat.parse(varDate);
		Date current = dateFormat.parse(cur);

		if (date.before(current) | date.equals(current)) {
			cal.setTime(current);
			while ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
					| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
				cal.add(Calendar.DATE, 1);
			}
			return dateFormat.format(cal.getTime());

		} else {
			cal.setTime(date);
			cal.add(Calendar.DATE, 1);
			while ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
					| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
				cal.add(Calendar.DATE, 1);
			}
			return dateFormat.format(cal.getTime());
		}
	}

	protected String restartDate(String varDate) throws ParseException {

		Calendar cal = Calendar.getInstance();
		Date date = dateFormat.parse(varDate);

		cal.setTime(date);
		cal.add(Calendar.DATE, 3);
		while ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
			cal.add(Calendar.DATE, 3);
		}
		return dateFormat.format(cal.getTime());
	}

	/**
	 * @return a future date which falls on a weekend
	 */
	protected String aWeekEndDate() {
		Calendar cal = Calendar.getInstance();

		while (cal.get(Calendar.DAY_OF_WEEK) != (Calendar.SATURDAY | Calendar.SUNDAY)) {
			cal.add(Calendar.DATE, 1);
		}

		return dateFormat.format(cal.getTime());
	}

	/**
	 * This function is created to check error message on LFO for ANZ which
	 * relates to settlement date must not exceed 6 months in the future
	 * 
	 * @return a date plus one year of todays date
	 */
	protected String dateAfterAYear() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);

		// while (cal.get(Calendar.DAY_OF_WEEK) == (Calendar.SATURDAY |
		// Calendar.SUNDAY))
		while ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
			cal.add(Calendar.DATE, 1);
		}

		return dateFormat.format(cal.getTime());

	}

	/**
	 * This method will get the "value" attribute of the web element and print
	 * it. This is not used for dropdown elements.
	 * 
	 * @param locator
	 * @param locatorType
	 * @author lenard.g.magpantay
	 */

	public String getTextinElement(String locator, String locatorType) {

		WebDriverWait elemWait = new WebDriverWait(driver, ELEMENT_APPEAR);

		String text = "";

		try {
			if (locatorType.equalsIgnoreCase("id")) {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.id(locator)));
				text = driver.findElement(By.id(locator)).getText();
			}

			else if (locatorType.equalsIgnoreCase("class")) {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.className(locator)));
				text = driver.findElement(By.className(locator)).getText();

			}

			else if (locatorType.equalsIgnoreCase("css")) {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.cssSelector(locator)));
				text = driver.findElement(By.cssSelector(locator)).getText();

			}

			else if (locatorType.equalsIgnoreCase("name")) {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.name(locator)));
				text = driver.findElement(By.name(locator)).getText();

			}

			else {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.xpath(locator)));
				text = driver.findElement(By.xpath(locator)).getText();
			}

			// element.getText();
			// System.out.println("The locator "+locator+" has a value of:" +
			// element.getAttribute("value"));
			// log("The "+locator+" has a value/s of:" +
			// element.getAttribute("value"));

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return text;
	}

	/**
	 * This method will get the "value" attribute of the web element and print
	 * it. This is not used for dropdown elements.
	 * 
	 * @param locator
	 * @param locatorType
	 * @author lenard.g.magpantay
	 */

	public String getValueinElement(String locator, String locatorType) {

		WebElement element;
		WebDriverWait elemWait = new WebDriverWait(driver, ELEMENT_APPEAR);
		String text = "";

		try {
			if (locatorType.equalsIgnoreCase("id")) {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.id(locator)));
				element = driver.findElement(By.id(locator));
			}

			else if (locatorType.equalsIgnoreCase("class")) {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.className(locator)));
				element = driver.findElement(By.className(locator));

			}

			else if (locatorType.equalsIgnoreCase("css")) {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.cssSelector(locator)));
				element = driver.findElement(By.cssSelector(locator));

			}

			else if (locatorType.equalsIgnoreCase("name")) {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.name(locator)));
				element = driver.findElement(By.name(locator));

			}

			else {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.xpath(locator)));
				element = driver.findElement(By.xpath(locator));
			}

			text = element.getAttribute("value");

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return text;
	}

	/**
	 * This method will get the "value" attribute of the dropdown element and
	 * print it.
	 * 
	 * @param locator
	 * @param locatorType
	 * @author lenard.g.magpantay
	 */

	public void getTextinDropdown(String locator, String locatorType) {
		WebElement select;
		WebDriverWait elemWait = new WebDriverWait(driver, ELEMENT_APPEAR);

		try {
			if (locatorType.equalsIgnoreCase("id")) {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.id(locator)));
				select = driver.findElement(By.id(locator));
				System.out.println("The locator " + locator
						+ "have the following values:");
			}

			else if (locatorType.equalsIgnoreCase("class")) {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.className(locator)));
				select = driver.findElement(By.className(locator));
				System.out.println("The locator " + locator
						+ "have the following values:");

			}

			else if (locatorType.equalsIgnoreCase("css")) {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.cssSelector(locator)));
				select = driver.findElement(By.cssSelector(locator));
				System.out.println("The locator " + locator
						+ "have the following values:");

			}

			else if (locatorType.equalsIgnoreCase("name")) {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.name(locator)));
				select = driver.findElement(By.name(locator));
				System.out.println("The locator " + locator
						+ "have the following values:");

			}

			else {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.xpath(locator)));
				select = driver.findElement(By.xpath(locator));
				System.out.println("The locator " + locator
						+ "have the following values:");
			}
			List<WebElement> allOptions = select.findElements(By
					.tagName("option"));

			for (WebElement element : allOptions) {

				String var2 = select.getText();
				System.out.println(var2);

			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	// List of Assertions and verification methods

	/**
	 * Is element in the page?
	 * 
	 * @param by
	 * @return
	 */
	public boolean is_element_seen(String by) {
		try {
			driver.findElement(By.id(by));
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public boolean is_element_visible(String locator, String locType) {
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

	/**
	 * waits for an element's presence (to aid some assertion calls)
	 * 
	 * @param locator
	 *            - locator of the element that needs to be waited.
	 * @param loctype
	 *            - is it an xpath or an id?
	 */
	public void waitForElement(String locator, String loctype) {
		WebDriverWait elemWait = new WebDriverWait(driver, ELEMENT_APPEAR);
		if (loctype.equalsIgnoreCase("xpath")) {
			elemWait.until(ExpectedConditions.presenceOfElementLocated(By
					.xpath(locator)));
		} else if (loctype.equalsIgnoreCase("id")) {
			elemWait.until(ExpectedConditions.presenceOfElementLocated(By
					.id(locator)));
		} else if (loctype.equalsIgnoreCase("name")) {
			elemWait.until(ExpectedConditions.presenceOfElementLocated(By
					.name(locator)));
		}

		else if (loctype.equalsIgnoreCase("css")) {
			elemWait.until(ExpectedConditions.presenceOfElementLocated(By
					.cssSelector(locator)));
		} else {
			elemWait.until(ExpectedConditions.presenceOfElementLocated(By
					.className(locator)));
		}
	}

	/**
	 * waits for an element to be clickable (to aid some assertion calls)
	 * 
	 * @param locator
	 *            - locator of the element that needs to be waited.
	 * @param loctype
	 *            - is it an xpath or an id?
	 */
	public void waitForClickableElement(String locator, String loctype) {
		WebDriverWait elemWait = new WebDriverWait(driver, 180);
		if (loctype.equalsIgnoreCase("class")) {
			elemWait.until(ExpectedConditions.elementToBeClickable(By
					.className(locator)));
		} else if (loctype.equalsIgnoreCase("id")) {
			elemWait.until(ExpectedConditions.elementToBeClickable(By
					.id(locator)));
		} else if (loctype.equalsIgnoreCase("name")) {
			elemWait.until(ExpectedConditions.elementToBeClickable(By
					.name(locator)));
		}

		else if (loctype.equalsIgnoreCase("css")) {
			elemWait.until(ExpectedConditions.elementToBeClickable(By
					.cssSelector(locator)));
		} else if (loctype.equalsIgnoreCase("link")) {
			elemWait.until(ExpectedConditions.elementToBeClickable(By
					.linkText(locator)));
		} else {
			elemWait.until(ExpectedConditions.elementToBeClickable(By
					.xpath(locator)));
		}
	}

	public void WaitForPageToLoad() {

	}

	/**
	 * Is element active or not?
	 * 
	 * @param by
	 * @param yesOrno
	 */
	public void is_element_enabled(String by, String yesOrno) {
		if (yesOrno.equalsIgnoreCase("enabled")) {
			Assert.assertTrue(webElementToCheck(by, "present").isEnabled());
		} else if (yesOrno.equalsIgnoreCase("notenabled")) {
			Assert.assertFalse(webElementToCheck(by, "present").isEnabled());
		}
	}

	/** New version of assertions */

	/**
	 * 06/23/2014: This function returns a particular WebElement represented by
	 * the locator provided. Used for assertions, identifies if the element is
	 * based on css, xpath or ID. Note that this function adds an explicit wait
	 * to the element, times out after 20 seconds and the element is still not
	 * found on the page's DOM
	 * 
	 * @param locator
	 *            - String representation of the locator used.
	 * @param presentNotPresent
	 *            - depends on the requirement.
	 * @return - returns WebElement
	 * @author clarenciotan
	 */
	private WebElement webElementToCheck(String locator,
			String presentNotPresent) {
		if (locator.startsWith("//")) {
			if (presentNotPresent.equalsIgnoreCase("present")) {
				return new WebDriverWait(driver, ELEMENT_APPEAR)
						.until(ExpectedConditions.presenceOfElementLocated(By
								.xpath(locator)));
			} else
				return driver.findElement(By.xpath(locator));
		}

		else if (locator.startsWith("span.") || locator.startsWith("td.")
				|| locator.startsWith("div[") || locator.startsWith("label")
				|| locator.startsWith("span[") || locator.startsWith("div.")
				|| locator.startsWith("tbody") || locator.startsWith("td:")
				|| locator.startsWith("b.ng") || locator.startsWith("small")
				|| locator.startsWith("dd") || locator.startsWith("label")
				|| locator.startsWith("p.") || locator.startsWith("form")
				|| locator.startsWith("h2.") || locator.startsWith("h1.")
				|| locator.startsWith("div.") || locator.startsWith("li.")
				|| locator.startsWith("dt") || locator.startsWith("input[")
				|| locator.startsWith("td[") || locator.startsWith("div[")
				|| locator.startsWith("table[") || locator.startsWith("tr[")
				|| locator.startsWith("tr:") || locator.startsWith("th:")) {
			if (presentNotPresent.equalsIgnoreCase("present")) {
				return new WebDriverWait(driver, ELEMENT_APPEAR)
						.until(ExpectedConditions.presenceOfElementLocated(By
								.cssSelector(locator)));
			} else
				return driver.findElement(By.cssSelector(locator));
		}

		else if (locator.startsWith("expenseInfo")) {
			if (presentNotPresent.equalsIgnoreCase("present")) {
				return new WebDriverWait(driver, ELEMENT_APPEAR)
						.until(ExpectedConditions.presenceOfElementLocated(By
								.className(locator)));
			} else
				return driver.findElement(By.className(locator));
		}

		else if (locator.startsWith("tfn")
				|| locator.equalsIgnoreCase("findOutMore")
				|| locator.equalsIgnoreCase("contBtn")
				|| locator.equalsIgnoreCase("email")
				|| locator.equalsIgnoreCase("firstName")
				|| locator.equalsIgnoreCase("surname")
				|| locator.equalsIgnoreCase("branchEditBtn")
				|| locator.equalsIgnoreCase("titleOption")
				|| locator.startsWith("mailing")
				|| locator.startsWith("medicare")
				|| locator.startsWith("driver")
				|| locator.startsWith("passport")
				|| locator.startsWith("gender") || locator.startsWith("branch")) {
			if (presentNotPresent.equalsIgnoreCase("present")) {
				return new WebDriverWait(driver, ELEMENT_APPEAR)
						.until(ExpectedConditions.presenceOfElementLocated(By
								.name(locator)));
			} else
				return driver.findElement(By.name(locator));
		} else if (presentNotPresent.equalsIgnoreCase("present")) {
			return new WebDriverWait(driver, ELEMENT_APPEAR)
					.until(ExpectedConditions.presenceOfElementLocated(By
							.id(locator)));
		} else
			return driver.findElement(By.id(locator));
	}

	/**
	 * this function details the styles needed for the assertion report.
	 */
	public void testReportFormat() {
		log("<style>" + ".testRep, .testRep td, .testRep tr" + "{"
				+ "border-style:ridge; border-collapse:collapse;" + "}"
				+ ".testRep th" + "{"
				+ "background-color:#E4BDEE; border-style:ridge;" + "}"
				+ " .testRep td" + "{" + "text-align:left;" + "}"
				+ ".testRep th" + "{" + "color:#005b96; text-align:left" + "}"
				+ ".tabItems th, .tabItems td" + "{" + "color:black;" + "}"
				+ " .resPass" + "{"
				+ "color:#228B22; font-weight:bold; background-color:#B5FCAB;"
				+ "}" + " .resFail" + "{"
				+ "color:#DC143C; font-weight:bold; background-color:#FC9E9E;"
				+ "}" + " .logsize" + "{" + "font-size:11px;" + "}"
				+ "</style>");
	}

	public void startTableRep(String tableTitle) {
		log("<table class=\"testRep\">" + "<tr class=\"testRep\">"
				+ "<th class=\"testRep\" colspan=\"6\">" + tableTitle + "</th>"
				+ "</tr>" + "<tr class=\"tabItems\">"
				+ "<th class=\"tabItems\">Field Name</th>"
				+ "<th class=\"tabItems\">Locator</th>"
				+ "<th class=\"tabItems\">Assertion Type</th>"
				+ "<th class=\"tabItems\">Expected value</th>"
				+ "<th class=\"tabItems\">Result</th>"
				+ "<th class=\"tabItems\">Screenshot</th>" + "</tr>");
	}

	public void endTableRep() {
		log("</table>");
	}

	/**
	 * This method will validate a text if it's present in a specific web
	 * element in page.
	 * 
	 * @param locator
	 * @apram valueToCheck
	 * 
	 * */
	public void assertTextInElementPresent(String locator, String valueToCheck)
			throws Exception {
		verify_Text_In_Element(valueToCheck, "present", locator, null,
				"assertTextInElementPresent");
	}

	/**
	 * This method will validate a text if it's present in a specific web
	 * element in page. Data will be provided from the Excel spreadsheet.
	 * 
	 * @param locator
	 * @apram valueToCheck
	 * 
	 * */

	public void assertTextInElementPresent(String locator, int colNum)
			throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Text_In_Element(valueToCheck, "present", locator, null,
				"assertTextInElementPresent");
	}

	/**
	 * This method will validate a text if it's not present in a specific web
	 * element in page.
	 * 
	 * @param locator
	 * @apram valueToCheck
	 * 
	 * */
	public void assertTextInElementNotPresent(String locator,
			String valueToCheck) throws Exception {
		verify_Text_In_Element(valueToCheck, "notpresent", locator, null,
				"assertFieldNameNotPresent");
	}

	/**
	 * This method will validate a text if it's not present in a specific web
	 * element in page. Data will be provided from the Excel spreadsheet.
	 * 
	 * @param locator
	 * @apram valueToCheck
	 * 
	 * */

	public void assertTextInElementNotPresent(String locator, int colNum)
			throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Text_In_Element(valueToCheck, "notpresent", locator, null,
				"assertTextInElementNotPresent");
	}

	/**
	 * This method will validate the VALUE attribute of a web element if it's
	 * present in the page.
	 * 
	 * @param locator
	 * @param valueToCheck
	 * @param value
	 *            valueLabel
	 * 
	 * */
	public void assertFieldValuePresent(String locator, String valueToCheck,
			String valueLabel) throws Exception {
		verify_Text_In_Element(valueToCheck, "present", locator, valueLabel,
				"assertFieldValuePresent");
	}

	/**
	 * This method will validate the VALUE attribute of a web element if it's
	 * present in the page. Data will be provided from the Excel spreadsheet.
	 * 
	 * @param locator
	 * @param valueToCheck
	 * @param value
	 *            valueLabel
	 * 
	 * */
	public void assertFieldValuePresent(String locator, int colNum,
			String valueLabel) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Text_In_Element(valueToCheck, "present", locator, valueLabel,
				"assertFieldValuePresent");
	}

	/**
	 * This method will validate the VALUE attribute of a web element if it's
	 * not present in the page.
	 * 
	 * @param locator
	 * @param valueToCheck
	 * @param value
	 *            valueLabel
	 * 
	 * */
	public void assertFieldValueNotPresent(String locator, String valueToCheck,
			String valueLabel) throws Exception {
		verify_Text_In_Element(valueToCheck, "notpresent", locator, valueLabel,
				"assertFieldValueNotPresent");
	}

	/**
	 * This method will validate the VALUE attribute of a web element if it's
	 * not present in the page. Data will be provided from the Excel
	 * spreadsheet.
	 * 
	 * @param locator
	 * @param valueToCheck
	 * @param value
	 *            valueLabel
	 * 
	 * */
	public void assertFieldValueNotPresent(String locator, int colNum,
			String valueLabel) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Text_In_Element(valueToCheck, "notpresent", locator, valueLabel,
				"assertFieldValueNotPresent");
	}

	/**
	 * This method will validate the VALUE attribute of a web element if it's
	 * present in the page.
	 * 
	 * @param locator
	 * @param valueToCheck
	 * @param locatorLabel
	 * 
	 * */
	public void assertFieldAttributeValuePresent(String locator,
			String valueToCheck, String locatorLabel) throws Exception {
		verify_Attribute_Of_Element(valueToCheck, "present", locator, "value",
				locatorLabel, "assertFieldAttributeValuePresent");
	}

	/**
	 * This method will validate the VALUE attribute of a web element if it's
	 * present in the page. Data will be provided from the Excel spreadsheet.
	 * 
	 * @param locator
	 * @param valueToCheck
	 * @param locatorLabel
	 * 
	 * */
	public void assertFieldAttributeValuePresent(String locator, int colNum,
			String locatorLabel) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Attribute_Of_Element(valueToCheck, "present", locator, "value",
				locatorLabel, "assertFieldAttributeValuePresent");
	}

	/**
	 * This method will validate the VALUE attribute of a web element if it's
	 * not present in the page.
	 * 
	 * @param valueToCheck
	 * @param locator
	 * @param locatorLabel
	 * 
	 * */
	public void assertFieldAttributeValueNotPresent(String locator,
			String valueToCheck, String locatorLabel) throws Exception {
		verify_Attribute_Of_Element(valueToCheck, "not", locator, "value",
				locatorLabel, "assertFieldAttributeValueNotPresent");
	}

	/**
	 * This method will validate the VALUE attribute of a web element if it's
	 * not present in the page. Data will be provided from the Excel
	 * spreadsheet.
	 * 
	 * @param valueToCheck
	 * @param locator
	 * @param locatorLabel
	 * 
	 * */
	public void assertFieldAttributeValueNotPresent(String locator, int colNum,
			String locatorLabel) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Attribute_Of_Element(valueToCheck, "not", locator, "value",
				locatorLabel, "assertFieldAttributeValueNotPresent");
	}

	/**
	 * This method will validate the STYLE attribute of a web element if it's
	 * present in the page.
	 * 
	 * 
	 * @param valueToCheck
	 * @param locator
	 * @param locatorLabel
	 * 
	 * */
	public void assertFieldAttributeStylePresent(String locator,
			String valueToCheck, String locatorLabel) throws Exception {
		verify_Attribute_Of_Element(valueToCheck, "present", locator, "style",
				locatorLabel, "assertFieldAttributeStylePresent");
	}

	/**
	 * This method will validate the STYLE attribute of a web element if it's
	 * present in the page. Data will be provided from the Excel spreadsheet.
	 * 
	 * @param valueToCheck
	 * @param locator
	 * @param locatorLabel
	 * 
	 * */
	public void assertFieldAttributeStylePresent(String locator, int colNum,
			String locatorLabel) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Attribute_Of_Element(valueToCheck, "present", locator, "style",
				locatorLabel, "assertFieldAttributeStylePresent");
	}

	/**
	 * This method will validate the STYLE attribute of a web element if it's
	 * not present in the page.
	 * 
	 * 
	 * @param valueToCheck
	 * @param locator
	 * @param locatorLabel
	 * 
	 * */
	public void assertFieldAttributeStyleNotPresent(String locator,
			String valueToCheck, String locatorLabel) throws Exception {
		verify_Attribute_Of_Element(valueToCheck, "not", locator, "style",
				locatorLabel, "assertFieldAttributeStyleNotPresent");
	}

	/**
	 * This method will validate the STYLE attribute of a web element if it's
	 * not present in the page. Data will be provided from the Excel
	 * spreadsheet.
	 * 
	 * @param valueToCheck
	 * @param locator
	 * @param locatorLabel
	 * 
	 * */
	public void assertFieldAttributeStyleNotPresent(String locator, int colNum,
			String locatorLabel) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Attribute_Of_Element(valueToCheck, "not", locator, "style",
				locatorLabel, "assertFieldAttributeStyleNotPresent");
	}

	/**
	 * This method will validate all of the values of a dropdown if it's present
	 * in the page.
	 * 
	 * 
	 * @param expectedValues
	 * @param locator
	 * @param dropdownLabel
	 * @throws Exception
	 * 
	 * */
	public void assertDropdownValues(String locator, String[] valueToCheck,
			String dropdownLabel) throws Exception {
		verify_AvailableSelectOptions(valueToCheck, locator, dropdownLabel);
	}

	/**
	 * This method will validate all of the values of a dropdown if it's present
	 * in the page. Data will be provided from the Excel spreadsheet.
	 * 
	 * @param expectedValues
	 * @param locator
	 * @param dropdownLabel
	 * @throws Exception
	 * 
	 * */
	public void assertDropdownValues(String locator, int colNum,
			String dropdownLabel) throws Exception {
		String cellData = getCellData(TestCaseRow, colNum);
		String[] valueToCheck = cellData.split("\",\"");
		verify_AvailableSelectOptions(valueToCheck, locator, dropdownLabel);
	}

	/**
	 * This method will validate the default value of a specific web element in
	 * page.
	 * 
	 * @param locator
	 * @param valueToCheck
	 * 
	 * */
	public void assertDefaultValue(String locator, String valueToCheck)
			throws Exception {
		verify_Text_In_Element(valueToCheck, "present", locator, null,
				"assertDefaultValue");
	}

	/**
	 * This method will validate the default value of a specific web element in
	 * page. Data will be provided from the Excel spreadsheet.
	 * 
	 * @param locator
	 * @param valueToCheck
	 * 
	 * */
	public void assertDefaultValue(String locator, int colNum) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Text_In_Element(valueToCheck, "present", locator, null,
				"assertDefaultValue");
	}

	/**
	 * This method will validate if a text is present on the whole page.
	 * 
	 * 
	 * @param textToCheck
	 * 
	 * */
	public void assertTextPresentInPage(String valueToCheck) throws Exception {
		verify_Text_In_Pagesource("found", valueToCheck, null);
	}

	/**
	 * This method will validate if a text is present on the whole page. Data
	 * will be provided from the Excel spreadsheet.
	 * 
	 * @param textToCheck
	 * 
	 * */
	public void assertTextPresentInPage(int colNum) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Text_In_Pagesource("found", valueToCheck, null);
	}

	/**
	 * This method will validate if a text is not present on the whole page.
	 * 
	 * @param textToCheck
	 * 
	 * */
	public void assertTextNotPresentInPage(String valueToCheck)
			throws Exception {
		verify_Text_In_Pagesource("notfound", valueToCheck, null);
	}

	/**
	 * This method will validate if a text is not present on the whole page.
	 * Data will be provided from the Excel spreadsheet.
	 * 
	 * @param textToCheck
	 * 
	 * */
	public void assertTextNotPresentInPage(int colNum) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Text_In_Pagesource("notfound", valueToCheck, null);
	}

	/**
	 * This method will validate if a web element is enabled (or not disabled)
	 * in the page.
	 * 
	 * @param locator
	 * @param fieldLabel
	 * 
	 * */
	public void assertFieldEditable(String locator, String fieldLabel)
			throws Exception {
		verify_Element_Enabled(locator, "enabled", fieldLabel);
	}

	/**
	 * This method will validate if a web element is enabled (or not disabled)
	 * in the page. Data will be provided from the Excel spreadsheet.
	 * 
	 * @param locator
	 * @param fieldLabel
	 * 
	 * */
	public void assertFieldEditable(int colNum, String fieldLabel)
			throws Exception {
		String locator = getCellData(TestCaseRow, colNum);
		verify_Element_Enabled(locator, "enabled", fieldLabel);
	}

	/**
	 * This method will validate if a web element is disabled (or not enabled)
	 * in the page.
	 * 
	 * @param locator
	 * @param fieldLabel
	 * 
	 * */
	public void assertFieldNotEditable(String locator, String fieldLabel)
			throws Exception {
		verify_Element_Enabled(locator, "notenabled", fieldLabel);
	}

	/**
	 * This method will validate if a web element is disabled (or not enabled)
	 * in the page. Data will be provided from the Excel spreadsheet.
	 * 
	 * @param locator
	 * @param fieldLabel
	 * 
	 * */
	public void assertFieldNotEditable(int colNum, String fieldLabel)
			throws Exception {
		String locator = getCellData(TestCaseRow, colNum);
		verify_Element_Enabled(locator, "notenabled", fieldLabel);
	}

	/**
	 * This method will validate if a web element is present in the page.
	 * 
	 * @param locator
	 * @param elementLabel
	 * 
	 * */
	public void assertPageElementPresent(String locator, String elementLabel)
			throws Exception {
		verify_Element_In_Page(locator, "present", elementLabel);
	}

	/**
	 * This method will validate if a web element is present in the page. Data
	 * will be provided from the Excel spreadsheet.
	 * 
	 * @param locator
	 * @param elementLabel
	 * 
	 * */
	public void assertPageElementPresent(int colNum, String elementLabel)
			throws Exception {
		String locator = getCellData(TestCaseRow, colNum);
		verify_Element_In_Page(locator, "present", elementLabel);
	}

	/**
	 * This method will validate if a web element is not present in the page.
	 * 
	 * @param locator
	 * @param elementLabel
	 * 
	 * */
	public void assertPageElementNotPresent(String locator, String elementLabel)
			throws Exception {
		verify_Element_In_Page(locator, "notpresent", elementLabel);
	}

	/**
	 * This method will validate if a web element is not present in the page.
	 * Data will be provided from the Excel spreadsheet.
	 * 
	 * @param locator
	 * @param elementLabel
	 * 
	 * */
	public void assertPageElementNotPresent(int colNum, String elementLabel)
			throws Exception {
		String locator = getCellData(TestCaseRow, colNum);
		verify_Element_In_Page(locator, "notpresent", elementLabel);
	}

	/**
	 * This method will validate the TITLE attribute of a web element if it's
	 * present in the page.
	 * 
	 * @param valueToCheck
	 * @param locator
	 * @param locatorLabel
	 * 
	 * */
	public void assertAttributeTitlePresent(String locator,
			String valueToCheck, String locatorLabel) throws Exception {
		verify_Attribute_Of_Element(valueToCheck, "present", locator, "title",
				locatorLabel, "assertAttributeTitlePresent");
	}

	/**
	 * This method will validate the TITLE attribute of a web element if it's
	 * present in the page. ata will be provided from the Excel spreadsheet.
	 * 
	 * @param valueToCheck
	 * @param locator
	 * @param locatorLabel
	 * 
	 * */
	public void assertAttributeTitlePresent(String locator, int colNum,
			String locatorLabel) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Attribute_Of_Element(valueToCheck, "present", locator, "title",
				locatorLabel, "assertAttributeTitlePresent");
	}

	// The list of methods below are used in the new version of assertions
	// These should not be directly used in the tests

	public void verify_Text_In_Element(String valueToCheck,
			String presentNotPresent, String locator, String locatorLabel,
			String assertLabel) throws Exception {

		try {
			if (presentNotPresent.equalsIgnoreCase("present")) {
				System.out
						.println("\tASSERT Check: " + "\"" + valueToCheck
								+ "\"" + " should BE found in " + "\""
								+ locator + "\"");
				text_in_element_check(locator, presentNotPresent, valueToCheck);
				System.out.println("\t-> PASSED: " + "\"" + valueToCheck + "\""
						+ " found.");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>" + "<td class=\"logsize\">" + assertLabel
						+ "</td>" + "<td class=\"logsize\">" + valueToCheck
						+ "</td>" + "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			} else if (presentNotPresent.equalsIgnoreCase("notPresent")) {
				System.out.println("\tASSERT Check: " + "\"" + valueToCheck
						+ "\"" + " should NOT BE found in " + "\"" + locator
						+ "\"");
				text_in_element_check(locator, presentNotPresent, valueToCheck);
				System.out.println("\t-> PASSED: " + "\"" + valueToCheck + "\""
						+ " NOT found.");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>" + "<td class=\"logsize\">" + assertLabel
						+ "</td>" + "<td class=\"logsize\">" + valueToCheck
						+ "</td>" + "<td class=\"resPass\">PASSED</td>"
						+ "<td>");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			}
		} catch (AssertionError e) {
			if (presentNotPresent.equalsIgnoreCase("present")) {
				System.out.println("\t-> FAILED: " + "\"" + valueToCheck + "\""
						+ " NOT FOUND in the locator");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>" + "<td class=\"logsize\">" + assertLabel
						+ "</td>" + "<td class=\"logsize\">" + valueToCheck
						+ "</td>" + "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot(assertLabel, "error");
				tableRepLog("</td>" + "</tr>");
			} else if (presentNotPresent.equalsIgnoreCase("notPresent")) {
				System.out.println("\t-> FAILED: " + "\"" + valueToCheck + "\""
						+ " FOUND in the locator");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>" + "<td class=\"logsize\">" + assertLabel
						+ "</td>" + "<td class=\"logsize\">" + valueToCheck
						+ "</td>" + "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot(assertLabel, "error");
				tableRepLog("</td>" + "</tr>");
			}
		}
	}

	public void text_in_element_check(String locator, String presentNotPresent,
			String valueToCheck) throws Exception {
		if (presentNotPresent.equalsIgnoreCase("present")) {
			Assert.assertTrue(webElementToCheck(locator, "present").getText()
					.contains(valueToCheck));

		} else if (presentNotPresent.equalsIgnoreCase("notPresent")) {

			Assert.assertFalse(webElementToCheck(locator, "notPresent")
					.getText().contains(valueToCheck));
		}
	}

	public void verify_Element_In_Page(String locator,
			String presentNotPresent, String locatorLabel) // ok
			throws Exception {
		try {
			if (presentNotPresent.equalsIgnoreCase("present")) {
				System.out.println("\tASSERT Check: " + "\"" + locator + "\""
						+ " should BE found in the current page");
				Assert.assertTrue(is_element_seens(locator, "present"));
				System.out.println("\t-> PASSED: " + "\"" + locator + "\""
						+ " found in the current page");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>"
						+ "<td class=\"logsize\">assertPageElementPresent</td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			} else if (presentNotPresent.equalsIgnoreCase("notpresent")) {
				System.out.println("\tASSERT Check: " + "\"" + locator + "\""
						+ " should NOT BE found in the current page");
				Assert.assertFalse(is_element_seens(locator, "notpresent"));
				System.out.println("\t-> PASSED: " + "\"" + locator + "\""
						+ " NOT found in the current page");
				tableRepLog("<tr>"
						+ "<td class=\"logsize\">"
						+ locatorLabel
						+ "</td>"
						+ "<td class=\"logsize\">"
						+ locator
						+ "</td>"
						+ "<td class=\"logsize\">assertPageElementNotPresent</td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			}
		} catch (AssertionError e) {
			if (presentNotPresent.equalsIgnoreCase("present")) {
				System.out.println("-> FAILED: " + "\"" + locator + "\""
						+ " NOT FOUND in the current page");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>"
						+ "<td class=\"logsize\">assertPageElementPresent</td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot("assertElementPresent", "error");
				tableRepLog("</td>" + "</tr>");
			} else if (presentNotPresent.equalsIgnoreCase("notpresent")) {
				System.out.println("-> FAILED: " + "\"" + locator + "\""
						+ " FOUND in the current page");
				tableRepLog("<tr>"
						+ "<td class=\"logsize\">"
						+ locatorLabel
						+ "</td>"
						+ "<td class=\"logsize\">"
						+ locator
						+ "</td>"
						+ "<td class=\"logsize\">assertPageElementNotPresent</td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot("assertElementNotPresent", "error");
				tableRepLog("</td>" + "</tr>");
			}
		}
	}

	/**
	 * This function checks if the element is present on a page currently being
	 * checked. If webElementToCheck throws NoSuchElementException or
	 * TimeoutException, it will be catched which means that the element is not
	 * seen on a page.
	 * 
	 * @param locator
	 *            - locator of the specified element being checked.
	 * @param presentNotPresent
	 *            - if the element is present or not.
	 * @return boolean - true; element is present, false otherwise
	 * @author clarenciotan
	 */
	public boolean is_element_seens(String locator, String presentNotPresent) {
		WebElement elementToCheck = null;

		if (presentNotPresent.equalsIgnoreCase("present")) {
			try {
				elementToCheck = webElementToCheck(locator, "present");
			} catch (NoSuchElementException e) {
				return false;
			} catch (TimeoutException e) {
				return false;
			}
		} else {
			try {
				elementToCheck = webElementToCheck(locator, "notPresent");
			} catch (NoSuchElementException e) {
				return false;
			} catch (TimeoutException e) {
				return false;
			}
		}

		// checking element presence on page:
		if (elementToCheck != null) {
			return true;
		} else
			return false;

	}

	public void verify_AvailableSelectOptions(String[] expected, String elemID,
			String locatorLabel) // ok
	{
		// added additional locator check for bendigo purposes - evert
		WebElement select;

		if (elemID.equalsIgnoreCase("titleOption")
				|| elemID.startsWith("contact") || elemID.startsWith("tfn")
				|| elemID.startsWith("tax")) {
			select = driver.findElement(By.name(elemID));
		} else if (elemID.startsWith("//")) {
			select = driver.findElement(By.xpath(elemID));
		} else {

			select = driver.findElement(By.id(elemID));
		}

		List<WebElement> allOptions = select.findElements(By.tagName("option"));
		try {

			if (expected.length != allOptions.size()) {
				System.out.println("\tIssue on number of elements...");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + elemID + "</td>"
						+ "<td class=\"logsize\">assertDropdownValues</td>"
						+ "<td class=\"logsize\">");
				StringBuilder sb = new StringBuilder().append(allOptions.get(0)
						.getText());
				for (int i = 1; i < allOptions.size(); i++) {
					sb.append("</br>").append(allOptions.get(i).getText());
				}
				tableRepLog("Element number MISMATCH warning! Actual:</br>");
				tableRepLog(sb.toString());
				tableRepLog("</td>" + "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\"></td>" + "</tr>");
				return;
			}

			for (int i = 0; i < expected.length; i++) {
				Assert.assertEquals(expected[i], allOptions.get(i).getText());
			}
			tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
					+ "</td>" + "<td class=\"logsize\">" + elemID + "</td>"
					+ "<td class=\"logsize\">assertDropdownValues</td>"
					+ "<td class=\"logsize\">");

			StringBuilder sb = new StringBuilder().append(expected[0])
					.append(" = ").append(allOptions.get(0).getText());
			for (int i = 1; i < expected.length; i++) {
				sb.append("</br>").append(expected[i]).append(" = ")
						.append(allOptions.get(i).getText());
			}
			tableRepLog(sb.toString());
			tableRepLog("</td>" + "<td class=\"resPass\">PASSED</td>"
					+ "<td class=\"logsize\">");
			// takeScreenshot();
			tableRepLog("</td>" + "</tr>");
		} catch (AssertionError e) {
			tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
					+ "</td>" + "<td class=\"logsize\">" + elemID + "</td>"
					+ "<td class=\"logsize\">assertDropdownValues</td>"
					+ "<td class=\"logsize\">");

			StringBuilder sb = new StringBuilder().append(expected[0])
					.append(" = ").append(allOptions.get(0).getText());
			for (int i = 1; i < expected.length; i++) {
				sb.append("</br>").append(expected[i]).append(" = ")
						.append(allOptions.get(i).getText());
			}
			tableRepLog(sb.toString());
			tableRepLog("</td>" + "<td class=\"resFail\">FAILED</td>"
					+ "<td class=\"logsize\">");
			takeScreenshot("assertDropdownValues", "error");
			tableRepLog("</td>" + "</tr>");
		}
	}

	public void verify_ExpectedOptions(String[] expected, String elemID,
			String locatorLabel) // ok
	{
		// added additional locator check for bendigo purposes - evert
		WebElement select;

		if (elemID.equalsIgnoreCase("titleOption")
				|| elemID.startsWith("contact") || elemID.startsWith("tfn")
				|| elemID.startsWith("tax")) {
			select = driver.findElement(By.name(elemID));
		} else if (elemID.startsWith("avaliablePaymentDateID")) {
			select = driver.findElement(By.id(elemID));
		} else {
			select = driver.findElement(By.xpath(elemID));
		}

		List<WebElement> allOptions = select.findElements(By.tagName("option"));
		try {
			for (int i = 0; i < expected.length; i++) {
				Assert.assertEquals(expected[i], allOptions.get(i).getText());
			}
			tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
					+ "</td>" + "<td class=\"logsize\">" + elemID + "</td>"
					+ "<td class=\"logsize\">assertDropdownValues</td>"
					+ "<td class=\"logsize\">");

			StringBuilder sb = new StringBuilder().append(expected[0])
					.append(" = ").append(allOptions.get(0).getText());
			for (int i = 1; i < expected.length; i++) {
				sb.append("</br>").append(expected[i]).append(" = ")
						.append(allOptions.get(i).getText());
			}
			tableRepLog(sb.toString());
			tableRepLog("</td>" + "<td class=\"resPass\">PASSED</td>"
					+ "<td class=\"logsize\">");
			// takeScreenshot();
			tableRepLog("</td>" + "</tr>");
		} catch (AssertionError e) {
			tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
					+ "</td>" + "<td class=\"logsize\">" + elemID + "</td>"
					+ "<td class=\"logsize\">assertDropdownValues</td>"
					+ "<td class=\"logsize\">");

			StringBuilder sb = new StringBuilder().append(expected[0])
					.append(" = ").append(allOptions.get(0).getText());
			for (int i = 1; i < expected.length; i++) {
				sb.append("</br>").append(expected[i]).append(" = ")
						.append(allOptions.get(i).getText());
			}
			tableRepLog(sb.toString());
			tableRepLog("</td>" + "<td class=\"resPass\">PASSED</td>"
					+ "<td class=\"logsize\">");
			takeScreenshot("assertDropdownValues", "error");
			tableRepLog("</td>" + "</tr>");
		}
	}

	public void verify_Attribute_Of_Element(
			String valueToCheck,
			String presentNotPresent, // ok
			String locator, String attributeName, String locatorLabel,
			String assertLabel) throws Exception {
		try {
			if (presentNotPresent.equalsIgnoreCase("present")) {
				System.out.println("\tASSERT Check: " + "\"" + valueToCheck
						+ "\"" + " should BE found in " + locator + " 's "
						+ attributeName + " attribute");
				attribute_correct_check(locator, presentNotPresent,
						attributeName, valueToCheck);
				System.out.println("\t-> PASSED: " + "\"" + valueToCheck + "\""
						+ " found.");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>" + "<td class=\"logsize\">" + assertLabel
						+ "</td>" + "<td class=\"logsize\">" + valueToCheck
						+ "</td>" + "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			} else if (presentNotPresent.equalsIgnoreCase("notpresent")) {
				System.out.println("\tASSERT Check: " + "\"" + valueToCheck
						+ "\"" + " should NOT BE found in " + locator + " 's "
						+ attributeName + " attribute");
				attribute_correct_check(locator, presentNotPresent,
						attributeName, valueToCheck);
				System.out.println("\t-> PASSED: " + "\"" + valueToCheck + "\""
						+ " NOT found.");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>" + "<td class=\"logsize\">" + assertLabel
						+ "</td>" + "<td class=\"logsize\">" + valueToCheck
						+ "</td>" + "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			}
		} catch (AssertionError e) {
			if (presentNotPresent.equalsIgnoreCase("present")) {
				System.out.println("\t-> FAILED: " + "\"" + valueToCheck + "\""
						+ " NOT FOUND in the element's indicated attribute");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>" + "<td class=\"logsize\">" + assertLabel
						+ "</td>" + "<td class=\"logsize\">" + valueToCheck
						+ "</td>" + "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot("assertValue", "error");
				tableRepLog("</td>" + "</tr>");
			} else if (presentNotPresent.equalsIgnoreCase("notpresent")) {
				System.out.println("\t-> FAILED: " + "\"" + valueToCheck + "\""
						+ " FOUND in the element's indicated attribute");
				tableRepLog("<tr class=\"testRep\">" + "<td class=\"logsize\">"
						+ locatorLabel + "</td>" + "<td class=\"logsize\">"
						+ locator + "</td>" + "<td class=\"logsize\">"
						+ assertLabel + "</td>" + "<td class=\"logsize\">"
						+ valueToCheck + "</td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot("assertNotValue", "error");
				tableRepLog("</td>" + "</tr>");
			}
		}
	}

	public void attribute_correct_check(String locator,
			String presentNotPresent, String attrib, String tocheck)
			throws Exception {
		if (presentNotPresent.equalsIgnoreCase("present")) {
			Assert.assertTrue(webElementToCheck(locator, "present")
					.getAttribute(attrib).contains(tocheck));

		} else if (presentNotPresent.equalsIgnoreCase("notpresent")) {
			Assert.assertFalse(webElementToCheck(locator, "notPresent")
					.getAttribute(attrib).contains(tocheck));

		}
	}

	public void verify_Element_Enabled(String locator, String isEnabled,
			String locatorLabel) // ok
			throws Exception {
		try {
			if (isEnabled.equalsIgnoreCase("enabled")) {
				System.out.println("\tASSERT Check: " + locator
						+ " should be ENABLED");
				is_element_enabled(locator, isEnabled);
				System.out.println("\t-> PASSED: " + locator + " IS ENABLED");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>"
						+ "<td class=\"logsize\">assertFieldEditable</td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			} else if (isEnabled.equalsIgnoreCase("notenabled")) {
				System.out.println("\tASSERT Check: " + locator
						+ " should NOT BE ENABLED");
				is_element_enabled(locator, isEnabled);
				System.out.println("\t-> PASSED: " + locator
						+ " is NOT ENABLED");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>"
						+ "<td class=\"logsize\">assertFieldNotEditable</td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			}
		} catch (AssertionError e) {
			if (isEnabled.equalsIgnoreCase("enabled")) {
				System.out.println("\t-> FAILED: The element WAS NOT enabled.");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>"
						+ "<td class=\"logsize\">assertFieldEditable</td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot("assertEditable", "error");
				tableRepLog("</td>" + "</tr>");
			} else if (isEnabled.equalsIgnoreCase("notenabled")) {
				System.out.println("\t-> FAILED: The element WAS enabled.");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>"
						+ "<td class=\"logsize\">assertFieldNotEditable</td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot("assertNotEditable", "error");
				tableRepLog("</td>" + "</tr>");
			}
		}
	}

	public void element_enabled_check(String by, String enabledNotEnabled) {
		if (enabledNotEnabled.equalsIgnoreCase("enabled")) {
			Assert.assertTrue(driver.findElement(By.id(by)).isEnabled());
		} else if (enabledNotEnabled.equalsIgnoreCase("not")) {
			Assert.assertFalse(driver.findElement(By.id(by)).isEnabled());
			Assert.assertNotEquals("actual", "expected");
		}
	}

	public void verify_Text_In_Pagesource(String foundNotFound,
			String valueToCheck, // ok
			String locatorLabel) throws Exception {
		try {
			if (foundNotFound.equalsIgnoreCase("found")) {
				System.out.println("\tASSERT Check: " + "\"" + valueToCheck
						+ "\"" + " text should BE found in the page");
				text_In_Source_Check(foundNotFound, valueToCheck);
				System.out.println("\t-> PASSED: " + "\"" + valueToCheck + "\""
						+ " found.");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\"></td>"
						+ "<td class=\"logsize\">TextInPage</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");

			} else if (foundNotFound.equalsIgnoreCase("notfound")) {
				System.out.println("\tASSERT Check: " + "\"" + valueToCheck
						+ "\"" + " text should NOT BE found in the page");
				text_In_Source_Check(foundNotFound, valueToCheck);
				System.out.println("\t-> PASSED: " + "\"" + valueToCheck + "\""
						+ " NOT found in the page.");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\"></td>"
						+ "<td class=\"logsize\">TextNotInPage</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			}
		} catch (AssertionError e) {
			if (foundNotFound.equalsIgnoreCase("found")) {
				System.out.println("\t-> FAILED: " + "\"" + valueToCheck + "\""
						+ " NOT FOUND in the page");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\"></td>"
						+ "<td class=\"logsize\">TextInPage</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot("assertElementInPage", "error");
				tableRepLog("</td>" + "</tr>");
			} else if (foundNotFound.equalsIgnoreCase("notfound")) {
				System.out.println("\t-> FAILED: " + "\"" + valueToCheck + "\""
						+ " FOUND in the page");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\"></td>"
						+ "<td class=\"logsize\">TextNotInPage</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot("assertElementNotInPage", "error");
				tableRepLog("</td>" + "</tr>");
			}
		}
	}

	public void text_In_Source_Check(String foundNotFound, String tocheck)
			throws Exception {
		if (foundNotFound.equalsIgnoreCase("found")) {
			Assert.assertTrue(driver.getPageSource().contains(tocheck));
		} else if (foundNotFound.equalsIgnoreCase("notfound")) {
			Assert.assertFalse(driver.getPageSource().contains(tocheck));
		}
	}

	public void assertTextInAlertPopup(String valueToCheck) throws Exception {

		verify_Text_In_Alert(valueToCheck, "found", "assertTextInAlertPopup");
	}

	public void assertTextInAlertPopup(int colNum) throws Exception {

		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Text_In_Alert(valueToCheck, "found", "assertTextInAlertPopup");
	}

	public void assertTextNotInAlertPopup(String valueToCheck) throws Exception {

		verify_Text_In_Alert(valueToCheck, "notfound",
				"assertTextNotInAlertPopup");
	}

	public void assertTextNotInAlertPopup(int colNum) throws Exception {

		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Text_In_Alert(valueToCheck, "notfound",
				"assertTextNotInAlertPopup");
	}

	public void assertTextInAlertPopupContains(String valueToCheck)
			throws Exception {
		verify_Text_In_Alert(valueToCheck, "contains",
				"assertTextInAlertPopupContains");
	}

	public void assertTextInAlertPopupContains(int colNum) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Text_In_Alert(valueToCheck, "contains",
				"assertTextInAlertPopupContains");
	}

	public void assertTextNotInAlertPopupContains(String valueToCheck)
			throws Exception {
		verify_Text_In_Alert(valueToCheck, "notcontains",
				"assertTextNotInAlertPopupContains");
	}

	public void assertTextNotInAlertPopupContains(int colNum) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Text_In_Alert(valueToCheck, "notcontains",
				"assertTextNotInAlertPopupContains");
	}

	public void verify_Text_In_Alert(String valueToCheck,
			String presentNotPresent, String assertLabel) throws Exception {

		try {
			if (presentNotPresent.equalsIgnoreCase("found")) {
				System.out.println("\tASSERT Check: " + "\"" + valueToCheck
						+ "\"" + " should BE found in the alert popup" + "\"");
				text_Alert_Check(presentNotPresent, valueToCheck);
				System.out.println("\t-> PASSED: " + "\"" + valueToCheck + "\""
						+ " found.");
				tableRepLog("<tr>" + "<td class=\"logsize\"> Alert Popup </td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"logsize\">" + assertLabel + "</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			} else if (presentNotPresent.equalsIgnoreCase("notfound")) {
				System.out.println("\tASSERT Check: " + "\"" + valueToCheck
						+ "\"" + " should NOT BE found in the alert popup"
						+ "\"");
				text_Alert_Check(presentNotPresent, valueToCheck);
				System.out.println("\t-> PASSED: " + "\"" + valueToCheck + "\""
						+ " NOT found.");
				tableRepLog("<tr>" + "<td class=\"logsize\"> Alert Popup </td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"logsize\">" + assertLabel + "</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resPass\">PASSED</td>" + "<td>");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			}

			// contains validation

			else if (presentNotPresent.equalsIgnoreCase("contains")) {
				System.out.println("\tASSERT Check: " + "\"" + valueToCheck
						+ "\"" + " should BE found in the alert popup" + "\"");
				text_Alert_Check(presentNotPresent, valueToCheck);
				System.out.println("\t-> PASSED: " + "\"" + valueToCheck + "\""
						+ " found.");
				tableRepLog("<tr>" + "<td class=\"logsize\"> Alert Popup </td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"logsize\">" + assertLabel + "</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			}

			else if (presentNotPresent.equalsIgnoreCase("notcontains")) {
				System.out.println("\tASSERT Check: " + "\"" + valueToCheck
						+ "\"" + " should NOT BE found in the alert popup"
						+ "\"");
				text_Alert_Check(presentNotPresent, valueToCheck);
				System.out.println("\t-> PASSED: " + "\"" + valueToCheck + "\""
						+ " NOT found.");
				tableRepLog("<tr>" + "<td class=\"logsize\"> Alert Popup </td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"logsize\">" + assertLabel + "</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resPass\">PASSED</td>" + "<td>");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			}
		} catch (AssertionError e) {
			if (presentNotPresent.equalsIgnoreCase("found")) {
				System.out.println("\t-> FAILED: " + "\"" + valueToCheck + "\""
						+ " NOT FOUND in the locator");
				tableRepLog("<tr>" + "<td class=\"logsize\"> Alert Popup </td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"logsize\">" + assertLabel + "</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot(assertLabel, "error");
				tableRepLog("</td>" + "</tr>");
			} else if (presentNotPresent.equalsIgnoreCase("notfound")) {
				System.out.println("\t-> FAILED: " + "\"" + valueToCheck + "\""
						+ " FOUND in the locator");
				tableRepLog("<tr>" + "<td class=\"logsize\"> Alert Popup </td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"logsize\">" + assertLabel + "</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot(assertLabel, "error");
				tableRepLog("</td>" + "</tr>");
			}

			// contains validation

			else if (presentNotPresent.equalsIgnoreCase("contains")) {
				System.out.println("\t-> FAILED: " + "\"" + valueToCheck + "\""
						+ " NOT FOUND in the locator");
				tableRepLog("<tr>" + "<td class=\"logsize\"> Alert Popup </td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"logsize\">" + assertLabel + "</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot(assertLabel, "error");
				tableRepLog("</td>" + "</tr>");
			} else if (presentNotPresent.equalsIgnoreCase("notcontains")) {
				System.out.println("\t-> FAILED: " + "\"" + valueToCheck + "\""
						+ " FOUND in the locator");
				tableRepLog("<tr>" + "<td class=\"logsize\"> Alert Popup </td>"
						+ "<td class=\"logsize\"></td>"
						+ "<td class=\"logsize\">" + assertLabel + "</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot(assertLabel, "error");
				tableRepLog("</td>" + "</tr>");
			}
		}
	}

	public void text_Alert_Check(String foundNotFound, String tocheck)
			throws Exception {
		try {

			if (foundNotFound.equalsIgnoreCase("found")) {
				Assert.assertTrue(getTextInAlertPopup().equals(tocheck));
			} else if (foundNotFound.equalsIgnoreCase("notfound")) {
				Assert.assertFalse(getTextInAlertPopup().equals(tocheck));

			} else if (foundNotFound.equalsIgnoreCase("contains")) {

				Assert.assertTrue(getTextInAlertPopup().contains(tocheck));

			} else if (foundNotFound.equalsIgnoreCase("notcontains")) {

				Assert.assertFalse(getTextInAlertPopup().contains(tocheck));
			}

		} catch (Exception e) {
			// exception handling
		}

	}

	public void highlightElement(String elemID, String elemType,
			WebDriver driver) {
		if (elemType.equalsIgnoreCase("xpath")) {
			WebElement element = driver.findElement(By.xpath(elemID));
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].style.outline = 'red solid 5px'",
					element);
		} else if (elemType.equalsIgnoreCase("id")) {
			WebElement element = driver.findElement(By.id(elemID));
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].style.outline = 'red solid 5px'",
					element);
		}
	}

	public void waitElementToBeVisible(String locator) {
		WebDriverWait elemWait = new WebDriverWait(driver, 60L);

		elemWait.until(ExpectedConditions.presenceOfElementLocated(By
				.id(locator)));

	}

	/**
	 * Search for one value to macth againts all dropdown values Data will be
	 * provided from the Excel spreadsheet.
	 * 
	 * @param locator
	 * @param valueToCheck
	 * @throws Exception
	 */
	public void assertDropdownExpectedValue(String locator, int colNum,
			String dropdownName) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		WebElement select;

		if (locator.equalsIgnoreCase("titleOption")
				|| locator.startsWith("contact") || locator.startsWith("tfn")
				|| locator.startsWith("tax")) {
			select = driver.findElement(By.name(locator));

		} else if (locator.equalsIgnoreCase("pt")) {

			select = driver.findElement(By.id(locator));
		} else {
			select = driver.findElement(By.xpath(locator));
		}

		List<WebElement> allOptions = select.findElements(By.tagName("option"));

		if (allOptions.size() == 3)// if only has 1 loan package
		{
			if (allOptions.get(2).getText().equalsIgnoreCase(valueToCheck)) {
				tableRepLog("<tr>"
						+ "<td class=\"logsize\">"
						+ dropdownName
						+ "</td>"
						+ "<td class=\"logsize\">"
						+ locator
						+ "</td>"
						+ "<td class=\"logsize\">assertDropdownExpectedValue</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			} else {
				tableRepLog("<tr>"
						+ "<td class=\"logsize\">"
						+ dropdownName
						+ "</td>"
						+ "<td class=\"logsize\">"
						+ locator
						+ "</td>"
						+ "<td class=\"logsize\">assertDropdownExpectedValue</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot("assertDropdownExpectedValue", "error");
				tableRepLog("</td>" + "</tr>");
			}
		} else {
			tableRepLog("<tr>" + "<td class=\"logsize\">" + dropdownName
					+ "</td>" + "<td class=\"logsize\">" + locator + "</td>"
					+ "<td class=\"logsize\">assertDropdownExpectedValue</td>"
					+ "<td class=\"logsize\">" + valueToCheck + "</td>"
					+ "<td class=\"resFail\">FAILED</td>"
					+ "<td class=\"logsize\">");
			takeScreenshot("assertDropdownExpectedValue", "error");
			tableRepLog("</td>" + "</tr>");
		}

	}

	/**
	 * Search for one value to macth againts all dropdown values
	 * 
	 * 
	 * @param locator
	 * @param valueToCheck
	 * @throws Exception
	 */
	public void assertDropdownExpectedValue(String locator,
			String valueToCheck, String dropdownName) throws Exception {
		WebElement select;

		if (locator.equalsIgnoreCase("titleOption")
				|| locator.startsWith("contact") || locator.startsWith("tfn")
				|| locator.startsWith("tax")) {
			select = driver.findElement(By.name(locator));
		} else {
			select = driver.findElement(By.xpath(locator));
		}

		List<WebElement> allOptions = select.findElements(By.tagName("option"));

		if (allOptions.size() == 3)// if only has 1 loan package
		{
			if (allOptions.get(2).getText().equalsIgnoreCase(valueToCheck)) {
				tableRepLog("<tr>"
						+ "<td class=\"logsize\">"
						+ dropdownName
						+ "</td>"
						+ "<td class=\"logsize\">"
						+ locator
						+ "</td>"
						+ "<td class=\"logsize\">assertDropdownExpectedValue</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			} else {
				tableRepLog("<tr>"
						+ "<td class=\"logsize\">"
						+ dropdownName
						+ "</td>"
						+ "<td class=\"logsize\">"
						+ locator
						+ "</td>"
						+ "<td class=\"logsize\">assertDropdownExpectedValue</td>"
						+ "<td class=\"logsize\">" + valueToCheck + "</td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot("assertDropdownExpectedValue", "error");
				tableRepLog("</td>" + "</tr>");
			}
		} else {
			tableRepLog("<tr>" + "<td class=\"logsize\">" + dropdownName
					+ "</td>" + "<td class=\"logsize\">" + locator + "</td>"
					+ "<td class=\"logsize\">assertDropdownExpectedValue</td>"
					+ "<td class=\"logsize\">" + valueToCheck + "</td>"
					+ "<td class=\"resFail\">FAILED</td>"
					+ "<td class=\"logsize\">");
			takeScreenshot("assertDropdownExpectedValue", "error");
			tableRepLog("</td>" + "</tr>");
		}

	}

	/**
	 * CTan 06022014: this function searches the text equivalent of all the
	 * dropdown choices identified by elemID. Assertion passes if valueToCheck
	 * is found in the dropdown, otherwise failed.
	 * 
	 * @param locator
	 *            - ID of dropdown
	 * @param valueToCheck
	 *            - item in the dropdown list aimed to be checked
	 * @param dropdownName
	 *            - a label that identifies the dropdown list in the screen
	 * @throws Exception
	 */
	public void assertValueInDropdownList(String locator, int colNum,
			String dropdownName) throws Exception {
		Select select;

		String valueToCheck = getCellData(TestCaseRow, colNum);

		if (locator.equalsIgnoreCase("titleOption")
				|| locator.startsWith("contact") || locator.startsWith("tfn")
				|| locator.startsWith("tax")
				|| locator.startsWith("selectBankInfoID")) {
			select = new Select(driver.findElement(By.id(locator)));

		} else {

			select = new Select(driver.findElement(By.xpath(locator)));
		}

		List<String> optList = new ArrayList<String>();

		for (WebElement eachOpt : select.getOptions()) {
			optList.add(eachOpt.getText());
		}

		if (optList.contains(valueToCheck)) {
			tableRepLog("<tr>" + "<td class=\"logsize\">" + dropdownName
					+ "</td>" + "<td class=\"logsize\">" + locator + "</td>"
					+ "<td class=\"logsize\">assertValueInDropdownList</td>"
					+ "<td class=\"logsize\">" + valueToCheck + "</td>"
					+ "<td class=\"resPass\">PASSED</td>"
					+ "<td class=\"logsize\">");
			// takeScreenshot();
			tableRepLog("</td>" + "</tr>");
		} else {
			tableRepLog("<tr>" + "<td class=\"logsize\">" + dropdownName
					+ "</td>" + "<td class=\"logsize\">" + locator + "</td>"
					+ "<td class=\"logsize\">assertValueInDropdownList</td>"
					+ "<td class=\"logsize\">" + valueToCheck + "</td>"
					+ "<td class=\"resFail\">FAILED</td>"
					+ "<td class=\"logsize\">");
			takeScreenshot("assertDropdownExpectedValue", "error");
			tableRepLog("</td>" + "</tr>");
		}
	}

	/**
	 * New wait function to cater change on the way addresses are being input.
	 * The latest address implementation seems to affect the succeeding
	 * instructions.
	 * 
	 * @author clarenciotan
	 */
	public void waitElementValueAttr(String locator, String textPresent) {
		WebDriverWait elemWait = new WebDriverWait(driver, 60);

		elemWait.until(ExpectedConditions.textToBePresentInElementValue(
				By.id(locator), textPresent));

	}

	public void waitForElementStaleness(String locator) {
		WebDriverWait elemWait = new WebDriverWait(driver, 2);

		try {
			if (locator.startsWith("tfn") || locator.startsWith("tax")
					|| locator.startsWith("p.sst")
					|| locator.startsWith("mailing")
					|| locator.startsWith("title")
					|| locator.startsWith("contact")
					|| locator.equals("selectCardButton")) {
				WebElement elemToWait = driver.findElement(By.name(locator));
				elemWait.until(ExpectedConditions.stalenessOf(elemToWait));
			} else if (locator.startsWith("label")
					|| locator.startsWith("button")
					|| locator.startsWith("div")) {
				WebElement elemToWait = driver.findElement(By
						.cssSelector(locator));
				elemWait.until(ExpectedConditions.stalenessOf(elemToWait));
			} else {
				WebElement elemToWait = driver.findElement(By.id(locator));
				elemWait.until(ExpectedConditions.stalenessOf(elemToWait));
			}
		} catch (Exception e1) {
			if (locator.startsWith("tfn") || locator.startsWith("tax")
					|| locator.startsWith("p.sst")
					|| locator.startsWith("mailing")
					|| locator.startsWith("title")
					|| locator.startsWith("contact")
					|| locator.equals("selectCardButton")) {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.name(locator)));
			} else if (locator.startsWith("label")
					|| locator.startsWith("button")
					|| locator.startsWith("div")) {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.cssSelector(locator)));
			} else {
				elemWait.until(ExpectedConditions.presenceOfElementLocated(By
						.id(locator)));
			}
		}
	}

	// public void verifyErrorMessage(String valueToCheck) throws Exception
	// {
	// waitForElement("errormsg", "class");
	// verify_Text_In_Element(valueToCheck, "present", "errormsg",
	// "Error Message",
	// "assertErrorMessagePresent");
	// }
	//
	// public void verifyAuthenticationErrorMessage(String valueToCheck) throws
	// Exception
	// {
	// verify_Text_In_Element(valueToCheck, "present", "errormsg",
	// "Error Message",
	// "assertErrorMessagePresent");
	// // [contains(text(),'"+valueToCheck+"')]
	// }
	//
	// public void verifyVinHintMessage(String valueToCheck) throws Exception
	// {
	// WebElement vinHintIcon = driver.findElement(By.xpath("//span/div/img"));
	//
	// Actions builder = new Actions(driver);
	// Actions mousehHover = builder.moveToElement(vinHintIcon);
	//
	// mousehHover.perform();
	// waitForElement("//div/span/span", "xpath");
	//
	// verify_Text_In_Element(valueToCheck, "present", "//div/span/span",
	// "Hint Message",
	// "assertHintMessagePresent");
	// }
	//
	// public void verifyHintMessage(String locator, String valueToCheck) throws
	// Exception
	// {
	// WebElement vinHintIcon = driver.findElement(By.id(locator));
	//
	// Actions builder = new Actions(driver);
	// Actions mousehHover = builder.moveToElement(vinHintIcon);
	//
	// mousehHover.perform();
	// waitForElement(locator, "id");
	//
	// verify_Text_In_Element(valueToCheck, "present", locator, "Hint Message",
	// "assertHintMessagePresent");
	// }

	public void verifyFieldMaxLength(String locator, String maxLength) {
		String length = driver.findElement(By.id(locator)).getAttribute(
				"maxlength");

		Assert.assertEquals(length, maxLength);
	}

	public void scrollDown() {
		JavascriptExecutor jsx = (JavascriptExecutor) driver;
		jsx.executeScript("window.scrollBy(0,450)", "");
	}

	/**
	 * Wait for a specific period of time until element become hidden/invisible
	 * (created for Bendigo)
	 * 
	 * @param locator
	 *            - element locator
	 */
	public void waitForElementToBeInvisible(String locator) {
		WebDriverWait elemWait = new WebDriverWait(driver, ELEMENT_APPEAR);
		if (locator.startsWith("button")) {
			elemWait.until(ExpectedConditions.invisibilityOfElementLocated(By
					.cssSelector(locator)));
		} else {
			elemWait.until(ExpectedConditions.invisibilityOfElementLocated(By
					.xpath(locator)));
		}
	}

	// public void assertRadioButtonSelected(String radioSelector, String
	// radioButtonLabel, String fieldName) throws Exception
	// {
	// checkRadioButton(radioSelector, "selected", radioButtonLabel, fieldName);
	// }
	//
	// public void assertRadioButtonSelected(int colNum, String
	// radioButtonLabel, String fieldName) throws Exception
	// {
	// String radioSelector = getCellData(TestCaseRow, colNum);
	// checkRadioButton(radioSelector, "selected", radioButtonLabel, fieldName);
	// }
	//
	// public void assertRadioButtonNotSelected(String radioSelector, String
	// radioButtonLabel, String fieldName) throws Exception
	// {
	// checkRadioButton(radioSelector, "notselected", radioButtonLabel,
	// fieldName);
	// }
	//
	// public void assertRadioButtonNotSelected(int colNum, String
	// radioButtonLabel, String fieldName) throws Exception
	// {
	// String radioSelector = getCellData(TestCaseRow, colNum);
	// checkRadioButton(radioSelector, "notselected", radioButtonLabel,
	// fieldName);
	// }

	public void assertRadioButtonSelected(String radioSelector,
			String radioButtonLabel, String fieldName, String loctype)
			throws Exception {
		checkRadioButton(radioSelector, "selected", radioButtonLabel, fieldName);
	}

	public void assertRadioButtonSelected(int colNum, String radioButtonLabel,
			String fieldName) throws Exception {
		String radioSelector = getCellData(TestCaseRow, colNum);
		checkRadioButton(radioSelector, "selected", radioButtonLabel, fieldName);
	}

	public void assertRadioButtonNotSelected(String radioSelector,
			String radioButtonLabel, String fieldName) throws Exception {
		checkRadioButton(radioSelector, "notselected", radioButtonLabel,
				fieldName);
	}

	public void assertRadioButtonNotSelected(int colNum,
			String radioButtonLabel, String fieldName) throws Exception {
		String radioSelector = getCellData(TestCaseRow, colNum);
		checkRadioButton(radioSelector, "notselected", radioButtonLabel,
				fieldName);
	}

	public void assertCheckBoxSelected(String checkBoxSelector,
			String checkBoxLabel) throws Exception {
		verifyCheckBox(checkBoxSelector, "selected", checkBoxLabel);
	}

	public void assertCheckBoxSelected(int colNum, String checkBoxLabel)
			throws Exception {
		String checkBoxSelector = getCellData(TestCaseRow, colNum);
		verifyCheckBox(checkBoxSelector, "selected", checkBoxLabel);
	}

	public void assertCheckBoxNotSelected(String checkBoxSelector,
			String checkBoxLabel) throws Exception {
		verifyCheckBox(checkBoxSelector, "notselected", checkBoxLabel);
	}

	public void assertCheckBoxNotSelected(int colNum, String checkBoxLabel)
			throws Exception {
		String checkBoxSelector = getCellData(TestCaseRow, colNum);
		verifyCheckBox(checkBoxSelector, "notselected", checkBoxLabel);
	}

	/**
	 * This function checks the radio button by using TestNG's Assert class
	 * using function radiobutton()
	 * 
	 * @param selector
	 *            - String, radio button's selector
	 * @param status
	 *            - should the radio button be selected or not?
	 * @param radioButtonLabel
	 *            - name of the radio button
	 * @author clarenciotan
	 */
	public void checkRadioButton(String selector, String status,
			String radioButtonLabel, String fieldName) {
		try {
			if (status.equalsIgnoreCase("selected")) {
				System.out.println("\tASSERT Check: Radio Button " + "\""
						+ selector + "\"" + " should BE selected");
				Assert.assertTrue(checkIfSelected(selector).equals("true"));
				System.out.println("\t-> PASSED: " + "\"" + selector + "\""
						+ " is selected.");

				tableRepLog("<tr>"
						+ "<td class=\"logsize\">"
						+ fieldName
						+ "</td>"
						+ "<td class=\"logsize\">"
						+ selector
						+ "</td>"
						+ "<td class=\"logsize\">assertRadioButtonSelected</td>"
						+ "<td class=\"logsize\">" + radioButtonLabel + "</td>"
						+ "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			} else if (status.equalsIgnoreCase("notselected")) {
				System.out.println("\tASSERT Check: Radio Button " + "\""
						+ selector + "\"" + " should NOT BE selected");
				Assert.assertTrue(checkIfSelected(selector).equals(false));
				System.out.println("\t-> PASSED: " + "\"" + selector + "\""
						+ " is NOT selected.");

				tableRepLog("<tr>"
						+ "<td class=\"logsize\">"
						+ fieldName
						+ "</td>"
						+ "<td class=\"logsize\">"
						+ selector
						+ "</td>"
						+ "<td class=\"logsize\">assertRadioButtonNotSelected</td>"
						+ "<td class=\"logsize\">" + radioButtonLabel + "</td>"
						+ "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			}
		} catch (AssertionError e) {
			if (status.equalsIgnoreCase("selected")) {

				System.out.println("\t-> FAILED: Radio Button " + "\""
						+ selector + "\"" + " should BE selected");
				tableRepLog("<tr>"
						+ "<td class=\"logsize\">"
						+ fieldName
						+ "</td>"
						+ "<td class=\"logsize\">"
						+ selector
						+ "</td>"
						+ "<td class=\"logsize\">assertRadioButtonSelected</td>"
						+ "<td class=\"logsize\">" + radioButtonLabel + "</td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot("assertRadioButtonSelected", "error");
				tableRepLog("</td>" + "</tr>");
			} else if (status.equalsIgnoreCase("notselected")) {
				System.out.println("\t-> FAILED: Radio Button " + "\""
						+ selector + "\"" + " should NOT BE selected");
				tableRepLog("<tr>"
						+ "<td class=\"logsize\">"
						+ fieldName
						+ "</td>"
						+ "<td class=\"logsize\">"
						+ selector
						+ "</td>"
						+ "<td class=\"logsize\">assertRadioButtonNotSelected</td>"
						+ "<td class=\"logsize\">" + radioButtonLabel + "</td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot("assertRadioButtonNotSelected", "error");
				tableRepLog("</td>" + "</tr>");
			}
		}
	}

	/**
	 * This function checks the check box by using TestNG's Assert class using
	 * function checkBox()
	 * 
	 * @param selector
	 *            - String, radio button's selector
	 * @param status
	 *            - should the radio button be selected or not?
	 * @param radioButtonLabel
	 *            - name of the radio button
	 * @author clarenciotan
	 */
	public void verifyCheckBox(String selector, String status,
			String checkBoxLabel) {
		try {
			if (status.equalsIgnoreCase("selected")) {
				System.out.println("\tASSERT Check: Check Box " + "\""
						+ selector + "\"" + " should BE selected");
				Assert.assertTrue(checkIfSelected(selector).equals(true));
				System.out.println("\t-> PASSED: " + "\"" + selector + "\""
						+ " is selected.");

				tableRepLog("<tr>" + "<td class=\"logsize\">" + checkBoxLabel
						+ "</td>" + "<td class=\"logsize\">" + selector
						+ "</td>"
						+ "<td class=\"logsize\">assertCheckBoxSelected</td>"
						+ "<td class=\"logsize\">" + status + "</td>"
						+ "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			} else if (status.equalsIgnoreCase("notselected")) {
				System.out.println("\tASSERT Check: Check Box " + "\""
						+ selector + "\"" + " should NOT BE selected");
				Assert.assertTrue(checkIfSelected(selector).equals(false));
				System.out.println("\t-> PASSED: " + "\"" + selector + "\""
						+ " is NOT selected.");

				tableRepLog("<tr>"
						+ "<td class=\"logsize\">"
						+ checkBoxLabel
						+ "</td>"
						+ "<td class=\"logsize\">"
						+ selector
						+ "</td>"
						+ "<td class=\"logsize\">assertCheckBoxNotSelected</td>"
						+ "<td class=\"logsize\">" + status + "</td>"
						+ "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			}
		} catch (AssertionError e) {
			if (status.equalsIgnoreCase("selected")) {

				System.out.println("\t-> FAILED: Check Box " + "\"" + selector
						+ "\"" + " should BE selected");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + checkBoxLabel
						+ "</td>" + "<td class=\"logsize\">" + selector
						+ "</td>"
						+ "<td class=\"logsize\">assertCheckBoxSelected</td>"
						+ "<td class=\"logsize\">" + status + "</td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot("assertCheckBoxSelected", "error");
				tableRepLog("</td>" + "</tr>");
			} else if (status.equalsIgnoreCase("notselected")) {
				System.out.println("\t-> FAILED: Check Box " + "\"" + selector
						+ "\"" + " should NOT BE selected");
				tableRepLog("<tr>"
						+ "<td class=\"logsize\">"
						+ checkBoxLabel
						+ "</td>"
						+ "<td class=\"logsize\">"
						+ selector
						+ "</td>"
						+ "<td class=\"logsize\">assertCheckBoxNotSelected</td>"
						+ "<td class=\"logsize\">" + status + "</td>"
						+ "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				takeScreenshot("assertCheckBoxNotSelected", "error");
				tableRepLog("</td>" + "</tr>");
			}
		}
	}

	/**
	 * This function returns status of the checkbox.
	 * 
	 * @param selector
	 *            - locator (can be of type id or others) of the checkbox.
	 * @return Boolean - true if the checkbox is selected, false otherwise
	 * @author clarenciotan
	 */
	private Boolean checkIfSelected(String selector) {
		return webElementToCheck(selector, "present").isSelected();
	}

	/**
	 * This function waits for the the desired instance of Browsers during
	 * testing. Used normally for valuation purpose wherein to test, we'll need
	 * to have at least 2 windows for testing.
	 * 
	 * @param numberOfWindows
	 *            - expected number of windows
	 * @author lovelielineses
	 */
	public void waitForNumberOfWindowsToEqual(final int numberOfWindows) {
		new WebDriverWait(driver, 30).until(new ExpectedCondition<Boolean>() {

			public Boolean apply(WebDriver driver) {
				return (driver.getWindowHandles().size() == numberOfWindows);
			}
		});
	}

	/**
	 * This function will switch the current WebDriver instance to the new
	 * browser. This new browser can be invoked either manually or by a click in
	 * a webelement during LendFast testing execution
	 * 
	 * @param windowSets
	 *            - s Set<String> that contains the handles of the current
	 *            browser instances
	 * @param urlContent
	 *            - a String representation which is contained the the navigated
	 *            url of the new window
	 * @author lovelielineses
	 */
	public void goToDesiredBrowserInstance(Set<String> windowSets,
			String urlContent) {
		for (String eachWindow : windowSets) {

			driver.switchTo().window(eachWindow);
			while (driver.getCurrentUrl().isEmpty()
					| driver.getCurrentUrl().contains("about:blank")) {
				try {
					System.out.println("URL is: \"" + driver.getCurrentUrl()
							+ "\"");
					Thread.sleep(1000);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			if (driver.getCurrentUrl().contains(urlContent)) {
				System.out.println("URL is when \"if\" was satisfied: \""
						+ driver.getCurrentUrl() + "\"");
				System.out.println("The window title is: \""
						+ driver.getTitle() + "\"");
				break;
			}

		}
		System.out.println("Im out of the loop...");
	}

	public void verify_Selected_Dropdown_Option(String valueToCheck,
			String selectedNotSelected, String locator, String locatorLabel,
			String assertLabel) throws Exception {

		try {
			if (selectedNotSelected.equalsIgnoreCase("selected")) {
				System.out
						.println("\tASSERT Check: " + "\"" + valueToCheck
								+ "\"" + " should BE found in " + "\""
								+ locator + "\"");
				getDropdownSelectedOption(locator, valueToCheck,
						selectedNotSelected);
				System.out.println("\t-> PASSED: " + "\"" + valueToCheck + "\""
						+ " found.");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>" + "<td class=\"logsize\">" + assertLabel
						+ "</td>" + "<td class=\"logsize\">" + valueToCheck
						+ "</td>" + "<td class=\"resPass\">PASSED</td>"
						+ "<td class=\"logsize\">");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			} else if (selectedNotSelected.equalsIgnoreCase("notSelected")) {
				System.out.println("\tASSERT Check: " + "\"" + valueToCheck
						+ "\"" + " should NOT BE found in " + "\"" + locator
						+ "\"");
				getDropdownSelectedOption(locator, valueToCheck,
						selectedNotSelected);
				System.out.println("\t-> PASSED: " + "\"" + valueToCheck + "\""
						+ " NOT found.");
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>" + "<td class=\"logsize\">" + assertLabel
						+ "</td>" + "<td class=\"logsize\">" + valueToCheck
						+ "</td>" + "<td class=\"resPass\">PASSED</td>"
						+ "<td>");
				// takeScreenshot();
				tableRepLog("</td>" + "</tr>");
			}
		} catch (AssertionError e) {
			if (selectedNotSelected.equalsIgnoreCase("selected")) {
				System.out.println("\t-> FAILED: " + "\"" + valueToCheck + "\""
						+ " NOT FOUND in the locator");
				// WebElementHighlight.highlightElement(webElementToCheck(locator,
				// presentNotPresent));
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>" + "<td class=\"logsize\">" + assertLabel
						+ "</td>" + "<td class=\"logsize\">" + valueToCheck
						+ "</td>" + "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				// WebElementHighlight.highlightElement(webElementToCheck(locator,
				// presentNotPresent));
				takeScreenshot(assertLabel, "error");
				tableRepLog("</td>" + "</tr>");
			} else if (selectedNotSelected.equalsIgnoreCase("notSelected")) {
				System.out.println("\t-> FAILED: " + "\"" + valueToCheck + "\""
						+ " FOUND in the locator");
				// WebElementHighlight.highlightElement(webElementToCheck(locator,
				// presentNotPresent));
				tableRepLog("<tr>" + "<td class=\"logsize\">" + locatorLabel
						+ "</td>" + "<td class=\"logsize\">" + locator
						+ "</td>" + "<td class=\"logsize\">" + assertLabel
						+ "</td>" + "<td class=\"logsize\">" + valueToCheck
						+ "</td>" + "<td class=\"resFail\">FAILED</td>"
						+ "<td class=\"logsize\">");
				// WebElementHighlight.highlightElement(webElementToCheck(locator,
				// presentNotPresent));
				takeScreenshot(assertLabel, "error");
				tableRepLog("</td>" + "</tr>");
			}
		}
	}

	public void getDropdownSelectedOption(String locator, String valueToCheck,
			String selectedNotSelected) {
		if (selectedNotSelected.equalsIgnoreCase("selected")) {
			Assert.assertTrue(new Select(webElementToCheck(locator, "present"))
					.getFirstSelectedOption().getText().equals(valueToCheck));
		} else {
			Assert.assertFalse(new Select(webElementToCheck(locator, "present"))
					.getFirstSelectedOption().getText().equals(valueToCheck));
		}

	}

	public void assertDropdownSelectedOption(String locator,
			String valueToCheck, String dropDownlabel) throws Exception {
		verify_Selected_Dropdown_Option(valueToCheck, "selected", locator,
				dropDownlabel, "assertDropdownSelectedOption");
	}

	public void assertDropdownSelectedOption(String locator, int colNum,
			String dropDownlabel) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Selected_Dropdown_Option(valueToCheck, "selected", locator,
				dropDownlabel, "assertDropdownSelectedOption");
	}

	public void assertDropdownNotSelectedOption(String locator,
			String valueToCheck, String dropDownlabel) throws Exception {
		verify_Selected_Dropdown_Option(valueToCheck, "notSelected", locator,
				dropDownlabel, "assertDropdownNotSelectedOption");
	}

	public void assertDropdownNotSelectedOption(String locator, int colNum,
			String dropDownlabel) throws Exception {
		String valueToCheck = getCellData(TestCaseRow, colNum);
		verify_Selected_Dropdown_Option(valueToCheck, "notSelected", locator,
				dropDownlabel, "assertDropdownNotSelectedOption");
	}

	/**
	 * This method will click a link text in the page.
	 * 
	 * @author lenard.g.magpantay
	 * */

	public void clickNavigationLink(int colNum) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, ELEMENT_APPEAR);
		String linkText = getCellData(TestCaseRow, colNum);

		wait.until(ExpectedConditions.presenceOfElementLocated(By
				.linkText(linkText)));
		driver.findElement(By.linkText(linkText)).click();
		System.out.println("Clicking " + linkText + "...");
		log("Clicking " + linkText + "...");
	}

	/**
	 * This method will click a link text in the page.
	 * 
	 * @author lenard.g.magpantay
	 * */

	public void clickNavigationLink(String linkText) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, ELEMENT_APPEAR);

		wait.until(ExpectedConditions.presenceOfElementLocated(By
				.linkText(linkText)));
		driver.findElement(By.linkText(linkText)).click();
		System.out.println(linkText + " link has been clicked...");
		log(linkText + " link has been clicked...");
	}

	/**
	 * This method will click the user menu button and click a menu item.
	 * 
	 * @author lenard.g.magpantay
	 * */

	public void clickUserMenuButton(String menu) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, ELEMENT_APPEAR);

		wait.until(ExpectedConditions.presenceOfElementLocated(By
				.id("userNavButton")));
		driver.findElement(By.id("userNavButton")).click();
		System.out.println("User menu has been clicked...");
		log("User menu has been clicked...");

		wait.until(ExpectedConditions.presenceOfElementLocated(By
				.xpath("//*[@title='" + menu + "']")));
		driver.findElement(By.xpath("//*[@title='" + menu + "']")).click();
		System.out.println("Clicked " + menu + " menu item...");
		log("Clicked " + menu + " menu item...");
	}

	/**
	 * This method will click an item in the menu bar.
	 * 
	 * @author lenard.g.magpantay
	 * */

	public void clickMenuTab(String menu) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, ELEMENT_APPEAR);

		wait.until(ExpectedConditions.presenceOfElementLocated(By
				.xpath("//*[contains(@title,'" + menu + "')]")));
		driver.findElement(By.xpath("//*[contains(@title,'" + menu + "')]"))
				.click();

		System.out.println("Clicked " + menu + " menu tab...");
		log("Clicked " + menu + " menu tab...");
	}

	public void waitForAlertPopup() {
		try {
			WebDriverWait wait = new WebDriverWait(driver, ELEMENT_APPEAR);
			wait.until(ExpectedConditions.alertIsPresent());
			System.out.println("Alert popup is displayed...");
			log("Alert popup is displayed...");
		} catch (Exception e) {

			System.out.println("No alert popup has displayed in the page...");
			log("No alert popup has displayed in the page...");
		}

	}

	/**
	 * This will wait for a window alert popup and accept the content.
	 * 
	 * @author lenard.g.magpantay
	 * */
	public void checkAlertPopup(String windowName) {

		waitForAlertPopup();
		Alert alert = driver.switchTo().alert();
		alert.accept();
		System.out.println("Successfully handled alert message...");
		log("Successfully handled alert message...");
		switchToMainPage(windowName);

	}

	/**
	 * This method will wait for a window alert popup and the text content.
	 * 
	 * @author lenard.g.magpantay
	 * */

	public String getTextInAlertPopup() {
		String alertText = null;

		try {
			waitForAlertPopup();
			Alert alert = driver.switchTo().alert();
			alertText = alert.getText();

		} catch (Exception e) {
		}
		return alertText;
	}

	/**
	 * This method will switch the control of the window to an IFrame in the
	 * page.
	 * 
	 * @author lenard.g.magpantay
	 * */

	public void switchToFrame(String iFrame) {
		try {

			WebDriverWait wait = new WebDriverWait(driver, ELEMENT_APPEAR);
			wait.until(ExpectedConditions.presenceOfElementLocated(By
					.id(iFrame)));
			driver.switchTo().frame(driver.findElement(By.id(iFrame)));

			System.out.println("Switched the focus to a frame...");
			log("Switched the focus to a frame...");
		} catch (Exception e) {
			// exception handling
		}

	}

	/**
	 * This method will switch the control of the driver back to main browser
	 * instance.
	 * 
	 * @author lenard.g.magpantay
	 * */

	public void switchToMainPage(String windowHandle) {
		try {

			driver.switchTo().window(windowHandle);

			System.out.println("Switched to the main window...");
			log("Switched to the main window...");
		} catch (Exception e) {
			// exception handling
		}

	}

	/**
	 * This method will emulate the pressing of Esc button in the keyboard
	 * 
	 * @author lenard.g.magpantay
	 * */
	public void pressESCButton() throws AWTException {
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_ESCAPE);
		r.keyRelease(KeyEvent.VK_ESCAPE);

		System.out.println("Esc button has been pressed...");
		log("Esc button has been pressed...");
	}

	/**
	 * This method will emulate the pressing of Enter button in the keyboard
	 * 
	 * @author lenard.g.magpantay
	 * */

	public static void pressEnterButton() throws AWTException {
		Robot r = new Robot();
		r.keyPress(KeyEvent.VK_ENTER);
		r.keyRelease(KeyEvent.VK_ENTER);

		System.out.println("Enter button has been pressed...");
		log("Enter button has been pressed...");
	}

	/**
	 * This method will wait for a web element until not visible in the page.
	 * 
	 * @author lenard.g.magpantay
	 * */

	public void waitElementUntilNotPresent(String locator, String locType) {

		try {

			WebDriverWait wait = new WebDriverWait(driver, ELEMENT_APPEAR);

			if (locType.equalsIgnoreCase("id")) {
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By
						.id(locator)));

			} else if (locType.equalsIgnoreCase("name")) {
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By
						.name(locator)));

			} else if (locType.equalsIgnoreCase("class")) {
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By
						.className(locator)));
			} else {
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By
						.xpath(locator)));
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public String getWindowName() {

		try {
			windowHandle = driver.getWindowHandle();

		} catch (Exception e) {
			// exception handling
		}

		return windowHandle;
	}

	/**
	 * This method will switch the control of the window to another browser
	 * instance.
	 * 
	 * @author lenard.g.magpantay
	 * */

	public void switchWindow() {
		try {
			for (String winHandle : driver.getWindowHandles()) {
				if (!winHandle.equals(windowHandle)) {
					driver.switchTo().window(winHandle);
					driver.manage().window().maximize();
					System.out.println("Switched to another window...");
					log("Switched to another window...");
				}
			}
		} catch (Exception e) {
			// exception handling
		}
	}

	/**
	 * This method will refresh the current page displayed.
	 * 
	 * @author lenard.g.magpantay
	 * */
	public void refreshPage() {
		try {
			driver.navigate().refresh();
			System.out.println("Page has been refreshed...");
			log("Page has been refreshed...");

		} catch (Exception e) {
			// exception handling
		}

	}

	/**
	 * This method will get a data from MS Excel and return the value.
	 * 
	 * @author lenard.g.magpantay
	 * */

	public String getExcelData(int colNum) {

		String value = "";
		try {
			value = getCellData(BaseTest.TestCaseRow, colNum);

		} catch (Exception e) {
			// exception handling

		}
		return value;

	}
	
	public int getLastNonNullRow(int firstNonNullRow){
		int rowNum = firstNonNullRow;
		while(getExcelData(rowNum, 7, "text").length()>0){
			rowNum += 1;
		}
		return rowNum;
	}

	// Edited jerrick.m.falogme.....
	public String getExcelData(int rowNum, int colNum) {

		String value = "";
		try {
			value = getCellData(rowNum, colNum);

		} catch (Exception e) {
			// exception handling

		}
		return value;

	}

	public String getExcelData(int rowNum, int colNum, String type) {

		String value = "";
		try {
			value = getCellData(rowNum, colNum, type);

		} catch (Exception e) {
			// exception handling

		}
		return value;

	}
	
	/*public String getExcelSheetName(){
		return ExcelMainSheetName;
	}*/

	public void searchBoxSidebar(int colNum, int colNum2) {
		try {
			String dropdown = getCellData(TestCaseRow, colNum);
			String value = getCellData(TestCaseRow, colNum2);
			WebDriverWait wait = new WebDriverWait(driver, ELEMENT_APPEAR);

			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("sen")));
			new Select(driver.findElement(By.id("sen")))
					.selectByVisibleText(dropdown);
			log("Selected a value in Search dropdown...");
			System.out.println("Selected a value in Search dropdown...");

			wait.until(
					ExpectedConditions.presenceOfElementLocated(By.id("sbstr")))
					.sendKeys(value);
			log("Entered a value in the Search Box...");
			System.out.println("Entered a value in the Search Box...");

			wait.until(
					ExpectedConditions.presenceOfElementLocated(By
							.name("search"))).click();
			log("Clicked Go button...");
			System.out.println("Clicked Go button...");

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void searchBoxSidebar(String dropdown, String value) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, ELEMENT_APPEAR);

			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("sen")));
			new Select(driver.findElement(By.id("sen")))
					.selectByVisibleText(dropdown);
			log("Selected a value in Search dropdown...");
			System.out.println("Selected a value in Search dropdown...");

			wait.until(
					ExpectedConditions.presenceOfElementLocated(By.id("sbstr")))
					.sendKeys(value);
			log("Entered a value in the Search Box...");
			System.out.println("Entered a value in the Search Box...");

			wait.until(
					ExpectedConditions.presenceOfElementLocated(By
							.name("search"))).click();
			log("Clicked Go button...");
			System.out.println("Clicked Go button...");

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * This method will navigate the window to the previous page.
	 * 
	 * @author lenard.g.magpantay
	 * */

	public void clickBackButton() {
		try {
			driver.navigate().back();
			System.out.println("Navigated to previous page...");
			log("Navigated to previous page...");

		} catch (Exception e) {
			// exception handling
		}

	}

	/**
	 * This method will point the mouse pointer to a certain location in the
	 * page.
	 * 
	 * @author lenard.g.magpantay
	 * */

	public void mouseHover(String locator, String locType) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, ELEMENT_APPEAR);
			Actions action = new Actions(driver);

			if (locType.equalsIgnoreCase("id")) {
				WebElement loc = wait.until(ExpectedConditions
						.presenceOfElementLocated(By.id(locator)));
				action.moveToElement(loc)
						.moveToElement(driver.findElement(By.id(locator)))
						.click().build().perform();

			} else if (locType.equalsIgnoreCase("name")) {
				WebElement loc = wait.until(ExpectedConditions
						.presenceOfElementLocated(By.name(locator)));
				action.moveToElement(loc)
						.moveToElement(driver.findElement(By.name(locator)))
						.click().build().perform();

			} else if (locType.equalsIgnoreCase("class")) {
				WebElement loc = wait.until(ExpectedConditions
						.presenceOfElementLocated(By.className(locator)));
				action.moveToElement(loc)
						.moveToElement(
								driver.findElement(By.className(locator)))
						.click().build().perform();
			} else {
				WebElement loc = wait.until(ExpectedConditions
						.presenceOfElementLocated(By.xpath(locator)));
				action.moveToElement(loc)
						.moveToElement(driver.findElement(By.xpath(locator)))
						.click().build().perform();
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	public void logoutUser() throws Exception {
		clickUserMenuButton("Logout");
		System.out.println("Logging out...");
		log("Logging out...");

	}

	public void scrollTo(String direction) {

		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			if (direction.equalsIgnoreCase("up")) {
				js.executeScript("window.scrollBy(0,2000)", "");
				System.out.println("Scrolling up...");
				log("Scrolling up...");

			} else if (direction.equalsIgnoreCase("down")) {

				js.executeScript("window.scrollBy(0,-2000)", "");
				System.out.println("Scrolling down...");
				log("Scrolling down...");

			} else if (direction.equalsIgnoreCase("right")) {

				js.executeScript("window.scrollBy(2000,0)", "");
				System.out.println("Scrolling to the right...");
				log("Scrolling to the right...");

			} else if (direction.equalsIgnoreCase("left")) {

				js.executeScript("window.scrollBy(-2000,0)", "");
				System.out.println("Scrolling to the left...");
				log("Scrolling to the left...");

			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void maximizeWindow() {

		try {
			driver.manage().window().maximize();
			System.out.println("Window has been maximized...");
			log("Window has been maximized...");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
