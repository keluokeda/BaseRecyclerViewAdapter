package com.keluokeda.baserecyclerviewadapter_api;


import com.keluokeda.MultipleItem;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RecyclerViewAdapterFactory {

    private static final Map<String,Class> sMap = new TreeMap<>();

    private RecyclerViewAdapterFactory() {
        throw new RuntimeException("RecyclerViewAdapterFactory can not instance");
    }


    @SuppressWarnings("unchecked")
    public static BaseRecyclerViewAdapter createAdapter(Class itemClass, List<?> list) {
        BaseRecyclerViewAdapter adapter = null;
        try {

            String fullName = itemClass.getName();
            Class clazz = sMap.get(fullName);
            if (clazz == null) {
                clazz = Class.forName(fullName + "_RecyclerView_Adapter");
                sMap.put(fullName, clazz);
            }


            Constructor constructor = clazz.getConstructor(List.class);
            adapter = (BaseRecyclerViewAdapter) constructor.newInstance(list);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return adapter;
    }
}
