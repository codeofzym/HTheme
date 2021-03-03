package com.huawei.livingwallpaper.yiran.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.huawei.livingwallpaper.yiran.zhizunliuguangbaoshilan.AssertUtils;
import com.huawei.livingwallpaper.yiran.zhizunliuguangbaoshilan.FileUtils;
import com.huawei.livingwallpaper.yiran.zhizunliuguangbaoshilan.PermissionActivity;
import com.huawei.livingwallpaper.yiran.zhizunliuguangbaoshilan.PermissionMgr;
import com.huawei.livingwallpaper.yiran.zhizunliuguangbaoshilan.R;
import com.huawei.livingwallpaper.yiran.zhizunliuguangbaoshilan.WLog;
import com.zym.mediaplayer.ZMediaPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SwitchFileOperation implements IGestureOperation{
    private static final String TAG = "SwitchFileOperation";
    private static final String FILE_NAME = "zt5.png";
    private static final int POINT_NUM_3 = 3;
    private static final boolean OPTION3_OPEN = false;

    protected float mPoint3DownX = 0;
    protected float mPoint3DownY = 0;
    protected boolean mPoint3DownFlag = false;
    private boolean mLoop = false;
    private boolean mShowSignature = false;

    protected Context mContext;
    protected ZMediaPlayer mMediaPlayer;
    private float mSpeed = 1.0f;

    public SwitchFileOperation(Context context, ZMediaPlayer player) {
        this.mContext = context;
        this.mMediaPlayer = player;
    }

    private String getSignaturePath() {
        if(mContext == null) {
            return null;
        }

        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + Environment.DIRECTORY_PICTURES;
    }

    private String getSignatureFilePath() {
        String path = getSignaturePath();
        if(path == null) {
            return null;
        }

        File folder = new File(path);
        if(!folder.exists() || folder.isFile()) {
            return null;
        }
        File[] files = folder.listFiles();
        for(File f : files) {
            if(f.getName().equalsIgnoreCase(FILE_NAME)) {
                return f.getAbsolutePath();
            }
        }

        return null;
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

    }

    protected void moveVerticalDown() {
        Log.i(TAG, "moveVerticalDown:");

    }

    protected void moveHorizontalLeft() {
        Log.i(TAG, "moveHorizontalLeft:");

    }

    protected void moveHorizontalRight() {
        Log.i(TAG, "moveHorizontalRight:");

    }

    private void switchPreFile() {
        if(mContext == null) {
            return;
        }
        int index = AssertUtils.getPrePathIndex(mContext);
        String path = FileUtils.getFilePath(mContext, index);
        if(!new File(path).exists()) {
            Log.e(TAG, "File is not exists");
            return;
        }
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer.setDataResource(path);
            mMediaPlayer.start();
        }
    }

    private void switchNextFile() {
        if(mContext == null) {
            return;
        }
        int index = AssertUtils.getNextPathIndex(mContext);
        String path = FileUtils.getFilePath(mContext, index);
        if(!new File(path).exists()) {
            Log.e(TAG, "File is not exists");
            return;
        }
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer.setDataResource(path);
            mMediaPlayer.start();
        }
    }

    private void switchLoop() {
        mLoop = !mLoop;
        mMediaPlayer.setLooping(mLoop);
        if(mLoop) {
            Toast.makeText(mContext, R.string.tip_loop_change, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, R.string.tip_loop_change_no, Toast.LENGTH_SHORT).show();
        }
    }

    private void switchWatermark() {
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
            String path = getSignatureFilePath();
            if(path == null) {
                Toast.makeText(mContext, mContext.getString(R.string.tip_no_mark_file) + FILE_NAME, Toast.LENGTH_LONG).show();
                return;
            }

            final DisplayMetrics dis = mContext.getResources().getDisplayMetrics();
            File file = new File(path);
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(bitmap == null) {
                WLog.i(this, "bitmap == null");
                return;
            }
            WLog.i(this, "setWatermark");
            mMediaPlayer.setWatermark(bitmap, dis.widthPixels - bitmap.getWidth(),
                    dis.heightPixels - bitmap.getHeight() - mContext.getResources()
                            .getDimensionPixelSize(R.dimen.mark_bottom));
            mShowSignature = true;
        }
    }

    private void switchSpeed() {
        if (mMediaPlayer == null) {
            return;
        }
        mSpeed += 0.2f;
        if (mSpeed > 1.2f) {
            mSpeed = 0.8f;
        }
        mMediaPlayer.setPlaybackSpeed(mSpeed);
        Toast.makeText(mContext, String.format(mContext.getString(R.string.tip_speed_change), String.valueOf(mSpeed)),
                Toast.LENGTH_LONG).show();
    }
}
