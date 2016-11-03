package com.android.sjq.permission60;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {


    private onRequestPermissonResultListener mListener;
    private int requestPermissionCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //请求权限结果回调
    public interface onRequestPermissonResultListener {
        void onAllowPermission();

        void onRefusePermission();
    }

    /**
     * Android M请求权限封装
     * @param onRequestPermissonResultListener 请求结果回调
     * @param permission                       权限描述
     * @param requestCode                      请求权限请求码
     * @param permissions                      所请求的权限（数组）
     */
    public void requestPermissonWithCode(onRequestPermissonResultListener onRequestPermissonResultListener,
                                         String permission,
                                         int requestCode,
                                         String... permissions) {
        if (permissions == null || permissions.length == 0) {
            Toast.makeText(this, "权限请求异常", Toast.LENGTH_SHORT).show();
            return;
        }
        this.mListener = onRequestPermissonResultListener;
        this.requestPermissionCode = requestCode;
        boolean isGranted = checkPermissionIsGranted(permissions);
        if (isGranted) {
            //如果已经被授权
            onRequestPermissonResultListener.onAllowPermission();
        } else {
            //请求权限
            requestPermissions(permissions, requestCode);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("TAG", "------------------------->onRequestPermissionsResult");
        if (requestCode == requestPermissionCode) {
            //避免出现一个授权请求组多个授权，只要有一个没授权就算所有的拒绝授权
            StringBuffer str = new StringBuffer();
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    str.append("允许\n");
                } else {
                    str.append("拒绝\n");
                }
            }
            Log.i("TAG", str.toString());
            boolean isAllAllow = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isAllAllow = false;
                    break;
                }
            }
            if (isAllAllow) {
                mListener.onAllowPermission();
            } else {
                mListener.onRefusePermission();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //检查权限是否授予
    private boolean checkPermissionIsGranted(String... permissions) {
        boolean result = true;
        //判断版本号
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int targetSdkVersion = getTargetSdkVersion();
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                for (String permission : permissions) {
                    result = checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
                    //避免出现一次请求多个权限，第一个权限没有被授权第二个权限被受过权导致的result = true;
                    //当申请的权限组中有一个未授权时就全部重新请求
                    if (!result) {
                        break;
                    }
                }
            } else {
                for (String permission : permissions) {
                    result = PermissionChecker.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
                    if (!result) {
                        break;
                    }
                }

            }
        }
        return result;
    }

    //获取targetSdk
    private int getTargetSdkVersion() {
        int targetVersion = -1;
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            targetVersion = packageInfo.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return targetVersion;
    }


}
