package io.nodelink.server;

import io.nodelink.server.update.Version;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.utils.InfoCmp.Capability;
import java.nio.file.Paths;

public class NodeLinkHelper {

    private static final NodeLinkHelper INSTANCE = new NodeLinkHelper();

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";
    private static final String WHITE = "\u001B[37m";
    private static final String YELLOW = "\u001B[33m";

    private static String STATUS = "Main";
    private static String PRODUCT = "Post Production";

    private static final int RESERVED_ROWS = 36;

    public static NodeLinkHelper getHelper() {
        return INSTANCE;
    }

    protected void INITIALIZE() {
        initTerminal();
    }

    private void initTerminal() {
        try {
            Terminal terminal = TerminalBuilder.builder()
                    .name("NodeLink Server")
                    .system(true)
                    .build();

            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .variable(LineReader.HISTORY_FILE, Paths.get("bin/history.txt"))
                    .option(LineReader.Option.AUTO_FRESH_LINE, true)
                    .build();

            fullClearAndRefresh(terminal);

            while (true) {
                String prompt = GREEN + "Server" + RESET + "@" + YELLOW + "NodeLink" + RESET + "-(" + RED + STATUS + RESET + ")~" + WHITE + "$ " + RESET;

                try {
                    String command = reader.readLine(prompt);

                    if (command == null || command.equalsIgnoreCase("exit")) break;

                    if (command.equalsIgnoreCase("clear")) {
                        fullClearAndRefresh(terminal);
                        continue;
                    }

                    if (command.equalsIgnoreCase("wow")) {
                        STATUS = "Wow Mode";
                        fullClearAndRefresh(terminal);
                        continue;
                    }

                    terminal.writer().println("Exécution de : " + command);

                } catch (UserInterruptException | EndOfFileException e) {
                    break;
                }
            }
        } catch (Exception _) {}
    }

    private void fullClearAndRefresh(Terminal terminal) {
        terminal.writer().print("\u001B[r");
        terminal.writer().print("\u001B[2J\u001B[3J");
        terminal.puts(Capability.cursor_address, 0, 0);
        terminal.flush();

        drawStaticInterface(terminal);

        terminal.writer().print("\u001B[" + (RESERVED_ROWS + 1) + ";r");
        terminal.puts(Capability.cursor_address, RESERVED_ROWS, 0);
        terminal.flush();
    }

    private void drawStaticInterface(Terminal terminal) {
        terminal.puts(Capability.cursor_address, 0, 0);

        String[] logoLines = LOGO().split("\n");
        String[] headerLines = HEADER().split("\n");
        int width = terminal.getWidth();

        int logoHeight = logoLines.length;
        for (int i = 0; i < logoHeight; i++) {
            String leftPart = (i >= 12 && (i - 12) < headerLines.length) ? BLUE + headerLines[i - 12] + RESET : "";
            String rightPart = logoLines[i];

            int padding = width - getPlainTextLength(leftPart) - getPlainTextLength(rightPart);
            if (padding < 0) padding = 0;

            terminal.writer().println(leftPart + " ".repeat(padding) + rightPart);
        }

        terminal.writer().println(GREEN + "  ● Status: " + RESET + YELLOW + PRODUCT + RESET);
        terminal.writer().println(GREEN + "  ● Version: " + RESET + Version.VERSION);
        terminal.writer().println(GREEN + "  ● Memory: " + RESET + getMemoryUsage());

        int currentLine = logoHeight + 4;
        for (int i = currentLine; i < RESERVED_ROWS - 1; i++) {
            terminal.writer().println("");
        }

        terminal.writer().print(BLUE + "─".repeat(width) + RESET);
        terminal.flush();
    }

    private String getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long used = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        return used + " MB / " + (runtime.maxMemory() / 1024 / 1024) + " MB";
    }

    private int getPlainTextLength(String s) {
        return s.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }

    private String LOGO() {
        return """
                                                 ░                            \s
                                                 ░░                           \s
                                                 ░░░                          \s
                                                 ░░                           \s
                                ░         ░      ░░                ░          \s
                                ░░░       ░░     ░░ ░   ░░       ░            \s
                                  ░░░      ░░ ░  ░░░   ░░      ░░             \s
                                    ░░░░    ░░░░ ░░░   ░     ░░░              \s
                                      ░░░    ░░░░░░░  ░    ░░░░               \s
                                       ░░░░░░░░░▒▒▒▒░░░░░░░░░                 \s
                                ░░    ░░░░░░▒▒▒▒▒▒▒▒▒▒▒▒░░░░░     ░░          \s
                                  ░░░░ ░░░░▒▒░░░░▒▒▓▓▓▓▒▒▒░░  ░░░░            \s
                                      ░░░░▒░░░░░░▒▓▓▓▓▓▓▒▒▒░░░                \s
                                    ░░░░░▒▒░▒░░░▒▓▓▓▓███▓▒▒▒░░░               \s
                          ░░░░░░░░░░░░░▒▒▒▒▒▒▒▓▓▓▓▓▓████▓▒▒▒░░░░░░░░░░░░░░░   \s
                              ░░░ ░░ ░░░░▒▒▒▓▓▓▓▓▓███▓▓▓▓▒▒▒░░░░              \s
                                    ░░░░░▒▒▒▓▓▓▓▓▓▓▓▓▓▓▒▒▒▒░░                 \s
                                     ░░░░░▒▒▒▓▓▓▓▓▓▒▒▒░░▒▒░░░░░               \s
                                  ░░░  ░░░░▒▒▒▒▒▒▒░░░░▒▒▒░░░░   ░░░           \s
                                ░      ░░░░░░░▒▒▒▒▒▒▒▒░░░░░░░                 \s
                                     ░░░░ ░░ ░░░░▒░░░░░ ░░░░░░                \s
                                   ░ ░░     ░   ░░░ ░ ░░    ░░░               \s
                                   ░░      ░░   ░░░  ░ ░░      ░░             \s
                                 ░░       ░░    ░░░     ░░       ░░           \s
                                ░         ░      ░░      ░          ░         \s
                                                 ░░                           \s
                                                 ░░                           \s
                                                 ░░                           \s
                                                  ░                           \s
                                                  ░                           \s""";
    }

    private String HEADER() {
        return """
                 __  __       __                        ____          __            ______       ____          __  __       ______  \s
                /\\ \\/\\ \\     /\\ \\                      /\\  _`\\       /\\ \\          /\\__  _\\     /\\  _`\\       /\\ \\/\\ \\     /\\__  _\\ \s
                \\ \\ `\\\\ \\    \\ \\ \\                     \\ \\ \\/\\_\\     \\ \\ \\         \\/_/\\ \\/     \\ \\ \\L\\_\\     \\ \\ `\\\\ \\    \\/_/\\ \\/ \s
                 \\ \\ , ` \\    \\ \\ \\  __     _______     \\ \\ \\/_/_     \\ \\ \\  __       \\ \\ \\      \\ \\  _\\L      \\ \\ , ` \\      \\ \\ \\ \s
                  \\ \\ \\`\\ \\    \\ \\ \\L\\ \\   /\\______\\     \\ \\ \\L\\ \\     \\ \\ \\L\\ \\       \\_\\ \\__    \\ \\ \\L\\ \\     \\ \\ \\`\\ \\      \\ \\ \\\s
                   \\ \\_\\ \\_\\    \\ \\____/   \\/______/      \\ \\____/      \\ \\____/       /\\_____\\    \\ \\____/      \\ \\_\\ \\_\\      \\ \\_\\
                    \\/_/\\/_/     \\/___/                    \\/___/        \\/___/        \\/_____/     \\/___/        \\/_/\\/_/       \\/_/""";
    }
}