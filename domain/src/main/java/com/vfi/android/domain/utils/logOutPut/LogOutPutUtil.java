package com.vfi.android.domain.utils.logOutPut;



import com.vfi.android.domain.utils.AESUtil;
import com.vfi.android.domain.utils.StringUtil;

import java.io.File;
import java.io.FileWriter;


/**
 * Created by huan.lu on 2018/12/3.
 */

public class LogOutPutUtil {
    private static boolean isOutPutLog = false;
    private static String e = "" + System.currentTimeMillis();
    private static long logOutTimeMS = 0;
    private static long lastLogOutTimeMS = 0;

    public static synchronized void outPutLog(String log) {
        if (isCanOutPut()) {
            String date = StringUtil.getFormatSystemDatetime().substring(0,10).replace("-","");
            File logFile = new File("/storage/sdcard1/smartEDC", date + ".txt");

            FileWriter out = null;
            byte[] encrypt;
            try {
                File file = new File("/storage/sdcard1/smartEDC");

                if (!logFile.exists()) {
                    file.mkdirs();
                }

                if (!logFile.exists()) {
                    logFile.createNewFile();
                }

                String logOutPut = StringUtil.getFormatSystemDatetime() + "  " + log ;
                encrypt = AESUtil.encrypt(logOutPut,e);

                out = new FileWriter(logFile,true);
                out.write(StringUtil.byte2HexStr(encrypt) + "\r\n");
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isCanOutPut() {
        long currentTimeMillis = System.currentTimeMillis();
        if (lastLogOutTimeMS != 0) {
            long timeMS = Math.abs(currentTimeMillis - lastLogOutTimeMS);
            logOutTimeMS += timeMS;
        }

        lastLogOutTimeMS = currentTimeMillis;

        if (logOutTimeMS > 3600 * 24 * 1000) {
            System.out.println("TAG:" + logOutTimeMS);
            isOutPutLog = false;
            return false;
        }

        File sdCardPath = new File("/storage/sdcard1");
        return sdCardPath.canWrite();
    }

    public static boolean isIsOutPutLog() {
        return isOutPutLog;
    }

    public static void setIsOutPutLog(boolean isOutPutLog) {
        LogOutPutUtil.isOutPutLog = isOutPutLog;
    }

    public static void setE(String e) {
        LogOutPutUtil.e = e;
    }
}
