# LibCamera
 美颜的引用

#### CameraFactory
摄像头工厂类，用于创建摄像头对象

* getInstance 获取摄像头工厂对象
* initCamera 初始化摄像头
* changeOrientation 改变摄像头方向
* createRenderer 创建渲染器
* rendererOnResume 渲染器onResume
* rendererOnPause 渲染器onPause
* closeCamera 关闭摄像头
* takePhoto 拍照
* setBeautyParam 设置美颜参数
* setFilterParam 设置滤镜参数
* selectFilter 选择滤镜
* selectSticker 选择贴纸

#### FILTER_TYPE
滤镜类型
* FILTER_YUAN_TU 原图
* FILTER_HEI_BAI 黑白
* FILTER_GE_XING 个性
* FILTER_ZHI_GAN_HUI 质感灰
* FILTER_MI_TAO 蜜桃
* FILTER_XIAO_QING_XIN 小清新

#### STICKER_TYPE
贴纸类型
* STICKER_TYPE_NONE 无贴纸
* EFFECT_TYPE_STICKER 一般贴纸
