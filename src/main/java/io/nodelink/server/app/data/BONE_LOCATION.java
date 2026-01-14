package io.nodelink.server.app.data;

public enum BONE_LOCATION {

    // --- EUROPE ---
    EUROPE_WEST("west.eu.bone", 48.8566, 2.3522),    // Paris
    EUROPE_EAST("east.eu.bone", 52.2297, 21.0122),   // Varsovie
    EUROPE_NORTH("north.eu.bone", 59.3293, 18.0686), // Stockholm
    EUROPE_SOUTH("south.eu.bone", 41.9028, 12.4964), // Rome

    // --- NORTH AMERICA ---
    NORTH_AMERICA_WEST("west.na.bone", 34.0522, -118.2437), // Los Angeles
    NORTH_AMERICA_EAST("east.na.bone", 40.7128, -74.0060),  // New York
    NORTH_AMERICA_NORTH("north.na.bone", 53.5461, -113.4938), // Edmonton (Canada)
    NORTH_AMERICA_SOUTH("south.na.bone", 19.4326, -99.1332),  // Mexico City

    LATAM_AMERICA("latam.la.bone", -23.5505, -46.6333), // São Paulo

    // --- ASIA ---
    ASIA_EAST("east.asia.bone", 35.6762, 139.6503),  // Tokyo
    ASIA_SOUTH("south.asia.bone", 19.0760, 72.8777), // Mumbai
    ASIA_NORTH("north.asia.bone", 56.0153, 92.8932), // Krasnoyarsk (Siberia)
    ASIA_WEST("west.asia.bone", 32.4279, 53.6880),   // Iran (Central Asia West)

    OCEANIA("oceania.bone", -25.2744, 133.7751), // Australie (Centre)

    // --- MIDDLE EAST & AFRICA ---
    MIDDLE_EAST("me.bone", 25.2048, 55.2708),         // Dubai
    AFRICA_NORTH("north.africa.bone", 30.0444, 31.2357), // Le Caire
    AFRICA_SOUTH("south.africa.bone", -25.7479, 28.2293); // Pretoria

    private final String locationBone;
    private final double latitude;
    private final double longitude;

    BONE_LOCATION(String locationBone, double latitude, double longitude) {
        this.locationBone = locationBone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNameLocationBone() {
        return this.name();
    }

    public String getLocation() {
        return this.locationBone;
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