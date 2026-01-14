package io.nodelink.server.app.data;

public enum CLUSTER_LOCATION {
    NBG_DE_EU_CLUSTER("nbg.de.eu.cluster", "NBG-DE-EU-CLUSTER", 49.4521, 11.0767);

    private final String locationCluster;
    private final String nameCluster;
    private final double latitude;
    private final double longitude;


    CLUSTER_LOCATION(String locationCluster, String nameCluster, double latitude, double longitude) {
        this.locationCluster = locationCluster;
        this.nameCluster = nameCluster;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocationCluster() {
        return locationCluster;
    }

    public String getNameCluster() {
        return nameCluster;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double[] getCoords() {
        return new double[]{latitude, longitude};
    }

}
