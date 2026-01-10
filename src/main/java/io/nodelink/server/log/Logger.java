package io.nodelink.server.log;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
     * Formatter for time in HH:mm:ss format
     */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    private void printLog(String level, String msg, String levelColor, String msgColor) {
        String time = LocalTime.now().format(TIME_FORMATTER);

        String formattedLine = String.format("%s%s %s[%s] %s%s%s",
                GREEN, time, levelColor, level, msgColor, msg, RESET);

        System.out.println("\n" + formattedLine);

        if (logs != null) {
            logs.MakeALog(msg, level);
        }
    }

    public void INFO(String msg) {
        printLog("INFOS", msg, GREEN, CYAN);
    }

    public void WARN(String msg) {
        printLog("WARN", msg, YELLOW, YELLOW);
    }

    public void ERROR(String msg) {
        printLog("ERROR", msg, RED, RED);
    }

    public void CRITICAL(String msg) {
        printLog("CRITICAL", msg, MAGENTA, MAGENTA);
    }

    public void SUCCESS(String msg) {
        printLog("SUCCESS", msg, GREEN, GREEN);
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
