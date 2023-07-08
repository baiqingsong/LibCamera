package com.dawn.beauty.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.text.TextUtils;
import android.util.Log;

import com.dawn.beauty.FURenderer;
import com.dawn.beauty.OnCameraListener;
import com.dawn.beauty.R;
import com.dawn.beauty.custom.util.CameraUtils;
import com.dawn.beauty.custom.util.GlUtil;
import com.dawn.beauty.entity.Effect;
import com.dawn.beauty.param.BeautificationParam;
import com.dawn.beauty.utils.BitmapUtil;

public class CameraUtil {
    private Context mContext;
    private Activity activity;
    protected static int takePhotoOrientation = 0;//摄像头镜像显示还是同向显示
    protected BaseCameraRenderer mCameraRenderer;
    protected FURenderer mFURenderer;

    protected volatile boolean mIsNeedTakePic = false;//是否需要拍照
    private int currentPicIndex;//当前拍照编号
    private int currentWidth;//当前照片宽
    private int currentHeight;//当前照片高
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

    public OnCameraListener mListener;//回调函数
    /**
     * 摄像头初始化
     */
    public void initCamera(byte[] authpack){
        FURenderer.initFURenderer(mContext, authpack);
    }

    private CameraUtil(Context context){
        mContext = context;
    }

    public static CameraUtil cameraUtil;
    public static CameraUtil getInstance(Context context){
        if(cameraUtil == null)
            cameraUtil = new CameraUtil(context);
        return cameraUtil;
    }

    /**
     * 开启美颜
     */
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(mContext)
                .maxFaces(4)
                .maxHumans(1)
                .setOnBundleLoadCompleteListener(what -> Log.i("dawn", "美颜加载成功"))
                .setLoadAiHumanProcessor(true)
                .inputImageOrientation(CameraUtils.getFrontCameraOrientation())
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .build();
    }

    public void setTrackOrientation(int rotation){
        mFURenderer.setTrackOrientation(rotation);
    }

    public void rendererOnResume(){
        if(mCameraRenderer != null)
            mCameraRenderer.onResume();
    }

    public void rendererOnPause(){
        if(mCameraRenderer != null)
            mCameraRenderer.onPause();
    }

    public void rendererOnDestroy(){

    }

    public void createRenderer(Activity activity, GLSurfaceView glSurfaceView, OnCameraListener listener){
        mListener = listener;
        if(glSurfaceView != null){
            glSurfaceView.setEGLContextClientVersion(GlUtil.getSupportGLVersion(mContext));
        }
        mCameraRenderer = new Camera1Renderer(activity, glSurfaceView, new OnRendererStatusListener() {
            @Override
            public void onSurfaceCreated() {
                if(mFURenderer == null){
                    mFURenderer = initFURenderer();
                    if(mFURenderer != null){
                        mFURenderer.onSurfaceCreated();
                        mFURenderer.setBeautificationOn(true);
                    }
                    if(mListener != null)
                        mListener.onCameraCreate();
//                    selectCurrentM();
//                    if(Constant.TakePhotoBeauty)
//                        selectCurrentSticker();
                }
            }

            @Override
            public void onSurfaceChanged(int viewWidth, int viewHeight) {

            }

            @Override
            public int onDrawFrame(byte[] cameraNv21Byte, int cameraTexId, int cameraWidth, int cameraHeight, float[] mvpMatrix, float[] texMatrix, long timeStamp) {
                int fuTexId = 0;
                if(mFURenderer != null){
                    fuTexId = mFURenderer.onDrawFrame(cameraNv21Byte, cameraTexId, cameraWidth, cameraHeight);
                }
                getPic(fuTexId, GlUtil.IDENTITY_MATRIX, texMatrix, cameraWidth, cameraHeight);
                if(mListener != null)
                    mListener.onDrawFrame(fuTexId, GlUtil.IDENTITY_MATRIX, texMatrix, cameraWidth, cameraHeight, currentPicIndex, currentWidth, currentHeight);
                return fuTexId;
            }

            @Override
            public void onSurfaceDestroy() {
                if(mFURenderer != null){
                    mFURenderer.setBeautificationOn(false);
                    mFURenderer.onSurfaceDestroyed();
                    mFURenderer = null;
                }
            }

            @Override
            public void onCameraChanged(int cameraFacing, int cameraOrientation) {

            }
        });
        if(glSurfaceView != null){
            glSurfaceView.setRenderer(mCameraRenderer);
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }

    }

    /**
     * 拍照
     * @param picIndex 拍照编号
     * @param width 照片宽
     * @param height 照片高
     */
    public void takePhoto(int picIndex, int width, int height){
        this.currentPicIndex = picIndex;
        this.currentWidth = width;
        this.currentHeight = height;
        mIsNeedTakePic = true;
    }

    /**
     * 调整显示方向，镜像或同向
     * @param orientation 0镜像或1同向
     */
    public void changeOrientation(int orientation){
        takePhotoOrientation = orientation;
    }
    BitmapUtil.OnReadBitmapListener mOnReadBitmapListener = new BitmapUtil.OnReadBitmapListener() {

        @Override
        public void onReadBitmapListener(final Bitmap photo) {
            if (photo == null) {
                return;
            }
            if(mListener != null)
                mListener.getPhoto(photo, currentPicIndex, currentWidth, currentHeight);
        }
    };

    /**
     * 获取照片
     */
    private void getPic(int texId, float[] mvpMatrix, float[] texMatrix, final int texWidth, final int texHeight) {
        if (!mIsNeedTakePic) {
            return;
        }
        mIsNeedTakePic = false;
        BitmapUtil.glReadBitmap(texId, texMatrix, mvpMatrix, texWidth, texHeight, mOnReadBitmapListener, false);
    }

    /**
     * 刷新摄像头
     */
    public void setNeedUpdate(){
        if(mFURenderer == null)
            return;
        mFURenderer.setNeedUpdate(true);
    }


    /**
     * 原图
     */
    public void selectM1() {
        if(mFURenderer == null)
            return;
        mFURenderer.setFilterName(BeautificationParam.ZHIGANHUI_2);
        selectCustom();

        mFURenderer.setFilterLevel(yuantu_level);
        mFURenderer.setNeedUpdate(true);
    }

    /**
     * 黑白
     */
    public void selectM2() {
        if(mFURenderer == null)
            return;
        mFURenderer.setFilterName(BeautificationParam.HEIBAI_1);

        selectCustom();

        mFURenderer.setFilterLevel(heibai_level);
        mFURenderer.setNeedUpdate(true);
    }

    /**
     * 个性
     */
    public void selectM3() {
        if(mFURenderer == null)
            return;
        mFURenderer.setFilterName(BeautificationParam.GEXING_5);

        selectCustom();

        mFURenderer.setFilterLevel(gexing_level);
        mFURenderer.setNeedUpdate(true);
    }

    /**
     * 质感灰
     */
    public void selectM4() {
        if(mFURenderer == null)
            return;
        mFURenderer.setFilterName(BeautificationParam.HEIBAI_3);

        selectCustom();

        mFURenderer.setFilterLevel(zhiganhui_level);
        mFURenderer.setNeedUpdate(true);
    }

    /**
     * 蜜桃
     */
    public void selectM5() {
        if(mFURenderer == null)
            return;
        mFURenderer.setFilterName(BeautificationParam.MITAO_1);

        selectCustom();

        mFURenderer.setFilterLevel(mitao_level);
        mFURenderer.setNeedUpdate(true);
    }

    /**
     * 小清新
     */
    public void selectM6() {
        if(mFURenderer == null)
            return;
        mFURenderer.setFilterName(BeautificationParam.XIAOQINGXIN_3);

        selectCustom();

        mFURenderer.setFilterLevel(xiaoqingxin_level);
        mFURenderer.setNeedUpdate(true);
    }

    /**
     * 用用设置
     */
    public void selectCustom(){
        mFURenderer.onEyeEnlargeSelected(mEyeEnlarging);
        mFURenderer.onBlurLevelSelected(mBlurLevel);
        mFURenderer.onCheekVSelected(mCheekV);
        mFURenderer.onCheekThinningSelected(mCheekThinning);
        mFURenderer.onColorLevelSelected(mColorLevel);
    }

    /**
     * 设置原图滤镜
     * @param yuanTu 滤镜值
     */
    public void setYuanTu(float yuanTu){
        if(yuanTu > 1)
            yuanTu = 1f;
        if(yuanTu < 0)
            yuanTu = 0f;
        this.yuantu_level = yuanTu;
        if(mFURenderer != null)
            mFURenderer.setFilterLevel(yuantu_level);
    }

    /**
     * 设置黑白滤镜
     * @param heiBai 滤镜值
     */
    public void setHeiBai(float heiBai){
        if(heiBai > 1)
            heiBai = 1f;
        if(heiBai < 0)
            heiBai = 0f;
        this.heibai_level = heiBai;
        if(mFURenderer != null)
            mFURenderer.setFilterLevel(heibai_level);
    }

    /**
     * 设置质感灰滤镜
     * @param zhiGanHui 滤镜值
     */
    public void setZhiGanHui(float zhiGanHui){
        if(zhiGanHui > 1)
            zhiGanHui = 1f;
        if(zhiGanHui < 0)
            zhiGanHui = 0f;
        this.zhiganhui_level = zhiGanHui;
        if(mFURenderer != null)
            mFURenderer.setFilterLevel(zhiganhui_level);
    }

    /**
     * 设置个性滤镜
     * @param geXing 滤镜值
     */
    public void setGeXing(float geXing){
        if(geXing > 1)
            geXing = 1f;
        if(geXing < 0)
            geXing = 0f;
        this.gexing_level = geXing;
        if(mFURenderer != null)
            mFURenderer.setFilterLevel(gexing_level);
    }

    /**
     * 设置蜜桃滤镜
     * @param miTao 滤镜值
     */
    public void setMiTao(float miTao){
        if(miTao > 1)
            miTao = 1f;
        if(miTao < 0)
            miTao = 0f;
        this.mitao_level = miTao;
        if(mFURenderer != null)
            mFURenderer.setFilterLevel(mitao_level);
    }

    /**
     * 设置小清新滤镜
     * @param xiaoQingXin 滤镜值
     */
    public void setXiaoQingXin(float xiaoQingXin){
        if(xiaoQingXin > 1)
            xiaoQingXin = 1f;
        if(xiaoQingXin < 0)
            xiaoQingXin = 0f;
        this.xiaoqingxin_level = xiaoQingXin;
        if(mFURenderer != null)
            mFURenderer.setFilterLevel(xiaoqingxin_level);
    }

    /**
     * 设置大眼
     * @param eyeEnlarging 0-1
     */
    public void setEyeEnlarging(float eyeEnlarging){
        if(eyeEnlarging > 1)
            eyeEnlarging = 1f;
        if(eyeEnlarging < 0)
            eyeEnlarging = 0f;
        mEyeEnlarging = eyeEnlarging;
    }

    /**
     * 设置磨皮
     * @param blurLevel 0-6
     */
    public void setBlurLevel(float blurLevel){
        if(blurLevel > 6)
            blurLevel = 6f;
        if(blurLevel < 0)
            blurLevel = 0f;
        mBlurLevel = blurLevel;
    }

    /**
     * 设置v脸
     * @param cheekV 0-1
     */
    public void setCheekV(float cheekV){
        if(cheekV > 1)
            cheekV = 1f;
        if(cheekV < 0)
            cheekV = 0f;
        mCheekV = cheekV;
    }

    /**
     * 设置瘦脸
     * @param cheekThinning 0-1
     */
    public void setCheekThinning(float cheekThinning){
        if(cheekThinning > 1)
            cheekThinning = 1f;
        if(cheekThinning < 0)
            cheekThinning = 0f;
        mCheekThinning = cheekThinning;
    }

    /**
     * 设置美白
     * @param colorLevel 0-2
     */
    public void setColorLevel(float colorLevel){
        if(colorLevel > 2)
            colorLevel = 2f;
        if(colorLevel < 0)
            colorLevel = 0f;
        mColorLevel = colorLevel;
    }

    /**
     * 选择贴图
     * @param path 贴图地址
     */
    public void selectSticker(String path){
//        customEffects.add(new Effect("nihongdeng", R.drawable.nihongdeng, "effect/normal/nihongdeng.bundle", 4, Effect.EFFECT_TYPE_STICKER, 0));
        Effect effect;
        if(TextUtils.isEmpty(path)){
            effect = new Effect("select", R.drawable.ic_delete_all, "", 4, Effect.EFFECT_TYPE_NONE, 0);
        }else{
            effect = new Effect("select", R.drawable.ic_delete_all, path, 4, Effect.EFFECT_TYPE_STICKER, 0);
        }
        if(mFURenderer != null)
            mFURenderer.onEffectSelected(effect);
    }

    public void selectSticker(CameraEffect effect){
        if(mFURenderer != null)
            mFURenderer.onEffectSelected(effect);
    }

    /**
     * 关闭相机
     */
    public void closeCamera(){
        if(mCameraRenderer != null)
            mCameraRenderer.closeCamera();
    }

}
