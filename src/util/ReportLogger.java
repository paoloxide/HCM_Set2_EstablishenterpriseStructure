
package util;

import org.testng.Reporter;

/**
 * Utility class for logging messages in the TestNG report.
 * 
 * 
 */
public final class ReportLogger
{
    /** Default log message verbosity level */
    public static final int DEFAULT_VERBOSITY_LEVEL = 2;

    /** Debug message verbosity level */
    public static final int DEBUG_VERBOSITY_LEVEL = 4;

    /** Log output is escaped */
    private static boolean logOutputEscaped = Boolean.parseBoolean(System
            .getProperty("org.uncommons.reportng.escape-output"));

    /**
     * Constructs a new {@link ReportLogger} instance.
     */
    private ReportLogger()
    {
    }

    /**
     * Logs a line of message to the test report output using the default
     * message verbosity level.
     * 
     * @param message message to log
     */
    public static void log(String message)
    {
        log(message, DEFAULT_VERBOSITY_LEVEL);
    }

    /**
     * Logs a line of debug message to the test report output using the debug
     * message verbosity level.
     * 
     * @param message debug message to log
     */
    public static void logDebug(String message)
    {
        log("DEBUG: " + message, DEBUG_VERBOSITY_LEVEL);
    }

    /**
     * Logs a line of failure message to the test report output.
     * 
     * @param message test failure message to log
     */
    public static void logFailure(String message)
    {
        log("<mark><strong>FAILURE:</strong> <em>" + message + "</em></mark>");
    }

    /**
     * Logs a line of message to the test report output if the current TestNG
     * verbosity level is equal or greater than the indicated verbosity level
     * for this message.
     * 
     * @param message message to log
     * @param verbosity verbosity level of this message
     */
    public static void log(String message, int verbosity)
    {
        // add an HTML line break if log output is not escaped
        if (!logOutputEscaped)
        {
            message = message + "<br/>";
        }

        Reporter.log(message);
    }
    
    /**
     * This version is used only for 
     * printing the logs on the assertions.
     * 
     * @param message
     */
    public static void tableRepLog(String message)
    {
        Reporter.log(message);
    }
}
