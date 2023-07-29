package com.dawn.libcamera;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.dawn.beauty.CameraFactory;

public class TakePhotoActivity extends Activity {
    CameraFactory cameraFactory;
    private GLSurfaceView mGlSurfaceView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        cameraFactory = CameraFactory.getInstance(this);
        mGlSurfaceView = findViewById(R.id.glSurfaceView);
        cameraFactory.createRenderer(this, mGlSurfaceView, null);
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
}
