package com.vfi.android.payment.presentation.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;


import com.vfi.android.domain.entities.databeans.PinKeyCoordinate;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.payment.R;

import java.util.ArrayList;

public class PinKeyBoardView
        extends View {
    private final String TAG = "ScbPinKeyBoard";
    private int contentsize;
    private int[] coordinateInt;
    private DisplayMetrics dm;
    private float height;
    private int[] nums;
    private final Point p = new Point(540, 880);
    private Paint paint;
    private Path path;
    private float width;

    public PinKeyBoardView(Context paramContext) {
        super(paramContext);
        getScreenResolution(paramContext);
        init();
    }

    public PinKeyBoardView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        getScreenResolution(paramContext);
        init();
    }

    private void drawCancelIconCenter(Canvas canvas, float centerX, float centerY) {
        Context context = getContext();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_cancel);
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        float scale = ((float) this.contentsize) / bitmapWidth;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        LogUtil.d(TAG, "contentsize=" + contentsize + " Scale=" + scale);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        if (bitmap != null) {
            float leftTopX = centerX - bitmapWidth / 2;
            float leftTopY = centerY - bitmapHeight / 2;

            canvas.drawBitmap(bitmap, leftTopX, leftTopY, paint);
        }
    }

    private void drawStringCenter(Canvas canvas, float centerX, float centerY, String paramString) {
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setStrokeWidth(4.0F);
        Paint.FontMetrics localFontMetrics = this.paint.getFontMetrics();
        int i = (int) this.paint.measureText(paramString);
        int j = (int) Math.ceil(localFontMetrics.descent - localFontMetrics.ascent);
        int k = (int) localFontMetrics.descent;
        canvas.drawText(paramString, centerX - i / 2, centerY - k + j / 2, this.paint);
    }

    private void drawDelete(Canvas canvas, float deleteCenterX, float deleteCenterY, float radius) {
        float leftPointOffset = radius / 3.0F * 2.0F;
        paint.setColor(getResources().getColor(R.color.color_gray_blue, null));
        this.path.reset();
        this.path.moveTo(deleteCenterX - radius - leftPointOffset, deleteCenterY);
        this.path.lineTo(deleteCenterX - radius, deleteCenterY - radius);
        this.path.lineTo(deleteCenterX + radius, deleteCenterY - radius);
        this.path.lineTo(deleteCenterX + radius, deleteCenterY + radius);
        this.path.lineTo(deleteCenterX - radius, deleteCenterY + radius);
        this.path.close();
        canvas.drawPath(this.path, this.paint);

        float cancelRadius = radius / 2.0F;
        this.paint.setColor(Color.WHITE);
        this.paint.setStrokeWidth(4.0F);
        canvas.drawLine(deleteCenterX - cancelRadius, deleteCenterY - cancelRadius, deleteCenterX + cancelRadius, deleteCenterY + cancelRadius, this.paint);
        canvas.drawLine(deleteCenterX - cancelRadius, deleteCenterY + cancelRadius, deleteCenterX + cancelRadius, deleteCenterY - cancelRadius, this.paint);
    }

    private void getScreenResolution(Context paramContext) {
        WindowManager windowManager = (WindowManager) paramContext.getSystemService(Context.WINDOW_SERVICE);
        this.dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(this.dm);
        Log.i("N900PinKeyBoard", "hright=" + this.dm.heightPixels + ";width" + this.dm.widthPixels);
    }

    private void init() {
        this.path = new Path();
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.nums = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
    }

    public ArrayList<PinKeyCoordinate> getCoordinateList() {
        ArrayList<PinKeyCoordinate> pinKeyCoordinates = new ArrayList<>();

        int[] localObject = new int[2];
        getLocationOnScreen(localObject);
        int keyboardLeftTopX = localObject[0];
        int keyboardLeftTopY = localObject[1];
        LogUtil.d("TAG", "keyboardLeftTopX=" + keyboardLeftTopX + " keyboardLeftTopY=" + keyboardLeftTopY);

        int x0 = keyboardLeftTopX;
        int x1 = (int) (keyboardLeftTopX + this.width / 3.0F);
        int x2 = (int) (keyboardLeftTopX + this.width / 3.0F * 2.0F);
        int x3 = (int) (keyboardLeftTopX + this.width);

        float numbRecHeight = this.height / 13.0F * 11.0F;

        int y0 = keyboardLeftTopY;
        int y1 = (int) (keyboardLeftTopY + numbRecHeight / 4.0F);
        int y2 = (int) (keyboardLeftTopY + numbRecHeight / 2.0F);
        int y3 = (int) (keyboardLeftTopY + numbRecHeight / 4.0F * 3.0F);
        int y4 = (int) (keyboardLeftTopY + numbRecHeight);

        LogUtil.d("TAG, x0=" + x0 + " x1=" + x1 + " x2=" + x2 + " x3=" + x3);
        LogUtil.d("TAG, y0=" + y0 + " y1=" + y1 + " y2=" + y2 + " y3=" + x3 + " y4=" + y4);

        int TYPE_NUM = PinKeyCoordinate.KEY_TYPE_NUM;
        int TYPE_CONFIRM = PinKeyCoordinate.KEY_TYPE_CONFIRM;
        int TYPE_CANCEL = PinKeyCoordinate.KEY_TYPE_CANCEL;
        int TYPE_DELETE = PinKeyCoordinate.KEY_TYPE_DELETE;

        int confirmRightBottmY = (int) (this.height) + keyboardLeftTopY;

        pinKeyCoordinates.add(new PinKeyCoordinate("btn_0", x0, y0, x1, y1, TYPE_NUM));
        pinKeyCoordinates.add(new PinKeyCoordinate("btn_1", x1, y0, x2, y1, TYPE_NUM));
        pinKeyCoordinates.add(new PinKeyCoordinate("btn_2", x2, y0, x3, y1, TYPE_NUM));
        pinKeyCoordinates.add(new PinKeyCoordinate("btn_3", x0, y1, x1, y2, TYPE_NUM));
        pinKeyCoordinates.add(new PinKeyCoordinate("btn_4", x1, y1, x2, y2, TYPE_NUM));
        pinKeyCoordinates.add(new PinKeyCoordinate("btn_5", x2, y1, x3, y2, TYPE_NUM));
        pinKeyCoordinates.add(new PinKeyCoordinate("btn_6", x0, y2, x1, y3, TYPE_NUM));
        pinKeyCoordinates.add(new PinKeyCoordinate("btn_7", x1, y2, x2, y3, TYPE_NUM));
        pinKeyCoordinates.add(new PinKeyCoordinate("btn_8", x2, y2, x3, y3, TYPE_NUM));
        pinKeyCoordinates.add(new PinKeyCoordinate("btn_9", x1, y3, x2, y4, TYPE_NUM));
        pinKeyCoordinates.add(new PinKeyCoordinate("btn_10", x0, y4, x3, confirmRightBottmY, TYPE_CONFIRM));
        pinKeyCoordinates.add(new PinKeyCoordinate("btn_11", x0, y3, x1, y4, TYPE_CANCEL));
        pinKeyCoordinates.add(new PinKeyCoordinate("btn_12", x2, y3, x3, y4, TYPE_DELETE));


//
//        int i = localObject[0];
//        int j = (int)(localObject[0] + this.width / 4.0F);
//        int k = (int)(localObject[0] + this.width / 2.0F);
//        int m = (int)(localObject[0] + this.width / 4.0F * 3.0F);
//        int n = (int)(localObject[0] + this.width);
//        int i1 = localObject[1];
//        int i2 = (int)(localObject[1] + this.height / 4.0F);
//        int i3 = (int)(localObject[1] + this.height / 2.0F);
//        int i4 = (int)(localObject[1] + this.height / 4.0F * 3.0F);
//        int i5 = (int)(localObject[1] + this.height);
//        if (localObject[1] != 0) {
//            this.coordinateInt = new int[] { i, i1, j, i2, j, i1, k, i2, k, i1, m, i2, i, i4, j, i5, i, i2, j, i3, j, i2, k, i3, k, i2, m, i3, m, i1, n, i3, i, i3, j, i4, j, i3, k, i4, k, i3, m, i4, i, i1, i, i1, j, i4, m, i5, n, i5, n, i5, m, i3, n, i5 };
//        }
//
//        byte[] coordinate = new byte[this.coordinateInt.length * 2];
//        i = 0;
//        j = 0;
//        while (i < this.coordinateInt.length)
//        {
//            coordinate[j] = ((byte)(this.coordinateInt[i] >> 8 & 0xFF));
//            j += 1;
//            coordinate[j] = ((byte)(this.coordinateInt[i] & 0xFF));
//            i += 1;
//            j += 1;
//        }
        return pinKeyCoordinates;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0xffffffff - 657926);
        this.paint.setAlpha(255);
        this.paint.setColor(getResources().getColor(R.color.color_gray_blue, null));

//        Context context = getContext();
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_bg);
        Rect rect = new Rect((int)0.0F, (int)(1.0F + this.height / 13.0F * 11.0F), (int)this.width, (int)this.height);
//        canvas.drawBitmap(bitmap, null, rect, paint);
        canvas.drawRect(rect, paint);

        float centerX = (this.width - 0.0F) / 2.0F;
        float centerY = (this.height - (1.0F + this.height / 13.0F * 11.0F)) / 2.0F + (1.0F + this.height / 13.0F * 11.0F);
        this.paint.setColor(Color.WHITE);
        drawStringCenter(canvas, centerX, centerY, "Next");
        this.paint.setColor(0xff37474f);

        float numbRecHeight = this.height / 13.0F * 11.0F;
        drawDelete(canvas, this.width / 6.0F * 5.0F, numbRecHeight / 8.0F * 7.0F, numbRecHeight / 8.0F / 2.0F / 3.0F * 2.0F);

        this.paint.setColor(0xff37474f);
        this.paint.setTextSize(this.contentsize + 30);
        drawStringCenter(canvas, this.width / 6.0F, numbRecHeight / 8.0F, this.nums[0] + "");
        drawStringCenter(canvas, this.width / 6.0F * 3.0F, numbRecHeight / 8.0F, this.nums[1] + "");
        drawStringCenter(canvas, this.width / 6.0F * 5.0F, numbRecHeight / 8.0F, this.nums[2] + "");
        drawStringCenter(canvas, this.width / 6.0F, numbRecHeight / 8.0F * 3.0F, this.nums[3] + "");
        drawStringCenter(canvas, this.width / 6.0F * 3.0F, numbRecHeight / 8.0F * 3.0F, this.nums[4] + "");
        drawStringCenter(canvas, this.width / 6.0F * 5.0F, numbRecHeight / 8.0F * 3.0F, this.nums[5] + "");
        drawStringCenter(canvas, this.width / 6.0F, numbRecHeight / 8.0F * 5.0F, this.nums[6] + "");
        drawStringCenter(canvas, this.width / 6.0F * 3.0F, numbRecHeight / 8.0F * 5.0F, this.nums[7] + "");
        drawStringCenter(canvas, this.width / 6.0F * 5.0F, numbRecHeight / 8.0F * 5.0F, this.nums[8] + "");
        drawStringCenter(canvas, this.width / 6.0F * 3.0F, numbRecHeight / 8.0F * 7.0F, this.nums[9] + "");

        drawCancelIconCenter(canvas, this.width / 6.0F, numbRecHeight / 8.0F * 7.0F);
//        drawStringCenter(canvas, this.width / 6.0F, numbRecHeight / 8.0F * 7.0F, "·");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.width = MeasureSpec.getSize(widthMeasureSpec);
        this.height = (this.width / 14.3F * 13.0F);

        widthMeasureSpec = 15;
        for (; ; ) {
            if (widthMeasureSpec < 100) {
                this.paint.setTextSize(widthMeasureSpec);
                Paint.FontMetrics localFontMetrics = this.paint.getFontMetrics();
                float f1 = localFontMetrics.descent;
                float f2 = localFontMetrics.ascent;
                float f3 = this.paint.measureText("确认");

                if ((f1 - f2 > this.height / 8.0F) || (f3 > this.width / 8.0F)) {
                    this.contentsize = widthMeasureSpec;
                    setMeasuredDimension((int) this.width, (int) this.height);
                    break;
                }
            } else {
                this.contentsize = 46;
                setMeasuredDimension((int) this.width, (int) this.height);
                break;
            }
            widthMeasureSpec += 1;
        }
    }

    public void setRandomNumber(int[] paramArrayOfInt) {
        this.nums = paramArrayOfInt;
        invalidate();
    }
}

