package com.dawn.libcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dawn.beauty.CameraFactory;
import com.dawn.beauty.FURenderer;
import com.dawn.beauty.custom.BaseCameraRenderer;
import com.dawn.beauty.custom.Camera1Renderer;
import com.dawn.beauty.custom.CameraUtil;
import com.dawn.beauty.custom.OnRendererStatusListener;
import com.dawn.beauty.custom.util.CameraUtils;
import com.dawn.beauty.gles.core.GlUtil;
import com.dawn.beauty.param.BeautificationParam;
import com.dawn.beauty.utils.BitmapUtil;
import com.dawn.beauty.utils.Constant;

public class TakePhoto2Activity extends AppCompatActivity implements OnRendererStatusListener, SensorEventListener {
    protected GLSurfaceView mGLSurfaceView;
    protected BaseCameraRenderer mCameraRenderer;
    private int mFrontCameraOrientation;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    protected FURenderer mFURenderer;
    protected volatile boolean mIsDualInput = true;
    protected volatile boolean mIsNeedTakePic = false;
    protected volatile boolean mIsTakingPic = false;
    private float mEyeEnlarging = 1.0f;//大眼[0-1]
    private float mBlurLevel = 0.8f;//磨皮[0-6]
    private float mCheekV = 0.3f;//v脸[0-1]
    private float mCheekThinning = 0.6f;//瘦脸[0-1]
    private float mColorLevel = 0.8f;//美白[0-2]
    private float yuantu_level = 1f;//原图级别
    private float heibai_level = 1f;//黑白级别
    private float zhiganhui_level = 0.7f;//质感灰级别
    private float gexing_level = 1f;//个性级别
    private float mitao_level = 0.7f;//蜜桃级别
    private float xiaoqingxin_level = 0.7f;//小清新级别

    private int currentFilter = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo2);
        mGLSurfaceView = findViewById(R.id.fu_base_gl_surface);
        if(mGLSurfaceView != null){
            mGLSurfaceView.setEGLContextClientVersion(GlUtil.getSupportGLVersion(this));
        }
        mCameraRenderer = new Camera1Renderer(this, mGLSurfaceView, this);
        mFrontCameraOrientation = CameraUtils.getFrontCameraOrientation();
        if(mGLSurfaceView != null){
            mGLSurfaceView.setRenderer(mCameraRenderer);
            mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGLSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFilter ++;
                if(currentFilter> 6){
                    currentFilter = 0;
                }
                selectCurrentM(currentFilter);
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (Math.abs(x) > 3 || Math.abs(y) > 3) {
                if (Math.abs(x) > Math.abs(y)) {
                    if(mFURenderer != null){
                        mFURenderer.setTrackOrientation(x > 0 ? 0 : 180);
                    }
                } else {
                    if(mFURenderer != null){
                        mFURenderer.setTrackOrientation(y > 0 ? 90 : 270);
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSurfaceCreated() {
        if(mFURenderer == null){
            mFURenderer = initFURenderer();
            if(mFURenderer != null){
                mFURenderer.onSurfaceCreated();
                mFURenderer.setBeautificationOn(true);
            }
            selectCurrentM(5);
        }
    }

    @Override
    public void onSurfaceChanged(int viewWidth, int viewHeight) {

    }

    @Override
    public int onDrawFrame(byte[] cameraNv21Byte, int cameraTexId, int cameraWidth, int cameraHeight, float[] mvpMatrix, float[] texMatrix, long timeStamp) {
        int fuTexId = 0;
        if(mFURenderer != null){
            if (mIsDualInput) {
                fuTexId = mFURenderer.onDrawFrame(cameraNv21Byte, cameraTexId, cameraWidth, cameraHeight);
            } else {
                fuTexId = mFURenderer.onDrawFrame(cameraNv21Byte, cameraWidth, cameraHeight);
            }
        }
        getPic(fuTexId, GlUtil.IDENTITY_MATRIX, texMatrix, cameraWidth, cameraHeight);
//        mFURenderer.printLog();
        return fuTexId;
    }

    @Override
    public void onSurfaceDestroy() {
        if(mFURenderer != null){
            mFURenderer.setBeautificationOn(false);
            mFURenderer.onSurfaceDestroyed();
        }
    }

    @Override
    public void onCameraChanged(int cameraFacing, int cameraOrientation) {

    }
    /**
     * 开启美颜
     */
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .maxFaces(4)
                .maxHumans(1)
                .setOnBundleLoadCompleteListener(new FURenderer.OnBundleLoadCompleteListener() {
                    @Override
                    public void onBundleLoadComplete(int what) {

                    }
                })
                .setLoadAiHumanProcessor(true)
                .inputImageOrientation(mFrontCameraOrientation)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGLSurfaceView != null)
            mGLSurfaceView.onResume();
        mCameraRenderer.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mCameraRenderer.onPause();
        if(mGLSurfaceView != null)
            mGLSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    private void getPic(int texId, float[] mvpMatrix, float[] texMatrix, final int texWidth, final int texHeight) {
        if (!mIsNeedTakePic) {
            return;
        }
        mIsNeedTakePic = false;
//        Log.d("dawn", "texWidth--->" + texWidth + "   " + "texHeight----->" + texHeight);
//        mHandler.removeMessages(h_take_photo);
        BitmapUtil.glReadBitmap(texId, texMatrix, mvpMatrix, texWidth, texHeight, mOnReadBitmapListener, false);
    }
    BitmapUtil.OnReadBitmapListener mOnReadBitmapListener = new BitmapUtil.OnReadBitmapListener() {

        @Override
        public void onReadBitmapListener(final Bitmap photo) {
            if (photo == null) {
                return;
            }
            
            mIsTakingPic = false;
        }
    };

    /**
     * 选择当前的滤镜
     */
    protected void selectCurrentM(int currentFilter){
        switch (currentFilter) {
            case 2:
                selectM2();
                break;
            case 3:
                selectM3();
                break;
            case 4:
                selectM4();
                break;
            case 5:
                selectM5();
                break;
            case 6:
                selectM6();
                break;
            default:
                selectM1();
                break;
        }
    }

    /**
     * 选择当前的贴纸
     */
    protected void selectCurrentSticker(){

    }

    /**
     * 原图
     */
    protected void selectM1() {
        if(mFURenderer == null)
            return;
        mFURenderer.setFilterName(BeautificationParam.ZHIGANHUI_2);

        mFURenderer.onEyeEnlargeSelected(mEyeEnlarging);
        mFURenderer.onBlurLevelSelected(mBlurLevel);
        mFURenderer.onCheekVSelected(mCheekV);
        mFURenderer.onCheekThinningSelected(mCheekThinning);
        mFURenderer.onColorLevelSelected(mColorLevel);

        mFURenderer.setFilterLevel(yuantu_level);
        mFURenderer.setNeedUpdate(true);
    }

    /**
     * 黑白
     */
    protected void selectM2() {
        if(mFURenderer == null)
            return;
        mFURenderer.setFilterName(BeautificationParam.HEIBAI_1);

        mFURenderer.onEyeEnlargeSelected(mEyeEnlarging);
        mFURenderer.onBlurLevelSelected(mBlurLevel);
        mFURenderer.onCheekVSelected(mCheekV);
        mFURenderer.onCheekThinningSelected(mCheekThinning);
        mFURenderer.onColorLevelSelected(mColorLevel);

        mFURenderer.setFilterLevel(heibai_level);
        mFURenderer.setNeedUpdate(true);
    }

    /**
     * 个性
     */
    protected void selectM3() {
        if(mFURenderer == null)
            return;
        mFURenderer.setFilterName(BeautificationParam.GEXING_5);

        mFURenderer.onEyeEnlargeSelected(mEyeEnlarging);
        mFURenderer.onBlurLevelSelected(mBlurLevel);
        mFURenderer.onCheekVSelected(mCheekV);
        mFURenderer.onCheekThinningSelected(mCheekThinning);
        mFURenderer.onColorLevelSelected(mColorLevel);

        mFURenderer.setFilterLevel(gexing_level);
        mFURenderer.setNeedUpdate(true);
    }

    /**
     * 质感灰
     */
    protected void selectM4() {
        if(mFURenderer == null)
            return;
        mFURenderer.setFilterName(BeautificationParam.HEIBAI_3);

        mFURenderer.onEyeEnlargeSelected(mEyeEnlarging);
        mFURenderer.onBlurLevelSelected(mBlurLevel);
        mFURenderer.onCheekVSelected(mCheekV);
        mFURenderer.onCheekThinningSelected(mCheekThinning);
        mFURenderer.onColorLevelSelected(mColorLevel);

        mFURenderer.setFilterLevel(zhiganhui_level);
        mFURenderer.setNeedUpdate(true);
    }

    /**
     * 蜜桃
     */
    protected void selectM5() {
        if(mFURenderer == null)
            return;
        mFURenderer.setFilterName(BeautificationParam.MITAO_1);

        mFURenderer.onEyeEnlargeSelected(mEyeEnlarging);
        mFURenderer.onBlurLevelSelected(mBlurLevel);
        mFURenderer.onCheekVSelected(mCheekV);
        mFURenderer.onCheekThinningSelected(mCheekThinning);
        mFURenderer.onColorLevelSelected(mColorLevel);

        mFURenderer.setFilterLevel(mitao_level);
        mFURenderer.setNeedUpdate(true);
    }

    /**
     * 小清新
     */
    protected void selectM6() {
        if(mFURenderer == null)
            return;
        mFURenderer.setFilterName(BeautificationParam.XIAOQINGXIN_3);

        mFURenderer.onEyeEnlargeSelected(mEyeEnlarging);
        mFURenderer.onBlurLevelSelected(mBlurLevel);
        mFURenderer.onCheekVSelected(mCheekV);
        mFURenderer.onCheekThinningSelected(mCheekThinning);
        mFURenderer.onColorLevelSelected(mColorLevel);

        mFURenderer.setFilterLevel(xiaoqingxin_level);
        mFURenderer.setNeedUpdate(true);
    }
}
