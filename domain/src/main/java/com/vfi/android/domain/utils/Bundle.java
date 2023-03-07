package com.vfi.android.domain.utils;

import java.util.LinkedHashMap;

/**
 * Created by fusheng.z on 2017/12/1.
 */

public class Bundle {
    private LinkedHashMap<String, Object> bundle;

    public Bundle(){
        bundle = new LinkedHashMap<>();
    }

    public int getInt(String key){
        for (String tempKey : bundle.keySet()) {
            if( tempKey.equals(key) ){
                return (int)bundle.get(key);
            }
        }
        return 0;
    }

    public String getString(String key){
        for (String tempKey : bundle.keySet()) {
            if( tempKey.equals(key) ){
                return (String)bundle.get(key);
            }
        }
        return null;
    }

    public void putInt(String key, int value){
        bundle.put(key, value);
    }

    public void putString(String key, String value){
        bundle.put(key, value);
    }
}
