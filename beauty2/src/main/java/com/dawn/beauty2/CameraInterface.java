package com.dawn.beauty2;

import android.graphics.Bitmap;

import java.io.File;

public interface CameraInterface {
    void getPhoto(Bitmap bitmap);//获取照片
    void getRecord(File file);//获取视频文件
}
