package com.dawn.libcamera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dawn.beauty.CameraFactory;
import com.dawn.beauty.OnCameraListener;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CameraFactory.getInstance(this).initCamera(authpack.A());
        glSurfaceView = findViewById(R.id.glSurfaceView);
//        cameraCreate();
    }

    public void jumpToTakePhoto(View view){
        boolean hasCamera = CameraFactory.getInstance(this).hasCamera();
        if(!hasCamera){
            Toast.makeText(this, "没有摄像头", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, TakePhotoActivity.class);
        startActivity(intent);
    }
    /**
     * 检查摄像头状态
     */
    public void checkCameraStatus(View view){
        Intent intent = new Intent(this, CheckCameraActivity.class);
        startActivity(intent);
    }

}