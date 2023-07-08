package com.dawn.beauty.custom;


import com.dawn.beauty.entity.Effect;

public class CameraEffect extends Effect {
    public static final int EFFECT_TYPE_NONE = 0;
    public static final int EFFECT_TYPE_STICKER = 1;
    private String iconPath;//图片路径
    public CameraEffect(String bundleName, int iconId, String bundlePath, int type) {
        super(bundleName, iconId, bundlePath, 4, type, 0);
    }

    @Override
    public int getIconId() {
        return super.getIconId();
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
}
