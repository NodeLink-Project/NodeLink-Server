package io.nodelink.server;

import io.nodelink.server.command.CommandDispatcher;
import io.nodelink.server.command.CommandLogics;
import io.nodelink.server.command.CommandRegistry;
import io.nodelink.server.command.TabCompleter;
import io.nodelink.server.enums.CommandsEnum;
import io.nodelink.server.update.Version;

import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;

import java.nio.file.Paths;

import java.util.List;

public class NodeLinkHelper {

    private static final NodeLinkHelper INSTANCE = new NodeLinkHelper();

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";
    private static final String WHITE = "\u001B[37m";
    private static final String YELLOW = "\u001B[33m";


    private static String PRODUCT = "Post Production";

    private Terminal terminal;

    private String STATUS = "OFFLINE";

    public synchronized void updateStatus(String newStatus) {
        this.STATUS = (newStatus == null) ? "" : newStatus;
        if (this.terminal != null) {
            fullClearAndRefresh(this.terminal);
        }
    }

    public synchronized String getStatus() {
        return this.STATUS;
    }

    private static final int RESERVED_ROWS = 36;

    public static NodeLinkHelper getHelper() {
        return INSTANCE;
    }

    public void INITIALIZE() {
        initTerminal();
    }

    private void initTerminal() {
        try {
            terminal = TerminalBuilder.builder()
                    .name("NodeLink Server")
                    .system(true)
                    .build();

            CommandRegistry registry = new CommandRegistry();
            registry.registerParentEnum(CommandsEnum.class);

            TabCompleter appCompleter = new TabCompleter(registry);

            CommandDispatcher dispatcher = new CommandDispatcher(registry);

            CommandLogics logics = new CommandLogics(dispatcher, terminal);

            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer((lineReader, parsedLine, candidates) -> {
                        String buffer = parsedLine.line();
                        List<String> suggestions = appCompleter.complete(buffer);
                        for (String s : suggestions) {
                            candidates.add(new Candidate(s));
                        }
                    })
                    .variable(LineReader.HISTORY_FILE, Paths.get("bin/history.txt"))
                    .option(LineReader.Option.AUTO_FRESH_LINE, true)
                    .build();

            fullClearAndRefresh(terminal);

            while (true) {
                String prompt = GREEN + "Server" + RESET + "@" + YELLOW + "NodeLink" + RESET + "-(" + RED + STATUS + RESET + ")~" + WHITE + "$ " + RESET;

                try {
                    String command = reader.readLine(prompt);

                    if (command == null || command.equalsIgnoreCase("exit" ) || command.equalsIgnoreCase("quit")) {
                        System.exit(1);
                    }

                    if (command.equalsIgnoreCase("clear")) {
                        fullClearAndRefresh(terminal);
                        continue;
                    }

                    boolean handled = dispatcher.dispatch(command);
                    if (!handled) {
                        terminal.writer().println("Commande inconnue : " + command);
                        terminal.flush();
                    }

                } catch (UserInterruptException | EndOfFileException e) {
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            String leftPart = (i >= 10 && (i - 10) < headerLines.length) ? BLUE + headerLines[i - 10] + RESET : "";
            String rightPart = logoLines[i];

            int leftLen = getPlainTextLength(leftPart);
            int rightLen = getPlainTextLength(rightPart);

            int padding = width - leftLen - rightLen;
            if (padding < 1) padding = 1;

            terminal.writer().println(leftPart + " ".repeat(padding) + rightPart);
        }

        terminal.writer().println(GREEN + "  ● Status: " + RESET + YELLOW + PRODUCT + RESET);
        terminal.writer().println(GREEN + "  ● Version: " + RESET + Version.VERSION);

        int currentLine = logoHeight + 4;
        for (int i = currentLine; i < RESERVED_ROWS - 1; i++) {
            terminal.writer().println("");
        }

        terminal.writer().print(BLUE + "─".repeat(width) + RESET);
        terminal.flush();
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
                 __  __       __                        ____         ____          ____         __  __        ____          ____      \s
                /\\ \\/\\ \\     /\\ \\                      /\\  _`\\      /\\  _`\\       /\\  _`\\      /\\ \\/\\ \\      /\\  _`\\       /\\  _`\\    \s
                \\ \\ `\\\\ \\    \\ \\ \\                     \\ \\,\\L\\_\\    \\ \\ \\L\\_\\     \\ \\ \\L\\ \\    \\ \\ \\ \\ \\     \\ \\ \\L\\_\\     \\ \\ \\L\\ \\  \s
                 \\ \\ , ` \\    \\ \\ \\  __     _______     \\/_\\__ \\     \\ \\  _\\L      \\ \\ ,  /     \\ \\ \\ \\ \\     \\ \\  _\\L      \\ \\ ,  /  \s
                  \\ \\ \\`\\ \\    \\ \\ \\L\\ \\   /\\______\\      /\\ \\L\\ \\    \\ \\ \\L\\ \\     \\ \\ \\\\ \\     \\ \\ \\_/ \\     \\ \\ \\L\\ \\     \\ \\ \\\\ \\ \s
                   \\ \\_\\ \\_\\    \\ \\____/   \\/______/      \\ `\\____\\    \\ \\____/      \\ \\_\\ \\_\\    \\ `\\___/      \\ \\____/      \\ \\_\\ \\_\\
                    \\/_/\\/_/     \\/___/                    \\/_____/     \\/___/        \\/_/\\/ /     `\\/__/        \\/___/        \\/_/\\/ /""";
    }
}