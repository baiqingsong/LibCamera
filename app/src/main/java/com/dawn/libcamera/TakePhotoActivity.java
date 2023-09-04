package com.dawn.libcamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.dawn.beauty.CameraFactory;
import com.dawn.beauty.OnCameraListener;

import java.io.File;

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

            cameraFactory.setBeautyParam(1, 1, 0.8f, 1);
            cameraFactory.setFilterParam(1, 1, 1, 1, 1, 1);
            cameraFactory.selectFilter(CameraFactory.FILTER_TYPE.FILTER_HEI_BAI);
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
}
