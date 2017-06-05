package com.keluokeda.baserecyclerviewadapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.keluokeda.baserecyclerviewadapter_api.BaseRecyclerViewAdapter;
import com.keluokeda.baserecyclerviewadapter_api.MultiItem;
import com.keluokeda.baserecyclerviewadapter_api.RecyclerViewAdapterFactory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        int max = 100;
        List<Object> multiItems = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            Object item;
            if (i % 3 == 0) {
                item = new TextTextBean("right =" + i, "left = " + i);
            } else if (i % 3 == 1) {
                item = new ImageTextBean(R.mipmap.ic_launcher, "pos = " + i);
            } else {
                item = new ImageTextBean(R.mipmap.ic_launcher_round, "pos = " + i);
            }
            multiItems.add(item);

        }
        BaseRecyclerViewAdapter adapter = RecyclerViewAdapterFactory.createAdapter(AdapterItem.class, multiItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }
}
