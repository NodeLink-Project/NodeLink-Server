package io.nodelink.server.utils;

import java.util.HashMap;

public class StoreData {

    private static final StoreData INSTANCE = new StoreData();

    public final HashMap<String, Object> DATA = new HashMap<>();

    public final String WHICH_TYPE = "which_type";
    public final String CLUSTER_LOCATION = "cluster_location";
    public final String BONE_LOCATION = "bone_location";
    public final String FINAL_URL = "final_url";

    // DATA FROM BONE

    public void put(String key, Object value) {
        DATA.put(key, value);
    }

    public Object get(String key) {
        return DATA.get(key);
    }

    public Object remove(Object key) {
        return DATA.remove(key);
    }

    public static StoreData getStoreDataSingleton() {
        return INSTANCE;
    }
}
