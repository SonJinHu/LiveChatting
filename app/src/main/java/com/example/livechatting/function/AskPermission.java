package com.example.livechatting.function;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

@SuppressLint("Registered")
public abstract class AskPermission extends AppCompatActivity {

    public final String[] PERMISSIONS_CAMERA = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public final String[] PERMISSIONS_ALBUM = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public final int PER_RESULT_CAMERA = 100;
    public final int PER_RESULT_ALBUM = 200;
    public final int ACT_RESULT_SETTINGS_CAMERA = 300;
    public final int ACT_RESULT_SETTINGS_ALBUM = 400;

    public abstract void allowedPermissionAlbum();
    public abstract void allowedPermissionCamera();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 모든 퍼미션이 허용됐는지 체크
        boolean checkResult = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                checkResult = false;
                break;
            }
        }

        if (requestCode == PER_RESULT_CAMERA) {
            if (checkResult) {
                allowedPermissionCamera();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[1])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[2])) {
                    showDialogCameraPermission();
                } else {
                    showDialogCameraPermissionBySelectNoShow();
                }
            }
        }

        if (requestCode == PER_RESULT_ALBUM) {
            if (checkResult) {
                allowedPermissionAlbum();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[1])) {
                    showDialogAlbumPermission();
                } else {
                    showDialogAlbumPermissionBySelectNoShow();
                }
            }
        }
    }

    private void showDialogCameraPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("필수 사용 권한")
                .setMessage(getEmojiByUnicode(0x1F4F7) + " 카메라\n"
                        + "사진 촬영을 위해 '카메라' 사용 권한이 필요합니다.\n\n"
                        + getEmojiByUnicode(0x1F4BE) + " 저장공간\n"
                        + "사진을 저장하고 불러오기 위해 '저장공간' 사용 권한이 필요합니다.")
                .setPositiveButton("재시도", (dialog, which) -> requestPermissions(PERMISSIONS_CAMERA, PER_RESULT_CAMERA))
                .setNegativeButton("닫기", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .create()
                .show();
    }

    private void showDialogCameraPermissionBySelectNoShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("필수 사용 권한")
                .setMessage(getEmojiByUnicode(0x1F4F7) + " 카메라\n"
                        + "사진 촬영을 위해 '카메라' 사용 권한이 필요합니다.\n\n"
                        + getEmojiByUnicode(0x1F4BE) + " 저장공간\n"
                        + "사진을 저장하고 불러오기 위해 '저장공간' 사용 권한이 필요합니다.\n\n"
                        + "[설정] → [권한] 에서 권한을 허용해주세요.")
                .setPositiveButton("설정", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivityForResult(intent, ACT_RESULT_SETTINGS_CAMERA);
                })
                .setNegativeButton("닫기", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .create()
                .show();
    }

    private void showDialogAlbumPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("필수 사용 권한")
                .setMessage(getEmojiByUnicode(0x1F4BE) + " 저장공간\n"
                        + "사진을 저장하고 불러오기 위해 '저장공간' 사용 권한이 필요합니다.")
                .setPositiveButton("재시도", (dialog, which) -> requestPermissions(PERMISSIONS_ALBUM, PER_RESULT_ALBUM))
                .setNegativeButton("닫기", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .create()
                .show();
    }

    private void showDialogAlbumPermissionBySelectNoShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("필수 사용 권한")
                .setMessage(getEmojiByUnicode(0x1F4BE) + " 저장공간\n"
                        + "사진을 저장하고 불러오기 위해 '저장공간' 사용 권한이 필요합니다.\n\n"
                        + "[설정] → [권한] 에서 권한을 허용해주세요.")
                .setPositiveButton("설정", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivityForResult(intent, ACT_RESULT_SETTINGS_ALBUM);
                })
                .setNegativeButton("닫기", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .create()
                .show();
    }

    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}
