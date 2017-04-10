package com.delelong.diandiandriver.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/2/17.
 */

public class FileUtils {

    /**
     * 把数据写入文件
     *
     * @param filePath
     * @param fileName
     * @param bitmap
     * @return 文件路径
     */
    public static String createImage(String filePath, String fileName, Bitmap bitmap) {
        String path = filePath + fileName;
        FileOutputStream fileOutputStream = null;
        try {
            File fileDir = new File(filePath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);// 把数据写入文件
//            Log.i(TAG, "createImage: " + "保存图片" + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return path;
    }
    /**
     * 将位图对象转换为字节数组
     * @param bitmap
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
    /**
     * 保存二维码至SD卡
     * @param fileName
     * @param bitmap
     */
    public static String saveToSDCard(String filePath, String fileName, Bitmap bitmap) throws Exception {
        // 获取SD卡的路径：Environment.getExternalStorageDirectory()
        File file = new File(filePath,
                fileName);
        FileOutputStream outStream = new FileOutputStream(file);
        outStream.write(Bitmap2Bytes(bitmap));
        outStream.close();
        return file.getAbsolutePath();
    }
    public static void uploadFile(String filePath){

    }
}
