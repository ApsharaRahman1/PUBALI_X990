package com.vfi.android.payment.presentation.view.itemdecorations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.vfi.android.payment.R;
import com.vfi.android.payment.presentation.utils.DensityUtil;


public class ItemDecorationWithoutFooter extends RecyclerView.ItemDecoration {
    private Paint paint;
    private int width;

    public ItemDecorationWithoutFooter(Context context) {
        paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.color_gray));
        width = DensityUtil.dip2px(context, 1);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + width;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + width;
            c.drawRect(left, top, right, bottom, paint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (!isLastRow(view, parent))
            outRect.set(0, 0, 0, width);

    }

    public boolean isLastRow(View view, RecyclerView parent) {
        return parent.getLayoutManager().getPosition(view) == parent.getAdapter().getItemCount() - 1;
    }


}
