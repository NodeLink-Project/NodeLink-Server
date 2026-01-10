package io.nodelink.server.app.data;

public enum CLUSTER_LOCATION {

    NBG_DE_EU_CLUSTER("nbg.de.eu.cluster"),
    ;

    ///

    private final String locationCluster;

    CLUSTER_LOCATION(String locationCluster) {
        this.locationCluster = locationCluster;
    }

    public String getLocation() {
        return this.locationCluster;
    }

}
