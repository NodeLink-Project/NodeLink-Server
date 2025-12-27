package io.nodelink.server.log;

public class Logger {

    /**
     * Instance of Logger
     */
    private static final Logger INSTANCE = new Logger();

    /**
     * Instance of Logs
     */
    private final Logs logs = Logs.getLogsSingleton();

    /**
     * Method to display an informational message in the console.
     * This method prints the message in green color if showLogs is enabled,
     * and always logs it to file.
     *
     * @param msg The message to log
     */
    public void INFO(String msg) {
        // Green
        String INFO = "\u001B[32m[INFO] \u001B[0m";
        System.out.println(INFO + msg);

        logs.MakeALog(msg, "INFO");
    }

    /**
     * Method to display a warning message in the console.
     * This method prints the message in yellow color if showLogs is enabled,
     * and always logs it to file.
     *
     * @param msg The message to log
     */
    public void WARN(String msg) {

        // Yellow
        String WARN = "\u001B[33m[WARN] \u001B[0m";
        System.out.println(WARN + msg);

        logs.MakeALog(msg, "WARN");
    }

    /**
     * Method to display an error message in the console.
     * This method prints the message in red color if showLogs is enabled,
     * and always logs it to file.
     *
     * @param msg The message to log
     */
    public void ERROR(String msg) {

        // Red
        String ERROR = "\u001B[31m[ERROR] \u001B[0m";
        System.out.println(ERROR + msg);

        logs.MakeALog(msg, "ERROR");
    }

    /**
     * Method to display a critical message in the console.
     * This method prints the message in magenta color if showLogs is enabled,
     * and always logs it to file.
     *
     * @param msg The message to log
     */
    public void CRITICAL(String msg) {

        // Magenta
        String CRITICAL = "\u001B[35m[CRITICAL] \u001B[0m";
        System.out.println(CRITICAL + msg);


        logs.MakeALog(msg, "CRITICAL");
    }

    /**
     * Get the singleton instance of Logger
     *
     * @return The singleton instance of Logger
     */
    public static Logger getLoggerSingleton() {
        return INSTANCE;
    }
}
