package com.keluokeda.baserecyclerviewadapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.keluokeda.AdapterBean;
import com.keluokeda.Bind;
import com.keluokeda.baserecyclerviewadapter_api.ImageViewIntegerBinder;
import com.keluokeda.baserecyclerviewadapter_api.TextViewStringBinder;

@AdapterBean( layoutId = R.layout.item_image)
public class ImageTextBean {
    private Integer mInteger;
    private String mString;

    public ImageTextBean(Integer integer, String string) {
        mInteger = integer;
        mString = string;
    }

    @Bind(viewId = R.id.iv_image,viewClass = ImageView.class,binderClass = ImageViewIntegerBinder.class)
    public Integer getInteger() {
        return mInteger;
    }

    public void setInteger(Integer integer) {
        mInteger = integer;
    }

    @Bind(viewId = R.id.tv_content,viewClass = TextView.class,binderClass = TextViewStringBinder.class)
    public String getString() {
        return mString;
    }

    public void setString(String string) {
        mString = string;
    }
}
