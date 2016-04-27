package common;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * 
 * Class provides the predicate for checking ajax request completion. It's
 * a known issue that Groovy compiler sometimes is having problem in parsing
 * anonoymous class so this class has been refactored from BasePage.waitForA4jRequest
 * as a non-anonoymous class.
 * 
 * @author davidlai
 *
 */
class WaitForA4jRequestHandler implements ExpectedCondition<Boolean>
{
    /**
     * Execute javascript to check the ajax request has been completed.
     * 
     * @see com.google.common.base.Function#apply(java.lang.Object)
     */
    public Boolean apply(WebDriver webDriver)
    {
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        Boolean result = (Boolean) executor.executeScript("if(ajaxMonitor != null) { return ajaxMonitor.waitForA4jRequest(); } else { return false; }");
        return result;
    }

}
