package org.best.taskboard.models;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    private static Cache mInstance = new Cache();
    private final Map<String, Object> mStorage = new HashMap<>();

    public static Cache getInstance() {
        return mInstance;
    }

    private Cache() {
    }

    public Object put(String key, Object value) {
        mStorage.put(key, value);
        return value;
    }

    public Object get(String key) {
        return mStorage.get(key);
    }

    public Object getOrPut(String key, Object value) {
        Object val = get(key);
        if (val == null) {
            val = put(key, value);
        }
        return val;
    }
}
