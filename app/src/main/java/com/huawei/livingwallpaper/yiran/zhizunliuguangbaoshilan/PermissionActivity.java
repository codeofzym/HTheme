package com.huawei.livingwallpaper.yiran.zhizunliuguangbaoshilan;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Window;

public class PermissionActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(PermissionMgr.getInstance().deniedExternalStoragePermission(this)) {
            PermissionMgr.getInstance().requestExternalStoragePermission(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!PermissionMgr.getInstance().deniedExternalStoragePermission(this)) {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionMgr.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
