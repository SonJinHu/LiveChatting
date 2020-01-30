package com.example.livechatting.function;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.example.livechatting.BaA_FaceDetection;
import com.example.livechatting.data.Constant;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@SuppressLint("Registered")
public abstract class PickProfileImage extends AskPermission {

    private final String TAG = getClass().getName();
    private final int ACT_RESULT_ALBUM = 101;
    private final int ACT_RESULT_CAMERA = 102;
    private final int ACT_RESULT_CROP = 103;

    private Uri tmpFileUri;

    public abstract void onAfterProfile(File file);

    @Override
    public void allowedPermissionAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, ACT_RESULT_ALBUM);
    }

    @Override
    public void allowedPermissionCamera() {
        // 카메라가 찍은 사진을 임시로 담아놓는 파일 생성
        String name = "tmp_" + System.currentTimeMillis() + ".png";
        File file = new File(Constant.DIRECTORY_PATH, name);
        try {
            if (file.createNewFile())
                Log.e(TAG, "카메라가 찍은 사진을 임시로 담아놓는 파일 생성: " + file.getAbsolutePath());
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
            sendBroadcast(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT)
            tmpFileUri = FileProvider.getUriForFile(this,
                    "com.example.livechatting.fileprovider", file);
        else
            tmpFileUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tmpFileUri);
        startActivityForResult(intent, ACT_RESULT_CAMERA);
    }

    @Override
    public void allowedPermissionCameraDetection() {
        Intent intent = new Intent(this, BaA_FaceDetection.class);
        startActivityForResult(intent,11);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case ACT_RESULT_ALBUM:
                if (data != null)
                    tmpFileUri = data.getData();
            case ACT_RESULT_CAMERA:
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(tmpFileUri, "image/*");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, ACT_RESULT_CROP);
                break;
            case ACT_RESULT_CROP:
                File file = null;
                try {
                    // 크롭된 이미지를 담는 파일 생성
                    String name = System.currentTimeMillis() + ".png";
                    file = new File(Constant.DIRECTORY_PATH, name);
                    if (file.createNewFile())
                        Log.e(TAG, "크롭된 이미지를 담는 파일 생성: " + file.getAbsolutePath());

                    if (data != null && data.getExtras() != null) {
                        Bitmap bitmap = data.getExtras().getParcelable("data");
                        OutputStream outputStream = new FileOutputStream(file);
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                        if (bitmap != null)
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bufferedOutputStream);
                        bufferedOutputStream.flush();
                        bufferedOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
                sendBroadcast(intent);
                onAfterProfile(file);
                break;
        }
    }
}
