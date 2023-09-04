package com.dawn.beauty2;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.Log;

import androidx.annotation.NonNull;

import com.faceunity.core.callback.OperateCallback;
import com.faceunity.core.entity.FUCameraConfig;
import com.faceunity.core.entity.FURenderFrameData;
import com.faceunity.core.entity.FURenderInputData;
import com.faceunity.core.entity.FURenderOutputData;
import com.faceunity.core.enumeration.FUTransformMatrixEnum;
import com.faceunity.core.faceunity.FURenderManager;
import com.faceunity.core.listener.OnGlRendererListener;
import com.faceunity.core.media.photo.OnPhotoRecordingListener;
import com.faceunity.core.media.photo.PhotoRecordHelper;
import com.faceunity.core.media.video.OnVideoRecordingListener;
import com.faceunity.core.media.video.VideoRecordHelper;
import com.faceunity.core.renderer.CameraRenderer;
import com.faceunity.core.utils.FULogger;
import com.faceunity.core.utils.FileUtils;
import com.faceunity.core.utils.GlUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;


public class CameraFactory2 {
    //单例
    private static CameraFactory2 instance;

    private CameraFactory2() { }

    public static CameraFactory2 getInstance() {
        if (instance == null) {
            synchronized (CameraFactory2.class) {
                if (instance == null) {
                    instance = new CameraFactory2();
                }
            }
        }
        return instance;
    }

    public void cameraInit(Context context, byte[] authpack){
        FURenderManager.setKitDebug(FULogger.LogLevel.OFF);
        FURenderManager.setCoreDebug(FULogger.LogLevel.INFO);
        FURenderManager.registerFURender(context, authpack, new OperateCallback() {
            @Override
            public void onSuccess(int code, String msg) {
                Log.d("dawn", "success:" + msg);
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                Log.e("dawn", "errCode:" + errCode + "   errMsg:" + errMsg);
            }
        });
    }

    /**
     * 拍照
     */
    public void takePhoto(){
        if(!isTakePhoto)
            isTakePhoto = true;
    }

    /**
     * 开始录制
     */
    public void onStartRecord(GLSurfaceView surfaceView) {
        mVideoRecordHelper.startRecording(surfaceView, mCameraRenderer.getFUCamera().getCameraHeight(), mCameraRenderer.getFUCamera().getCameraWidth());
    }

    public void onStopRecord() {
        mVideoRecordHelper.stopRecording();
    }

    protected CameraRenderer mCameraRenderer;
    private PhotoRecordHelper mPhotoRecordHelper;
    private VideoRecordHelper mVideoRecordHelper;

    private boolean isTakePhoto;//是否拍照
    private volatile boolean isRecordingPrepared = false;//是否开始视频录制

    /**
     * renderer创建
     */
    public void rendererCreate(Context context, GLSurfaceView surfaceView, CameraInterface listener){
        mPhotoRecordHelper = new PhotoRecordHelper(bitmap -> {
            if(listener != null)
                listener.getPhoto(bitmap);
        });
        mVideoRecordHelper = new VideoRecordHelper(context, new OnVideoRecordingListener() {
            @Override
            public void onPrepared() {
                isRecordingPrepared = true;
            }

            @Override
            public void onProcess(Long time) {

            }

            @Override
            public void onFinish(File file) {
                isRecordingPrepared = false;
                if(listener != null)
                    listener.getRecord(file);
            }
        });
        mCameraRenderer = new CameraRenderer(surfaceView, getCameraConfig(), mOnGlRendererListener);
    }

    /**
     * 开始
     */
    public void rendererOnResume(){
        if(mCameraRenderer != null)
            mCameraRenderer.onResume();
    }

    /**
     * 暂停
     */
    public void rendererOnPause(){
        if(mCameraRenderer != null)
            mCameraRenderer.onPause();
    }

    /**
     * 关闭
     */
    public void rendererOnDestroy(){
        if(mCameraRenderer != null)
            mCameraRenderer.onDestroy();
    }

    /**
     * 配置相机参数
     *
     * @return CameraBuilder
     */
    protected FUCameraConfig getCameraConfig() {
        FUCameraConfig cameraConfig = new FUCameraConfig();
        cameraConfig.setCameraWidth(1920);
        cameraConfig.setCameraHeight(1080);
        return cameraConfig;
    }

    /* CameraRenderer 回调*/
    private final OnGlRendererListener mOnGlRendererListener = new OnGlRendererListener() {

        @Override
        public void onSurfaceCreated() {

        }

        @Override
        public void onSurfaceChanged(int width, int height) {

        }

        @Override
        public void onRenderBefore(FURenderInputData inputData) {
            inputData.getRenderConfig().setInputBufferMatrix(FUTransformMatrixEnum.CCROT0);
            inputData.getRenderConfig().setInputTextureMatrix(FUTransformMatrixEnum.CCROT0);
        }


        @Override
        public void onRenderAfter(@NonNull FURenderOutputData outputData, @NotNull FURenderFrameData frameData) {
            recordingData(outputData, frameData.getTexMatrix());
        }

        @Override
        public void onDrawFrameAfter() {

        }


        @Override
        public void onSurfaceDestroy() {

        }
        /*录制保存*/
        private void recordingData(FURenderOutputData outputData, float[] texMatrix) {
            if (outputData == null || outputData.getTexture() == null || outputData.getTexture().getTexId() <= 0) {
                return;
            }
            if (isRecordingPrepared) {
                mVideoRecordHelper.frameAvailableSoon(outputData.getTexture().getTexId(), texMatrix, GlUtil.IDENTITY_MATRIX);
            }
            if (isTakePhoto) {
                isTakePhoto = false;
                mPhotoRecordHelper.sendRecordingData(outputData.getTexture().getTexId(), texMatrix, GlUtil.IDENTITY_MATRIX, outputData.getTexture().getWidth(), outputData.getTexture().getHeight());
            }
        }

    };


}
