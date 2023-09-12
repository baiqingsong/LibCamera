package com.dawn.libcamera;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {
    /**
     * Save Bitmap
     * @param name file name
     * @param bm  picture to save
     */
    public static String saveBitmap(Context mContext, Bitmap bm, String name) {
//        Log.d("dawn", "Ready to save picture");
        //指定我们想要存储文件的地址
        String TargetPath = mContext.getExternalFilesDir(null).getAbsolutePath() + "/images/";
//        String TargetPath = Constant.imgDir;
        Log.i("dawn", "Save Path=" + TargetPath);
        //判断指定文件夹的路径是否存在
        File fileDir = new File(TargetPath);
        if(!fileDir.exists())
            fileDir.mkdir();
        //如果指定文件夹创建成功，那么我们则需要进行图片存储操作
        File saveFile = new File(TargetPath, name);
        if(saveFile.exists()){
            saveFile.delete();
        }
        try {
            saveFile.createNewFile();
            FileOutputStream saveImgOut = new FileOutputStream(saveFile);
            // compress - 压缩的意思
            bm.compress(Bitmap.CompressFormat.JPEG, 100, saveImgOut);
            //存储完成后需要清除相关的进程
            saveImgOut.flush();
            saveImgOut.close();
//                Log.d("Save Bitmap", "The picture is save to your phone!");
            return TargetPath + name;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;

    }

    /**
     * 保存图片
     * @param savePath 保存路径
     * @param bm picture to save
     */
    public static void saveBitmap(String savePath, Bitmap bm) {
        //如果指定文件夹创建成功，那么我们则需要进行图片存储操作
        File saveFile = new File(savePath);
        if(saveFile.exists()){
            saveFile.delete();
        }
        try {
            saveFile.createNewFile();
            FileOutputStream saveImgOut = new FileOutputStream(saveFile);
            // compress - 压缩的意思
            bm.compress(Bitmap.CompressFormat.JPEG, 100, saveImgOut);
            //存储完成后需要清除相关的进程
            saveImgOut.flush();
            saveImgOut.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    /**
     * 判断指定目录的文件夹是否存在，如果不存在则需要创建新的文件夹
     * @param fileName 指定目录
     * @return 返回创建结果 TRUE or FALSE
     */
    private static boolean fileIsExist(String fileName) {
        //传入指定的路径，然后判断路径是否存在
        File file=new File(fileName);
        if (file.exists())
            return true;
        else{
            //file.mkdirs() 创建文件夹的意思
            return file.mkdirs();
        }
    }

    public static void UnZipFolder(String zipFileString, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {

                File file = new File(outPathString + File.separator + szName);
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }

    /**
     * 把bitmap转换成Base64字符串
     */
    public static String bitmapToString(Bitmap bitmap) {

        if (bitmap == null) {
            return "";
        }

        String string = "";
        ByteArrayOutputStream btString = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, btString);
        byte[] bytes = btString.toByteArray();
        string = Base64.encodeToString(bytes, Base64.URL_SAFE);
        return string;
    }
    /**
     * 把Base64字符串转换成bitmap
     */
    public static Bitmap base64ToBitmap(String base64String) {
        if (TextUtils.isEmpty(base64String)) {
            return null;
        }
        byte[] decode = Base64.decode(base64String.toString().trim(), Base64.URL_SAFE);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        return bitmap;
    }

    public static Bitmap getBit(Context context, String filename){
//以最省内存的方式读取本地资源的图片
        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 2;//图片宽高都为原来的二分之一，即图片为原来的四分之一
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        AssetManager asm = context.getAssets();
        InputStream is;
        Bitmap bitmap = null;
        try {
            is = asm.open(filename);
            bitmap = BitmapFactory.decodeStream(is
                    , null, options);
//            mImageView.setImageBitmap(bitmap);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 删除图片保存文件夹
     */
    public static void deleteImageDir(Context context){
        String TargetPath = context.getExternalFilesDir(null).getAbsolutePath() + "/images/";
        File fileDir = new File(TargetPath);
        if(fileDir.exists()){
            File[] fileChildren = fileDir.listFiles();
            for(int i = 0 ; i < fileChildren.length; i ++){
                fileChildren[i].delete();
            }
            Log.e("dawn", "file delete");
        }
    }

    /**
     * 从路径中提取名称
     * @param path 路径
     * @return 名称
     */
    public static String getFileName(String path){

        int start=path.lastIndexOf("/");
        int end=path.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            return path.substring(start+1,end);
        }else{
            return null;
        }

    }

    /**
     * 获取文件全名
     * @param path 路径
     * @return 文件名
     */
    public static String getAllFileName(String path){

        int start=path.lastIndexOf("/");
        if(start!=-1){
            return path.substring(start+1);
        }else{
            return null;
        }

    }

    /**
     * 删除所有图片
     */
    public static void deleteImages(Context mContext){
        String TargetPath = mContext.getExternalFilesDir(null).getAbsolutePath() + "/images/";
//        Log.d("Save Bitmap", "Save Path=" + TargetPath);
        //判断指定文件夹的路径是否存在
        File fileDir = new File(TargetPath);
        if(!fileDir.exists())
            fileDir.mkdir();
        File[] files = fileDir.listFiles();
        for(int i = 0; i < files.length; i ++){
            files[i].delete();
        }
    }

    //file文件读取成byte[]
    public static byte[] readFile(File file) {
        RandomAccessFile rf = null;
        byte[] data = null;
        try {
            rf = new RandomAccessFile(file, "r");
            data = new byte[(int) rf.length()];
            rf.readFully(data);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            closeQuietly(rf);
        }
        return data;
    }

    //关闭读取file
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
//                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();

        }

    }

    // 下载网络图片
    public static void downloadImage(final String url, final String name) {
        new Thread(() -> {
            try {
                URL url1 = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200) {
                    InputStream inputStream = conn.getInputStream();
                    File file = new File(name);
                    if(file.exists())
                        file.delete();
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while ((len = inputStream.read(bytes)) != -1) {
                        fos.write(bytes, 0, len);
                    }
                    fos.close();
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 下载网络图片
    public static void downloadImageMainThread(final String url, final String name) {
        try {
            URL url1 = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                File file = new File(name);
                if(file.exists())
                    file.delete();
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(bytes)) != -1) {
                    fos.write(bytes, 0, len);
                }
                fos.close();
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件夹以及文件夹里面的文件
     */
    public static void deleteDirWithFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWithFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }


}
