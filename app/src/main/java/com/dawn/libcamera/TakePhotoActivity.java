package com.dawn.libcamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.dawn.beauty.CameraFactory;
import com.dawn.beauty.OnCameraListener;
import com.dawn.beauty.utils.Constant;

public class TakePhotoActivity extends Activity {
    CameraFactory cameraFactory;
    private GLSurfaceView mGlSurfaceView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        cameraFactory = CameraFactory.getInstance(this);
        mGlSurfaceView = findViewById(R.id.glSurfaceView);
        cameraFactory.createRenderer(this, mGlSurfaceView, new OnCameraListener() {
            @Override
            public void onCameraCreate() {

            }

            @Override
            public void getPhoto(Bitmap bitmap, int picIndex, int width, int height) {
                FileUtil.saveBitmap(TakePhotoActivity.this, bitmap, "test.jpg");
                Log.i("dawn", "save bitmap");
            }

            @Override
            public void onDrawFrame(int texId, float[] mvpMatrix, float[] texMatrix, int texWidth, int texHeight, int currentPicIndex, int currentWidth, int currentHeight) {

            }
        });

        new Handler().postDelayed(() -> {

            cameraFactory.setBeautyParam(1, 1, 0.8f, 1);
            cameraFactory.setFilterParam(1, 1, 1, 1, 1, 1);
            cameraFactory.selectFilter(CameraFactory.FILTER_TYPE.FILTER_HEI_BAI);
        }, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cameraFactory != null)
            cameraFactory.rendererOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraFactory != null)
            cameraFactory.rendererOnPause();
    }

    public void switchCamera(View view){
        cameraFactory.switchCamera();
    }

    public void takePhoto(View view){
        //2592*1944
        cameraFactory.takePhoto(1, 2592, 1944);
    }
}
