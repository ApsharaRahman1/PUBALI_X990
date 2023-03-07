package com.vfi.android.payment.presentation.view.widget;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;

/**
 * 解决了原生SearchView再输入内容为空的情况下不触发OnQueryTextListener的问题
 */
public class EmptyQuerySearchView extends SearchView {
    SearchView.SearchAutoComplete mSearchSrcTextView;
    OnQueryTextListener listener;

    public EmptyQuerySearchView(Context context) {
        super(context);
    }

    public EmptyQuerySearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyQuerySearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnQueryTextListener(OnQueryTextListener listener) {
        super.setOnQueryTextListener(listener);
        this.listener = listener;
        mSearchSrcTextView = this.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mSearchSrcTextView.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (listener != null) {
                listener.onQueryTextSubmit(getQuery().toString());
            }
            return true;
        });
    }
}
