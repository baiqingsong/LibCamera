package com.dawn.libcamera;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class CustomCameraUtil {
    private final static int colorDefault = -16380922;
    private static int colorStart;//第一个颜色
    /**
     * 判断图片是否四周都是黑色的
     */
    public static boolean hasAllBlack(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        colorStart = bitmap.getPixel(0, 0);
        Log.i("dawn", "get color start " + colorStart);
        for (int i = 0; i < width; i++) {//上边距
            if (bitmap.getPixel(i, 0) != colorStart) {
//                Log.i("dawn", "green " + Color.GREEN + " " + Color.BLACK);
//                Log.i("dawn", "bitmap i " + i + " color " + bitmap.getPixel(i, 0));
                return false;
            }
        }
        for (int i = 0; i < height; i++) {// 右边距
            if (bitmap.getPixel(width - 1, i) != colorStart) {
                return false;
            }
        }
        for (int i = 0; i < width; i++) {//下边距
            if (bitmap.getPixel(i, height - 1) != colorStart) {
                return false;
            }
        }
        for (int i = 0; i < height; i++) {// 左边距
            if (bitmap.getPixel(0, i) != colorStart) {
                return false;
            }
        }
        return true;//四个边距都是黑色默认就都是黑色
    }

    /**
     * 判断图片是否四周都是黑色的
     */
    public static boolean isAllBlack(Bitmap bitmap){
        //获取图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //获取图片的像素
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);//获取图片的像素
        //检查图片是否全黑
        boolean isAllBlack = true;
        for (int pixel : pixels) {
            // 获取 RGB 值
            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);

            // 判断是否为黑色
            if (red != 0 || green != 0 || blue != 0) {
                isAllBlack = false;
                break;
            }
        }
        return isAllBlack;
    }
}
