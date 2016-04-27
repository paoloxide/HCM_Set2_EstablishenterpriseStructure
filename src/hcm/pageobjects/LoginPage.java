package hcm.pageobjects;

import org.openqa.selenium.WebDriver;
import common.BasePage;
import hcm.locators.LoginLocators;
import static common.BaseTest.TestCaseRow;
import static util.ReportLogger.log;
import static common.ExcelUtilities.getCellData;

public class LoginPage extends BasePage{
	
	public LoginPage(WebDriver driver){
		super(driver);
	}
	
	@Override
    public String getPageId()
    {
        return "login-aufsn4x0cba.oracleoutsourcing.com/oam/server/obrareq.cgi";
    }

	public void enterUserID(int colNum) throws Exception{
		String value = getCellData(TestCaseRow, colNum);
		enterTextByName(LoginLocators.enterUserID, value); //
		log("Entered User ID...");
		System.out.println("Entered User ID...");
	}
	
	public void enterPassword(int colNum) throws Exception{
		String value = getCellData(TestCaseRow, colNum);
		enterTextByName(LoginLocators.enterPassword, value);
		log("Entered Password...");
		System.out.println("Entered Password...");
	}
	
	public void clickSignInButton(){
		clickByXpath(LoginLocators.clickSignInButton);
		log("Clicked Sign In Button...");
		System.out.println("Clicked Sign In Button...");
	}

}
