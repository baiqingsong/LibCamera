package com.dawn.beauty.record.media.photo;

import android.graphics.Bitmap;

public interface OnPhotoRecordingListener {


    /**
     * 录制完成回调
     */
    void onRecordSuccess(Bitmap bitmap);
}
