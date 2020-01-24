package com.example.livechatting;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.livechatting.api.AsyncTask;
import com.example.livechatting.data.Constant;
import com.example.livechatting.function.PickProfileImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Ba_SignUp extends PickProfileImage implements View.OnClickListener {

    private final String TAG = getClass().getName();

    private EditText et_id;
    private EditText et_nick;
    private EditText et_pw;
    private EditText et_pwCheck;

    private boolean isProfileImage = false; //? 프로필 이미지 선택 목록에 '기본이미지로 변경' 추가 : 추가하지 않음
    private File file;
    private boolean isCheckId = false;
    private boolean isCheckNick = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ba_sign_up);
        et_id = findViewById(R.id.ba_et_id);
        et_nick = findViewById(R.id.ba_et_nickname);
        et_pw = findViewById(R.id.ba_et_pw);
        et_pwCheck = findViewById(R.id.ba_et_pwCheck);

        View bt_checkId = findViewById(R.id.ba_bt_checkId);
        View bt_checkNick = findViewById(R.id.ba_bt_checkNick);
        View bt_signUp = findViewById(R.id.ba_bt_signUp);
        View tv_signIn = findViewById(R.id.ba_tv_signIn);

        bt_checkId.setOnClickListener(this);
        bt_checkNick.setOnClickListener(this);
        bt_signUp.setOnClickListener(this);
        tv_signIn.setOnClickListener(this);

        ImageView iv_profile = findViewById(R.id.ba_iv_photo);
        iv_profile.setOnClickListener(v -> showDialog());

        // 프로필 이미지 기본으로 초기화
        Glide.with(getApplicationContext())
                .load(R.drawable.profile_default)
                .apply(RequestOptions.circleCropTransform())
                .into(iv_profile);
        isProfileImage = false;
    }

    @Override
    public void onAfterProfile(File file, Bitmap bitmap) {
        ImageView iv_profile = findViewById(R.id.ba_iv_photo);
        Glide.with(getApplicationContext())
                .load(bitmap)
                .apply(RequestOptions.circleCropTransform())
                .into(iv_profile);
        isProfileImage = true;
        this.file = file;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ba_bt_checkId:
                checkId();
                break;
            case R.id.ba_bt_checkNick:
                checkNick();
                break;
            case R.id.ba_bt_signUp:
                signUp();
                break;
            case R.id.ba_tv_signIn:
                finish();
                break;
        }
    }

    private void showDialog() {
        List<String> ListItems = new ArrayList<>();
        ListItems.add("카메라");
        ListItems.add("얼굴검출 카메라");
        ListItems.add("갤러리");
        if (isProfileImage)
            ListItems.add("기본 이미지로 변경");
        CharSequence[] items = ListItems.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, (dialog, which) -> {
            switch (which) {
                case 0: // 카메라
                    ActivityCompat.requestPermissions(this, PERMISSIONS_CAMERA, PER_RESULT_CAMERA);
                    break;
                case 1: // 얼굴검출 카메라
                    Toast.makeText(getApplicationContext(), "준비 중입니다.", Toast.LENGTH_SHORT).show();
                    break;
                case 2: // 갤러리
                    ActivityCompat.requestPermissions(this, PERMISSIONS_ALBUM, PER_RESULT_ALBUM);
                    break;
                case 3: // 기본이미지
                    ImageView iv_profile = findViewById(R.id.ba_iv_photo);
                    Glide.with(getApplicationContext())
                            .load(R.drawable.profile_default)
                            .apply(new RequestOptions().circleCrop())
                            .into(iv_profile);
                    isProfileImage = false;
                    break;
            }
        });
        builder.show();
    }

    private void checkId() {
        String url = Constant.URL + Constant.CHECK_ID;
        String param = "id=" + et_id.getText().toString();

        AsyncTask.RequestServer async = new AsyncTask.RequestServer();
        async.setOnPostListener(result -> {
            if (result.equals("0")) {
                Toast.makeText(getApplicationContext(), "아이디 사용 가능", Toast.LENGTH_SHORT).show();
                isCheckId = true;
            } else {
                Toast.makeText(getApplicationContext(), "아이디 사용 불가", Toast.LENGTH_SHORT).show();
                isCheckId = false;
            }
        });
        async.execute(url, param);
    }

    private void checkNick() {
        String url = Constant.URL + Constant.CHECK_NICK;
        String param = "nick=" + et_nick.getText().toString();

        AsyncTask.RequestServer async = new AsyncTask.RequestServer();
        async.setOnPostListener(result -> {
            if (result.equals("0")) {
                Toast.makeText(getApplicationContext(), "닉네임 사용 가능", Toast.LENGTH_SHORT).show();
                isCheckNick = true;
            } else {
                Toast.makeText(getApplicationContext(), "닉네임 사용 불가", Toast.LENGTH_SHORT).show();
                isCheckNick = false;
            }
        });
        async.execute(url, param);
    }

    private void signUp() {
        if (!isCheckId) {
            Toast.makeText(getApplicationContext(), "아이디를 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isCheckNick) {
            Toast.makeText(getApplicationContext(), "닉네임을 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!et_pw.getText().toString().equals(et_pwCheck.getText().toString())) {
            Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncTask.SignUp async = new AsyncTask.SignUp();
        async.setOnPostListener(result -> {
            Log.e(TAG, result);
            if (result.equals("0")) {
                Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "잠시 후에 시도해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        String id = et_id.getText().toString();
        String nick = et_nick.getText().toString();
        String pw = et_pwCheck.getText().toString();

        if (isProfileImage && file != null) {
            async.execute(id, nick, pw, file.getAbsolutePath());
            return;
        }
        async.execute(id, nick, pw);
    }
}