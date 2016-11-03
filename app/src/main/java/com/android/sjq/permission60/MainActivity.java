package com.android.sjq.permission60;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void requestCamera(View view) {
        requestPermissonWithCode(new onRequestPermissonResultListener() {
            @Override
            public void onAllowPermission() {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }

            @Override
            public void onRefusePermission() {
                showSettingDialog();

                Toast.makeText(MainActivity.this, "请求相机权限失败", Toast.LENGTH_SHORT).show();
            }
        }, "请求相机权限", 100, new String[]{Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE});
    }

    //设置dialog
    private void showSettingDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("提示信息")
                .setMessage("当前应用缺少必要权限，或将导致部分功能无法正常使用，如若需要，请单击确定前往【设置】")
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //前往设置
                        startSetting();
                    }
                })
                .show();
    }

    //设置
    private void startSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + this.getPackageName()));
        startActivity(intent);
    }
}
