package io.nodelink.server.utils;

import java.util.HashMap;

public class StoreData {

    private static final StoreData INSTANCE = new StoreData();

    public final HashMap<String, Object> DATA = new HashMap<>();

    public final String WHICH_TYPE = "which_type";
    public final String CLUSTER_LOCATION = "cluster_location";
    public final String BONE_LOCATION = "bone_location";

    public final String ID = "id";

    // DATA FROM BONE
    public final String ID_BONE = "idBone";
    public final String TYPE_BONE = "boneType";
    public final String URL_BONE = "urlBone";

    // DATA FROM CLUSTER
    public final String ID_CLUSTER = "idCluster";
    public final String TYPE_CLUSTER = "clusterType";
    public final String URL_CLUSTER = "urlCluster";

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
