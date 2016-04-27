
package common;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * Class provides the predicate for checking page load completion. It's a known
 * issue that Groovy compiler sometimes is having problem in parsing anonoymous
 * class so this class has been refactored from BasePage.waitUntilPageLoaded as
 * a non-anonoymous class.
 * 
 * @author phongtran
 */
class WaitForPageLoadedHandler implements ExpectedCondition<Boolean>
{
    private BasePage page;

    /**
     * Constructor.
     * 
     * @param page The page to process.
     */
    public WaitForPageLoadedHandler(BasePage page)
    {
        this.page = page;
    }

    /**
     * Execute javascript to check the page has been loaded.
     * 
     * @param webDriver The selenium web driver.
     * @return Whether the page has been loaded.
     */
    public Boolean apply(WebDriver webDriver)
    {
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        Boolean result = (Boolean) executor.executeScript("return (window.location.href.search('"
                + page.getPageId() + "') >= 0);");
        return result;
    }
}
