
 package common;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DriverCommand;

/**
 * An extension of the default <tt>InternetExplorerDriver</tt> class but with
 * support for taking screenshots.
 * 
 * @author clementlyons
 */
public class ExtendedInternetExplorerDriver extends InternetExplorerDriver implements
        TakesScreenshot
{
    /**
     * Constructor.
     * 
     * @param capabilities
     */
    public ExtendedInternetExplorerDriver(Capabilities capabilities)
    {
        super(capabilities);
    }

    /**
     * Retrieves the screenshot data as a byte array.
     * 
     * @param target The output type.
     * @return The screenshot data as a byte array.
     * @throws WebDriverException
     */
    public byte[] getScreenshot(OutputType<byte[]> target) throws WebDriverException
    {
        String base64Str = execute(DriverCommand.SCREENSHOT).getValue().toString();
        return target.convertFromBase64Png(base64Str);
    }
}
