package com.keluokeda.baserecyclerviewadapter_api;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> {
    private List<MultiItem> mMultiItemList;
    private LayoutInflater mLayoutInflater;


    public BaseRecyclerViewAdapter(List list) {
        mMultiItemList = new ArrayList<>(list.size());
        for (Object o : list) {
            mMultiItemList.add(createMultiItem(o));
        }
    }

//    public BaseRecyclerViewAdapter(List<MultiItem> multiItemList) {
//        mMultiItemList = multiItemList == null ? new ArrayList<MultiItem>(0) : multiItemList;
//    }

    protected abstract MultiItem createMultiItem(Object object);

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater(parent.getContext()).inflate(viewType, parent, false);
        return createViewHolder(view, viewType);
    }

    private LayoutInflater getLayoutInflater(Context context) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(context);
        }
        return mLayoutInflater;
    }


    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.bindData(mMultiItemList.get(position).getBean(), position);
    }


    @Override
    public int getItemCount() {
        return mMultiItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMultiItemList.get(position).getItemType();
    }


    protected abstract BaseRecyclerViewHolder createViewHolder(View itemView, int itemType);


}
