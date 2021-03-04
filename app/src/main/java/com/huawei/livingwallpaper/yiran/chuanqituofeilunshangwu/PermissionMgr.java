package com.huawei.livingwallpaper.yiran.chuanqituofeilunshangwu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PermissionMgr {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final PermissionMgr sInstance = new PermissionMgr();

    private PermissionMgr() {
    }

    ;

    public static PermissionMgr getInstance() {
        return sInstance;
    }

    public boolean deniedExternalStoragePermission(Context context) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            return true;
        }
        return false;
    }

    public void requestExternalStoragePermission(Activity context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        for (int i = 0; i < grantResults.length; i++) {
            if (permissions[i].equals(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    return;
                }
            }
        }
        if (showConfirmDialog(activity)) {
            requestExternalStoragePermission(activity);
            return;
        }

        askForPermission(activity);
    }

    private boolean showConfirmDialog(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return false;
        }

        // 还可以弹框确认
        if (activity.shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return true;
        }

        return false;
    }

    private void askForPermission(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Need Permission!");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName())); // 根据包名打开对应的设置界面
                activity.startActivity(intent);
            }
        });

        builder.setCancelable(false);
        builder.create().show();
    }
}
