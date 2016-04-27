
package common;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;

/**
 * An extension of the default <tt>FirefoxDriver</tt> class but with support for
 * taking screenshots.
 * 
 * 
 */
interface TakesScreenshot
{
    /**
     * Retrieves the screenshot data as a byte array.
     * 
     * @param target output type
     * @return screenshot data as a byte array
     * @throws WebDriverException
     */
    byte[] getScreenshot(OutputType<byte[]> target) throws WebDriverException;
    
    
}
