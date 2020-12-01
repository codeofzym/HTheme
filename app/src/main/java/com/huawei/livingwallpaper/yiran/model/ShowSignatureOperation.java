package com.huawei.livingwallpaper.yiran.model;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.huawei.livingwallpaper.yiran.wuwangbuzhiweidongxiao.PermissionActivity;
import com.huawei.livingwallpaper.yiran.wuwangbuzhiweidongxiao.PermissionMgr;
import com.huawei.livingwallpaper.yiran.wuwangbuzhiweidongxiao.WLog;
import com.zym.mediaplayer.ZMediaPlayer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ShowSignatureOperation implements IGestureOperation{
    private static final String TAG = "ShowSignatureOperation";
    private static final String FILE_NAME = "83bg.png";
    private static final int POINT_NUM_3 = 3;
    private static final boolean OPTION3_OPEN = true;

    protected float mPoint3DownX = 0;
    protected float mPoint3DownY = 0;
    protected boolean mPoint3DownFlag = false;

    protected Context mContext;
    protected ZMediaPlayer mMediaPlayer;
    private boolean mShowSignature = false;

    public ShowSignatureOperation(Context context, ZMediaPlayer player) {
        this.mContext = context;
        this.mMediaPlayer = player;
    }

    private String getSignaturePath() {
        if(mContext == null) {
            return null;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + Environment.DIRECTORY_DCIM + File.separator + Environment.DIRECTORY_PICTURES;
        }
    }

    @Override
    public boolean onPoint3Down(MotionEvent event) {
        if(!OPTION3_OPEN) {
            return false;
        }
        if(event.getAction() == MotionEvent.ACTION_POINTER_3_DOWN
                && event.getPointerCount() == POINT_NUM_3){
            Log.i(TAG, "onPoint3Down into:");
            mPoint3DownFlag = true;
            mPoint3DownX = event.getX(0);
            mPoint3DownY = event.getY(0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onDownUp(MotionEvent event) {
        Log.i(TAG, "onDownUp");
        if(!mPoint3DownFlag && event.getAction() == MotionEvent.ACTION_UP) {
            mMediaPlayer.start();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPoint3Up(MotionEvent event) {
        if(mPoint3DownFlag && (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_POINTER_UP)){
            mPoint3DownFlag = false;
            float hor = event.getX(0) - mPoint3DownX;
            float ver = event.getY(0) - mPoint3DownY;
            if(Math.abs(hor) > Math.abs(ver)) {
                if(hor > 0) {
                    moveHorizontalRight();
                } else {
                    moveHorizontalLeft();
                }
            }  else {
                if(ver > 0) {
                    moveVerticalDown();
                } else {
                    moveVerticalUp();
                }
            }
            return true;
        }
        return false;
    }

    protected void moveVerticalUp() {
        Log.i(TAG, "moveVerticalUp");
        if(mContext == null) {
            return;
        }
        if(PermissionMgr.getInstance().deniedExternalStoragePermission(mContext)) {
            Intent intent = new Intent(mContext, PermissionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            return;
        }
        if(mShowSignature) {
            mShowSignature = false;
            mMediaPlayer.setWatermark(null, 0, 0);
        } else {
            String path = getSignaturePath();
            if(path == null) {
                WLog.i(this, "path is null");
                return;
            }
            final DisplayMetrics dis = mContext.getResources().getDisplayMetrics();
            File file = new File(path + File.separator + FILE_NAME);
            Bitmap bitmap = null;
            if(file.exists()) {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            } else {
                AssetManager am = mContext.getAssets();
                InputStream is = null;
                try {
                    is = am.open(FILE_NAME);
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            WLog.i(this, "setWatermark");
            mMediaPlayer.setWatermark(bitmap, dis.widthPixels - bitmap.getWidth(),
                    dis.heightPixels - bitmap.getHeight() - 300);
            mShowSignature = true;
        }
    }

    protected void moveVerticalDown() {
        Log.i(TAG, "moveVerticalDown:");
        moveVerticalUp();
    }

    protected void moveHorizontalLeft() {
        Log.i(TAG, "moveHorizontalLeft:");
    }

    protected void moveHorizontalRight() {
        Log.i(TAG, "moveHorizontalRight:");
    }
}
