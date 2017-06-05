package com.keluokeda.baserecyclerviewadapter_api;

import android.widget.ImageView;


public class ImageViewIntegerBinder implements ViewValueBinder<ImageView,Integer> {
    @Override
    public void bind(ImageView imageView, Integer integer) {
        imageView.setImageResource(integer);
    }
}
