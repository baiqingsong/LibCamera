package com.dawn.libcamera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.dawn.beauty.CameraFactory;
import com.dawn.beauty.OnCameraListener;
import com.dawn.beauty.utils.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class TakePhotoActivity extends Activity {
    CameraFactory cameraFactory;
//    private CameraFactory2 cameraFactory2;
    private GLSurfaceView mGlSurfaceView;
    private ImageView ivPhoto;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        mGlSurfaceView = findViewById(R.id.glSurfaceView);
        ivPhoto = findViewById(R.id.iv_photo);

        CameraFactory.getInstance(this).initCamera(authpack.A());
        cameraFactory = CameraFactory.getInstance(this);
        cameraFactory.createRenderer(this, mGlSurfaceView, new OnCameraListener() {
            @Override
            public void onCameraCreate() {

            }

            @Override
            public void getPhoto(Bitmap bitmap, int picIndex, int width, int height) {
//                FileUtil.saveBitmap(TakePhotoActivity.this, bitmap, "test.jpg");
                Log.i("dawn", "save bitmap");
                runOnUiThread(() -> {
                    if(ivPhoto != null)
                        ivPhoto.setImageBitmap(bitmap);
                });
                boolean isAllBlack = hasAllBlack(bitmap);
                Log.e("dawn", "bitmap is all black " + isAllBlack);
//                savePhoto(bitmap);
//                combinePhoto(bitmap);
//                combinePhoto2(bitmap);

            }

            @Override
            public void onDrawFrame(int texId, float[] mvpMatrix, float[] texMatrix, int texWidth, int texHeight, int currentPicIndex, int currentWidth, int currentHeight) {

            }

            @Override
            public void getRecord(File file) {
                Log.i("dawn", "file path " + file.getAbsolutePath());
            }
        });

        new Handler().postDelayed(() -> {

            cameraFactory.setBeautyParam(1, 0.8f, 0.6f, 0.8f);
            cameraFactory.setFilterParam(1, 1, 0.7f, 1, 0.7f, 0.7f);
            cameraFactory.selectFilter(CameraFactory.FILTER_TYPE.FILTER_MI_TAO);
        }, 500);

//        cameraFactory2 = CameraFactory2.getInstance();
//        cameraFactory2.cameraInit(this, authpack.A());
//        cameraFactory2.rendererCreate(this, mGlSurfaceView, new CameraInterface() {
//            @Override
//            public void getPhoto(Bitmap bitmap) {
//                runOnUiThread(() -> {
//                    if(ivPhoto != null)
//                        ivPhoto.setImageBitmap(bitmap);
//                });
//            }
//
//            @Override
//            public void getRecord(File file) {
//
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cameraFactory != null)
            cameraFactory.rendererOnResume();
//        if(cameraFactory2 != null)
//            cameraFactory2.rendererOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraFactory != null)
            cameraFactory.rendererOnPause();
//        if(cameraFactory2 != null)
//            cameraFactory2.rendererOnPause();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(cameraFactory2 != null)
//            cameraFactory2.rendererOnDestroy();
//    }

    public void switchCamera(View view){
//        cameraFactory.switchCamera();
    }

    public void takePhoto(View view){
        //2592*1944
        cameraFactory.takePhoto(1, 1920, 1080);
//        if(cameraFactory2 != null)
//            cameraFactory2.takePhoto();
    }

    public void startRecord(View view){
        cameraFactory.startRecord(mGlSurfaceView);
    }

    public void stopRecord(View view){
        cameraFactory.stopRecord();
    }

    public static String bitmap2Path(Bitmap bitmap, String path) {
        try {
            OutputStream os = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("TAG", "", e);
        }
        return path;
    }

    public static void saveBitmap(Bitmap bm, String path) {
        //如果指定文件夹创建成功，那么我们则需要进行图片存储操作
        File saveFile = new File(path);
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

    private void savePhoto(Bitmap bitmap){
        File filePath = new File(Objects.requireNonNull(TakePhotoActivity.this.getExternalFilesDir(null)).getAbsolutePath() + "/png/");
        if(!filePath.exists())
            filePath.mkdir();
//        Log.i("dawn", "save path " + filePath);
        bitmap2Path(bitmap, filePath.getAbsolutePath() + "/take.png");
        Log.i("dawn", "save photo png end");
        saveBitmap(bitmap, filePath.getAbsolutePath() + "/take_.png");
        Log.i("dawn", "save photo jpg end");
        Bitmap bitmapNew = Bitmap.createScaledBitmap(bitmap, 960, 540, true);
        bitmap2Path(bitmapNew, filePath.getAbsolutePath() + "/take2.png");
        bitmapNew.recycle();
        Log.i("dawn", "save half photo end");
    }

    private void combinePhoto(Bitmap bitmap){
        Bitmap photoBitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);//创建画布
        Canvas canvas = new Canvas(photoBitmap);
        Bitmap bitmapNew = Bitmap.createScaledBitmap(bitmap, 960, 540, true);
        canvas.drawBitmap(bitmapNew, 0, 0, null);
        canvas.drawBitmap(bitmapNew, 960, 0, null);
        canvas.drawBitmap(bitmapNew, 0, 540, null);
        canvas.drawBitmap(bitmapNew, 960, 540, null);
        bitmapNew.recycle();
        canvas.save();
        canvas.restore();
        File filePath = new File(Objects.requireNonNull(TakePhotoActivity.this.getExternalFilesDir(null)).getAbsolutePath() + "/png/");
        if(!filePath.exists())
            filePath.mkdir();
        bitmap2Path(photoBitmap, filePath.getAbsolutePath() + "/take_combine.png");
        photoBitmap.recycle();
        Log.i("dawn", "combine photo end");
    }

    private void combinePhoto2(Bitmap bitmap){
        Bitmap photoBitmap = Bitmap.createBitmap(3840, 2160, Bitmap.Config.ARGB_8888);//创建画布
        Canvas canvas = new Canvas(photoBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(bitmap, 1920, 0, null);
        canvas.drawBitmap(bitmap, 0, 1080, null);
        canvas.drawBitmap(bitmap, 1920, 1080, null);
        canvas.save();
        canvas.restore();
        Bitmap bitmapNew = Bitmap.createScaledBitmap(photoBitmap, 1920, 1080, true);
        File filePath = new File(Objects.requireNonNull(TakePhotoActivity.this.getExternalFilesDir(null)).getAbsolutePath() + "/png/");
        if(!filePath.exists())
            filePath.mkdir();
        bitmap2Path(photoBitmap, filePath.getAbsolutePath() + "/take_combine3.png");
        Log.i("dawn", "combine photo3 end");
        bitmap2Path(bitmapNew, filePath.getAbsolutePath() + "/take_combine2.png");
        photoBitmap.recycle();
        bitmapNew.recycle();
        Log.i("dawn", "combine photo2 end");
    }

    /**
     * 判断图片是否四周都是黑色的
     */
    private boolean hasAllBlack(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        for (int i = 0; i < width; i++) {//上边距
            if (bitmap.getPixel(i, 0) != Color.BLACK) {
                return false;
            }
        }
        for (int i = 0; i < height; i++) {// 右边距
            if (bitmap.getPixel(width - 1, i) != Color.BLACK) {
                return false;
            }
        }
        for (int i = 0; i < width; i++) {//下边距
            if (bitmap.getPixel(i, height - 1) != Color.BLACK) {
                return false;
            }
        }
        for (int i = 0; i < height; i++) {// 左边距
            if (bitmap.getPixel(0, i) != Color.BLACK) {
                return false;
            }
        }
        return true;//四个边距都是黑色默认就都是黑色
    }

}
