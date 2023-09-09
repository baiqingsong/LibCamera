package com.dawn.libcamera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dawn.beauty.CameraFactory;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jumpToTakePhoto(View view){
        boolean hasCamera = CameraFactory.getInstance(this).hasCamera();
        if(!hasCamera){
            Toast.makeText(this, "没有摄像头", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, TakePhotoActivity.class);
        startActivity(intent);
    }
}