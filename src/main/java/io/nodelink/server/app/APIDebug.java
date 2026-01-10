package io.nodelink.server.app;

import io.nodelink.server.NodeLink;

public class APIDebug {

    static void main(String[] args) {
        System.out.println("API Debugging...");

        NodeLink.getInstance().getNodeStarter().startServer();

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
