package io.nodelink.server.app.data;

public enum BONE_LOCATION {

    ///

    EUROPE_WEST("west.eu.bone"),
    EUROPE_EAST("east.eu.bone"),
    EUROPE_NORTH("north.eu.bone"),
    EUROPE_SOUTH("south.eu.bone"),

    ///

    NORTH_AMERICA_WEST("west.na.bone"),
    NORTH_AMERICA_EAST("east.na.bone"),
    NORTH_AMERICA_NORTH("north.na.bone"),
    NORTH_AMERICA_SOUTH("south.na.bone"),

    LATAM_AMERICA("latam.la.bone"),

    ///

    ASIA_EAST("east.asia.bone"),
    ASIA_SOUTH("south.asia.bone"),
    ASIA_NORTH("north.asia.bone"),
    ASIA_WEST("west.asia.bone"),

    OCEANIA("oceania.bone"),

    ///

    MIDDLE_EAST("me.bone"),
    AFRICA_NORTH("north.africa.bone"),
    AFRICA_SOUTH("south.africa.bone"),

    ///

    ;

    private final String locationBone;

    BONE_LOCATION(String locationBone) {
        this.locationBone = locationBone;
    }

    public String getLocation() {
        return this.locationBone;
    }

}
