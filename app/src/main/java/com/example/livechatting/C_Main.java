package com.example.livechatting;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.livechatting.data.Constants;
import com.example.livechatting.data.UserInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class C_Main extends AppCompatActivity {

    private final String TAG = getClass().getName();
    private boolean exit = false;
    private ServerThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_main);

        Toolbar toolbar = findViewById(R.id.c_toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView nav = findViewById(R.id.c_bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.c_fragment);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            switch (id) {
                case R.id.navigation_friends:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("친구");
                    break;
                case R.id.navigation_chat:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("채팅");
                    break;
                case R.id.navigation_broadcast:
                    Objects.requireNonNull(getSupportActionBar()).setTitle("방송보기");
                    break;
            }
        });
        NavigationUI.setupWithNavController(nav, navController);

        Log.e(TAG, "onCreate() :: thread = new ServerThread()");
        thread = new ServerThread();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 객체 생성되었지만 start() 호출되지 않은 상태
        if (thread.getState() == Thread.State.NEW) {
            Log.e(TAG, "onResume() :: thread start");
            thread.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (thread.isAlive()) {
            Log.e(TAG, "onStop() :: thread turnOff");
            thread.turnOff();
        }
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "'뒤로' 버튼 한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(() -> exit = false, 3000);
        }
    }

    class ServerThread extends Thread {

        private final String TAG = getClass().getName();
        private Socket sock;

        void turnOff() {
            try {
                if (sock.isConnected())
                    sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                sock = new Socket(Constants.HOST, Constants.PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
                out.println(UserInfo.num);

                String line;
                while ((line = in.readLine()) != null) {
                    Log.e(TAG, "Message :: " + line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}