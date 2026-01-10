package io.nodelink.server.command;

import io.nodelink.server.NodeLink;
import io.nodelink.server.enums.CommandsEnum;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

public class CommandLogics {

    private void blankSpace(Terminal terminal) {
        terminal.writer().println("\n");
    }

    public CommandLogics(CommandDispatcher dispatcher, LineReader reader, Terminal terminal) {


        dispatcher.registerHandler(CommandsEnum.CLEAR, tokens -> {
            NodeLink.getHelper().fullClearAndRefresh(terminal);
        });

        dispatcher.registerHandler(CommandsEnum.HELP, tokens -> {
            //NodeLink.getHelper().displayHelp(terminal);
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_SET_CLUSTER, tokens -> {
            NodeLink.getHelper().updateStatus("Cluster");

            terminal.writer().println("Mode cluster activé");
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_SET_BONE, tokens -> {
            NodeLink.getHelper().updateStatus("Bone");

            NodeLink.getInstance().getNodeStarter().startServer();
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_DEV_API_STOP, tokens -> {


            terminal.writer().println("Spring Boot arrêté");
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_MODE_STATUS, tokens -> {
            String STATUS = NodeLink.getHelper().getStatus();
            String PRODUCT = NodeLink.getHelper().getPRODUCT();

            terminal.writer().println("Statut actuel : " + STATUS + " (" + PRODUCT + ")");
            terminal.writer().println("Opérationnel ? :" + " RED STATUS");
        });
    }
}
