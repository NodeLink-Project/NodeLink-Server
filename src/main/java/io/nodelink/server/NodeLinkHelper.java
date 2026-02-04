package io.nodelink.server;

import io.nodelink.server.app.data.BONE_LOCATION;
import io.nodelink.server.app.data.CLUSTER_LOCATION;
import io.nodelink.server.command.CommandDispatcher;
import io.nodelink.server.command.CommandLogics;
import io.nodelink.server.command.CommandRegistry;
import io.nodelink.server.command.TabCompleter;
import io.nodelink.server.enums.CommandsEnum;
import io.nodelink.server.update.Version;

import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.utils.InfoCmp.Capability;

import java.util.List;

public class NodeLinkHelper {

    private static final NodeLinkHelper INSTANCE = new NodeLinkHelper();

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";
    private static final String WHITE = "\u001B[37m";
    private static final String YELLOW = "\u001B[33m";


    private final String PRODUCT = "Post Production";

    public synchronized String getPRODUCT() {
        return this.PRODUCT;
    }

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

            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer((lineReader, parsedLine, candidates) -> {
                        String buffer = parsedLine.line();
                        List<String> suggestions = appCompleter.complete(buffer);
                        for (String s : suggestions) {
                            candidates.add(new Candidate(s));
                        }
                    })
                    .option(LineReader.Option.AUTO_FRESH_LINE, true)
                    .build();

            CommandLogics logics = new CommandLogics(dispatcher, reader, terminal);

            fullClearAndRefresh(terminal);

            while (true) {
                String prompt = GREEN + "Server" + RESET + "@" + YELLOW + "NodeLink" + RESET + "-(" + RED + NodeLink.getHelper().getStatus() + RESET + ")~" + WHITE + "$ " + RESET;

                try {
                    String command = reader.readLine(prompt);

                    if (command == null || command.trim().isEmpty()) {
                        terminal.writer().println("\n");
                        continue;
                    }

                    command = command.trim();

                    try {
                        boolean handled = dispatcher.dispatch(command);

                        if (!handled) {
                            terminal.writer().println("Commande inconnue : " + command);
                        }
                    } catch (Exception e) {
                        terminal.writer().println("Erreur : La commande '" + command + "' n'est pas reconnue.");
                    }

                    terminal.writer().flush();

                    if (command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("quit")) {
                        terminal.writer().print("\u001B[r");
                        terminal.writer().print("\u001B[2J\u001B[3J");
                        terminal.puts(InfoCmp.Capability.cursor_address, 0, 0);
                        terminal.flush();
                        System.exit(0);
                    }

                } catch (UserInterruptException | EndOfFileException e) {
                    terminal.writer().print("\u001B[r");
                    terminal.writer().print("\u001B[2J\u001B[3J");
                    terminal.puts(InfoCmp.Capability.cursor_address, 0, 0);
                    terminal.flush();
                    System.exit(0);
                } catch (Exception e) {
                    terminal.writer().println("Une erreur système est survenue : " + e.getMessage());
                    terminal.writer().flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fullClearAndRefresh(Terminal terminal) {
        terminal.writer().print("\u001B[r");
        terminal.writer().print("\u001B[2J\u001B[3J");
        terminal.puts(Capability.cursor_address, 0, 0);
        terminal.flush();

        drawStaticInterface(terminal);

        terminal.writer().print("\u001B[" + (RESERVED_ROWS + 1) + ";r");
        terminal.puts(Capability.cursor_address, RESERVED_ROWS, 0);
        terminal.flush();
    }

    private String getFinalURL() {
        Object value = NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().WHICH_TYPE);

        if (value == null) {
            return "Not set...";
        } else if ((boolean) value) {
            return "Not available...";
        } else {
            return NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().URL_BONE).toString();
        }
    }

    private void updateLocationDisplay() {
        Object value = NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().WHICH_TYPE);

        if (value == null) {
            terminal.writer().println(GREEN + "  ● Type: " + RESET + "Not set...");
        } else if ((boolean) value) {
            terminal.writer().println(GREEN + "  ● Type: " + RESET + "Cluster");
        } else {
            terminal.writer().println(GREEN + "  ● Type: " + RESET + "Bone");
        }

        if (value != null && (boolean) value) {
            Object clusterLocationValue = NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().CLUSTER_LOCATION);
            if (clusterLocationValue != null) {
                CLUSTER_LOCATION clusterLocation = CLUSTER_LOCATION.valueOf(clusterLocationValue.toString());

                terminal.writer().println(GREEN + "  ● Cluster Region: " + RESET + clusterLocation.name() + " |" + " (" + getFinalURL() + ")");
            } else {
                terminal.writer().println(GREEN + "  ● Cluster Region: " + RESET + "Not set...");
            }
        } else if (value != null && !(boolean) value) {
            Object boneLocationValue = NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().BONE_LOCATION);
            if (boneLocationValue != null) {
                BONE_LOCATION boneLocation = BONE_LOCATION.valueOf(boneLocationValue.toString());

                terminal.writer().println(GREEN + "  ● Bone Location: " + RESET + boneLocation.name() + " |" + " (" + getFinalURL() + ")");
            } else {
                terminal.writer().println(GREEN + "  ● Bone Location: " + RESET + "Not set...");
            }
        }

        Object locationValue = NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().CLUSTER_LOCATION);

        terminal.writer().flush();
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

        updateLocationDisplay();


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

    public void displayHelpPage() {

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