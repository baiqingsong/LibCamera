package com.dawn.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.text.TextUtils;

import androidx.annotation.IdRes;

import com.dawn.camera.custom.CameraEffect;
import com.dawn.camera.custom.CameraUtil;
import com.dawn.camera.custom.util.CameraUtils;
import com.dawn.camera.utils.BitmapUtil;
import com.dawn.camera.utils.Constant;

import java.io.File;

public class CameraFactory {
    private Context mContext;
    //单例
    private static CameraFactory instance;

    private CameraFactory(Context context) {
        mContext = context;
        mCameraUtil = CameraUtil.getInstance(context);
    }

    public static CameraFactory getInstance(Context context) {
        if (instance == null) {
            synchronized (CameraFactory.class) {
                if (instance == null) {
                    instance = new CameraFactory(context);
                }
            }
        }
        return instance;
    }

    private CameraUtil mCameraUtil;//摄像头工具类
    private int mCameraOrientation = 0;//摄像头方向

    /**
     * 摄像头初始化
     * @param authPack 授权包
     */
    public void initCamera(byte[] authPack) {
        initCamera(authPack, mCameraOrientation);
    }

    /**
     * 摄像头初始化
     * @param authPack 授权包
     * @param cameraOrientation 摄像头方向
     */
    public void initCamera(byte[] authPack, int cameraOrientation) {
        this.mCameraOrientation = cameraOrientation;
        mCameraUtil.initCamera(authPack);
        mCameraUtil.changeOrientation(mCameraOrientation);//设置摄像头方向
    }

    /**
     * 切换方向
     * @param cameraOrientation 摄像头方向
     */
    public void changeOrientation(int cameraOrientation){
        this.mCameraOrientation = cameraOrientation;
        mCameraUtil.changeOrientation(mCameraOrientation);//设置摄像头方向
    }

    /**
     * render创建
     * @param glSurfaceView 控件
     */
    public void createRenderer(Activity activity, GLSurfaceView glSurfaceView, OnCameraListener listener){
        int number = Camera.getNumberOfCameras();
        if(number == 0)
            return;
        mCameraUtil.createRenderer(activity, glSurfaceView, new OnCameraListener() {
            @Override
            public void onCameraCreate() {
                if(listener != null)
                    listener.onCameraCreate();
            }

            @Override
            public void getPhoto(Bitmap bitmap, int picIndex, int width, int height) {
                if(listener != null){
                    if(mCameraOrientation != 0)
                        listener.getPhoto(toHorizontalMirror(bitmap), picIndex, width, height);
                    else
                        listener.getPhoto(bitmap, picIndex, width, height);
                }
            }

            @Override
            public void onDrawFrame(int texId, float[] mvpMatrix, float[] texMatrix, final int texWidth, final int texHeight, int currentPicIndex, int currentWidth, int currentHeight) {
                if(listener != null)
                    listener.onDrawFrame(texId, mvpMatrix, texMatrix, texWidth, texHeight, currentPicIndex, currentWidth, currentHeight);
            }
        });
    }

    /**
     * 开始
     */
    public void rendererOnResume(){
        mCameraUtil.rendererOnResume();
    }

    /**
     * 暂停
     */
    public void rendererOnPause(){
        mCameraUtil.rendererOnPause();
    }

    /**
     * 关闭相机
     */
    public void closeCamera(){
        if(mCameraUtil != null)
            mCameraUtil.closeCamera();
    }

    /**
     * 拍照
     * @param picIndex 拍照编号
     * @param width 照片宽
     * @param height 照片高
     */
    public void takePhoto(int picIndex, int width, int height){
        mCameraUtil.takePhoto(picIndex, width, height);
    }

    public enum FILTER_TYPE{FILTER_YUAN_TU, FILTER_HEI_BAI, FILTER_GE_XING, FILTER_ZHI_GAN_HUI, FILTER_MI_TAO, FILTER_XIAO_QING_XIN}
    /**
     * 设置滤镜
     * @param filterType 滤镜参数
     *        1. 默认滤镜
     *        2. 黑白滤镜
     *        3. 个性滤镜
     *        4. 质感灰滤镜
     *        5. 蜜桃滤镜
     *        6. 小清新滤镜
     */
    public void selectFilter(FILTER_TYPE filterType){
        switch (filterType){
            case FILTER_YUAN_TU:
                mCameraUtil.selectM1();
                break;
            case FILTER_HEI_BAI:
                mCameraUtil.selectM2();
                break;
            case FILTER_GE_XING:
                mCameraUtil.selectM3();
                break;
            case FILTER_ZHI_GAN_HUI:
                mCameraUtil.selectM4();
                break;
            case FILTER_MI_TAO:
                mCameraUtil.selectM5();
                break;
            case FILTER_XIAO_QING_XIN:
                mCameraUtil.selectM6();
                break;
        }
    }

    /**
     * 设置美颜参数
     * @param eyeEnlarging 大眼参数 0-1
     * @param blurLevel 磨皮参数 0-6
     * @param cheekThinning 瘦脸参数 0-1
     * @param colorLevel 美白参数 0-2
     */
    public void setBeautyParam(float eyeEnlarging, float blurLevel, float cheekThinning, float colorLevel){
        mCameraUtil.setEyeEnlarging(eyeEnlarging);
        mCameraUtil.setBlurLevel(blurLevel);
        mCameraUtil.setCheekThinning(cheekThinning);
        mCameraUtil.setColorLevel(colorLevel);
    }

    /**
     * 设置滤镜参数
     * @param yuanTu 原图级别 0-1
     * @param heiBai 黑白级别 0-1
     * @param zhiGanHui 质感灰级别 0-1
     * @param geXing 个性级别 0-1
     * @param miTao 蜜桃级别 0-1
     * @param xiaoQingXin 小清新级别 0-1
     */
    public void setFilterParam(float yuanTu, float heiBai, float zhiGanHui, float geXing, float miTao, float xiaoQingXin){
        mCameraUtil.setYuanTu(yuanTu);
        mCameraUtil.setHeiBai(heiBai);
        mCameraUtil.setZhiGanHui(zhiGanHui);
        mCameraUtil.setGeXing(geXing);
        mCameraUtil.setMiTao(miTao);
        mCameraUtil.setXiaoQingXin(xiaoQingXin);
    }

    /**
     * 选择贴图
     * @param path 贴图地址
     */
    public void selectSticker(String path){
        mCameraUtil.selectSticker(path);
    }

    /**
     * 设置贴图
     */
    public void selectSticker(String iconPath, String bundlePath){
        CameraEffect cameraEffect = null;
        if(TextUtils.isEmpty(bundlePath) || !new File(bundlePath).exists())
            cameraEffect = new CameraEffect("sticker" + System.currentTimeMillis(), 0, bundlePath, CameraEffect.EFFECT_TYPE_NONE);
        else
            cameraEffect = new CameraEffect("sticker" + System.currentTimeMillis(), 0, bundlePath, CameraEffect.EFFECT_TYPE_STICKER);
        cameraEffect.setIconPath(iconPath);
        selectSticker(cameraEffect);
    }

    /**
     * 设置贴图
     * @param cameraEffect 贴图实体类
     */
    public void selectSticker(CameraEffect cameraEffect){
        mCameraUtil.selectSticker(cameraEffect);
    }

    /**
     * 是否有摄像头
     */
    public boolean hasCamera(){
        return CameraUtils.isCamera();
    }

    /**
     * 获取拍照的bitmap
     */
    public void getTakePhotoBitmap(int texId, float[] texMatrix, float[] mvpMatrix, final int texWidth, final int texHeight, final BitmapUtil.OnReadBitmapListener listener){
        BitmapUtil.glReadBitmap(texId, texMatrix, mvpMatrix, texWidth, texHeight, listener, false);
    }


    /**
     * 获取左右镜像
     */
    protected Bitmap toHorizontalMirror(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(-1f, 1f); // 水平镜像翻转
        Bitmap rotationBitmap =  Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
        bmp.recycle();
        return rotationBitmap;
    }

    public enum STICKER_TYPE{
        STICKER_TYPE_NONE(0),
        EFFECT_TYPE_STICKER(1);
        STICKER_TYPE(int value) {
            this.value = value;
        }
        private int value;

        public int getValue() {
            return value;
        }
    }

}
