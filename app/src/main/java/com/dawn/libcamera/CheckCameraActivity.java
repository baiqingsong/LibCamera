package com.dawn.libcamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.dawn.beauty.CameraFactory;
import com.dawn.beauty.OnCameraListener;

import java.io.File;

public class CheckCameraActivity extends Activity {
    private CameraFactory cameraFactory;
    private GLSurfaceView glSurfaceView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(R.style.Transparent, true);
        setContentView(R.layout.activity_check_camera);
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        glSurfaceView = findViewById(R.id.glSurfaceView);
        cameraFactory = CameraFactory.getInstance(this);
        cameraCreate();
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

    private void cameraCreate(){
        cameraFactory = CameraFactory.getInstance(this);
        cameraFactory.createRenderer(this, glSurfaceView, new OnCameraListener() {
            @Override
            public void onCameraCreate() {
                Log.i("dawn", "camera create");
                runOnUiThread(() -> new Handler().postDelayed(()->{
                    cameraFactory.takePhoto(0);
                }, 500));

            }

            @Override
            public void getPhoto(Bitmap bitmap, int picIndex, int width, int height) {
                boolean hasAllBlack = CustomCameraUtil.isAllBlack(bitmap);
                Log.e("dawn", "check camera status " + hasAllBlack);
                finish();
            }

            @Override
            public void onDrawFrame(int texId, float[] mvpMatrix, float[] texMatrix, int texWidth, int texHeight, int currentPicIndex, int currentWidth, int currentHeight) {

            }

            @Override
            public void getRecord(File file) {

            }
        });
    }
}
