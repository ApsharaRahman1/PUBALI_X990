package com.vfi.android.payment.presentation.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.vfi.android.domain.utils.BitmapUtil;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.ZlibUtil;
import com.vfi.android.payment.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by chong.z on 2018/3/26.
 */
public class ESignBoradSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "esign_SurfaceDrawCanvas";

    private static final int BACK_COLOR = 0xfff0e9e8; //背景颜色

    private Paint paint, paintText;//
    private Canvas canvas;//
    private Bitmap bitmap;//
    // private Bitmap originBitmap;//
    private int mov_x;//
    private int mov_y;
    private String signaValue;
    //final static int BUFFER_SIZE = 10000;
    //public byte[] top;
    private int width, height;
    SurfaceHolder holder;

    private int textW = 0;
    private int textH = 0;
    //private int reSign_x = 0;
    //private int reSign_y = 0;

    private boolean isOnTouch = false;//
    private int down_x = 0;
    private int down_y = 0;

    private Context context;

    public ESignBoradSurfaceView(Context context, String signValue, int screenWidth, int width, int height) {
        super(context);

        int leftOffset = 0;
        this.context = context;

        holder = this.getHolder();
        holder.addCallback(this);

        this.signaValue = signValue;
        this.width = width;
        this.height = height;
        Log.d("", "screenWidth" + screenWidth);
        Log.d("", "width" + width);
        if (width > screenWidth) {
            leftOffset = (width - screenWidth) / 2;
        }
        Log.d("", "" + leftOffset);
        //背景
        setZOrderOnTop(true); //不透明

        //holder.setFormat(PixelFormat.TRANSLUCENT); //tony 不起什么作用
        init();
    }

    private void init() {
        Paint mPaint;
        int zoomMultiples;// 放大倍数
        Rect textRect;

        zoomMultiples = height / 100;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        // paint.setStyle(Style.FILL);//
        paint.setStrokeWidth(4);//
        // paint.setFakeBoldText(true);
        paint.setLinearText(true);
        // paint.setSubpixelText(true);
        // paint.setFilterBitmap(true);
        paint.setStrokeCap(Paint.Cap.ROUND);//
        paint.setDither(true);//
        paint.setAntiAlias(true);// ?


        paintText = new Paint();
        String familyName = "Times New Roman";
        Typeface font = Typeface.create(familyName, Typeface.NORMAL);
        paintText.setTypeface(font);
        paintText.setTextSize(15 * zoomMultiples); //tony 特征码字体大小
        paintText.setAntiAlias(true);// 锯齿不显示
        paintText.setColor(Color.DKGRAY); //tony 特征码字体颜色
        paintText.setStyle(Paint.Style.STROKE);// 设置非填充
        // paintText.setStrokeWidth(4* zoomMultiples);// 笔宽5像素

        mPaint = new Paint();
        mPaint.setStrokeWidth(3);
        mPaint.setTextSize(40);
        mPaint.setColor(Color.GRAY);//字的颜色
        mPaint.setTextAlign(Paint.Align.LEFT);

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); //
        bitmap.eraseColor(BACK_COLOR); //背景颜色
        canvas = new Canvas();
        canvas.setBitmap(bitmap);

        textRect = new Rect();
        // 获取特征值所需矩阵
        paintText.getTextBounds(signaValue, 0, signaValue.length(), textRect);
        textW = textRect.width();// 获取特征值矩阵的宽
        textH = textRect.height();// 获取特征值矩阵的高

/*      never used
        textRect = new Rect((width - textW) / 2 + leftOffset,
                (height - textH) / 2, (width - textW) / 2 + textW + leftOffset,
                (height - textH) / 2 + textH);*/
        // canvas.drawRect(textRect, paint);
        // 画上特征值， 不知为何画文字时会以左下为坐标原点
        canvas.drawText(this.signaValue, (width - textW) / 2, (height - textH)
                / 2 + textH, paintText);

        //canvas.setBitmap(bitmap);

        Log.d("Surface", "w:" + bitmap.getWidth());
        Log.d("Surface", "h:" + bitmap.getHeight());
        //myDrawText(canvas, mPaint);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new MyThread().start();
        threadFlag = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        threadFlag = false;
    }

    boolean threadFlag = false;

    class MyThread extends Thread {
        @Override
        public void run() {
            while (threadFlag) {
                long startTime = System.currentTimeMillis();

                myDraw();
                long endTime = System.currentTimeMillis();
                /*
                 * 1000ms /60 = 16.67ms
                 */
                if (endTime - startTime < 30) {
                    try {
                        Thread.sleep(30 - (endTime - startTime));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //重签
    public void resetCanvas() {
        isOnTouch = false;
        bitmap = Bitmap.createBitmap(width + 30, height, Bitmap.Config.ARGB_8888); // 设置位图的宽高
        bitmap.eraseColor(BACK_COLOR); //背景颜色
        canvas.setBitmap(bitmap);
        canvas.drawText(this.signaValue, (width - textW) / 2, (height - textH) / 2 + textH, paintText);
        canvas.setBitmap(bitmap);
    }

    public Bitmap saveCanvas() {
        if (isOnTouch) {
//            saveBitmapFile(bitmap);
            if (bitmap == null) {
                return null;
            }

            //myDrawText(canvas, mPaint);
            return getBitmap(bitmap);
        } else {
            return null;
        }
    }

    /**
     * 压缩后的图像
     */
    public byte[] getCompressedESignData() {
        Bitmap bitmap = saveCanvas();
        if (bitmap == null) {
            return null;
        }

        bitmap = BitmapUtil.getHalfWidthHeightImage(bitmap);
        if (bitmap == null) {
            return null;
        }

        byte[] imageData = BitmapUtil.convertToOneBitBmp(bitmap, 170);
        if (imageData == null) {
            imageData = BitmapUtil.compressBitmap(bitmap);
        }
        if (imageData != null) {
            LogUtil.d(TAG, "compressData len=" + imageData.length);
        }

        byte[] zlibData = ZlibUtil.compressToZipFileData(imageData);
        if (imageData != null) {
            LogUtil.d(TAG, "zlibData len=" + zlibData.length);
        }

        if (BuildConfig.DEBUG) {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File("/sdcard/1.zip"));
                outputStream.write(zlibData);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return zlibData;
    }

    protected void myDraw() {
        Canvas draw_canvas = holder.lockCanvas();
        if (draw_canvas == null) {
            return;
        }
        draw_canvas.drawBitmap(bitmap, 0, 0, paint);
        holder.unlockCanvasAndPost(draw_canvas);
    }

    //保存到相册
    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    private void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        this.context.sendBroadcast(mediaScanIntent);
    }

/*    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }*/

    //保存图片
    private Bitmap getBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale((float) 384 / bitmap.getWidth(), (float) 150 / bitmap.getHeight());
        // matrix.postRotate(180);
        Bitmap compressBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return compressBitmap;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isOnTouch) {
                down_x = (int) event.getX();
                down_y = (int) event.getY();
            }
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            canvas.drawLine(mov_x, mov_y, event.getX(), event.getY(), paint);
            //if (!isOnTouch && (Math.abs(down_x - mov_x) > 100 || Math.abs(down_y - mov_y) > 50)) {
            if (!isOnTouch && (Math.abs(down_x - mov_x) > 0 || Math.abs(down_y - mov_y) > 0)) { //tony fix #411
                isOnTouch = true;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mov_x = (int) event.getX();
            mov_y = (int) event.getY();
            isOnTouch = true;
        }
        mov_x = (int) event.getX();
        mov_y = (int) event.getY();

        //重签"button"
/*        if ((mov_x > (reSign_x - 20) && mov_x < this.width) && (mov_y > (reSign_y - 20) && mov_y < this.height)) {
            resetCanvas();
            myDrawText(canvas, mPaint);
        }*/
        return true;
    }

    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        //数据库中的String类型转换成Bitmap
        if (string != null) {
            byte[] bytes = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }

        return bitmap;
    }

    public static byte[] bitmapStrToBytes(String string) {
        //数据库中的String类型转换成byte[]
        if (string != null) {
            return Base64.decode(string, Base64.DEFAULT);
        }

        return null;
    }

    public static String bitmapToString(Bitmap bitmap) {
        String bitmapStr = null;
        //bitmap转换成String
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();// 转为byte数组
            bitmapStr = Base64.encodeToString(bytes, Base64.DEFAULT);
            Log.d(TAG, "bitmapToString bitmapStr length = " + bitmapStr.length());
        } else {
            Log.d(TAG, "bitmapToString bitmap==null");
        }

        return bitmapStr;
    }
}
