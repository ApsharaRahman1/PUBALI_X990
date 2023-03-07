package com.vfi.android.payment.presentation.view.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vfi.android.domain.utils.LogUtil;


public class HistoryScrollListener extends RecyclerView.OnScrollListener {
    private String TAG = "HistoryScrollListener";
    //声明一个LinearLayoutManager
    private LinearLayoutManager mLinearLayoutManager;

    //当前页，从0开始
    private int currentPage = 0;
    //已经加载出来的Item的数量
    private int totalItemCount;

    //主要用来存储上一个totalItemCount
    private int previousTotal = 0;

    //在屏幕上可见的item数量,recycler view只会绘制一屏幕的child，还未滑到的和滑出的都未绘制
    private int visibleItemCount;

    //在屏幕可见的Item中的第一个
    private int firstVisibleItem;

    //是否正在上拉数据
    private boolean loading = true;
    private LoadMoreCallback callback;


    public HistoryScrollListener(LinearLayoutManager linearLayoutManager, LoadMoreCallback callback) {
        this.mLinearLayoutManager = linearLayoutManager;
        this.callback = callback;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        LogUtil.d(TAG, "onscroll dx:" + dx + ",dy:" + dy);
        visibleItemCount = recyclerView.getChildCount();
        //因为最后一个是footer，不能参与这里的计数的
        totalItemCount = mLinearLayoutManager.getItemCount() - 1;
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        if (loading) {
            if (totalItemCount > previousTotal) {
                //说明数据已经加载结束
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        //当没有在加载且 已经加载出来的item数量-屏幕上可见的item数量<=屏幕上可见的第一个item的序号（即已经滑倒底的时候）
        //执行相应的操作
        LogUtil.d(TAG, "total:" + totalItemCount + ",visible:" + visibleItemCount + ",fist visible:" + firstVisibleItem);
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem ) {
            currentPage++;
            callback.loadMore(currentPage);
            loading = true;
        }
    }

    public void clear() {
        currentPage = 0;
        previousTotal = 0;
        loading = true;
    }

    public interface LoadMoreCallback {
        void loadMore(int page);
    }
}
