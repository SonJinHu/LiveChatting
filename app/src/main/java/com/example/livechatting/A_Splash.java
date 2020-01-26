package com.example.livechatting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.livechatting.data.Constant;
import com.example.livechatting.data.Shared;

public class A_Splash extends AppCompatActivity {

    final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_splash);

        // Splash 기능 : SPLASH_DISPLAY_LENGTH 이후, run() 실행
        new Handler().postDelayed(() -> {
            // 자동 로그인 검사
            boolean isAutoSignIn = Shared.getIsAuto(getApplicationContext());
            if (isAutoSignIn)
                goMain();
            else
                goSignIn();

            /*                SharedPreferences pref = getSharedPreferences("Auto", Context.MODE_PRIVATE);
            final String type = pref.getString("type", "");
            final String id = pref.getString("id", "");
            final String nick = pref.getString("nick", "");
            final String img = pref.getString("img", "");*/
            /*switch (type) {
                default:
                    goSignIn();
                    break;
                case "default":
                    if (!id.equals("")) {
                        goMain(type, id, nick, img);
                    } else {
                        goSignIn();
                    }
                    break;
            }*/
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void goSignIn() {
        Intent intent = new Intent(getApplicationContext(), B_SignIn.class);
        startActivity(intent);
        finish();
    }

    private void goMain() {
        Intent intent = new Intent(getApplicationContext(), C_Main.class);
        startActivity(intent);
        finish();
    }

/*    private void goMain(String type, String id, String nick, String img) {
        Intent intent = new Intent(getApplicationContext(), C_Main.class);
        intent.putExtra("type", type);
        intent.putExtra("id", id);
        intent.putExtra("nick", nick);
        intent.putExtra("img", img);
        startActivity(intent);
        finish();
    }*/
}
