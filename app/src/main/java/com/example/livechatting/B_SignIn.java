package com.example.livechatting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.livechatting.api.ApiService;
import com.example.livechatting.api.Charger;
import com.example.livechatting.api.ChargerList;
import com.example.livechatting.api.RetroClient;
import com.example.livechatting.data.Shared;
import com.example.livechatting.data.UserInfo;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class B_SignIn extends AppCompatActivity implements View.OnClickListener {

    boolean exit = false;

    EditText et_id;
    EditText et_pw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_sign_in);

        et_id = findViewById(R.id.b_et_id);
        et_pw = findViewById(R.id.b_et_pw);
        findViewById(R.id.b_bt_signIn).setOnClickListener(this);
        findViewById(R.id.b_tv_signUp).setOnClickListener(this);

        CheckBox cb_hide = findViewById(R.id.b_cb_hide);
        cb_hide.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {// 비밀번호 숨기기
                et_pw.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {// 비밀번호 보이기
                et_pw.setTransformationMethod(null);
            }
        });

        // 자동로그인 초기화
        CheckBox cb_auto = findViewById(R.id.b_cb_auto);
        cb_auto.setChecked(false);
        Shared.setIsAuto(getApplicationContext(), false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_bt_signIn:
                signIn();
                break;
            case R.id.b_tv_signUp:
                Intent intent = new Intent(getApplicationContext(), Ba_SignUp.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "'뒤로'버튼 한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(() -> exit = false, 3000);
        }
    }

    private void signIn() {
        String id = et_id.getText().toString();
        String pw = et_pw.getText().toString();

        if (id.equals("")) {
            Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pw.equals("")) {
            Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetroClient.getApiService();
        Call<ChargerList> call = api.signIn(id, pw);
        call.enqueue(new Callback<ChargerList>() {
            @Override
            public void onResponse(@NonNull Call<ChargerList> call, @NonNull Response<ChargerList> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ArrayList<Charger.SignIn> list = response.body().getSignIn();
                        String status = list.get(0).getStatus();

                        switch (status) {
                            case "0": // 로그인 가능
                                CheckBox cb_auto = findViewById(R.id.b_cb_auto);
                                if(cb_auto.isChecked()) {
                                    Shared.setIsAuto(getApplicationContext(),true);
                                } else {
                                    Shared.setIsAuto(getApplicationContext(),false);
                                }

                                UserInfo.num = list.get(0).getNum();
                                UserInfo.id = list.get(0).getId();
                                UserInfo.nick = list.get(0).getNick();
                                UserInfo.img = list.get(0).getImg();

                                Intent intent = new Intent(getApplicationContext(), C_Main.class);
                                startActivity(intent);
                                finish();
                                break;
                            case "1": // 비밀번호가 맞지 않음
                                Toast.makeText(getApplicationContext(), "비밀번호를 잘못 입력하셨습니다", Toast.LENGTH_SHORT).show();
                                break;
                            case "2": // 아이디 존재 하지 않음
                                Toast.makeText(getApplicationContext(), "등록되지 않은 아이디입니다", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChargerList> call, @NonNull Throwable t) {
                Log.e("onFailure", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}