package io.nodelink.server;

import io.nodelink.server.log.Logger;
import io.nodelink.server.update.Updater;

import java.io.File;

public class NodeLink extends NodeLinkHelper {

    private static final NodeLink INSTANCE = new NodeLink();

    static void main(String[] args) {
        try {
            NodeLink.getInstance().getUpdater().checkForUpdates();

            for (int i = 0; i < args.length; i++) {
                if ("--delete-old".equals(args[i]) && (i + 1) < args.length) {
                    String oldJarPath = args[i + 1];
                    File oldFile = new File(oldJarPath);
                    Thread.sleep(2000);

                    if (oldFile.exists()) {
                        boolean deleted = oldFile.delete();
                        if (!deleted) {
                            System.err.println("Failed to delete old JAR: " + oldJarPath);
                        } else {
                            System.out.println("Old JAR deleted successfully: " + oldJarPath);
                        }
                    }
                    break;
                }
            }

            // Runtime Code //

            getHelper().INITIALIZE();

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

