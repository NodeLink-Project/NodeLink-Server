package io.nodelink.server;

import io.nodelink.server.log.Logger;
import io.nodelink.server.update.Updater;

public class NodeLink extends NodeLinkHelper {

    private static final NodeLink INSTANCE = new NodeLink();

    static void main(String[] args) {
        try {
            NodeLink.getInstance().getUpdater().handleArguments(args);
            NodeLink.getInstance().getUpdater().checkForUpdates().block();

        } catch (Exception e) {
            getInstance().getLogger().ERROR("Error deleting old JAR: " + e.getMessage());
        }
    }



    /// Getters ///

    public static NodeLink getInstance() {
        return INSTANCE;
    }

    public Logger getLogger() {
        return Logger.getLoggerSingleton();
    }

    public Updater getUpdater() {
        return Updater.getUpdaterSingleton();
    }

}

