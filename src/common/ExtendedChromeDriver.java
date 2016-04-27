package common;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DriverCommand;

public class ExtendedChromeDriver extends ChromeDriver implements TakesScreenshot
{
    public ExtendedChromeDriver(Capabilities capabilities)
    {
        super(capabilities);
    }
    
    public byte[] getScreenshot(OutputType<byte[]> target) throws WebDriverException
    {
        String base64Str = execute(DriverCommand.SCREENSHOT).getValue().toString();
        return target.convertFromBase64Png(base64Str);
    }

}
