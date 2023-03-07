package com.vfi.android.payment;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.EncryptionUtil;
import com.vfi.android.domain.utils.LogUtil;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String passwd = "123456";
        String md5 = EncryptionUtil.getMd5HexString(passwd.getBytes());
        LogUtil.d(TAGS.Encryption, "md5=[" + md5 + "]");
        passwd = "1";
        md5 = EncryptionUtil.getMd5HexString(passwd.getBytes());
        LogUtil.d(TAGS.Encryption, "md5=[" + md5 + "]");
        passwd = "265273";
        md5 = EncryptionUtil.getMd5HexString(passwd.getBytes());
        LogUtil.d(TAGS.Encryption, "md5=[" + md5 + "]");
        assertEquals(4, 2 + 2);
    }

    @Test
    public void jumpToTest() {
        String baseDirName = "D:\\VMware_Share_Files\\GitRoot\\android-philippine-rcbc-cardpayment\\presentation\\src\\main\\java\\com\\vfi\\android\\payment\\presentation\\navigation\\transflows";
        File baseDir = new File(baseDirName);       // 创建一个File对象
        if (!baseDir.exists() || !baseDir.isDirectory()) {  // 判断目录是否存在
            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
        }

        String tmpDirName = baseDirName + "/tmp";
        File tmpDir = new File(tmpDirName);
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }

        String tempName = null;
        //判断目录是否存在
        File tempFile;
        File newFile;
        File[] files = baseDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            tempFile = files[i];
            if(tempFile.isDirectory()){
                continue;
            } else if(tempFile.isFile()){
                tempName = tempFile.getName();
                newFile = new File(tmpDirName + "/" + tempName);
                try {
                    newFile.createNewFile();

                    FileInputStream inputStream = new FileInputStream(tempFile);
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    BufferedReader bReader = new BufferedReader(reader);

                    FileOutputStream outputStream = new FileOutputStream(newFile);
                    OutputStreamWriter writer = new OutputStreamWriter(outputStream);

                    String line;
                    String activityClassName = "";
                    boolean isBegin = false;
                    while ((line = bReader.readLine()) != null) {
                        if (line.contains("AndroidUtil.startActivity")) {
                            int startIndex = line.indexOf(",");
                            startIndex++;
                            int endIndex = line.indexOf(")");
                            activityClassName = line.substring(startIndex, endIndex);
                            LogUtil.d("CUNCHE", "activityClassName=" + activityClassName);
                            isBegin = true;
                            continue;
                        } else if (line.contains("controlData.pushUIState")) {
                            if (isBegin) {
                                int beforeSpace = line.indexOf("controlData");
                                int startIndex = line.indexOf("(");
                                startIndex++;
                                int endIndex = line.indexOf(")");
                                String state = line.substring(startIndex, endIndex);
                                LogUtil.d("CUNCHE", "state=" + state);
                                String newLine = "jumpToState(context," + state + ", " + activityClassName + ");\n";
                                byte[] spaces = new byte[beforeSpace];
                                Arrays.fill(spaces, (byte)' ');
                                newLine = new String(spaces) + newLine;
                                writer.write(newLine);
                            }
                        } else {
                            writer.write(line + "\n");
                            writer.flush();
                        }

                        isBegin = false;
                    }

                    bReader.close();
                    reader.close();
                    inputStream.close();
                    writer.close();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}