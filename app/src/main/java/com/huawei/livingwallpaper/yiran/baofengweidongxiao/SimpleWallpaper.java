package com.huawei.livingwallpaper.yiran.baofengweidongxiao;

import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.huawei.livingwallpaper.yiran.model.IGestureOperation;
import com.huawei.livingwallpaper.yiran.model.SwitchFileOperation;
import com.zym.mediaplayer.ZMediaPlayer;

import java.io.File;

public class SimpleWallpaper extends WallpaperService {
    private static final String TAG = "SimpleWallpaper";
    @Override
    public void onCreate() {
        super.onCreate();
        WLog.d(this, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WLog.d(this, "onDestroy");
    }

    @Override
    public Engine onCreateEngine() {
        WLog.d(this, "onCreateEngine");
        return new VideoEngine();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        WLog.d(this, "onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        WLog.d(this, "onTrimMemory");
    }

    private class VideoEngine extends Engine {
        private static final int VIDEO_SCALE_MODE = MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;

        private final ZMediaPlayer mMediaPlayer = new ZMediaPlayer();
        private boolean isReady = false;
        private boolean mCurrentLooping = false;
        private IGestureOperation mOperation = new SwitchFileOperation(getApplicationContext(), mMediaPlayer);
        private Surface mSurface = null;


        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            WLog.i(TAG, "onCreate ");
            mMediaPlayer.setLooping(false);
            setTouchEventsEnabled(true);

            int index = AssertUtils.getCureentPathIndex(getApplicationContext());
            String path = FileUtils.getFilePath(getApplicationContext(), index);
            File file = new File(path);
            if(file.exists()) {
                mMediaPlayer.setDataResource(path);
            } else {
                FileUtils.copyAllFileFromAssets(getApplicationContext(), mMediaPlayer);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            WLog.i(TAG, "onDestroy ");
            mMediaPlayer.release();
        }


        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if(visible) {
                if(!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                }
            } else {
                if(mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            WLog.i(this, "event:" + event.getAction());
            if(!mOperation.onPoint3Down(event)) {
                if(!mOperation.onPoint3Up(event)) {
                    mOperation.onDownUp(event);
                }
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            WLog.i(TAG, "onSurfaceChanged ");
            mSurface = holder.getSurface();
            mMediaPlayer.setSurface(mSurface);
        }

        @Override
        public void onSurfaceRedrawNeeded(SurfaceHolder holder) {
            super.onSurfaceRedrawNeeded(holder);
            WLog.i(TAG, "onSurfaceRedrawNeeded ");
            mSurface = holder.getSurface();
            mMediaPlayer.setSurface(mSurface);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            WLog.i(TAG, "onSurfaceCreated");
            mSurface = holder.getSurface();
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.start();
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            WLog.i(TAG, "onSurfaceDestroyed");
            mMediaPlayer.release();
        }
    }
}
