package com.keluokeda;


import com.squareup.javapoet.ClassName;

class TypeUtil {
    static final ClassName CLASSNAME_VIEW = ClassName.get("android.view", "View");
    static final ClassName CLASSNAME_RECYCLERVIEW_ADAPTER = ClassName.get("com.keluokeda.baserecyclerviewadapter_api", "BaseRecyclerViewAdapter");
    static final ClassName CLASSNAME_VIEW_HOLDER = ClassName.get("com.keluokeda.baserecyclerviewadapter_api", "BaseRecyclerViewHolder");
    static final ClassName CLASSNAME_BEAN_BINDER = ClassName.get("com.keluokeda.baserecyclerviewadapter_api", "BeanBinder");
    static final ClassName CLASSNAME_MULTI_ITEM = ClassName.get("com.keluokeda.baserecyclerviewadapter_api", "MultiItem");
}
