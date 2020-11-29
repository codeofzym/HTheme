package com.huawei.livingwallpaper.yiran.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.zym.mediaplayer.ZMediaPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final String WATER_MARK_PATH = Environment.getExternalStorageDirectory()
            .getPath() + "/Pictures/";
    private static final String WATER_MARK_FILE_NAME = "80bg.png";

    public static void loadVideoFiles(final Context context, final ZMediaPlayer player) {
        if(context == null || player == null) {
            return;
        }

        final String path = context.getFilesDir().getAbsoluteFile() + "/";
        File file = new File(path + "video.mp4");
        if(!file.exists()) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        InputStream is = context.getAssets().open("video.mp4");
                        byte[] buf = new byte[8 * 1024];
                        FileOutputStream fos = new FileOutputStream(path + "video.mp4_tem");
                        int length = 0;
                        while ((length = is.read(buf)) > 0) {
                            fos.write(buf, 0 , length);
                        }
                        fos.flush();
                        is.close();
                        fos.close();
                        File sFile = new File(path + "video.mp4_tem");
                        sFile.renameTo(new File(path + "video.mp4"));
                        player.setDataResource(path + "video.mp4");
                        player.start();
                        player.setBreakPointFrameIndex(new long[]{58 * 1000, 133*1000});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            WLog.i("FileUtils", "file exist");
            player.setDataResource(path + "video.mp4");
            player.start();
            player.setBreakPointFrameIndex(new long[]{58 * 1000, 133*1000});
        }
    }

    public static void loadImageFiles(final Context context, final ZMediaPlayer player) {
        WLog.e("FileUtil", "loadImageFiles");
        if(context == null || player == null) {
            return;
        }

        final String path = context.getFilesDir().getAbsoluteFile() + "/";
        final DisplayMetrics dis = context.getResources().getDisplayMetrics();
        WLog.i("FileUtils", "" + dis.widthPixels + " height:" + dis.heightPixels);

        File file = new File(WATER_MARK_PATH);
        if(!file.exists()) {
            WLog.e("FileUtil", "file is not exists");
            return;
        }

        file = new File(WATER_MARK_PATH + WATER_MARK_FILE_NAME);
        if(file.exists()) {
            WLog.e("FileUtil", "read file");
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        InputStream is = new FileInputStream(WATER_MARK_PATH + WATER_MARK_FILE_NAME);
                        byte[] buf = new byte[8 * 1024];
                        FileOutputStream fos = new FileOutputStream(path + "watermark.png_tem");
                        int length = 0;
                        while ((length = is.read(buf)) > 0) {
                            fos.write(buf, 0 , length);
                        }
                        fos.flush();
                        is.close();
                        fos.close();
                        File sFile = new File(path + "watermark.png_tem");
                        sFile.renameTo(new File(path + "watermark.png"));
                        Bitmap bitmap = BitmapFactory.decodeFile(path + "watermark.png");
                        WLog.e("FileUtil", "read file" + bitmap.getWidth());
                        player.setWatermark(bitmap, dis.widthPixels - bitmap.getWidth(),
                                dis.heightPixels - bitmap.getHeight() - 300);

                        sFile = new File(WATER_MARK_PATH + WATER_MARK_FILE_NAME);
                        sFile.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            WLog.i("FileUtil", "file is exists");
            file = new File(path + "watermark.png");
            if(file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path + "watermark.png");
                player.setWatermark(bitmap, dis.widthPixels - bitmap.getWidth(),
                        dis.heightPixels - bitmap.getHeight() - 300);
            }
        }
    }

    public static void copyAllFileFromAssets(final Context context, final ZMediaPlayer player) {
        AssetManager manager = context.getAssets();
        try {
            String name[] = manager.list("");
            int index = 0;
            for (int i = 0; i < name.length; i++) {
                Log.i(TAG, "name" + i + ":" + name[i]);
                if(name[i].endsWith(".mp4")) {
                    String path = getFilePath(context, index);
                    if(index == 0) {
                        copyFileFromAssets(manager, name[i], path, player);
                    } else {
                        copyFileFromAssets(manager, name[i], path, null);
                    }
                    index ++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFilePath(Context context, int index) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(context.getFilesDir().getAbsolutePath());
        buffer.append(File.separator);
        buffer.append(index);
        buffer.append(File.separator);
        buffer.append("video.mp4");
        Log.i(TAG, "getFilePath:" + buffer.toString());
        return buffer.toString();
    }

    public static boolean checkFolderExists(String path) {
        if(path == null) {
            return false;
        }
        int dex = path.lastIndexOf(File.separator);
        if(dex > 0) {
            File file = new File(path.substring(0, dex));
            if(!file.exists()) {
                file.mkdirs();
            }

            if(file.isDirectory()) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkFileExists(String path) {
        if(path == null) {
            return false;
        }
        File file = new File(path);
        if(file.isFile() && file.exists()) {
            return true;
        }
        return false;
    }

    private static void copyFileFromAssets(final AssetManager manager, final String src,
                                           final String des, final ZMediaPlayer player) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                InputStream is = null;
                FileOutputStream fos = null;

                checkFolderExists(des);
                try {
                    is = manager.open(src);
                    fos = new FileOutputStream(des + "_tem");
                    byte[] buf = new byte[8 * 1024];
                    int length = 0;
                    while ((length = is.read(buf)) > 0) {
                        fos.write(buf, 0 , length);
                    }
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(is != null) {
                            is.close();
                        }
                        if(fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                File sFile = new File(des + "_tem");
                sFile.renameTo(new File(des));
                if(player != null) {
                    player.setDataResource(des);
                    player.start();
                }
            }
        }.start();
    }
}
