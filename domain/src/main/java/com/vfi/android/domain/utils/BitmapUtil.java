package com.vfi.android.domain.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapUtil {
    private static final String TAG = "BitmapUtil";

    public static byte[] bitmap2byteArray(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos); // 40 is ok, I tested it.
        return bos.toByteArray();
    }

    public static byte[] compressBitmap(Bitmap bitmap) {
        byte[] data = bitmap2byteArray(bitmap);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = 2;
        Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        return bitmap2byteArray(b);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 原图片的宽高
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 先将inJustDecodeBounds设置为true来获取图片的长宽属性
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // 计算inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 加载压缩版图片
        options.inJustDecodeBounds = false;
        // 根据具体情况选择具体的解码方法
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap wrapSize(byte[] image, int desiredWidth) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        return wrapSize(bitmap, desiredWidth);
    }

    public static Bitmap wrapSize(Bitmap bitmap, int desiredWidth) {
        float scale = (float) desiredWidth / bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap getHalfWidthHeightImage(Bitmap bitmap) {
        float scale = 0.5f;
        Matrix matrix=new Matrix();
        matrix.postScale(scale, scale);
        bitmap = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix,true);
        return bitmap;
    }

    public static Bitmap getTwoTimesWidthHeightImage(Bitmap bitmap) {
        float scale = 2.0f;
        Matrix matrix=new Matrix();
        matrix.postScale(scale, scale);
        bitmap = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix,true);
        return bitmap;
    }

    public static byte[] convertToOneBitBmp(Bitmap bitmap, int threshold) {
        if (bitmap == null) {
            return null;
        }

        LogUtil.d(TAG, "threshold=" + threshold);
        if (threshold > 256 || threshold < 0) {
            threshold = 200;
        }

        byte[] outData;
        int nBmpWidth = bitmap.getWidth();
        int nBmpHeight = bitmap.getHeight();
        int dataSizePerLine = (int) ((nBmpWidth * 1 + 31) / 8);
        dataSizePerLine = dataSizePerLine / 4 * 4;
        int finalImageSize = (dataSizePerLine * nBmpHeight + 3) / 4 * 4;

        LogUtil.d(TAG, "finalImageSize=" + finalImageSize + " dataSizePerLine=" + dataSizePerLine);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(14 + 40 + 8 + finalImageSize);
            // set bmp header
            int bfType = 0x4d42;
            long bfSize = 14 + 40 + 8 + finalImageSize; // bmp header + bitmap header + rgbQUAD + imageData
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            long bfOffBits = 14 + 40 + 8;
            // write bmp header
            writeWord(bos, bfType);
            writeDword(bos, bfSize);
            writeWord(bos, bfReserved1);
            writeWord(bos, bfReserved2);
            writeDword(bos, bfOffBits);
            // set bitmap header
            long biSize = 40L;
            long biWidth = nBmpWidth;
            long biHeight = nBmpHeight;
            int biPlanes = 1;
            int biBitCount = 1;
            long biCompression = 0L;
            long biSizeImage = finalImageSize;
            long biXpelsPerMeter = 0L;
            long biYPelsPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
            // write bitmap header
            writeDword(bos, biSize);
            writeLong(bos, biWidth);
            writeLong(bos, biHeight);
            writeWord(bos, biPlanes);
            writeWord(bos, biBitCount);
            writeDword(bos, biCompression);
            writeDword(bos, biSizeImage);
            writeLong(bos, biXpelsPerMeter);
            writeLong(bos, biYPelsPerMeter);
            writeDword(bos, biClrUsed);
            writeDword(bos, biClrImportant);

            // write 2 bit image map table
            writeRGBQUAD(bos);

            byte bmpData[] = new byte[finalImageSize];
            int[] imagePixels = new int[nBmpWidth * nBmpHeight];
            bitmap.getPixels(imagePixels, 0, nBmpWidth, 0, 0, nBmpWidth, nBmpHeight);

            for (int i = 0; i < imagePixels.length; i++) {
                int red = (((imagePixels[i] & 0x00ff0000) >> 16) & 0xff); // 取高两位
                int green = (((imagePixels[i] & 0x0000ff00) >> 8) & 0xff); // 取中两位
                int blue = ((imagePixels[i] & 0x000000ff) & 0xff);

                int x = i % nBmpWidth;
                int y = i / nBmpWidth;

                int arrayIdx = x / 8;
                int bitIdx = x % 8;

                y = nBmpHeight - y - 1;

                if (red > threshold) {
                    bmpData[y * dataSizePerLine + arrayIdx] &= ~(0x01 << (7 - bitIdx));
                } else {
                    bmpData[y * dataSizePerLine + arrayIdx] |= (0x01 << (7 - bitIdx));
                }
            }

            bos.write(bmpData);

            outData = bos.toByteArray();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            outData = null;
        }

        return outData;
    }

    private static void writeWord(OutputStream stream, int value) throws IOException {
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        stream.write(b);
    }

    private static void writeDword(OutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    private static void writeLong(OutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    private static void writeRGBQUAD(OutputStream stream) throws IOException {
        byte[] quad = new byte[8];
        quad[0] = (byte) 0xff;
        quad[1] = (byte) 0xff;
        quad[2] = (byte) 0xff;
        quad[3] = (byte) 0x00;
        quad[4] = (byte) 0x00;
        quad[5] = (byte) 0x00;
        quad[6] = (byte) 0x00;
        quad[7] = (byte) 0x00;
        stream.write(quad);
    }
}
