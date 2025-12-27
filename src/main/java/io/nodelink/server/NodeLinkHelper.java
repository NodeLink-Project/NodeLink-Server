package io.nodelink.server;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.nio.file.Paths;

public class NodeLinkHelper {

    private static final NodeLinkHelper INSTANCE = new NodeLinkHelper();

    protected void INITIALIZE() {
        NodeLink.getInstance().getUpdater().checkForUpdates();

        initTerminal();

    }

    private void initTerminal() {
        try {
            Terminal TERMINAL = TerminalBuilder.builder()
                    .name("NodeLink Server")
                    .system(true)
                    .build();

            LineReader READER = LineReaderBuilder.builder()
                    .terminal(TERMINAL)
                    .variable(LineReader.HISTORY_FILE, Paths.get("bin/history.txt"))
                    .option(LineReader.Option.AUTO_FRESH_LINE, true)
                    .option(LineReader.Option.HISTORY_BEEP, true)
                    .build();

        } catch (Exception e) {
            NodeLink.getInstance().getLogger().ERROR(e.getMessage());
        }
    }

    /// Getters ///

    public static NodeLinkHelper getHelper() {
        return INSTANCE;
    }
}
