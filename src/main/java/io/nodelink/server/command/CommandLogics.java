package io.nodelink.server.command;

import io.nodelink.server.NodeLink;
import io.nodelink.server.enums.CommandsEnum;
import org.jline.terminal.Terminal;

public class CommandLogics {

    public CommandLogics(CommandDispatcher dispatcher, Terminal terminal) {
        dispatcher.registerHandler(CommandsEnum.SERVICE_SET_CLUSTER, tokens -> {
            terminal.writer().println("Mode cluster activé");

            NodeLink.getHelper().updateStatus("Cluster");
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_SET_BONE, tokens -> {
            terminal.writer().println("Mode bone activé");

            NodeLink.getHelper().updateStatus("Bone");
        });
    }
}
