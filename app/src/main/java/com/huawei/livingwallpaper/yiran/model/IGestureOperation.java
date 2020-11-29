package com.huawei.livingwallpaper.yiran.model;

import android.content.Context;
import android.view.MotionEvent;

public interface IGestureOperation {
    public boolean onPoint3Down(MotionEvent event);
    public boolean onPoint3Up(MotionEvent event);
    public boolean onDownUp(MotionEvent event);
}
