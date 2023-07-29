package com.dawn.libcamera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dawn.beauty.CameraFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CameraFactory.getInstance(this).initCamera(null);
    }

    public void jumpToTakePhoto(View view){
        Intent intent = new Intent(this, TakePhotoActivity.class);
        startActivity(intent);
    }
}