package com.vfi.android.payment.presentation.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;

public class SelectItemAdapter extends RecyclerView.Adapter {
    private final String TAG = this.getClass().getSimpleName();

    private OnItemClickListener onItemClickListener;
    private String[] items;
    private boolean isTwoLineShow;

    public SelectItemAdapter(String[] items) {
        if (items == null) {
            return;
        }

        LogUtil.d(TAG, "SelectItemAdapter item size=" + items.length);
        this.items = items;
    }

    public boolean isTwoLineShow() {
        return isTwoLineShow;
    }

    public void setTwoLineShow(boolean twoLineShow) {
        isTwoLineShow = twoLineShow;
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewRoot;
        if (isTwoLineShow) {
            viewRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_select_2line_item, parent, false);
        } else {
            viewRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_select_item, parent, false);
        }
        viewRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, (int)v.getTag());
                }
            }
        });
        return new SelectViewHolder(viewRoot);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SelectViewHolder selectViewHolder = (SelectViewHolder) holder;
        selectViewHolder.getRootView().setTag(position);

        selectViewHolder.getTvItem().setText(items[position]);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    private class SelectViewHolder extends RecyclerView.ViewHolder {
        private View rootView;
        private TextView tvItem;

        public SelectViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            tvItem = rootView.findViewById(R.id.tv_item);
        }


        public View getRootView() {
            return rootView;
        }

        public TextView getTvItem() {
            return tvItem;
        }
    }
}
