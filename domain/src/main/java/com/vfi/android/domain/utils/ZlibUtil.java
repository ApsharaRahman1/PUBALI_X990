package com.vfi.android.domain.utils;

import com.vfi.android.domain.BuildConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZlibUtil {
    public static byte[] compress(byte[] data) {
        byte[] output = new byte[0];

        Deflater compresser = new Deflater();

        compresser.reset();
        compresser.setInput(data);
        compresser.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            int cnt = 0;
            byte[] buf = new byte[1024];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                cnt += i;
                bos.write(buf, 0, i);
            }
            LogUtil.d("esign_SurfaceDrawCanvas cnt=" + cnt);
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        compresser.end();
        return output;
    }

    public static byte[] compressToZipFileData(byte[] imageData) {
        byte[] zipData = null;
        try {
            byte data[] = new byte[2048];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(bos);
            ByteArrayInputStream entryStream = new ByteArrayInputStream(imageData);
            ZipEntry entry = new ZipEntry("image");
            zos.putNextEntry(entry);
            int count;
            while ((count = entryStream.read(data, 0, 2048)) != -1) {
                zos.write(data, 0, count);
            }
            entryStream.close();
            zos.closeEntry();
            zos.close();
            zipData = bos.toByteArray();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            zipData = null;
        }

        return zipData;
    }

    public static byte[] decompressZipFileData(byte[] zipFileData) {
        byte[] data = null;

        if (zipFileData == null) {
            return null;
        }

        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipFileData));
        ZipEntry zipEntry = null;

        try {
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.getName().equals("image")) {
                    LogUtil.d("TAG", "============");
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int len;
                    byte[] buff = new byte[2048];
                    while ((len = zis.read(buff, 0 , 2048)) != -1) {
                        bos.write(buff, 0, len);
                    }
                    data = bos.toByteArray();
                    bos.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG) {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File("/sdcard/2.jpg"));
                outputStream.write(data);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}
