package io.nodelink.server.command;

import io.nodelink.server.NodeLink;
import io.nodelink.server.app.data.BONE_LOCATION;
import io.nodelink.server.app.data.CLUSTER_LOCATION;
import io.nodelink.server.enums.CommandsEnum;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.w3c.dom.Node;

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
            NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().WHICH_TYPE, true);

            NodeLink.getHelper().fullClearAndRefresh(terminal);

            NodeLink.getInstance().getClusterStarter().startServer();
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_SET_BONE, tokens -> {
            NodeLink.getHelper().updateStatus("Bone");
            NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().WHICH_TYPE, false);

            NodeLink.getHelper().fullClearAndRefresh(terminal);

            NodeLink.getInstance().getNodeStarter().startServer();
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_DEV_API_STOP, tokens -> {


            terminal.writer().println("Spring Boot arrêté");
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_SET_BONE_LOCATION, tokens -> {
            Object value = NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().WHICH_TYPE);

            if (value == null || (boolean) value) {
                terminal.writer().println("Erreur : Mode Bone non activé");
                return;
            }

            if (tokens.length < 5) {
                terminal.writer().println("Usage: service set bone location <location>");
                return;
            }

            String locationInput = tokens[4].toUpperCase();
            try {
                BONE_LOCATION boneLocation = BONE_LOCATION.valueOf(locationInput);
                terminal.writer().println("Emplacement du bone défini sur : " + boneLocation.name());
                NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().BONE_LOCATION, boneLocation.name());
            } catch (IllegalArgumentException e) {
                terminal.writer().println("Emplacement invalide. Veuillez choisir parmi les emplacements disponibles.");
            }
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_SET_CLUSTER_LOCATION, tokens -> {
            Object value = NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().WHICH_TYPE);

            if (value == null || !(boolean) value) {
                terminal.writer().println("Erreur : Mode cluster non activé");
                return;
            }

            if (tokens.length < 5) {
                terminal.writer().println("Usage: service set cluster location <location>");
                return;
            }

            String locationInput = tokens[4].toUpperCase();
            try {
                CLUSTER_LOCATION clusterLocation = CLUSTER_LOCATION.valueOf(locationInput);
                terminal.writer().println("Emplacement du cluster défini sur : " + clusterLocation.name());
                NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().CLUSTER_LOCATION, clusterLocation.name());
            } catch (IllegalArgumentException e) {
                terminal.writer().println("Emplacement invalide. Veuillez choisir parmi les emplacements disponibles.");
            }
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_MODE_STATUS, tokens -> {
            String STATUS = NodeLink.getHelper().getStatus();
            String PRODUCT = NodeLink.getHelper().getPRODUCT();

            terminal.writer().println("Statut actuel : " + STATUS + " (" + PRODUCT + ")");
            terminal.writer().println("Opérationnel ? :" + " RED STATUS");
        });
    }
}
