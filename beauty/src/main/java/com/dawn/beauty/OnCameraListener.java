package com.dawn.beauty;

import android.graphics.Bitmap;

import java.io.File;

public interface OnCameraListener {
    void onCameraCreate();
    void getPhoto(Bitmap bitmap, int picIndex, int width, int height);

    void onDrawFrame(int texId, float[] mvpMatrix, float[] texMatrix, final int texWidth, final int texHeight, int currentPicIndex, int currentWidth, int currentHeight);
    void getRecord(File file);//录制视频的文件
}
