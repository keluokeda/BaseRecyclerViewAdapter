package com.keluokeda.baserecyclerviewadapter_api;

import android.widget.TextView;



public class TextViewStringBinder implements ViewValueBinder<TextView,String> {
    @Override
    public void bind(TextView textView, String s) {
        textView.setText(s);
    }
}
