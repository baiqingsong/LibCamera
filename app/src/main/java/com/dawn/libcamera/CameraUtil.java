package com.dawn.libcamera;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class CameraUtil {
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
}
