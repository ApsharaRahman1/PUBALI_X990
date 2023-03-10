package com.vfi.android.payment.presentation.view.itemdecorations;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.vfi.android.domain.utils.LogUtil;


public class MyItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable mDivider;

    public MyItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + mDivider.getIntrinsicWidth();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private boolean isLastColum(RecyclerView parent, int spanCount, View view) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int pos = layoutManager.getPosition(view);
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {// 如果是最后一列，则不需要绘制右边
                return true;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int spanCount,
                              int childCount, View view) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int pos = layoutManager.getPosition(view);
        if (layoutManager instanceof GridLayoutManager) {
            /*
            if (childCount % spanCount == 0 || childCount % spanCount < spanCount)
                return true;
                */
            int lastRawIdx = (childCount + spanCount - 1) / spanCount;
            int curentViewRawIdx = (pos + spanCount - 1) /spanCount;
            if (lastRawIdx == curentViewRawIdx) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State
            state) {
        super.getItemOffsets(outRect, view, parent, state);
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
//        LogUtil.d("TAG", "spanCount=" + spanCount + " childCount=" + childCount);

        boolean isLastRaw = isLastRaw(parent, spanCount, childCount, view);
        boolean isLastColum = isLastColum(parent, spanCount, view);
//        LogUtil.d("TAG", "isLastRaw=" + isLastRaw + " isLastColum=" + isLastColum);
//        LogUtil.d("TAG", "intrinsicWidth=" + mDivider.getIntrinsicWidth() + " intrinsicHeight=" + mDivider.getIntrinsicHeight());

        if (isLastRaw) { // 如果是最后一行，则不需要绘制底部
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
        else if (isLastColum) { // 如果是最后一列，则不需要绘制右边
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(),
                    mDivider.getIntrinsicHeight());
        }
    }
}
