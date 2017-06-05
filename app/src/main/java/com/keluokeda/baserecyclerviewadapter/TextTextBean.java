package com.keluokeda.baserecyclerviewadapter;


import android.widget.TextView;

import com.keluokeda.AdapterBean;
import com.keluokeda.Bind;
import com.keluokeda.baserecyclerviewadapter_api.TextViewStringBinder;

@AdapterBean( layoutId = R.layout.item_text)
public class TextTextBean {
    private String right;
    private String left;

    public TextTextBean(String right, String left) {
        this.right = right;
        this.left = left;
    }

    @Bind(viewId = R.id.tv_content_right,viewClass = TextView.class,binderClass = TextViewStringBinder.class)
    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    @Bind(viewId = R.id.tv_content_left,viewClass = TextView.class,binderClass = TextViewStringBinder.class)
    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }
}
